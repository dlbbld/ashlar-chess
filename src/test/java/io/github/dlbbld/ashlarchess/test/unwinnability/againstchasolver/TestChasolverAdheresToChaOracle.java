// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.unwinnability.againstchasolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.test.ConfigurationTestConstants;
import io.github.dlbbld.ashlarchess.test.common.utility.FileUtility;
import io.github.dlbbld.ashlarchess.test.common.utility.Loggers;

/**
 * The headline cross-implementation test: does Miguel Ambrona's Rust {@code chasolver} adhere to his C++ {@code cha}
 * (D3-Chess)? Compares the two pre-generated oracles - {@code ambrona-unwinnability.tsv} (C++) and
 * {@code chasolver-unwinnability.tsv} (Rust) - position for position over the identical FEN set, with no ashlar analysis
 * involved.
 *
 * <p>Two invariants are asserted:
 * <ul>
 * <li><b>Adherence:</b> for the complete (full) verdict, whenever both implementations <em>decide</em> a position
 * (WINNABLE or UNWINNABLE), they must agree. A flat WINNABLE-vs-UNWINNABLE contradiction is a real adherence violation
 * and fails the build. (UNDETERMINED on either side is allowed: it only means an implementation hit its own search bound,
 * not that it disagrees.)</li>
 * <li><b>Soundness:</b> within each oracle, a quick {@code UNWINNABLE} (a claimed proof of unwinnability) must be
 * backed by the complete verdict also being {@code UNWINNABLE}. A quick {@code UNWINNABLE} over a position the full
 * search calls WINNABLE would be an unsound fast check.</li>
 * </ul>
 *
 * <p>The remaining, allowed differences (one side resolves a position the other left UNDETERMINED; the two
 * deliberately-incomplete quick pre-filters prove unwinnability on different position types) are logged as a summary,
 * not failed.
 */
class TestChasolverAdheresToChaOracle {
  private static final Logger logger = Loggers.getLogger(TestChasolverAdheresToChaOracle.class);

  private static final Path CHA_ORACLE_PATH = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/oracle/ambrona-unwinnability.tsv");
  private static final Path CHASOLVER_ORACLE_PATH = Nulls
      .pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
          "src/test/resources/oracle/chasolver-unwinnability.tsv");

  private record Verdicts(String fullWhite, String fullBlack, String quickWhite, String quickBlack) {
  }

  @SuppressWarnings("static-method")
  @Test
  void rustChasolverAdheresToCppChaOnEveryDecidedPosition() {
    final Map<String, Verdicts> cha = readOracle(CHA_ORACLE_PATH);
    final Map<String, Verdicts> rust = readOracle(CHASOLVER_ORACLE_PATH);

    assertEquals(cha.keySet(), rust.keySet(), "the cha and chasolver oracles must cover the same FEN set");

    final List<String> contradictions = new ArrayList<>();
    final List<String> soundnessViolations = new ArrayList<>();
    int fullDecidedAgree = 0;
    int rustResolvedWhatChaLeftUndetermined = 0;
    int chaResolvedWhatRustLeftUndetermined = 0;
    int quickGapRustStronger = 0;
    int quickGapChaStronger = 0;

    for (final Map.Entry<String, Verdicts> entry : Nulls.entrySet(cha)) {
      final String fen = Nulls.getKey(entry);
      final Verdicts c = Nulls.getValue(entry);
      final Verdicts r = Nulls.get(rust, fen);

      // Full-verdict adherence, per intended winner.
      final String[] fullKinds = {"fullWhite", "fullBlack"};
      final String[] chaFulls = {c.fullWhite(), c.fullBlack()};
      final String[] rustFulls = {r.fullWhite(), r.fullBlack()};
      for (int i = 0; i < fullKinds.length; i++) {
        final String cf = Nulls.get(chaFulls, i);
        final String rf = Nulls.get(rustFulls, i);
        if (isDecided(cf) && isDecided(rf)) {
          if (cf.equals(rf)) {
            fullDecidedAgree++;
          } else {
            contradictions
                .add(Nulls.get(fullKinds, i) + ": cha=" + cf + " chasolver=" + rf + " fen=" + fen);
          }
        } else if ("UNDETERMINED".equals(cf) && isDecided(rf)) {
          rustResolvedWhatChaLeftUndetermined++;
        } else if (isDecided(cf) && "UNDETERMINED".equals(rf)) {
          chaResolvedWhatRustLeftUndetermined++;
        }
      }

      // Quick-prover gaps, per intended winner (informational, both sound by the soundness check below).
      quickGapRustStronger += rustStrongerQuick(c.quickWhite(), r.quickWhite())
          + rustStrongerQuick(c.quickBlack(), r.quickBlack());
      quickGapChaStronger += chaStrongerQuick(c.quickWhite(), r.quickWhite())
          + chaStrongerQuick(c.quickBlack(), r.quickBlack());

      // Soundness: a quick UNWINNABLE must be backed by a full UNWINNABLE in the same oracle.
      checkQuickSoundness("cha", fen, "White", c.quickWhite(), c.fullWhite(), soundnessViolations);
      checkQuickSoundness("cha", fen, "Black", c.quickBlack(), c.fullBlack(), soundnessViolations);
      checkQuickSoundness("chasolver", fen, "White", r.quickWhite(), r.fullWhite(), soundnessViolations);
      checkQuickSoundness("chasolver", fen, "Black", r.quickBlack(), r.fullBlack(), soundnessViolations);
    }

    logger.info("Compared {} positions (x2 winners).", cha.size());
    logger.info("Full verdicts: {} decided-and-agree; chasolver resolved {} that cha left UNDETERMINED; "
        + "cha resolved {} that chasolver left UNDETERMINED; contradictions: {}.", fullDecidedAgree,
        rustResolvedWhatChaLeftUndetermined, chaResolvedWhatRustLeftUndetermined, contradictions.size());
    logger.info("Quick pre-filter gaps: chasolver-stronger {}, cha-stronger {} (all sound).", quickGapRustStronger,
        quickGapChaStronger);

    assertTrue(contradictions.isEmpty(),
        "Full-mode CONTRADICTIONS between cha (C++) and chasolver (Rust) - a real adherence violation:\n"
            + Nulls.join("\n", contradictions));
    assertTrue(soundnessViolations.isEmpty(),
        "Quick-implies-full soundness violations (a quick UNWINNABLE not backed by a full UNWINNABLE):\n"
            + Nulls.join("\n", soundnessViolations));
  }

  private static int rustStrongerQuick(String chaQuick, String rustQuick) {
    return "POSSIBLY_WINNABLE".equals(chaQuick) && "UNWINNABLE".equals(rustQuick) ? 1 : 0;
  }

  private static int chaStrongerQuick(String chaQuick, String rustQuick) {
    return "UNWINNABLE".equals(chaQuick) && "POSSIBLY_WINNABLE".equals(rustQuick) ? 1 : 0;
  }

  private static void checkQuickSoundness(String oracle, String fen, String side, String quick, String full,
      List<String> violations) {
    if ("UNWINNABLE".equals(quick) && !"UNWINNABLE".equals(full)) {
      violations.add(oracle + " " + side + " quick=UNWINNABLE but full=" + full + " fen=" + fen);
    }
  }

  private static boolean isDecided(String fullVerdict) {
    return "WINNABLE".equals(fullVerdict) || "UNWINNABLE".equals(fullVerdict);
  }

  private static Map<String, Verdicts> readOracle(Path path) {
    final List<String> lines = FileUtility.readFileLines(path);
    if (lines.isEmpty() || !"fen\tfullWhite\tfullBlack\tquickWhite\tquickBlack".equals(Nulls.get(lines, 0))) {
      throw new ProgrammingMistakeException("Unexpected unwinnability oracle header in " + path);
    }
    final Map<String, Verdicts> result = new LinkedHashMap<>();
    for (int i = 1; i < lines.size(); i++) {
      final String[] item = Nulls.split(Nulls.get(lines, i), "\t");
      if (item.length != 5) {
        throw new ProgrammingMistakeException("Invalid unwinnability oracle row in " + path + ": " + Nulls.get(lines, i));
      }
      final String fen = Nulls.get(item, 0);
      if (result.containsKey(fen)) {
        throw new ProgrammingMistakeException("Duplicate oracle FEN in " + path + ": " + fen);
      }
      result.put(fen, new Verdicts(Nulls.get(item, 1), Nulls.get(item, 2), Nulls.get(item, 3), Nulls.get(item, 4)));
    }
    return result;
  }
}
