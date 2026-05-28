package io.github.dlbbld.ashlarchess.moves;

import java.util.Set;
import java.util.TreeSet;

import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.model.LegalMove;

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
