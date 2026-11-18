// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.messages.Message;

/**
 * Normalizes notation for strict parsing and semantic recovery, preserving the moving piece and any supplied origin.
 */
final class LenientSanShapeNormalize {

  private static final Pattern HYPHENATED_LAN = Nulls.compile("[KQRBN]?[a-h][1-8]-[a-h][1-8](?:=?[QRBNqrbn])?");

  private LenientSanShapeNormalize() {
  }

  static String normalize(String text, Board board, List<LenientSanValidationProblem> codes) {
    if (text.isBlank()) {
      // Defer to strict for the canonical "blank" diagnostic.
      return text;
    }

    final char lastChar = text.charAt(text.length() - 1);
    final boolean hasTerminalMarker = lastChar == '+' || lastChar == '#';
    final String terminalMarker = hasTerminalMarker ? Nulls.valueOf(lastChar) : "";
    String body = hasTerminalMarker ? Nulls.substring(text, 0, text.length() - 1) : text;

    if (matchesCastlingZeroOPattern(body)) {
      return normalizeCastling(body, text, terminalMarker, codes);
    }
    if (isShortBFilePawnCapture(body, board, text)) {
      codes.add(LenientSanValidationProblem.MISSING_CAPTURE_MARKER);
      body = "bx" + Nulls.substring(body, 1);
    }
    body = applyCaseFixups(body, codes);
    final boolean explicitPawn = body.length() >= 2 && body.charAt(0) == 'P' && isFileLetter(body.charAt(1));
    if (explicitPawn) {
      codes.add(LenientSanValidationProblem.EXPLICIT_PAWN_LETTER);
      body = Nulls.substring(body, 1);
    }
    body = handleLanOrUci(body, board, explicitPawn, codes);
    body = stripKingOriginSquare(body, board, codes);
    body = insertMissingPawnCaptureMarker(body, codes);
    body = insertMissingPromotionEquals(body, codes);

    return body + terminalMarker;
  }

  private static String normalizeCastling(String body, String text, String marker,
      List<LenientSanValidationProblem> codes) {
    if (body.indexOf('0') < 0) {
      return text;
    }
    if (body.indexOf('O') >= 0) {
      throw new LenientSanParserValidationException(
          Message.getString("validation.san.lenient.mixedCastlingZeroAndO", text), text, SanValidationProblem.NONE,
          ForgivenSanItem.NO_ITEMS);
    }
    codes.add(LenientSanValidationProblem.ZERO_INSTEAD_OF_O_CASTLING);
    return Nulls.replace(body, '0', 'O') + marker;
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
    if (piece != Piece.of(board.getSideToMove(), PieceType.KING)) {
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
  // LAN ("e2-e4", "e4xd5", "Nb1-d7") and UCI ("e2e4", "b1d7") use overlapping pure-string shapes; without a
  // separator, pawn LAN is identical to pawn UCI. We disambiguate by the separator between the two squares: a hyphen
  // or a capture marker means LAN (only LONG_ALGEBRAIC_NOTATION code); no separator means UCI shape (only
  // UCI_NOTATION code). Either branch may then translate to canonical SAN; the translation itself does not emit a
  // second code.

  private static String handleLanOrUci(String body, Board board, boolean explicitPawn,
      List<LenientSanValidationProblem> codes) {
    if (body.indexOf('-') >= 0) {
      if (!HYPHENATED_LAN.matcher(body).matches()) {
        return body;
      }
      final String dehyphenated = Nulls.replace(body, "-", "");
      codes.add(LenientSanValidationProblem.LONG_ALGEBRAIC_NOTATION);
      final String translated = tryTranslateCoordinateShape(dehyphenated, board, explicitPawn);
      return translated != null ? translated : dehyphenated;
    }
    if (!explicitPawn) {
      final String castling = tryTranslateUciCastling(body, board);
      if (castling != null) {
        codes.add(LenientSanValidationProblem.UCI_NOTATION);
        return castling;
      }
    }
    final String translated = tryTranslateCoordinateShape(body, board, explicitPawn);
    if (translated != null) {
      final boolean hasCaptureMarker = body.charAt(COORDINATE_SEPARATOR_INDEX) == 'x';
      codes.add(hasCaptureMarker ? LenientSanValidationProblem.LONG_ALGEBRAIC_NOTATION
          : LenientSanValidationProblem.UCI_NOTATION);
      return translated;
    }
    return body;
  }

  private static final int COORDINATE_SEPARATOR_INDEX = 2;

  /**
   * Translates a move given by its two squares and no piece letter to SAN using the piece on the from-square. The shape
   * is {@code <file><rank>[x]<file><rank>[[=]<promotion piece>]}, which covers UCI ({@code e2e4}, {@code e7e8q}) and
   * the pawn and letterless forms of long algebraic notation once the hyphen is removed ({@code e4xd5}, {@code e7e8=Q},
   * {@code b7xa8=N}). Returns {@code null} when the shape doesn't match - the caller decides whether that means "leave
   * the body unchanged" or "this branch doesn't apply."
   */
  private static @Nullable String tryTranslateCoordinateShape(String body, Board board, boolean explicitPawn) {
    if (body.length() < 4 || !isFileLetter(body.charAt(0)) || !isRankDigit(body.charAt(1))) {
      return null;
    }
    final boolean hasCaptureMarker = body.charAt(COORDINATE_SEPARATOR_INDEX) == 'x';
    final int toIndex = hasCaptureMarker ? COORDINATE_SEPARATOR_INDEX + 1 : COORDINATE_SEPARATOR_INDEX;
    if (body.length() < toIndex + 2 || !isFileLetter(body.charAt(toIndex)) || !isRankDigit(body.charAt(toIndex + 1))) {
      return null;
    }
    final String promotionSuffix = Nulls.substring(body, toIndex + 2);
    final boolean hasPromotionEquals = !promotionSuffix.isEmpty() && promotionSuffix.charAt(0) == '=';
    final String promotionLetter = hasPromotionEquals ? Nulls.substring(promotionSuffix, 1) : promotionSuffix;
    if (hasPromotionEquals && promotionLetter.isEmpty() || promotionLetter.length() > 1
        || promotionLetter.length() == 1 && !isPromotionPieceLetterAnyCase(promotionLetter.charAt(0))) {
      return null;
    }
    final String toSquareName = Nulls.substring(body, toIndex, toIndex + 2);
    final Square from = parseSquare(body.charAt(0), body.charAt(1));
    final Square to = parseSquare(body.charAt(toIndex), body.charAt(toIndex + 1));
    if (from == Square.NONE || to == Square.NONE || from == to) {
      return null;
    }
    final BitboardPosition position = board.getBitboardPosition();
    final Piece piece = position.get(from);
    if (piece == Piece.NONE) {
      return null;
    }
    final PieceType pieceType = piece.getPieceType();
    if (pieceType != PieceType.PAWN && (explicitPawn || !promotionLetter.isEmpty())) {
      return null;
    }

    final StringBuilder out = new StringBuilder();
    if (pieceType == PieceType.PAWN) {
      // Pawn SAN drops the origin rank, so first rule out resolving the coordinates to a different pawn.
      if (board.getLegalMoves().stream().noneMatch(
          move -> move.moveSpecification().fromSquare() == from && move.moveSpecification().toSquare() == to)) {
        return null;
      }
      // Pawn SAN: forward "<file><rank>", capture "<fromFile>x<toFile><toRank>", optional "=<P>"
      final boolean isCapture = body.charAt(0) != body.charAt(toIndex);
      if (hasCaptureMarker && !isCapture) {
        return null;
      }
      if (isCapture) {
        out.append(body.charAt(0)).append('x');
      }
      out.append(toSquareName);
      if (!promotionLetter.isEmpty()) {
        out.append('=').append(Character.toUpperCase(promotionLetter.charAt(0)));
      }
    } else {
      // Non-pawn SAN with full square disambiguation. Without a capture marker in the input the destination
      // capture-marker is unknown without walking destination semantics; let Phase 2 add it via
      // MISSING_CAPTURE_MARKER if needed.
      out.append(pieceType.getLetter());
      out.append(body.charAt(0)).append(body.charAt(1));
      if (hasCaptureMarker) {
        out.append('x');
      }
      out.append(toSquareName);
    }
    return Nulls.toString(out);
  }

  // The king is the one piece whose strict format has no room for an origin square, so the recovery phase has no
  // over-specification code to act on. A king move carrying the origin square - written that way ("Ke1e2", "Ke1xd2")
  // or produced by the LAN/UCI translation - is reduced here. An origin square that does not hold the moving king is
  // left for strict to reject.
  private static String stripKingOriginSquare(String body, Board board, List<LenientSanValidationProblem> codes) {
    if (body.length() < 5 || body.charAt(0) != 'K' || !isFileLetter(body.charAt(1)) || !isRankDigit(body.charAt(2))) {
      return body;
    }
    final String destination = Nulls.substring(body, 3);
    final String toSquareName = destination.charAt(0) == 'x' ? Nulls.substring(destination, 1) : destination;
    if (toSquareName.length() != 2 || !isFileLetter(toSquareName.charAt(0)) || !isRankDigit(toSquareName.charAt(1))) {
      return body;
    }
    final Square from = parseSquare(body.charAt(1), body.charAt(2));
    if (board.getBitboardPosition().get(from) != Piece.of(board.getSideToMove(), PieceType.KING)) {
      return body;
    }
    codes.add(LenientSanValidationProblem.OVERSPECIFIED_SQUARE_DISAMBIGUATION);
    return "K" + destination;
  }

  // --- Body transforms ---

  // A short "ba5" can name the b-pawn (missing x) or a bishop (lowercase B).
  private static boolean isShortBFilePawnCapture(String body, Board board, String text) {
    if (body.length() != 3 || body.charAt(0) != 'b' || !isAdjacentFile('b', Character.toLowerCase(body.charAt(1)))
        || !isRankDigit(body.charAt(2))) {
      return false;
    }
    final Square to = parseSquare(body.charAt(1), body.charAt(2));
    boolean pawn = false;
    boolean bishop = false;
    for (final LegalMove move : board.getLegalMoves()) {
      if (move.moveSpecification().toSquare() == to) {
        final PieceType piece = move.movingPiece().getPieceType();
        if (piece == PieceType.PAWN && !move.isPromotion()
            && move.moveSpecification().fromSquare().getFile() == File.FILE_B) {
          pawn = true;
        } else if (piece == PieceType.BISHOP) {
          bishop = true;
        }
      }
    }
    if (pawn && bishop) {
      throw new LenientSanParserValidationException(Message.getString("validation.san.lenient.ambiguousBFile", text),
          text, SanValidationProblem.NONE, ForgivenSanItem.NO_ITEMS);
    }
    return pawn;
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
    return caseFixLowercasePromotionPiece(Nulls.substring(body, 0, len - 1) + "=" + last, codes);
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
      if (body.length() >= 2 && isFileLetterAnyCase(body.charAt(1)) && !hasPromotionSuffix(body)) {
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
      // No move ends on a file letter, so an uppercase B closing the body is the bishop of an underpromotion.
      final boolean isPromotionPiecePosition = i == body.length() - 1;
      if (isUppercaseFileLetter(c) && !isPromotionPiecePosition) {
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
   * valid bishop SAN - i.e. a bare destination, a hyphen after the origin square, or a promotion suffix. Capture and
   * disambiguation forms (e.g. {@code Bxf7}, {@code B1d4}) are canonically bishop, even though they could lexically
   * also be uppercase b-file pawn forms; the case carries the user's intent and we respect it.
   */
  private static boolean isUppercaseBPawnOnlyShape(String body) {
    return body.length() >= 2 && isRankDigit(body.charAt(1)) && (body.length() == 2 || body.charAt(2) == '-')
        || hasPromotionSuffix(body);
  }

  private static boolean hasPromotionSuffix(String body) {
    final int piece = body.length() - 1;
    if (piece < 2 || !isPromotionPieceLetterAnyCase(body.charAt(piece))) {
      return false;
    }
    final int rank = body.charAt(piece - 1) == '=' ? piece - 2 : piece - 1;
    return body.charAt(rank) == '1' || body.charAt(rank) == '8';
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
