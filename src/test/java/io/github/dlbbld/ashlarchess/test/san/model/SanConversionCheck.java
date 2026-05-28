// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.model;

import io.github.dlbbld.ashlarchess.san.SanConversion;

public record SanConversionCheck(boolean isMatch, SanConversion sanConversion) {

  public static final SanConversionCheck IS_NO_MATCH = new SanConversionCheck(false, SanConversion.EMPTY);

}
