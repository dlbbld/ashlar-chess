// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * ashlar-chess module descriptor. The module name {@code io.github.dlbbld.ashlarchess} matches the
 * {@code Automatic-Module-Name} published in 19.1.0, so this is not a renaming break.
 *
 * <p>
 * Exported packages are the deliberate public API: the {@code Board} game object and its vocabulary, the FEN/PGN/SAN
 * parsers and their result types, adjudication, reporting, the unwinnability face, the base exception hierarchy, and
 * {@code BitboardPosition} as documented advanced low-level API. Everything not exported ({@code moves},
 * {@code analyze}, {@code squares}, {@code messages}, {@code internal}, {@code board.model}, and the {@code *.internal}
 * subpackages of {@code bitboard}/{@code board}/{@code board.enums}/{@code pgn}/{@code san}/{@code fen}) is internal
 * and hidden from modular consumers.
 *
 * <p>
 * <strong>One qualified exception.</strong> {@code bitboard.internal} - the fast move-table engine - is lent, via a
 * <em>qualified</em> export, to the single module {@code io.github.dlbbld.fun22reference} (a clean-room reference
 * implementation of Ambrona's FUN 2022 unwinnability algorithm, used as a research oracle). This is deliberately
 * <em>not</em> public API: no other consumer can see it, and no source/binary compatibility is promised across
 * releases. It exists only so that one owned, privileged research module can drive the fast legal-move generator.
 */
module io.github.dlbbld.ashlarchess {

  requires java.logging;
  // transitive so a JDT-aware consumer can resolve the @NonNull/@Nullable annotations that appear in our exported
  // API signatures (e.g. ForgivenSanItem.NO_ITEMS); static so the dependency stays optional - the annotations are
  // CLASS-retention and never needed at runtime, so this does not force jdt.annotation onto consumers.
  requires static transitive org.eclipse.jdt.annotation;

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

  // Qualified (friend) export: the fast move-table engine is NOT public API. It is lent only to the
  // fun22-reference research oracle. No compatibility promise to any other consumer.
  exports io.github.dlbbld.ashlarchess.bitboard.internal to io.github.dlbbld.fun22reference;

}
