package io.github.dlbbld.ashlarchess.test.san.validate.statically.strict.calculate;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.san.SanParse;

public class SanValidateStaticallyStrict implements EnumConstants {

  private static final ImmutableMap<String, SanParse> SAN_VALIDATION_WHITE_MAP;
  private static final ImmutableMap<String, SanParse> SAN_VALIDATION_BLACK_MAP;

  // white map: 29472 entries, black map: 29472 entries, initialized in 123 ms
  static {
    final Map<String, SanParse> sanValidationAllMap = new TreeMap<>(
        RookSanValidateStaticallyStrictCalculate.calculateSanMap());

    sanValidationAllMap.putAll(KnightSanValidateStaticallyStrictCalculate.calculateSanMap());

    sanValidationAllMap.putAll(BishopSanValidateStaticallyStrictCalculate.calculateSanMap());

    sanValidationAllMap.putAll(QueenSanValidateStaticallyStrictCalculate.calculateSanMap());

    sanValidationAllMap.putAll(KingNonCastlingSanValidateStaticallyStrictCalculate.calculateSanMap());
    sanValidationAllMap.putAll(KingCastlingSanValidateStaticallyStrictCalculate.calculateSanMap());

    final Map<String, SanParse> sanValidationWhiteMap = new TreeMap<>(sanValidationAllMap);
    sanValidationWhiteMap.putAll(PawnSanValidateStaticallyStrictCalculate.calculateSanMap(WHITE));
    SAN_VALIDATION_WHITE_MAP = Nulls.copyOfMap(sanValidationWhiteMap);

    final Map<String, SanParse> sanValidationBlackMap = new TreeMap<>(sanValidationAllMap);
    sanValidationBlackMap.putAll(PawnSanValidateStaticallyStrictCalculate.calculateSanMap(BLACK));
    SAN_VALIDATION_BLACK_MAP = Nulls.copyOfMap(sanValidationBlackMap);
  }

  public static ImmutableMap<String, SanParse> getSanValidationWhiteMap() {
    return SAN_VALIDATION_WHITE_MAP;
  }

  public static ImmutableMap<String, SanParse> getSanValidationBlackMap() {
    return SAN_VALIDATION_BLACK_MAP;
  }

  public static boolean exists(String san, Side side) {
    return switch (side) {
      case WHITE -> SAN_VALIDATION_WHITE_MAP.containsKey(san);
      case BLACK -> SAN_VALIDATION_BLACK_MAP.containsKey(san);
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static SanParse getChecked(String san, Side side) {
    if (!exists(san, side)) {
      throw new IllegalArgumentException("The SAN does not exist");
    }
    return switch (side) {
      case WHITE -> Nulls.get(SAN_VALIDATION_WHITE_MAP, san);
      case BLACK -> Nulls.get(SAN_VALIDATION_BLACK_MAP, san);
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  // for performance reasons
  public static @Nullable SanParse get(String san, Side side) {
    return switch (side) {
      case WHITE -> SAN_VALIDATION_WHITE_MAP.get(san);
      case BLACK -> SAN_VALIDATION_BLACK_MAP.get(san);
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

}
