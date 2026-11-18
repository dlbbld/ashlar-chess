// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.ListUtility;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.ResultTagValue;
import io.github.dlbbld.ashlarchess.pgn.Tag;

public final class TagUtility {

  private TagUtility() {
  }

  private static final String TAG_PATTERN = "\\[([\\w]+) \"([^\"]*)\"\\]";

  /** PGN spec section 7: a symbol token is at most 255 characters long. */
  public static final int MAX_TAG_NAME_LENGTH = 255;
  public static final int MAX_TAG_VALUE_LENGTH = 255;

  @SuppressWarnings("null")
  public static final List<StandardTag> SEVEN_TAG_ROSTER_TAGS = Nulls.copyOfList(Arrays.asList(StandardTag.EVENT,
      StandardTag.SITE, StandardTag.DATE, StandardTag.ROUND, StandardTag.WHITE, StandardTag.BLACK, StandardTag.RESULT));

  public static boolean isTagNameInitialCharacter(int c) {
    return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
  }

  public static boolean isTagNameCharacter(int c) {
    return isTagNameInitialCharacter(c) || c == '_';
  }

  /**
   * The problem the strict parser and archival export report for a tag value, or {@code null} when the value is valid.
   * PGN section 7 limits the decoded string data, excluding delimiters and escape syntax.
   */
  public static @Nullable String tagValueProblem(String value) {
    final int nonPrinting = firstNonPrintingCodePoint(value);
    if (nonPrinting >= 0) {
      return "A tag value contains a non-printing character " + formatCodePoint(nonPrinting) + ".";
    }
    if (tagValueLength(value) > MAX_TAG_VALUE_LENGTH) {
      return "A tag value may contain at most " + MAX_TAG_VALUE_LENGTH + " characters.";
    }
    return null;
  }

  /**
   * The first code point of the value that a PGN string may not contain, or {@code -1} when there is none.
   *
   * <p>
   * Format characters are not among them. They belong to ordinary text - Persian names carry the zero-width non-joiner,
   * emoji sequences the zero-width joiner - so rejecting them would reject legitimate names.
   */
  public static int firstNonPrintingCodePoint(String value) {
    for (int i = 0; i < value.length();) {
      final int cp = value.codePointAt(i);
      final int type = Character.getType(cp);
      if (type == Character.CONTROL || type == Character.LINE_SEPARATOR || type == Character.PARAGRAPH_SEPARATOR
          || type == Character.SURROGATE || type == Character.PRIVATE_USE || PgnCharacters.isNoncharacter(cp)) {
        return cp;
      }
      i += Character.charCount(cp);
    }
    return -1;
  }

  /** The length the limit of {@value #MAX_TAG_VALUE_LENGTH} applies to: decoded code points, not UTF-16 units. */
  public static int tagValueLength(String value) {
    return value.codePointCount(0, value.length());
  }

  public static String formatCodePoint(int codePoint) {
    return Nulls.format("U+%04X", codePoint);
  }

  public static boolean hasEvent(List<Tag> tags) {
    return existsTagName(tags, StandardTag.EVENT);
  }

  static String readEvent(List<Tag> tags) {
    return readTagValue(tags, StandardTag.EVENT);
  }

  static boolean hasSite(List<Tag> tags) {
    return existsTagName(tags, StandardTag.SITE);
  }

  static String readSite(List<Tag> tags) {
    return readTagValue(tags, StandardTag.SITE);
  }

  static boolean hasDate(List<Tag> tags) {
    return existsTagName(tags, StandardTag.DATE);
  }

  public static String readDate(List<Tag> tags) {
    return readTagValue(tags, StandardTag.DATE);
  }

  static boolean hasRound(List<Tag> tags) {
    return existsTagName(tags, StandardTag.ROUND);
  }

  static String readRound(List<Tag> tags) {
    return readTagValue(tags, StandardTag.ROUND);
  }

  static boolean hasWhite(List<Tag> tags) {
    return existsTagName(tags, StandardTag.WHITE);
  }

  static String readWhite(List<Tag> tags) {
    return readTagValue(tags, StandardTag.WHITE);
  }

  static boolean hasBlack(List<Tag> tags) {
    return existsTagName(tags, StandardTag.BLACK);
  }

  static String readBlack(List<Tag> tags) {
    return readTagValue(tags, StandardTag.BLACK);
  }

  public static boolean hasResult(List<Tag> tags) {
    return existsTagName(tags, StandardTag.RESULT);
  }

  public static String readResult(List<Tag> tags) {
    return readTagValue(tags, StandardTag.RESULT);
  }

  public static boolean hasSetUp(List<Tag> tags) {
    return existsTagName(tags, StandardTag.SET_UP);
  }

  public static String readSetUp(List<Tag> tags) {
    return readTagValue(tags, StandardTag.SET_UP);
  }

  public static boolean hasFen(List<Tag> tags) {
    return existsTagName(tags, StandardTag.FEN);
  }

  public static String readFen(List<Tag> tags) {
    return readTagValue(tags, StandardTag.FEN);
  }

  public static ResultTagValue readResultTagValue(List<Tag> tags) {
    return ResultTagValue.parse(readResult(tags));
  }

  public static ResultTagValue readResultTagValue(PgnGame pgnGame) {
    return readResultTagValue(pgnGame.tags());
  }

  static Tag calculateTag(String tagLine) {
    final Pattern pattern = Pattern.compile(TAG_PATTERN);
    final Matcher matcher = pattern.matcher(tagLine);
    // check all occurance
    if (matcher.matches()) {
      @SuppressWarnings("null") @NonNull final String tagName = matcher.group(1);
      @SuppressWarnings("null") @NonNull final String tagValue = matcher.group(2);
      return new Tag(tagName, tagValue);
    }
    throw new ProgrammingMistakeException("Must be validated to be a correct tag at this point");
  }

  public static boolean existsTag(List<Tag> tags, StandardTag tag) {
    return existsTagName(tags, tag.getName());
  }

  /**
   * Returns the name of the first tag whose name occurs more than once, or {@code null} when all names are unique. One
   * pass over the names rather than a comparison of every pair, which is quadratic in the number of tags.
   */
  public static @Nullable String findFirstRepeatedTagName(List<Tag> tags) {
    final Set<String> names = new HashSet<>();
    final Set<String> repeatedNames = new HashSet<>();
    for (final Tag tag : tags) {
      if (!names.add(tag.name())) {
        repeatedNames.add(tag.name());
      }
    }
    if (repeatedNames.isEmpty()) {
      return null;
    }
    for (final Tag tag : tags) {
      if (repeatedNames.contains(tag.name())) {
        return tag.name();
      }
    }
    throw new ProgrammingMistakeException("A repeated tag name belongs to one of the tags");
  }

  private static boolean existsTagName(List<Tag> tags, StandardTag standardTag) {
    return existsTagName(tags, standardTag.getName());
  }

  private static boolean existsTagName(List<Tag> tags, String tagName) {
    for (final Tag tag : tags) {
      if (tag.name().equals(tagName)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasAllSevenTagRosterTags(List<Tag> tags) {
    for (final StandardTag standardTag : SEVEN_TAG_ROSTER_TAGS) {
      if (!TagUtility.existsTag(tags, standardTag)) {
        return false;
      }
    }
    return true;
  }

  public static String calculateSevenTagRosterDescription() {
    final List<String> list = new ArrayList<>();
    for (final StandardTag tag : SEVEN_TAG_ROSTER_TAGS) {
      list.add(tag.getName());
    }
    return ListUtility.toCommaSeparatedString(list);
  }

  public static void removeTag(List<Tag> tags, StandardTag tag) {

    int indexFound = -1;
    int index = -1;
    for (final Tag tagCandidate : tags) {
      index++;
      if (tagCandidate.name().equals(tag.getName())) {
        indexFound = index;
        break;
      }
    }
    if (indexFound != -1) {
      tags.remove(indexFound);
    }
  }

  public static void removeSetUpTag(List<Tag> tags) {
    removeTag(tags, StandardTag.SET_UP);
  }

  public static void removeFenTag(List<Tag> tags) {
    removeTag(tags, StandardTag.FEN);
  }

  private static String readTagValue(List<Tag> tags, StandardTag tag) {
    return readTagValue(tags, tag.getName());
  }

  static String readTagValue(List<Tag> tags, String tagName) {
    if (!existsTagName(tags, tagName)) {
      throw new IllegalArgumentException(
          "The method can only be used if a tag with the tagName exists, check first with the provided method.");
    }
    for (final Tag tag : tags) {
      if (tag.name().equals(tagName)) {
        return tag.value();
      }
    }
    throw new ProgrammingMistakeException();
  }

  public static String readTagValue(PgnGame pgnGame, String tagName) {
    if (!existsTagName(pgnGame.tags(), tagName)) {
      return "NA";
    }
    return readTagValue(pgnGame.tags(), tagName);
  }

  public static String readTagValue(PgnGame pgnGame, StandardTag sevenTagRoster) {
    return readTagValue(pgnGame, sevenTagRoster.getName());
  }

}
