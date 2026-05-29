// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test;

import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTestInclusion;

public abstract class RestrictTestConstants {

  /**
   * Master switch driven by the {@code ashlar-chess.full} system property. Set to {@code true} via Maven profile
   * {@code -Pfull} to run the full heavy test suite. Default: {@code false} - restricted, fast suite for normal
   * iteration. Required to be {@code true} before tagging a release (see {@code specification.md} section 6).
   */
  private static final boolean IS_FULL = Boolean.getBoolean("ashlar-chess.full");

  /**
   * Master gate for all {@code IS_RESTRICT_PGN_*_TEST} flags below. Driven by {@link #IS_FULL}: in full mode the master
   * is {@code false}, all restricted PGN tests run at full coverage. Individual flags that want to opt out of the
   * master gate can be authored as a plain {@code true} instead of {@code true && IS_RESTRICT_PGN}; that keeps the
   * restriction on regardless of the master.
   */
  public static final boolean IS_RESTRICT_PGN = !IS_FULL;

  // PGN tests - iterate over PGN test case lists. Each flag is the conjunction of its own restriction and the
  // master gate above, so the master flips every individual restriction simultaneously.
  public static final boolean IS_RESTRICT_PGN_BOARD_API_AGAINST_EACH_OTHER_TEST = true && IS_RESTRICT_PGN;
  public static final boolean IS_RESTRICT_PGN_FEN_PARSER_ALL_TEST = true && IS_RESTRICT_PGN;
  public static final boolean IS_RESTRICT_PGN_INSUFFICIENT_MATERIAL_TEST = true && IS_RESTRICT_PGN;
  public static final boolean IS_RESTRICT_PGN_LEGAL_MOVE_VALIDATION_AGAINST_BOTTOM_UP_TEST = true && IS_RESTRICT_PGN;
  public static final boolean IS_RESTRICT_PGN_LENIENT_PARSER_API_AGAINST_EACH_OTHER_TEST = true && IS_RESTRICT_PGN;
  // The three flags below toggle between a curated smoke subset (true) and the full ALL_EXCEPT_LONGEST_POSSIBLE
  // corpus (false). Smoke subsets live in PgnExpectedValue.getParserIntegrationSmokeList() and
  // getExportRoundtripSmokeList(). The master flip turns these off along with the rest.
  public static final boolean IS_RESTRICT_PGN_STRICT_AGAINST_LENIENT_TEST = true && IS_RESTRICT_PGN;
  public static final boolean IS_RESTRICT_PGN_EXPORT_IDEMPOTENCY_TEST = true && IS_RESTRICT_PGN;
  public static final boolean IS_RESTRICT_PGN_WRITER_TEST = true && IS_RESTRICT_PGN;
  public static final boolean IS_RESTRICT_PGN_STARTING_POSITION_NONE_AGAINST_INITIAL_FEN_TEST = true && IS_RESTRICT_PGN;
  public static final boolean IS_RESTRICT_PGN_UNWINNABILITY_QUICK_AGAINST_LIMITED_ORACLE_TEST = true && IS_RESTRICT_PGN;
  public static final boolean IS_RESTRICT_PGN_REPORT_TEST = true && IS_RESTRICT_PGN;
  public static final boolean IS_RESTRICT_UNWINNABILITY_QUICK_AGAINST_AMBRONA_ORACLE_TEST = true && IS_RESTRICT_PGN;
  // TestAmbronaSemiStaticOracleComparison: heavy comparison against the ambrona-semistatic.tsv oracle (1249 unique
  // FENs, ~60s). Smoke-mode runs only the first {@code MAX_FENS_IN_SMOKE_MODE} entries (see the test).
  public static final boolean IS_RESTRICT_AMBRONA_SEMISTATIC_ORACLE_COMPARISON_TEST = true && IS_RESTRICT_PGN;

  public static final PgnTestInclusion PGN_TEST_INCLUSION = IS_FULL ? PgnTestInclusion.ALL
      : PgnTestInclusion.ALL_EXCEPT_LONGEST_POSSIBLE;

  // Long running tests - excluded entirely via JUnit assumeFalse. Driven by the master switch.
  public static final boolean IS_EXCLUDE_LONG_RUNNING_SAN_VALIDATE_FORMAT_FAILURE_ORACLE_COMPLEMENT_TEST = !IS_FULL;

  // TestUnwinnabilityFull.testPgnExpected analyzes one heavy PGN (~10 seconds). Only useful
  // when changes touch the unwinnability-full analyzer.
  public static final boolean IS_EXCLUDE_LONG_RUNNING_UNWINNABILITY_FULL_PGN_EXPECTED_TEST = !IS_FULL;

  // TestPgnCorpusNotPlaysBeyondAudit walks the whole corpus (~1300 PGN files) and parses each
  // through the strict parser to confirm none plays beyond a FIDE-automatic termination. Takes
  // a few minutes; only useful when the corpus changes or the strict-pipeline rules change.
  public static final boolean IS_EXCLUDE_LONG_RUNNING_PGN_CORPUS_NOT_PLAYS_BEYOND_AUDIT = !IS_FULL;

}
