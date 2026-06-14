// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.common.utility.StaticPositionUtility;

class TestPerformMoveMainlyStaticPositionState implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void testGameWithMostBasicMoves() {
    final Board apiBoard = new Board();
    final StaticPosition staticPosition0 = StaticPosition.INITIAL_POSITION;
    assertEquals(staticPosition0, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());

    StaticPosition workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move1W = new MoveSpecification(E2, E4);
    apiBoard.move(move1W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E2);
    final StaticPosition staticPosition1W = StaticPositionUtility.createChangedPosition(workingPosition, E4, WHITE_PAWN);
    assertEquals(staticPosition1W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("e4", apiBoard.getSan());
    assertEquals("e2-e4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move1B = new MoveSpecification(C7, C5);
    apiBoard.move(move1B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C7);
    final StaticPosition staticPosition1B = StaticPositionUtility.createChangedPosition(workingPosition, C5, BLACK_PAWN);
    assertEquals(staticPosition1B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("c5", apiBoard.getSan());
    assertEquals("c7-c5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move2W = new MoveSpecification(G1, F3);
    apiBoard.move(move2W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G1);
    final StaticPosition staticPosition2W = StaticPositionUtility.createChangedPosition(workingPosition, F3, WHITE_KNIGHT);
    assertEquals(staticPosition2W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nf3", apiBoard.getSan());
    assertEquals("Ng1-f3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move2B = new MoveSpecification(B8, C6);
    apiBoard.move(move2B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, B8);
    final StaticPosition staticPosition2B = StaticPositionUtility.createChangedPosition(workingPosition, C6, BLACK_KNIGHT);
    assertEquals(staticPosition2B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Nc6", apiBoard.getSan());
    assertEquals("Nb8-c6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move3W = new MoveSpecification(F1, C4);
    apiBoard.move(move3W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F1);
    final StaticPosition staticPosition3W = StaticPositionUtility.createChangedPosition(workingPosition, C4, WHITE_BISHOP);
    assertEquals(staticPosition3W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Bc4", apiBoard.getSan());
    assertEquals("Bf1-c4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move3B = new MoveSpecification(D7, D6);
    apiBoard.move(move3B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D7);
    final StaticPosition staticPosition3B = StaticPositionUtility.createChangedPosition(workingPosition, D6, BLACK_PAWN);
    assertEquals(staticPosition3B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("d6", apiBoard.getSan());
    assertEquals("d7-d6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move4W = new MoveSpecification(B1, C3);
    apiBoard.move(move4W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, B1);
    final StaticPosition staticPosition4W = StaticPositionUtility.createChangedPosition(workingPosition, C3, WHITE_KNIGHT);
    assertEquals(staticPosition4W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nc3", apiBoard.getSan());
    assertEquals("Nb1-c3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move4B = new MoveSpecification(C8, G4);
    apiBoard.move(move4B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C8);
    final StaticPosition staticPosition4B = StaticPositionUtility.createChangedPosition(workingPosition, G4, BLACK_BISHOP);
    assertEquals(staticPosition4B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Bg4", apiBoard.getSan());
    assertEquals("Bc8-g4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move5W = new MoveSpecification(D2, D3);
    apiBoard.move(move5W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D2);
    final StaticPosition staticPosition5W = StaticPositionUtility.createChangedPosition(workingPosition, D3, WHITE_PAWN);
    assertEquals(staticPosition5W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("d3", apiBoard.getSan());
    assertEquals("d2-d3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move5B = new MoveSpecification(G8, F6);
    apiBoard.move(move5B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G8);
    final StaticPosition staticPosition5B = StaticPositionUtility.createChangedPosition(workingPosition, F6, BLACK_KNIGHT);
    assertEquals(staticPosition5B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Nf6", apiBoard.getSan());
    assertEquals("Ng8-f6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move6W = new MoveSpecification(C1, F4);
    apiBoard.move(move6W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C1);
    final StaticPosition staticPosition6W = StaticPositionUtility.createChangedPosition(workingPosition, F4, WHITE_BISHOP);
    assertEquals(staticPosition6W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Bf4", apiBoard.getSan());
    assertEquals("Bc1-f4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move6B = new MoveSpecification(E7, E5);
    apiBoard.move(move6B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E7);
    final StaticPosition staticPosition6B = StaticPositionUtility.createChangedPosition(workingPosition, E5, BLACK_PAWN);
    assertEquals(staticPosition6B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("e5", apiBoard.getSan());
    assertEquals("e7-e5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move7W = new MoveSpecification(D1, E2);
    apiBoard.move(move7W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D1);
    final StaticPosition staticPosition7W = StaticPositionUtility.createChangedPosition(workingPosition, E2, WHITE_QUEEN);
    assertEquals(staticPosition7W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qe2", apiBoard.getSan());
    assertEquals("Qd1-e2", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move7B = new MoveSpecification(F8, E7);
    apiBoard.move(move7B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F8);
    final StaticPosition staticPosition7B = StaticPositionUtility.createChangedPosition(workingPosition, E7, BLACK_BISHOP);
    assertEquals(staticPosition7B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Be7", apiBoard.getSan());
    assertEquals("Bf8-e7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move8W = new MoveSpecification(A1, D1);
    apiBoard.move(move8W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, A1);
    final StaticPosition staticPosition8W = StaticPositionUtility.createChangedPosition(workingPosition, D1, WHITE_ROOK);
    assertEquals(staticPosition8W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Rd1", apiBoard.getSan());
    assertEquals("Ra1-d1", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move8B = new MoveSpecification(D8, D7);
    apiBoard.move(move8B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D8);
    final StaticPosition staticPosition8B = StaticPositionUtility.createChangedPosition(workingPosition, D7, BLACK_QUEEN);
    assertEquals(staticPosition8B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Qd7", apiBoard.getSan());
    assertEquals("Qd8-d7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move9W = new MoveSpecification(CastlingMove.KING_SIDE);
    apiBoard.move(move9W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E1);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G1, WHITE_KING);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, H1);
    final StaticPosition staticPosition9W = StaticPositionUtility.createChangedPosition(workingPosition, F1, WHITE_ROOK);
    assertEquals(staticPosition9W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("O-O", apiBoard.getSan());
    assertEquals("O-O", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move9B = new MoveSpecification(CastlingMove.QUEEN_SIDE);
    apiBoard.move(move9B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E8);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C8, BLACK_KING);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, A8);
    final StaticPosition staticPosition9B = StaticPositionUtility.createChangedPosition(workingPosition, D8, BLACK_ROOK);
    assertEquals(staticPosition9B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("O-O-O", apiBoard.getSan());
    assertEquals("O-O-O", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move10W = new MoveSpecification(F1, E1);
    apiBoard.move(move10W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F1);
    final StaticPosition staticPosition10W = StaticPositionUtility.createChangedPosition(workingPosition, E1, WHITE_ROOK);
    assertEquals(staticPosition10W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Rfe1", apiBoard.getSan());
    assertEquals("Rf1-e1", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move10B = new MoveSpecification(D8, E8);
    apiBoard.move(move10B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D8);
    final StaticPosition staticPosition10B = StaticPositionUtility.createChangedPosition(workingPosition, E8, BLACK_ROOK);
    assertEquals(staticPosition10B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Rde8", apiBoard.getSan());
    assertEquals("Rd8-e8", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move11W = new MoveSpecification(G1, H1);
    apiBoard.move(move11W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G1);
    final StaticPosition staticPosition11W = StaticPositionUtility.createChangedPosition(workingPosition, H1, WHITE_KING);
    assertEquals(staticPosition11W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Kh1", apiBoard.getSan());
    assertEquals("Kg1-h1", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move11B = new MoveSpecification(C8, B8);
    apiBoard.move(move11B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C8);
    final StaticPosition staticPosition11B = StaticPositionUtility.createChangedPosition(workingPosition, B8, BLACK_KING);
    assertEquals(staticPosition11B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kb8", apiBoard.getSan());
    assertEquals("Kc8-b8", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move12W = new MoveSpecification(H2, H4);
    apiBoard.move(move12W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, H2);
    final StaticPosition staticPosition12W = StaticPositionUtility.createChangedPosition(workingPosition, H4, WHITE_PAWN);
    assertEquals(staticPosition12W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("h4", apiBoard.getSan());
    assertEquals("h2-h4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move12B = new MoveSpecification(A7, A5);
    apiBoard.move(move12B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, A7);
    final StaticPosition staticPosition12B = StaticPositionUtility.createChangedPosition(workingPosition, A5, BLACK_PAWN);
    assertEquals(staticPosition12B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("a5", apiBoard.getSan());
    assertEquals("a7-a5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move13W = new MoveSpecification(H4, H5);
    apiBoard.move(move13W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, H4);
    final StaticPosition staticPosition13W = StaticPositionUtility.createChangedPosition(workingPosition, H5, WHITE_PAWN);
    assertEquals(staticPosition13W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("h5", apiBoard.getSan());
    assertEquals("h4-h5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move13B = new MoveSpecification(G7, G5);
    apiBoard.move(move13B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G7);
    final StaticPosition staticPosition13B = StaticPositionUtility.createChangedPosition(workingPosition, G5, BLACK_PAWN);
    assertEquals(staticPosition13B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("g5", apiBoard.getSan());
    assertEquals("g7-g5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move14W = new MoveSpecification(H5, G6);
    apiBoard.move(move14W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, H5);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G6, WHITE_PAWN);
    final StaticPosition staticPosition14W = StaticPositionUtility.createChangedPosition(workingPosition, G5);
    assertEquals(staticPosition14W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("hxg6", apiBoard.getSan());
    assertEquals("h5xg6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move14B = new MoveSpecification(A5, A4);
    apiBoard.move(move14B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, A5);
    final StaticPosition staticPosition14B = StaticPositionUtility.createChangedPosition(workingPosition, A4, BLACK_PAWN);
    assertEquals(staticPosition14B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("a4", apiBoard.getSan());
    assertEquals("a5-a4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move15W = new MoveSpecification(B2, B4);
    apiBoard.move(move15W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, B2);
    final StaticPosition staticPosition15W = StaticPositionUtility.createChangedPosition(workingPosition, B4, WHITE_PAWN);
    assertEquals(staticPosition15W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("b4", apiBoard.getSan());
    assertEquals("b2-b4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move15B = new MoveSpecification(A4, B3);
    apiBoard.move(move15B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, A4);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, B3, BLACK_PAWN);
    final StaticPosition staticPosition15B = StaticPositionUtility.createChangedPosition(workingPosition, B4);
    assertEquals(staticPosition15B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("axb3", apiBoard.getSan());
    assertEquals("a4xb3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move16W = new MoveSpecification(G6, H7);
    apiBoard.move(move16W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G6);
    final StaticPosition staticPosition16W = StaticPositionUtility.createChangedPosition(workingPosition, H7, WHITE_PAWN);
    assertEquals(staticPosition16W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("gxh7", apiBoard.getSan());
    assertEquals("g6xh7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move16B = new MoveSpecification(B3, A2);
    apiBoard.move(move16B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, B3);
    final StaticPosition staticPosition16B = StaticPositionUtility.createChangedPosition(workingPosition, A2, BLACK_PAWN);
    assertEquals(staticPosition16B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("bxa2", apiBoard.getSan());
    assertEquals("b3xa2", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move17W = new MoveSpecification(F4, E5);
    apiBoard.move(move17W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F4);
    final StaticPosition staticPosition17W = StaticPositionUtility.createChangedPosition(workingPosition, E5, WHITE_BISHOP);
    assertEquals(staticPosition17W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Bxe5", apiBoard.getSan());
    assertEquals("Bf4xe5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move17B = new MoveSpecification(A2, A1, PromotionPieceType.QUEEN);
    apiBoard.move(move17B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, A2);
    final StaticPosition staticPosition17B = StaticPositionUtility.createChangedPosition(workingPosition, A1, BLACK_QUEEN);
    assertEquals(staticPosition17B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("a1=Q", apiBoard.getSan());
    assertEquals("a2-a1=Q", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move18W = new MoveSpecification(F3, G5);
    apiBoard.move(move18W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F3);
    final StaticPosition staticPosition18W = StaticPositionUtility.createChangedPosition(workingPosition, G5, WHITE_KNIGHT);
    assertEquals(staticPosition18W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Ng5", apiBoard.getSan());
    assertEquals("Nf3-g5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move18B = new MoveSpecification(H8, G8);
    apiBoard.move(move18B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, H8);
    final StaticPosition staticPosition18B = StaticPositionUtility.createChangedPosition(workingPosition, G8, BLACK_ROOK);
    assertEquals(staticPosition18B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Rhg8", apiBoard.getSan());
    assertEquals("Rh8-g8", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move19W = new MoveSpecification(H7, G8, PromotionPieceType.KNIGHT);
    apiBoard.move(move19W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, H7);
    final StaticPosition staticPosition19W = StaticPositionUtility.createChangedPosition(workingPosition, G8, WHITE_KNIGHT);
    assertEquals(staticPosition19W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("hxg8=N", apiBoard.getSan());
    assertEquals("h7xg8=N", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move19B = new MoveSpecification(C6, A5);
    apiBoard.move(move19B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C6);
    final StaticPosition staticPosition19B = StaticPositionUtility.createChangedPosition(workingPosition, A5, BLACK_KNIGHT);
    assertEquals(staticPosition19B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Na5", apiBoard.getSan());
    assertEquals("Nc6-a5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move20W = new MoveSpecification(G5, F7);
    apiBoard.move(move20W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G5);
    final StaticPosition staticPosition20W = StaticPositionUtility.createChangedPosition(workingPosition, F7, WHITE_KNIGHT);
    assertEquals(staticPosition20W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nxf7", apiBoard.getSan());
    assertEquals("Ng5xf7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move20B = new MoveSpecification(A5, C4);
    apiBoard.move(move20B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, A5);
    final StaticPosition staticPosition20B = StaticPositionUtility.createChangedPosition(workingPosition, C4, BLACK_KNIGHT);
    assertEquals(staticPosition20B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Nxc4", apiBoard.getSan());
    assertEquals("Na5xc4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move21W = new MoveSpecification(G8, E7);
    apiBoard.move(move21W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G8);
    final StaticPosition staticPosition21W = StaticPositionUtility.createChangedPosition(workingPosition, E7, WHITE_KNIGHT);
    assertEquals(staticPosition21W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nxe7", apiBoard.getSan());
    assertEquals("Ng8xe7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move21B = new MoveSpecification(E8, E7);
    apiBoard.move(move21B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E8);
    final StaticPosition staticPosition21B = StaticPositionUtility.createChangedPosition(workingPosition, E7, BLACK_ROOK);
    assertEquals(staticPosition21B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Rxe7", apiBoard.getSan());
    assertEquals("Re8xe7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move22W = new MoveSpecification(F2, F3);
    apiBoard.move(move22W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F2);
    final StaticPosition staticPosition22W = StaticPositionUtility.createChangedPosition(workingPosition, F3, WHITE_PAWN);
    assertEquals(staticPosition22W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("f3", apiBoard.getSan());
    assertEquals("f2-f3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move22B = new MoveSpecification(A1, D1);
    apiBoard.move(move22B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, A1);
    final StaticPosition staticPosition22B = StaticPositionUtility.createChangedPosition(workingPosition, D1, BLACK_QUEEN);
    assertEquals(staticPosition22B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Qxd1", apiBoard.getSan());
    assertEquals("Qa1xd1", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move23W = new MoveSpecification(E1, D1);
    apiBoard.move(move23W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E1);
    final StaticPosition staticPosition23W = StaticPositionUtility.createChangedPosition(workingPosition, D1, WHITE_ROOK);
    assertEquals(staticPosition23W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Rxd1", apiBoard.getSan());
    assertEquals("Re1xd1", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move23B = new MoveSpecification(G4, F3);
    apiBoard.move(move23B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G4);
    final StaticPosition staticPosition23B = StaticPositionUtility.createChangedPosition(workingPosition, F3, BLACK_BISHOP);
    assertEquals(staticPosition23B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Bxf3", apiBoard.getSan());
    assertEquals("Bg4xf3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move24W = new MoveSpecification(G2, F3);
    apiBoard.move(move24W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G2);
    final StaticPosition staticPosition24W = StaticPositionUtility.createChangedPosition(workingPosition, F3, WHITE_PAWN);
    assertEquals(staticPosition24W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("gxf3", apiBoard.getSan());
    assertEquals("g2xf3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move24B = new MoveSpecification(C4, E5);
    apiBoard.move(move24B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C4);
    final StaticPosition staticPosition24B = StaticPositionUtility.createChangedPosition(workingPosition, E5, BLACK_KNIGHT);
    assertEquals(staticPosition24B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Nxe5", apiBoard.getSan());
    assertEquals("Nc4xe5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move25W = new MoveSpecification(F7, E5);
    apiBoard.move(move25W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F7);
    final StaticPosition staticPosition25W = StaticPositionUtility.createChangedPosition(workingPosition, E5, WHITE_KNIGHT);
    assertEquals(staticPosition25W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nxe5", apiBoard.getSan());
    assertEquals("Nf7xe5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move25B = new MoveSpecification(F6, E4);
    apiBoard.move(move25B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F6);
    final StaticPosition staticPosition25B = StaticPositionUtility.createChangedPosition(workingPosition, E4, BLACK_KNIGHT);
    assertEquals(staticPosition25B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Nxe4", apiBoard.getSan());
    assertEquals("Nf6xe4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move26W = new MoveSpecification(D3, E4);
    apiBoard.move(move26W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D3);
    final StaticPosition staticPosition26W = StaticPositionUtility.createChangedPosition(workingPosition, E4, WHITE_PAWN);
    assertEquals(staticPosition26W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("dxe4", apiBoard.getSan());
    assertEquals("d3xe4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move26B = new MoveSpecification(E7, E5);
    apiBoard.move(move26B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E7);
    final StaticPosition staticPosition26B = StaticPositionUtility.createChangedPosition(workingPosition, E5, BLACK_ROOK);
    assertEquals(staticPosition26B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Rxe5", apiBoard.getSan());
    assertEquals("Re7xe5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move27W = new MoveSpecification(D1, D6);
    apiBoard.move(move27W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D1);
    final StaticPosition staticPosition27W = StaticPositionUtility.createChangedPosition(workingPosition, D6, WHITE_ROOK);
    assertEquals(staticPosition27W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Rxd6", apiBoard.getSan());
    assertEquals("Rd1xd6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move27B = new MoveSpecification(D7, D6);
    apiBoard.move(move27B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D7);
    final StaticPosition staticPosition27B = StaticPositionUtility.createChangedPosition(workingPosition, D6, BLACK_QUEEN);
    assertEquals(staticPosition27B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Qxd6", apiBoard.getSan());
    assertEquals("Qd7xd6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move28W = new MoveSpecification(C3, D5);
    apiBoard.move(move28W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C3);
    final StaticPosition staticPosition28W = StaticPositionUtility.createChangedPosition(workingPosition, D5, WHITE_KNIGHT);
    assertEquals(staticPosition28W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Nd5", apiBoard.getSan());
    assertEquals("Nc3-d5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move28B = new MoveSpecification(E5, E4);
    apiBoard.move(move28B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E5);
    final StaticPosition staticPosition28B = StaticPositionUtility.createChangedPosition(workingPosition, E4, BLACK_ROOK);
    assertEquals(staticPosition28B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Rxe4", apiBoard.getSan());
    assertEquals("Re5xe4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move29W = new MoveSpecification(E2, E4);
    apiBoard.move(move29W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E2);
    final StaticPosition staticPosition29W = StaticPositionUtility.createChangedPosition(workingPosition, E4, WHITE_QUEEN);
    assertEquals(staticPosition29W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qxe4", apiBoard.getSan());
    assertEquals("Qe2xe4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move29B = new MoveSpecification(D6, D5);
    apiBoard.move(move29B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D6);
    final StaticPosition staticPosition29B = StaticPositionUtility.createChangedPosition(workingPosition, D5, BLACK_QUEEN);
    assertEquals(staticPosition29B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Qxd5", apiBoard.getSan());
    assertEquals("Qd6xd5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move30W = new MoveSpecification(E4, D5);
    apiBoard.move(move30W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E4);
    final StaticPosition staticPosition30W = StaticPositionUtility.createChangedPosition(workingPosition, D5, WHITE_QUEEN);
    assertEquals(staticPosition30W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qxd5", apiBoard.getSan());
    assertEquals("Qe4xd5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move30B = new MoveSpecification(B7, B5);
    apiBoard.move(move30B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, B7);
    final StaticPosition staticPosition30B = StaticPositionUtility.createChangedPosition(workingPosition, B5, BLACK_PAWN);
    assertEquals(staticPosition30B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("b5", apiBoard.getSan());
    assertEquals("b7-b5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move31W = new MoveSpecification(C2, C4);
    apiBoard.move(move31W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C2);
    final StaticPosition staticPosition31W = StaticPositionUtility.createChangedPosition(workingPosition, C4, WHITE_PAWN);
    assertEquals(staticPosition31W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("c4", apiBoard.getSan());
    assertEquals("c2-c4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move31B = new MoveSpecification(B5, C4);
    apiBoard.move(move31B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, B5);
    final StaticPosition staticPosition31B = StaticPositionUtility.createChangedPosition(workingPosition, C4, BLACK_PAWN);
    assertEquals(staticPosition31B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("bxc4", apiBoard.getSan());
    assertEquals("b5xc4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move32W = new MoveSpecification(F3, F4);
    apiBoard.move(move32W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F3);
    final StaticPosition staticPosition32W = StaticPositionUtility.createChangedPosition(workingPosition, F4, WHITE_PAWN);
    assertEquals(staticPosition32W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("f4", apiBoard.getSan());
    assertEquals("f3-f4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move32B = new MoveSpecification(B8, C7);
    apiBoard.move(move32B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, B8);
    final StaticPosition staticPosition32B = StaticPositionUtility.createChangedPosition(workingPosition, C7, BLACK_KING);
    assertEquals(staticPosition32B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kc7", apiBoard.getSan());
    assertEquals("Kb8-c7", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move33W = new MoveSpecification(D5, C4);
    apiBoard.move(move33W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D5);
    final StaticPosition staticPosition33W = StaticPositionUtility.createChangedPosition(workingPosition, C4, WHITE_QUEEN);
    assertEquals(staticPosition33W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qxc4", apiBoard.getSan());
    assertEquals("Qd5xc4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move33B = new MoveSpecification(C7, D6);
    apiBoard.move(move33B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C7);
    final StaticPosition staticPosition33B = StaticPositionUtility.createChangedPosition(workingPosition, D6, BLACK_KING);
    assertEquals(staticPosition33B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kd6", apiBoard.getSan());
    assertEquals("Kc7-d6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move34W = new MoveSpecification(C4, C3);
    apiBoard.move(move34W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C4);
    final StaticPosition staticPosition34W = StaticPositionUtility.createChangedPosition(workingPosition, C3, WHITE_QUEEN);
    assertEquals(staticPosition34W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qc3", apiBoard.getSan());
    assertEquals("Qc4-c3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move34B = new MoveSpecification(D6, E6);
    apiBoard.move(move34B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, D6);
    final StaticPosition staticPosition34B = StaticPositionUtility.createChangedPosition(workingPosition, E6, BLACK_KING);
    assertEquals(staticPosition34B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Ke6", apiBoard.getSan());
    assertEquals("Kd6-e6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move35W = new MoveSpecification(C3, C2);
    apiBoard.move(move35W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C3);
    final StaticPosition staticPosition35W = StaticPositionUtility.createChangedPosition(workingPosition, C2, WHITE_QUEEN);
    assertEquals(staticPosition35W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qc2", apiBoard.getSan());
    assertEquals("Qc3-c2", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move35B = new MoveSpecification(E6, F6);
    apiBoard.move(move35B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, E6);
    final StaticPosition staticPosition35B = StaticPositionUtility.createChangedPosition(workingPosition, F6, BLACK_KING);
    assertEquals(staticPosition35B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kf6", apiBoard.getSan());
    assertEquals("Ke6-f6", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move36W = new MoveSpecification(F4, F5);
    apiBoard.move(move36W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F4);
    final StaticPosition staticPosition36W = StaticPositionUtility.createChangedPosition(workingPosition, F5, WHITE_PAWN);
    assertEquals(staticPosition36W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("f5", apiBoard.getSan());
    assertEquals("f4-f5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move36B = new MoveSpecification(F6, G5);
    apiBoard.move(move36B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F6);
    final StaticPosition staticPosition36B = StaticPositionUtility.createChangedPosition(workingPosition, G5, BLACK_KING);
    assertEquals(staticPosition36B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kg5", apiBoard.getSan());
    assertEquals("Kf6-g5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move37W = new MoveSpecification(C2, C3);
    apiBoard.move(move37W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C2);
    final StaticPosition staticPosition37W = StaticPositionUtility.createChangedPosition(workingPosition, C3, WHITE_QUEEN);
    assertEquals(staticPosition37W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qc3", apiBoard.getSan());
    assertEquals("Qc2-c3", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move37B = new MoveSpecification(G5, F5);
    apiBoard.move(move37B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, G5);
    final StaticPosition staticPosition37B = StaticPositionUtility.createChangedPosition(workingPosition, F5, BLACK_KING);
    assertEquals(staticPosition37B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kxf5", apiBoard.getSan());
    assertEquals("Kg5xf5", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move38W = new MoveSpecification(C3, C5);
    apiBoard.move(move38W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C3);
    final StaticPosition staticPosition38W = StaticPositionUtility.createChangedPosition(workingPosition, C5, WHITE_QUEEN);
    assertEquals(staticPosition38W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qxc5+", apiBoard.getSan());
    assertEquals("Qc3xc5+", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move38B = new MoveSpecification(F5, F4);
    apiBoard.move(move38B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F5);
    final StaticPosition staticPosition38B = StaticPositionUtility.createChangedPosition(workingPosition, F4, BLACK_KING);
    assertEquals(staticPosition38B, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(WHITE, apiBoard.getHavingMove());
    assertEquals("Kf4", apiBoard.getSan());
    assertEquals("Kf5-f4", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move39W = new MoveSpecification(C5, F5);
    apiBoard.move(move39W);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, C5);
    final StaticPosition staticPosition39W = StaticPositionUtility.createChangedPosition(workingPosition, F5, WHITE_QUEEN);
    assertEquals(staticPosition39W, StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition()));
    assertEquals(BLACK, apiBoard.getHavingMove());
    assertEquals("Qf5+", apiBoard.getSan());
    assertEquals("Qc5-f5+", apiBoard.getLan());

    workingPosition = StaticPositionBridge.toStaticPosition(apiBoard.getBitboardPosition());
    final MoveSpecification move39B = new MoveSpecification(F4, F5);
    apiBoard.move(move39B);
    workingPosition = StaticPositionUtility.createChangedPosition(workingPosition, F4);
    final StaticPosition staticPosition39B = StaticPositionUtility.createChangedPosition(workingPosition, F5, BLACK_KING);
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
