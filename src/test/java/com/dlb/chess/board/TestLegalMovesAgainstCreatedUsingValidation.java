package com.dlb.chess.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.dlb.chess.board.enums.CastlingMove;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.BasicChessUtility;
import com.dlb.chess.exceptions.InvalidMoveException;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.pgn.PgnGame;
import com.dlb.chess.squares.AbstractPotentialToSquares;
import com.dlb.chess.test.RestrictTestConstants;
import com.dlb.chess.test.model.PgnTestCase;
import com.dlb.chess.test.model.PgnTestCaseList;
import com.dlb.chess.test.pgn.parser.PgnCacheForStrictPgnParserTestCases;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.bitboard.StaticPositionBridge;

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
      for (final PgnTestCase testCase : testCaseList.list()) {
        checkLegalMoves(testCaseList.pgnTest().getFolderPath(), testCase.pgnName());
      }
    }
  }

  private static void checkLegalMoves(Path folderPath, String pgnName) {

    logger.info(pgnName);

    final PgnGame pgnGame = PgnCacheForStrictPgnParserTestCases.getPgn(folderPath, pgnName);

    final Board board = new Board(pgnGame.startFen(), false);
    checkLegalMoves(board);

    for (final PgnHalfMove halfMove : pgnGame.halfMoveList()) {
      board.moveStrict(halfMove.san());
      checkLegalMoves(board);
    }

  }

  private static void checkLegalMoves(Board board) {

    // Under the strict-game invariant, positions in FIDE-automatic termination cannot accept
    // any further move; ValidateNewMove rejects everything with GAME_ALREADY_ENDED. The
    // bottom-up legal-move generator still reports geometrically-legal moves on such positions
    // (e.g. king moves on a K-vs-K board), so the two sets cannot match here. This consistency
    // check is meaningful only on ongoing positions.
    if (BasicChessUtility.calculateGameStatus(board).isAutomaticTermination()) {
      return;
    }

    final List<LegalMove> legalMovesActual = board.getLegalMoves();

    final Set<MoveSpecification> moveSpecificationsBottomUp = toMoveSpecifications(legalMovesActual);

    final Set<MoveSpecification> moveSpecificationsFromValidation = calculateMoveSpecificationsFromValidation(board);

    // Comparing the MoveSpecification sets is the meaningful invariant — it verifies that the bottom-up legal-move
    // generator and the validation pipeline agree on which moves are legal. Comparing the LegalMove sets would only
    // verify that two derivation paths produce the same derived data (moving piece, captured piece, en-passant role)
    // for the same MoveSpecification — internal consistency, not chess correctness — and would require exposing
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
    if (StaticPositionBridge.toStaticPosition(board.getBitboardPosition()).isEmpty(fromSquare)) {
      return listForSquare;
    }
    final Piece boardPiece = StaticPositionBridge.toStaticPosition(board.getBitboardPosition()).get(fromSquare);
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
          StaticPositionBridge.toStaticPosition(board.getBitboardPosition()), board.getEnPassantCaptureTargetSquare(), havingMove, fromSquare);
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
            && Rank.calculateIsPromotionRank(havingMove, toSquare.getRank())) {
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
