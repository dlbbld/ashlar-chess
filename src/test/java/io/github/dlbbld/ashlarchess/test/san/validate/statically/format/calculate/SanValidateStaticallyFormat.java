package io.github.dlbbld.ashlarchess.test.san.validate.statically.format.calculate;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.san.SanParse;

public class SanValidateStaticallyFormat implements EnumConstants {

  private static final ImmutableMap<String, SanParse> SAN_VALIDATE_MAP;

  // map initialized: 133446 entries in 269 ms
  static {
    final Map<String, SanParse> sanValidationAllMap = new TreeMap<>(
        RookSanValidateStaticallyFormatCalculate.calculateSanMap());

    sanValidationAllMap.putAll(KnightSanValidateStaticallyFormatCalculate.calculateSanMap());

    sanValidationAllMap.putAll(BishopSanValidateStaticallyFormatCalculate.calculateSanMap());

    sanValidationAllMap.putAll(QueenSanValidateStaticallyFormatCalculate.calculateSanMap());

    sanValidationAllMap.putAll(KingNonCastlingSanValidateStaticallyFormatCalculate.calculateSanMap());
    sanValidationAllMap.putAll(KingCastlingSanValidateStaticallyFormatCalculate.calculateSanMap());

    sanValidationAllMap.putAll(PawnSanValidateStaticallyFormatCalculate.calculateSanMap());

    SAN_VALIDATE_MAP = Nulls.copyOfMap(sanValidationAllMap);
  }

  public static boolean exists(String san) {
    return SAN_VALIDATE_MAP.containsKey(san);
  }

  public static SanParse calculate(String san) {
    if (!exists(san)) {
      throw new IllegalArgumentException("The SAN does not exist");
    }
    return Nulls.get(SAN_VALIDATE_MAP, san);
  }

  // for performance reasons
  public static @Nullable SanParse get(String san) {
    return SAN_VALIDATE_MAP.get(san);
  }

  public static ImmutableMap<String, SanParse> getSanValidationMap() {
    return SAN_VALIDATE_MAP;
  }

}
