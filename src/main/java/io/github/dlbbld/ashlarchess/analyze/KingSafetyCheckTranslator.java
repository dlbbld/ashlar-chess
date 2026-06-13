// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.analyze;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.enums.KingSafetyCheck;
import io.github.dlbbld.ashlarchess.enums.MoveCheck;

/**
 * Translates a {@link KingSafetyCheck} failure into the broader {@link MoveCheck} vocabulary, used when a king-safety
 * refusal is surfaced through {@code InvalidMoveException} for MoveCheck public-API stability.
 *
 * <p>
 * The switch is exhaustive (no {@code default:}) so any new value added to {@code KingSafetyCheck} causes a compile
 * error here.
 */
public abstract class KingSafetyCheckTranslator {

  public static MoveCheck toMoveCheck(KingSafetyCheck kingSafetyCheck) {
    return switch (kingSafetyCheck) {
      case NON_KING_LEFT_IN_CHECK -> MoveCheck.ALL_BUT_KING_KING_LEFT_IN_CHECK;
      case NON_KING_EXPOSED_TO_CHECK -> MoveCheck.ALL_BUT_KING_KING_EXPOSED_TO_CHECK;
      case SUCCESS -> throw new ProgrammingMistakeException("SUCCESS is not a king-safety failure");
    };
  }

}
