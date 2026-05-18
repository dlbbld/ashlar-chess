package com.dlb.chess.bitboard;

import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.LegalMoveKind;

/**
 * Converts a bare {@link MoveSpecification} (as produced by {@link BitboardPosition#legalMoves}) into a fully-typed
 * {@link LegalMove} record. The converter determines the moving piece and captured piece from the position, plus
 * the {@link LegalMoveKind} from the move shape:
 *
 * <ul>
 * <li>{@link LegalMoveKind#CASTLING} — when {@code moveSpec.castlingMove()} is non-NONE.</li>
 * <li>{@link LegalMoveKind#PROMOTION} — when {@code moveSpec.promotionPieceType()} is non-NONE.</li>
 * <li>{@link LegalMoveKind#PAWN_TWO_SQUARE_ADVANCE} — pawn moving two ranks.</li>
 * <li>{@link LegalMoveKind#EN_PASSANT_CAPTURE} — pawn moving diagonally onto an empty square.</li>
 * <li>{@link LegalMoveKind#NORMAL} — everything else.</li>
 * </ul>
 *
 * <p>
 * This is the Phase 2 prerequisite for porting {@code Board.getLegalMoves()} and the unwinnability analyzers off the
 * StaticPosition-backed move pipeline.
 */
public final class BitboardLegalMoveFactory {

  private BitboardLegalMoveFactory() {
  }

  public static LegalMove toLegalMove(BitboardPosition position, MoveSpecification moveSpec, Side movingSide) {
    if (movingSide != Side.WHITE && movingSide != Side.BLACK) {
      throw new IllegalArgumentException("toLegalMove requires Side.WHITE or Side.BLACK, got " + movingSide);
    }
    if (moveSpec.castlingMove() != com.dlb.chess.board.enums.CastlingMove.NONE) {
      final Piece kingPiece = movingSide == Side.WHITE ? Piece.WHITE_KING : Piece.BLACK_KING;
      return new LegalMove(moveSpec, kingPiece, Piece.NONE, LegalMoveKind.CASTLING);
    }

    final Square from = moveSpec.fromSquare();
    final Square to = moveSpec.toSquare();
    final Piece movingPiece = position.get(from);
    if (movingPiece == Piece.NONE) {
      throw new IllegalArgumentException("No piece on the from-square " + from.getName());
    }

    final boolean isPawn = movingPiece.getPieceType() == PieceType.PAWN;
    final boolean diagonalPawnMove = isPawn && from.getFile() != to.getFile();
    final boolean toEmpty = position.isEmpty(to);

    final Piece capturedPiece;
    if (!toEmpty) {
      capturedPiece = position.get(to);
    } else if (diagonalPawnMove) {
      // En-passant: captured pawn on the same rank as `from` (one rank back from `to`).
      capturedPiece = movingSide == Side.WHITE ? Piece.BLACK_PAWN : Piece.WHITE_PAWN;
    } else {
      capturedPiece = Piece.NONE;
    }

    final LegalMoveKind kind;
    if (moveSpec.promotionPieceType() != PromotionPieceType.NONE) {
      kind = LegalMoveKind.PROMOTION;
    } else if (isPawn && Math.abs(from.getRank().getNumber() - to.getRank().getNumber()) == 2) {
      kind = LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE;
    } else if (diagonalPawnMove && toEmpty) {
      kind = LegalMoveKind.EN_PASSANT_CAPTURE;
    } else {
      kind = LegalMoveKind.NORMAL;
    }

    return new LegalMove(moveSpec, movingPiece, capturedPiece, kind);
  }
}
