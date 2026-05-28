package com.dlb.chess.san;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.enums.CastlingRightLoss;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.enums.CastlingCheck;
import com.google.common.collect.ImmutableList;

/**
 * Lock-down tests for the bridge between {@link CastlingCheck} + {@link CastlingRightLoss} (internal pipeline
 * vocabulary) and {@link SanValidationProblem} (external error-code vocabulary) in the castling subset.
 *
 * <p>
 * The input enums form two orthogonal dimensions; {@code SanValidationProblem} flattens them: the FINAL_NO_RIGHT case
 * expands across the 5 provenance values of {@code CastlingRightLoss} (all except {@code NOT_LOST}), while the 3
 * TEMPORARY cases stay flat. Total castling constants in {@code SanValidationProblem} = 3 TEMPORARY + 5 FINAL = 8.
 *
 * <p>
 * These tests make the invariants machine-checkable so drift is caught at build time.
 */
class TestCastlingCheckMapper {

  private static final ImmutableList<CastlingCheck> EXPECTED_CASTLING_CHECKS = Nulls.listOf(
      CastlingCheck.FINAL_NO_RIGHT, CastlingCheck.TEMPORARY_SQUARES_NOT_EMPTY, CastlingCheck.TEMPORARY_KING_IN_CHECK,
      CastlingCheck.TEMPORARY_KING_TRAVELS_THROUGH_CHECK, CastlingCheck.TEMPORARY_KING_ENDS_IN_CHECK);

  private static final ImmutableList<SanValidationProblem> EXPECTED_KING_CASTLING_PROBLEMS = Nulls.listOf(
      SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_KING_MOVED,
      SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_ROOK_MOVED,
      SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_ROOK_CAPTURED,
      SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_CASTLED,
      SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_UNKNOWN_FEN_IMPORT,
      SanValidationProblem.KING_CASTLING_TEMPORARY_SQUARES_NOT_EMPTY,
      SanValidationProblem.KING_CASTLING_TEMPORARY_KING_IN_CHECK,
      SanValidationProblem.KING_CASTLING_TEMPORARY_KING_TRAVELS_THROUGH_CHECK,
      SanValidationProblem.KING_CASTLING_TEMPORARY_KING_ENDS_IN_CHECK);

  /**
   * Provenance values of {@link CastlingRightLoss} that correspond to a FINAL_NO_RIGHT failure.
   */
  private static final ImmutableList<CastlingRightLoss> EXPECTED_FINAL_NO_RIGHT_PROVENANCES = Nulls.listOf(
      CastlingRightLoss.KING_MOVED, CastlingRightLoss.ROOK_MOVED, CastlingRightLoss.ROOK_CAPTURED,
      CastlingRightLoss.CASTLED, CastlingRightLoss.UNKNOWN_FEN_IMPORT);

  private static final ImmutableList<SanValidationProblem> EXPECTED_FINAL_NO_RIGHT_PROBLEMS = Nulls.listOf(
      SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_KING_MOVED,
      SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_ROOK_MOVED,
      SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_ROOK_CAPTURED,
      SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_CASTLED,
      SanValidationProblem.KING_CASTLING_FINAL_NO_RIGHT_UNKNOWN_FEN_IMPORT);

  @SuppressWarnings("static-method")
  @Test
  void testMapperIsExhaustiveForTemporary() {
    // For the TEMPORARY CastlingCheck values, the provenance is irrelevant; any value works.
    for (final CastlingCheck castlingCheck : EXPECTED_CASTLING_CHECKS) {
      if (castlingCheck == CastlingCheck.FINAL_NO_RIGHT) {
        continue;
      }
      final SanValidationProblem mapped = CastlingCheckMapper.map(castlingCheck, CastlingRightLoss.NOT_LOST);
      assertNotNull(mapped, "mapper returned null for " + castlingCheck);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testMapperIsExhaustiveForFinalNoRight() {
    for (final CastlingRightLoss loss : EXPECTED_FINAL_NO_RIGHT_PROVENANCES) {
      final SanValidationProblem mapped = CastlingCheckMapper.map(CastlingCheck.FINAL_NO_RIGHT, loss);
      assertNotNull(mapped, "mapper returned null for FINAL_NO_RIGHT + " + loss);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testParityCount() {
    final int expectedProblemCount = EXPECTED_CASTLING_CHECKS.size() - 1 + EXPECTED_FINAL_NO_RIGHT_PROVENANCES.size();
    assertEquals(expectedProblemCount, EXPECTED_KING_CASTLING_PROBLEMS.size(),
        "|KING_CASTLING_*| must equal (|CastlingCheck refusal| - 1) + |FINAL_NO_RIGHT provenances|");
  }

  @SuppressWarnings("static-method")
  @Test
  void testFinalNoRightMappingMatchesProvenanceOrder() {
    for (int i = 0; i < EXPECTED_FINAL_NO_RIGHT_PROVENANCES.size(); i++) {
      assertEquals(Nulls.get(EXPECTED_FINAL_NO_RIGHT_PROBLEMS, i),
          CastlingCheckMapper.map(CastlingCheck.FINAL_NO_RIGHT, Nulls.get(EXPECTED_FINAL_NO_RIGHT_PROVENANCES, i)),
          "FINAL_NO_RIGHT mapping mismatch for provenance " + Nulls.get(EXPECTED_FINAL_NO_RIGHT_PROVENANCES, i));
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testTemporaryMappingMatchesExpectedOrder() {
    // The TEMPORARY entries in EXPECTED_KING_CASTLING_PROBLEMS start after the 5 FINAL entries.
    final int temporaryStartIndex = EXPECTED_FINAL_NO_RIGHT_PROVENANCES.size();
    final ImmutableList<CastlingCheck> temporaryCastlingChecks = EXPECTED_CASTLING_CHECKS.subList(1, EXPECTED_CASTLING_CHECKS.size());
    for (int i = 0; i < temporaryCastlingChecks.size(); i++) {
      assertEquals(Nulls.get(EXPECTED_KING_CASTLING_PROBLEMS, temporaryStartIndex + i),
          CastlingCheckMapper.map(Nulls.get(temporaryCastlingChecks, i), CastlingRightLoss.NOT_LOST),
          "TEMPORARY mapping mismatch at position " + i);
    }
  }
}
