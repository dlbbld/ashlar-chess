package com.dlb.chess.pgn;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.HalfMoveUtility;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.PgnCommentaryValidationException;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.enums.MoveSuffixAnnotation;
import com.dlb.chess.fen.FenParserAdvanced;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.san.SanValidationException;
import com.dlb.chess.san.SanValidationProblem;

/**
 * Strict PGN parser. Single forward pass over the token stream; validation and semantic construction happen together.
 * Structural violations throw {@link StrictPgnParserValidationException} with a fine-grained
 * {@link StrictPgnParserValidationProblem} category.
 */
public final class StrictPgnParser {

  private static final int MAX_TAG_NAME_LENGTH = 255;
  private static final int SAN_MIN_LENGTH = 2;
  private static final int SAN_MAX_LENGTH = 7;

  private final String source;
  private final PgnTokenizer tokenizer;

  private StrictPgnParser(String source) {
    this.source = NewlineNormalization.toLf(source);
    this.tokenizer = new PgnTokenizer(new PgnCharStream(this.source));
  }

  // -------------------------------------------------------------------------------------------------
  // Public entry points
  // -------------------------------------------------------------------------------------------------

  public static PgnGame parseText(String pgn) {
    return new StrictPgnParser(pgn).parseInternal();
  }

  public static PgnGame parse(Path pgnPath) {
    // Read raw bytes - line-based reconstruction would hide whether the source's trailing newline is actually
    // present.
    return parseText(PgnReader.readPgn(pgnPath));
  }

  public static PgnGame parse(Path pgnFolderPath, String pgnName) {
    return parse(Nulls.pathResolve(pgnFolderPath, pgnName));
  }

  public static PgnGame parse(String pgnPath) {
    return parse(Nulls.pathOf(pgnPath));
  }

  /** Parses lines produced by a line-based reader (each entry is one line without its terminator). */
  public static PgnGame parse(List<String> fileLines) {
    return parseText(joinLines(fileLines));
  }

  private static String joinLines(List<String> lines) {
    final StringBuilder builder = new StringBuilder();
    for (final String line : lines) {
      builder.append(line).append('\n');
    }
    return Nulls.toString(builder);
  }

  public static StrictPgnParserValidationResult validate(Path pgnFolderPath, String pgnName) {
    return validate(Nulls.pathResolve(pgnFolderPath, pgnName));
  }

  public static StrictPgnParserValidationResult validate(String pgnPath) {
    return validate(Nulls.pathOf(pgnPath));
  }

  public static StrictPgnParserValidationResult validate(Path pgnPath) {
    try {
      parse(pgnPath);
      return new StrictPgnParserValidationResult(StrictPgnParserValidationProblem.OK, SanValidationProblem.NONE, "OK");
    } catch (final StrictPgnParserValidationException e) {
      final String message = BasicUtility.getMessage(e);
      return new StrictPgnParserValidationResult(e.getStrictPgnParserValidationProblem(), e.getSanValidationProblem(),
          message);
    } catch (final RuntimeException e) {
      final String message = unexpectedValidationErrorMessage(e);
      return new StrictPgnParserValidationResult(StrictPgnParserValidationProblem.UNKNOWN_ERROR,
          SanValidationProblem.UNKNOWN_ERROR, message);
    }
  }

  /**
   * Like {@link #parseText(String)} but returns a structured result instead of throwing.
   */
  public static StrictPgnParserValidationResult validateText(String pgn) {
    try {
      parseText(pgn);
      return new StrictPgnParserValidationResult(StrictPgnParserValidationProblem.OK, SanValidationProblem.NONE, "OK");
    } catch (final StrictPgnParserValidationException e) {
      final String message = BasicUtility.getMessage(e);
      return new StrictPgnParserValidationResult(e.getStrictPgnParserValidationProblem(), e.getSanValidationProblem(),
          message);
    } catch (final RuntimeException e) {
      final String message = unexpectedValidationErrorMessage(e);
      return new StrictPgnParserValidationResult(StrictPgnParserValidationProblem.UNKNOWN_ERROR,
          SanValidationProblem.UNKNOWN_ERROR, message);
    }
  }

  @SuppressWarnings("null")
  private static @NonNull String unexpectedValidationErrorMessage(RuntimeException e) {
    final @Nullable String nullableReason = e.getMessage();
    final var reason = nullableReason == null ? "" : nullableReason;
    return "An unexpected error occurred during validation. Reason: " + reason;
  }

  // -------------------------------------------------------------------------------------------------
  // Top-level parsing
  // -------------------------------------------------------------------------------------------------

  private PgnGame parseInternal() {
    StrictFileStructurePreScan.validate(source);

    final List<Tag> tagList = parseTagSection();
    validateUniqueTagNames(tagList);

    final ResultTagValue resultTagValue = validateResultTagValue(tagList);
    final SetUpTagValue setUpTagValue = validateTagSetUpValue(tagList);
    final var isStartFromPosition = setUpTagValue == SetUpTagValue.START_FROM_SETUP_POSITION;
    final Fen startFen = calculateStartFen(tagList, isStartFromPosition);

    final MovetextOutcome movetext = parseMovetext(startFen, resultTagValue);
    expectOnlyTrailingWhitespaceUntilEof();

    validateBoardPerLastMove(startFen, movetext.halfMoveList());

    return new PgnGame(Nulls.copyOfList(tagList), startFen, movetext.pregameCommentary(),
        Nulls.copyOfList(movetext.halfMoveList()), resultTagValue);
  }

  // -------------------------------------------------------------------------------------------------
  // Tag section
  // -------------------------------------------------------------------------------------------------

  private List<Tag> parseTagSection() {
    final List<Tag> tags = new ArrayList<>();
    while (true) {
      final PgnToken peek = tokenizer.peek();
      if (peek.type() == PgnTokenType.NEWLINE) {
        tokenizer.next(); // the separator blank line
        return tags;
      }
      if (peek.type() == PgnTokenType.EOF) {
        // Should be unreachable given the structural pre-scan, but guard anyway.
        throw tagFormatError(StrictPgnParserValidationProblem.TAG_FORMAT_INVALID, "Unexpected end of input in tags.");
      }
      tags.add(parseTag());
      final PgnToken eol = tokenizer.next();
      if (eol.type() != PgnTokenType.NEWLINE) {
        throw tagFormatError(StrictPgnParserValidationProblem.TAG_FORMAT_NOT_ENDING_WITH_RIGHT_SQUARE_BRACKET,
            "The tag line must end right after the closing right square bracket ].");
      }
    }
  }

  private Tag parseTag() {
    final PgnToken open = tokenizer.next();
    if (open.type() != PgnTokenType.TAG_BRACKET_OPEN) {
      throw tagFormatError(StrictPgnParserValidationProblem.TAG_FORMAT_NOT_STARTING_WITH_LEFT_SQUARE_BRACKET,
          "The tag line must start with the left square bracket [.");
    }

    final PgnToken afterOpen = tokenizer.peek();
    if (afterOpen.type() == PgnTokenType.SPACES) {
      throw tagFormatError(StrictPgnParserValidationProblem.TAG_FORMAT_LEFT_SQUARE_BRACKET_FOLLOWED_BY_SPACE,
          "The left square bracket [ must be followed by the tag name, but a space was found.");
    }

    if (afterOpen.type() != PgnTokenType.SYMBOL) {
      throw tagFormatError(StrictPgnParserValidationProblem.TAG_NAME_FIRST_CHARACTER,
          "The first character in the tag name must be one of A-Z, a-z or 0-9.");
    }
    final PgnToken nameToken = tokenizer.next();
    final String tagName = nameToken.text();
    validateTagNameFirstCharacter(tagName);
    validateTagNameCharacters(tagName);
    validateTagNameLength(tagName);

    final PgnToken afterName = tokenizer.peek();
    if (afterName.type() != PgnTokenType.SPACES || afterName.text().length() != 1) {
      throw tagFormatError(StrictPgnParserValidationProblem.TAG_FORMAT_INVALID,
          "A single space is required between the tag name and the tag value.");
    }
    tokenizer.next();

    final PgnToken valueToken = tokenizer.next();
    if (valueToken.type() == PgnTokenType.TAG_VALUE_STRING_UNTERMINATED) {
      // If the unterminated content ends with `]` the source had `["Tag "value]` (closing quote missing but bracket
      // present) -> unterminated-string error; otherwise the bracket itself is missing.
      final String content = valueToken.text();
      if (!content.isEmpty() && content.charAt(content.length() - 1) == ']') {
        throw tagFormatError(StrictPgnParserValidationProblem.TAG_FORMAT_INVALID,
            "The tag value starts with a double quote but is not properly terminated.");
      }
      throw tagFormatError(StrictPgnParserValidationProblem.TAG_FORMAT_NOT_ENDING_WITH_RIGHT_SQUARE_BRACKET,
          "The tag line must end with the right square bracket ].");
    }
    if (valueToken.type() != PgnTokenType.TAG_VALUE_STRING) {
      throw tagFormatError(StrictPgnParserValidationProblem.TAG_FORMAT_INVALID,
          "The tag value must be enclosed in double quotes.");
    }

    final PgnToken close = tokenizer.next();
    if (close.type() != PgnTokenType.TAG_BRACKET_CLOSE) {
      throw tagFormatError(StrictPgnParserValidationProblem.TAG_FORMAT_NOT_ENDING_WITH_RIGHT_SQUARE_BRACKET,
          "The tag line must end with the right square bracket ].");
    }

    return new Tag(tagName, valueToken.text());
  }

  private static void validateTagNameFirstCharacter(String name) {
    if (name.isEmpty() || !isAsciiLetterOrDigit(name.charAt(0))) {
      throw tagFormatError(StrictPgnParserValidationProblem.TAG_NAME_FIRST_CHARACTER,
          "The first character in the tag name must be one of A-Z, a-z or 0-9.");
    }
  }

  private static void validateTagNameCharacters(String name) {
    for (var i = 0; i < name.length(); i++) {
      final var c = name.charAt(i);
      if (!isAsciiLetterOrDigit(c) && c != '_' && c != '+' && c != '#' && c != '=' && c != ':' && c != '-') {
        throw tagFormatError(StrictPgnParserValidationProblem.TAG_FORMAT_INVALID,
            "The tag name contains an invalid character \"" + c + "\".");
      }
    }
  }

  private static void validateTagNameLength(String name) {
    if (name.length() > MAX_TAG_NAME_LENGTH) {
      throw tagFormatError(StrictPgnParserValidationProblem.TAG_NAME_EXCEEDS_MAXIMUM_LENGTH,
          "With " + name.length() + " characters, the tag name exceeds the allowed maximum length of "
              + MAX_TAG_NAME_LENGTH + " characters per the PGN specification.");
    }
  }

  private static void validateUniqueTagNames(List<Tag> tagList) {
    for (var i = 0; i < tagList.size(); i++) {
      for (var j = i + 1; j < tagList.size(); j++) {
        if (Nulls.get(tagList, i).name().equals(Nulls.get(tagList, j).name())) {
          throw new StrictPgnParserValidationException(StrictPgnParserValidationProblem.TAG_NAME_NOT_UNIQUE,
              SanValidationProblem.NONE, "The tag name must be unique. The tag name \"" + Nulls.get(tagList, i).name()
                  + "\" was used more than once.");
        }
      }
    }
  }

  private static ResultTagValue validateResultTagValue(List<Tag> tagList) {
    if (!TagUtility.hasResult(tagList)) {
      throw new StrictPgnParserValidationException(StrictPgnParserValidationProblem.TAG_RESULT_MISSING,
          SanValidationProblem.NONE,
          "The " + StandardTag.RESULT.getName()
              + " tag is required. PGN spec section 8.1.1 archival storage requires the full seven tag roster, but the"
              + " strict parser only enforces the semantic essentials: a Result tag (whose value must match the"
              + " termination marker) and the SetUp/FEN coupling. Other roster tags are archival-storage concerns"
              + " only.");
    }
    final String value = TagUtility.readResult(tagList);
    if (!ResultTagValue.exists(value)) {
      throw new StrictPgnParserValidationException(StrictPgnParserValidationProblem.TAG_RESULT_VALUE_INVALID,
          SanValidationProblem.NONE, "The " + StandardTag.RESULT.getName() + " tag value must exactly match one \""
              + ResultTagValue.calculateList() + "\".");
    }
    return ResultTagValue.calculate(value);
  }

  private static SetUpTagValue validateTagSetUpValue(List<Tag> tagList) {
    final SetUpTagValue setUpTagValue;
    if (!TagUtility.hasSetUp(tagList)) {
      setUpTagValue = SetUpTagValue.NONE;
    } else {
      final String setUpStr = TagUtility.readSetUp(tagList);
      if (!SetUpTagValue.exists(setUpStr)) {
        throw new StrictPgnParserValidationException(StrictPgnParserValidationProblem.TAG_SET_UP_VALUE_INVALID,
            SanValidationProblem.NONE, "The " + StandardTag.SET_UP.getName() + " tag value must exactly match one \""
                + SetUpTagValue.calculateList() + "\".");
      }
      setUpTagValue = SetUpTagValue.calculate(setUpStr);
    }
    validateTagFenValue(tagList, setUpTagValue);
    return setUpTagValue;
  }

  private static void validateTagFenValue(List<Tag> tagList, SetUpTagValue setUpTagValue) {
    final var hasFen = TagUtility.hasFen(tagList);
    switch (setUpTagValue) {
      case NONE, START_FROM_INITIAL_POSITION -> {
        if (hasFen) {
          throw new StrictPgnParserValidationException(StrictPgnParserValidationProblem.TAG_FEN_NOT_REQUIRED_BUT_SET,
              SanValidationProblem.NONE, "The FEN can only be set when " + StandardTag.FEN.getName() + " is set to "
                  + SetUpTagValue.START_FROM_SETUP_POSITION.getValue());
        }
      }
      case START_FROM_SETUP_POSITION -> {
        if (!hasFen) {
          throw new StrictPgnParserValidationException(
              StrictPgnParserValidationProblem.TAG_SET_UP_REQUIRES_FEN_TAG_BUT_MISSING, SanValidationProblem.NONE,
              "If the " + StandardTag.SET_UP.getName() + " is set to " + SetUpTagValue.START_FROM_SETUP_POSITION
                  + ", the " + StandardTag.FEN.getName() + " tag must be set.");
        }
        final String fen = TagUtility.readFen(tagList);
        try {
          FenParserAdvanced.parseFenAdvanced(fen);
        } catch (final com.dlb.chess.common.exceptions.FenAdvancedValidationException e) {
          final String fenErrorReason = BasicUtility.getMessage(e);
          throw new StrictPgnParserValidationException(
              StrictPgnParserValidationProblem.TAG_SET_UP_REQUIRES_FEN_TAG_BUT_FEN_INVALID, SanValidationProblem.NONE,
              "The required FEN tag was provided but is invalid. The error message when parsing was \"" + fenErrorReason
                  + "\".");
        }
      }
      default -> throw new IllegalArgumentException();
    }
  }

  // -------------------------------------------------------------------------------------------------
  // Movetext section
  // -------------------------------------------------------------------------------------------------

  private record MovetextOutcome(List<PgnHalfMove> halfMoveList, PgnCommentary pregameCommentary) {
  }

  private MovetextOutcome parseMovetext(Fen startFen, ResultTagValue resultTagValue) {
    // Pregame commentary (optional).
    var pregameCommentary = PgnCommentary.EMPTY;
    if (isBraceToken(tokenizer.peek().type())) {
      pregameCommentary = consumeCommentaryOrThrow();
      expectSpaceAfterComment();
    }

    final List<PgnHalfMove> halfMoves = new ArrayList<>();
    Side havingMove = startFen.havingMove();
    var fullMoveNumber = startFen.fullMoveNumber();
    var isFirstMove = true;

    // Zero-move game: just <space><terminator> after the (optional) pregame commentary - no move numbers, no SANs.
    if (tokenizer.peek().type() == PgnTokenType.SPACES && tokenizer.peek().text().length() == 1
        && tokenizer.peekNext().type() == PgnTokenType.TERMINATION_MARKER) {
      tokenizer.next();
      final PgnToken terminator = tokenizer.next();
      validateTermination(terminator, resultTagValue);
      return new MovetextOutcome(halfMoves, pregameCommentary);
    }

    // T-002 / PGN spec section 8.2.2 case 1: commentary on White's move forces "N..." before the next Black move.
    var priorCommentaryAttached = false;

    while (true) {
      if (tokenizer.peek().type() == PgnTokenType.TERMINATION_MARKER) {
        final PgnToken terminator = tokenizer.next();
        validateTermination(terminator, resultTagValue);
        return new MovetextOutcome(halfMoves, pregameCommentary);
      }

      // Non-initial Black move: with prior commentary, "N..." indicator is required (T-002); without, forbidden.
      if (!isFirstMove && havingMove == Side.BLACK) {
        if (priorCommentaryAttached) {
          final PgnToken token = tokenizer.peek();
          final String expected = HalfMoveUtility.calculateFullMoveNumberInitialWithoutSpace(fullMoveNumber,
              Side.BLACK);
          if (token.type() != PgnTokenType.MOVE_NUMBER_BLACK || !token.text().equals(expected)) {
            throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_REQUIRED_AFTER_COMMENTARY,
                "Black move after intervening commentary requires move-number indicator \"" + expected + "\".");
          }
          tokenizer.next();
          expectInterTokenSpace(StrictPgnParserValidationProblem.MOVETEXT_UNEXPECTED_FORMAT,
              "A move number must be followed by a single space.");
        } else {
          final PgnTokenType t = tokenizer.peek().type();
          if (t == PgnTokenType.MOVE_NUMBER_WHITE || t == PgnTokenType.MOVE_NUMBER_BLACK) {
            throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_FOR_BLACK_NON_INITIAL_MOVE,
                "Move number specified for Black non initial move.");
          }
        }
      }

      if (isFirstMove || havingMove == Side.WHITE) {
        expectMoveNumber(fullMoveNumber, havingMove, isFirstMove);
        expectInterTokenSpace(StrictPgnParserValidationProblem.MOVETEXT_UNEXPECTED_FORMAT,
            "A move number must be followed by a single space.");
      }

      final SanAndSuffix sanAndSuffix = parseSanAndSuffix();

      // Trailing blank line at this position -> file has no termination marker; report specifically.
      expectInterTokenSeparatorOrMissingTermination(resultTagValue);

      var commentary = PgnCommentary.EMPTY;
      if (isBraceToken(tokenizer.peek().type())) {
        commentary = consumeCommentaryOrThrow();
        expectSpaceAfterComment();
        priorCommentaryAttached = true;
      } else {
        priorCommentaryAttached = false;
      }

      halfMoves.add(new PgnHalfMove(sanAndSuffix.san(), sanAndSuffix.suffix(), commentary));

      isFirstMove = false;
      if (havingMove == Side.BLACK) {
        fullMoveNumber++;
      }
      havingMove = havingMove.getOppositeSide();
    }
  }

  /**
   * Returns the {@link PgnCommentary} for a well-formed brace token, or throws the matching error category.
   */
  private PgnCommentary consumeCommentaryOrThrow() {
    final PgnToken token = tokenizer.next();
    switch (token.type()) {
      case BRACE_COMMENT:
        try {
          return new PgnCommentary(token.text());
        } catch (final PgnCommentaryValidationException pcve) {
          // Defensive - the tokenizer cannot produce `}` here (handled as separate types), so unreachable in
          // practice.
          final String message = BasicUtility.getMessage(pcve);
          throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_CONTAINS_FORBIDDEN_CHARACTER,
              message);
        }
      case BRACE_COMMENT_UNCLOSED:
        throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE,
            "A commentary opened with { was not closed with } before end of input.");
      case BRACE_STRAY_CLOSE:
        throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE,
            "A closing brace } was found with no matching opening brace.");
      default:
        throw new ProgrammingMistakeException("consumeCommentaryOrThrow called for non-brace token: " + token.type());
    }
  }

  private static boolean isBraceToken(PgnTokenType type) {
    return type == PgnTokenType.BRACE_COMMENT || type == PgnTokenType.BRACE_COMMENT_UNCLOSED
        || type == PgnTokenType.BRACE_STRAY_CLOSE;
  }

  private void expectSpaceAfterComment() {
    final PgnToken token = tokenizer.peek();
    if (token.type() == PgnTokenType.NEWLINE || token.type() == PgnTokenType.SPACES && token.text().length() == 1) {
      tokenizer.next();
      return;
    }
    // A broken brace right after the closed commentary reports its specific category, not the generic missing-space.
    throwIfBrokenBrace(token);
    throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_NOT_FOLLOWED_BY_SPACE,
        "The movetext doesnt continue with a space after a comment");
  }

  /** After the termination marker only whitespace/newlines may appear before EOF. */
  private void expectOnlyTrailingWhitespaceUntilEof() {
    while (true) {
      final PgnToken token = tokenizer.peek();
      if (token.type() == PgnTokenType.EOF) {
        return;
      }
      if (token.type() == PgnTokenType.NEWLINE || token.type() == PgnTokenType.SPACES) {
        tokenizer.next();
        continue;
      }
      throwIfBrokenBrace(token);
      throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_CONTENT_AFTER_TERMINATION,
          "Unexpected content after the game termination marker: \"" + token.text() + "\".");
    }
  }

  /**
   * Throws the broken-brace-specific error if {@code token} is one; returns normally otherwise.
   */
  private static void throwIfBrokenBrace(PgnToken token) {
    switch (token.type()) {
      case BRACE_COMMENT_UNCLOSED:
        throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE,
            "A commentary opened with { was not closed with } before end of input.");
      case BRACE_STRAY_CLOSE:
        throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE,
            "A closing brace } was found with no matching opening brace.");
      default:
        // Not a broken brace - caller handles.
    }
  }

  private void expectInterTokenSeparatorOrMissingTermination(ResultTagValue resultTagValue) {
    final PgnToken peek = tokenizer.peek();
    if (peek.type() == PgnTokenType.NEWLINE && tokenizer.peekNext().type() == PgnTokenType.NEWLINE
        || peek.type() == PgnTokenType.EOF) {
      throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_TERMINATION_INVALID,
          "The file must end with the result provided in the result tag. The provided result is \""
              + resultTagValue.getValue() + "\".");
    }
    expectInterTokenSpace(StrictPgnParserValidationProblem.MOVETEXT_UNEXPECTED_FORMAT,
        "A half-move must be followed by a single space before the next token.");
  }

  private void expectInterTokenSpace(StrictPgnParserValidationProblem problem, String message) {
    final PgnToken token = tokenizer.next();
    if (token.type() == PgnTokenType.NEWLINE) {
      return;
    }
    if (token.type() != PgnTokenType.SPACES || token.text().length() != 1) {
      throw movetextError(problem, message);
    }
  }

  private record SanAndSuffix(String san, MoveSuffixAnnotation suffix) {
  }

  private SanAndSuffix parseSanAndSuffix() {
    final PgnToken token = tokenizer.peek();
    if (isBraceToken(token.type())) {
      throwIfBrokenBrace(token);
      throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_COMMENTARY_NOT_ALLOWED_IN_SAN,
          "A commentary brace cannot occur where a SAN half-move is expected.");
    }
    if (token.type() != PgnTokenType.SYMBOL) {
      throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_SAN_EMPTY,
          "The movetext is invalid because a SAN possibly annotated was expected but no SAN was found.");
    }
    final PgnToken sanToken = tokenizer.next();
    final String san = sanToken.text();
    validateSanCharacters(san);
    validateSanLength(san);

    var suffix = MoveSuffixAnnotation.NONE;
    if (tokenizer.peek().type() == PgnTokenType.MOVE_SUFFIX_ANNOTATION) {
      final PgnToken suffixToken = tokenizer.next();
      if (!MoveSuffixAnnotation.exists(suffixToken.text())) {
        throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_MOVE_SUFFIX_ANNOTATION_INVALID,
            "An invalid move annotation suffix of \"" + suffixToken.text() + "\" was found. Valid values are \""
                + MoveSuffixAnnotation.calculateValueList() + "\".");
      }
      suffix = MoveSuffixAnnotation.calculate(suffixToken.text());
    }
    return new SanAndSuffix(san, suffix);
  }

  private void expectMoveNumber(int expectedNumber, Side havingMove, boolean isFirstMove) {
    final PgnToken token = tokenizer.peek();
    final String expected = HalfMoveUtility.calculateFullMoveNumberInitialWithoutSpace(expectedNumber, havingMove);

    if (havingMove == Side.BLACK) {
      if (!isFirstMove) {
        if (token.type() == PgnTokenType.MOVE_NUMBER_BLACK || token.type() == PgnTokenType.MOVE_NUMBER_WHITE) {
          throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_FOR_BLACK_NON_INITIAL_MOVE,
              "Move number specified for Black non initial move.");
        }
        return;
      }
      if (token.type() != PgnTokenType.MOVE_NUMBER_BLACK || !token.text().equals(expected)) {
        throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_DOES_NOT_BEGIN_WITH_BLACK_MOVE,
            "The move text does not begin indicating a Black move \"" + expected + "\" as expected.");
      }
      tokenizer.next();
      return;
    }

    // White move number expected.
    if (token.type() != PgnTokenType.MOVE_NUMBER_WHITE || !token.text().equals(expected)) {
      if (isFirstMove) {
        throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_DOES_NOT_BEGIN_WITH_WHITE_MOVE,
            "The move text does not begin indicating a White move \"" + expected + "\" as expected.");
      }
      throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_MOVE_NUMBER_DOES_NOT_CONTINUE_AS_EXPECTED,
          "The movetext numbering does not continue with \"" + expected + "\" as expected.");
    }
    tokenizer.next();
  }

  private static void validateSanCharacters(String san) {
    for (var i = 0; i < san.length(); i++) {
      final var c = san.charAt(i);
      if (!com.dlb.chess.fen.FenPieceSymbol.exists(c) && !com.dlb.chess.board.enums.File.exists(c)
          && !com.dlb.chess.board.enums.Rank.exists(c) && !com.dlb.chess.san.SanSymbol.exists(c)) {
        throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_SAN_CHARACTER_INVALID,
            "The movetext is invalid because a SAN contains an invalid character of \"" + c + "\".");
      }
    }
  }

  private static void validateSanLength(String san) {
    if (san.length() < SAN_MIN_LENGTH || san.length() > SAN_MAX_LENGTH) {
      throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_SAN_LENGTH_INVALID,
          "The movetext contains the SAN '" + san + "' with an invalid SAN length.");
    }
  }

  private static void validateTermination(PgnToken terminator, ResultTagValue resultTagValue) {
    if (!terminator.text().equals(resultTagValue.getValue())) {
      throw movetextError(StrictPgnParserValidationProblem.MOVETEXT_TERMINATION_INVALID,
          "The file must end with the result provided in the result tag. The provided result is \""
              + resultTagValue.getValue() + "\".");
    }
  }

  // -------------------------------------------------------------------------------------------------
  // Board replay & post-processing
  // -------------------------------------------------------------------------------------------------

  private static void validateBoardPerLastMove(Fen startFen, List<PgnHalfMove> halfMoveList) {
    // Strict parsing enforces PGN syntax + move legality + the cheap FIDE terminations
    // (checkmate / stalemate / mutual insufficient material). Fivefold repetition, the 75-move rule, and
    // analyzer-driven dead positions are queryable predicates on Board; historical PGN corpora routinely contain games
    // whose recorded play continues past such states.
    final Board board = new Board(startFen);
    for (final PgnHalfMove halfMove : halfMoveList) {
      final Side side = board.getHavingMove();
      final var fullMoveNumber = board.getFullMoveNumber();
      try {
        board.moveStrict(halfMove.san());
      } catch (final SanValidationException e) {
        final String moveNumberAndSan = HalfMoveUtility.calculateMoveNumberAndSanWithSpace(fullMoveNumber, side,
            halfMove.san());
        final String messageSanValidationFailure = BasicUtility.getMessage(e);
        final var message = "The validation for " + moveNumberAndSan + " failed. Reason: "
            + messageSanValidationFailure;
        throw new StrictPgnParserValidationException(StrictPgnParserValidationProblem.SAN, e.getSanValidationProblem(),
            message);
      }
    }
  }

  private static Fen calculateStartFen(List<Tag> tagList, boolean isStartFromPosition) {
    final var startFenStr = isStartFromPosition ? TagUtility.readFen(tagList) : FenConstants.FEN_INITIAL_STR;
    return FenParserAdvanced.parseFenAdvanced(startFenStr);
  }

  // -------------------------------------------------------------------------------------------------
  // Exception builders
  // -------------------------------------------------------------------------------------------------

  private static StrictPgnParserValidationException tagFormatError(StrictPgnParserValidationProblem problem,
      String message) {
    return new StrictPgnParserValidationException(problem, SanValidationProblem.NONE, message);
  }

  private static StrictPgnParserValidationException movetextError(StrictPgnParserValidationProblem problem,
      String message) {
    return new StrictPgnParserValidationException(problem, SanValidationProblem.NONE, message);
  }

  private static boolean isAsciiLetterOrDigit(char c) {
    return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
  }
}
