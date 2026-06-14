// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.fen;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PieceUtility;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.enums.SquareType;
import io.github.dlbbld.ashlarchess.board.enums.SquareUtility;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.BasicConstants;
import io.github.dlbbld.ashlarchess.common.constants.ChessConstants;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.enums.FenAdvancedValidationProblem;
import io.github.dlbbld.ashlarchess.common.exceptions.FenAdvancedValidationException;
import io.github.dlbbld.ashlarchess.common.exceptions.FenRawValidationException;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.utility.BasicUtility;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.fen.model.FenRaw;
import io.github.dlbbld.ashlarchess.model.CastlingRightBoth;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;

public class FenParserAdvanced implements EnumConstants {

  private static final String REG_EXP_EMPTY_RANK = "//";
  @SuppressWarnings("null")
  private static final Pattern PATTERN_EMPTY_RANK = Pattern.compile(REG_EXP_EMPTY_RANK);

  private static final String REG_EXP_RANK = "^[RNBQKPrnbqkp12345678]+$";
  @SuppressWarnings("null")
  private static final Pattern PATTERN_RANK = Pattern.compile(REG_EXP_RANK);

  private FenParserAdvanced() {
  }

  public static Fen parseFenAdvanced(String fen) throws FenAdvancedValidationException {
    final FenRaw fenRaw;
    try {
      fenRaw = FenParserRaw.parseFenRaw(fen);
    } catch (final FenRawValidationException e) {
      final String message = BasicUtility.getMessage(e);
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_FORMAT, message);
    }

    final String piecePlacement = fenRaw.piecePlacement();
    final BitboardPosition bitboardPosition = validatePiecePlacement(piecePlacement);
    validateNumberOfPieces(bitboardPosition);

    validatePawnRankNotPromotionRank(bitboardPosition);

    validatePawnRankNotGroundRank(bitboardPosition);

    final String havingMoveCheck = fenRaw.havingMove();
    final Side havingMove = validateHavingMove(havingMoveCheck);

    if (bitboardPosition.isInCheck(havingMove.getOppositeSide())) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_POSITION_CHECK,
          "the king of the opposing player is in check");
    }

    final String castlingRightBothStr = fenRaw.castlingRightBothStr();
    final CastlingRightBoth castlingRightBoth = validateCastlingRightBoth(bitboardPosition, castlingRightBothStr);

    final String enPassantCaptureTargetSquareStr = fenRaw.enPassantCaptureTargetSquare();
    final Square enPassantCaptureTargetSquare = validateEnPassantCaptureTargetSquare(bitboardPosition,
        enPassantCaptureTargetSquareStr, havingMove);

    final String halfMoveClockStr = fenRaw.halfMoveClock();
    final int halfMoveClock = validateHalfMoveClock(halfMoveClockStr, enPassantCaptureTargetSquare);

    final String fullMoveNumberStr = fenRaw.fullMoveNumber();
    final int fullMoveNumber = validateFullMoveNumber(fullMoveNumberStr);

    validateHalfMoveClockAgainstFullMoveNumber(halfMoveClock, fullMoveNumber, havingMove);

    return new Fen(fen, bitboardPosition, havingMove, castlingRightBoth.castlingRightWhite(),
        castlingRightBoth.castlingRightBlack(), enPassantCaptureTargetSquare, halfMoveClock, fullMoveNumber);
  }

  /**
   * Halfmove clock cannot exceed the maximum number of halfmoves that have been played by the start of the given
   * fullmove number. With {@code havingMove == WHITE} the maximum is {@code 2 * (fullMoveNumber - 1)}; with
   * {@code havingMove == BLACK} the count includes White's halfmove on the current fullmove number, so the maximum is
   * {@code 2 * (fullMoveNumber - 1) + 1}. Violations are physical impossibilities - a FEN like {@code ... 15 1} (15
   * halfmoves played, claiming move 1) cannot arise from a real game. The lenient FEN parser auto-corrects this by
   * bumping {@code fullMoveNumber} up to {@code halfMoveClock} rounded up to the next multiple of ten (a generous
   * reserve over the strict minimum; the round-numbered value signals a reconstructed placeholder) and surfaces the
   * deviation via {@code ForgivenFenItemCode.HALF_MOVE_CLOCK_INCONSISTENT_WITH_FULL_MOVE_NUMBER}.
   */
  private static void validateHalfMoveClockAgainstFullMoveNumber(int halfMoveClock, int fullMoveNumber, Side havingMove)
      throws FenAdvancedValidationException {
    final int maximumPossibleHalfMoveClock = 2 * (fullMoveNumber - 1) + (havingMove == BLACK ? 1 : 0);
    if (halfMoveClock > maximumPossibleHalfMoveClock) {
      throw new FenAdvancedValidationException(
          FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_TOO_BIG_RELATIVE_TO_FULL_MOVE_NUMBER,
          "the halfmove clock \"" + halfMoveClock + "\" is greater than the maximum possible halfmove clock \""
              + maximumPossibleHalfMoveClock + "\" for the specified fullmove number of " + fullMoveNumber);
    }
  }

  // important semantically
  // we only check if the string is correct regarding format
  // we do not validate if the position actually makes sense, like two kings of
  // same color or no king or both kings in
  // check, too much pawns etc.
  public static BitboardPosition validatePiecePlacement(String piecePlacement) throws FenAdvancedValidationException {

    if (piecePlacement.endsWith("/")) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_POSITION_ENDS_WITH_FORWARD_SLASH,
          "it ends with a slash");
    }

    // we check for empty ranks before the rank count, so we can avoid counting
    // empty ranks
    final Matcher matcherEmptyRank = PATTERN_EMPTY_RANK.matcher(piecePlacement);
    if (matcherEmptyRank.find()) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_POSITION_EMPTY_RANK,
          "it contains empty ranks");
    }

    final String[] rankDescriptionList = piecePlacement.split("/");

    if (rankDescriptionList.length != 8) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_POSITION_NUMBER_OF_RANKS,
          "it does not specify eight ranks");
    }

    int rankNumber = 0;
    for (final String rankDescription : rankDescriptionList) {
      rankNumber++;
      final Matcher matcherRank = PATTERN_RANK.matcher(rankDescription);
      if (!matcherRank.find()) {
        throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_POSITION_UNKNOWN_CHAR,
            "the rank " + rankNumber + " contains invalid chars");
      }
    }

    final List<List<String>> evaluatedRankList = new ArrayList<>();
    for (final String rankDescription : rankDescriptionList) {
      @SuppressWarnings("null") @NonNull final String rankDescriptionNonNull = rankDescription;
      final List<String> evaluatedRank = validateEvaluatedLength(rankDescriptionNonNull);
      evaluatedRankList.add(evaluatedRank);
    }

    final List<Piece> pieceList = new ArrayList<>();
    for (final List<String> evaluatedRank : evaluatedRankList) {
      pieceList.addAll(convertRankDescriptionEvaluatedToRank(evaluatedRank));
    }

    if (pieceList.size() != 64) {
      throw new ProgrammingMistakeException("The piece list construction is incorrect");
    }

    // pieceList is indexed in FEN reading order (rank 8 first, then rank 7, ..., rank 1). Within each rank,
    // file a..h. Convert to bitboard: square ordinal in little-endian rank-file order is rank*8 + file with
    // rank 0..7 (rank 1 = 0). For pieceList index i: rank-from-top = i / 8 (0 = rank 8), file = i % 8 (0 = a).
    // So square ordinal = (7 - i/8) * 8 + (i % 8) = (7 - i / 8) * 8 + i % 8.
    long whitePawns = 0L;
    long whiteRooks = 0L;
    long whiteKnights = 0L;
    long whiteBishops = 0L;
    long whiteQueens = 0L;
    long whiteKings = 0L;
    long blackPawns = 0L;
    long blackRooks = 0L;
    long blackKnights = 0L;
    long blackBishops = 0L;
    long blackQueens = 0L;
    long blackKings = 0L;
    for (int i = 0; i < 64; i++) {
      final Piece piece = Nulls.get(pieceList, i);
      if (piece == Piece.NONE) {
        continue;
      }
      final int squareOrdinal = (7 - i / 8) * 8 + i % 8;
      final long bit = 1L << squareOrdinal;
      switch (piece) {
        case WHITE_PAWN -> whitePawns |= bit;
        case WHITE_ROOK -> whiteRooks |= bit;
        case WHITE_KNIGHT -> whiteKnights |= bit;
        case WHITE_BISHOP -> whiteBishops |= bit;
        case WHITE_QUEEN -> whiteQueens |= bit;
        case WHITE_KING -> whiteKings |= bit;
        case BLACK_PAWN -> blackPawns |= bit;
        case BLACK_ROOK -> blackRooks |= bit;
        case BLACK_KNIGHT -> blackKnights |= bit;
        case BLACK_BISHOP -> blackBishops |= bit;
        case BLACK_QUEEN -> blackQueens |= bit;
        case BLACK_KING -> blackKings |= bit;
        case NONE -> throw new IllegalStateException("unreachable");
        default -> throw new IllegalArgumentException();
      }
    }
    return new BitboardPosition(whitePawns, whiteRooks, whiteKnights, whiteBishops, whiteQueens, whiteKings, blackPawns,
        blackRooks, blackKnights, blackBishops, blackQueens, blackKings);
  }

  private static List<Piece> convertRankDescriptionEvaluatedToRank(List<String> rankDescriptionEvaluated) {
    if (rankDescriptionEvaluated.size() != 8) {
      throw new ProgrammingMistakeException("The rank description evaluated must consist of exactly eight characters");
    }
    final List<Piece> rankPieceList = new ArrayList<>();
    for (final String letter : rankDescriptionEvaluated) {
      if (BasicConstants.BLANK.equals(letter)) {
        rankPieceList.add(Piece.NONE);
      } else {
        final char letterChar = letter.charAt(0);
        if (!FenPieceSymbol.exists(letterChar)) {
          throw new ProgrammingMistakeException(
              "An unknown piece was found which was not filtered before by regular expression");
        }
        final Piece piece = FenPieceSymbol.calculate(letterChar).piece();
        rankPieceList.add(piece);
      }
    }

    // post condition
    if (rankPieceList.size() != 8) {
      throw new ProgrammingMistakeException("Post condition of eight elements for rank evaluation not met");
    }

    return rankPieceList;

  }

  private static List<String> validateEvaluatedLength(String rankDescription) throws FenAdvancedValidationException {
    final List<String> squareDescriptionList = new ArrayList<>();

    int countEvaluatedLength = 0;

    for (int i = 0; i < rankDescription.length(); i++) {
      final String currentChar = Nulls.substring(rankDescription, i, i + 1);
      try {
        final int numberOfEmptyFields = Integer.parseInt(currentChar);
        countEvaluatedLength += numberOfEmptyFields;
        for (int j = 1; j <= numberOfEmptyFields; j++) {
          squareDescriptionList.add(BasicConstants.BLANK);
        }
      } catch (@SuppressWarnings("unused") final NumberFormatException e) {
        countEvaluatedLength++;
        squareDescriptionList.add(currentChar);
      }

    }
    if (countEvaluatedLength != 8) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_POSITION_LINE_EVALUATION_LENGTH,
          "the rank description \"" + rankDescription + "\" for the position does not evaluate to eight squares");
    }

    // post condition
    if (squareDescriptionList.size() != 8) {
      throw new ProgrammingMistakeException("Post condition of eight elements for square description not met");
    }
    return squareDescriptionList;
  }

  private static Side validateHavingMove(String havingMove) throws FenAdvancedValidationException {
    if (havingMove.length() != 1 || !FenSideSymbol.exists(havingMove.charAt(0))) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_HAVING_MOVE_RANGE,
          "the having move part of \"" + havingMove + "\" is not valid");
    }
    return FenSideSymbol.calculate(havingMove.charAt(0)).side();
  }

  private static CastlingRightBoth validateCastlingRightBoth(BitboardPosition bitboardPosition,
      String castlingRightBothStr) throws FenAdvancedValidationException {
    final CastlingRightBoth castlingRightBoth = validateCastlingRightBoth(castlingRightBothStr);
    validateCastlingRightAgainstBitboardPosition(bitboardPosition, castlingRightBoth);
    return castlingRightBoth;
  }

  private static CastlingRightBoth validateCastlingRightBoth(String castlingRightBothStr)
      throws FenAdvancedValidationException {

    final boolean hasK = castlingRightBothStr.contains("K");
    final boolean hasQ = castlingRightBothStr.contains("Q");
    final boolean hask = castlingRightBothStr.contains("k");
    final boolean hasq = castlingRightBothStr.contains("q");

    final String expected = (hasK ? "K" : "") + (hasQ ? "Q" : "") + (hask ? "k" : "") + (hasq ? "q" : "");
    if (expected.isEmpty() && !"-".equals(castlingRightBothStr)
        || !expected.isEmpty() && !expected.equals(castlingRightBothStr)) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_RANGE,
          "the castling right part of \"" + castlingRightBothStr + "\" is not valid");
    }

    final CastlingRight white = hasK && hasQ ? CastlingRight.KING_AND_QUEEN_SIDE
        : hasK ? CastlingRight.KING_SIDE : hasQ ? CastlingRight.QUEEN_SIDE : CastlingRight.NONE;
    final CastlingRight black = hask && hasq ? CastlingRight.KING_AND_QUEEN_SIDE
        : hask ? CastlingRight.KING_SIDE : hasq ? CastlingRight.QUEEN_SIDE : CastlingRight.NONE;

    return new CastlingRightBoth(white, black);
  }

  private static Square validateEnPassantCaptureTargetSquare(BitboardPosition bitboardPosition,
      String enPassantCaptureTargetSquareStr, Side havingMove) throws FenAdvancedValidationException {
    final Square enPassantCaptureTargetSquare = validateEnPassantCaptureTargetSquare(enPassantCaptureTargetSquareStr,
        havingMove);
    validateEnPassantCaptureTargetSquareAgainstBitboardPosition(bitboardPosition, enPassantCaptureTargetSquare,
        havingMove);
    return enPassantCaptureTargetSquare;
  }

  private static Square validateEnPassantCaptureTargetSquare(String enPassantCaptureTargetSquare, Side havingMove)
      throws FenAdvancedValidationException {
    if (enPassantCaptureTargetSquare.length() == 1 && "-".equals(enPassantCaptureTargetSquare)) {
      return Square.NONE;
    }
    if (enPassantCaptureTargetSquare.length() == 2) {
      final char fileLetter = enPassantCaptureTargetSquare.charAt(0);
      if (File.exists(fileLetter)) {
        final File file = File.calculateFile(fileLetter);
        final char rankLetter = enPassantCaptureTargetSquare.charAt(1);

        if (Rank.exists(rankLetter)) {
          final Rank rank = Rank.calculateRank(rankLetter);
          final Square square = Square.calculate(file, rank);
          if (SquareUtility.calculateEnPassantCaptureTargetSquareList(havingMove).contains(square)) {
            return square;
          }
          final Side oppositeSide = havingMove.getOppositeSide();
          if (SquareUtility.calculateEnPassantCaptureTargetSquareList(oppositeSide).contains(square)) {
            throw new FenAdvancedValidationException(
                FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_WRONG_COLOR,
                "the en passant target square \"" + enPassantCaptureTargetSquare
                    + "\" belongs to the player having the move, not the opponent");
          }
        }
      }
    }
    throw new FenAdvancedValidationException(
        FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_RANGE,
        "the en passant target square part of \"" + enPassantCaptureTargetSquare + "\" is not valid");
  }

  private static void validateEnPassantCaptureTargetSquareAgainstBitboardPosition(BitboardPosition bitboardPosition,
      Square enPassantCaptureTargetSquare, Side havingMove) throws FenAdvancedValidationException {
    if (enPassantCaptureTargetSquare == Square.NONE) {
      // if not set there is nothing to validate
      return;
    }

    final Side oppositeSide = havingMove.getOppositeSide();
    final Square pawnTwoAdvanceSquare = Square.calculateAheadSquare(oppositeSide, enPassantCaptureTargetSquare);
    // The two-advance square must carry an opposite-side PAWN (the pawn that just played the two-square advance).
    // Three rejection conditions, all unioned: no piece at all, wrong side, or wrong piece type. The original
    // StaticPosition predicate read `A || (B && C)` due to Java operator precedence, which let some wrong-side or
    // wrong-piece-type FENs slip past this check and trip an exception further down (in the rewind step). The
    // bitboard rewind is strict (withRelocatedPiece preconditions piece presence) so the loose predicate now
    // surfaces as an unchecked IllegalArgumentException - fixed here to throw the right typed validation exception.
    final Piece pieceOnTwoAdvanceSquare = bitboardPosition.get(pawnTwoAdvanceSquare);
    if (pieceOnTwoAdvanceSquare == Piece.NONE || pieceOnTwoAdvanceSquare.getSide() != oppositeSide
        || pieceOnTwoAdvanceSquare.getPieceType() != PAWN) {
      throw new FenAdvancedValidationException(
          FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_NO_PAWN_AFTER,
          "the en passant target square is specified as \"" + enPassantCaptureTargetSquare
              + "\" but there is no opponent pawn on \"" + pawnTwoAdvanceSquare + "\"");
    }

    // that must come after checking if pawn has potentially advanced two squares
    final Square startingSquare = Square.calculateBehindSquare(oppositeSide, enPassantCaptureTargetSquare);
    final Piece pieceOnStartingSquare = bitboardPosition.get(startingSquare);
    final boolean isStartingSquareEmpty = pieceOnStartingSquare != Piece.NONE;

    final Piece pieceOnEnPassantCaptureTargetSquare = bitboardPosition.get(enPassantCaptureTargetSquare);
    final boolean isEnPassantCaptureTargetSquareEmpty = pieceOnEnPassantCaptureTargetSquare != Piece.NONE;

    if (isStartingSquareEmpty && isEnPassantCaptureTargetSquareEmpty) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_BOTH_NOT_EMPTY,
          "the en passant target square \"" + enPassantCaptureTargetSquare + "\" and the pawn starting square  \""
              + startingSquare + "\" are not empty");
    }

    if (isStartingSquareEmpty) {
      throw new FenAdvancedValidationException(
          FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_STARTING_SQUARE_NOT_EMPTY,
          "the from square \"" + startingSquare + "\" of the pawn making the two square advance is not empty");
    }

    if (isEnPassantCaptureTargetSquareEmpty) {
      throw new FenAdvancedValidationException(
          FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_TARGET_SQUARE_NOT_EMPTY,
          "the en passant target square \"" + enPassantCaptureTargetSquare + "\" is not empty");
    }

    // previous check are necessary for this check: rewind the opponent's pawn two-square advance and verify
    // that havingMove was not already in check in that prior position. Bitboard-wise this is: relocate the
    // opponent pawn from the two-advance square back to its starting square; then ask isInCheck(havingMove).
    final Piece opponentPawn = PieceUtility.calculatePawnPiece(oppositeSide);
    final BitboardPosition bitboardPositionBeforeTwoSquareAdvance = bitboardPosition.withRelocatedPiece(opponentPawn,
        pawnTwoAdvanceSquare, startingSquare);

    if (bitboardPositionBeforeTwoSquareAdvance.isInCheck(havingMove)) {
      throw new FenAdvancedValidationException(
          FenAdvancedValidationProblem.INVALID_EN_PASSANT_CAPTURE_PREVIOUS_POSITION_ILLEGAL,
          "the opponent king was in check before before performing the pawn two square advance");
    }

  }

  private static int validateHalfMoveClock(String halfMoveClockStr, Square enPassantCaptureTargetSquare)
      throws FenAdvancedValidationException {
    final int halfMoveClock = validateHalfMoveClock(halfMoveClockStr);
    validateHalfMoveClock(halfMoveClock, enPassantCaptureTargetSquare);
    return halfMoveClock;
  }

  private static int validateHalfMoveClock(String halfMoveClockStr) throws FenAdvancedValidationException {
    int halfMoveClock;
    try {
      halfMoveClock = Integer.parseInt(halfMoveClockStr);
    } catch (@SuppressWarnings("unused") final NumberFormatException e) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_RANGE,
          "the halfmove clock part of \"" + halfMoveClockStr + "\" is not an integer value");
    }
    return halfMoveClock;
  }

  private static void validateHalfMoveClock(int halfMoveClock, Square enPassantCaptureTargetSquare)
      throws FenAdvancedValidationException {
    if (enPassantCaptureTargetSquare != Square.NONE && halfMoveClock != 0) {
      throw new FenAdvancedValidationException(
          FenAdvancedValidationProblem.INVALID_HALF_MOVE_CLOCK_NOT_ZERO_BUT_EN_PASSANT_CAPTURE_TARGET_SQUARE_SET,
          "the halfmove clock is \"" + halfMoveClock + "\" must be zero if en passant target square is set");
    }
  }

  private static int validateFullMoveNumber(String fullMoveNumberStr) throws FenAdvancedValidationException {
    int fullMoveNumber;
    try {
      fullMoveNumber = Integer.parseInt(fullMoveNumberStr);
    } catch (@SuppressWarnings("unused") final NumberFormatException e) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_FULL_MOVE_NUMBER_RANGE,
          "the fullmove number of \"" + fullMoveNumberStr + "\" is not an integer value");
    }

    if (fullMoveNumber < 0) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_FULL_MOVE_NUMBER_NEGATIVE,
          "the fullmove number of \"" + fullMoveNumberStr + "\" cannot be negative");
    }

    if (fullMoveNumber == 0) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_FULL_MOVE_NUMBER_ZERO,
          "the fullmove number cannot be zero");
    }

    if (fullMoveNumber > FenConstants.MAX_FULL_MOVE_NUMBER) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_FULL_MOVE_NUMBER_TOO_BIG_ABSOLUT,
          "the fullmove number of " + fullMoveNumber + " is above the maximum supported value of "
              + FenConstants.MAX_FULL_MOVE_NUMBER + "");
    }

    return fullMoveNumber;
  }

  private static void validateCastlingRightAgainstBitboardPosition(BitboardPosition bitboardPosition,
      CastlingRightBoth castlingRightBoth) throws FenAdvancedValidationException {

    final List<Side> sideToCheckList = new ArrayList<>();
    sideToCheckList.add(WHITE);
    sideToCheckList.add(BLACK);

    for (final Side sideToCheck : sideToCheckList) {
      final CastlingRight sideCastlingRight = CastlingUtility.getCastlingRight(castlingRightBoth, sideToCheck);

      final boolean isKingSideCastlingOriginalPosition = CastlingUtility
          .calculateKingSideCastlingIsOriginalPosition(bitboardPosition, sideToCheck);
      final boolean isQueenSideCastlingOriginalPosition = CastlingUtility
          .calculateQueenSideCastlingIsOriginalPosition(bitboardPosition, sideToCheck);

      final FenAdvancedValidationProblem parseFenCheck = calculateParseFenCheck(sideToCheck, sideCastlingRight,
          isKingSideCastlingOriginalPosition, isQueenSideCastlingOriginalPosition);

      if (parseFenCheck != FenAdvancedValidationProblem.SUCCESS) {
        throw calculateParseFenException(sideToCheck, sideCastlingRight, parseFenCheck);
      }
    }
  }

  private static FenAdvancedValidationException calculateParseFenException(Side sideToCheck,
      CastlingRight castlingRight, FenAdvancedValidationProblem parseFenCheck) {

    final StringBuilder message = new StringBuilder();
    message.append("Castling rights for ");
    message.append(sideToCheck.getName());
    message.append(" have been specified as ");
    message.append(castlingRight.getDescription());
    message.append(
        ", but castling as such is not possible, as the king and/or rock are not in their original positions anymore.");

    return new FenAdvancedValidationException(parseFenCheck, Nulls.toString(message));
  }

  private static FenAdvancedValidationProblem calculateParseFenCheck(Side sideToCheck, CastlingRight castlingRight,
      boolean isKingSideCastlingOriginalPosition, boolean isQueenSideCastlingOriginalPosition) {
    switch (castlingRight) {
      case KING_AND_QUEEN_SIDE:
        if (!isKingSideCastlingOriginalPosition && !isQueenSideCastlingOriginalPosition) {
          return switch (sideToCheck) {
            case BLACK -> FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_BOTH;
            case WHITE -> FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_BOTH;
            case NONE -> throw new IllegalArgumentException();
            default -> throw new IllegalArgumentException();
          };
        }
        if (!isKingSideCastlingOriginalPosition) {
          return switch (sideToCheck) {
            case BLACK -> FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_KINGSIDE;
            case WHITE -> FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_KINGSIDE;
            case NONE -> throw new IllegalArgumentException();
            default -> throw new IllegalArgumentException();
          };
        }
        if (!isQueenSideCastlingOriginalPosition) {
          return switch (sideToCheck) {
            case BLACK -> FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_QUEENSIDE;
            case WHITE -> FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_QUEENSIDE;
            case NONE -> throw new IllegalArgumentException();
            default -> throw new IllegalArgumentException();
          };
        }
        break;
      case KING_SIDE:
        if (!isKingSideCastlingOriginalPosition) {
          return switch (sideToCheck) {
            case BLACK -> FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_KINGSIDE;
            case WHITE -> FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_KINGSIDE;
            case NONE -> throw new IllegalArgumentException();
            default -> throw new IllegalArgumentException();
          };
        }
        break;
      case NONE:
        break;
      case QUEEN_SIDE:
        if (!isQueenSideCastlingOriginalPosition) {
          return switch (sideToCheck) {
            case BLACK -> FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_BLACK_QUEENSIDE;
            case WHITE -> FenAdvancedValidationProblem.INVALID_CASTLING_RIGHT_WHITE_QUEENSIDE;
            case NONE -> throw new IllegalArgumentException();
            default -> throw new IllegalArgumentException();
          };
        }
        break;
      default:
        throw new IllegalArgumentException();
    }
    return FenAdvancedValidationProblem.SUCCESS;

  }

  private static void validateNumberOfPieces(BitboardPosition bitboardPosition) throws FenAdvancedValidationException {
    validateWhiteNumberOfPieces(bitboardPosition);
    validateBlackNumberOfPieces(bitboardPosition);
  }

  private static void validateWhiteNumberOfPieces(BitboardPosition bitboardPosition)
      throws FenAdvancedValidationException {
    // kings
    final int numberOfKings = FenMaterialCount.calculateNumberOfPieces(Side.WHITE, bitboardPosition, PieceType.KING);
    if (numberOfKings > ChessConstants.NUMBER_OF_KINGS) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_KINGS,
          "there is more than one white king");
    }
    if (numberOfKings == 0) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_WHITE_NO_KING,
          "there is no white king");
    }

    // pawns
    final int numberOfPawns = FenMaterialCount.calculateNumberOfPieces(Side.WHITE, bitboardPosition, PieceType.PAWN);
    if (numberOfPawns > ChessConstants.INITIAL_NUMBER_OF_PAWNS) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_PAWNS,
          "there are too many white pawns");
    }

    final int numberOfPossiblePromotions = ChessConstants.INITIAL_NUMBER_OF_PAWNS - numberOfPawns;

    // rooks
    final int numberOfRooks = FenMaterialCount.calculateNumberOfPieces(Side.WHITE, bitboardPosition, PieceType.ROOK);
    final int numberOfRooksPromoted = numberOfRooks - ChessConstants.INITIAL_NUMBER_OF_ROOKS;
    if (numberOfRooksPromoted > numberOfPossiblePromotions) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_ROOKS,
          "there are too many white rooks");
    }

    // knights
    final int numberOfKnights = FenMaterialCount.calculateNumberOfPieces(Side.WHITE, bitboardPosition,
        PieceType.KNIGHT);
    final int numberOfKnightsPromoted = numberOfKnights - ChessConstants.INITIAL_NUMBER_OF_KNIGHTS;
    if (numberOfKnightsPromoted > numberOfPossiblePromotions) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_KNIGHTS,
          "there are too many white knights");
    }

    // light square bishops
    final int numberOfLightSquareBishops = FenMaterialCount.calculateNumberOfBishops(Side.WHITE, bitboardPosition,
        SquareType.LIGHT_SQUARE);
    final int numberOfLightSquareBishopsPromoted = numberOfLightSquareBishops
        - ChessConstants.INITIAL_NUMBER_OF_LIGHT_SQUARE_BISHOPS;
    if (numberOfLightSquareBishopsPromoted > numberOfPossiblePromotions) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_LIGHT_SQUARE_BISHOPS,
          "there are too many white light squared bishops");
    }

    // dark square bishops
    final int numberOfDarkSquareBishops = FenMaterialCount.calculateNumberOfBishops(Side.WHITE, bitboardPosition,
        SquareType.DARK_SQUARE);
    final int numberOfDarkSquareBishopsPromoted = numberOfDarkSquareBishops
        - ChessConstants.INITIAL_NUMBER_OF_DARK_SQUARE_BISHOPS;
    if (numberOfDarkSquareBishopsPromoted > numberOfPossiblePromotions) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_DARK_SQUARE_BISHOPS,
          "there are too many white dark squared bishops");
    }

    // queens
    final int numberOfQueens = FenMaterialCount.calculateNumberOfPieces(Side.WHITE, bitboardPosition, PieceType.QUEEN);
    final int numberOfQueensPromoted = numberOfQueens - ChessConstants.INITIAL_NUMBER_OF_QUEENS;
    if (numberOfQueensPromoted > numberOfPossiblePromotions) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_WHITE_TOO_MANY_QUEENS,
          "there are too many white queens");
    }
  }

  private static void validateBlackNumberOfPieces(BitboardPosition bitboardPosition)
      throws FenAdvancedValidationException {
    // copy/replace code of white
    // kings
    final int numberOfKings = FenMaterialCount.calculateNumberOfPieces(Side.BLACK, bitboardPosition, PieceType.KING);
    if (numberOfKings > ChessConstants.NUMBER_OF_KINGS) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_KINGS,
          "there is more than one black king");
    }
    if (numberOfKings == 0) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_BLACK_NO_KING,
          "there is no black king");
    }

    // pawns
    final int numberOfPawns = FenMaterialCount.calculateNumberOfPieces(Side.BLACK, bitboardPosition, PieceType.PAWN);
    if (numberOfPawns > ChessConstants.INITIAL_NUMBER_OF_PAWNS) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_PAWNS,
          "there are too many black pawns");
    }

    final int numberOfPossiblePromotions = ChessConstants.INITIAL_NUMBER_OF_PAWNS - numberOfPawns;

    // rooks
    final int numberOfRooks = FenMaterialCount.calculateNumberOfPieces(Side.BLACK, bitboardPosition, PieceType.ROOK);
    final int numberOfRooksPromoted = numberOfRooks - ChessConstants.INITIAL_NUMBER_OF_ROOKS;
    if (numberOfRooksPromoted > numberOfPossiblePromotions) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_ROOKS,
          "there are too many black rooks");
    }

    // knights
    final int numberOfKnights = FenMaterialCount.calculateNumberOfPieces(Side.BLACK, bitboardPosition,
        PieceType.KNIGHT);
    final int numberOfKnightsPromoted = numberOfKnights - ChessConstants.INITIAL_NUMBER_OF_KNIGHTS;
    if (numberOfKnightsPromoted > numberOfPossiblePromotions) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_KNIGHTS,
          "there are too many black knights");
    }

    // light square bishops
    final int numberOfLightSquareBishops = FenMaterialCount.calculateNumberOfBishops(Side.BLACK, bitboardPosition,
        SquareType.LIGHT_SQUARE);
    final int numberOfLightSquareBishopsPromoted = numberOfLightSquareBishops
        - ChessConstants.INITIAL_NUMBER_OF_LIGHT_SQUARE_BISHOPS;
    if (numberOfLightSquareBishopsPromoted > numberOfPossiblePromotions) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_LIGHT_SQUARE_BISHOPS,
          "there are too many black light squared bishops");
    }

    // dark square bishops
    final int numberOfDarkSquareBishops = FenMaterialCount.calculateNumberOfBishops(Side.BLACK, bitboardPosition,
        SquareType.DARK_SQUARE);
    final int numberOfDarkSquareBishopsPromoted = numberOfDarkSquareBishops
        - ChessConstants.INITIAL_NUMBER_OF_DARK_SQUARE_BISHOPS;
    if (numberOfDarkSquareBishopsPromoted > numberOfPossiblePromotions) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_DARK_SQUARE_BISHOPS,
          "there are too many black dark squared bishops");
    }

    // queens
    final int numberOfQueens = FenMaterialCount.calculateNumberOfPieces(Side.BLACK, bitboardPosition, PieceType.QUEEN);
    final int numberOfQueensPromoted = numberOfQueens - ChessConstants.INITIAL_NUMBER_OF_QUEENS;
    if (numberOfQueensPromoted > numberOfPossiblePromotions) {
      throw new FenAdvancedValidationException(FenAdvancedValidationProblem.INVALID_BLACK_TOO_MANY_QUEENS,
          "there are too many black queens");
    }

  }

  private static void validatePawnRankNotPromotionRank(BitboardPosition bitboardPosition)
      throws FenAdvancedValidationException {
    for (final Square square : SquareUtility.getPromotionRank(WHITE)) {
      if (bitboardPosition.get(square) == WHITE_PAWN) {
        throw new FenAdvancedValidationException(
            FenAdvancedValidationProblem.INVALID_WHITE_PAWN_INVALID_RANK_PROMOTION_RANK,
            "There is a non promoted white pawn on rank " + square.getRank().getNumber());
      }
    }

    for (final Square square : SquareUtility.getPromotionRank(BLACK)) {
      if (bitboardPosition.get(square) == BLACK_PAWN) {
        throw new FenAdvancedValidationException(
            FenAdvancedValidationProblem.INVALID_BLACK_PAWN_INVALID_RANK_PROMOTION_RANK,
            "There is a non promoted black pawn on rank " + square.getRank().getNumber());
      }
    }
  }

  private static void validatePawnRankNotGroundRank(BitboardPosition bitboardPosition)
      throws FenAdvancedValidationException {
    for (final Square square : SquareUtility.getPromotionRank(BLACK)) {
      if (bitboardPosition.get(square) == WHITE_PAWN) {
        throw new FenAdvancedValidationException(
            FenAdvancedValidationProblem.INVALID_WHITE_PAWN_INVALID_RANK_GROUND_RANK,
            "There is a white pawn on rank " + square.getRank().getNumber());
      }
    }

    for (final Square square : SquareUtility.getPromotionRank(WHITE)) {
      if (bitboardPosition.get(square) == BLACK_PAWN) {
        throw new FenAdvancedValidationException(
            FenAdvancedValidationProblem.INVALID_BLACK_PAWN_INVALID_RANK_GROUND_RANK,
            "There is a black pawn on rank " + square.getRank().getNumber());
      }
    }

  }
}
