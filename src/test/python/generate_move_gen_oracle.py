"""Generate the python-chess move-generation oracle.

Scans every PGN under each covered move-rule-mechanics bucket, replays the game,
and records the sorted set of legal UCIs at each visited position (before each
move, plus the position after the final move). Writes one JSON object per PGN
to src/test/resources/oracle/python-chess/move-gen/<folderPart>.jsonl.

The JSONL files are committed to the repo and consumed by
TestLegalMovesAgainstPythonChessOracle on the Java side. Regenerate these
files only when fixtures change.

JSONL schema (one record per line, keys sorted alphabetically):

  Record := {
    "pgn":    <string>,          # PGN filename
    "perPly": [Ply, ...]         # length = halfMoveCount + 1
                                 # perPly[0] = legal moves at startFen
                                 # perPly[N] = legal moves after Nth played move
                                 # perPly[halfMoveCount] is empty list for mate/stalemate
  }

  Ply := {
    "legalMovesUci": [<string>, ...]  # sorted ascending
  }

This is a separate oracle from the PGN-import oracle by design: it answers
"does our legal-move generator match python-chess at every ply?", which is a
distinct question from "did we import this PGN the same way?". Apply to
move-rule-mechanics buckets only — long-game corpora would balloon file size
without proportional value (move generation is already heavily internally tested).

FEN convention: not relevant here — we record UCI legal-move sets, not FEN strings.

Reproducibility: python-chess 1.11.2 is the pinned version of record. Install via
  pip install -r src/test/python/requirements.txt

Usage:
  python src/test/python/generate_move_gen_oracle.py
"""

import json
import sys
from pathlib import Path

import chess
import chess.pgn

REPO_ROOT = Path(__file__).resolve().parents[3]
PGN_ROOT = REPO_ROOT / "src" / "test" / "resources" / "pgn"
ORACLE_ROOT = REPO_ROOT / "src" / "test" / "resources" / "oracle" / "python-chess" / "move-gen"

# Move-rule-mechanics buckets only: piece moves, captures, en passant, promotion,
# check / double check, castling, plus parserFenMechanics (FEN-load positions
# are unusual move-gen inputs). Excludes stalemate/checkmate-only buckets since
# those test terminal flags, not the move generator at non-terminal positions.
BUCKETS = [
    "parserFenMechanics",
    "basic/movingPiece/white",
    "basic/movingPiece/black",
    "basic/capture/systematic/white",
    "basic/capture/systematic/black",
    "basic/capture/lastMove",
    "basic/enPassantCapture/white",
    "basic/enPassantCapture/black",
    "basic/promotionPiece/white",
    "basic/promotionPiece/black",
    "basic/promotionSquare/white",
    "basic/promotionSquare/black",
    "basic/check/white",
    "basic/check/black",
    "basic/doubleCheck/noCheckmate/white",
    "basic/doubleCheck/noCheckmate/black",
    "basic/doubleCheck/checkmate/white",
    "basic/doubleCheck/checkmate/black",
    "basic/castling/simplest/white",
    "basic/castling/simplest/black",
    "basic/castling/special/white",
    "basic/castling/special/black",
]


def legal_uci_sorted(board: chess.Board) -> list[str]:
    return sorted(move.uci() for move in board.legal_moves)


def record_for_pgn(pgn_path: Path) -> dict:
    with pgn_path.open(encoding="utf-8") as fh:
        game = chess.pgn.read_game(fh)
    if game is None:
        raise RuntimeError(f"no game in {pgn_path.name}")

    board = game.board()
    per_ply = [{"legalMovesUci": legal_uci_sorted(board)}]
    for move in game.mainline_moves():
        board.push(move)
        per_ply.append({"legalMovesUci": legal_uci_sorted(board)})

    return {"pgn": pgn_path.name, "perPly": per_ply}


def generate_for_bucket(bucket: str) -> tuple[int, Path]:
    pgn_dir = PGN_ROOT / bucket
    output_path = ORACLE_ROOT / f"{bucket}.jsonl"

    if not pgn_dir.is_dir():
        raise RuntimeError(f"PGN directory not found: {pgn_dir}")

    output_path.parent.mkdir(parents=True, exist_ok=True)

    pgn_paths = sorted(pgn_dir.glob("*.pgn"))
    if not pgn_paths:
        raise RuntimeError(f"no PGN files in {pgn_dir}")

    with output_path.open("w", encoding="utf-8", newline="\n") as out:
        for pgn_path in pgn_paths:
            record = record_for_pgn(pgn_path)
            out.write(json.dumps(record, sort_keys=True, separators=(",", ":")))
            out.write("\n")

    return len(pgn_paths), output_path


def main() -> int:
    total_fixtures = 0
    for bucket in BUCKETS:
        count, out_path = generate_for_bucket(bucket)
        total_fixtures += count
        print(f"{bucket:50s} {count:4d} fixtures -> {out_path.relative_to(REPO_ROOT)}")

    print(f"\n{len(BUCKETS)} buckets, {total_fixtures} fixtures total")
    print(f"python-chess version: {chess.__version__}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
