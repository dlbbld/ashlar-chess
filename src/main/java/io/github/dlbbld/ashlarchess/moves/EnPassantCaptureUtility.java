// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.moves;

import static io.github.dlbbld.ashlarchess.board.enums.PieceType.PAWN;
import static io.github.dlbbld.ashlarchess.board.enums.Square.A3;
import static io.github.dlbbld.ashlarchess.board.enums.Square.A4;
import static io.github.dlbbld.ashlarchess.board.enums.Square.A5;
import static io.github.dlbbld.ashlarchess.board.enums.Square.A6;
import static io.github.dlbbld.ashlarchess.board.enums.Square.B3;
import static io.github.dlbbld.ashlarchess.board.enums.Square.B4;
import static io.github.dlbbld.ashlarchess.board.enums.Square.B5;
import static io.github.dlbbld.ashlarchess.board.enums.Square.B6;
import static io.github.dlbbld.ashlarchess.board.enums.Square.C3;
import static io.github.dlbbld.ashlarchess.board.enums.Square.C4;
import static io.github.dlbbld.ashlarchess.board.enums.Square.C5;
import static io.github.dlbbld.ashlarchess.board.enums.Square.C6;
import static io.github.dlbbld.ashlarchess.board.enums.Square.D3;
import static io.github.dlbbld.ashlarchess.board.enums.Square.D4;
import static io.github.dlbbld.ashlarchess.board.enums.Square.D5;
import static io.github.dlbbld.ashlarchess.board.enums.Square.D6;
import static io.github.dlbbld.ashlarchess.board.enums.Square.E3;
import static io.github.dlbbld.ashlarchess.board.enums.Square.E4;
import static io.github.dlbbld.ashlarchess.board.enums.Square.E5;
import static io.github.dlbbld.ashlarchess.board.enums.Square.E6;
import static io.github.dlbbld.ashlarchess.board.enums.Square.F3;
import static io.github.dlbbld.ashlarchess.board.enums.Square.F4;
import static io.github.dlbbld.ashlarchess.board.enums.Square.F5;
import static io.github.dlbbld.ashlarchess.board.enums.Square.F6;
import static io.github.dlbbld.ashlarchess.board.enums.Square.G3;
import static io.github.dlbbld.ashlarchess.board.enums.Square.G4;
import static io.github.dlbbld.ashlarchess.board.enums.Square.G5;
import static io.github.dlbbld.ashlarchess.board.enums.Square.G6;
import static io.github.dlbbld.ashlarchess.board.enums.Square.H3;
import static io.github.dlbbld.ashlarchess.board.enums.Square.H4;
import static io.github.dlbbld.ashlarchess.board.enums.Square.H5;
import static io.github.dlbbld.ashlarchess.board.enums.Square.H6;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.board.model.UpdateSquare;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.board.MoveSpecification;
import io.github.dlbbld.ashlarchess.board.LegalMove;
import io.github.dlbbld.ashlarchess.board.LegalMoveKind;

public final class EnPassantCaptureUtility {

  private EnPassantCaptureUtility() {
  }

  private static final List<List<Square>> WHITE_EN_PASSANT_CAPTURE_FROM_TO;

  static {
    final List<List<Square>> list = new ArrayList<>();
    list.add(Nulls.listOf(A5, B6));
    list.add(Nulls.listOf(B5, C6));
    list.add(Nulls.listOf(C5, D6));
    list.add(Nulls.listOf(D5, E6));
    list.add(Nulls.listOf(E5, F6));
    list.add(Nulls.listOf(F5, G6));
    list.add(Nulls.listOf(G5, H6));
    list.add(Nulls.listOf(B5, A6));
    list.add(Nulls.listOf(C5, B6));
    list.add(Nulls.listOf(D5, C6));
    list.add(Nulls.listOf(E5, D6));
    list.add(Nulls.listOf(F5, E6));
    list.add(Nulls.listOf(G5, F6));
    list.add(Nulls.listOf(H5, G6));
    WHITE_EN_PASSANT_CAPTURE_FROM_TO = Nulls.copyOfList(list);

  }

  private static final List<List<Square>> BLACK_EN_PASSANT_CAPTURE_FROM_TO;

  static {
    final List<List<Square>> list = new ArrayList<>();

    list.add(Nulls.listOf(A4, B3));
    list.add(Nulls.listOf(B4, C3));
    list.add(Nulls.listOf(C4, D3));
    list.add(Nulls.listOf(D4, E3));
    list.add(Nulls.listOf(E4, F3));
    list.add(Nulls.listOf(F4, G3));
    list.add(Nulls.listOf(G4, H3));
    list.add(Nulls.listOf(B4, A3));
    list.add(Nulls.listOf(C4, B3));
    list.add(Nulls.listOf(D4, C3));
    list.add(Nulls.listOf(E4, D3));
    list.add(Nulls.listOf(F4, E3));
    list.add(Nulls.listOf(G4, F3));
    list.add(Nulls.listOf(H4, G3));

    BLACK_EN_PASSANT_CAPTURE_FROM_TO = Nulls.copyOfList(list);
  }

  private static final Map<Square, Square> WHITE_EN_PASSANT_CAPTURE_TO_CAPTURE;

  static {
    final EnumMap<Square, Square> map = new EnumMap<>(Square.class);

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

  private static final Map<Square, Square> BLACK_EN_PASSANT_CAPTURE_TO_CAPTURE;

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

  private static final Map<Square, Square> WHITE_TWO_SQUARE_ADVANCE_TO_EN_PASSANT_CAPTURE_TO;

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

  private static final Map<Square, Square> BLACK_TWO_SQUARE_ADVANCE_TO_EN_PASSANT_CAPTURE_TO;

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
  public static boolean isPawnTwoSquareAdvanceMove(Piece movingPiece, MoveSpecification move) {
    if (movingPiece != Piece.NONE && movingPiece.getPieceType() == PAWN) {
      return switch (movingPiece.getSide()) {
        case WHITE -> Square.WHITE_PAWN_TWO_SQUARE_ADVANCE.contains(calculateFromToSquares(move));
        case BLACK -> Square.BLACK_PAWN_TWO_SQUARE_ADVANCE.contains(calculateFromToSquares(move));
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
    }
    return false;
  }

  private static List<Square> calculateFromToSquares(MoveSpecification move) {
    final List<Square> result = new ArrayList<>();
    result.add(move.fromSquare());
    result.add(move.toSquare());
    return result;
  }

  public static Square calculateEnPassantCaptureTargetSquare(LegalMove legalMove) {
    if (legalMove.kind() == LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE) {
      return calculateEnPassantCaptureTargetSquareForTwoSquareAdvanceMove(legalMove.movingSide(),
          legalMove.moveSpecification());
    }
    return Square.NONE;
  }

  private static Square calculateEnPassantCaptureTargetSquareForTwoSquareAdvanceMove(Side sideToMove,
      MoveSpecification move) {
    switch (sideToMove) {
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

  // Whether the pawn move would be an en-passant capture, evaluated against the position before the move is applied.
  public static boolean isPotentialEnPassantCapture(BitboardPosition bitboardPositionBeforeMove,
      MoveSpecification move) {
    if (CastlingUtility.isCastlingMove(move)) {
      return false;
    }
    final Piece movingPiece = bitboardPositionBeforeMove.get(move.fromSquare());
    if (movingPiece == Piece.NONE || movingPiece.getPieceType() != PAWN) {
      return false;
    }
    return switch (movingPiece.getSide()) {
      case WHITE -> WHITE_EN_PASSANT_CAPTURE_FROM_TO.contains(calculateFromToSquares(move))
          && bitboardPositionBeforeMove.get(move.toSquare()) == Piece.NONE;
      case BLACK -> BLACK_EN_PASSANT_CAPTURE_FROM_TO.contains(calculateFromToSquares(move))
          && bitboardPositionBeforeMove.get(move.toSquare()) == Piece.NONE;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public static Square calculateSquareOfCapturedPawnForEnPassantCapture(Side sideToMove, MoveSpecification move) {
    return calculateSquareOfCapturedPawnForEnPassantCapture(sideToMove, move.toSquare());
  }

  private static Square calculateSquareOfCapturedPawnForEnPassantCapture(Side sideToMove, Square square) {
    switch (sideToMove) {
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

  public static List<UpdateSquare> performEnPassantCaptureMovements(Side sideToMove,
      MoveSpecification moveSpecification) {
    // arriving here, the move must have been identified as en passant capture
    final List<UpdateSquare> result = new ArrayList<>();

    // pawn move: from square becomes empty; on to square is the moved pawn (always a pawn of sideToMove).
    result.add(new UpdateSquare(moveSpecification.fromSquare()));
    result.add(new UpdateSquare(moveSpecification.toSquare(), Piece.of(sideToMove, PieceType.PAWN)));

    // remove the captured pawn (one rank back from the to-square, same file)
    final Square squareOfCapturedPawnForEnPassantCapture = calculateSquareOfCapturedPawnForEnPassantCapture(sideToMove,
        moveSpecification);
    result.add(new UpdateSquare(squareOfCapturedPawnForEnPassantCapture, Piece.NONE));

    return result;
  }

}
