package io.github.dlbbld.ashlarchess.test.san.model;

import io.github.dlbbld.ashlarchess.san.SanConversion;

public record SanConversionCheck(boolean isMatch, SanConversion sanConversion) {

  public static final SanConversionCheck IS_NO_MATCH = new SanConversionCheck(false, SanConversion.EMPTY);

}
