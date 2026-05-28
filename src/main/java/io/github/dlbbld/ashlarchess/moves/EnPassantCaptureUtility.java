package io.github.dlbbld.ashlarchess.moves;

import static io.github.dlbbld.ashlarchess.common.utility.ImmutableUtility.constructListSquare;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.model.UpdateSquare;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;

public abstract class EnPassantCaptureUtility implements EnumConstants {

  private static final ImmutableList<ImmutableList<Square>> WHITE_EN_PASSANT_CAPTURE_FROM_TO;

  static {
    final List<ImmutableList<Square>> list = new ArrayList<>();
    list.add(constructListSquare(A5, B6));
    list.add(constructListSquare(B5, C6));
    list.add(constructListSquare(C5, D6));
    list.add(constructListSquare(D5, E6));
    list.add(constructListSquare(E5, F6));
    list.add(constructListSquare(F5, G6));
    list.add(constructListSquare(G5, H6));
    list.add(constructListSquare(B5, A6));
    list.add(constructListSquare(C5, B6));
    list.add(constructListSquare(D5, C6));
    list.add(constructListSquare(E5, D6));
    list.add(constructListSquare(F5, E6));
    list.add(constructListSquare(G5, F6));
    list.add(constructListSquare(H5, G6));
    WHITE_EN_PASSANT_CAPTURE_FROM_TO = Nulls.copyOfList(list);

  }

  private static final ImmutableList<ImmutableList<Square>> BLACK_EN_PASSANT_CAPTURE_FROM_TO;

  static {
    final List<ImmutableList<Square>> list = new ArrayList<>();

    list.add(constructListSquare(A4, B3));
    list.add(constructListSquare(B4, C3));
    list.add(constructListSquare(C4, D3));
    list.add(constructListSquare(D4, E3));
    list.add(constructListSquare(E4, F3));
    list.add(constructListSquare(F4, G3));
    list.add(constructListSquare(G4, H3));
    list.add(constructListSquare(B4, A3));
    list.add(constructListSquare(C4, B3));
    list.add(constructListSquare(D4, C3));
    list.add(constructListSquare(E4, D3));
    list.add(constructListSquare(F4, E3));
    list.add(constructListSquare(G4, F3));
    list.add(constructListSquare(H4, G3));

    BLACK_EN_PASSANT_CAPTURE_FROM_TO = Nulls.copyOfList(list);
  }

  private static final ImmutableMap<Square, Square> WHITE_EN_PASSANT_CAPTURE_TO_CAPTURE;

  static {
    final EnumMap<Square, Square> map = Maps.newEnumMap(Square.class);

    map.put(A6, A5);
    map.put(B6, B5);
    map.put(C6, C5);
    map.put(D6, D5);
    map.put(E6, E5);
    map.put(F6, F5);
    map.put(G6, G5);
    map.put(H6, H5);

    WHITE_EN_PASSANT_CAPTURE_TO_CAPTURE = Nulls.immutableEnumMap(map);
  }

  private static final ImmutableMap<Square, Square> BLACK_EN_PASSANT_CAPTURE_TO_CAPTURE;

  static {
    final EnumMap<Square, Square> map = Nulls.newEnumMap(Square.class);

    map.put(A3, A4);
    map.put(B3, B4);
    map.put(C3, C4);
    map.put(D3, D4);
    map.put(E3, E4);
    map.put(F3, F4);
    map.put(G3, G4);
    map.put(H3, H4);

    BLACK_EN_PASSANT_CAPTURE_TO_CAPTURE = Nulls.immutableEnumMap(map);
  }

  private static final ImmutableMap<Square, Square> WHITE_TWO_SQUARE_ADVANCE_TO_EN_PASSANT_CAPTURE_TO;

  static {
    final EnumMap<Square, Square> map = Nulls.newEnumMap(Square.class);

    map.put(A4, A3);
    map.put(B4, B3);
    map.put(C4, C3);
    map.put(D4, D3);
    map.put(E4, E3);
    map.put(F4, F3);
    map.put(G4, G3);
    map.put(H4, H3);

    WHITE_TWO_SQUARE_ADVANCE_TO_EN_PASSANT_CAPTURE_TO = Nulls.immutableEnumMap(map);
  }

  private static final ImmutableMap<Square, Square> BLACK_TWO_SQUARE_ADVANCE_TO_EN_PASSANT_CAPTURE_TO;

  static {
    final EnumMap<Square, Square> map = Nulls.newEnumMap(Square.class);

    map.put(A5, A6);
    map.put(B5, B6);
    map.put(C5, C6);
    map.put(D5, D6);
    map.put(E5, E6);
    map.put(F5, F6);
    map.put(G5, G6);
    map.put(H5, H6);

    BLACK_TWO_SQUARE_ADVANCE_TO_EN_PASSANT_CAPTURE_TO = Nulls.immutableEnumMap(map);

  }

  // we check if the moving piece is a pawn and the move itself
  public static boolean calculateIsPawnTwoSquareAdvanceMove(Piece movingPiece, MoveSpecification move) {
    if (movingPiece != Piece.NONE && movingPiece.getPieceType() == PAWN) {
      return switch (movingPiece.getSide()) {
        case WHITE -> Square.WHITE_PAWN_TWO_SQUARE_ADVANCE.contains(calculateFromToList(move));
        case BLACK -> Square.BLACK_PAWN_TWO_SQUARE_ADVANCE.contains(calculateFromToList(move));
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
    }
    return false;
  }

  private static List<Square> calculateFromToList(MoveSpecification move) {
    final List<Square> result = new ArrayList<>();
    result.add(move.fromSquare());
    result.add(move.toSquare());
    return result;
  }

  public static Square calculateEnPassantCaptureTargetSquare(LegalMove legalMove) {
    if (legalMove.kind() == LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE) {
      return calculateEnPassantCaptureTargetSquareForTwoSquareAdvanceMove(legalMove.havingMove(),
          legalMove.moveSpecification());
    }
    return Square.NONE;
  }

  private static Square calculateEnPassantCaptureTargetSquareForTwoSquareAdvanceMove(Side havingMove,
      MoveSpecification move) {
    switch (havingMove) {
      case WHITE:
        if (!WHITE_TWO_SQUARE_ADVANCE_TO_EN_PASSANT_CAPTURE_TO.containsKey(move.toSquare())) {
          throw new IllegalArgumentException("The method only applies for en passant moves");
        }
        return Nulls.get(WHITE_TWO_SQUARE_ADVANCE_TO_EN_PASSANT_CAPTURE_TO, move.toSquare());
      case BLACK:
        if (!BLACK_TWO_SQUARE_ADVANCE_TO_EN_PASSANT_CAPTURE_TO.containsKey(move.toSquare())) {
          throw new IllegalArgumentException("The method only applies for en passant moves");
        }
        return Nulls.get(BLACK_TWO_SQUARE_ADVANCE_TO_EN_PASSANT_CAPTURE_TO, move.toSquare());
      case NONE:
      default:
        throw new IllegalArgumentException();
    }
  }

  // Only the bitboard variant survives in src/main after Phase 6 of the role-inversion release. The
  // StaticPosition-shaped overloads (calculateIsEnPassantCaptureNewMove, calculateIsPotentialEnPassantCapture)
  // were collapsed: the only test-side caller (the relocated StaticPositionUtility) now derives a bitboard via
  // StaticPositionBridge and calls this method instead.
  public static boolean calculateIsPotentialEnPassantCapture(BitboardPosition bitboardPositionBeforeMove,
      MoveSpecification move) {
    if (CastlingUtility.calculateIsCastlingMove(move)) {
      return false;
    }
    final Piece movingPiece = bitboardPositionBeforeMove.get(move.fromSquare());
    if (movingPiece == Piece.NONE || movingPiece.getPieceType() != PAWN) {
      return false;
    }
    return switch (movingPiece.getSide()) {
      case WHITE -> WHITE_EN_PASSANT_CAPTURE_FROM_TO.contains(calculateFromToList(move))
          && bitboardPositionBeforeMove.get(move.toSquare()) == Piece.NONE;
      case BLACK -> BLACK_EN_PASSANT_CAPTURE_FROM_TO.contains(calculateFromToList(move))
          && bitboardPositionBeforeMove.get(move.toSquare()) == Piece.NONE;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Square calculateSquareOfCapturedPawnForEnPassantCapture(Side havingMove, MoveSpecification move) {
    return calculateSquareOfCapturedPawnForEnPassantCapture(havingMove, move.toSquare());
  }

  private static Square calculateSquareOfCapturedPawnForEnPassantCapture(Side havingMove, Square square) {
    switch (havingMove) {
      case WHITE:
        if (!WHITE_EN_PASSANT_CAPTURE_TO_CAPTURE.containsKey(square)) {
          throw new IllegalArgumentException("Please provide the target square of an en passant capture");
        }
        return Nulls.get(WHITE_EN_PASSANT_CAPTURE_TO_CAPTURE, square);
      case BLACK:
        if (!BLACK_EN_PASSANT_CAPTURE_TO_CAPTURE.containsKey(square)) {
          throw new IllegalArgumentException("Please provide the target square of an en passant capture");
        }
        return Nulls.get(BLACK_EN_PASSANT_CAPTURE_TO_CAPTURE, square);
      case NONE:
      default:
        throw new IllegalArgumentException();
    }
  }

  public static List<UpdateSquare> performEnPassantCaptureMovements(Side havingMove,
      MoveSpecification moveSpecification) {
    // arriving here, the move must have been identified as en passant capture
    final List<UpdateSquare> result = new ArrayList<>();

    // pawn move: from square becomes empty; on to square is the moved pawn (always a pawn of havingMove).
    result.add(new UpdateSquare(moveSpecification.fromSquare()));
    result.add(new UpdateSquare(moveSpecification.toSquare(), Piece.calculatePawnPiece(havingMove)));

    // remove the captured pawn (one rank back from the to-square, same file)
    final Square squareOfCapturedPawnForEnPassantCapture = calculateSquareOfCapturedPawnForEnPassantCapture(havingMove,
        moveSpecification);
    result.add(new UpdateSquare(squareOfCapturedPawnForEnPassantCapture, Piece.NONE));

    return result;
  }

}
