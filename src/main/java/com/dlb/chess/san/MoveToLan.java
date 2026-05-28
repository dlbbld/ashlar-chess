package com.dlb.chess.san;

import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.CastlingConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.moves.CastlingUtility;
import com.dlb.chess.moves.PromotionUtility;

public class MoveToLan extends AbstractSan {

  /**
   * LAN non-capture separator between from and to squares. Captures use {@link SanSymbol#CAPTURE} ({@code 'x'})
   * instead; castling emits {@code O-O} / {@code O-O-O} without per-square components.
   *
   * <p>
   * LAN grammar this class emits:
   * 
   * <pre>
   * &lt;LAN piece move&gt; ::= &lt;piece symbol&gt;&lt;from square&gt;('-'|'x')&lt;to square&gt;
   * &lt;LAN pawn move&gt;  ::= &lt;from square&gt;('-'|'x')&lt;to square&gt;[<promoted to>]
   * &lt;LAN castle&gt;     ::= 'O-O' | 'O-O-O'
   * &lt;promoted to&gt;    ::= '=' ('N' | 'B' | 'R' | 'Q')
   * </pre>
   * 
   * Plus the terminal marker ({@code +} for check, {@code #} for checkmate) appended to every move shape including
   * castling. Matches python-chess {@code board.lan(move)}.
   */
  private static final char LAN_NON_CAPTURE_SEPARATOR = '-';

  public static String calculateLanLastMove(LegalMove lastMove, SanTerminalMarker sanTerminalMarker) {
    final MoveSpecification moveSpecification = lastMove.moveSpecification();
    final StringBuilder buildSan = new StringBuilder();

    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      final String castlingLan = switch (moveSpecification.castlingMove()) {
        case KING_SIDE -> CastlingConstants.SAN_CASTLING_KING_SIDE;
        case QUEEN_SIDE -> CastlingConstants.SAN_CASTLING_QUEEN_SIDE;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      buildSan.append(castlingLan);
      sanTerminalMarker.append(buildSan);
      return Nulls.toString(buildSan);
    }

    final Piece movingPiece = lastMove.movingPiece();
    final String fromSquareName = moveSpecification.fromSquare().getName();
    final String toSquareName = moveSpecification.toSquare().getName();
    final var isCapture = lastMove.pieceCaptured() != Piece.NONE;
    switch (movingPiece.getPieceType()) {
      case PAWN:
        buildSan.append(fromSquareName);
        buildSan.append(isCapture ? SanSymbol.CAPTURE.getSymbol() : LAN_NON_CAPTURE_SEPARATOR);
        buildSan.append(toSquareName);
        if (PromotionUtility.calculateIsPromotion(moveSpecification)) {
          final var promotionPieceLetter = moveSpecification.promotionPieceType().getPieceType().getLetter();
          buildSan.append(SanSymbol.PROMOTION.getSymbol());
          buildSan.append(promotionPieceLetter);
        }
        break;
      case ROOK:
      case KNIGHT:
      case BISHOP:
      case QUEEN:
      case KING:
        final var pieceLetter = String.valueOf(movingPiece.getPieceType().getLetter());
        buildSan.append(pieceLetter);
        buildSan.append(fromSquareName);
        buildSan.append(isCapture ? SanSymbol.CAPTURE.getSymbol() : LAN_NON_CAPTURE_SEPARATOR);
        buildSan.append(toSquareName);
        break;
      case NONE:
      default:
        throw new IllegalArgumentException();
    }
    sanTerminalMarker.append(buildSan);

    return Nulls.toString(buildSan);
  }
}
