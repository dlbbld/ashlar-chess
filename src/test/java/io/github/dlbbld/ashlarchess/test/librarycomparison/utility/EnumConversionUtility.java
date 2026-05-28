package io.github.dlbbld.ashlarchess.test.librarycomparison.utility;

import com.github.bhlangonijr.chesslib.CastleRight;

import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.test.librarycarlos.NullsCarlos;

public abstract class EnumConversionUtility implements EnumConstants {

  public static com.github.bhlangonijr.chesslib.Piece convertToPiece(Side havingMove, PieceType pieceType) {
    return switch (havingMove) {
      case BLACK -> switch (pieceType) {
        case PAWN -> com.github.bhlangonijr.chesslib.Piece.BLACK_PAWN;
        case ROOK -> com.github.bhlangonijr.chesslib.Piece.BLACK_ROOK;
        case KNIGHT -> com.github.bhlangonijr.chesslib.Piece.BLACK_KNIGHT;
        case BISHOP -> com.github.bhlangonijr.chesslib.Piece.BLACK_BISHOP;
        case QUEEN -> com.github.bhlangonijr.chesslib.Piece.BLACK_QUEEN;
        case KING -> com.github.bhlangonijr.chesslib.Piece.BLACK_KING;
        case NONE -> com.github.bhlangonijr.chesslib.Piece.NONE;
        default -> throw new IllegalArgumentException();
      };
      case WHITE -> switch (pieceType) {
        case PAWN -> com.github.bhlangonijr.chesslib.Piece.WHITE_PAWN;
        case ROOK -> com.github.bhlangonijr.chesslib.Piece.WHITE_ROOK;
        case KNIGHT -> com.github.bhlangonijr.chesslib.Piece.WHITE_KNIGHT;
        case BISHOP -> com.github.bhlangonijr.chesslib.Piece.WHITE_BISHOP;
        case QUEEN -> com.github.bhlangonijr.chesslib.Piece.WHITE_QUEEN;
        case KING -> com.github.bhlangonijr.chesslib.Piece.WHITE_KING;
        case NONE -> com.github.bhlangonijr.chesslib.Piece.NONE;
        default -> throw new IllegalArgumentException();
      };
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static com.github.bhlangonijr.chesslib.Piece convertToPiece(Side havingMove,
      PromotionPieceType promotionPieceType) {
    return switch (havingMove) {
      case BLACK -> switch (promotionPieceType) {
        case ROOK -> com.github.bhlangonijr.chesslib.Piece.BLACK_ROOK;
        case KNIGHT -> com.github.bhlangonijr.chesslib.Piece.BLACK_KNIGHT;
        case BISHOP -> com.github.bhlangonijr.chesslib.Piece.BLACK_BISHOP;
        case QUEEN -> com.github.bhlangonijr.chesslib.Piece.BLACK_QUEEN;
        case NONE -> com.github.bhlangonijr.chesslib.Piece.NONE;
        default -> throw new IllegalArgumentException();
      };
      case WHITE -> switch (promotionPieceType) {
        case ROOK -> com.github.bhlangonijr.chesslib.Piece.WHITE_ROOK;
        case KNIGHT -> com.github.bhlangonijr.chesslib.Piece.WHITE_KNIGHT;
        case BISHOP -> com.github.bhlangonijr.chesslib.Piece.WHITE_BISHOP;
        case QUEEN -> com.github.bhlangonijr.chesslib.Piece.WHITE_QUEEN;
        case NONE -> com.github.bhlangonijr.chesslib.Piece.NONE;
        default -> throw new IllegalArgumentException();
      };
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static com.github.bhlangonijr.chesslib.Piece convertToPiece(Piece piece) {
    return switch (piece) {
      case WHITE_PAWN -> com.github.bhlangonijr.chesslib.Piece.WHITE_PAWN;
      case WHITE_ROOK -> com.github.bhlangonijr.chesslib.Piece.WHITE_ROOK;
      case WHITE_KNIGHT -> com.github.bhlangonijr.chesslib.Piece.WHITE_KNIGHT;
      case WHITE_BISHOP -> com.github.bhlangonijr.chesslib.Piece.WHITE_BISHOP;
      case WHITE_QUEEN -> com.github.bhlangonijr.chesslib.Piece.WHITE_QUEEN;
      case WHITE_KING -> com.github.bhlangonijr.chesslib.Piece.WHITE_KING;
      case BLACK_PAWN -> com.github.bhlangonijr.chesslib.Piece.BLACK_PAWN;
      case BLACK_ROOK -> com.github.bhlangonijr.chesslib.Piece.BLACK_ROOK;
      case BLACK_KNIGHT -> com.github.bhlangonijr.chesslib.Piece.BLACK_KNIGHT;
      case BLACK_BISHOP -> com.github.bhlangonijr.chesslib.Piece.BLACK_BISHOP;
      case BLACK_QUEEN -> com.github.bhlangonijr.chesslib.Piece.BLACK_QUEEN;
      case BLACK_KING -> com.github.bhlangonijr.chesslib.Piece.BLACK_KING;
      case NONE -> com.github.bhlangonijr.chesslib.Piece.NONE;
      default -> throw new IllegalArgumentException();
    };
  }

  public static Piece convertToMyPiece(com.github.bhlangonijr.chesslib.Piece piece) {
    return switch (piece) {
      case WHITE_PAWN -> WHITE_PAWN;
      case WHITE_ROOK -> WHITE_ROOK;
      case WHITE_KNIGHT -> WHITE_KNIGHT;
      case WHITE_BISHOP -> WHITE_BISHOP;
      case WHITE_QUEEN -> WHITE_QUEEN;
      case WHITE_KING -> WHITE_KING;
      case BLACK_PAWN -> BLACK_PAWN;
      case BLACK_ROOK -> BLACK_ROOK;
      case BLACK_KNIGHT -> BLACK_KNIGHT;
      case BLACK_BISHOP -> BLACK_BISHOP;
      case BLACK_QUEEN -> BLACK_QUEEN;
      case BLACK_KING -> BLACK_KING;
      case NONE -> Piece.NONE;
      default -> throw new IllegalArgumentException();
    };
  }

  public static PieceType convertToMyPieceType(com.github.bhlangonijr.chesslib.Piece piece) {
    return switch (piece) {
      case WHITE_PAWN -> PAWN;
      case WHITE_ROOK -> ROOK;
      case WHITE_KNIGHT -> KNIGHT;
      case WHITE_BISHOP -> BISHOP;
      case WHITE_QUEEN -> QUEEN;
      case WHITE_KING -> KING;
      case BLACK_PAWN -> PAWN;
      case BLACK_ROOK -> ROOK;
      case BLACK_KNIGHT -> KNIGHT;
      case BLACK_BISHOP -> BISHOP;
      case BLACK_QUEEN -> QUEEN;
      case BLACK_KING -> KING;
      case NONE -> PieceType.NONE;
      default -> throw new IllegalArgumentException();
    };
  }

  public static PromotionPieceType convertToMyPromotionPieceType(com.github.bhlangonijr.chesslib.Piece piece) {
    return switch (piece) {
      case WHITE_ROOK -> PromotionPieceType.ROOK;
      case WHITE_KNIGHT -> PromotionPieceType.KNIGHT;
      case WHITE_BISHOP -> PromotionPieceType.BISHOP;
      case WHITE_QUEEN -> PromotionPieceType.QUEEN;
      case BLACK_ROOK -> PromotionPieceType.ROOK;
      case BLACK_KNIGHT -> PromotionPieceType.KNIGHT;
      case BLACK_BISHOP -> PromotionPieceType.BISHOP;
      case BLACK_QUEEN -> PromotionPieceType.QUEEN;
      case WHITE_KING, WHITE_PAWN, BLACK_KING, BLACK_PAWN, NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Side convertToMySide(com.github.bhlangonijr.chesslib.Side side) {
    return switch (side) {
      case BLACK -> BLACK;
      case WHITE -> WHITE;
      default -> throw new IllegalArgumentException();
    };
  }

  public static com.github.bhlangonijr.chesslib.Side convertToSide(Side side) {
    return switch (side) {
      case BLACK -> com.github.bhlangonijr.chesslib.Side.BLACK;
      case WHITE -> com.github.bhlangonijr.chesslib.Side.WHITE;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Square convertToMySquare(com.github.bhlangonijr.chesslib.Square squareLibraryCarlos) {

    if (!existsSquare(squareLibraryCarlos)) {
      throw new IllegalArgumentException("For this square no corresponding square exists");
    }

    final String name = getSquareName(squareLibraryCarlos);

    for (final Square square : Square.REAL) {
      if (square.getName().equals(name)) {
        return square;
      }
    }
    throw new ProgrammingMistakeException("The code for converting the square to square does not work");
  }

  public static boolean existsSquare(com.github.bhlangonijr.chesslib.Square squareLibraryCarlos) {

    final String name = getSquareName(squareLibraryCarlos);

    for (final Square square : Square.REAL) {
      if (square.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  public static com.github.bhlangonijr.chesslib.Square convertToSquare(Square square) {

    if (!existsMySquare(square)) {
      throw new IllegalArgumentException("For this square no corresponding square exists");
    }

    for (final com.github.bhlangonijr.chesslib.Square squareLibraryCarlos : com.github.bhlangonijr.chesslib.Square
        .values()) {
      if (square.getName().equals(getSquareName(squareLibraryCarlos))) {
        return squareLibraryCarlos;
      }
    }
    throw new ProgrammingMistakeException("The code for converting the square to square does not work");
  }

  public static boolean existsMySquare(Square square) {

    for (final com.github.bhlangonijr.chesslib.Square squareLibraryCarlos : com.github.bhlangonijr.chesslib.Square
        .values()) {
      if (square.getName().equals(getSquareName(squareLibraryCarlos))) {
        return true;
      }
    }
    return false;
  }

  private static String getSquareName(com.github.bhlangonijr.chesslib.Square square) {
    return Nulls.toLowerCase(NullsCarlos.name(square));
  }

  public static Piece convertPiece(com.github.bhlangonijr.chesslib.Piece piece) {
    return switch (piece) {
      case WHITE_PAWN -> WHITE_PAWN;
      case WHITE_ROOK -> WHITE_ROOK;
      case WHITE_KNIGHT -> WHITE_KNIGHT;
      case WHITE_BISHOP -> WHITE_BISHOP;
      case WHITE_QUEEN -> WHITE_QUEEN;
      case WHITE_KING -> WHITE_KING;
      case BLACK_PAWN -> BLACK_PAWN;
      case BLACK_ROOK -> BLACK_ROOK;
      case BLACK_KNIGHT -> BLACK_KNIGHT;
      case BLACK_BISHOP -> BLACK_BISHOP;
      case BLACK_QUEEN -> BLACK_QUEEN;
      case BLACK_KING -> BLACK_KING;
      case NONE -> Piece.NONE;
      default -> throw new IllegalArgumentException();
    };
  }

  public static CastlingRight convert(CastleRight castlingRight) {
    return switch (castlingRight) {
      case KING_AND_QUEEN_SIDE -> CastlingRight.KING_AND_QUEEN_SIDE;
      case KING_SIDE -> CastlingRight.KING_SIDE;
      case NONE -> CastlingRight.NONE;
      case QUEEN_SIDE -> CastlingRight.QUEEN_SIDE;
      default -> throw new IllegalArgumentException();
    };
  }
}
