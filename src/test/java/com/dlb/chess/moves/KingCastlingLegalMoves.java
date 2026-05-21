package com.dlb.chess.moves;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.constants.CastlingConstants;
import com.dlb.chess.enums.CastlingCheck;
import com.dlb.chess.model.LegalMove;

class KingCastlingLegalMoves extends KingLegalMoves {
  public static Set<LegalMove> calculateKingCastlingLegalMoves(StaticPosition staticPosition, Side havingMove,
      CastlingRight castlingRight) {
    // StaticPosition overload bridges through the bitboard pipeline.
    return calculateKingCastlingLegalMoves(StaticPositionBridge.fromStaticPosition(staticPosition), havingMove,
        castlingRight);
  }

  // Bitboard sibling of the StaticPosition overload above. The bitboard production pipeline reaches this through
  // AbstractLegalMoves.calculateCastlingLegalMoves(BitboardPosition, ...).
  public static Set<LegalMove> calculateKingCastlingLegalMoves(BitboardPosition bitboardPosition, Side havingMove,
      CastlingRight castlingRight) {

    final Set<LegalMove> legalMoveSet = new TreeSet<>();

    switch (havingMove) {
      case BLACK:
        if (CastlingUtility.calculateQueenSideCastlingCheck(bitboardPosition, havingMove,
            castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.BLACK_QUEEN_SIDE_CASTLING_MOVE);
        }

        if (CastlingUtility.calculateKingSideCastlingCheck(bitboardPosition, havingMove,
            castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.BLACK_KING_SIDE_CASTLING_MOVE);
        }
        break;
      case WHITE:
        if (CastlingUtility.calculateQueenSideCastlingCheck(bitboardPosition, havingMove,
            castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.WHITE_QUEEN_SIDE_CASTLING_MOVE);
        }

        if (CastlingUtility.calculateKingSideCastlingCheck(bitboardPosition, havingMove,
            castlingRight) == CastlingCheck.SUCCESS) {
          legalMoveSet.add(CastlingConstants.WHITE_KING_SIDE_CASTLING_MOVE);
        }
        break;
      case NONE:
      default:
        throw new IllegalArgumentException();

    }
    return legalMoveSet;
  }

}
