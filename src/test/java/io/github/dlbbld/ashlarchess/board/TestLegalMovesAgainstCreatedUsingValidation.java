// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.RankUtility;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.exceptions.InvalidMoveException;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.PgnMove;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.squares.AbstractPotentialToSquares;
import io.github.dlbbld.ashlarchess.test.RestrictTestConstants;
import io.github.dlbbld.ashlarchess.test.model.PgnFen;
import io.github.dlbbld.ashlarchess.test.model.PgnTestCaseList;
import io.github.dlbbld.ashlarchess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import io.github.dlbbld.ashlarchess.test.pgn.setup.PgnTestCaseCatalog;

class TestLegalMovesAgainstCreatedUsingValidation {

  private static final Logger logger = Nulls.getLogger(TestLegalMovesAgainstCreatedUsingValidation.class);

  @SuppressWarnings("static-method")
  @Test
  void test() {
    // the move generation from validation for testing is about ten times slower than the used on
    // so we only perform a spot checks on the PGN^s
    for (final PgnTestCaseList testCaseList : PgnTestCaseCatalog.getRestrictedTestListList()) {
      if (RestrictTestConstants.IS_RESTRICT_PGN_LEGAL_MOVE_VALIDATION_AGAINST_BOTTOM_UP_TEST) {
        switch (testCaseList.pgnTest()) {
          case BASIC_CHECK_WHITE:
          case BASIC_CHECK_BLACK:
          case BASIC_CHECKMATE_WHITE:
          case BASIC_CHECKMATE_BLACK:
          case BASIC_STALEMATE:
            break;
          // $CASES-OMITTED$
          default:
            continue;
        }
      }
      for (final PgnFen testCase : testCaseList.list()) {
        checkLegalMoves(testCaseList.pgnTest().getFolderPath(), testCase.pgnName());
      }
    }
  }

  private static void checkLegalMoves(Path folderPath, String pgnName) {

    logger.info(pgnName);

    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(folderPath, pgnName);

    final Board board = new Board(pgnGame.startFen());
    checkLegalMoves(board);

    for (final PgnMove move : pgnGame.moveList()) {
      board.moveStrict(move.san());
      checkLegalMoves(board);
    }

  }

  private static void checkLegalMoves(Board board) {

    final List<LegalMove> legalMovesActual = board.getLegalMoves();

    final Set<MoveSpecification> moveSpecificationsBottomUp = toMoveSpecifications(legalMovesActual);

    final Set<MoveSpecification> moveSpecificationsFromValidation = calculateMoveSpecificationsFromValidation(board);

    // Comparing the MoveSpecification sets is the meaningful invariant - it verifies that the bottom-up legal-move
    // generator and the validation pipeline agree on which moves are legal. Comparing the LegalMove sets would only
    // verify that two derivation paths produce the same derived data (moving piece, captured piece, en-passant role)
    // for the same MoveSpecification - internal consistency, not chess correctness - and would require exposing
    // Board.calculateLegalMove publicly, which the rule pipeline has no business letting outside callers touch.
    assertEquals(moveSpecificationsFromValidation, moveSpecificationsBottomUp);
  }

  private static Set<MoveSpecification> calculateMoveSpecificationsFromValidation(Board board) {
    final Set<MoveSpecification> listForBoard = new TreeSet<>();
    // now we do something crazy:
    // we loop through all possible from/to square combinations and filter out the legal ones using the validation
    // this must match with the calculated legal moves - so both methods are hopefully correct (or both wrong..)
    for (final Square fromSquare : Square.REAL) {
      final Set<MoveSpecification> listForSquare = calculateMoveSpecificationsFromValidation(board, fromSquare);
      listForBoard.addAll(listForSquare);
    }
    return listForBoard;
  }

  private static Set<MoveSpecification> calculateMoveSpecificationsFromValidation(Board board, Square fromSquare) {
    final Side havingMove = board.getHavingMove();

    final Set<MoveSpecification> listForSquare = new TreeSet<>();
    // now we do something crazy:
    // we loop through all possible from/to square combinations and filter out the legal ones using the validation
    // this must match with the calculated legal moves - so both methods are hopefully correct (or both wrong..)
    if (board.getBitboardPosition().isEmpty(fromSquare)) {
      return listForSquare;
    }
    final Piece boardPiece = board.getBitboardPosition().get(fromSquare);
    if (boardPiece.getSide() == havingMove) {
      // castling needs special treatment as always
      if (boardPiece.getPieceType() == PieceType.KING) {
        final MoveSpecification castlingKingSide = new MoveSpecification(CastlingMove.KING_SIDE);
        try {
          ValidateNewMove.validateNewMove(board, castlingKingSide);
          listForSquare.add(castlingKingSide);
        } catch (@SuppressWarnings("unused") final InvalidMoveException e) {
          // not valid, so not adding
        }
        final MoveSpecification castlingQueenSide = new MoveSpecification(CastlingMove.QUEEN_SIDE);
        try {
          ValidateNewMove.validateNewMove(board, castlingQueenSide);
          listForSquare.add(castlingQueenSide);
        } catch (@SuppressWarnings("unused") final InvalidMoveException e) {
          // not valid, so not adding
        }
      }
      final Set<Square> potentialToSquareSet = AbstractPotentialToSquares.calculatePotentialToSquare(
          StaticPositionBridge.toStaticPosition(board.getBitboardPosition()), board.getEnPassantCaptureTargetSquare(),
          havingMove, fromSquare);
      // we cannot use all board squares - that get's too slow
      // all PGN's expected outcomes are not through in 90 minutes
      for (final Square toSquare : potentialToSquareSet) {
        final MoveSpecification move = new MoveSpecification(fromSquare, toSquare);
        try {
          ValidateNewMove.validateNewMove(board, move);
          listForSquare.add(move);
        } catch (@SuppressWarnings("unused") final InvalidMoveException e) {
          // not valid, so not adding
        }
        // we only check the actual promotion moves and not all silly possible combinations
        // that get's too much otherwise
        if (boardPiece.getPieceType() == PieceType.PAWN
            && RankUtility.calculateIsPromotionRank(havingMove, toSquare.getRank())) {
          for (final PromotionPieceType promotionPieceType : PromotionPieceType.REAL) {
            final MoveSpecification promotionMove = new MoveSpecification(fromSquare, toSquare, promotionPieceType);
            try {
              ValidateNewMove.validateNewMove(board, promotionMove);
              listForSquare.add(promotionMove);
            } catch (@SuppressWarnings("unused") final InvalidMoveException e) {
              // not valid, so not adding
            }

          }
        }
      }
    }
    return listForSquare;
  }

  private static Set<MoveSpecification> toMoveSpecifications(List<LegalMove> legalMoves) {
    final Set<MoveSpecification> result = new TreeSet<>();
    for (final LegalMove legalMove : legalMoves) {
      result.add(legalMove.moveSpecification());
    }
    return result;
  }

}
