package com.dlb.chess.test.common.utility;

import java.util.Random;

public abstract class RandomUtility {

  // recommended by SonarLint to reuse
  private static final Random RANDOM = new Random();

  public static int calculateRandomNumber(int minimumInclusive, int maximumInclusive) {
    return calculateRandomNumber(minimumInclusive, maximumInclusive, RANDOM);
  }

  public static int calculateRandomNumber(int minimumInclusive, int maximumInclusive, Random random) {
    if (minimumInclusive > maximumInclusive) {
      throw new IllegalArgumentException();
    }
    final int maximumExclusive = maximumInclusive + 1;
    return random.nextInt(maximumExclusive - minimumInclusive) + minimumInclusive;
  }
}
