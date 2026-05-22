package com.dlb.chess.moves;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.fen.FenParserAdvanced;
import com.dlb.chess.model.LegalMove;

class TestLegalMovesForPiecesIllegalPosition implements EnumConstants {

  // The relocated reference oracle (RookLegalMoves / BishopLegalMoves / etc.) takes StaticPosition; the FEN parser
  // returns BitboardPosition after Phase 3+4 of the role-inversion release. Test-oracle bridge below derives the
  // StaticPosition from the parser's bitboard output.
  private static StaticPosition staticPositionFromFen(String piecePlacement) {
    return StaticPositionBridge.toStaticPosition(FenParserAdvanced.validatePiecePlacement(piecePlacement));
  }

  @SuppressWarnings("static-method")
  @Test
  void testWhiteToMoveBlackKingInCheck() {

    {
      // white rook not checking - own king not in check
      final StaticPosition position = staticPositionFromFen("7k/7q/8/8/8/8/KB6/R2B4");
      final Set<LegalMove> calculatedLegalMoveSet = RookLegalMoves.calculateRookLegalMoves(position, Side.WHITE, A1);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(A1, B1));
      expected.add(new MoveSpecification(A1, C1));

      check(expected, calculatedLegalMoveSet);
    }

    {
      // white rook not checking - own king in check
      final StaticPosition position = staticPositionFromFen("7k/7q/8/7K/8/8/1B6/R2B4");
      final Set<LegalMove> calculatedLegalMoveSet = RookLegalMoves.calculateRookLegalMoves(position, Side.WHITE, A1);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white rook checking - own king not in check
      final StaticPosition position = staticPositionFromFen("8/7q/6N1/k7/8/8/8/RK1B4");
      final Set<LegalMove> calculatedLegalMoveSet = RookLegalMoves.calculateRookLegalMoves(position, Side.WHITE, A1);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(A1, A2));
      expected.add(new MoveSpecification(A1, A3));
      expected.add(new MoveSpecification(A1, A4));

      check(expected, calculatedLegalMoveSet);
    }

    {
      // white rook checking - own king in check
      final StaticPosition position = staticPositionFromFen("8/7q/6N1/k6K/8/8/8/R2B4");
      final Set<LegalMove> calculatedLegalMoveSet = RookLegalMoves.calculateRookLegalMoves(position, Side.WHITE, A1);

      final Set<MoveSpecification> expected = new TreeSet<>();

      check(expected, calculatedLegalMoveSet);
    }

    {
      // white knight not checking - own king not in check
      final StaticPosition position = staticPositionFromFen("N6k/7q/5B2/8/8/8/K7/R7");
      final Set<LegalMove> calculatedLegalMoveSet = KnightLegalMoves.calculateKnightLegalMoves(position, Side.WHITE,
          A8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(A8, C7));
      expected.add(new MoveSpecification(A8, B6));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white knight not checking - own king in check
      final StaticPosition position = staticPositionFromFen("N6k/8/5B2/8/8/8/K6q/R7");
      final Set<LegalMove> calculatedLegalMoveSet = KnightLegalMoves.calculateKnightLegalMoves(position, Side.WHITE,
          A8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white knight checking - own king not in check
      final StaticPosition position = staticPositionFromFen("7k/7q/6N1/8/8/8/K7/R2B4");
      final Set<LegalMove> calculatedLegalMoveSet = KnightLegalMoves.calculateKnightLegalMoves(position, Side.WHITE,
          G6);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(G6, H4));
      expected.add(new MoveSpecification(G6, F4));
      expected.add(new MoveSpecification(G6, E5));
      expected.add(new MoveSpecification(G6, E7));
      expected.add(new MoveSpecification(G6, F8));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white knight checking - own king in check
      final StaticPosition position = staticPositionFromFen("7k/8/6N1/8/8/8/K6q/R2B4");
      final Set<LegalMove> calculatedLegalMoveSet = KnightLegalMoves.calculateKnightLegalMoves(position, Side.WHITE,
          G6);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white bishop not checking - own king not in check
      final StaticPosition position = staticPositionFromFen("7k/7q/6N1/8/8/8/K1P5/R2B4");
      final Set<LegalMove> calculatedLegalMoveSet = BishopLegalMoves.calculateBishopLegalMoves(position, Side.WHITE,
          D1);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(D1, E2));
      expected.add(new MoveSpecification(D1, F3));
      expected.add(new MoveSpecification(D1, G4));
      expected.add(new MoveSpecification(D1, H5));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white bishop not checking - own king in check
      final StaticPosition position = staticPositionFromFen("7k/8/6N1/8/2q5/8/K1P5/R2B4");
      final Set<LegalMove> calculatedLegalMoveSet = BishopLegalMoves.calculateBishopLegalMoves(position, Side.WHITE,
          D1);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white bishop checking - own king not in check
      final StaticPosition position = staticPositionFromFen("6k1/8/8/3B4/2q1N3/K7/2P5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = BishopLegalMoves.calculateBishopLegalMoves(position, Side.WHITE,
          D5);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(D5, E6));
      expected.add(new MoveSpecification(D5, F7));
      expected.add(new MoveSpecification(D5, C6));
      expected.add(new MoveSpecification(D5, B7));
      expected.add(new MoveSpecification(D5, A8));
      expected.add(new MoveSpecification(D5, C4));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white bishop checking - own king in check
      final StaticPosition position = staticPositionFromFen("6k1/8/8/3B4/2q1N3/8/K1P5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = BishopLegalMoves.calculateBishopLegalMoves(position, Side.WHITE,
          D5);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(D5, C4));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white queen not checking - own king not in check
      final StaticPosition position = staticPositionFromFen("6k1/8/8/3B4/q3N3/Q7/K1P5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = QueenLegalMoves.calculateQueenLegalMoves(position, Side.WHITE, A3);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(A3, A4));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white queen not checking - own king in check
      final StaticPosition position = staticPositionFromFen("6k1/8/8/3B4/4N3/Q7/K1q5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = QueenLegalMoves.calculateQueenLegalMoves(position, Side.WHITE, A3);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(A3, B2));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white queen checking - own king not in check
      final StaticPosition position = staticPositionFromFen("Q3k3/B7/8/8/4N3/2q5/K7/R7");
      final Set<LegalMove> calculatedLegalMoveSet = QueenLegalMoves.calculateQueenLegalMoves(position, Side.WHITE, A8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(A8, B8));
      expected.add(new MoveSpecification(A8, C8));
      expected.add(new MoveSpecification(A8, D8));
      expected.add(new MoveSpecification(A8, B7));
      expected.add(new MoveSpecification(A8, C6));
      expected.add(new MoveSpecification(A8, D5));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white queen checking - own king in check
      final StaticPosition position = staticPositionFromFen("Q3k3/B7/8/8/4N3/8/K1q5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = QueenLegalMoves.calculateQueenLegalMoves(position, Side.WHITE, A8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white pawn not checking - own king not in check
      final StaticPosition position = staticPositionFromFen("6k1/8/8/3B4/q3N3/Q7/K1P5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = PawnLegalMoves.calculatePawnLegalMoves(position, Square.NONE,
          Side.WHITE, C2);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(C2, C3));
      expected.add(new MoveSpecification(C2, C4));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white pawn not checking - own king in check
      final StaticPosition position = staticPositionFromFen("6k1/8/8/3B4/2q1N3/Q7/K1P5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = PawnLegalMoves.calculatePawnLegalMoves(position, Square.NONE,
          Side.WHITE, C2);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white pawn checking - own king not in check
      final StaticPosition position = staticPositionFromFen("8/8/8/2qB4/4N3/Q2k4/K1P5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = PawnLegalMoves.calculatePawnLegalMoves(position, Square.NONE,
          Side.WHITE, C2);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(C2, C3));
      expected.add(new MoveSpecification(C2, C4));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white pawn checking - own king in check
      final StaticPosition position = staticPositionFromFen("6q1/8/8/8/4N3/Q2k4/K1P5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = PawnLegalMoves.calculatePawnLegalMoves(position, Square.NONE,
          Side.WHITE, C2);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(C2, C4));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white king not in check - kings not next to each other
      final StaticPosition position = staticPositionFromFen("5q2/8/8/8/4N3/Q2k4/K1P5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = KingLegalMoves.calculateKingLegalMoves(position, CastlingRight.NONE,
          Side.WHITE, A2);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(A2, B1));
      expected.add(new MoveSpecification(A2, B2));
      expected.add(new MoveSpecification(A2, B3));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // white king in check - kings not next to each other
      final StaticPosition position = staticPositionFromFen("8/5q2/8/8/4N3/Q2k4/K1P5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = KingLegalMoves.calculateKingLegalMoves(position, CastlingRight.NONE,
          Side.WHITE, A2);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(A2, B1));
      expected.add(new MoveSpecification(A2, B2));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // kings next to each other
      final StaticPosition position = staticPositionFromFen("8/5q2/8/8/4N3/Q1Kk4/2P5/R7");
      final Set<LegalMove> calculatedLegalMoveSet = KingLegalMoves.calculateKingLegalMoves(position, CastlingRight.NONE,
          Side.WHITE, C3);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(C3, B4));
      expected.add(new MoveSpecification(C3, B2));
      check(expected, calculatedLegalMoveSet);
    }

    // white bishop not checking - own king not in check
    // white bishop not checking - own king in check
    // white bishop checking - own king not in check
    // white bishop checking - own king in check

  }

  @SuppressWarnings("static-method")
  @Test
  void testBlackToMoveWhiteKingInCheck() {

    {
      // black rook not checking - own king not in check
      final StaticPosition position = staticPositionFromFen("rr2k3/1B5q/8/8/8/8/KB6/R7");
      final Set<LegalMove> calculatedLegalMoveSet = RookLegalMoves.calculateRookLegalMoves(position, Side.BLACK, B8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(B8, B7));
      expected.add(new MoveSpecification(B8, C8));
      expected.add(new MoveSpecification(B8, D8));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black rook not checking - own king in check
      final StaticPosition position = staticPositionFromFen("rr2k1R1/1B5q/8/8/8/8/KB6/R7");
      final Set<LegalMove> calculatedLegalMoveSet = RookLegalMoves.calculateRookLegalMoves(position, Side.BLACK, B8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black rook checking - own king not in check
      final StaticPosition position = staticPositionFromFen("r3k3/rB5q/8/8/K7/8/1B6/R5R1");
      final Set<LegalMove> calculatedLegalMoveSet = RookLegalMoves.calculateRookLegalMoves(position, Side.BLACK, A7);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(A7, A6));
      expected.add(new MoveSpecification(A7, A5));
      expected.add(new MoveSpecification(A7, B7));

      check(expected, calculatedLegalMoveSet);
    }

    {
      // black rook checking - own king in check
      final StaticPosition position = staticPositionFromFen("r3k1R1/rB5q/8/8/K7/8/1B6/R7");
      final Set<LegalMove> calculatedLegalMoveSet = RookLegalMoves.calculateRookLegalMoves(position, Side.BLACK, A7);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black knight not checking - own king not in check
      final StaticPosition position = staticPositionFromFen("r5n1/rB2k2q/8/8/K7/8/1B6/R7");
      final Set<LegalMove> calculatedLegalMoveSet = KnightLegalMoves.calculateKnightLegalMoves(position, Side.BLACK,
          G8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(G8, H6));
      expected.add(new MoveSpecification(G8, F6));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black knight not checking - own king in check
      final StaticPosition position = staticPositionFromFen("r3k1n1/rB5q/8/8/K3R3/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = KnightLegalMoves.calculateKnightLegalMoves(position, Side.BLACK,
          G8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(G8, E7));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black knight checking - own king not in check
      final StaticPosition position = staticPositionFromFen("r3k1n1/rB5q/5K2/8/3R4/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = KnightLegalMoves.calculateKnightLegalMoves(position, Side.BLACK,
          G8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(G8, E7));
      expected.add(new MoveSpecification(G8, H6));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black knight checking - own king in check
      final StaticPosition position = staticPositionFromFen("r3k2n/rB5q/5K2/8/4R3/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = KnightLegalMoves.calculateKnightLegalMoves(position, Side.BLACK,
          H8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black bishop not checking - own king not in check
      final StaticPosition position = staticPositionFromFen("r3k2n/rB6/5K1q/8/5R2/8/1B5b/8");
      final Set<LegalMove> calculatedLegalMoveSet = BishopLegalMoves.calculateBishopLegalMoves(position, Side.BLACK,
          H2);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(H2, G1));
      expected.add(new MoveSpecification(H2, G3));
      expected.add(new MoveSpecification(H2, F4));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black bishop not checking - own king in check
      final StaticPosition position = staticPositionFromFen("r3k2n/rB6/5K1q/8/4R3/8/1B5b/8");
      final Set<LegalMove> calculatedLegalMoveSet = BishopLegalMoves.calculateBishopLegalMoves(position, Side.BLACK,
          H2);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(H2, E5));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black bishop checking - own king not in check (pinned)
      final StaticPosition position = staticPositionFromFen("r3k2n/rB6/5K1q/4b3/4R3/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = BishopLegalMoves.calculateBishopLegalMoves(position, Side.BLACK,
          E5);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black bishop checking - own king in check
      final StaticPosition position = staticPositionFromFen("r2Rk2n/rB6/5K1q/4b3/8/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = BishopLegalMoves.calculateBishopLegalMoves(position, Side.BLACK,
          E5);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black queen not checking - own king not in check
      final StaticPosition position = staticPositionFromFen("r3k2q/1B4bn/8/r2K4/8/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = QueenLegalMoves.calculateQueenLegalMoves(position, Side.BLACK, H8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(H8, G8));
      expected.add(new MoveSpecification(H8, F8));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black queen not checking - own king in check
      final StaticPosition position = staticPositionFromFen("r2Rk2q/1B4bn/8/r2K4/8/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = QueenLegalMoves.calculateQueenLegalMoves(position, Side.BLACK, H8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black queen checking - own king not in check
      final StaticPosition position = staticPositionFromFen("r2R1K1q/1B4bn/8/r7/8/6k1/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = QueenLegalMoves.calculateQueenLegalMoves(position, Side.BLACK, H8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(H8, G8));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black queen checking - own king in check
      final StaticPosition position = staticPositionFromFen("rR1K1q2/1B4bn/8/r7/4B3/8/6k1/8");
      final Set<LegalMove> calculatedLegalMoveSet = QueenLegalMoves.calculateQueenLegalMoves(position, Side.BLACK, F8);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(F8, F3));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black pawn not checking - own king not in check
      final StaticPosition position = staticPositionFromFen("r2R3q/1B2p1bn/8/r3K3/8/6k1/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = PawnLegalMoves.calculatePawnLegalMoves(position, Square.NONE,
          Side.BLACK, E7);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(E7, E6));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black pawn not checking - own king in check
      final StaticPosition position = staticPositionFromFen("r6q/1B2p1bn/8/r2R2k1/4K3/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = PawnLegalMoves.calculatePawnLegalMoves(position, Square.NONE,
          Side.BLACK, E7);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(E7, E5));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black pawn checking - own king not in check
      final StaticPosition position = staticPositionFromFen("r4b1q/1B2p2n/5K2/r2R4/8/6k1/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = PawnLegalMoves.calculatePawnLegalMoves(position, Square.NONE,
          Side.BLACK, E7);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(E7, E6));
      expected.add(new MoveSpecification(E7, E5));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black pawn checking - own king in check
      final StaticPosition position = staticPositionFromFen("r4b1q/1B2p2n/5K2/r2R3k/8/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = PawnLegalMoves.calculatePawnLegalMoves(position, Square.NONE,
          Side.BLACK, E7);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(E7, E5));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black king not in check - kings not next to each other
      final StaticPosition position = staticPositionFromFen("r4b1q/1B2p2n/5K2/r6k/3R4/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = KingLegalMoves.calculateKingLegalMoves(position, CastlingRight.NONE,
          Side.BLACK, H5);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(H5, H6));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // black king in check - kings not next to each other
      final StaticPosition position = staticPositionFromFen("r4b1q/1B2p2n/5K2/r2R3k/8/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = KingLegalMoves.calculateKingLegalMoves(position, CastlingRight.NONE,
          Side.BLACK, H5);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(H5, H4));
      expected.add(new MoveSpecification(H5, G4));
      expected.add(new MoveSpecification(H5, H6));
      check(expected, calculatedLegalMoveSet);
    }

    {
      // kings next to each other
      final StaticPosition position = staticPositionFromFen("r4b1q/1B2p2n/8/r2R1Kk1/8/8/1B6/8");
      final Set<LegalMove> calculatedLegalMoveSet = KingLegalMoves.calculateKingLegalMoves(position, CastlingRight.NONE,
          Side.BLACK, G5);

      final Set<MoveSpecification> expected = new TreeSet<>();
      expected.add(new MoveSpecification(G5, H4));
      expected.add(new MoveSpecification(G5, H5));
      expected.add(new MoveSpecification(G5, H6));
      check(expected, calculatedLegalMoveSet);
    }

    // black bishop not checking - own king not in check
    // black bishop not checking - own king in check
    // black bishop checking - own king not in check
    // black bishop checking - own king in check

  }

  private static void check(Set<MoveSpecification> expected, Set<LegalMove> calculatedLegalMoveSet) {
    final Set<MoveSpecification> actual = toMoveSpecifications(calculatedLegalMoveSet);
    assertEquals(expected, actual);

  }

  private static Set<MoveSpecification> toMoveSpecifications(Set<LegalMove> legalMoveSet) {
    final Set<MoveSpecification> result = new TreeSet<>();
    for (final LegalMove legalMove : legalMoveSet) {
      result.add(legalMove.moveSpecification());
    }
    return result;
  }
}
