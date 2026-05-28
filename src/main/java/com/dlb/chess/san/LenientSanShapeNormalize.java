package com.dlb.chess.san;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.messages.Message;

/**
 * Phase 1 of the lenient pipeline: pure-string shape normalization plus board-aware UCI translation. Takes the raw user
 * input and produces a candidate SAN string in (or close to) canonical form, accumulating one
 * {@link LenientSanValidationProblem} per detected deviation. The candidate is then handed to Phase 2
 * ({@link LenientSanRecover}) for board-aware semantic recovery.
 *
 * <p>
 * Order of normalization is significant - earlier steps assume later steps have not yet run. The order is:
 * <ol>
 * <li>Castling shape (zero-vs-O, mixed-rejection, UCI castling)
 * <li>Strip terminal marker for body-only processing, re-attach at the end
 * <li>UCI piece-move translation (board-aware lookup of the piece on the from-square)
 * <li>Body transforms: explicit-P strip, LAN hyphen strip, missing promotion-equals insert
 * <li>Case fixups (lowercase piece letter, uppercase file letter, uppercase capture marker, lowercase promotion piece)
 * </ol>
 */
final class LenientSanShapeNormalize {

  private LenientSanShapeNormalize() {
  }

  static String normalize(String text, Board board, List<LenientSanValidationProblem> codes) {
    if (text.isBlank()) {
      // Defer to strict for the canonical "blank" diagnostic.
      return text;
    }

    final String castlingNormalized = tryNormalizeCastling(text, board, codes);
    if (castlingNormalized != null) {
      return castlingNormalized;
    }

    final char lastChar = text.charAt(text.length() - 1);
    final boolean hasTerminalMarker = lastChar == '+' || lastChar == '#';
    final String terminalMarker = hasTerminalMarker ? Nulls.valueOf(lastChar) : "";
      String body = hasTerminalMarker ? Nulls.substring(text, 0, text.length() - 1) : text;

    body = stripExplicitPawnLetter(body, codes);
    // Case fixes run early so downstream steps (UCI/LAN translation, pawn-missing-x, promotion-equals)
    // see lowercase-normalized files and can match shape unambiguously.
    body = applyCaseFixups(body, codes);
    body = handleLanOrUci(body, board, codes);
    body = insertMissingPawnCaptureMarker(body, codes);
    body = insertMissingPromotionEquals(body, codes);

    return body + terminalMarker;
  }

  // --- Castling normalization (returns null if the input doesn't match a castling shape) ---

  private static @Nullable String tryNormalizeCastling(String text, Board board,
      List<LenientSanValidationProblem> codes) {
    final char lastChar = text.charAt(text.length() - 1);
    final boolean hasTerminalMarker = lastChar == '+' || lastChar == '#';
    final String marker = hasTerminalMarker ? Nulls.valueOf(lastChar) : "";
    final String body = hasTerminalMarker ? Nulls.substring(text, 0, text.length() - 1) : text;

    // Classic O-O / 0-0 / mixed forms. Three or five characters with hyphens and zero-or-O at every other position.
    if (matchesCastlingZeroOPattern(body)) {
      final boolean hasO = body.indexOf('O') >= 0;
      final boolean hasZero = body.indexOf('0') >= 0;
      if (hasO && hasZero) {
        throw new LenientSanParserValidationException(
            Message.getString("validation.san.lenient.mixedCastlingZeroAndO", text), text, null,
            ForgivenItem.EMPTY_LIST);
      }
      if (hasZero) {
        codes.add(LenientSanValidationProblem.ZERO_INSTEAD_OF_O_CASTLING);
        return Nulls.replace(body, '0', 'O') + marker;
      }
      // All-O: already canonical (or canonical with marker) - let strict handle it.
      return text;
    }

    // UCI king-castling: e1g1 / e1c1 / e8g8 / e8c8 with the king actually on the e-file home square.
    final String uciCastling = tryTranslateUciCastling(body, board);
    if (uciCastling != null) {
      codes.add(LenientSanValidationProblem.UCI_NOTATION);
      return uciCastling + marker;
    }

    return null;
  }

  private static boolean matchesCastlingZeroOPattern(String body) {
    if (body.length() == 3) {
      return isZeroOrO(body.charAt(0)) && body.charAt(1) == '-' && isZeroOrO(body.charAt(2));
    }
    if (body.length() == 5) {
      return isZeroOrO(body.charAt(0)) && body.charAt(1) == '-' && isZeroOrO(body.charAt(2)) && body.charAt(3) == '-'
          && isZeroOrO(body.charAt(4));
    }
    return false;
  }

  private static boolean isZeroOrO(char c) {
    return c == '0' || c == 'O';
  }

  private static @Nullable String tryTranslateUciCastling(String body, Board board) {
    if (body.length() != 4) {
      return null;
    }
    if (!isFileLetter(body.charAt(0)) || !isRankDigit(body.charAt(1)) || !isFileLetter(body.charAt(2))
        || !isRankDigit(body.charAt(3))) {
      return null;
    }
    final Square from = parseSquare(body.charAt(0), body.charAt(1));
    final Square to = parseSquare(body.charAt(2), body.charAt(3));
    if (from == Square.NONE || to == Square.NONE) {
      return null;
    }
    final BitboardPosition position = board.getBitboardPosition();
    final Piece piece = position.get(from);
    if (piece == Piece.NONE || piece.getPieceType() != PieceType.KING) {
      return null;
    }
    if (from == Square.E1 && to == Square.G1) {
      return "O-O";
    }
    if (from == Square.E1 && to == Square.C1) {
      return "O-O-O";
    }
    if (from == Square.E8 && to == Square.G8) {
      return "O-O";
    }
    if (from == Square.E8 && to == Square.C8) {
      return "O-O-O";
    }
    return null;
  }

  // --- LAN / UCI handling (mutually exclusive at the input level) ---
  //
  // LAN-with-hyphen ("e2-e4", "Nb1-d7") and UCI ("e2e4", "b1d7") use overlapping pure-string shapes; without
  // the hyphen, pawn LAN is identical to pawn UCI. We disambiguate by the presence of a hyphen in the input:
  // a hyphen means LAN (only LONG_ALGEBRAIC_NOTATION code); no hyphen means UCI shape (only UCI_NOTATION code).
  // Either branch may then translate to canonical SAN; the translation itself does not emit a second code.

  private static String handleLanOrUci(String body, Board board, List<LenientSanValidationProblem> codes) {
    if (body.indexOf('-') >= 0) {
      final String dehyphenated = Nulls.replace(body, "-", "");
      codes.add(LenientSanValidationProblem.LONG_ALGEBRAIC_NOTATION);
      final String translated = tryTranslateUciShape(dehyphenated, board);
      return translated != null ? translated : dehyphenated;
    }
    final String translated = tryTranslateUciShape(body, board);
    if (translated != null) {
      codes.add(LenientSanValidationProblem.UCI_NOTATION);
      return translated;
    }
    return body;
  }

  /**
   * Translates a UCI-shape string (4 or 5 chars, no piece letter, file/rank/file/rank/optional-promotion) to canonical
   * SAN using the piece on the from-square. Returns {@code null} when the shape doesn't match - the caller decides
   * whether that means "leave the body unchanged" or "this branch doesn't apply."
   */
  private static @Nullable String tryTranslateUciShape(String body, Board board) {
    if (body.length() != 4 && body.length() != 5) {
      return null;
    }
    if (!isFileLetter(body.charAt(0)) || !isRankDigit(body.charAt(1)) || !isFileLetter(body.charAt(2))
        || !isRankDigit(body.charAt(3))) {
      return null;
    }
    if (body.length() == 5 && !isPromotionPieceLetterAnyCase(body.charAt(4))) {
      return null;
    }
    final Square from = parseSquare(body.charAt(0), body.charAt(1));
    final Square to = parseSquare(body.charAt(2), body.charAt(3));
    if (from == Square.NONE || to == Square.NONE || from == to) {
      return null;
    }
    final BitboardPosition position = board.getBitboardPosition();
    final Piece piece = position.get(from);
    if (piece == Piece.NONE) {
      return null;
    }
    final PieceType pieceType = piece.getPieceType();

    final StringBuilder out = new StringBuilder();
    if (pieceType == PieceType.PAWN) {
      // Pawn SAN: forward "<file><rank>", capture "<fromFile>x<toFile><toRank>", optional "=<P>"
      final boolean isCapture = body.charAt(0) != body.charAt(2);
      if (isCapture) {
        out.append(body.charAt(0)).append('x').append(body.charAt(2)).append(body.charAt(3));
      } else {
        out.append(body.charAt(2)).append(body.charAt(3));
      }
      if (body.length() == 5) {
        out.append('=').append(Character.toUpperCase(body.charAt(4)));
      }
    } else {
      // Non-pawn SAN with full square disambiguation. The destination capture-marker is unknown without
      // walking destination semantics; let Phase 2 add it via MISSING_CAPTURE_MARKER if needed.
      out.append(pieceType.getLetter());
      out.append(body.charAt(0)).append(body.charAt(1));
      out.append(body.charAt(2)).append(body.charAt(3));
    }
    return Nulls.toString(out);
  }

  // --- Body transforms ---

  private static String stripExplicitPawnLetter(String body, List<LenientSanValidationProblem> codes) {
    if (body.length() < 2 || body.charAt(0) != 'P' || !isFileLetter(body.charAt(1))) {
      return body;
    }
    codes.add(LenientSanValidationProblem.EXPLICIT_PAWN_LETTER);
    return Nulls.substring(body, 1);
  }

  private static String insertMissingPromotionEquals(String body, List<LenientSanValidationProblem> codes) {
    final int len = body.length();
    if (len < 3) {
      return body;
    }
    final char last = body.charAt(len - 1);
    if (!isPromotionPieceLetterAnyCase(last)) {
      return body;
    }
    final char prev = body.charAt(len - 2);
    if (prev != '1' && prev != '8' || body.charAt(len - 3) == '=') {
      return body;
    }
    codes.add(LenientSanValidationProblem.MISSING_PROMOTION_EQUALS);
    return Nulls.substring(body, 0, len - 1) + "=" + last;
  }

  private static String applyCaseFixups(String body, List<LenientSanValidationProblem> codes) {
    String result = body;
    result = caseFixUppercaseCaptureMarker(result, codes);
    result = caseFixLowercasePromotionPiece(result, codes);
    result = caseFixLowercasePieceLetter(result, codes);
    result = caseFixUppercaseFileLetters(result, codes);
    return result;
  }

  private static String caseFixUppercaseCaptureMarker(String body, List<LenientSanValidationProblem> codes) {
    if (body.indexOf('X') < 0) {
      return body;
    }
    codes.add(LenientSanValidationProblem.UPPERCASE_CAPTURE_MARKER);
    return Nulls.replace(body, 'X', 'x');
  }

  private static String caseFixLowercasePromotionPiece(String body, List<LenientSanValidationProblem> codes) {
    final int eq = body.lastIndexOf('=');
    if (eq < 0 || eq != body.length() - 2) {
      return body;
    }
    final char piece = body.charAt(eq + 1);
    if (piece == 'r' || piece == 'n' || piece == 'b' || piece == 'q') {
      codes.add(LenientSanValidationProblem.LOWERCASE_PROMOTION_PIECE);
      return Nulls.substring(body, 0, eq + 1) + Character.toUpperCase(piece);
    }
    return body;
  }

  private static String caseFixLowercasePieceLetter(String body, List<LenientSanValidationProblem> codes) {
    if (body.isEmpty()) {
      return body;
    }
    final char first = body.charAt(0);
    if (first == 'n' || first == 'r' || first == 'q' || first == 'k') {
      codes.add(LenientSanValidationProblem.LOWERCASE_PIECE_LETTER);
      return Character.toUpperCase(first) + Nulls.substring(body, 1);
    }
    if (first == 'b') {
      // Lowercase 'b' is canonically a file letter (pawn move from b-file) or the file leader of a UCI / LAN
      // form (e.g. "b1c3"). Two shapes point at a lowercase bishop letter:
      //
      // (a) <piece><file>... - position 1 is a file letter (e.g. "bf3" = bishop to f3, "bdxe5" = bishop with
      // file disambig and capture). Pawn moves never have a file letter at position 1.
      // (b) bx<file><rank> with non-adjacent files - pawn captures must be diagonal, so b-pawn can only
      // capture on a- or c-file. If the capture destination file is not adjacent to b, the pawn
      // interpretation is geometrically illegal regardless of board state, and the user must mean a
      // lowercase bishop capture (e.g. "bxf7" - bishop on c4 captures on f7).
      if (body.length() >= 2 && isFileLetterAnyCase(body.charAt(1))) {
        codes.add(LenientSanValidationProblem.LOWERCASE_PIECE_LETTER);
        return "B" + Nulls.substring(body, 1);
      }
      if (body.length() >= 4 && body.charAt(1) == 'x' && isFileLetterAnyCase(body.charAt(2))
          && !isAdjacentFile('b', Character.toLowerCase(body.charAt(2)))) {
        codes.add(LenientSanValidationProblem.LOWERCASE_PIECE_LETTER);
        return "B" + Nulls.substring(body, 1);
      }
    }
    return body;
  }

  private static boolean isAdjacentFile(char a, char b) {
    return Math.abs(a - b) == 1;
  }

  private static String caseFixUppercaseFileLetters(String body, List<LenientSanValidationProblem> codes) {
    final StringBuilder out = new StringBuilder(body.length());
      boolean changed = false;
    for (int i = 0; i < body.length(); i++) {
      final char c = body.charAt(i);
      if (i == 0) {
        // Position 0 may be a piece letter (P, R, N, B, Q, K) OR a file letter for pawn / UCI / LAN forms.
        // A, C, D, E, F, G, H are unambiguously file letters (no piece uses these). B is ambiguous (file b
        // vs bishop) - only fold to file when the body shape is pawn-compatible.
        if (c >= 'A' && c <= 'H' && c != 'B') {
          out.append(Character.toLowerCase(c));
          changed = true;
          continue;
        }
        if (c == 'B' && isUppercaseBPawnOnlyShape(body)) {
          out.append('b');
          changed = true;
          continue;
        }
        out.append(c);
        continue;
      }
      if (isUppercaseFileLetter(c)) {
        out.append(Character.toLowerCase(c));
        changed = true;
      } else {
        out.append(c);
      }
    }
    if (changed) {
      codes.add(LenientSanValidationProblem.UPPERCASE_FILE_LETTER);
      return Nulls.toString(out);
    }
    return body;
  }

  /**
   * Returns true when {@code body} starts with uppercase {@code B} but the rest of the shape is incompatible with any
   * valid bishop SAN - i.e. only the b-file pawn interpretation makes sense. Used to decide whether an uppercase
   * {@code B} at position 0 should be lowercased. The shapes that pass here are:
   * <ul>
   * <li>{@code B<rank>} (length 2) - bishop has no length-2 SAN form
   * <li>{@code B<rank>=<piece>} (length 4 promotion) - bishops don't promote
   * <li>{@code Bx<file><rank><piece>} (length 5 capture promotion missing {@code =}) - bishops don't promote
   * <li>{@code Bx<file><rank>=<piece>} (length 6 capture promotion) - bishops don't promote
   * </ul>
   * Capture and disambiguation forms (e.g. {@code Bxf7}, {@code B1d4}) are canonically bishop, even though they could
   * lexically also be uppercase b-file pawn forms; the case carries the user's intent and we respect it.
   */
  private static boolean isUppercaseBPawnOnlyShape(String body) {
    if (body.length() == 2) {
      return isRankDigit(body.charAt(1));
    }
    if (body.length() == 4) {
      return isRankDigit(body.charAt(1)) && body.charAt(2) == '=' && isPromotionPieceLetterAnyCase(body.charAt(3));
    }
    if (body.length() == 5) {
      // Bx<file><rank><piece>: capture promotion missing the '=' marker - bishops don't promote, so this is
      // unambiguously an uppercase b-file pawn capture promotion.
      return body.charAt(1) == 'x' && isFileLetterAnyCase(body.charAt(2))
          && (body.charAt(3) == '1' || body.charAt(3) == '8') && isPromotionPieceLetterAnyCase(body.charAt(4));
    }
    if (body.length() == 6) {
      return body.charAt(1) == 'x' && isFileLetterAnyCase(body.charAt(2)) && isRankDigit(body.charAt(3))
          && body.charAt(4) == '=' && isPromotionPieceLetterAnyCase(body.charAt(5));
    }
    return false;
  }

  private static boolean isFileLetterAnyCase(char c) {
    return c >= 'a' && c <= 'h' || c >= 'A' && c <= 'H';
  }

  /**
   * Detects pawn captures written without the {@code x} marker: {@code <file><file><rank>} (3 chars, e.g. {@code ed5}),
   * {@code <file><file><rank>=<piece>} (5 chars, e.g. {@code ed8=Q}), or {@code <file><file><rank><piece>} (4 chars
   * with rank 1/8, e.g. {@code ed8Q} - capture promotion missing both x and =). Inserts {@code x} at position 1 and
   * emits {@link LenientSanValidationProblem#MISSING_CAPTURE_MARKER}.
   */
  private static String insertMissingPawnCaptureMarker(String body, List<LenientSanValidationProblem> codes) {
    if (body.length() == 3 && isFileLetter(body.charAt(0)) && isFileLetter(body.charAt(1))
        && isRankDigit(body.charAt(2))) {
      codes.add(LenientSanValidationProblem.MISSING_CAPTURE_MARKER);
      return body.charAt(0) + "x" + Nulls.substring(body, 1);
    }
    if (body.length() == 4 && isFileLetter(body.charAt(0)) && isFileLetter(body.charAt(1))
        && (body.charAt(2) == '1' || body.charAt(2) == '8') && isPromotionPieceLetterAnyCase(body.charAt(3))) {
      codes.add(LenientSanValidationProblem.MISSING_CAPTURE_MARKER);
      return body.charAt(0) + "x" + Nulls.substring(body, 1);
    }
    if (body.length() == 5 && isFileLetter(body.charAt(0)) && isFileLetter(body.charAt(1))
        && isRankDigit(body.charAt(2)) && body.charAt(3) == '=' && isPromotionPieceLetterAnyCase(body.charAt(4))) {
      codes.add(LenientSanValidationProblem.MISSING_CAPTURE_MARKER);
      return body.charAt(0) + "x" + Nulls.substring(body, 1);
    }
    return body;
  }

  // --- Character helpers ---

  private static boolean isFileLetter(char c) {
    return c >= 'a' && c <= 'h';
  }

  private static boolean isRankDigit(char c) {
    return c >= '1' && c <= '8';
  }

  private static boolean isUppercaseFileLetter(char c) {
    return c >= 'A' && c <= 'H';
  }

  private static boolean isPromotionPieceLetterAnyCase(char c) {
    return c == 'R' || c == 'N' || c == 'B' || c == 'Q' || c == 'r' || c == 'n' || c == 'b' || c == 'q';
  }

  private static Square parseSquare(char fileLetter, char rankDigit) {
    try {
      return Square.valueOf(Nulls.toUpperCase("" + fileLetter + rankDigit));
    } catch (final IllegalArgumentException e) {
      throw new ProgrammingMistakeException("Square.valueOf failed for validated file/rank chars", e);
    }
  }
}
