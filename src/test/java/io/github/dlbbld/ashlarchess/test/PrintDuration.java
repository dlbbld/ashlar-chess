// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class PrintDuration {

  public static void printDuration(List<Long> milliSecondValues, Logger logger) {
    final int numberOfTests = milliSecondValues.size();
    double totalmilliSeconds = 0D;
    for (final Long milliSecondsTest : milliSecondValues) {
      totalmilliSeconds += milliSecondsTest;
    }

    final double averageMilliSecondsPerTest = totalmilliSeconds / numberOfTests;

    logger.printf(Level.INFO, "Average test duration milliseconds: %f (%d tests)", averageMilliSecondsPerTest,
        numberOfTests);

  }

}
