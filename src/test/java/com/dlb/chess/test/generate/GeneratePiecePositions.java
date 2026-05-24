package com.dlb.chess.test.generate;

import org.apache.logging.log4j.Logger;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.board.enums.SquareType;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.FenAdvancedValidationException;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.utility.StaticPositionUtility;
import com.dlb.chess.fen.FenParserAdvanced;
import com.dlb.chess.test.common.utility.FenUtility;

public class GeneratePiecePositions {

  private static final Logger logger = Nulls.getLogger(GeneratePiecePositions.class);

  // Current generated counts:
  // K vs K:
  // - White to move: 3612
  // - Black to move: 3612
  //
  // K+N vs K:
  // Runtime note: takes about two minutes.
  // - White to move: 205496
  // - Black to move: 223944
  //
  // K+B vs K:
  // Runtime note: takes about four minutes.
  // - Light-square bishop, white to move: 96642
  // - Light-square bishop, black to move: 111972
  // - Dark-square bishop, white to move: 96642
  // - Dark-square bishop, black to move: 111972
  // - Any bishop square, white to move: 193284
  // - Any bishop square, black to move: 223944
  //
  // K+B vs K+B, bishops on the same colour square:
  // Runtime note: not completed run yet, takes maybe hours.
  // - Light squares, white to move: ?
  // - Light squares, black to move: ?

  public static void main(String[] args) {
    generateKingVersusKing();
    generateKingKnightVersusKing();
    generateKingBishopVersusKing();
    generateKingBishopVersusBishopSameColourSquare();
  }

  private static void generateKingVersusKing() {
    printCount("K vs K, white to move", calculateKingVersusKing(Side.WHITE));
    printCount("K vs K, black to move", calculateKingVersusKing(Side.BLACK));
  }

  private static int calculateKingVersusKing(Side side) {
    var counter = 0;

    StaticPosition staticPositionWork = StaticPosition.EMPTY_POSITION;
    for (final Square squareFirstKing : Square.REAL) {
      staticPositionWork = staticPositionWork.createChangedPosition(squareFirstKing, Piece.WHITE_KING);
      for (final Square squareSecondKing : Square.REAL) {
        if (squareSecondKing == squareFirstKing) {
          continue;
        }
        staticPositionWork = staticPositionWork.createChangedPosition(squareSecondKing, Piece.BLACK_KING);
        final String piecePlacement = StaticPositionUtility.calculatePiecePlacement(staticPositionWork);
        final String fen = FenUtility.createDummyFenForPiecePlacement(piecePlacement, side);
        try {
          FenParserAdvanced.parseFenAdvanced(fen);
          counter++;
          ensureNotCheckmate(fen);
        } catch (@SuppressWarnings("unused") final FenAdvancedValidationException fve) {
          // not counting if invalid
        }
        staticPositionWork = staticPositionWork.createChangedPosition(squareSecondKing);
      }
      staticPositionWork = staticPositionWork.createChangedPosition(squareFirstKing);
      ensureEmpty(staticPositionWork);
    }
    return counter;
  }

  private static void generateKingKnightVersusKing() {
    // Takes about two minutes.
    printCount("K+N vs K, white to move", calculateKingKnightVersusKing(Side.WHITE));
    printCount("K+N vs K, black to move", calculateKingKnightVersusKing(Side.BLACK));
  }

  private static int calculateKingKnightVersusKing(Side side) {
    return calculateKingPieceVersusKing(side, Piece.WHITE_KNIGHT, SquareType.NONE);
  }

  private static void generateKingBishopVersusKing() {
    // Takes about four minutes.
    printCount("K+B vs K, light-square bishop, white to move",
        calculateKingPieceVersusKing(Side.WHITE, Piece.WHITE_BISHOP, SquareType.LIGHT_SQUARE));
    printCount("K+B vs K, light-square bishop, black to move",
        calculateKingPieceVersusKing(Side.BLACK, Piece.WHITE_BISHOP, SquareType.LIGHT_SQUARE));
    printCount("K+B vs K, dark-square bishop, white to move",
        calculateKingPieceVersusKing(Side.WHITE, Piece.WHITE_BISHOP, SquareType.DARK_SQUARE));
    printCount("K+B vs K, dark-square bishop, black to move",
        calculateKingPieceVersusKing(Side.BLACK, Piece.WHITE_BISHOP, SquareType.DARK_SQUARE));
    printCount("K+B vs K, any bishop square, white to move", calculateKingBishopVersusKing(Side.WHITE));
    printCount("K+B vs K, any bishop square, black to move", calculateKingBishopVersusKing(Side.BLACK));
  }

  private static int calculateKingBishopVersusKing(Side side) {
    return calculateKingPieceVersusKing(side, Piece.WHITE_BISHOP, SquareType.NONE);
  }

  private static void generateKingBishopVersusBishopSameColourSquare() {
    // Not completed run yet, takes maybe hours.
    printCount("K+B vs K+B, light squares, white to move",
        calculateKingBishopVersusBishopSameColourSquare(Side.WHITE, SquareType.LIGHT_SQUARE));
    printCount("K+B vs K+B, light squares, black to move",
        calculateKingBishopVersusBishopSameColourSquare(Side.BLACK, SquareType.LIGHT_SQUARE));
  }

  private static int calculateKingBishopVersusBishopSameColourSquare(Side side, SquareType squareType) {
    var counter = 0;

    StaticPosition staticPositionWork = StaticPosition.EMPTY_POSITION;
    for (final Square squareFirstKing : Square.REAL) {
      staticPositionWork = staticPositionWork.createChangedPosition(squareFirstKing, Piece.WHITE_KING);
      for (final Square squareFirstBishop : Square.REAL) {
        if (squareFirstBishop == squareFirstKing || squareFirstBishop.getSquareType() != squareType) {
          continue;
        }
        staticPositionWork = staticPositionWork.createChangedPosition(squareFirstBishop, Piece.WHITE_BISHOP);
        for (final Square squareSecondKing : Square.REAL) {
          if (squareSecondKing == squareFirstBishop || squareSecondKing == squareFirstKing) {
            continue;
          }
          staticPositionWork = staticPositionWork.createChangedPosition(squareSecondKing, Piece.BLACK_KING);

          for (final Square squareSecondBishop : Square.REAL) {
            if (squareSecondBishop == squareFirstKing || squareSecondBishop == squareFirstBishop
                || squareSecondBishop == squareSecondKing || squareSecondBishop.getSquareType() != squareType) {
              continue;
            }
            staticPositionWork = staticPositionWork.createChangedPosition(squareSecondBishop, Piece.BLACK_BISHOP);

            final String piecePlacement = StaticPositionUtility.calculatePiecePlacement(staticPositionWork);
            final String fen = FenUtility.createDummyFenForPiecePlacement(piecePlacement, side);
            try {
              FenParserAdvanced.parseFenAdvanced(fen);
              counter++;
              ensureNotCheckmate(fen);
            } catch (@SuppressWarnings("unused") final FenAdvancedValidationException fve) {
              // not counting if invalid
            }
            staticPositionWork = staticPositionWork.createChangedPosition(squareSecondBishop);
          }
          staticPositionWork = staticPositionWork.createChangedPosition(squareSecondKing);
        }
        logger.info(counter);
        staticPositionWork = staticPositionWork.createChangedPosition(squareFirstBishop);
      }
      staticPositionWork = staticPositionWork.createChangedPosition(squareFirstKing);
      ensureEmpty(staticPositionWork);
    }
    return counter;
  }

  private static int calculateKingPieceVersusKing(Side side, Piece piece, SquareType squareType) {
    var counter = 0;

    StaticPosition staticPositionWork = StaticPosition.EMPTY_POSITION;
    for (final Square squareFirstKing : Square.REAL) {
      staticPositionWork = staticPositionWork.createChangedPosition(squareFirstKing, Piece.WHITE_KING);
      for (final Square squarePiece : Square.REAL) {
        if (squarePiece == squareFirstKing || !isSelectedSquareType(squarePiece, squareType)) {
          continue;
        }
        staticPositionWork = staticPositionWork.createChangedPosition(squarePiece, piece);
        for (final Square squareSecondKing : Square.REAL) {
          if (squareSecondKing == squarePiece || squareSecondKing == squareFirstKing) {
            continue;
          }
          staticPositionWork = staticPositionWork.createChangedPosition(squareSecondKing, Piece.BLACK_KING);
          final String piecePlacement = StaticPositionUtility.calculatePiecePlacement(staticPositionWork);
          final String fen = FenUtility.createDummyFenForPiecePlacement(piecePlacement, side);
          try {
            FenParserAdvanced.parseFenAdvanced(fen);
            counter++;
            ensureNotCheckmate(fen);
          } catch (@SuppressWarnings("unused") final FenAdvancedValidationException fve) {
            // not counting if invalid
          }
          staticPositionWork = staticPositionWork.createChangedPosition(squareSecondKing);
        }
        staticPositionWork = staticPositionWork.createChangedPosition(squarePiece);
      }
      staticPositionWork = staticPositionWork.createChangedPosition(squareFirstKing);
      ensureEmpty(staticPositionWork);
    }
    return counter;
  }

  private static boolean isSelectedSquareType(Square square, SquareType squareType) {
    return switch (squareType) {
      case LIGHT_SQUARE, DARK_SQUARE -> square.getSquareType() == squareType;
      case NONE -> true;
      default -> throw new IllegalArgumentException();
    };
  }

  private static void ensureNotCheckmate(String fen) {
    final Board board = new Board(fen);
    if (board.isCheckmate()) {
      throw new ProgrammingMistakeException("We don't have insufficient material. The FEN is \"" + fen + "\".");
    }
  }

  private static void ensureEmpty(StaticPosition staticPosition) {
    if (!staticPosition.equals(StaticPosition.EMPTY_POSITION)) {
      throw new ProgrammingMistakeException();
    }
  }

  private static void printCount(String name, int count) {
    System.out.println(name + ": " + count);
  }
}
