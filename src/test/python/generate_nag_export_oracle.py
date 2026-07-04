"""Generate the python-chess reference oracle for NAG (numeric annotation glyph) export.

Reads every PGN under src/test/resources/pgnNagExport/, imports it with python-chess, and
records two things per fixture: python-chess's own movetext export (headers off, no line
wrapping) and the per-mainline-move NAG set. The Java side
(TestPgnArchivalExportAgainstPythonChessOracle) parses the same fixtures with ashlar-chess,
emits WriteMode.ARCHIVAL, and asserts its movetext matches python-chess's export and its
per-move NAG codes match python-chess's set.

Scope note: the fixtures deliberately carry NAGs in ascending, duplicate-free order and no
recursive annotation variations, so the two libraries' serialisations coincide. python-chess
holds a move's NAGs in a set (dedup + sorted on export); ashlar-chess holds an ordered list
(order and duplicates preserved). They agree exactly only when the input is already canonical
- which is what real exporters emit - so these fixtures stay in that canonical form.

The oracle file src/test/resources/oracle/python-chess/nagExport.jsonl is committed and consumed
by the Java test; regenerate only when the fixtures change. Determinism: filenames sorted, JSON
keys sorted, NAG lists sorted.

JSONL schema (one record per line):

  Record := {
    "pgn":      <string>,        # PGN filename (sort key)
    "movetext": <string>,        # python-chess StringExporter(headers=False, columns=None) output
    "nags":     [[int, ...], ...] # one sorted NAG list per mainline half-move
  }

Reproducibility: python-chess 1.11.2 is the pinned version of record. Install via
  pip install -r src/test/python/requirements.txt

Usage:
  python src/test/python/generate_nag_export_oracle.py
"""

import json
import sys
from pathlib import Path

import chess
import chess.pgn

REPO_ROOT = Path(__file__).resolve().parents[3]
FIXTURE_DIR = REPO_ROOT / "src" / "test" / "resources" / "pgnNagExport"
ORACLE_PATH = REPO_ROOT / "src" / "test" / "resources" / "oracle" / "python-chess" / "nagExport.jsonl"


def record_for_pgn(pgn_path: Path) -> dict:
    with pgn_path.open(encoding="utf-8") as fh:
        game = chess.pgn.read_game(fh)
    if game is None:
        raise RuntimeError(f"no game in {pgn_path.name}")
    if game.errors:
        raise RuntimeError(f"python-chess reported errors for {pgn_path.name}: {game.errors}")

    exporter = chess.pgn.StringExporter(headers=False, variations=True, comments=True, columns=None)
    movetext = game.accept(exporter).strip()

    nags = []
    node = game
    while node.variations:
        node = node.variations[0]
        nags.append(sorted(node.nags))

    return {"pgn": pgn_path.name, "movetext": movetext, "nags": nags}


def main() -> int:
    if not FIXTURE_DIR.is_dir():
        raise RuntimeError(f"fixture directory not found: {FIXTURE_DIR}")
    pgn_paths = sorted(FIXTURE_DIR.glob("*.pgn"))
    if not pgn_paths:
        raise RuntimeError(f"no PGN files in {FIXTURE_DIR}")

    ORACLE_PATH.parent.mkdir(parents=True, exist_ok=True)
    with ORACLE_PATH.open("w", encoding="utf-8", newline="\n") as out:
        for pgn_path in pgn_paths:
            record = record_for_pgn(pgn_path)
            out.write(json.dumps(record, sort_keys=True, separators=(",", ":")))
            out.write("\n")

    print(f"{len(pgn_paths)} fixtures -> {ORACLE_PATH.relative_to(REPO_ROOT)}")
    print(f"python-chess version: {chess.__version__}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
