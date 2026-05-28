package io.github.dlbbld.ashlarchess.board.enums;

public enum AddSpace {

  YES(" "),
  NO("");

  private final String value;

  AddSpace(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
