// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.analyze;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.analyze.ChessRuleAnalyzer;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.enums.KingSafetyCheck;
import io.github.dlbbld.ashlarchess.enums.MoveCheck;
import io.github.dlbbld.ashlarchess.enums.MovementCheck;
import io.github.dlbbld.ashlarchess.exceptions.InvalidMoveException;

/**
 * Base for analyzer-routed scenario tests. The {@link #check} helper inspects the expected {@link MoveCheck} and
 * dispatches:
 * <ul>
 * <li>movement-category and king-specific MoveChecks ({@code MOVEMENT_*}, {@code KING_CAPTURES_GUARDED_PIECE},
 * {@code KING_MOVES_NEXT_TO_OPPONENT_KING}) - call {@link ChessRuleAnalyzer#analyzeMovement} directly and assert the
 * translated MovementCheck;</li>
 * <li>king-safety MoveChecks ({@code KING_KING_*}, {@code ALL_BUT_KING_KING_*}) - call
 * {@link ChessRuleAnalyzer#analyzeKingSafety} directly and assert the translated KingSafetyCheck;</li>
 * <li>spec-coherence and castling MoveChecks ({@code MOVE_SPEC_*}, {@code KING_CASTLING_*}) - fall back to
 * {@code Board.move} and assert the InvalidMoveException's MoveCheck. These are not in analyzer territory.</li>
 * </ul>
 *
 * <p>
 * The fallback path lets the inherited TestValidateNewMove scenarios run unchanged in this class while routing
 * analyzer-territory assertions through the analyzer for direct test coverage.
 */
public abstract class AbstractTestChessRuleAnalyzerScenarios implements EnumConstants {

  static void check(Board board, MoveSpecification move, MoveCheck expectedMoveCheck) {
    final MovementCheck expectedMc = toMovementCheck(expectedMoveCheck);
    if (expectedMc != null) {
      final MovementCheck actual = ChessRuleAnalyzer.analyzeMovement(board.getBitboardPosition(), board.getHavingMove(),
          board.getEnPassantCaptureTargetSquare(), move);
      assertEquals(expectedMc, actual);
      return;
    }
    final KingSafetyCheck expectedKs = toKingSafetyCheck(expectedMoveCheck);
    if (expectedKs != null) {
      // king-safety presupposes movement passes
      final MovementCheck actualMc = ChessRuleAnalyzer.analyzeMovement(board.getBitboardPosition(),
          board.getHavingMove(), board.getEnPassantCaptureTargetSquare(), move);
      assertEquals(MovementCheck.SUCCESS, actualMc, "movement should pass before king-safety check");
      final KingSafetyCheck actualKs = ChessRuleAnalyzer.analyzeKingSafety(board.getBitboardPosition(),
          board.getHavingMove(), move);
      assertEquals(expectedKs, actualKs);
      return;
    }
    // fallback: spec-coherence or castling - exercise via the surface
    boolean isException = false;
    try {
      board.move(move);
    } catch (final InvalidMoveException e) {
      isException = true;
      assertEquals(expectedMoveCheck, e.getMoveCheck());
    }
    assertTrue(isException);
  }

  static void check(String fen, MoveSpecification move, MoveCheck expectedMoveCheck) {
    check(new Board(fen), move, expectedMoveCheck);
  }

  private static @Nullable MovementCheck toMovementCheck(MoveCheck mc) {
    return switch (mc) {
      case MOVEMENT_NOT_POSSIBLE -> MovementCheck.NOT_POSSIBLE;
      case MOVEMENT_TO_SQUARE_OCCUPIED_BY_OWN_PIECE -> MovementCheck.TO_SQUARE_OCCUPIED_BY_OWN_PIECE;
      case MOVEMENT_LONG_RANGE_PIECE_JUMPS_OVER_PIECE -> MovementCheck.LONG_RANGE_PIECE_JUMPS_OVER_PIECE;
      case MOVEMENT_PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY -> MovementCheck.PAWN_FORWARD_TWO_SQUARE_JUMP_OVER_SQUARE_ONLY_NOT_EMPTY;
      case MOVEMENT_PAWN_FORWARD_TWO_SQUARE_TO_SQUARE_ONLY_NOT_EMPTY -> MovementCheck.PAWN_FORWARD_TWO_SQUARE_TO_SQUARE_ONLY_NOT_EMPTY;
      case MOVEMENT_PAWN_FORWARD_TWO_SQUARE_BOTH_SQUARE_NOT_EMPTY -> MovementCheck.PAWN_FORWARD_TWO_SQUARE_BOTH_SQUARE_NOT_EMPTY;
      case MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE -> MovementCheck.PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OWN_PIECE;
      case MOVEMENT_PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE -> MovementCheck.PAWN_FORWARD_ONE_SQUARE_TO_SQUARE_NOT_EMPTY_OPPONENT_PIECE;
      case MOVEMENT_PAWN_DIAGONAL_OWN_PIECE -> MovementCheck.PAWN_DIAGONAL_OWN_PIECE;
      case MOVEMENT_PAWN_EN_PASSANT_WRONG_RANK -> MovementCheck.PAWN_EN_PASSANT_WRONG_RANK;
      case MOVEMENT_PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE -> MovementCheck.PAWN_EN_PASSANT_NO_IMMEDIATE_BEFORE_TWO_SQUARE_ADVANCE;
      case KING_CAPTURES_GUARDED_PIECE -> MovementCheck.KING_CAPTURES_GUARDED_PIECE;
      case KING_MOVES_NEXT_TO_OPPONENT_KING -> MovementCheck.KING_MOVES_NEXT_TO_OPPONENT_KING;
      case KING_MOVES_TO_ATTACKED_EMPTY_SQUARE -> MovementCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE;
      default -> null;
    };
  }

  private static @Nullable KingSafetyCheck toKingSafetyCheck(MoveCheck mc) {
    return switch (mc) {
      case ALL_BUT_KING_KING_LEFT_IN_CHECK -> KingSafetyCheck.NON_KING_LEFT_IN_CHECK;
      case ALL_BUT_KING_KING_EXPOSED_TO_CHECK -> KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK;
      default -> null;
    };
  }
}
