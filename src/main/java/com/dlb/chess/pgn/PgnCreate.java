package com.dlb.chess.pgn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.Outcome;
import com.dlb.chess.common.utility.BasicChessUtility;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.enums.MoveSuffixAnnotation;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.PgnHalfMove;

/**
 * PGN serialisation entry points. The library defaults to {@link WriteMode#SEMANTIC} - emits the parse model as-given
 * without inventing content. {@link WriteMode#ARCHIVAL} runs the model through {@link PgnArchivalNormalization} first
 * to produce a PGN spec section 8.1.1-conformant artifact.
 */
public class PgnCreate {

  /** PGN export-format guideline: lines should not exceed 79 characters. */
  public static final int MAX_LINE_LENGTH = 79;

  public static String createPgnString(Board board) {
    return createPgnString(createPgnGame(board));
  }

  public static String createPgnString(PgnGame pgnGame) {
    return createPgnString(pgnGame, WriteMode.SEMANTIC);
  }

  public static String createPgnString(PgnGame pgnGame, WriteMode writeMode) {
    return appendEmptyLine(BasicUtility.convertToString(createPgnLines(pgnGame, writeMode)));
  }

  public static List<String> createPgnLines(PgnGame pgnGame) {
    return createPgnLines(pgnGame, WriteMode.SEMANTIC);
  }

  public static List<String> createPgnLines(PgnGame pgnGame, WriteMode writeMode) {
    final PgnGame effective = writeMode == WriteMode.ARCHIVAL ? PgnArchivalNormalization.apply(pgnGame) : pgnGame;
    return calculateFileLines(effective.tagList(), effective.pregameCommentary(), effective.startFen(),
        effective.halfMoveList(), effective.terminationMarker());
  }

  private static String appendEmptyLine(String text) {
    return text + "\n";
  }

  private static List<String> calculateFileLines(List<Tag> tagList, PgnCommentary pregameCommentary, Fen startFen,
      List<PgnHalfMove> halfMoveList, @Nullable ResultTagValue terminationMarker) {

    final List<String> fileLines = new ArrayList<>();
    for (final Tag tag : tagList) {
      fileLines.add(calculateTagEntry(tag));
    }
    // PGN spec section 8.2.2: a tag section is followed by a single empty line. If there is no tag section
    // (semantic-mode output of a Board with no tags), no separator is emitted; the movetext starts immediately.
    if (!tagList.isEmpty()) {
      fileLines.add("");
    }

    final String moves = calculateMovetextWithoutGameTerminationMarker(startFen.fullMoveNumber(), startFen.havingMove(),
        halfMoveList);

    // PgnCommentary is contract-validated (no `}`, no `\r`), so the value writes verbatim into {...}.
    final String pregameCommentaryValue = pregameCommentary.value();
    final String terminationSuffix = terminationMarker != null ? " " + terminationMarker.getValue() : "";
    final String movetextIncludingPreGameCommentary;
    if (pregameCommentaryValue.isEmpty()) {
      movetextIncludingPreGameCommentary = moves + terminationSuffix;
    } else if (moves.isEmpty()) {
      movetextIncludingPreGameCommentary = "{" + pregameCommentaryValue + "}" + terminationSuffix;
    } else {
      movetextIncludingPreGameCommentary = "{" + pregameCommentaryValue + "}" + " " + moves + terminationSuffix;
    }

    // Lenient parses can produce a PgnGame with no pregame commentary, no half-moves, and no termination marker
    // (a tags-only PGN). The movetext string is then empty; PgnLineWrapper rejects empty input, so skip the
    // wrap call and leave the movetext section blank. The output stays structurally well-formed (tag section,
    // separator, trailing blank) and re-parses cleanly under the lenient parser.
    if (!movetextIncludingPreGameCommentary.isEmpty()) {
      fileLines
          .addAll(PgnLineWrapper.calculateWrappedLines(movetextIncludingPreGameCommentary, PgnCreate.MAX_LINE_LENGTH));
    }
    // Trailing blank line per the strict format.
    fileLines.add("");

    return fileLines;
  }

  private static List<PgnHalfMove> calculatePgnHalfMoveList(List<String> sanList) {
    final List<PgnHalfMove> halfMoveList = new ArrayList<>();

    for (final String san : sanList) {
      PgnHalfMove halfMove;
      halfMove = new PgnHalfMove(san, MoveSuffixAnnotation.NONE, PgnCommentary.EMPTY);
      halfMoveList.add(halfMove);
    }

    return halfMoveList;
  }

  private static ResultTagValue calculateResultTagValue(Board board) {
    final Outcome outcome = BasicChessUtility.calculateOutcome(board);
    switch (outcome.termination()) {
      case NONE:
        // Game is ongoing - including positions with one-sided insufficient material, which is a
        // diagnostic state on the board (queryable via Board.isInsufficientMaterial(Side)) and not
        // an automatic termination.
        return ResultTagValue.ONGOING;
      case CHECKMATE: {
        // Outcome's compact constructor guarantees winner() is WHITE or BLACK for CHECKMATE - the
        // side that delivered mate. Side.NONE is reserved for drawing terminations and cannot
        // appear here by the record's invariant.
        final Side winner = outcome.winner();
        switch (winner) {
          case WHITE:
            return ResultTagValue.WHITE_WON;
          case BLACK:
            return ResultTagValue.BLACK_WON;
          case NONE:
          default:
            throw new ProgrammingMistakeException("Outcome invariant violated: CHECKMATE with winner=" + winner);
        }
      }
      case STALEMATE, INSUFFICIENT_MATERIAL, FIVEFOLD_REPETITION, SEVENTY_FIVE_MOVES:
        return ResultTagValue.DRAW;
      default:
        throw new IllegalArgumentException();
    }
  }

  private static String calculateTagEntry(Tag tag) {
    final StringBuilder result = new StringBuilder();
    result.append("[").append(tag.name()).append(" ");
    result.append("\"").append(escapeTagValue(tag.value())).append("\"");
    result.append("]");
    return Nulls.toString(result);
  }

  /**
   * Inverse of the tokeniser's tag-string unescape (see {@code PgnTokenizer.readTagValueString}). PGN spec section
   * 8.1.2 defines two escapes inside a string token: a backslash represents a literal backslash and a backslash
   * followed by a quote represents a literal quote. Other characters do not require escaping. Order matters - backslash
   * must be doubled before quotes are escaped, otherwise the backslash introduced by quote-escaping would itself be
   * re-escaped.
   */
  private static String escapeTagValue(String value) {
    return Nulls.replace(Nulls.replace(value, "\\", "\\\\"), "\"", "\\\"");
  }

  private static String calculateMovetextWithoutGameTerminationMarker(int fullMoveNumber, Side havingMove,
      List<PgnHalfMove> halfMoveList) {

    final StringBuilder result = new StringBuilder();

      int currentFullMoveNumber = fullMoveNumber;
    Side currentHavingMove = havingMove;
      boolean isFirstMove = true;
    // T-002 / PGN spec section 8.2.2 case 1: commentary on White's move forces "N..." before the next Black move.
      boolean priorCommentaryAttached = false;
    for (final PgnHalfMove halfMove : halfMoveList) {

      // Emit the move-number indicator in the three required cases: first half-move, before any White move, or
      // before a Black move that follows commentary on White's move (T-002).
      if (isFirstMove) {
        isFirstMove = false;
        final String fullMoveNumberPart = HalfMoveUtility.calculateFullMoveNumberInitialWithoutSpace(fullMoveNumber,
            currentHavingMove);
        result.append(fullMoveNumberPart);
      } else if (currentHavingMove == Side.WHITE) {
        result.append(" ").append(currentFullMoveNumber).append('.');
      } else if (priorCommentaryAttached) {
        result.append(" ").append(currentFullMoveNumber).append("...");
      }

      final String san = halfMove.san();
      result.append(" ").append(san);
      if (halfMove.moveSuffixAnnotation() != MoveSuffixAnnotation.NONE) {
        result.append(halfMove.moveSuffixAnnotation().getSuffix());
      }

      final String commentaryValue = halfMove.commentary().value();
      if (!commentaryValue.isEmpty()) {
        result.append(" {").append(commentaryValue).append('}');
        priorCommentaryAttached = true;
      } else {
        priorCommentaryAttached = false;
      }

      if (currentHavingMove == Side.BLACK) {
        currentFullMoveNumber++;
      }
      currentHavingMove = currentHavingMove.getOppositeSide();
    }
    return Nulls.toString(result);
  }

  /**
   * Creates a PgnGame from a Board with a caller-supplied tag list. The tag list is preserved verbatim (no fabrication,
   * no sort). The termination marker is derived from the board's game-status - semantic-mode export will emit it as the
   * movetext trailer; archival-mode export will also synthesise a Result tag from it.
   */
  public static PgnGame createPgnGame(Board board, List<Tag> tagList) {

    final List<PgnHalfMove> halfMoveList = calculatePgnHalfMoveList(board.getSanList());

    return new PgnGame(Nulls.copyOfList(tagList), board.getInitialFen(), PgnCommentary.EMPTY,
        Nulls.copyOfList(halfMoveList), calculateResultTagValue(board));
  }

  /**
   * Creates a PgnGame from a Board with no caller-supplied tags. The tag list is the minimal honest shape: empty when
   * the board started from the initial position, or just SetUp+FEN when from a non-initial position. STR fabrication
   * does not happen here - callers who want a spec section 8.1.1-conformant artifact pass {@link WriteMode#ARCHIVAL} to
   * {@link PgnWriter} or {@link #createPgnString(PgnGame, WriteMode)}.
   */
  public static PgnGame createPgnGame(Board board) {

    final List<Tag> tagList = new ArrayList<>();

    if (board.getInitialFen() != FenConstants.FEN_INITIAL) {
      tagList.add(new Tag(StandardTag.SET_UP.getName(), SetUpTagValue.START_FROM_SETUP_POSITION.getValue()));
      tagList.add(new Tag(StandardTag.FEN.getName(), board.getInitialFen().fen()));
    }

    return createPgnGame(board, tagList);
  }

}
