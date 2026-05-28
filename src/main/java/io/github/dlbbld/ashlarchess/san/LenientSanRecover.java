package io.github.dlbbld.ashlarchess.san;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.enums.NotationMovingPiece;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.LegalMove;

/**
 * Phase 2 of the lenient pipeline: try the strict pipeline on the Phase 1 candidate; if strict rejects with one of the
 * recoverable diagnostic codes (over-specification, capture-marker mismatch, terminal-marker mismatch), mutate the
 * candidate, accumulate a forgiven item, and retry. If strict rejects with a non-recoverable code, the original
 * {@link SanValidationException} is propagated to the caller, which wraps it in a
 * {@link io.github.dlbbld.ashlarchess.san.LenientSanParserValidationException}.
 *
 * <p>
 * The recovery loop is bounded by the number of distinct lenient codes (each can fire at most once per parse).
 */
final class LenientSanRecover {

  private static final int MAX_ITERATIONS = 16;

  private LenientSanRecover() {
  }

  static MoveSpecification parseWithRecovery(String candidate, Board board, List<LenientSanValidationProblem> codes) {
    final Set<LenientSanValidationProblem> emitted = EnumSet.noneOf(LenientSanValidationProblem.class);
    emitted.addAll(codes);

    String current = candidate;
    for (int i = 0; i < MAX_ITERATIONS; i++) {
      try {
        return StrictSanParser.parseText(current, board).moveSpecification();
      } catch (final SanValidationException e) {
        final SanValidationProblem strictCode = e.getSanValidationProblem();
        final LenientSanValidationProblem lenientCode = mapToLenientCode(strictCode);
        if (lenientCode == null || emitted.contains(lenientCode)) {
          // Same code would fire twice - defensive bail-out; shouldn't happen because each mutation
          // strictly reduces the set of applicable strict codes.
          throw e;
        }
        current = mutate(current, strictCode, board);
        emitted.add(lenientCode);
        codes.add(lenientCode);
      }
    }
    throw new ProgrammingMistakeException("Lenient SAN recovery exceeded iteration bound for candidate: " + candidate);
  }

  private static @Nullable LenientSanValidationProblem mapToLenientCode(SanValidationProblem strictCode) {
    return switch (strictCode) {
      // Terminal-marker mismatches
      case NO_SYMBOL_BUT_CHECK -> LenientSanValidationProblem.MISSING_CHECK_SUFFIX;
      case NO_SYMBOL_BUT_CHECKMATE -> LenientSanValidationProblem.MISSING_CHECKMATE_SUFFIX;
      case CHECK_SYMBOL_BUT_NO_CHECK -> LenientSanValidationProblem.SPURIOUS_CHECK_SUFFIX;
      case CHECKMATE_SYMBOL_BUT_NO_CHECK -> LenientSanValidationProblem.SPURIOUS_CHECKMATE_SUFFIX;
      case CHECK_SYMBOL_BUT_CHECKMATE -> LenientSanValidationProblem.WRONG_CHECK_SUFFIX_FOR_CHECKMATE;
      case CHECKMATE_SYMBOL_BUT_CHECK_ONLY -> LenientSanValidationProblem.WRONG_CHECKMATE_SUFFIX_FOR_CHECK;

      // Capture-marker mismatches (piece moves only - pawn variants are not auto-recoverable)
      case DESTINATION_RNBQK_EMPTY_CAPTURE_SYMBOL -> LenientSanValidationProblem.SPURIOUS_CAPTURE_MARKER;
      case DESTINATION_RNBQK_OPPONENT_NON_KING_NO_CAPTURE_SYMBOL -> LenientSanValidationProblem.MISSING_CAPTURE_MARKER;

      // Disambiguation overspec
      case OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE -> LenientSanValidationProblem.OVERSPECIFIED_FILE_DISAMBIGUATION;
      case OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE -> LenientSanValidationProblem.OVERSPECIFIED_RANK_DISAMBIGUATION;
      case NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE -> LenientSanValidationProblem.NON_STANDARD_RANK_DISAMBIGUATION;
      case OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE, OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY, OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY -> LenientSanValidationProblem.OVERSPECIFIED_SQUARE_DISAMBIGUATION;

      default -> null;
    };
  }

  private static String mutate(String current, SanValidationProblem strictCode, Board board) {
    return switch (strictCode) {
      // Terminal markers operate on the trailing character only
      case NO_SYMBOL_BUT_CHECK -> current + "+";
      case NO_SYMBOL_BUT_CHECKMATE -> current + "#";
      case CHECK_SYMBOL_BUT_NO_CHECK, CHECKMATE_SYMBOL_BUT_NO_CHECK -> stripTerminalMarker(current);
      case CHECK_SYMBOL_BUT_CHECKMATE -> stripTerminalMarker(current) + "#";
      case CHECKMATE_SYMBOL_BUT_CHECK_ONLY -> stripTerminalMarker(current) + "+";

      // Capture marker mutations (piece moves)
      case DESTINATION_RNBQK_EMPTY_CAPTURE_SYMBOL -> stripCaptureMarker(current);
      case DESTINATION_RNBQK_OPPONENT_NON_KING_NO_CAPTURE_SYMBOL -> insertCaptureMarker(current);

      // Disambiguation strips
      case OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE, OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE -> stripFirstDisambigChar(
          current);
      case OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE -> stripSquareDisambig(current);
      case OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY -> stripFirstDisambigChar(current);
      case OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY -> stripSecondDisambigChar(current);

      // Non-canonical disambig form: user typed rank where canonical is file. Resolve via board lookup.
      case NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE -> rewriteRankAsFile(current, board);

      default -> throw new ProgrammingMistakeException("No mutation defined for strict code: " + strictCode);
    };
  }

  // --- Mutation helpers ---

  private static String stripTerminalMarker(String s) {
    if (s.isEmpty()) {
      return s;
    }
    final char last = s.charAt(s.length() - 1);
    if (last == '+' || last == '#') {
      return Nulls.substring(s, 0, s.length() - 1);
    }
    return s;
  }

  private static String stripCaptureMarker(String s) {
    final int x = s.indexOf('x');
    if (x < 0) {
      throw new ProgrammingMistakeException("stripCaptureMarker called with no 'x' in input: " + s);
    }
    return Nulls.substring(s, 0, x) + Nulls.substring(s, x + 1);
  }

  private static String insertCaptureMarker(String s) {
    final String body;
    final String marker;
    if (!s.isEmpty() && (s.charAt(s.length() - 1) == '+' || s.charAt(s.length() - 1) == '#')) {
      body = Nulls.substring(s, 0, s.length() - 1);
      marker = Nulls.valueOf(s.charAt(s.length() - 1));
    } else {
      body = s;
      marker = "";
    }
    if (body.length() < 3) {
      throw new ProgrammingMistakeException("insertCaptureMarker called with body too short: " + s);
    }
    // Insert 'x' immediately before the destination square (last 2 chars of body).
    final int destStart = body.length() - 2;
    return Nulls.substring(body, 0, destStart) + "x" + Nulls.substring(body, destStart) + marker;
  }

  private static String stripFirstDisambigChar(String s) {
    final BodyAndMarker split = splitMarker(s);
    if (split.body().length() < 4) {
      throw new ProgrammingMistakeException("stripFirstDisambigChar called with body too short: " + s);
    }
    // RNBQ form: position 0 = piece, position 1 = disambig char to strip.
    return split.body().charAt(0) + Nulls.substring(split.body(), 2) + split.marker();
  }

  private static String stripSecondDisambigChar(String s) {
    final BodyAndMarker split = splitMarker(s);
    if (split.body().length() < 5) {
      throw new ProgrammingMistakeException("stripSecondDisambigChar called with body too short: " + s);
    }
    // Square disambig form: positions 1 and 2 are file+rank; strip position 2 (rank).
    return split.body().charAt(0) + Nulls.substring(split.body(), 1, 2) + Nulls.substring(split.body(), 3)
        + split.marker();
  }

  private static String stripSquareDisambig(String s) {
    final BodyAndMarker split = splitMarker(s);
    if (split.body().length() < 5) {
      throw new ProgrammingMistakeException("stripSquareDisambig called with body too short: " + s);
    }
    // Square disambig form: strip both file and rank at positions 1 and 2.
    return split.body().charAt(0) + Nulls.substring(split.body(), 3) + split.marker();
  }

  /**
   * Rewrites a rank-disambiguated SAN move to its canonical file-disambiguated form. Used when strict rejects with
   * {@link SanValidationProblem#NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE} - the move is uniquely identified by
   * the user's rank disambig, but canonical SAN prefers file disambig when both forms work. We look up the from-file of
   * the unique matching legal move and substitute it for the rank character.
   */
  private static String rewriteRankAsFile(String s, Board board) {
    final BodyAndMarker split = splitMarker(s);
    final String body = split.body();
    if (body.length() < 4) {
      throw new ProgrammingMistakeException("rewriteRankAsFile body too short: " + s);
    }
    final char pieceLetter = body.charAt(0);
    final char rankDigit = body.charAt(1);
    final PieceType pieceType = NotationMovingPiece.calculate(pieceLetter).getPieceType();
    final Rank fromRank = Rank.calculateRank(rankDigit);
    final Square toSquare = Square.calculate(File.calculateFile(body.charAt(body.length() - 2)),
        Rank.calculateRank(body.charAt(body.length() - 1)));
    final Side havingMove = board.getHavingMove();
    final Piece movingPiece = PieceType.calculate(havingMove, pieceType);

    @Nullable LegalMove match = null;
    for (final LegalMove lm : board.getLegalMoves()) {
      if (lm.movingPiece() == movingPiece && lm.moveSpecification().fromSquare().getRank() == fromRank
          && lm.moveSpecification().toSquare() == toSquare) {
        if (match != null) {
          throw new ProgrammingMistakeException(
              "Multiple legal moves match rank-disambig recovery; strict invariant violated: " + s);
        }
        match = lm;
      }
    }
    if (match == null) {
      throw new ProgrammingMistakeException(
          "No legal move matches rank-disambig recovery; strict invariant violated: " + s);
    }
    final File fromFile = match.moveSpecification().fromSquare().getFile();
    return body.charAt(0) + Nulls.valueOf(fromFile.getLetter()) + Nulls.substring(body, 2) + split.marker();
  }

  private static BodyAndMarker splitMarker(String s) {
    if (!s.isEmpty()) {
      final char last = s.charAt(s.length() - 1);
      if (last == '+' || last == '#') {
        return new BodyAndMarker(Nulls.substring(s, 0, s.length() - 1), Nulls.valueOf(last));
      }
    }
    return new BodyAndMarker(s, "");
  }

  private record BodyAndMarker(String body, String marker) {
  }
}
