package com.dlb.chess.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.common.constants.ChessConstants;
import com.dlb.chess.pgn.PgnUtility;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

/**
 * Cross-checks that the printed report sections are <em>derivable</em> from the report objects — line counts, asterisk
 * counts, and the "None" sentinel correspond to the {@link ThreefoldClaimAheadReport} and {@link ThreefoldExistingReport}
 * contents. The point is to catch silent drift between the analysis layer (builders, records) and the presentation
 * layer (print classes); a regression in either side would break the correspondence the formatter relies on.
 *
 * <p>
 * Sister test to {@code TestReporterGoldenOutput}, which pins exact byte content. This test pins structural
 * correspondence — a formatting wording change that preserves structure will not break this test, but a logic change
 * that drops or adds entries silently will.
 */
class TestReportPrintoutDerivesFromObjectModel {

  private static final String CLAIM_AHEAD_HEADER_PREFIX = "Valid threefold claims ahead";
  private static final String EXISTING_HEADER_PREFIX = "Threefolds and beyond";
  private static final String FIFTY_CLAIM_AHEAD_HEADER_PREFIX = "Valid fifty-move claims ahead";
  private static final String FIFTY_SEQUENCE_HEADER_PREFIX = "Fifty moves without capture";
  private static final String NONE_SENTINEL = "None";

  @SuppressWarnings("static-method")
  @Test
  void shortGameWithNoActivityHasBothSectionsEmpty() {
    final Board board = new Board();
    board.movesStrict("e4", "e5", "Nf3", "Nf6");

    checkCorrespondence(board);
  }

  @SuppressWarnings("static-method")
  @Test
  void threefoldReachedOnInitialPosition() {
    // 8-ply knight shuffle: initial-position threefold on the board. Claim-ahead detected at the
    // prior ply -> hasBeenPlayed == true -> asterisk in the output.
    final Board board = new Board();
    board.movesStrict("Nf3", "Nf6", "Ng1", "Ng8", "Nf3", "Nf6", "Ng1", "Ng8");

    checkCorrespondence(board);
  }

  @SuppressWarnings("static-method")
  @Test
  void twoThreefoldsBeyondFixture() {
    // Realistic multi-group fixture from the corpus. Exercises both sections heavily.
    final Board board = loadCorpusBoard("18_threefold_two_threefolds_beyond.pgn");

    checkCorrespondence(board);
  }

  // --- helpers ---

  /**
   * For the given board, builds the four report records and the captured Reporter output, then asserts every structural
   * invariant the print layer is expected to preserve.
   */
  private static void checkCorrespondence(Board board) {
    final ThreefoldClaimAheadReport claimAhead = ThreefoldClaimAheadReportBuilder.build(board);
    final ThreefoldExistingReport existing = ThreefoldExistingReportBuilder.build(board.getInitialDynamicPosition(),
        board.getHalfMoveList(), ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD);
    final FiftyMoveClaimAheadReport fiftyClaimAhead = FiftyMoveClaimAheadReportBuilder.build(board);
    final FiftyMoveSequenceReport fiftySequence = FiftyMoveSequenceReportBuilder.build(board);

    final List<String> outputLines = captureReporter(board);

    final List<String> claimAheadSection = extractSection(outputLines, CLAIM_AHEAD_HEADER_PREFIX,
        EXISTING_HEADER_PREFIX);
    final List<String> existingSection = extractSection(outputLines, EXISTING_HEADER_PREFIX,
        FIFTY_CLAIM_AHEAD_HEADER_PREFIX);
    final List<String> fiftyClaimAheadSection = extractSection(outputLines, FIFTY_CLAIM_AHEAD_HEADER_PREFIX,
        FIFTY_SEQUENCE_HEADER_PREFIX);
    final List<String> fiftySequenceSection = extractSectionToEnd(outputLines, FIFTY_SEQUENCE_HEADER_PREFIX);

    // --- claim-ahead section ---
    if (claimAhead.entries().isEmpty()) {
      assertEquals(1, claimAheadSection.size(), "empty claim-ahead must render exactly one 'None' line");
      assertEquals(NONE_SENTINEL, claimAheadSection.get(0));
    } else {
      assertEquals(claimAhead.entries().size(), claimAheadSection.size(),
          "claim-ahead section must have one rendered line per ClaimAheadEntry");
      final long asterisks = claimAheadSection.stream().filter(line -> line.contains("*")).count();
      final long expectedAsterisks = claimAhead.entries().stream().filter(ClaimAheadEntry::hasBeenPlayed).count();
      assertEquals(expectedAsterisks, asterisks,
          "asterisk count in printed claim-ahead lines must equal count of hasBeenPlayed entries");
    }

    // --- existing-threefold section ---
    if (existing.groups().isEmpty()) {
      assertEquals(1, existingSection.size(), "empty existing-threefold section must render exactly one 'None' line");
      assertEquals(NONE_SENTINEL, existingSection.get(0));
    } else {
      assertEquals(existing.groups().size(), existingSection.size(),
          "existing section must have one rendered line per RepetitionGroup");
    }

    // --- fifty-move claim-ahead section ---
    if (fiftyClaimAhead.entries().isEmpty()) {
      assertEquals(1, fiftyClaimAheadSection.size(),
          "empty fifty-move claim-ahead must render exactly one 'None' line");
      assertEquals(NONE_SENTINEL, fiftyClaimAheadSection.get(0));
    } else {
      assertEquals(fiftyClaimAhead.entries().size(), fiftyClaimAheadSection.size(),
          "fifty-move claim-ahead section must have one rendered line per FiftyMoveClaimAheadEntry");
      final long asterisks = fiftyClaimAheadSection.stream().filter(line -> line.contains("*")).count();
      final long expectedAsterisks = fiftyClaimAhead.entries().stream().filter(FiftyMoveClaimAheadEntry::hasBeenPlayed)
          .count();
      assertEquals(expectedAsterisks, asterisks,
          "asterisk count in printed fifty-move claim-ahead lines must equal count of hasBeenPlayed entries");
    }

    // --- fifty-move sequence section ---
    if (fiftySequence.sequences().isEmpty()) {
      assertEquals(1, fiftySequenceSection.size(),
          "empty fifty-move sequence section must render exactly one 'None' line");
      assertEquals(NONE_SENTINEL, fiftySequenceSection.get(0));
    } else {
      assertEquals(fiftySequence.sequences().size(), fiftySequenceSection.size(),
          "fifty-move sequence section must have one rendered line per FiftyMoveSequence");
    }
  }

  /** Captures System.out for one {@code Reporter.printReport(board)} invocation as a list of trimmed lines. */
  private static List<String> captureReporter(Board board) {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final PrintStream original = System.out;
    try (PrintStream captured = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
      System.setOut(captured);
      Reporter.printReport(board);
    } finally {
      System.setOut(original);
    }
    final String text = buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    final List<String> lines = new ArrayList<>();
    for (final String line : text.split("\n", -1)) {
      lines.add(line);
    }
    return lines;
  }

  /**
   * Returns the non-blank content lines of the section starting at the line whose content starts with
   * {@code sectionHeaderPrefix} (exclusive of the header itself) and ending immediately before the next section's
   * header (or the end of input, whichever comes first).
   */
  private static List<String> extractSection(List<String> lines, String sectionHeaderPrefix,
      String nextSectionHeaderPrefix) {
    var inSection = false;
    final List<String> contents = new ArrayList<>();
    for (final String raw : lines) {
      final String line = raw.trim();
      if (!inSection && line.startsWith(sectionHeaderPrefix)) {
        inSection = true;
        continue;
      }
      if (inSection) {
        if (line.startsWith(nextSectionHeaderPrefix)) {
          break;
        }
        if (line.isEmpty()) {
          continue;
        }
        contents.add(line);
      }
    }
    assertTrue(inSection, "section header '" + sectionHeaderPrefix + "' must appear in the captured output");
    return contents;
  }

  /**
   * Like {@link #extractSection} but for the final section — runs to the end of input rather than to a successor
   * header.
   */
  private static List<String> extractSectionToEnd(List<String> lines, String sectionHeaderPrefix) {
    var inSection = false;
    final List<String> contents = new ArrayList<>();
    for (final String raw : lines) {
      final String line = raw.trim();
      if (!inSection && line.startsWith(sectionHeaderPrefix)) {
        inSection = true;
        continue;
      }
      if (inSection) {
        if (line.isEmpty()) {
          continue;
        }
        contents.add(line);
      }
    }
    assertTrue(inSection, "section header '" + sectionHeaderPrefix + "' must appear in the captured output");
    return contents;
  }

  private static Board loadCorpusBoard(String pgnName) {
    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    return PgnUtility.calculateBoard(pgnTest.getFolderPath(), pgnName);
  }
}
