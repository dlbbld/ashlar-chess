package com.dlb.chess.moves;

import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.model.LegalMove;

class KingLegalMoves extends AbstractLegalMoves {
  public static Set<LegalMove> calculateKingLegalMoves(StaticPosition staticPosition, CastlingRight castlingRight,
      Side havingMove, Square fromSquare) {

    final Set<LegalMove> legalMoveSet = new TreeSet<>();

    final Set<LegalMove> kingNonCastlingLegalMoveSet = KingNonCastlingLegalMoves
        .calculateKingNonCastlingLegalMoves(staticPosition, havingMove, fromSquare);
    legalMoveSet.addAll(kingNonCastlingLegalMoveSet);

    final Set<LegalMove> kingCastlingLegalMoveSet = KingCastlingLegalMoves
        .calculateKingCastlingLegalMoves(staticPosition, havingMove, castlingRight);
    legalMoveSet.addAll(kingCastlingLegalMoveSet);

    return legalMoveSet;

  }

}
