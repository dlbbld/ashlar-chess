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
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.enums.MoveSuffixAnnotation;
import com.dlb.chess.fen.LenientFenParser;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.PgnHalfMove;
import com.dlb.chess.san.ForgivenItem;
import com.dlb.chess.san.LenientSanParserValidationException;
import com.dlb.chess.san.LenientSanParserValidationResult;
import com.dlb.chess.san.SanValidationProblem;

/**
 * Lenient PGN parser. Permissive inter-token rules: any whitespace separates tokens, move-number indicators are
 * consumed and ignored, termination marker is optional, space before suffixes is tolerated. Tag-list contents are
 * preserved as given (missing seven-tag-roster entries, FEN without SetUp, Result tag absent, redundant
 * initial-position FEN/SetUp) - no fabrication into the parse model. Tag-level deviations surface via
 * {@code tagForgivenItems} on the validation result; archival output is opt-in via {@code WriteMode.ARCHIVAL} on
 * {@code PgnWriter}. Independent validation pipeline from {@link StrictPgnParser}.
 */
public final class LenientPgnParser {

  private static final int SAN_MIN_LENGTH = 2;
  // Bumped from 7 to 9 to admit the longest lenient SAN forms - e.g. pawn LAN promotion with hyphen and check
  // ("e7-e8=Q+" is 8 chars).
  private static final int SAN_MAX_LENGTH = 9;

  private final String source;
  private final PgnTokenizer tokenizer;
  private final List<ForgivenItem> sanForgivenItemsAccumulator = new ArrayList<>();
  private final List<ForgivenTagItem> tagForgivenItemsAccumulator = new ArrayList<>();

  private LenientPgnParser(String source) {
    this.source = NewlineNormalization.toLf(stripUtf8Bom(source));
    this.tokenizer = new PgnTokenizer(new PgnCharStream(this.source));
  }

  // -------------------------------------------------------------------------------------------------
  // Public entry points
  // -------------------------------------------------------------------------------------------------

  public static PgnGame parseText(String pgn) {
    return new LenientPgnParser(pgn).parseInternal();
  }

  public static PgnGame parse(Path pgnPath) {
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

  public static LenientPgnParserValidationResult validate(Path pgnFolderPath, String pgnName) {
    return validate(Nulls.pathResolve(pgnFolderPath, pgnName));
  }

  public static LenientPgnParserValidationResult validate(String pgnPath) {
    return validate(Nulls.pathOf(pgnPath));
  }

  public static LenientPgnParserValidationResult validate(Path pgnPath) {
    final LenientPgnParser parser;
    try {
      parser = new LenientPgnParser(PgnReader.readPgn(pgnPath));
    } catch (final RuntimeException e) {
      return new LenientPgnParserValidationResult(LenientPgnParserValidationProblem.UNKNOWN_ERROR,
          SanValidationProblem.NONE, unexpectedValidationErrorMessage(e), null, ForgivenItem.EMPTY_LIST,
          ForgivenTagItem.EMPTY_LIST);
    }
    return runValidation(parser);
  }

  /**
   * Like {@link #parseText(String)} but returns a structured result instead of throwing. The result also carries the
   * parsed {@link PgnGame} (on success) and the list of SAN-level deviations the lenient layer forgave during movetext
   * replay.
   */
  public static LenientPgnParserValidationResult validateText(String pgn) {
    return runValidation(new LenientPgnParser(pgn));
  }

  private static LenientPgnParserValidationResult runValidation(LenientPgnParser parser) {
    try {
      final PgnGame pgnGame = parser.parseInternal();
      return new LenientPgnParserValidationResult(LenientPgnParserValidationProblem.OK, SanValidationProblem.NONE, "OK",
          pgnGame, Nulls.copyOfList(parser.sanForgivenItemsAccumulator),
          Nulls.copyOfList(parser.tagForgivenItemsAccumulator));
    } catch (final LenientPgnParserValidationException e) {
      final String message = BasicUtility.getMessage(e);
      return new LenientPgnParserValidationResult(e.getLenientPgnParserValidationProblem(), e.getSanValidationProblem(),
          message, null, e.getSanForgivenItemsAccumulated(), e.getTagForgivenItemsAccumulated());
    } catch (final com.dlb.chess.fen.LenientFenParserValidationException e) {
      // The FEN tag in the PGN failed lenient FEN parsing (either unrecoverable input or strict-semantic
      // rejection by FenParserAdvanced). Surface as a typed PGN problem rather than leaking the FEN exception
      // type through the generic RuntimeException path. The tag-level forgiven items accumulated up to the
      // FEN-tag parse point are carried so callers retain partial diagnostic context.
      final String message = "The PGN FEN tag is invalid. Reason: " + BasicUtility.getMessage(e);
      return new LenientPgnParserValidationResult(LenientPgnParserValidationProblem.FEN_TAG_INVALID,
          SanValidationProblem.NONE, message, null, Nulls.copyOfList(parser.sanForgivenItemsAccumulator),
          Nulls.copyOfList(parser.tagForgivenItemsAccumulator));
    } catch (final RuntimeException e) {
      final String message = unexpectedValidationErrorMessage(e);
      return new LenientPgnParserValidationResult(LenientPgnParserValidationProblem.UNKNOWN_ERROR,
          SanValidationProblem.NONE, message, null, ForgivenItem.EMPTY_LIST, ForgivenTagItem.EMPTY_LIST);
    }
  }

  @SuppressWarnings("null")
  private static @NonNull String unexpectedValidationErrorMessage(RuntimeException e) {
    final @Nullable String nullableReason = e.getMessage();
    final String reason = nullableReason == null ? "" : nullableReason;
    return "An unexpected error occurred during validation. Reason: " + reason;
  }

  private static String stripUtf8Bom(String source) {
    if (!source.isEmpty() && source.charAt(0) == '\uFEFF') {
      return Nulls.substring(source, 1);
    }
    return source;
  }

  // -------------------------------------------------------------------------------------------------
  // Top-level parsing
  // -------------------------------------------------------------------------------------------------

  private PgnGame parseInternal() {
    // Empty-input rejection. Lenient leniency is about recovering signal from imperfect PGN text - it does not
    // fabricate a game out of zero input. "Empty" here includes whitespace-only (spaces, tabs, newlines) on top
    // of strictly zero-length: a file containing nothing but whitespace carries no signal either. Callers who
    // really want the initial position have {@code new Board()}.
    if (source.isBlank()) {
      throw new LenientPgnParserValidationException(LenientPgnParserValidationProblem.FILE_EMPTY,
          SanValidationProblem.NONE, "The PGN is empty.");
    }

    skipInsignificantWhitespace();

    final List<Tag> tagList = parseTagSection();
    validateUniqueTagNames(tagList);
    validateResultTagValueIfPresent(tagList);

    skipInsignificantWhitespace();
    final MovetextOutcome movetext = parseMovetext();
    expectOnlyTrailingContentUntilEof();

    validateResultConsistency(tagList, movetext.terminationResult());

    validateTagSetUpValue(tagList);
    // Lenient policy: presence of the FEN tag drives the setup-from-position decision. SetUp/FEN coupling
    // deviations are preserved in the tag list as-given; they surface as tag-level forgiven items below.
    final boolean isStartFromPosition = TagUtility.hasFen(tagList);

    final Fen startFen = calculateStartFen(tagList, isStartFromPosition);

    // Tag-level forgiveness reporting. Populated before movetext replay so the diagnostics survive a SAN-level
    // failure (the exception path carries the accumulator). The parse model is preserved as given; this pass only
    // populates the diagnostic accumulator that surfaces on the validation result.
    recordMissingStrTagItems(tagList);
    recordResultAndTerminationMarkerItems(tagList, movetext.terminationResult());
    recordSetUpFenCouplingItems(tagList, startFen);

    final List<PgnHalfMove> canonicalHalfMoveList = replayBoardCanonicalizing(startFen, movetext.halfMoveList());

    return new PgnGame(Nulls.copyOfList(tagList), startFen, movetext.pregameCommentary(),
        Nulls.copyOfList(canonicalHalfMoveList), movetext.terminationResult());
  }

  // -------------------------------------------------------------------------------------------------
  // Tag section
  // -------------------------------------------------------------------------------------------------

  private List<Tag> parseTagSection() {
    final List<Tag> tags = new ArrayList<>();
    while (true) {
      skipInsignificantWhitespace();
      final PgnToken peek = tokenizer.peek();
      if (peek.type() == PgnTokenType.TAG_BRACKET_OPEN) {
        tags.add(parseTag());
        continue;
      }
      // Any line still containing [ or ] is a tag-line candidate; report it rather than slip into movetext parsing.
      if (currentLineContainsTagBracket(peek.line())) {
        throw tagFormatError("A tag line with an invalid format was found on line " + peek.line() + ".");
      }
      return tags;
    }
  }

  /**
   * Returns true if line {@code lineNumber} (1-based) contains {@code [} or {@code ]}.
   */
  private boolean currentLineContainsTagBracket(int lineNumber) {
      int index = 0;
      int currentLine = 1;
    while (currentLine < lineNumber && index < source.length()) {
      final char c = source.charAt(index);
      if (c == '\r') {
        currentLine++;
        if (index + 1 < source.length() && source.charAt(index + 1) == '\n') {
          index++;
        }
      } else if (c == '\n') {
        currentLine++;
      }
      index++;
    }
    while (index < source.length()) {
      final char c = source.charAt(index);
      if (c == '\n' || c == '\r') {
        break;
      }
      if (c == '[' || c == ']') {
        return true;
      }
      index++;
    }
    return false;
  }

  private Tag parseTag() {
    final PgnToken open = tokenizer.next();
    if (open.type() != PgnTokenType.TAG_BRACKET_OPEN) {
      throw tagFormatError("Tag opening bracket [ expected but found \"" + open.text() + "\".");
    }
    skipInlineWhitespace();

    final PgnToken nameToken = tokenizer.next();
    if (nameToken.type() != PgnTokenType.SYMBOL) {
      throw tagFormatError("The first character in the tag name must be one of A-Z, a-z or 0-9.");
    }
    validateTagNameCharacters(nameToken.text());
    skipInlineWhitespace();

    final PgnToken valueToken = tokenizer.next();
    if (valueToken.type() != PgnTokenType.TAG_VALUE_STRING) {
      throw tagFormatError("A tag value enclosed in double quotes was expected.");
    }
    skipInlineWhitespace();

    final PgnToken close = tokenizer.next();
    if (close.type() != PgnTokenType.TAG_BRACKET_CLOSE) {
      throw tagFormatError("The tag line must end with the right square bracket ].");
    }

    return new Tag(nameToken.text(), valueToken.text());
  }

  private static void validateTagNameCharacters(String name) {
    if (name.isEmpty()) {
      throw tagFormatError("Tag name must not be empty.");
    }
    for (int i = 0; i < name.length(); i++) {
      final char c = name.charAt(i);
      if (!isAsciiLetterOrDigit(c) && c != '_' && c != '+' && c != '#' && c != '=' && c != ':' && c != '-') {
        throw tagFormatError("The tag name contains an invalid character \"" + c + "\".");
      }
    }
  }

  private static void validateUniqueTagNames(List<Tag> tagList) {
    for (int i = 0; i < tagList.size(); i++) {
      for (int j = i + 1; j < tagList.size(); j++) {
        if (Nulls.get(tagList, i).name().equals(Nulls.get(tagList, j).name())) {
          throw new LenientPgnParserValidationException(LenientPgnParserValidationProblem.TAG_NAME_NOT_UNIQUE,
              SanValidationProblem.NONE, "The tag name must be unique. The tag name \"" + Nulls.get(tagList, i).name()
                  + "\" was used more than once.");
        }
      }
    }
  }

  private static void validateResultTagValueIfPresent(List<Tag> tagList) {
    if (!TagUtility.hasResult(tagList)) {
      return;
    }
    final String value = TagUtility.readResult(tagList);
    if (!ResultTagValue.exists(value)) {
      throw new LenientPgnParserValidationException(LenientPgnParserValidationProblem.TAG_RESULT_VALUE_INVALID,
          SanValidationProblem.NONE, "The " + StandardTag.RESULT.getName() + " tag value must exactly match one \""
              + ResultTagValue.calculateList() + "\".");
    }
  }

  private static void validateTagSetUpValue(List<Tag> tagList) {
    if (!TagUtility.hasSetUp(tagList)) {
      return;
    }
    final String value = TagUtility.readSetUp(tagList);
    if (!SetUpTagValue.exists(value)) {
      throw new LenientPgnParserValidationException(LenientPgnParserValidationProblem.TAG_SET_UP_VALUE_INVALID,
          SanValidationProblem.NONE, "The " + StandardTag.SET_UP.getName() + " tag value must exactly match one \""
              + SetUpTagValue.calculateList() + "\".");
    }
    final SetUpTagValue setUpTagValue = SetUpTagValue.calculate(value);
    if (setUpTagValue == SetUpTagValue.START_FROM_INITIAL_POSITION && TagUtility.hasFen(tagList)) {
      throw new LenientPgnParserValidationException(
          LenientPgnParserValidationProblem.TAG_SET_UP_VALUE_ZERO_BUT_FEN_PROVIDED, SanValidationProblem.NONE,
          "When the " + StandardTag.SET_UP.getName() + " tag is set to "
              + SetUpTagValue.START_FROM_INITIAL_POSITION.getValue() + ", then no " + StandardTag.FEN.getName()
              + " tag can be provided.");
    }
  }

  // -------------------------------------------------------------------------------------------------
  // Movetext section
  // -------------------------------------------------------------------------------------------------

  private record MovetextOutcome(List<PgnHalfMove> halfMoveList, PgnCommentary pregameCommentary,
      @Nullable ResultTagValue terminationResult) {
  }

  private MovetextOutcome parseMovetext() {
      PgnCommentary pregameCommentary = PgnCommentary.EMPTY;
    final List<PgnHalfMove> halfMoves = new ArrayList<>();
    @Nullable ResultTagValue terminationResult = null;

    skipInsignificantWhitespace();
    // Pregame-commentary slot: exactly one commentary allowed before the first move. Additional braces before any
    // half-move fall through to the main loop and are reported as R4 (brace at SAN-expected position).
    if (isBraceToken(tokenizer.peek().type())) {
      pregameCommentary = consumeCommentaryOrThrow();
    }

    while (true) {
      skipInsignificantWhitespace();
      final PgnToken peek = tokenizer.peek();
      final PgnTokenType type = peek.type();

      if (type == PgnTokenType.EOF) {
        break;
      }
      if (type == PgnTokenType.TAG_BRACKET_OPEN) {
        throw tagReappearError();
      }
      if (type == PgnTokenType.TERMINATION_MARKER) {
        tokenizer.next();
        terminationResult = ResultTagValue.calculate(peek.text());
        break;
      }
      if (type == PgnTokenType.MOVE_NUMBER_WHITE || type == PgnTokenType.MOVE_NUMBER_BLACK) {
        tokenizer.next();
        continue;
      }
      if (isBraceToken(type)) {
        throwIfBrokenBrace(peek);
        throw movetextError(LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_NOT_ALLOWED_IN_SAN,
            "A commentary brace cannot occur where a SAN half-move is expected.");
      }
      if (type == PgnTokenType.MOVE_SUFFIX_ANNOTATION) {
        if (halfMoves.isEmpty()) {
          tokenizer.next();
          continue;
        }
        final PgnToken suffixToken = tokenizer.next();
        final MoveSuffixAnnotation suffix = parseMoveSuffix(suffixToken.text());
        final int last = halfMoves.size() - 1;
        final PgnHalfMove previous = Nulls.get(halfMoves, last);
        halfMoves.set(last, new PgnHalfMove(previous.san(), suffix, previous.commentary()));
        continue;
      }
      if (type == PgnTokenType.SYMBOL) {
        // Tolerate spaced move-number indicators like `1 . e4` and `1 ... e5` - see consumedSpacedMoveNumber.
        if (consumedSpacedMoveNumber(peek)) {
          continue;
        }
        final PgnHalfMove halfMove = parseHalfMoveLenient();
        halfMoves.add(halfMove);
        skipInsignificantWhitespace();
        if (isBraceToken(tokenizer.peek().type())) {
          final PgnCommentary commentary = consumeCommentaryOrThrow();
          final int last = halfMoves.size() - 1;
          final PgnHalfMove previous = Nulls.get(halfMoves, last);
          halfMoves.set(last, new PgnHalfMove(previous.san(), previous.moveSuffixAnnotation(), commentary));
        }
        continue;
      }
      throw new LenientPgnParserValidationException(
          LenientPgnParserValidationProblem.EXCEPTION_CAUGHT_FROM_STRICT_VALIDATION, SanValidationProblem.NONE,
          "Unexpected token \"" + peek.text() + "\" at line " + peek.line() + ".");
    }

    return new MovetextOutcome(halfMoves, pregameCommentary, terminationResult);
  }

  /**
   * Detects and consumes a spaced move-number indicator (`N . ` or `N ... `) where digits and dots arrive as separate
   * symbols because whitespace split them. Returns true on consumption.
   */
  private boolean consumedSpacedMoveNumber(PgnToken digitsPeek) {
    if (!isPurelyDigits(digitsPeek.text())) {
      return false;
    }
    // Pattern 1 - digits + SPACES + dots-only symbol. Committing to consume the digits is safe because a lone
    // digits symbol is not a valid SAN; the length-error path below catches the no-dots case.
    final PgnTokenType next = tokenizer.peekNext().type();
    if (next == PgnTokenType.SPACES) {
      tokenizer.next(); // digits
      skipInlineWhitespace();
      final PgnToken afterSpace = tokenizer.peek();
      if (afterSpace.type() == PgnTokenType.SYMBOL && isAllDots(afterSpace.text())) {
        tokenizer.next();
        return true;
      }
      throw movetextError(LenientPgnParserValidationProblem.EXCEPTION_CAUGHT_FROM_STRICT_VALIDATION,
          "The movetext contains the SAN '" + digitsPeek.text() + "' with an invalid SAN length.");
    }
    // Pattern 2 - digits + dots-only SYMBOL with no separating SPACES. Rare; tokenizer normally coalesces these.
    if (next == PgnTokenType.SYMBOL && isAllDots(tokenizer.peekNext().text())) {
      tokenizer.next(); // digits
      tokenizer.next(); // dots
      return true;
    }
    return false;
  }

  private static boolean isPurelyDigits(String text) {
    if (text.isEmpty()) {
      return false;
    }
    for (int i = 0; i < text.length(); i++) {
      final char c = text.charAt(i);
      if (c < '0' || c > '9') {
        return false;
      }
    }
    return true;
  }

  private static boolean isAllDots(String text) {
    if (text.isEmpty()) {
      return false;
    }
    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) != '.') {
        return false;
      }
    }
    return true;
  }

  private PgnHalfMove parseHalfMoveLenient() {
    final PgnToken sanToken = tokenizer.next();
    final StringBuilder sanBuilder = new StringBuilder(sanToken.text());

    // Lenient quirk: a lone + or # appearing after whitespace belongs to the preceding SAN. The tokenizer emits
    // it as its own SYMBOL because the whitespace split it off from the main SAN body.
    skipInlineWhitespace();
    final PgnToken peekAfterSpace = tokenizer.peek();
    if (peekAfterSpace.type() == PgnTokenType.SYMBOL && isBareCheckOrMate(peekAfterSpace.text())) {
      sanBuilder.append(peekAfterSpace.text());
      tokenizer.next();
    }

    final String san = Nulls.toString(sanBuilder);
    validateSanCharacters(san);
    validateSanLength(san);

      MoveSuffixAnnotation suffix = MoveSuffixAnnotation.NONE;
    // Allow whitespace between SAN and suffix annotation (`e4 !!`).
    skipInlineWhitespace();
    if (tokenizer.peek().type() == PgnTokenType.MOVE_SUFFIX_ANNOTATION) {
      suffix = parseMoveSuffix(tokenizer.next().text());
    }

    return new PgnHalfMove(san, suffix, PgnCommentary.EMPTY);
  }

  private static boolean isBareCheckOrMate(String text) {
    return "+".equals(text) || "#".equals(text);
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
          throw movetextError(LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_CONTAINS_FORBIDDEN_CHARACTER,
              message);
        }
      case BRACE_COMMENT_UNCLOSED:
        throw movetextError(LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE,
            "A commentary opened with { was not closed with } before end of input.");
      case BRACE_STRAY_CLOSE:
        throw movetextError(LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE,
            "A closing brace } was found with no matching opening brace.");
      default:
        throw new com.dlb.chess.common.exceptions.ProgrammingMistakeException(
            "consumeCommentaryOrThrow called for non-brace token: " + token.type());
    }
  }

  private static boolean isBraceToken(PgnTokenType type) {
    return type == PgnTokenType.BRACE_COMMENT || type == PgnTokenType.BRACE_COMMENT_UNCLOSED
        || type == PgnTokenType.BRACE_STRAY_CLOSE;
  }

  /** After the termination marker (or EOF) only whitespace may appear before EOF. */
  private void expectOnlyTrailingContentUntilEof() {
    while (true) {
      final PgnToken token = tokenizer.peek();
      if (token.type() == PgnTokenType.EOF) {
        return;
      }
      if (token.type() == PgnTokenType.SPACES || token.type() == PgnTokenType.NEWLINE) {
        tokenizer.next();
        continue;
      }
      if (token.type() == PgnTokenType.TAG_BRACKET_OPEN || currentLineContainsTagBracket(token.line())) {
        throw tagReappearError();
      }
      throwIfBrokenBrace(token);
      throw movetextError(LenientPgnParserValidationProblem.MOVETEXT_CONTENT_AFTER_TERMINATION,
          "Unexpected content after the game termination marker: \"" + token.text() + "\".");
    }
  }

  /**
   * Throws the broken-brace-specific error if {@code token} is one; returns normally otherwise.
   */
  private static void throwIfBrokenBrace(PgnToken token) {
    switch (token.type()) {
      case BRACE_COMMENT_UNCLOSED:
        throw movetextError(LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE,
            "A commentary opened with { was not closed with } before end of input.");
      case BRACE_STRAY_CLOSE:
        throw movetextError(LenientPgnParserValidationProblem.MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE,
            "A closing brace } was found with no matching opening brace.");
      default:
        // Not a broken brace - caller handles.
    }
  }

  private static LenientPgnParserValidationException movetextError(LenientPgnParserValidationProblem problem,
      String message) {
    return new LenientPgnParserValidationException(problem, SanValidationProblem.NONE, message);
  }

  private static MoveSuffixAnnotation parseMoveSuffix(String text) {
    // Lenient: unknown suffixes downgrade to NONE rather than fail.
    if (!MoveSuffixAnnotation.exists(text)) {
      return MoveSuffixAnnotation.NONE;
    }
    return MoveSuffixAnnotation.calculate(text);
  }

  private static void validateSanCharacters(String san) {
    for (int i = 0; i < san.length(); i++) {
      final char c = san.charAt(i);
      if (isAllowedLenientSanCharacter(c)) {
        continue;
      }
      throw new LenientPgnParserValidationException(
          LenientPgnParserValidationProblem.EXCEPTION_CAUGHT_FROM_STRICT_VALIDATION, SanValidationProblem.NONE,
          "The movetext is invalid because a SAN contains an invalid character of \"" + c + "\".");
    }
  }

  private static boolean isAllowedLenientSanCharacter(char c) {
    if (com.dlb.chess.fen.FenPieceSymbol.exists(c) || com.dlb.chess.board.enums.File.exists(c)
        || com.dlb.chess.board.enums.Rank.exists(c) || com.dlb.chess.san.SanSymbol.exists(c)) {
      return true;
    }
    // Lenient additions: uppercase file letter (UPPERCASE_FILE_LETTER), uppercase capture marker
    // (UPPERCASE_CAPTURE_MARKER), digit zero (ZERO_INSTEAD_OF_O_CASTLING).
    return c >= 'A' && c <= 'H' || c == 'X' || c == '0';
  }

  private static void validateSanLength(String san) {
    if (san.length() < SAN_MIN_LENGTH || san.length() > SAN_MAX_LENGTH) {
      throw new LenientPgnParserValidationException(
          LenientPgnParserValidationProblem.EXCEPTION_CAUGHT_FROM_STRICT_VALIDATION, SanValidationProblem.NONE,
          "The movetext contains the SAN '" + san + "' with an invalid SAN length.");
    }
  }

  // -------------------------------------------------------------------------------------------------
  // Tag consistency checks
  // -------------------------------------------------------------------------------------------------

  /**
   * Validates that the Result tag value (if present) matches the movetext termination marker (if present). This is the
   * only result-related cross-check the lenient parser performs; both signals are preserved independently on the parse
   * model (Result tag on the tag list, termination marker on {@link PgnGame#terminationMarker()}).
   */
  private static void validateResultConsistency(List<Tag> tagList, @Nullable ResultTagValue terminationResult) {
    if (!TagUtility.hasResult(tagList) || terminationResult == null) {
      return;
    }
    final ResultTagValue fromTag = ResultTagValue.calculate(TagUtility.readResult(tagList));
    if (terminationResult != fromTag) {
      throw new LenientPgnParserValidationException(LenientPgnParserValidationProblem.TAG_RESULT_BOTH_SET_BUT_DIFFERENT,
          SanValidationProblem.NONE,
          "The result in the result tag and in the movetext must be the same. The result \"" + fromTag.getValue()
              + "\" was specified in the result tag, the result \"" + terminationResult.getValue()
              + "\" was specified in the movetext");
    }
  }

  // -------------------------------------------------------------------------------------------------
  // Tag-level forgiveness reporting
  // -------------------------------------------------------------------------------------------------

  /**
   * One {@link ForgivenTagItemCode#STR_TAG_MISSING} item per missing Seven Tag Roster entry, excluding Result (Result
   * has dedicated codes that also account for the termination-marker interaction).
   */
  private void recordMissingStrTagItems(List<Tag> tagList) {
    for (final StandardTag standardTag : TagUtility.SEVEN_TAG_ROSTER_TAG_LIST) {
      if (standardTag == StandardTag.RESULT) {
        continue;
      }
      if (!TagUtility.existsTag(tagList, standardTag)) {
        tagForgivenItemsAccumulator
            .add(new ForgivenTagItem(ForgivenTagItemCode.STR_TAG_MISSING, standardTag.getName(), ""));
      }
    }
  }

  /**
   * Result tag and termination-marker interaction. Emits {@code RESULT_TAG_MISSING_BUT_TERMINATION_MARKER_PRESENT} when
   * only the marker was given (with the marker value on {@code detail}), or
   * {@code RESULT_TAG_AND_TERMINATION_MARKER_BOTH_MISSING} when neither was given. When the Result tag is present,
   * nothing is recorded - the value is already in the tag list (and {@link #validateResultConsistency} has already
   * cross-checked it against the marker if both are present).
   */
  private void recordResultAndTerminationMarkerItems(List<Tag> tagList, @Nullable ResultTagValue terminationResult) {
    if (TagUtility.hasResult(tagList)) {
      return;
    }
    if (terminationResult != null) {
      tagForgivenItemsAccumulator
          .add(new ForgivenTagItem(ForgivenTagItemCode.RESULT_TAG_MISSING_BUT_TERMINATION_MARKER_PRESENT,
              StandardTag.RESULT.getName(), terminationResult.getValue()));
    } else {
      tagForgivenItemsAccumulator.add(new ForgivenTagItem(
          ForgivenTagItemCode.RESULT_TAG_AND_TERMINATION_MARKER_BOTH_MISSING, StandardTag.RESULT.getName(), ""));
    }
  }

  /**
   * SetUp/FEN coupling and redundancy. Three deviations possible:
   * <ul>
   * <li>FEN present, SetUp absent: {@code SETUP_TAG_MISSING_BUT_FEN_PRESENT}.</li>
   * <li>SetUp present, FEN absent: {@code SETUP_TAG_PRESENT_BUT_FEN_MISSING}.</li>
   * <li>FEN present and describes the initial position (redundant signal):
   * {@code REDUNDANT_FEN_AND_SETUP_FOR_INITIAL_POSITION}.</li>
   * </ul>
   * The first two cases are exclusive of each other. The third can fire alongside neither, since it requires FEN
   * presence - when it fires, the SetUp tag may or may not be present (both shapes are equally redundant).
   */
  private void recordSetUpFenCouplingItems(List<Tag> tagList, Fen startFen) {
    final boolean hasFen = TagUtility.hasFen(tagList);
    final boolean hasSetUp = TagUtility.hasSetUp(tagList);
    if (hasFen && !hasSetUp) {
      tagForgivenItemsAccumulator.add(
          new ForgivenTagItem(ForgivenTagItemCode.SETUP_TAG_MISSING_BUT_FEN_PRESENT, StandardTag.SET_UP.getName(), ""));
    } else if (!hasFen && hasSetUp) {
      tagForgivenItemsAccumulator.add(
          new ForgivenTagItem(ForgivenTagItemCode.SETUP_TAG_PRESENT_BUT_FEN_MISSING, StandardTag.FEN.getName(), ""));
    }
    if (hasFen && startFen.equals(FenConstants.FEN_INITIAL)) {
      tagForgivenItemsAccumulator.add(new ForgivenTagItem(
          ForgivenTagItemCode.REDUNDANT_FEN_AND_SETUP_FOR_INITIAL_POSITION, StandardTag.FEN.getName(), ""));
    }
  }

  // -------------------------------------------------------------------------------------------------
  // Whitespace skipping helpers
  // -------------------------------------------------------------------------------------------------

  /**
   * Consumes any run of {@link PgnTokenType#SPACES} and {@link PgnTokenType#NEWLINE} tokens at the current position.
   */
  private void skipInsignificantWhitespace() {
    while (true) {
      final PgnTokenType type = tokenizer.peek().type();
      if (type != PgnTokenType.SPACES && type != PgnTokenType.NEWLINE) {
        break;
      }
      tokenizer.next();
    }
  }

  /**
   * Consumes only {@link PgnTokenType#SPACES} at the current position. Used inside a single logical line.
   */
  private void skipInlineWhitespace() {
    while (tokenizer.peek().type() == PgnTokenType.SPACES) {
      tokenizer.next();
    }
  }

  // -------------------------------------------------------------------------------------------------
  // Board replay & post-processing
  // -------------------------------------------------------------------------------------------------

  /**
   * Replays each half-move on a fresh board via {@link Board#moveLenient}, accumulates SAN-level forgiven items into
   * the parser-instance accumulator, and returns a copy of the half-move list with the canonical SAN substituted (so
   * the resulting {@link PgnGame} compares equal to a strict-parsed file built from the same canonical moves).
   * Move-suffix annotations and commentaries are carried through unchanged.
   *
   * <p>
   * On a SAN-level failure the partial accumulator is included on the thrown
   * {@link LenientPgnParserValidationException} so callers see how many deviations were forgiven before the failure.
   */
  private List<PgnHalfMove> replayBoardCanonicalizing(Fen startFen, List<PgnHalfMove> halfMoveList) {
    final Board board = new Board(startFen);
    final List<PgnHalfMove> canonicalList = new ArrayList<>(halfMoveList.size());
    for (final PgnHalfMove halfMove : halfMoveList) {
      final Side side = board.getHavingMove();
      final int fullMoveNumber = board.getFullMoveNumber();
      try {
        final LenientSanParserValidationResult result = board.moveLenient(halfMove.san());
        sanForgivenItemsAccumulator.addAll(result.forgivenItems());
        final String canonicalSan = board.getSan();
        canonicalList.add(new PgnHalfMove(canonicalSan, halfMove.moveSuffixAnnotation(), halfMove.commentary()));
      } catch (final LenientSanParserValidationException e) {
        final String moveNumberAndSan = HalfMoveUtility.calculateMoveNumberAndSanWithSpace(fullMoveNumber, side,
            halfMove.san());
        final String messageSanValidationFailure = BasicUtility.getMessage(e);
        final String message = "The validation for " + moveNumberAndSan + " failed. Reason: "
            + messageSanValidationFailure;
        final SanValidationProblem underlying = e.getUnderlyingSanValidationProblem();
        throw new LenientPgnParserValidationException(LenientPgnParserValidationProblem.SAN,
            underlying == null ? SanValidationProblem.UNKNOWN_ERROR : underlying, message,
            Nulls.copyOfList(sanForgivenItemsAccumulator), Nulls.copyOfList(tagForgivenItemsAccumulator));
      }
    }
    return canonicalList;
  }

  private static Fen calculateStartFen(List<Tag> tagList, boolean isStartFromPosition) {
    final String startFenStr = isStartFromPosition ? TagUtility.readFen(tagList) : FenConstants.FEN_INITIAL_STR;
    // Lenient PGN parser routes the FEN tag through the lenient FEN parser too - symmetry with movetext leniency
    // means deficient FEN tags (extra whitespace, missing counters, speculative fullMoveNumber on a non-initial
    // position) parse cleanly. The lenient layer only forgives syntactic deviations; structural / rule-consistency
    // violations still propagate as FenAdvancedValidationException via LenientFenParserValidationException.
    return LenientFenParser.parseText(startFenStr);
  }

  // -------------------------------------------------------------------------------------------------
  // Exception builder
  // -------------------------------------------------------------------------------------------------

  private static LenientPgnParserValidationException tagFormatError(String message) {
    return new LenientPgnParserValidationException(LenientPgnParserValidationProblem.TAG_FORMAT_INVALID,
        SanValidationProblem.NONE, message);
  }

  private static LenientPgnParserValidationException tagReappearError() {
    return new LenientPgnParserValidationException(LenientPgnParserValidationProblem.TAG_REAPPEAR,
        SanValidationProblem.NONE, "After the movetext started, tags can no longer appear.");
  }

  private static boolean isAsciiLetterOrDigit(char c) {
    return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
  }
}
