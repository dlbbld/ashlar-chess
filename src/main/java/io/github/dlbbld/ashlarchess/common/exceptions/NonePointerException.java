package io.github.dlbbld.ashlarchess.common.exceptions;

public class NonePointerException extends ProgrammingMistakeException {

  private static final String BASE_MESSAGE = "Properties of NONE enums have no meaning and are not supposed to be assessed";

  public NonePointerException() {
    super(BASE_MESSAGE);
  }

}
