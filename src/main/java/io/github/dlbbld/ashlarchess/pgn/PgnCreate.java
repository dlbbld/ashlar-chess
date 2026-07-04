// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.Outcome;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.internal.MoveNumberFormat;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.fen.internal.FenConstants;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.internal.ListUtility;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.internal.StandardTag;

/**
 * PGN serialisation entry points. The library defaults to {@link WriteMode#SEMANTIC} - emits the parse model as-given
 * without inventing content. {@link WriteMode#ARCHIVAL} runs the model through {@link PgnArchivalNormalization} first
 * to produce a PGN spec section 8.1.1-conformant artifact.
 */
public final class PgnCreate {

  private PgnCreate() {
  }

  /** PGN export-format guideline: lines should not exceed 79 characters. */
  public static final int MAX_LINE_LENGTH = 79;

  public static String toPgnString(Board board) {
    return toPgnString(createPgnGame(board));
  }

  public static String toPgnString(PgnGame pgnGame) {
    return toPgnString(pgnGame, WriteMode.SEMANTIC);
  }

  public static String toPgnString(PgnGame pgnGame, WriteMode writeMode) {
    return appendEmptyLine(ListUtility.toLineSeparatedString(toPgnLines(pgnGame, writeMode)));
  }

  public static List<String> toPgnLines(PgnGame pgnGame) {
    return toPgnLines(pgnGame, WriteMode.SEMANTIC);
  }

  public static List<String> toPgnLines(PgnGame pgnGame, WriteMode writeMode) {
    final PgnGame effective = writeMode == WriteMode.ARCHIVAL ? PgnArchivalNormalization.apply(pgnGame) : pgnGame;
    return Nulls.copyOfList(calculateFileLines(effective.tags(), effective.pregameCommentary(), effective.startFen(),
        effective.moves(), effective.terminationMarker(), writeMode));
  }

  private static String appendEmptyLine(String text) {
    return text + "\n";
  }

  private static List<String> calculateFileLines(List<Tag> tags, PgnCommentary pregameCommentary, Fen startFen,
      List<PgnMove> moves, @Nullable ResultTagValue terminationMarker, WriteMode writeMode) {

    final List<String> fileLines = new ArrayList<>();
    for (final Tag tag : tags) {
      fileLines.add(calculateTagEntry(tag));
    }
    // PGN spec section 8.2.2: a tag section is followed by a single empty line. If there is no tag section
    // (semantic-mode output of a Board with no tags), no separator is emitted; the movetext starts immediately.
    if (!tags.isEmpty()) {
      fileLines.add("");
    }

    final String movetext = calculateMovetextWithoutGameTerminationMarker(startFen.fullMoveNumber(),
        startFen.sideToMove(), moves, writeMode);

    // PgnCommentary is contract-validated (no `}`, no `\r`), so the value writes verbatim into {...}.
    final String pregameCommentaryValue = pregameCommentary.value();
    final String terminationSuffix = terminationMarker != null ? " " + terminationMarker.getValue() : "";
    final String movetextIncludingPreGameCommentary;
    if (pregameCommentaryValue.isEmpty()) {
      movetextIncludingPreGameCommentary = movetext + terminationSuffix;
    } else if (movetext.isEmpty()) {
      movetextIncludingPreGameCommentary = "{" + pregameCommentaryValue + "}" + terminationSuffix;
    } else {
      movetextIncludingPreGameCommentary = "{" + pregameCommentaryValue + "}" + " " + movetext + terminationSuffix;
    }

    // Lenient parses can produce a PgnGame with no pregame commentary, no moves, and no termination marker
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

  private static List<PgnMove> calculatePgnMoves(List<String> sans) {
    final List<PgnMove> moves = new ArrayList<>();

    for (final String san : sans) {
      PgnMove move;
      move = new PgnMove(san, List.of(), PgnCommentary.EMPTY);
      moves.add(move);
    }

    return moves;
  }

  private static ResultTagValue calculateResultTagValue(Board board) {
    final Outcome outcome = board.outcome();
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

  private static String calculateMovetextWithoutGameTerminationMarker(int fullMoveNumber, Side sideToMove,
      List<PgnMove> moves, WriteMode writeMode) {

    final StringBuilder result = new StringBuilder();

    int currentFullMoveNumber = fullMoveNumber;
    Side currentSideToMove = sideToMove;
    boolean isFirstMove = true;
    // T-002 / PGN spec section 8.2.2 case 1: commentary on White's move forces "N..." before the next Black move.
    boolean priorCommentaryAttached = false;
    for (final PgnMove move : moves) {

      // Emit the move-number indicator in the three required cases: first move, before any White move, or
      // before a Black move that follows commentary on White's move (T-002).
      if (isFirstMove) {
        isFirstMove = false;
        final String fullMoveNumberPart = MoveNumberFormat.calculateFullMoveNumberInitialWithoutSpace(fullMoveNumber,
            currentSideToMove);
        result.append(fullMoveNumberPart);
      } else if (currentSideToMove == Side.WHITE) {
        result.append(" ").append(currentFullMoveNumber).append('.');
      } else if (priorCommentaryAttached) {
        result.append(" ").append(currentFullMoveNumber).append("...");
      }

      final String san = move.san();
      result.append(" ").append(san);
      appendNags(result, move.nags(), writeMode);

      final String commentaryValue = move.commentary().value();
      if (!commentaryValue.isEmpty()) {
        result.append(" {").append(commentaryValue).append('}');
        priorCommentaryAttached = true;
      } else {
        priorCommentaryAttached = false;
      }

      if (currentSideToMove == Side.BLACK) {
        currentFullMoveNumber++;
      }
      currentSideToMove = currentSideToMove.getOppositeSide();
    }
    return Nulls.toString(result);
  }

  /**
   * Renders a move's NAGs, in one of two forms.
   *
   * <p>
   * <b>Archival</b> ({@link WriteMode#ARCHIVAL}) is the canonical, PGN export-format conformant form (spec section
   * 8.2.3.8, which defines the suffix glyphs as import-only shorthand for NAGs): the NAGs are deduplicated and sorted
   * ascending, and <em>every</em> one is written as a spaced {@code $N} token, including the assessment codes
   * {@code 1..6}. So {@code e4?} exports as {@code e4 $2}, and a move carrying {@code $2 $1 $1} exports as
   * {@code $1 $2}. This matches python-chess (which holds a move's NAGs in a set), so archival output agrees with it
   * byte-for-byte.
   *
   * <p>
   * <b>Semantic</b> (the default) favours human readability and honest preservation over canonical form: the source
   * order and duplicates are kept, the <em>first</em> assessment NAG (code {@code 1..6}) is written as its glyph
   * attached to the SAN ({@code e4?}) and every other NAG - a second assessment code, {@code $0}, or any positional
   * code - as a spaced {@code $N}. At most one glyph is attached so the output stays unambiguous (two fused glyphs,
   * e.g. {@code ?}+{@code !}, would read as the single glyph {@code ?!}).
   */
  private static void appendNags(StringBuilder result, List<Nag> nags, WriteMode writeMode) {
    if (writeMode == WriteMode.ARCHIVAL) {
      for (final int code : sortedDistinctNagCodes(nags)) {
        result.append(" $").append(code);
      }
      return;
    }
    int glyphIndex = -1;
    for (int i = 0; i < nags.size(); i++) {
      final MoveSuffixAnnotation glyph = MoveSuffixAnnotation.fromNagCode(Nulls.get(nags, i).code());
      if (glyph != null) {
        result.append(glyph.getSuffix());
        glyphIndex = i;
        break;
      }
    }
    for (int i = 0; i < nags.size(); i++) {
      if (i != glyphIndex) {
        result.append(" ").append(Nulls.get(nags, i).toToken());
      }
    }
  }

  /** The move's NAG codes, deduplicated and sorted ascending - the canonical (archival) ordering. */
  private static List<Integer> sortedDistinctNagCodes(List<Nag> nags) {
    final List<Integer> codes = new ArrayList<>();
    for (final Nag nag : nags) {
      if (!codes.contains(nag.code())) {
        codes.add(nag.code());
      }
    }
    codes.sort(null);
    return codes;
  }

  /**
   * Creates a PgnGame from a Board with a caller-supplied tag list. The tag list is preserved verbatim (no fabrication,
   * no sort). The termination marker is derived from the board's game-status - semantic-mode export will emit it as the
   * movetext trailer; archival-mode export will also synthesise a Result tag from it.
   */
  public static PgnGame createPgnGame(Board board, List<Tag> tags) {

    final List<PgnMove> moves = calculatePgnMoves(board.getPerformedMovesAsSan());

    return new PgnGame(Nulls.copyOfList(tags), board.getInitialFen(), PgnCommentary.EMPTY, Nulls.copyOfList(moves),
        calculateResultTagValue(board));
  }

  /**
   * Creates a PgnGame from a Board with no caller-supplied tags. The tag list is the minimal honest shape: empty when
   * the board started from the initial position, or just SetUp+FEN when from a non-initial position. STR fabrication
   * does not happen here - callers who want a spec section 8.1.1-conformant artifact pass {@link WriteMode#ARCHIVAL} to
   * {@link PgnWriter} or {@link #toPgnString(PgnGame, WriteMode)}.
   */
  public static PgnGame createPgnGame(Board board) {

    final List<Tag> tags = new ArrayList<>();

    if (board.getInitialFen() != FenConstants.FEN_INITIAL) {
      tags.add(new Tag(StandardTag.SET_UP.getName(), SetUpTagValue.START_FROM_SETUP_POSITION.getValue()));
      tags.add(new Tag(StandardTag.FEN.getName(), board.getInitialFen().fen()));
    }

    return createPgnGame(board, tags);
  }

}
