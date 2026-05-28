package com.dlb.chess.messages;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ConfigurationConstants;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;

public class Message {

  @SuppressWarnings("null")
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
      .getBundle(Message.class.getPackageName() + ".messages");

  private Message() {
  }

  @SuppressWarnings("null")
  public static String getString(String key) {
    return Nulls.normalizeSpace(RESOURCE_BUNDLE.getString(key));
  }

  public static String getString(String key, String arg1) {
    return getString(key, new String[] { arg1 });
  }

  public static String getString(String key, int arg1) {
    return getString(key, new String[] { String.valueOf(arg1) });
  }

  public static String getString(String key, String arg1, String arg2) {
    return getString(key, new String[] { arg1, arg2 });
  }

  public static String getString(String key, String arg1, String arg2, String arg3) {
    return getString(key, new String[] { arg1, arg2, arg3 });
  }

  public static String getString(String key, String arg1, String arg2, String arg3, String arg4) {
    return getString(key, new String[] { arg1, arg2, arg3, arg4 });
  }

  private static String getString(String key, String[] argArray) {
    final String pattern = RESOURCE_BUNDLE.getString(key);
    final MessageFormat messageWithWildcards = new MessageFormat(pattern, ConfigurationConstants.LOCALE);
    final String messageWithWildcardsSubstituted = messageWithWildcards.format(argArray);
    if (messageWithWildcardsSubstituted == null) {
      throw new ProgrammingMistakeException("Assuming the method never returns null");
    }
    return Nulls.normalizeSpace(messageWithWildcardsSubstituted);
  }

}
