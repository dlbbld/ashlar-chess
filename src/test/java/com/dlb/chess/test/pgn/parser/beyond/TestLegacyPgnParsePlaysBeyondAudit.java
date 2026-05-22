package com.dlb.chess.test.pgn.parser.beyond;

import static com.dlb.chess.common.enums.GameStatus.DEAD_POSITION_INSUFFICIENT_MATERIAL;
import static com.dlb.chess.common.enums.GameStatus.FIVE_FOLD_REPETITION_RULE;
import static com.dlb.chess.common.enums.GameStatus.SEVENTY_FIVE_MOVE_RULE;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.pgn.StrictPgnParser;
import com.dlb.chess.pgn.StrictPgnParserValidationException;
import com.dlb.chess.pgn.StrictPgnParserValidationProblem;
import com.dlb.chess.san.SanValidationProblem;
import com.dlb.chess.test.ConfigurationTestConstants;
import com.dlb.chess.test.RestrictTestConstants;

/**
 * Verifies that every PGN fixture under {@code pgnParser/legacy/common/beyond/} is rejected by the strict parser
 * exactly as expected. The legacy tree contains real-game and crafted PGNs relocated out of the regular corpus because
 * their recorded halfmove sequence continues past a FIDE-automatic termination, or because their FEN tag's halfmove
 * clock is above the 75-move threshold.
 *
 * <p>
 * For each file, an explicit expected outcome is hardcoded — exception type, validation problem, and where applicable
 * the {@link GameStatus} that ended the game. The classification is derived from filename/folder ("guess per name") and
 * the audit results from {@code TestPgnCorpusNotPlaysBeyondAudit}, then fixed in code so a future change in parser
 * behaviour shows up as a precise mismatch rather than a vague rejection.
 *
 * <p>
 * The legacy tree has 99 fixtures; iteration through the strict parser takes a few seconds. Heavier than per-test
 * methods but a single iterating test keeps the explicit map close to the assertions.
 */
class TestLegacyPgnParsePlaysBeyondAudit {

  private static final Path LEGACY_FOLDER = Nulls.pathResolve(ConfigurationTestConstants.PROJECT_ROOT_FOLDER_PATH,
      "src/test/resources/pgnParser/legacy/common/beyond");

  private record Expected(StrictPgnParserValidationProblem problem, SanValidationProblem sanProblem,
      @Nullable GameStatus gameStatus) {
  }

  private static Expected sanGameEnded(GameStatus gameStatus) {
    return new Expected(StrictPgnParserValidationProblem.SAN, SanValidationProblem.GAME_ALREADY_ENDED, gameStatus);
  }

  private static Expected fenInvalid() {
    return new Expected(StrictPgnParserValidationProblem.TAG_SET_UP_REQUIRES_FEN_TAG_BUT_FEN_INVALID,
        SanValidationProblem.NONE, null);
  }

  private static final Map<String, Expected> EXPECTED = buildExpected();

  @SuppressWarnings("static-method")
  @Test
  void test() {
    assumeFalse(RestrictTestConstants.IS_EXCLUDE_LONG_RUNNING_LEGACY_PGN_PARSE_PLAYS_BEYOND_AUDIT,
        "Long-running legacy parse audit excluded by IS_EXCLUDE_LONG_RUNNING_LEGACY_PGN_PARSE_PLAYS_BEYOND_AUDIT");

    final List<String> failures = new ArrayList<>();
    var totalFiles = 0;

    for (final Map.Entry<String, Expected> entry : Nulls.entrySet(EXPECTED)) {
      totalFiles++;
      final String relativePath = Nulls.getKey(entry);
      final Expected expected = Nulls.getValue(entry);

      final var slash = relativePath.lastIndexOf('/');
      final var subfolder = Nulls.substring(relativePath, 0, slash);
      final var fileName = Nulls.substring(relativePath, slash + 1);
      final var folderPath = Nulls.pathResolve(LEGACY_FOLDER, subfolder);

      try {
        StrictPgnParser.parse(folderPath, fileName);
        failures.add(relativePath + " — expected rejection (" + expected + ") but parsed cleanly");
      } catch (final StrictPgnParserValidationException e) {
        if (e.getStrictPgnParserValidationProblem() != expected.problem
            || e.getSanValidationProblem() != expected.sanProblem || e.getGameStatus() != expected.gameStatus) {
          failures.add(relativePath + " — expected " + expected + ", got problem="
              + e.getStrictPgnParserValidationProblem() + ", sanProblem=" + e.getSanValidationProblem()
              + ", gameStatus=" + e.getGameStatus() + " (" + e.getMessage() + ")");
        }
      }
    }

    if (totalFiles != 99) {
      fail("Expected 99 legacy fixtures in the EXPECTED map, found " + totalFiles);
    }
    if (!failures.isEmpty()) {
      final var report = new StringBuilder().append(failures.size()).append(" of ").append(totalFiles)
          .append(" legacy fixtures did not match expected ").append("rejection:\n");
      for (final String f : failures) {
        report.append("  ").append(f).append('\n');
      }
      fail(report.toString());
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Hardcoded per-file expectations. Built programmatically only to keep the entries compact;
  // every relative path appears as an explicit string literal.
  // ---------------------------------------------------------------------------------------------

  private static Map<String, Expected> buildExpected() {
    final Map<String, Expected> m = new LinkedHashMap<>();

    // ====== basic/fivefold (2) — fivefold continuation ======
    m.put("basic/fivefold/05_fivefold_beyond.pgn", sanGameEnded(FIVE_FOLD_REPETITION_RULE));
    m.put("basic/fivefold/06_fivefold_end_with_first_sixfold.pgn", sanGameEnded(FIVE_FOLD_REPETITION_RULE));

    // ====== basic/fromFen — half_move_clock 150 (FEN at terminal moment, first move rejected) ======
    for (final String prefix : List.of("from_fen_capture_first_move", "from_fen_capture_second_move",
        "from_fen_one_move", "from_fen_pawn_first_move", "from_fen_two_moves", "from_fen_three_moves")) {
      for (final String side : List.of("black", "white")) {
        m.put("basic/fromFen/" + prefix + "_half_move_clock_150_" + side + "_to_move.pgn",
            sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
      }
    }

    // ====== basic/fromFen — half_move_clock 149 (move past triggers 75-move) ======
    for (final String prefix : List.of("from_fen_capture_second_move", "from_fen_two_moves", "from_fen_three_moves")) {
      for (final String side : List.of("black", "white")) {
        m.put("basic/fromFen/" + prefix + "_half_move_clock_149_" + side + "_to_move.pgn",
            sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
      }
    }

    // ====== basic/fromFen — half_move_clock 151 (FEN parse rejection) ======
    for (final String prefix : List.of("from_fen_capture_first_move", "from_fen_capture_second_move",
        "from_fen_one_move", "from_fen_pawn_first_move", "from_fen_two_moves", "from_fen_three_moves")) {
      for (final String side : List.of("black", "white")) {
        m.put("basic/fromFen/" + prefix + "_half_move_clock_151_" + side + "_to_move.pgn", fenInvalid());
      }
    }

    // ====== basic/fromFen — repetition sixfold (8 files) ======
    for (final String moves : List.of("zero_moves", "one_move", "two_moves", "three_moves")) {
      for (final String side : List.of("black", "white")) {
        m.put("basic/fromFen/from_fen_repetition_from_" + moves + "_" + side + "_to_move_sixfold.pgn",
            sanGameEnded(FIVE_FOLD_REPETITION_RULE));
      }
    }

    // ====== basic/fromFenNoProgress (20 files) ======
    // White — 04, 05, 06, 07, 08, 12 cross 75-move via move past; 13-16 are FEN-rejected
    m.put("basic/fromFenNoProgress/white/04_white_from_fen_no_progress_fifty_reoccuring_above_seventy_five.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/white/05_white_from_fen_no_progress_seventy_five_reoccuring_fifty.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/white/06_white_from_fen_no_progress_seventy_five_reoccuring_seventy_five.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/white/07_white_from_fen_no_progress_seventy_five_reoccuring_above_fifty.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/white/08_white_from_fen_no_progress_seventy_five_reoccuring_above_seventy_five.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/white/12_white_from_fen_no_progress_above_fifty_reoccuring_above_seventy_five.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/white/13_white_from_fen_no_progress_above_seventy_five_reoccuring_fifty.pgn",
        fenInvalid());
    m.put("basic/fromFenNoProgress/white/14_white_from_fen_no_progress_above_seventy_five_reoccuring_seventy_five.pgn",
        fenInvalid());
    m.put("basic/fromFenNoProgress/white/15_white_from_fen_no_progress_above_seventy_five_reoccuring_above_fifty.pgn",
        fenInvalid());
    m.put(
        "basic/fromFenNoProgress/white/16_white_from_fen_no_progress_above_seventy_five_reoccuring_above_seventy_five.pgn",
        fenInvalid());

    // Black — 04, 06, 07, 08, 12 cross 75-move via move past; 05 is FEN-rejected (clock 175);
    // 13-16 also FEN-rejected.
    m.put("basic/fromFenNoProgress/black/04_black_from_fen_no_progress_fifty_reoccuring_above_seventy_five.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/black/05_black_from_fen_no_progress_seventy_five_reoccuring_fifty.pgn",
        fenInvalid());
    m.put("basic/fromFenNoProgress/black/06_black_from_fen_no_progress_seventy_five_reoccuring_seventy_five.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/black/07_black_from_fen_no_progress_seventy_five_reoccuring_above_fifty.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/black/08_black_from_fen_no_progress_seventy_five_reoccuring_above_seventy_five.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/black/12_black_from_fen_no_progress_above_fifty_reoccuring_above_seventy_five.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/fromFenNoProgress/black/13_black_from_fen_no_progress_above_seventy_five_reoccuring_fifty.pgn",
        fenInvalid());
    m.put("basic/fromFenNoProgress/black/14_black_from_fen_no_progress_above_seventy_five_reoccuring_seventy_five.pgn",
        fenInvalid());
    m.put("basic/fromFenNoProgress/black/15_black_from_fen_no_progress_above_seventy_five_reoccuring_above_fifty.pgn",
        fenInvalid());
    m.put(
        "basic/fromFenNoProgress/black/16_black_from_fen_no_progress_above_seventy_five_reoccuring_above_seventy_five.pgn",
        fenInvalid());

    // ====== basic/intervening (4) — outer/inner 5-fold + 3-fold interactions, all play past 5-fold ======
    m.put("basic/intervening/02_intervening_threefold_encapsulating_fivefold.pgn",
        sanGameEnded(FIVE_FOLD_REPETITION_RULE));
    m.put("basic/intervening/04_intervening_fivefold_encapsulating_fivefold.pgn",
        sanGameEnded(FIVE_FOLD_REPETITION_RULE));
    m.put("basic/intervening/07_intervening_fivefold_interlocked_threefold.pgn",
        sanGameEnded(FIVE_FOLD_REPETITION_RULE));
    m.put("basic/intervening/08_intervening_fivefold_interlocked_fivefold.pgn",
        sanGameEnded(FIVE_FOLD_REPETITION_RULE));

    // ====== basic/seventyFive (8) — naming says ".._154" or "_beyond_..", all play past 75-move ======
    m.put("basic/seventyFive/03_seventy_five_end_with_half_move_clock_154.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/seventyFive/04_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_99.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/seventyFive/05_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_100.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/seventyFive/06_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_117.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/seventyFive/07_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_149.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/seventyFive/08_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_150.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("basic/seventyFive/09_seventy_five_beyond_half_move_clock_154_end_with_half_move_clock_178.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put(
        "basic/seventyFive/10_seventy_five_beyond_half_move_clock_154_beyond_half_move_clock_178_end_with_half_move_clock_10.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));

    // ====== basic/threefold (1) — name says threefold but actually plays past 5-fold ======
    m.put(
        "basic/threefold/37_threefold_en_passant_capture_situation_capture_allowed_no_exposing_own_king_to_check_mine_end_with_first_threefold.pgn",
        sanGameEnded(FIVE_FOLD_REPETITION_RULE));

    // ====== fivefold/beyond (3) — historical games crossing 5-fold ======
    m.put("fivefold/beyond/fivefold_beyond_savchenko_yu_y2017.pgn", sanGameEnded(FIVE_FOLD_REPETITION_RULE));
    m.put("fivefold/beyond/fivefold_beyond_wang_yu_2017.pgn", sanGameEnded(FIVE_FOLD_REPETITION_RULE));
    m.put("fivefold/beyond/fivefold_beyond_yu_alekseenko_2018.pgn", sanGameEnded(FIVE_FOLD_REPETITION_RULE));

    // ====== lastMoveAddedAccidentally (1) — KvK, last move past dead-position ======
    m.put("lastMoveAddedAccidentally/02_last_move_added_accidentally_result_draw_one_move_in_KvK.pgn",
        sanGameEnded(DEAD_POSITION_INSUFFICIENT_MATERIAL));

    // ====== long (1) — historical long game crossing 75-move ======
    m.put("long/long_nikolic_arsovic_1989.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));

    // ====== longestMate (9) — synthetic mate sequences crossing 75-move ======
    m.put("longestMate/longest_mate_seven_pieces_rank_1.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("longestMate/longest_mate_seven_pieces_rank_2.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("longestMate/longest_mate_seven_pieces_rank_3.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("longestMate/longest_mate_seven_pieces_rank_4.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("longestMate/longest_mate_seven_pieces_rank_5.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("longestMate/longest_mate_seven_pieces_rank_6.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("longestMate/longest_mate_seven_pieces_rank_7.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("longestMate/longest_mate_seven_pieces_rank_8.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));

    // ====== random/noRepetition (4) — long random games crossing 75-move ======
    m.put("random/noRepetition/01_random_no_repetition.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("random/noRepetition/02_random_no_repetition.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("random/noRepetition/03_random_no_repetition.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("random/noRepetition/04_random_no_repetition.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));

    // ====== seventyFive/beyond (5) — historical games crossing 75-move ======
    m.put("seventyFive/beyond/seventy_five_beyond_anton_guijarro_antipov_2015.pgn",
        sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("seventyFive/beyond/seventy_five_beyond_aronian_navara_2017.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("seventyFive/beyond/seventy_five_beyond_cheparinov_jones_2019.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("seventyFive/beyond/seventy_five_beyond_moiseenko_radjabov_2016.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("seventyFive/beyond/seventy_five_beyond_onischuk_guseinov_2014.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));

    // ====== various (2) — historical games crossing 75-move ======
    m.put("various/various_drozdova_tan_2018.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));
    m.put("various/various_kevlishvili_zhigalko_2018.pgn", sanGameEnded(SEVENTY_FIVE_MOVE_RULE));

    // ====== wikipedia/threefold (2) — Wikipedia 3-fold examples that actually play past 5-fold ======
    m.put("wikipedia/threefold/wikipedia_threefold_4_0_1_pest_paris.pgn", sanGameEnded(FIVE_FOLD_REPETITION_RULE));
    m.put("wikipedia/threefold/wikipedia_threefold_4_0_1_pest_paris_six_fold.pgn",
        sanGameEnded(FIVE_FOLD_REPETITION_RULE));

    return Nulls.copyOfMap(m);
  }
}
