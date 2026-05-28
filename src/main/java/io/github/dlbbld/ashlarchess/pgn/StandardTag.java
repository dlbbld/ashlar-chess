// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

public enum StandardTag {

  // PGN specification
  // Seven Tag Roster

  // Event (the name of the tournament or match event)
  EVENT("Event", true, 1),
  // Site (the location of the event)
  SITE("Site", true, 2),
  // Date (the starting date of the game)
  DATE("Date", true, 3),
  // Round (the playing round ordinal of the game)
  ROUND("Round", true, 4),
  // White (the player of the white pieces)
  WHITE("White", true, 5),
  // Black (the player of the black pieces)
  BLACK("Black", true, 6),
  // Result (the result of the game)
  RESULT("Result", true, 7),

  // PGN specification
  // 9: Supplemental tag names

  // 9.1: Player related information
  WHITE_TITLE("WhiteTitle", false, 8),
  BLACK_TITLE("BlackTitle", false, 9),
  WHITE_ELO("WhiteElo", false, 10),
  BLACK_ELO("BlackElo", false, 11),
  WHITE_USCF("WhiteUscf", false, 12),
  BLACK_USCF("BlackUscf", false, 13),
  WHITE_NA("WhiteNa", false, 14),
  BLACK_NA("BlackNa", false, 15),
  WHITE_TYPE("WhiteType", false, 16),
  BLACK_TYPE("BlackType", false, 17),

  // 9.2: Event related information
  EVENT_DATE("EventDate", false, 18),
  EVENT_SPONSOR("EventSponsor", false, 19),
  SECTION("Section", false, 20),
  STAGE("Stage", false, 21),
  BOARD("Board", false, 22),

  // 9.3: Opening information (locale specific)
  OPENING("Opening", false, 23),
  VARIATION("Variation", false, 24),
  SUB_VARIATION("SubVariation", false, 25),

  // 9.4: Opening information (third party vendors)
  ECO("ECO", false, 26),
  NIC("NIC", false, 27),

  // 9.5: Time and date related information
  TIME("Time", false, 28),
  UTC_TIME("UTCTime", false, 29),
  UTC_DATE("UTCDate", false, 30),

  // 9.6: Time control
  TIME_CONTROL("TimeControl", false, 31),

  // 9.7: Alternative starting positions
  SET_UP("SetUp", false, 32),
  FEN("FEN", false, 33),

  // 9.8: Game conclusion
  TERMINATION("Termination", false, 34),

  // 9.9: Miscellaneous
  ANNOTATOR("Annotator", false, 35),
  MODE("Mode", false, 36),
  PLY_COUNT("PlyCount", false, 37);

  private final String name;
  private final boolean isSevenTagRosterTag;
  private final int sortOrder;

  StandardTag(String name, boolean isSevenTagRosterTag, int sortOrder) {
    this.name = name;
    this.isSevenTagRosterTag = isSevenTagRosterTag;
    this.sortOrder = sortOrder;
  }

  public String getName() {
    return name;
  }

  public boolean getIsSevenTagRosterTag() {
    return isSevenTagRosterTag;
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public static boolean exists(String name) {
    for (final StandardTag standardTag : values()) {
      if (standardTag.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  public static StandardTag calculate(String name) {
    if (!exists(name)) {
      throw new IllegalArgumentException("No tag for this name exists");
    }
    for (final StandardTag standardTag : values()) {
      if (standardTag.getName().equals(name)) {
        return standardTag;
      }
    }
    // not possible to come here
    throw new ProgrammingMistakeException();
  }
}
