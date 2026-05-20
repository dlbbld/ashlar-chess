package com.dlb.chess.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.CastlingMove;
import com.dlb.chess.board.enums.PromotionPieceType;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.bitboard.StaticPositionBridge;

class TestPerformMoveMainlyStaticPositionState implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testGameWithMostBasicMoves() {
    final Board apiBoard = new Board(false);
    final StaticPosition staticPosition0 = StaticPosition.INITIAL_POSITION;
    assertEquals(staticPosition0, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    StaticPosition workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move1W = new MoveSpecification(E2, E4);
    apiBoard.move(move1W);
    workingPosition = workingPosition.createChangedPosition(E2);
    final StaticPosition staticPosition1W = workingPosition.createChangedPosition(E4, WHITE_PAWN);
    assertEquals(staticPosition1W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("e4", apiBoard.getSan());
    assertEquals("e2e4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move1B = new MoveSpecification(C7, C5);
    apiBoard.move(move1B);
    workingPosition = workingPosition.createChangedPosition(C7);
    final StaticPosition staticPosition1B = workingPosition.createChangedPosition(C5, BLACK_PAWN);
    assertEquals(staticPosition1B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("c5", apiBoard.getSan());
    assertEquals("c7c5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move2W = new MoveSpecification(G1, F3);
    apiBoard.move(move2W);
    workingPosition = workingPosition.createChangedPosition(G1);
    final StaticPosition staticPosition2W = workingPosition.createChangedPosition(F3, WHITE_KNIGHT);
    assertEquals(staticPosition2W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nf3", apiBoard.getSan());
    assertEquals("Ng1f3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move2B = new MoveSpecification(B8, C6);
    apiBoard.move(move2B);
    workingPosition = workingPosition.createChangedPosition(B8);
    final StaticPosition staticPosition2B = workingPosition.createChangedPosition(C6, BLACK_KNIGHT);
    assertEquals(staticPosition2B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Nc6", apiBoard.getSan());
    assertEquals("Nb8c6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move3W = new MoveSpecification(F1, C4);
    apiBoard.move(move3W);
    workingPosition = workingPosition.createChangedPosition(F1);
    final StaticPosition staticPosition3W = workingPosition.createChangedPosition(C4, WHITE_BISHOP);
    assertEquals(staticPosition3W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Bc4", apiBoard.getSan());
    assertEquals("Bf1c4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move3B = new MoveSpecification(D7, D6);
    apiBoard.move(move3B);
    workingPosition = workingPosition.createChangedPosition(D7);
    final StaticPosition staticPosition3B = workingPosition.createChangedPosition(D6, BLACK_PAWN);
    assertEquals(staticPosition3B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("d6", apiBoard.getSan());
    assertEquals("d7d6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move4W = new MoveSpecification(B1, C3);
    apiBoard.move(move4W);
    workingPosition = workingPosition.createChangedPosition(B1);
    final StaticPosition staticPosition4W = workingPosition.createChangedPosition(C3, WHITE_KNIGHT);
    assertEquals(staticPosition4W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nc3", apiBoard.getSan());
    assertEquals("Nb1c3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move4B = new MoveSpecification(C8, G4);
    apiBoard.move(move4B);
    workingPosition = workingPosition.createChangedPosition(C8);
    final StaticPosition staticPosition4B = workingPosition.createChangedPosition(G4, BLACK_BISHOP);
    assertEquals(staticPosition4B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Bg4", apiBoard.getSan());
    assertEquals("Bc8g4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move5W = new MoveSpecification(D2, D3);
    apiBoard.move(move5W);
    workingPosition = workingPosition.createChangedPosition(D2);
    final StaticPosition staticPosition5W = workingPosition.createChangedPosition(D3, WHITE_PAWN);
    assertEquals(staticPosition5W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("d3", apiBoard.getSan());
    assertEquals("d2d3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move5B = new MoveSpecification(G8, F6);
    apiBoard.move(move5B);
    workingPosition = workingPosition.createChangedPosition(G8);
    final StaticPosition staticPosition5B = workingPosition.createChangedPosition(F6, BLACK_KNIGHT);
    assertEquals(staticPosition5B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Nf6", apiBoard.getSan());
    assertEquals("Ng8f6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move6W = new MoveSpecification(C1, F4);
    apiBoard.move(move6W);
    workingPosition = workingPosition.createChangedPosition(C1);
    final StaticPosition staticPosition6W = workingPosition.createChangedPosition(F4, WHITE_BISHOP);
    assertEquals(staticPosition6W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Bf4", apiBoard.getSan());
    assertEquals("Bc1f4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move6B = new MoveSpecification(E7, E5);
    apiBoard.move(move6B);
    workingPosition = workingPosition.createChangedPosition(E7);
    final StaticPosition staticPosition6B = workingPosition.createChangedPosition(E5, BLACK_PAWN);
    assertEquals(staticPosition6B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("e5", apiBoard.getSan());
    assertEquals("e7e5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move7W = new MoveSpecification(D1, E2);
    apiBoard.move(move7W);
    workingPosition = workingPosition.createChangedPosition(D1);
    final StaticPosition staticPosition7W = workingPosition.createChangedPosition(E2, WHITE_QUEEN);
    assertEquals(staticPosition7W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qe2", apiBoard.getSan());
    assertEquals("Qd1e2", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move7B = new MoveSpecification(F8, E7);
    apiBoard.move(move7B);
    workingPosition = workingPosition.createChangedPosition(F8);
    final StaticPosition staticPosition7B = workingPosition.createChangedPosition(E7, BLACK_BISHOP);
    assertEquals(staticPosition7B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Be7", apiBoard.getSan());
    assertEquals("Bf8e7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move8W = new MoveSpecification(A1, D1);
    apiBoard.move(move8W);
    workingPosition = workingPosition.createChangedPosition(A1);
    final StaticPosition staticPosition8W = workingPosition.createChangedPosition(D1, WHITE_ROOK);
    assertEquals(staticPosition8W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Rd1", apiBoard.getSan());
    assertEquals("Ra1d1", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move8B = new MoveSpecification(D8, D7);
    apiBoard.move(move8B);
    workingPosition = workingPosition.createChangedPosition(D8);
    final StaticPosition staticPosition8B = workingPosition.createChangedPosition(D7, BLACK_QUEEN);
    assertEquals(staticPosition8B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Qd7", apiBoard.getSan());
    assertEquals("Qd8d7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move9W = new MoveSpecification(CastlingMove.KING_SIDE);
    apiBoard.move(move9W);
    workingPosition = workingPosition.createChangedPosition(E1);
    workingPosition = workingPosition.createChangedPosition(G1, WHITE_KING);
    workingPosition = workingPosition.createChangedPosition(H1);
    final StaticPosition staticPosition9W = workingPosition.createChangedPosition(F1, WHITE_ROOK);
    assertEquals(staticPosition9W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("O-O", apiBoard.getSan());
    assertEquals("O-O", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move9B = new MoveSpecification(CastlingMove.QUEEN_SIDE);
    apiBoard.move(move9B);
    workingPosition = workingPosition.createChangedPosition(E8);
    workingPosition = workingPosition.createChangedPosition(C8, BLACK_KING);
    workingPosition = workingPosition.createChangedPosition(A8);
    final StaticPosition staticPosition9B = workingPosition.createChangedPosition(D8, BLACK_ROOK);
    assertEquals(staticPosition9B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("O-O-O", apiBoard.getSan());
    assertEquals("O-O-O", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move10W = new MoveSpecification(F1, E1);
    apiBoard.move(move10W);
    workingPosition = workingPosition.createChangedPosition(F1);
    final StaticPosition staticPosition10W = workingPosition.createChangedPosition(E1, WHITE_ROOK);
    assertEquals(staticPosition10W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Rfe1", apiBoard.getSan());
    assertEquals("Rf1e1", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move10B = new MoveSpecification(D8, E8);
    apiBoard.move(move10B);
    workingPosition = workingPosition.createChangedPosition(D8);
    final StaticPosition staticPosition10B = workingPosition.createChangedPosition(E8, BLACK_ROOK);
    assertEquals(staticPosition10B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Rde8", apiBoard.getSan());
    assertEquals("Rd8e8", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move11W = new MoveSpecification(G1, H1);
    apiBoard.move(move11W);
    workingPosition = workingPosition.createChangedPosition(G1);
    final StaticPosition staticPosition11W = workingPosition.createChangedPosition(H1, WHITE_KING);
    assertEquals(staticPosition11W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Kh1", apiBoard.getSan());
    assertEquals("Kg1h1", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move11B = new MoveSpecification(C8, B8);
    apiBoard.move(move11B);
    workingPosition = workingPosition.createChangedPosition(C8);
    final StaticPosition staticPosition11B = workingPosition.createChangedPosition(B8, BLACK_KING);
    assertEquals(staticPosition11B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kb8", apiBoard.getSan());
    assertEquals("Kc8b8", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move12W = new MoveSpecification(H2, H4);
    apiBoard.move(move12W);
    workingPosition = workingPosition.createChangedPosition(H2);
    final StaticPosition staticPosition12W = workingPosition.createChangedPosition(H4, WHITE_PAWN);
    assertEquals(staticPosition12W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("h4", apiBoard.getSan());
    assertEquals("h2h4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move12B = new MoveSpecification(A7, A5);
    apiBoard.move(move12B);
    workingPosition = workingPosition.createChangedPosition(A7);
    final StaticPosition staticPosition12B = workingPosition.createChangedPosition(A5, BLACK_PAWN);
    assertEquals(staticPosition12B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("a5", apiBoard.getSan());
    assertEquals("a7a5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move13W = new MoveSpecification(H4, H5);
    apiBoard.move(move13W);
    workingPosition = workingPosition.createChangedPosition(H4);
    final StaticPosition staticPosition13W = workingPosition.createChangedPosition(H5, WHITE_PAWN);
    assertEquals(staticPosition13W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("h5", apiBoard.getSan());
    assertEquals("h4h5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move13B = new MoveSpecification(G7, G5);
    apiBoard.move(move13B);
    workingPosition = workingPosition.createChangedPosition(G7);
    final StaticPosition staticPosition13B = workingPosition.createChangedPosition(G5, BLACK_PAWN);
    assertEquals(staticPosition13B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("g5", apiBoard.getSan());
    assertEquals("g7g5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move14W = new MoveSpecification(H5, G6);
    apiBoard.move(move14W);
    workingPosition = workingPosition.createChangedPosition(H5);
    workingPosition = workingPosition.createChangedPosition(G6, WHITE_PAWN);
    final StaticPosition staticPosition14W = workingPosition.createChangedPosition(G5);
    assertEquals(staticPosition14W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("hxg6", apiBoard.getSan());
    assertEquals("h5xg6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move14B = new MoveSpecification(A5, A4);
    apiBoard.move(move14B);
    workingPosition = workingPosition.createChangedPosition(A5);
    final StaticPosition staticPosition14B = workingPosition.createChangedPosition(A4, BLACK_PAWN);
    assertEquals(staticPosition14B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("a4", apiBoard.getSan());
    assertEquals("a5a4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move15W = new MoveSpecification(B2, B4);
    apiBoard.move(move15W);
    workingPosition = workingPosition.createChangedPosition(B2);
    final StaticPosition staticPosition15W = workingPosition.createChangedPosition(B4, WHITE_PAWN);
    assertEquals(staticPosition15W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("b4", apiBoard.getSan());
    assertEquals("b2b4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move15B = new MoveSpecification(A4, B3);
    apiBoard.move(move15B);
    workingPosition = workingPosition.createChangedPosition(A4);
    workingPosition = workingPosition.createChangedPosition(B3, BLACK_PAWN);
    final StaticPosition staticPosition15B = workingPosition.createChangedPosition(B4);
    assertEquals(staticPosition15B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("axb3", apiBoard.getSan());
    assertEquals("a4xb3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move16W = new MoveSpecification(G6, H7);
    apiBoard.move(move16W);
    workingPosition = workingPosition.createChangedPosition(G6);
    final StaticPosition staticPosition16W = workingPosition.createChangedPosition(H7, WHITE_PAWN);
    assertEquals(staticPosition16W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("gxh7", apiBoard.getSan());
    assertEquals("g6xh7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move16B = new MoveSpecification(B3, A2);
    apiBoard.move(move16B);
    workingPosition = workingPosition.createChangedPosition(B3);
    final StaticPosition staticPosition16B = workingPosition.createChangedPosition(A2, BLACK_PAWN);
    assertEquals(staticPosition16B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("bxa2", apiBoard.getSan());
    assertEquals("b3xa2", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move17W = new MoveSpecification(F4, E5);
    apiBoard.move(move17W);
    workingPosition = workingPosition.createChangedPosition(F4);
    final StaticPosition staticPosition17W = workingPosition.createChangedPosition(E5, WHITE_BISHOP);
    assertEquals(staticPosition17W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Bxe5", apiBoard.getSan());
    assertEquals("Bf4xe5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move17B = new MoveSpecification(A2, A1, PromotionPieceType.QUEEN);
    apiBoard.move(move17B);
    workingPosition = workingPosition.createChangedPosition(A2);
    final StaticPosition staticPosition17B = workingPosition.createChangedPosition(A1, BLACK_QUEEN);
    assertEquals(staticPosition17B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("a1=Q", apiBoard.getSan());
    assertEquals("a2a1=Q", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move18W = new MoveSpecification(F3, G5);
    apiBoard.move(move18W);
    workingPosition = workingPosition.createChangedPosition(F3);
    final StaticPosition staticPosition18W = workingPosition.createChangedPosition(G5, WHITE_KNIGHT);
    assertEquals(staticPosition18W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Ng5", apiBoard.getSan());
    assertEquals("Nf3g5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move18B = new MoveSpecification(H8, G8);
    apiBoard.move(move18B);
    workingPosition = workingPosition.createChangedPosition(H8);
    final StaticPosition staticPosition18B = workingPosition.createChangedPosition(G8, BLACK_ROOK);
    assertEquals(staticPosition18B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Rhg8", apiBoard.getSan());
    assertEquals("Rh8g8", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move19W = new MoveSpecification(H7, G8, PromotionPieceType.KNIGHT);
    apiBoard.move(move19W);
    workingPosition = workingPosition.createChangedPosition(H7);
    final StaticPosition staticPosition19W = workingPosition.createChangedPosition(G8, WHITE_KNIGHT);
    assertEquals(staticPosition19W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("hxg8=N", apiBoard.getSan());
    assertEquals("h7xg8=N", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move19B = new MoveSpecification(C6, A5);
    apiBoard.move(move19B);
    workingPosition = workingPosition.createChangedPosition(C6);
    final StaticPosition staticPosition19B = workingPosition.createChangedPosition(A5, BLACK_KNIGHT);
    assertEquals(staticPosition19B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Na5", apiBoard.getSan());
    assertEquals("Nc6a5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move20W = new MoveSpecification(G5, F7);
    apiBoard.move(move20W);
    workingPosition = workingPosition.createChangedPosition(G5);
    final StaticPosition staticPosition20W = workingPosition.createChangedPosition(F7, WHITE_KNIGHT);
    assertEquals(staticPosition20W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nxf7", apiBoard.getSan());
    assertEquals("Ng5xf7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move20B = new MoveSpecification(A5, C4);
    apiBoard.move(move20B);
    workingPosition = workingPosition.createChangedPosition(A5);
    final StaticPosition staticPosition20B = workingPosition.createChangedPosition(C4, BLACK_KNIGHT);
    assertEquals(staticPosition20B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Nxc4", apiBoard.getSan());
    assertEquals("Na5xc4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move21W = new MoveSpecification(G8, E7);
    apiBoard.move(move21W);
    workingPosition = workingPosition.createChangedPosition(G8);
    final StaticPosition staticPosition21W = workingPosition.createChangedPosition(E7, WHITE_KNIGHT);
    assertEquals(staticPosition21W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nxe7", apiBoard.getSan());
    assertEquals("Ng8xe7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move21B = new MoveSpecification(E8, E7);
    apiBoard.move(move21B);
    workingPosition = workingPosition.createChangedPosition(E8);
    final StaticPosition staticPosition21B = workingPosition.createChangedPosition(E7, BLACK_ROOK);
    assertEquals(staticPosition21B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Rxe7", apiBoard.getSan());
    assertEquals("Re8xe7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move22W = new MoveSpecification(F2, F3);
    apiBoard.move(move22W);
    workingPosition = workingPosition.createChangedPosition(F2);
    final StaticPosition staticPosition22W = workingPosition.createChangedPosition(F3, WHITE_PAWN);
    assertEquals(staticPosition22W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("f3", apiBoard.getSan());
    assertEquals("f2f3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move22B = new MoveSpecification(A1, D1);
    apiBoard.move(move22B);
    workingPosition = workingPosition.createChangedPosition(A1);
    final StaticPosition staticPosition22B = workingPosition.createChangedPosition(D1, BLACK_QUEEN);
    assertEquals(staticPosition22B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Qxd1", apiBoard.getSan());
    assertEquals("Qa1xd1", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move23W = new MoveSpecification(E1, D1);
    apiBoard.move(move23W);
    workingPosition = workingPosition.createChangedPosition(E1);
    final StaticPosition staticPosition23W = workingPosition.createChangedPosition(D1, WHITE_ROOK);
    assertEquals(staticPosition23W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Rxd1", apiBoard.getSan());
    assertEquals("Re1xd1", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move23B = new MoveSpecification(G4, F3);
    apiBoard.move(move23B);
    workingPosition = workingPosition.createChangedPosition(G4);
    final StaticPosition staticPosition23B = workingPosition.createChangedPosition(F3, BLACK_BISHOP);
    assertEquals(staticPosition23B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Bxf3", apiBoard.getSan());
    assertEquals("Bg4xf3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move24W = new MoveSpecification(G2, F3);
    apiBoard.move(move24W);
    workingPosition = workingPosition.createChangedPosition(G2);
    final StaticPosition staticPosition24W = workingPosition.createChangedPosition(F3, WHITE_PAWN);
    assertEquals(staticPosition24W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("gxf3", apiBoard.getSan());
    assertEquals("g2xf3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move24B = new MoveSpecification(C4, E5);
    apiBoard.move(move24B);
    workingPosition = workingPosition.createChangedPosition(C4);
    final StaticPosition staticPosition24B = workingPosition.createChangedPosition(E5, BLACK_KNIGHT);
    assertEquals(staticPosition24B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Nxe5", apiBoard.getSan());
    assertEquals("Nc4xe5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move25W = new MoveSpecification(F7, E5);
    apiBoard.move(move25W);
    workingPosition = workingPosition.createChangedPosition(F7);
    final StaticPosition staticPosition25W = workingPosition.createChangedPosition(E5, WHITE_KNIGHT);
    assertEquals(staticPosition25W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nxe5", apiBoard.getSan());
    assertEquals("Nf7xe5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move25B = new MoveSpecification(F6, E4);
    apiBoard.move(move25B);
    workingPosition = workingPosition.createChangedPosition(F6);
    final StaticPosition staticPosition25B = workingPosition.createChangedPosition(E4, BLACK_KNIGHT);
    assertEquals(staticPosition25B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Nxe4", apiBoard.getSan());
    assertEquals("Nf6xe4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move26W = new MoveSpecification(D3, E4);
    apiBoard.move(move26W);
    workingPosition = workingPosition.createChangedPosition(D3);
    final StaticPosition staticPosition26W = workingPosition.createChangedPosition(E4, WHITE_PAWN);
    assertEquals(staticPosition26W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("dxe4", apiBoard.getSan());
    assertEquals("d3xe4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move26B = new MoveSpecification(E7, E5);
    apiBoard.move(move26B);
    workingPosition = workingPosition.createChangedPosition(E7);
    final StaticPosition staticPosition26B = workingPosition.createChangedPosition(E5, BLACK_ROOK);
    assertEquals(staticPosition26B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Rxe5", apiBoard.getSan());
    assertEquals("Re7xe5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move27W = new MoveSpecification(D1, D6);
    apiBoard.move(move27W);
    workingPosition = workingPosition.createChangedPosition(D1);
    final StaticPosition staticPosition27W = workingPosition.createChangedPosition(D6, WHITE_ROOK);
    assertEquals(staticPosition27W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Rxd6", apiBoard.getSan());
    assertEquals("Rd1xd6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move27B = new MoveSpecification(D7, D6);
    apiBoard.move(move27B);
    workingPosition = workingPosition.createChangedPosition(D7);
    final StaticPosition staticPosition27B = workingPosition.createChangedPosition(D6, BLACK_QUEEN);
    assertEquals(staticPosition27B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Qxd6", apiBoard.getSan());
    assertEquals("Qd7xd6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move28W = new MoveSpecification(C3, D5);
    apiBoard.move(move28W);
    workingPosition = workingPosition.createChangedPosition(C3);
    final StaticPosition staticPosition28W = workingPosition.createChangedPosition(D5, WHITE_KNIGHT);
    assertEquals(staticPosition28W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nd5", apiBoard.getSan());
    assertEquals("Nc3d5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move28B = new MoveSpecification(E5, E4);
    apiBoard.move(move28B);
    workingPosition = workingPosition.createChangedPosition(E5);
    final StaticPosition staticPosition28B = workingPosition.createChangedPosition(E4, BLACK_ROOK);
    assertEquals(staticPosition28B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Rxe4", apiBoard.getSan());
    assertEquals("Re5xe4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move29W = new MoveSpecification(E2, E4);
    apiBoard.move(move29W);
    workingPosition = workingPosition.createChangedPosition(E2);
    final StaticPosition staticPosition29W = workingPosition.createChangedPosition(E4, WHITE_QUEEN);
    assertEquals(staticPosition29W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qxe4", apiBoard.getSan());
    assertEquals("Qe2xe4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move29B = new MoveSpecification(D6, D5);
    apiBoard.move(move29B);
    workingPosition = workingPosition.createChangedPosition(D6);
    final StaticPosition staticPosition29B = workingPosition.createChangedPosition(D5, BLACK_QUEEN);
    assertEquals(staticPosition29B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Qxd5", apiBoard.getSan());
    assertEquals("Qd6xd5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move30W = new MoveSpecification(E4, D5);
    apiBoard.move(move30W);
    workingPosition = workingPosition.createChangedPosition(E4);
    final StaticPosition staticPosition30W = workingPosition.createChangedPosition(D5, WHITE_QUEEN);
    assertEquals(staticPosition30W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qxd5", apiBoard.getSan());
    assertEquals("Qe4xd5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move30B = new MoveSpecification(B7, B5);
    apiBoard.move(move30B);
    workingPosition = workingPosition.createChangedPosition(B7);
    final StaticPosition staticPosition30B = workingPosition.createChangedPosition(B5, BLACK_PAWN);
    assertEquals(staticPosition30B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("b5", apiBoard.getSan());
    assertEquals("b7b5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move31W = new MoveSpecification(C2, C4);
    apiBoard.move(move31W);
    workingPosition = workingPosition.createChangedPosition(C2);
    final StaticPosition staticPosition31W = workingPosition.createChangedPosition(C4, WHITE_PAWN);
    assertEquals(staticPosition31W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("c4", apiBoard.getSan());
    assertEquals("c2c4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move31B = new MoveSpecification(B5, C4);
    apiBoard.move(move31B);
    workingPosition = workingPosition.createChangedPosition(B5);
    final StaticPosition staticPosition31B = workingPosition.createChangedPosition(C4, BLACK_PAWN);
    assertEquals(staticPosition31B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("bxc4", apiBoard.getSan());
    assertEquals("b5xc4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move32W = new MoveSpecification(F3, F4);
    apiBoard.move(move32W);
    workingPosition = workingPosition.createChangedPosition(F3);
    final StaticPosition staticPosition32W = workingPosition.createChangedPosition(F4, WHITE_PAWN);
    assertEquals(staticPosition32W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("f4", apiBoard.getSan());
    assertEquals("f3f4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move32B = new MoveSpecification(B8, C7);
    apiBoard.move(move32B);
    workingPosition = workingPosition.createChangedPosition(B8);
    final StaticPosition staticPosition32B = workingPosition.createChangedPosition(C7, BLACK_KING);
    assertEquals(staticPosition32B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kc7", apiBoard.getSan());
    assertEquals("Kb8c7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move33W = new MoveSpecification(D5, C4);
    apiBoard.move(move33W);
    workingPosition = workingPosition.createChangedPosition(D5);
    final StaticPosition staticPosition33W = workingPosition.createChangedPosition(C4, WHITE_QUEEN);
    assertEquals(staticPosition33W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qxc4", apiBoard.getSan());
    assertEquals("Qd5xc4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move33B = new MoveSpecification(C7, D6);
    apiBoard.move(move33B);
    workingPosition = workingPosition.createChangedPosition(C7);
    final StaticPosition staticPosition33B = workingPosition.createChangedPosition(D6, BLACK_KING);
    assertEquals(staticPosition33B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kd6", apiBoard.getSan());
    assertEquals("Kc7d6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move34W = new MoveSpecification(C4, C3);
    apiBoard.move(move34W);
    workingPosition = workingPosition.createChangedPosition(C4);
    final StaticPosition staticPosition34W = workingPosition.createChangedPosition(C3, WHITE_QUEEN);
    assertEquals(staticPosition34W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qc3", apiBoard.getSan());
    assertEquals("Qc4c3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move34B = new MoveSpecification(D6, E6);
    apiBoard.move(move34B);
    workingPosition = workingPosition.createChangedPosition(D6);
    final StaticPosition staticPosition34B = workingPosition.createChangedPosition(E6, BLACK_KING);
    assertEquals(staticPosition34B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Ke6", apiBoard.getSan());
    assertEquals("Kd6e6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move35W = new MoveSpecification(C3, C2);
    apiBoard.move(move35W);
    workingPosition = workingPosition.createChangedPosition(C3);
    final StaticPosition staticPosition35W = workingPosition.createChangedPosition(C2, WHITE_QUEEN);
    assertEquals(staticPosition35W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qc2", apiBoard.getSan());
    assertEquals("Qc3c2", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move35B = new MoveSpecification(E6, F6);
    apiBoard.move(move35B);
    workingPosition = workingPosition.createChangedPosition(E6);
    final StaticPosition staticPosition35B = workingPosition.createChangedPosition(F6, BLACK_KING);
    assertEquals(staticPosition35B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kf6", apiBoard.getSan());
    assertEquals("Ke6f6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move36W = new MoveSpecification(F4, F5);
    apiBoard.move(move36W);
    workingPosition = workingPosition.createChangedPosition(F4);
    final StaticPosition staticPosition36W = workingPosition.createChangedPosition(F5, WHITE_PAWN);
    assertEquals(staticPosition36W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("f5", apiBoard.getSan());
    assertEquals("f4f5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move36B = new MoveSpecification(F6, G5);
    apiBoard.move(move36B);
    workingPosition = workingPosition.createChangedPosition(F6);
    final StaticPosition staticPosition36B = workingPosition.createChangedPosition(G5, BLACK_KING);
    assertEquals(staticPosition36B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kg5", apiBoard.getSan());
    assertEquals("Kf6g5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move37W = new MoveSpecification(C2, C3);
    apiBoard.move(move37W);
    workingPosition = workingPosition.createChangedPosition(C2);
    final StaticPosition staticPosition37W = workingPosition.createChangedPosition(C3, WHITE_QUEEN);
    assertEquals(staticPosition37W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qc3", apiBoard.getSan());
    assertEquals("Qc2c3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move37B = new MoveSpecification(G5, F5);
    apiBoard.move(move37B);
    workingPosition = workingPosition.createChangedPosition(G5);
    final StaticPosition staticPosition37B = workingPosition.createChangedPosition(F5, BLACK_KING);
    assertEquals(staticPosition37B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kxf5", apiBoard.getSan());
    assertEquals("Kg5xf5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move38W = new MoveSpecification(C3, C5);
    apiBoard.move(move38W);
    workingPosition = workingPosition.createChangedPosition(C3);
    final StaticPosition staticPosition38W = workingPosition.createChangedPosition(C5, WHITE_QUEEN);
    assertEquals(staticPosition38W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qxc5+", apiBoard.getSan());
    assertEquals("Qc3xc5+", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move38B = new MoveSpecification(F5, F4);
    apiBoard.move(move38B);
    workingPosition = workingPosition.createChangedPosition(F5);
    final StaticPosition staticPosition38B = workingPosition.createChangedPosition(F4, BLACK_KING);
    assertEquals(staticPosition38B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kf4", apiBoard.getSan());
    assertEquals("Kf5f4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move39W = new MoveSpecification(C5, F5);
    apiBoard.move(move39W);
    workingPosition = workingPosition.createChangedPosition(C5);
    final StaticPosition staticPosition39W = workingPosition.createChangedPosition(F5, WHITE_QUEEN);
    assertEquals(staticPosition39W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qf5+", apiBoard.getSan());
    assertEquals("Qc5f5+", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move39B = new MoveSpecification(F4, F5);
    apiBoard.move(move39B);
    workingPosition = workingPosition.createChangedPosition(F4);
    final StaticPosition staticPosition39B = workingPosition.createChangedPosition(F5, BLACK_KING);
    assertEquals(staticPosition39B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kxf5", apiBoard.getSan());
    assertEquals("Kf4xf5", apiBoard.getLan());

    // undo the moves
    apiBoard.unmove();
    assertEquals(staticPosition39W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition38B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition38W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition37B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition37W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition36B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition36W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition35B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition35W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition34B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition34W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition33B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition33W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition32B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition32W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition31B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition31W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition30B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition30W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition29B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition29W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition28B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition28W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition27B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition27W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition26B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition26W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition25B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition25W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition24B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition24W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition23B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition23W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition22B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition22W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition21B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition21W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition20B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition20W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition19B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition19W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition18B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition18W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition17B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition17W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition16B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition16W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition15B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition15W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition14B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition14W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition13B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition13W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition12B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition12W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition11B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition11W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition10B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition10W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition9B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition9W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition8B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition8W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition7B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition7W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition6B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition6W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition5B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition5W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition4B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition4W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition3B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition3W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition2B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition2W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition1B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition1W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());

    apiBoard.unmove();
    assertEquals(staticPosition0, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
  }

}
