package com.dlb.chess.san;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.messages.Message;
import com.dlb.chess.model.LegalMove;
import com.google.common.collect.ImmutableList;

/**
 * Public entry point for the lenient SAN pipeline. Accepts inputs that the strict pipeline rejects, when those inputs
 * uniquely identify a legal move and the deviation matches a supported tolerance category.
 *
 * <p>
 * See {@link com.dlb.chess.san the package-level Javadoc} for the strategy. The two public methods are:
 * <ul>
 * <li>{@link #parseText(String, Board)} - full parse, returns the resolved move plus the list of forgiven items.
 * <li>{@link #validateText(String, Board)} - discards the result, throws on rejection. Convenience for callers that
 * only need yes/no.
 * </ul>
 */
public final class LenientSanParser {

  private LenientSanParser() {
  }

  /**
   * Parses {@code text} as a SAN move on {@code board}, accepting a defined set of canonical-SAN deviations. On
   * success, returns the resolved {@link MoveSpecification} together with one {@link ForgivenItem} per deviation that
   * was forgiven. On a canonical input, the forgiven-items list is empty.
   *
   * @throws LenientSanParserValidationException if the input cannot be resolved to a legal move even after applying
   *                                             every supported tolerance
   */
  public static LenientSanParserValidationResult parseText(String text, Board board) {
    // Phase 0: try strict on the raw input first. Canonical SAN pays zero lenient overhead.
    try {
      final MoveSpecification ms = StrictSanParser.parseText(text, board).moveSpecification();
      return new LenientSanParserValidationResult(ms, ForgivenItem.EMPTY_LIST);
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
      final String reason = BasicUtility.getMessage(finalReject);
      throw new LenientSanParserValidationException(
          Message.getString("validation.san.lenient.parseFailed", text, reason), text,
          finalReject.getSanValidationProblem(), itemsWithoutCanonical(text, codes));
    }

    // Phase 3: compute the canonical-SAN equivalent and finalize the forgiven items.
    final String canonicalSan = computeCanonicalSan(moveSpecification, board);
    final List<ForgivenItem> items = new ArrayList<>(codes.size());
    for (final LenientSanValidationProblem code : codes) {
      items.add(new ForgivenItem(code, text, canonicalSan));
    }
    return new LenientSanParserValidationResult(moveSpecification, Nulls.copyOfList(items));
  }

  /**
   * Validates {@code text} as a lenient SAN move on {@code board}. Equivalent to {@link #parseText} with the result
   * discarded.
   *
   * @throws LenientSanParserValidationException if the input cannot be resolved to a legal move even after applying
   *                                             every supported tolerance
   */
  public static void validateText(String text, Board board) {
    parseText(text, board);
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
    final SanTerminalMarker marker = SanTerminalMarker.calculate(board.isCheck(), board.isCheckmate());
    board.unmove();

    return MoveToSan.calculateSanLastMove(matching, legalMovesBefore, marker);
  }

  private static ImmutableList<ForgivenItem> itemsWithoutCanonical(String text,
      List<LenientSanValidationProblem> codes) {
    if (codes.isEmpty()) {
      return ForgivenItem.EMPTY_LIST;
    }
    // Failure path: the canonical SAN is unknown (the parse never resolved a move), so we surface the codes
    // accumulated so far paired with the original token. Callers diagnosing a failed lenient parse care about
    // which deviations applied before the failure, not the (unknown) canonical equivalent.
    final List<ForgivenItem> items = new ArrayList<>(codes.size());
    for (final LenientSanValidationProblem code : codes) {
      items.add(new ForgivenItem(code, text, text));
    }
    return Nulls.copyOfList(items);
  }
}
