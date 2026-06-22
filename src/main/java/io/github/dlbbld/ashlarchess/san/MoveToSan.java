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
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;

public final class MoveToSan {

  private MoveToSan() {
  }

  public static String toSan(LegalMove move, List<LegalMove> legalMovesBeforeMove,
      SanTerminalMarker sanTerminalMarker) {

    // first - check if castling move
    final MoveSpecification moveSpecification = move.moveSpecification();
    if (CastlingUtility.isCastlingMove(moveSpecification)) {
      return calculateSanLastMoveCastling(moveSpecification, sanTerminalMarker);
    }
    return calculateSanLastMoveNonCastling(move, legalMovesBeforeMove, sanTerminalMarker);
  }

  private static SanSourceSpecification calculateSourceSpecification(LegalMove legalMove,
      List<LegalMove> legalMovesForMovingPiece) {

    final MoveSpecification moveSpecification = legalMove.moveSpecification();

    final List<LegalMove> legalMovesForPieceAndToSquare = SanDisambiguationUtility
        .filterLegalMovesCandidates(legalMovesForMovingPiece, moveSpecification.toSquare());
    final int numberOfLegalMovesFromSameFile = SanDisambiguationUtility
        .calculateNumberOfLegalMovesFromFile(moveSpecification.fromSquare().getFile(), legalMovesForPieceAndToSquare);
    final int numberOfLegalMovesFromSameRank = SanDisambiguationUtility
        .calculateNumberOfLegalMovesFromRank(moveSpecification.fromSquare().getRank(), legalMovesForPieceAndToSquare);
    final boolean hasOtherFilesHavingLegalMoves = SanDisambiguationUtility.calculateHasOtherFilesHavingLegalMoves(
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

    SanTerminalMarkerUtility.appendTo(buildSan, sanTerminalMarker);
    return Nulls.toString(buildSan);
  }

  private static String calculateSanLastMoveNonCastling(LegalMove lastMove, List<LegalMove> legalMovesBeforeLastMove,
      SanTerminalMarker sanTerminalMarker) {

    final MoveSpecification moveSpecification = lastMove.moveSpecification();
    // LegalMove's canonical constructor forbids a NONE moving piece (see LegalMove), so movingPiece is always a real
    // piece here.
    final Piece movingPiece = lastMove.movingPiece();
    final String pieceLetter = String.valueOf(movingPiece.getPieceType().getLetter());
    final Square fromSquare = moveSpecification.fromSquare();
    final File fromFile = fromSquare.getFile();
    final Rank fromRank = fromSquare.getRank();
    final String fromFileLetter = String.valueOf(fromFile.getLetter());
    final int fromRankNumber = fromRank.getNumber();
    final String toSquareName = moveSpecification.toSquare().getName();
    final boolean isCapture = lastMove.capturedPiece() != Piece.NONE;

    final StringBuilder buildSan = new StringBuilder();

    switch (movingPiece.getPieceType()) {
      case PAWN:
        if (!moveSpecification.isPromotion()) {
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
    SanTerminalMarkerUtility.appendTo(buildSan, sanTerminalMarker);
    return Nulls.toString(buildSan);
  }

  // Castling moves carry the king as their moving piece (not NONE), so searching for the king here also returns the
  // side's castling moves. That is harmless for SAN disambiguation: a castling move and a normal king move never share
  // a destination square, so castling never collides with a normal king move's from-file / from-rank disambiguation.
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
