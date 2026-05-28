package io.github.dlbbld.ashlarchess.test.pgntest.enums;

public enum PgnTestInclusion {

  ALL("Including all test cases"),
  ONLY_LONGEST_POSSIBLE("Including only longest possible cases"),
  ALL_EXCEPT_LONGEST_POSSIBLE("Excluding longest possible cases");

  private final String message;

  PgnTestInclusion(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

}
