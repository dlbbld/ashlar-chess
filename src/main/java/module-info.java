// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * ashlar-chess module descriptor. The module name {@code io.github.dlbbld.ashlarchess} matches the
 * {@code Automatic-Module-Name} published in 19.1.0, so this is not a renaming break.
 *
 * <p>
 * Exported packages are the deliberate public API: the {@code Board} game object and its vocabulary, the
 * FEN/PGN/SAN parsers and their result types, adjudication, reporting, the unwinnability face, the base
 * exception hierarchy, and {@code BitboardPosition} as documented advanced low-level API. Everything not
 * exported ({@code moves}, {@code analyze}, {@code squares}, {@code messages}, {@code common.*},
 * {@code board.model}, and the {@code *.internal} subpackages of {@code bitboard}/{@code pgn}/{@code san}/{@code fen})
 * is internal and hidden from modular consumers.
 */
module io.github.dlbbld.ashlarchess {

  requires org.apache.logging.log4j;
  requires org.apache.commons.lang3;
  requires static org.eclipse.jdt.annotation;

  exports io.github.dlbbld.ashlarchess.board;
  exports io.github.dlbbld.ashlarchess.board.enums;
  exports io.github.dlbbld.ashlarchess.fen;
  exports io.github.dlbbld.ashlarchess.fen.model;
  exports io.github.dlbbld.ashlarchess.pgn;
  exports io.github.dlbbld.ashlarchess.san;
  exports io.github.dlbbld.ashlarchess.adjudication;
  exports io.github.dlbbld.ashlarchess.report;
  exports io.github.dlbbld.ashlarchess.unwinnability;
  exports io.github.dlbbld.ashlarchess.exceptions;
  exports io.github.dlbbld.ashlarchess.bitboard;

}
