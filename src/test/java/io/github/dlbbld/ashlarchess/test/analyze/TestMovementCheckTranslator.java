package io.github.dlbbld.ashlarchess.test.analyze;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.enums.MoveCheck;
import io.github.dlbbld.ashlarchess.enums.MovementCheck;

/**
 * Lock-down test for {@link MovementCheck#toMoveCheck()}. Ensures the translator stays exhaustive over all failure
 * values and that SUCCESS is rejected as a refusal-reason translation.
 */
class TestMovementCheckTranslator {

  @SuppressWarnings("static-method")
  @Test
  void testTranslatorExhaustiveForFailures() {
    for (final MovementCheck check : MovementCheck.values()) {
      if (check == MovementCheck.SUCCESS) {
        continue;
      }
      final MoveCheck translated = check.toMoveCheck();
      assertNotNull(translated, "translator returned null for " + check);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testSuccessThrows() {
    assertThrows(ProgrammingMistakeException.class, MovementCheck.SUCCESS::toMoveCheck);
  }
}
