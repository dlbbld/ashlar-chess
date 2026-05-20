package com.dlb.chess.test.bitboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.bitboard.PawnMoves;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Differential test for {@link PawnMoves#captures}. Regular captures = diagonal-forward squares occupied by an
 * opponent piece; en-passant = diagonal-forward square equal to the en-passant target (only relevant to the side
 * to move on the fixture board). The reference oracle is derived from {@link StaticPosition#isOpponentPiece} plus a
 * direct EP-target comparison.
 */
class TestPawnCaptures {

  @SuppressWarnings("static-method")
  @Test
  void corpusCapturesAgree() {
    for (final PgnTest pgnTest : PgnTest.values()) {
      final PgnTestCaseList testCaseList = PgnTestCaseCatalog.getTestList(pgnTest);
      for (final PgnTestCase testCase : testCaseList.list()) {
        final Board board = testCase.finalPosition();
        final StaticPosition staticPosition = BitboardPositionUtility.toStaticPosition(board.getBitboardPosition());
        final BitboardPosition bitboardPosition = BitboardPositionUtility.fromStaticPosition(staticPosition);
        final Square boardEpTarget = board.getEnPassantCaptureTargetSquare();
        final Side havingMove = board.getHavingMove();

        // EP is only available to the side to move on the fixture board; the other side gets 0L.
        final long epForWhite = (havingMove == Side.WHITE && boardEpTarget != Square.NONE)
            ? (1L << boardEpTarget.ordinal()) : 0L;
        final long epForBlack = (havingMove == Side.BLACK && boardEpTarget != Square.NONE)
            ? (1L << boardEpTarget.ordinal()) : 0L;
        final Square epForWhiteSquare = epForWhite == 0L ? Square.NONE : boardEpTarget;
        final Square epForBlackSquare = epForBlack == 0L ? Square.NONE : boardEpTarget;

        assertCapturesAgree(staticPosition, bitboardPosition.whitePawns(), Side.WHITE,
            bitboardPosition.occupied(Side.BLACK), epForWhite, epForWhiteSquare, testCase);
        assertCapturesAgree(staticPosition, bitboardPosition.blackPawns(), Side.BLACK,
            bitboardPosition.occupied(Side.WHITE), epForBlack, epForBlackSquare, testCase);
      }
    }
  }

  private static void assertCapturesAgree(StaticPosition staticPosition, long pawns, Side side, long opponentPieces,
      long enPassantBit, Square enPassantSquare, PgnTestCase testCase) {
    long remaining = pawns;
    while (remaining != 0L) {
      final int squareOrdinal = Long.numberOfTrailingZeros(remaining);
      final Square fromSquare = Nulls.get(Square.REAL, squareOrdinal);
      final Set<Square> bitboardCaptures = BitboardPositionUtility
          .toSquareSet(PawnMoves.captures(squareOrdinal, opponentPieces, enPassantBit, side));
      final Set<Square> referenceCaptures = referenceCaptures(staticPosition, fromSquare, side, enPassantSquare);
      assertEquals(referenceCaptures, bitboardCaptures,
          side + " pawn captures from " + fromSquare.getName() + " in fixture " + testCase.pgnName());
      remaining &= remaining - 1L;
    }
  }

  private static Set<Square> referenceCaptures(StaticPosition staticPosition, Square from, Side side,
      Square enPassantSquare) {
    final Set<Square> result = new TreeSet<>();
    final int fromFile = from.getFile().getNumber();
    final int fromRank = from.getRank().getNumber();
    final int rankOffset = side == Side.WHITE ? 1 : -1;
    final int toRank = fromRank + rankOffset;
    if (toRank < 1 || toRank > 8) {
      return result;
    }
    for (final int fileOffset : new int[] { -1, +1 }) {
      final int toFile = fromFile + fileOffset;
      if (toFile < 1 || toFile > 8) {
        continue;
      }
      final Square target = Square.calculate(toFile, toRank);
      if (staticPosition.isOpponentPiece(target, side)) {
        result.add(target);
      } else if (enPassantSquare != Square.NONE && target == enPassantSquare) {
        result.add(target);
      }
    }
    return result;
  }

  @SuppressWarnings("static-method")
  @Test
  void initialPositionNoCaptures() {
    // No opponent pieces are reachable from any pawn on the initial rank, no EP target.
    final long whiteOpp = BitboardPosition.INITIAL_POSITION.occupied(Side.BLACK);
    for (int file = 0; file < 8; file++) {
      final int squareOrdinal = 8 + file;
      assertEquals(0L, PawnMoves.captures(squareOrdinal, whiteOpp, 0L, Side.WHITE),
          "white pawn at file " + file + " has no captures from initial position");
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void outOfRangeAndNoneSideThrow() {
    assertThrows(IllegalArgumentException.class, () -> PawnMoves.captures(-1, 0L, 0L, Side.WHITE));
    assertThrows(IllegalArgumentException.class, () -> PawnMoves.captures(64, 0L, 0L, Side.WHITE));
    assertThrows(IllegalArgumentException.class, () -> PawnMoves.captures(0, 0L, 0L, Side.NONE));
  }
}
