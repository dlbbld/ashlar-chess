"""Generate the python-chess reference oracle for the PARSER_FROM_FEN bucket.

Scans every PGN under src/test/resources/pgn/parserFenMechanics/, replays it with
python-chess starting from the [FEN] header, and writes one JSON object per PGN to
src/test/resources/oracle/python-chess/parserFenMechanics.jsonl.

The JSONL file is committed to the repo and consumed by
TestParserFenMechanicsAgainstPythonChessOracle on the Java side. Regenerate this
file only when fixtures change. Determinism: PGN filenames are sorted; JSON keys
are sorted; line order is stable across runs.

FEN convention: emitted via `board.fen(en_passant="fen")` so the en-passant target
square is always written after a pawn double-step (PGN/Edwards 1994 §16.1.3.4),
matching clean-chess. python-chess's default `board.fen()` omits the e.p. target
when no capture is legal next move (Stockfish / Lichess / X-FEN de-facto), which
would disagree with clean-chess on every fixture where a pawn just double-stepped
without an adjacent opposing pawn.

Requires: python-chess (`pip install chess`).

Usage:
  python src/test/python/generate_parser_fen_mechanics_oracle.py
"""

import json
import sys
from pathlib import Path

import chess
import chess.pgn

REPO_ROOT = Path(__file__).resolve().parents[3]
PGN_DIR = REPO_ROOT / "src" / "test" / "resources" / "pgn" / "parserFenMechanics"
OUT_FILE = REPO_ROOT / "src" / "test" / "resources" / "oracle" / "python-chess" / "parserFenMechanics.jsonl"


def record_for_pgn(pgn_path: Path) -> dict:
    with pgn_path.open(encoding="utf-8") as fh:
        game = chess.pgn.read_game(fh)
    if game is None:
        raise RuntimeError(f"no game in {pgn_path.name}")

    board = game.board()
    start_fen = board.fen(en_passant="fen")

    moves = []
    for move in game.mainline_moves():
        san = board.san(move)
        uci = move.uci()
        board.push(move)
        moves.append({
            "san": san,
            "uci": uci,
            "fenAfter": board.fen(en_passant="fen"),
            "halfmoveClock": board.halfmove_clock,
            "fullmoveNumber": board.fullmove_number,
            "isCheck": board.is_check(),
            "isCheckmate": board.is_checkmate(),
            "isStalemate": board.is_stalemate(),
        })

    return {
        "pgn": pgn_path.name,
        "startFen": start_fen,
        "moves": moves,
        "finalFen": board.fen(en_passant="fen"),
    }


def main() -> int:
    if not PGN_DIR.is_dir():
        print(f"PGN directory not found: {PGN_DIR}", file=sys.stderr)
        return 1

    OUT_FILE.parent.mkdir(parents=True, exist_ok=True)

    pgn_paths = sorted(PGN_DIR.glob("*.pgn"))
    if not pgn_paths:
        print(f"no PGN files in {PGN_DIR}", file=sys.stderr)
        return 1

    print(f"generating oracle for {len(pgn_paths)} fixtures from {PGN_DIR}")
    with OUT_FILE.open("w", encoding="utf-8", newline="\n") as out:
        for pgn_path in pgn_paths:
            record = record_for_pgn(pgn_path)
            out.write(json.dumps(record, sort_keys=True, separators=(",", ":")))
            out.write("\n")

    print(f"wrote {OUT_FILE}")
    print(f"python-chess version: {chess.__version__}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
