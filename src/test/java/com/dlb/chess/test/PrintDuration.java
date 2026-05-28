package com.dlb.chess.test;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class PrintDuration {

  public static void printDuration(List<Long> milliSecondsList, Logger logger) {
    final int numberOfTests = milliSecondsList.size();
      double totalmilliSeconds = 0D;
    for (final Long milliSecondsTest : milliSecondsList) {
      totalmilliSeconds += milliSecondsTest;
    }

    final double averageMilliSecondsPerTest = totalmilliSeconds / numberOfTests;

    logger.printf(Level.INFO, "Average test duration milliseconds: %f (%d tests)", averageMilliSecondsPerTest,
        numberOfTests);

  }

}
