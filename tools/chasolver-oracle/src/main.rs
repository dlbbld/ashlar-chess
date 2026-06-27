// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

//! Local oracle runner for Miguel Ambrona's `chasolver` (the Rust successor to
//! D3-Chess/CHA). Reads one FEN per line from stdin and prints, per FEN, the full
//! and quick unwinnability verdicts for both sides as a tab-separated row:
//!
//! ```text
//! fen<TAB>fullWhite<TAB>fullBlack<TAB>quickWhite<TAB>quickBlack
//! ```
//!
//! This mirrors `tools/ambrona-oracle/cha_oracle.cpp` so the Rust verdicts land in the
//! exact same oracle format as the C++ (cha) verdicts and can be diffed 1:1.
//!
//! * full  : `winnability(board, winner)` -> `Some(Winnable)` | `Some(Unwinnable)` | `None`
//!           mapped to `WINNABLE` | `UNWINNABLE` | `UNDETERMINED`
//! * quick : `is_unwinnable_fast(board, winner)` -> `bool`
//!           mapped to `UNWINNABLE` | `POSSIBLY_WINNABLE`
//!
//! Unlike cha there is no configurable node limit; `winnability` uses chasolver's own
//! internal bound and returns `None` (mapped to `UNDETERMINED`) when it is reached.

use std::io::{self, BufRead, BufWriter, Write};
use std::str::FromStr;

use chasolver::{is_unwinnable_fast, winnability, Winnability};
use chess::{Board, Color};

fn full_verdict(board: &Board, winner: Color) -> &'static str {
    match winnability(board, winner) {
        Some(Winnability::Winnable { .. }) => "WINNABLE",
        Some(Winnability::Unwinnable) => "UNWINNABLE",
        None => "UNDETERMINED",
    }
}

fn quick_verdict(board: &Board, winner: Color) -> &'static str {
    if is_unwinnable_fast(board, winner) {
        "UNWINNABLE"
    } else {
        "POSSIBLY_WINNABLE"
    }
}

fn main() {
    let stdin = io::stdin();
    let stdout = io::stdout();
    let mut out = BufWriter::new(stdout.lock());

    for line in stdin.lock().lines() {
        let line = line.expect("failed to read stdin");
        let fen = line.trim();
        if fen.is_empty() {
            continue;
        }

        match Board::from_str(fen) {
            Ok(board) => {
                let full_white = full_verdict(&board, Color::White);
                let full_black = full_verdict(&board, Color::Black);
                let quick_white = quick_verdict(&board, Color::White);
                let quick_black = quick_verdict(&board, Color::Black);
                writeln!(out, "{fen}\t{full_white}\t{full_black}\t{quick_white}\t{quick_black}")
                    .expect("write failed");
            }
            Err(err) => {
                // Keep the stream alive and 5-column shaped, but make the failure impossible
                // to miss: a sentinel token on stdout plus a diagnostic on stderr.
                eprintln!("FEN parse error for '{fen}': {err}");
                writeln!(out, "{fen}\tPARSE_ERROR\tPARSE_ERROR\tPARSE_ERROR\tPARSE_ERROR")
                    .expect("write failed");
            }
        }
        out.flush().expect("flush failed");
    }
}
