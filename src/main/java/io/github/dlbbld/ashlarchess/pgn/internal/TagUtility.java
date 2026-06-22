// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.pgn.PgnGame;
import io.github.dlbbld.ashlarchess.pgn.ResultTagValue;
import io.github.dlbbld.ashlarchess.pgn.Tag;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.utility.ListUtility;

public final class TagUtility {

  private TagUtility() {
  }

  private static final String TAG_PATTERN = "\\[([\\w]+) \"([^\"]*)\"\\]";

  @SuppressWarnings("null")
  public static final List<StandardTag> SEVEN_TAG_ROSTER_TAGS = Nulls
      .copyOfList(Arrays.asList(StandardTag.EVENT, StandardTag.SITE, StandardTag.DATE, StandardTag.ROUND,
          StandardTag.WHITE, StandardTag.BLACK, StandardTag.RESULT));

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
