package io.github.dlbbld.ashlarchess.test.san.reference;

import io.github.dlbbld.ashlarchess.san.SanValidateFormat;

/**
 * Test-only coarse sanity filter over SAN strings - a superset of the structurally valid SAN language that lives in
 * test sources because it is not part of the main parse pipeline.
 *
 * <p>
 * Used by the failure-oracle complement test to verify that any SAN rejected by this cheap upfront filter is also
 * rejected by {@link SanValidateFormat#validateFormat}. Has its own dedicated unit test
 * ({@code TestSanValidateFormatBasic}) because other tests depend on its correctness as an oracle.
 */
public abstract class SanValidateFormatBasic {

  /**
   * Returns {@code true} iff {@code san} passes the coarse character-class and length checks.
   */
  public static boolean isBasicFormatValid(String san) {
    if (san.isBlank() || san.length() > 7) {
      return false;
    }

    int countX = 0;
    int countEquals = 0;
    int equalsIndex = -1;
    int countCheckOrCheckmate = 0;
    int countK = 0;
    int countRnbq = 0;
    int countDigits = 0;
    int countFiles = 0;
    for (int i = 0; i < san.length(); i++) {
      switch (san.charAt(i)) {
        case 'x' -> countX++;
        case '=' -> {
          countEquals++;
          equalsIndex = i;
        }
        case '+', '#' -> countCheckOrCheckmate++;
        case 'K' -> countK++;
        case 'R', 'N', 'B', 'Q' -> countRnbq++;
        case '1', '2', '3', '4', '5', '6', '7', '8' -> countDigits++;
        case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' -> countFiles++;
        case 'O', '-' -> {
          // castling characters, counted implicitly elsewhere
        }
        default -> {
          return false;
        }
      }
    }
    if (countX > 1 || countEquals > 1 || countCheckOrCheckmate > 1 || countK > 1 || countRnbq > 1 || countDigits > 2
        || countFiles > 2) {
      return false;
    }
    if (countEquals == 1 && equalsIndex != san.length() - 2 && equalsIndex != san.length() - 3) {
      return false;
    }
    return true;
  }

}
