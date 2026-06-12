// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import java.util.ArrayList;
import java.util.List;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.CastlingConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;
import io.github.dlbbld.ashlarchess.moves.PromotionUtility;

public class MoveToSan extends AbstractSan {

  public static String calculateSanLastMove(LegalMove lastMove, List<LegalMove> legalMovesBeforeLastMove,
      SanTerminalMarker sanTerminalMarker) {

    // first - check if castling move
    final MoveSpecification moveSpecification = lastMove.moveSpecification();
    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      return calculateSanLastMoveCastling(moveSpecification, sanTerminalMarker);
    }
    return calculateSanLastMoveNonCastling(lastMove, legalMovesBeforeLastMove, sanTerminalMarker);
  }

  private static SanSourceSpecification calculateSourceSpecification(LegalMove legalMove,
      List<LegalMove> legalMovesForMovingPiece) {

    final MoveSpecification moveSpecification = legalMove.moveSpecification();

    final List<LegalMove> legalMovesForPieceAndToSquare = filterLegalMovesCandidates(legalMovesForMovingPiece,
        moveSpecification.toSquare());
    final int numberOfLegalMovesFromSameFile = calculateNumberOfLegalMovesFromFile(
        moveSpecification.fromSquare().getFile(), legalMovesForPieceAndToSquare);
    final int numberOfLegalMovesFromSameRank = calculateNumberOfLegalMovesFromRank(
        moveSpecification.fromSquare().getRank(), legalMovesForPieceAndToSquare);
    final boolean hasOtherFilesHavingLegalMoves = calculateHasOtherFilesHavingLegalMoves(
        moveSpecification.fromSquare().getFile(), legalMovesForPieceAndToSquare);

    if (hasOtherFilesHavingLegalMoves) {
      if (numberOfLegalMovesFromSameFile == 1) {
        return SanSourceSpecification.SOURCE_REQUIRED_FILE_BUT_NOT_RANK;
      }
      if (numberOfLegalMovesFromSameRank == 1) {
        return SanSourceSpecification.SOURCE_REQUIRED_RANK_BUT_NOT_FILE;
      }
      return SanSourceSpecification.SOURCE_REQUIRED_SQUARE;
    }

    if (numberOfLegalMovesFromSameFile == 1) {
      // only one legal move
      return SanSourceSpecification.SOURCE_NOT_REQUIRED;
    }
    if (numberOfLegalMovesFromSameRank == 1) {
      return SanSourceSpecification.SOURCE_REQUIRED_RANK_BUT_NOT_FILE;
    }
    return SanSourceSpecification.SOURCE_REQUIRED_SQUARE;
  }

  private static String calculateSanLastMoveCastling(MoveSpecification moveSpecification,
      SanTerminalMarker sanTerminalMarker) {
    final StringBuilder buildSan = new StringBuilder();
    switch (moveSpecification.castlingMove()) {
      case KING_SIDE -> buildSan.append(CastlingConstants.SAN_CASTLING_KING_SIDE);
      case QUEEN_SIDE -> buildSan.append(CastlingConstants.SAN_CASTLING_QUEEN_SIDE);
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    }

    sanTerminalMarker.append(buildSan);
    return Nulls.toString(buildSan);
  }

  private static String calculateSanLastMoveNonCastling(LegalMove lastMove,
      List<LegalMove> legalMovesBeforeLastMove, SanTerminalMarker sanTerminalMarker) {

    final MoveSpecification moveSpecification = lastMove.moveSpecification();
    final Piece movingPiece = lastMove.movingPiece();
    if (movingPiece == Piece.NONE) {
      throw new ProgrammingMistakeException(
          "Something is wrong, a non castling move always specifies a piece to be moved");
    }

    final String pieceLetter = String.valueOf(movingPiece.getPieceType().getLetter());
    final Square fromSquare = moveSpecification.fromSquare();
    final File fromFile = fromSquare.getFile();
    final Rank fromRank = fromSquare.getRank();
    final String fromFileLetter = String.valueOf(fromFile.getLetter());
    final int fromRankNumber = fromRank.getNumber();
    final String toSquareName = moveSpecification.toSquare().getName();
    final boolean isCapture = lastMove.pieceCaptured() != Piece.NONE;

    final StringBuilder buildSan = new StringBuilder();

    switch (movingPiece.getPieceType()) {
      case PAWN:
        if (!PromotionUtility.calculateIsPromotion(moveSpecification)) {
          if (isCapture) {
            buildSan.append(fromFileLetter).append(SanSymbol.CAPTURE.getSymbol());
          }
          buildSan.append(toSquareName);
        } else {
          final char promotionPieceLetter = moveSpecification.promotionPieceType().getPieceType().getLetter();
          if (isCapture) {
            buildSan.append(fromFileLetter).append(SanSymbol.CAPTURE.getSymbol());
          }
          buildSan.append(toSquareName).append(SanSymbol.PROMOTION.getSymbol()).append(promotionPieceLetter);
        }
        break;
      case ROOK:
      case KNIGHT:
      case BISHOP:
      case QUEEN:
        buildSan.append(pieceLetter);

        final List<LegalMove> legalMovesForMovingPiece = calculateLegalMovesForMovingPiece(lastMove.movingPiece(),
            legalMovesBeforeLastMove);

        final SanSourceSpecification sourceSpecification = calculateSourceSpecification(lastMove,
            legalMovesForMovingPiece);
        switch (sourceSpecification) {
          case SOURCE_NOT_REQUIRED:
            // nothing to add
            break;
          case SOURCE_REQUIRED_FILE_BUT_NOT_RANK:
            buildSan.append(fromFileLetter);
            break;
          case SOURCE_REQUIRED_RANK_BUT_NOT_FILE:
            buildSan.append(fromRankNumber);
            break;
          case SOURCE_REQUIRED_SQUARE:
            buildSan.append(fromFileLetter);
            buildSan.append(fromRankNumber);
            break;
          default:
            throw new IllegalArgumentException();
        }

        if (isCapture) {
          buildSan.append(SanSymbol.CAPTURE.getSymbol());
        }

        buildSan.append(toSquareName);
        break;
      case KING:
        buildSan.append(pieceLetter);
        if (isCapture) {
          buildSan.append(SanSymbol.CAPTURE.getSymbol());
        }
        buildSan.append(toSquareName);
        break;
      case NONE:
      default:
        throw new IllegalArgumentException();
    }
    sanTerminalMarker.append(buildSan);
    return Nulls.toString(buildSan);
  }

  // semantics for moving piece: for castling the moving piece is none! so the castling is not returned here when
  // searching for the king as moving piece!!!
  static List<LegalMove> calculateLegalMovesForMovingPiece(Piece movingPiece, List<LegalMove> legalMoves) {
    final List<LegalMove> legalMovesForMovingPiece = new ArrayList<>();
    for (final LegalMove legalMove : legalMoves) {
      if (legalMove.movingPiece() == movingPiece) {
        legalMovesForMovingPiece.add(legalMove);
      }
    }
    return legalMovesForMovingPiece;
  }
}
