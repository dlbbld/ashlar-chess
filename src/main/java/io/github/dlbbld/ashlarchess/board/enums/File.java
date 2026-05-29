// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board.enums;

import java.util.EnumMap;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.exceptions.NonePointerException;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

public enum File {
  FILE_A('a', 1, true),
  FILE_B('b', 2, false),
  FILE_C('c', 3, false),
  FILE_D('d', 4, false),
  FILE_E('e', 5, false),
  FILE_F('f', 6, false),
  FILE_G('g', 7, false),
  FILE_H('h', 8, true),
  NONE('\0', 0, false);

  @SuppressWarnings("null")
  public static final ImmutableList<File> REAL = ImmutableList.of(FILE_A, FILE_B, FILE_C, FILE_D, FILE_E, FILE_F,
      FILE_G, FILE_H);

  private final char letter;
  private final String letterString;

  private final int number;
  private final boolean isBorderFile;

  File(char letter, int number, boolean isBorderFile) {
    this.letter = letter;
    this.letterString = Nulls.valueOf(letter);
    this.number = number;
    this.isBorderFile = isBorderFile;
  }

  public char getLetter() {
    check();
    return letter;
  }

  public String getLetterString() {
    check();
    return letterString;
  }

  public int getNumber() {
    check();
    return number;
  }

  public boolean getIsBorderFile() {
    check();
    return isBorderFile;
  }

  public static boolean exists(char letter) {
    for (final File file : values()) {
      if (file == NONE) {
        continue;
      }
      if (file.getLetter() == letter) {
        return true;
      }
    }
    return false;
  }

  public static File calculateFile(char letter) {
    if (!exists(letter)) {
      throw new IllegalArgumentException("For this letter no corresponding non dummy File exists");
    }
    for (final File file : values()) {
      if (file == NONE) {
        continue;
      }
      if (file.getLetter() == letter) {
        return file;
      }
    }
    throw new ProgrammingMistakeException("The code for calculating the file by letter is wrong");
  }

  // ---------------------------------------------------------------------------------------------
  // Single-step file-geometry lookup tables.
  //
  // For each Side, a mapping from each File to its left / right neighbour from that side's
  // perspective. Absent entries mean the source file is on the relevant board edge.
  // ---------------------------------------------------------------------------------------------

  private static EnumMap<Side, EnumMap<File, File>> buildOffsetTable(int offsetForWhite) {
    final EnumMap<Side, EnumMap<File, File>> result = Nulls.newEnumMap(Side.class);
    for (final Side side : Side.REAL) {
      final int offset = side == Side.WHITE ? offsetForWhite : -offsetForWhite;
      final EnumMap<File, File> sideMap = Nulls.newEnumMap(File.class);
      for (final File source : REAL) {
        final int targetNumber = source.getNumber() + offset;
        if (targetNumber >= 1 && targetNumber <= 8) {
          sideMap.put(source, calculateByNumber(targetNumber));
        }
      }
      result.put(side, sideMap);
    }
    return result;
  }

  private static File calculateByNumber(int number) {
    for (final File file : REAL) {
      if (file.getNumber() == number) {
        return file;
      }
    }
    throw new ProgrammingMistakeException("No file for number " + number);
  }

  private static final EnumMap<Side, EnumMap<File, File>> LEFT_FILE = buildOffsetTable(-1);
  private static final EnumMap<Side, EnumMap<File, File>> RIGHT_FILE = buildOffsetTable(1);

  public static boolean calculateHasLeftFile(Side havingMove, File file) {
    if (havingMove == Side.NONE || file == NONE) {
      throw new IllegalArgumentException();
    }
    return Nulls.get(LEFT_FILE, havingMove).containsKey(file);
  }

  public static File calculateLeftFile(Side havingMove, File file) {
    if (havingMove == Side.NONE || file == NONE) {
      throw new IllegalArgumentException();
    }
    final EnumMap<File, File> sideMap = Nulls.get(LEFT_FILE, havingMove);
    if (!sideMap.containsKey(file)) {
      throw new IllegalArgumentException("No left file");
    }
    return Nulls.get(sideMap, file);
  }

  public static boolean calculateHasRightFile(Side havingMove, File file) {
    if (havingMove == Side.NONE || file == NONE) {
      throw new IllegalArgumentException();
    }
    return Nulls.get(RIGHT_FILE, havingMove).containsKey(file);
  }

  public static File calculateRightFile(Side havingMove, File file) {
    if (havingMove == Side.NONE || file == NONE) {
      throw new IllegalArgumentException();
    }
    final EnumMap<File, File> sideMap = Nulls.get(RIGHT_FILE, havingMove);
    if (!sideMap.containsKey(file)) {
      throw new IllegalArgumentException("No right file");
    }
    return Nulls.get(sideMap, file);
  }

  public static boolean calculateHasLeftLeftFile(Side havingMove, File file) {
    if (havingMove == Side.NONE || file == NONE) {
      throw new IllegalArgumentException();
    }
    if (!calculateHasLeftFile(havingMove, file)) {
      return false;
    }
    return calculateHasLeftFile(havingMove, calculateLeftFile(havingMove, file));
  }

  public static boolean calculateHasRightRightFile(Side havingMove, File file) {
    if (havingMove == Side.NONE || file == NONE) {
      throw new IllegalArgumentException();
    }
    if (!calculateHasRightFile(havingMove, file)) {
      return false;
    }
    return calculateHasRightFile(havingMove, calculateRightFile(havingMove, file));
  }

  private void check() {
    if (this == NONE) {
      throw new NonePointerException();
    }
  }
}
