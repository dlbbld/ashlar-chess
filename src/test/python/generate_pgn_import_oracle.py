"""Generate the python-chess reference oracle for the PGN-import test surface.

Scans every PGN under each covered bucket of src/test/resources/pgn/, replays it with
python-chess starting from the (optional) [FEN] header, and writes one JSON object per PGN
to src/test/resources/oracle/python-chess/<folderPart>.jsonl (mirroring the bucket folder
structure under src/test/resources/pgn/).

The JSONL files are committed to the repo and consumed by
TestPgnImportAgainstPythonChessOracle on the Java side. Regenerate these
files only when fixtures change. Determinism: PGN filenames are sorted; JSON keys
are sorted; line order is stable across runs.

JSONL schema (one record per line, keys sorted alphabetically):

  Record := {
    "pgn":       <string>,    # PGN filename (sort key for the line)
    "startFen":  <string>,    # FEN at the start of the game, re-emitted by python-chess
    "moves":     [Move, ...], # one entry per half-move in mainline order
    "finalFen":  <string>     # FEN after the last move
  }

  Move := {
    "san":                          <string>,  # python-chess's regenerated SAN
    "uci":                          <string>,  # UCI of the move
    "fenAfter":                     <string>,  # FEN after the move
    "halfmoveClock":                <int>,     # FIDE halfmove clock after the move
    "fullmoveNumber":               <int>,     # FEN-convention fullmove number after the move
    "isCheck":                      <bool>,    # board.is_check() after the move
    "isCheckmate":                  <bool>,    # board.is_checkmate() after the move
    "isStalemate":                  <bool>,    # board.is_stalemate() after the move
    "isInsufficientMaterial":       <bool>,    # board.is_insufficient_material() (both sides)
    "hasInsufficientMaterialWhite": <bool>,    # board.has_insufficient_material(chess.WHITE)
    "hasInsufficientMaterialBlack": <bool>,    # board.has_insufficient_material(chess.BLACK)
    "isRepetition2":                <bool>,    # board.is_repetition(2)
    "isRepetition3":                <bool>,    # board.is_repetition(3) — threefold by count
    "isRepetition4":                <bool>,    # board.is_repetition(4)
    "isFivefoldRepetition":         <bool>,    # board.is_fivefold_repetition() — FIDE 9.6.1 auto-draw
    "isFiftyMoves":                 <bool>,    # board.is_fifty_moves() — halfmove clock >= 100
    "isSeventyFiveMoves":           <bool>,    # board.is_seventyfive_moves() — FIDE 9.6.2 auto-draw
    "canClaimThreefold":            <bool>,    # board.can_claim_threefold_repetition() — claim by repetition
    "canClaimFifty":                <bool>     # board.can_claim_fifty_moves() — claim by 50-move rule
  }

This module is the schema source of truth; the Java side
(com.dlb.chess.test.oracle.python.OracleRecord / OracleMove) mirrors it.

FEN convention: emitted via `board.fen(en_passant="fen")` so the en-passant target
square is always written after a pawn double-step (PGN/Edwards 1994 §16.1.3.4),
matching clean-chess. python-chess's default `board.fen()` omits the e.p. target
when no capture is legal next move (Stockfish / Lichess / X-FEN de-facto), which
would disagree with clean-chess on every fixture where a pawn just double-stepped
without an adjacent opposing pawn.

Bucket coverage: PARSER_FROM_FEN plus all BASIC_* buckets plus the curated
real-games / Wikipedia / WCC buckets. Skipped: CHA_*, edgeCases, random, MAX_*,
MONSTER_*, REPETITION_QUIZ_*.

Reproducibility: python-chess 1.11.2 is the pinned version of record. Install via
  pip install -r src/test/python/requirements.txt

Usage:
  python src/test/python/generate_pgn_import_oracle.py
"""

import json
import sys
from pathlib import Path

import chess
import chess.pgn

REPO_ROOT = Path(__file__).resolve().parents[3]
PGN_ROOT = REPO_ROOT / "src" / "test" / "resources" / "pgn"
ORACLE_ROOT = REPO_ROOT / "src" / "test" / "resources" / "oracle" / "python-chess"

# Folder paths relative to src/test/resources/pgn/, matching the folderPart on each
# PgnTest enum value (sans the optional trailing slash). Order: PARSER_FROM_FEN, BASIC_*
# in PgnTest declaration order, then real-games / review buckets.
BUCKETS = [
    "parserFenMechanics",
    # basic — moving pieces / capture / en passant / promotion
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
    # basic — check / checkmate / double check / stalemate
    "basic/check/white",
    "basic/check/black",
    "basic/checkmate/rbnqkVariations/white",
    "basic/checkmate/rbnqkVariations/black",
    "basic/checkmate/various/white",
    "basic/checkmate/various/black",
    "basic/doubleCheck/noCheckmate/white",
    "basic/doubleCheck/noCheckmate/black",
    "basic/doubleCheck/checkmate/white",
    "basic/doubleCheck/checkmate/black",
    "basic/stalemate",
    # basic — insufficient material / repetition / fifty / seventy-five
    "basic/insufficientMaterial/both",
    "basic/insufficientMaterial/onlyWhite",
    "basic/insufficientMaterial/onlyBlack",
    "basic/insufficientMaterial/none",
    "basic/threefold",
    "basic/fifty",
    "basic/fivefold",
    "basic/seventyFive",
    "basic/intervening",
    "basic/doubleDraw",
    # basic — castling / forced / report
    "basic/castling/simplest/white",
    "basic/castling/simplest/black",
    "basic/castling/special/white",
    "basic/castling/special/black",
    "basic/forced",
    "basic/report/noProgress/white",
    "basic/report/noProgress/black",
    "basic/report/repetition",
    "basic/report/maxNoProgress",
    # real games / review
    "realGames/various",
    "review/wcc2021",
    "realGames/fivefold/correct",
    "realGames/fifty/general",
    "realGames/fifty/pattern",
    "realGames/seventyFive/correct",
    "realGames/earlyDraw",
    "review/wikipedia/threefold",
    "review/wikipedia/fiftyMove",
]


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
            "isInsufficientMaterial": board.is_insufficient_material(),
            "hasInsufficientMaterialWhite": board.has_insufficient_material(chess.WHITE),
            "hasInsufficientMaterialBlack": board.has_insufficient_material(chess.BLACK),
            "isRepetition2": board.is_repetition(2),
            "isRepetition3": board.is_repetition(3),
            "isRepetition4": board.is_repetition(4),
            "isFivefoldRepetition": board.is_fivefold_repetition(),
            "isFiftyMoves": board.is_fifty_moves(),
            "isSeventyFiveMoves": board.is_seventyfive_moves(),
            "canClaimThreefold": board.can_claim_threefold_repetition(),
            "canClaimFifty": board.can_claim_fifty_moves(),
        })

    return {
        "pgn": pgn_path.name,
        "startFen": start_fen,
        "moves": moves,
        "finalFen": board.fen(en_passant="fen"),
    }


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
        print(f"{bucket:55s} {count:5d} fixtures -> {out_path.relative_to(REPO_ROOT)}")

    print(f"\n{len(BUCKETS)} buckets, {total_fixtures} fixtures total")
    print(f"python-chess version: {chess.__version__}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
