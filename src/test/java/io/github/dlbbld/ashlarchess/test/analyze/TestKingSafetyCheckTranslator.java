// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.analyze;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.enums.KingSafetyCheck;
import io.github.dlbbld.ashlarchess.enums.MoveCheck;

/**
 * Lock-down test for {@link KingSafetyCheck#toMoveCheck()}. Ensures the translator stays exhaustive over all failure
 * values and that SUCCESS is rejected as a refusal-reason translation.
 */
class TestKingSafetyCheckTranslator {

  @SuppressWarnings("static-method")
  @Test
  void testTranslatorExhaustiveForFailures() {
    for (final KingSafetyCheck check : KingSafetyCheck.values()) {
      if (check == KingSafetyCheck.SUCCESS) {
        continue;
      }
      final MoveCheck translated = check.toMoveCheck();
      assertNotNull(translated, "translator returned null for " + check);
    }
  }

  @SuppressWarnings("static-method")
  @Test
  void testSuccessThrows() {
    assertThrows(ProgrammingMistakeException.class, KingSafetyCheck.SUCCESS::toMoveCheck);
  }
}
