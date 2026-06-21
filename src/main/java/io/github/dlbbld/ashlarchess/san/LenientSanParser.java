// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.common.utility.ExceptionUtility;
import io.github.dlbbld.ashlarchess.messages.Message;
import io.github.dlbbld.ashlarchess.model.LegalMove;

/**
 * Public entry point for the lenient SAN pipeline. Accepts inputs that the strict pipeline rejects, when those inputs
 * uniquely identify a legal move and the deviation matches a supported tolerance category.
 *
 * <p>
 * See {@link io.github.dlbbld.ashlarchess.san the package-level Javadoc} for the strategy. The public entry point
 * {@link #parse(String, Board)} does a full parse, returning the resolved move plus the list of forgiven items.
 */
public final class LenientSanParser {

  private LenientSanParser() {
  }

  /**
   * Parses {@code text} as a SAN move on {@code board}, accepting a defined set of canonical-SAN deviations. On
   * success, returns the resolved {@link MoveSpecification} together with one {@link ForgivenSanItem} per deviation
   * that was forgiven. On a canonical input, the forgiven-items list is empty.
   *
   * @throws LenientSanParserValidationException if the input cannot be resolved to a legal move even after applying
   *                                             every supported tolerance
   */
  public static LenientSanParseResult parse(String text, Board board) {
    // Phase 0: try strict on the raw input first. Canonical SAN pays zero lenient overhead.
    try {
      final MoveSpecification ms = StrictSanParser.parse(text, board);
      return new LenientSanParseResult(ms, ForgivenSanItem.NO_ITEMS);
    } catch (@SuppressWarnings("unused") final SanValidationException ignored) {
      // Fall through to the lenient pipeline.
    }

    final List<LenientSanValidationProblem> codes = new ArrayList<>();

    // Phase 1: shape normalization. Throws LenientSanParserValidationException for hard-rejected shapes
    // (e.g. mixed 0-O castling).
    final String normalized = LenientSanShapeNormalize.normalize(text, board, codes);

    // Phase 2: try strict + recovery loop.
    final MoveSpecification moveSpecification;
    try {
      moveSpecification = LenientSanRecover.parseWithRecovery(normalized, board, codes);
    } catch (final SanValidationException finalReject) {
      final String reason = ExceptionUtility.getMessage(finalReject);
      throw new LenientSanParserValidationException(
          Message.getString("validation.san.lenient.parseFailed", text, reason), text,
          finalReject.getSanValidationProblem(), itemsWithoutCanonical(text, codes));
    }

    // Phase 3: compute the canonical-SAN equivalent and finalize the forgiven items.
    final String canonicalSan = computeCanonicalSan(moveSpecification, board);
    final List<ForgivenSanItem> items = new ArrayList<>(codes.size());
    for (final LenientSanValidationProblem code : codes) {
      items.add(new ForgivenSanItem(code, text, canonicalSan));
    }
    return new LenientSanParseResult(moveSpecification, Nulls.copyOfList(items));
  }

  // --- Helpers ---

  private static String computeCanonicalSan(MoveSpecification moveSpecification, Board board) {
    final ImmutableList<LegalMove> legalMovesBefore = board.getLegalMoves();
    @Nullable LegalMove matching = null;
    for (final LegalMove candidate : legalMovesBefore) {
      if (candidate.moveSpecification().equals(moveSpecification)) {
        matching = candidate;
        break;
      }
    }
    if (matching == null) {
      throw new ProgrammingMistakeException(
          "Resolved MoveSpecification not found in legal-move set; lenient parser invariant violated");
    }

    board.move(moveSpecification);
    final SanTerminalMarker marker = SanTerminalMarkerUtility.calculate(board.isCheck(), board.isCheckmate());
    board.unmove();

    return MoveToSan.toSan(matching, legalMovesBefore, marker);
  }

  private static ImmutableList<ForgivenSanItem> itemsWithoutCanonical(String text,
      List<LenientSanValidationProblem> codes) {
    if (codes.isEmpty()) {
      return ForgivenSanItem.NO_ITEMS;
    }
    // Failure path: the canonical SAN is unknown (the parse never resolved a move), so we surface the codes
    // accumulated so far paired with the original token. Callers diagnosing a failed lenient parse care about
    // which deviations applied before the failure, not the (unknown) canonical equivalent.
    final List<ForgivenSanItem> items = new ArrayList<>(codes.size());
    for (final LenientSanValidationProblem code : codes) {
      items.add(new ForgivenSanItem(code, text, text));
    }
    return Nulls.copyOfList(items);
  }
}
