// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;

public abstract class TagUtility {

  private static final String TAG_PATTERN = "\\[([\\w]+) \"([^\"]*)\"\\]";

  @SuppressWarnings("null")
  public static final ImmutableList<StandardTag> SEVEN_TAG_ROSTER_TAG_LIST = Nulls
      .copyOfList(Arrays.asList(StandardTag.EVENT, StandardTag.SITE, StandardTag.DATE, StandardTag.ROUND,
          StandardTag.WHITE, StandardTag.BLACK, StandardTag.RESULT));

  public static boolean hasEvent(List<Tag> tagList) {
    return existsTagName(tagList, StandardTag.EVENT);
  }

  static String readEvent(List<Tag> tagList) {
    return readTagValue(tagList, StandardTag.EVENT);
  }

  static boolean hasSite(List<Tag> tagList) {
    return existsTagName(tagList, StandardTag.SITE);
  }

  static String readSite(List<Tag> tagList) {
    return readTagValue(tagList, StandardTag.SITE);
  }

  static boolean hasDate(List<Tag> tagList) {
    return existsTagName(tagList, StandardTag.DATE);
  }

  public static String readDate(List<Tag> tagList) {
    return readTagValue(tagList, StandardTag.DATE);
  }

  static boolean hasRound(List<Tag> tagList) {
    return existsTagName(tagList, StandardTag.ROUND);
  }

  static String readRound(List<Tag> tagList) {
    return readTagValue(tagList, StandardTag.ROUND);
  }

  static boolean hasWhite(List<Tag> tagList) {
    return existsTagName(tagList, StandardTag.WHITE);
  }

  static String readWhite(List<Tag> tagList) {
    return readTagValue(tagList, StandardTag.WHITE);
  }

  static boolean hasBlack(List<Tag> tagList) {
    return existsTagName(tagList, StandardTag.BLACK);
  }

  static String readBlack(List<Tag> tagList) {
    return readTagValue(tagList, StandardTag.BLACK);
  }

  public static boolean hasResult(List<Tag> tagList) {
    return existsTagName(tagList, StandardTag.RESULT);
  }

  public static String readResult(List<Tag> tagList) {
    return readTagValue(tagList, StandardTag.RESULT);
  }

  public static boolean hasSetUp(List<Tag> tagList) {
    return existsTagName(tagList, StandardTag.SET_UP);
  }

  public static String readSetUp(List<Tag> tagList) {
    return readTagValue(tagList, StandardTag.SET_UP);
  }

  public static boolean hasFen(List<Tag> tagList) {
    return existsTagName(tagList, StandardTag.FEN);
  }

  public static String readFen(List<Tag> tagList) {
    return readTagValue(tagList, StandardTag.FEN);
  }

  public static ResultTagValue readResultTagValue(List<Tag> tagList) {
    return ResultTagValue.calculate(readResult(tagList));
  }

  public static ResultTagValue readResultTagValue(PgnGame pgnGame) {
    return readResultTagValue(pgnGame.tagList());
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

  private static String readTagValue(List<Tag> tagList, StandardTag tag) {
    return readTagValue(tagList, tag.getName());
  }

  static String readTagValue(List<Tag> tagList, String tagName) {
    if (!existsTagName(tagList, tagName)) {
      throw new IllegalArgumentException(
          "The method can only be used if a tag with the tagName exists, check first with the provided method.");
    }
    for (final Tag tag : tagList) {
      if (tag.name().equals(tagName)) {
        return tag.value();
      }
    }
    throw new ProgrammingMistakeException();
  }

  public static boolean existsTag(List<Tag> tagList, StandardTag tag) {
    return existsTagName(tagList, tag.getName());
  }

  private static boolean existsTagName(List<Tag> tagList, StandardTag standardTag) {
    return existsTagName(tagList, standardTag.getName());
  }

  private static boolean existsTagName(List<Tag> tagList, String tagName) {
    for (final Tag tag : tagList) {
      if (tag.name().equals(tagName)) {
        return true;
      }
    }
    return false;
  }

  public static boolean calculateIsContainsAllSevenTagRosterTags(List<Tag> tagList) {
    for (final StandardTag standardTag : SEVEN_TAG_ROSTER_TAG_LIST) {
      if (!TagUtility.existsTag(tagList, standardTag)) {
        return false;
      }
    }
    return true;
  }

  public static String calculateSevenTagRosterDescription() {
    final List<String> list = new ArrayList<>();
    for (final StandardTag tag : SEVEN_TAG_ROSTER_TAG_LIST) {
      list.add(tag.getName());
    }
    return BasicUtility.calculateCommaSeparatedList(list);
  }

  public static void removeTag(List<Tag> tagList, StandardTag tag) {

    int indexFound = -1;
    int index = -1;
    for (final Tag tagCandidate : tagList) {
      index++;
      if (tagCandidate.name().equals(tag.getName())) {
        indexFound = index;
        break;
      }
    }
    if (indexFound != -1) {
      tagList.remove(indexFound);
    }
  }

  public static void removeSetUpTag(List<Tag> tagList) {
    removeTag(tagList, StandardTag.SET_UP);
  }

  public static void removeFenTag(List<Tag> tagList) {
    removeTag(tagList, StandardTag.FEN);
  }

  public static String calculateTagValue(PgnGame pgnGame, String tagName) {
    if (!existsTagName(pgnGame.tagList(), tagName)) {
      return "NA";
    }
    return readTagValue(pgnGame.tagList(), tagName);
  }

  public static String calculateTagValue(PgnGame pgnGame, StandardTag sevenTagRoster) {
    return calculateTagValue(pgnGame, sevenTagRoster.getName());
  }

}
