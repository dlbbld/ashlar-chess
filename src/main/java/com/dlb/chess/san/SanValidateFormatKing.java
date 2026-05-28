package com.dlb.chess.san;

import com.dlb.chess.board.enums.File;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.messages.Message;

/**
 * Parses king-move SAN (core starts with 'K'). Castling (O-O / O-O-O) is handled separately in
 * {@link SanValidateFormat} and does not come through here.
 *
 * <p>
 * Recognised core patterns (trailing +/# already stripped):
 *
 * <pre>
 *   length 3: K[file][rank]   non-castling, non-capturing   e.g. Ke5
 *   length 4: Kx[file][rank]  non-castling, capturing       e.g. Kxe5
 * </pre>
 *
 * <p>
 * Parses the SAN character-by-character (sequential), analogous to {@link SanValidateFormatPawn}. Each failure is
 * reported with the most specific {@link SanValidationProblem} describing which character is missing or wrong.
 */
abstract class SanValidateFormatKing extends AbstractSan {

  static SanParse parseKingMove(final String core, final SanTerminalMarker sanTerminalMarker) {
    // core[0] == 'K' ensured by the dispatcher in SanValidateFormat

    // Capture path is determined by the second char being 'x'. Anything else (or absence) is treated as a
    // (malformed) non-capturing move, reported symmetrically with the capture-path destination-file codes.
    if (core.length() >= 2 && core.charAt(1) == 'x') {
      return parseKingCaptureMove(core, sanTerminalMarker);
    }

    // Non-capturing path: second char is the destination file letter.
    if (core.length() == 1) {
      throw new SanValidationException(SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_NO_DESTINATION_FILE,
          Message.getString("validation.san.format.king.nonCastling.nonCapture.noDestinationFile"));
    }

    final var secondChar = core.charAt(1);

    if (!SanValidateFormat.isFileLetter(secondChar)) {
      throw new SanValidationException(SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE,
          Message.getString("validation.san.format.king.nonCastling.nonCapture.wrongDestinationFile",
              Nulls.toString(secondChar)));
    }

    // K[file]... - non-capturing path continues with the destination rank
    return parseKingNonCaptureMove(core, sanTerminalMarker);
  }

  private static SanParse parseKingNonCaptureMove(final String core, final SanTerminalMarker sanTerminalMarker) {
    // core[0]='K', core[1]=file letter. Expect rank digit next.
    final var secondChar = core.charAt(1);

    if (core.length() == 2) {
      throw new SanValidationException(SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_NO_DESTINATION_RANK,
          Message.getString("validation.san.format.king.nonCastling.nonCapture.noDestinationRank"));
    }

    final var thirdChar = core.charAt(2);

    // Third char must be a rank digit. Anything else (another file letter, 'x', etc.) falls into the same generic
    // "wrong destination rank" bucket; detecting file-disambiguation specifically adds no user value beyond what
    // this message already says.
    if (!SanValidateFormat.isRankDigit(thirdChar)) {
      throw new SanValidationException(SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK,
          Message.getString("validation.san.format.king.nonCastling.nonCapture.wrongDestinationRank",
              Nulls.toString(thirdChar)));
    }

    // K[file][rank] - valid destination, length 3 expected
    if (core.length() == 3) {
      return new SanParse(SanFormat.KING_NON_CASTLING_NON_CAPTURING,
          new SanConversion(PieceType.KING, File.NONE, Rank.NONE,
              Square.calculate(SanValidateFormat.parseFile(secondChar), SanValidateFormat.parseRank(thirdChar)),
              PromotionPieceType.NONE, sanTerminalMarker));
    }

    // Length > 3 after valid K[file][rank] - overlength. Square-disambiguation attempts like "Ka2b3" also land
    // here;
    // the generic overlength message is sufficient since the user can see the move already has 4+ chars.
    throw new SanValidationException(SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_OVERLENGTH,
        Message.getString("validation.san.format.king.nonCastling.nonCapture.overlength"));
  }

  private static SanParse parseKingCaptureMove(final String core, final SanTerminalMarker sanTerminalMarker) {
    // core[0]='K', core[1]='x'. Expect [file][rank] next.

    // Third character (destination file)
    if (core.length() == 2) {
      throw new SanValidationException(SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_NO_DESTINATION_FILE,
          Message.getString("validation.san.format.king.nonCastling.capture.noDestinationFile"));
    }

    final var thirdChar = core.charAt(2);

    if (!SanValidateFormat.isFileLetter(thirdChar)) {
      throw new SanValidationException(SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_WRONG_DESTINATION_FILE,
          Message.getString("validation.san.format.king.nonCastling.capture.wrongDestinationFile",
              Nulls.toString(thirdChar)));
    }

    // Fourth character (destination rank)
    if (core.length() == 3) {
      throw new SanValidationException(SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_NO_DESTINATION_RANK,
          Message.getString("validation.san.format.king.nonCastling.capture.noDestinationRank"));
    }

    final var fourthChar = core.charAt(3);

    if (!SanValidateFormat.isRankDigit(fourthChar)) {
      throw new SanValidationException(SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_WRONG_DESTINATION_RANK,
          Message.getString("validation.san.format.king.nonCastling.capture.wrongDestinationRank",
              Nulls.toString(fourthChar)));
    }

    // Valid Kx[file][rank] - length 4 expected
    if (core.length() == 4) {
      return new SanParse(SanFormat.KING_NON_CASTLING_CAPTURING,
          new SanConversion(PieceType.KING, File.NONE, Rank.NONE,
              Square.calculate(SanValidateFormat.parseFile(thirdChar), SanValidateFormat.parseRank(fourthChar)),
              PromotionPieceType.NONE, sanTerminalMarker));
    }

    // Length > 4 after valid Kx[file][rank] - always overlength
    throw new SanValidationException(SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_OVERLENGTH,
        Message.getString("validation.san.format.king.nonCastling.capture.overlength"));
  }

}
