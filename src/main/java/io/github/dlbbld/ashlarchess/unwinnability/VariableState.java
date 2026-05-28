package io.github.dlbbld.ashlarchess.unwinnability;

enum VariableState {

  ZERO("no"),
  ONE("yes");

  private final String description;

  VariableState(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

}
