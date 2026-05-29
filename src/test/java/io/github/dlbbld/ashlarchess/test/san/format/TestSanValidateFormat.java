// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.san.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.board.enums.File;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.PromotionPieceType;
import io.github.dlbbld.ashlarchess.board.enums.Rank;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.constants.CastlingConstants;
import io.github.dlbbld.ashlarchess.san.SanConversion;
import io.github.dlbbld.ashlarchess.san.SanFormat;
import io.github.dlbbld.ashlarchess.san.SanParse;
import io.github.dlbbld.ashlarchess.san.SanTerminalMarker;
import io.github.dlbbld.ashlarchess.san.SanValidateFormat;
import io.github.dlbbld.ashlarchess.san.SanValidationException;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;
import io.github.dlbbld.ashlarchess.test.san.reference.SanValidateFormatReference;

class TestSanValidateFormat {

  @SuppressWarnings("static-method")
  @Test
  void testInvalid() {
    checkException("+", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("#", SanValidationProblem.FORMAT_FIRST_CHARACTER);

    // d3 - replace first char with valid non-file, second char with valid non-digit
    checkException("=3", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("dd", SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER);

    // dxe5 - invalid first, second, third, fourth chars
    checkException("+xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("d=e5", SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER);
    checkException("dx15", SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_FILE);
    checkException("dxeR", SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_RANK);

    // d8=Q - invalid first, second (rank), structure, promotion piece
    checkException("18=Q", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("dd=Q", SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER);
    checkException("d8xQ", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_WRONG_PROMOTION_SYMBOL);
    checkException("d8=K", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_WRONG_PROMOTION_PIECE);

    // dxe8=Q - invalid chars replaced with valid SAN chars
    checkException("1xe8=Q", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("d=e8=Q", SanValidationProblem.FORMAT_PAWN_WRONG_SECOND_CHARACTER);
    checkException("dxa=Q", SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_RANK);
    checkException("dxeR=Q", SanValidationProblem.FORMAT_PAWN_CAPTURE_WRONG_RANK);
    checkException("dxe8xQ", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_WRONG_PROMOTION_SYMBOL);
    checkException("dxe8=K", SanValidationProblem.FORMAT_PAWN_CAPTURE_PROMOTION_WRONG_PROMOTION_PIECE);

    // Qe5
    checkException("+e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("QK5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("Qe=", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);

    // Qae5
    checkException("+ae5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("QKe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("QaK5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("QaeR", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_FILE_WRONG_DESTINATION_RANK);

    // Q2e5
    checkException("+2e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("QRe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("Q2K5", SanValidationProblem.FORMAT_RNBQ_RANK_WRONG_THIRD_CHARACTER);
    checkException("Q2eR", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_RANK_WRONG_DESTINATION_RANK);

    // Qc3e5
    checkException("+c3e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("QK3e5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("QcRe5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("Qc3K5", SanValidationProblem.FORMAT_RNBQ_SQUARE_WRONG_THIRD_CHARACTER);
    checkException("Qc3eR", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_SQUARE_WRONG_DESTINATION_RANK);

    // Qxe5
    checkException("+xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("Q=e5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("QxK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("QxeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_WRONG_DESTINATION_RANK);

    // Qaxe5
    checkException("+axe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("QKxe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("Qa=e5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("QaxK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_WRONG_DESTINATION_FILE);
    checkException("QaxeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_WRONG_DESTINATION_RANK);

    // Q2xe5
    checkException("+2xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("QRxe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("Q2=e5", SanValidationProblem.FORMAT_RNBQ_RANK_WRONG_THIRD_CHARACTER);
    checkException("Q2xK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_WRONG_DESTINATION_FILE);
    checkException("Q2xeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_WRONG_DESTINATION_RANK);

    // Qc3xe5
    checkException("+c3xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("QK3xe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("QcRxe5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("Qc3=e5", SanValidationProblem.FORMAT_RNBQ_SQUARE_WRONG_THIRD_CHARACTER);
    checkException("Qc3xK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_SQUARE_WRONG_DESTINATION_FILE);
    checkException("Qc3xeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_SQUARE_WRONG_DESTINATION_RANK);

    // Re5
    checkException("+e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("RK5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("ReR", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);

    // Rae5
    checkException("+ae5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("RKe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("RaK5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("RaeR", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_FILE_WRONG_DESTINATION_RANK);

    // R2e5
    checkException("+2e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("RRe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("R2K5", SanValidationProblem.FORMAT_RNBQ_RANK_WRONG_THIRD_CHARACTER);
    checkException("R2eR", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_RANK_WRONG_DESTINATION_RANK);

    // Rxe5
    checkException("+xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("R=e5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("RxK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("RxeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_WRONG_DESTINATION_RANK);

    // Raxe5
    checkException("+axe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("RKxe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("Ra=e5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("RaxK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_WRONG_DESTINATION_FILE);
    checkException("RaxeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_WRONG_DESTINATION_RANK);

    // R2xe5
    checkException("+2xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("RRxe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("R2=e5", SanValidationProblem.FORMAT_RNBQ_RANK_WRONG_THIRD_CHARACTER);
    checkException("R2xK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_WRONG_DESTINATION_FILE);
    checkException("R2xeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_WRONG_DESTINATION_RANK);

    // Ne5
    checkException("+e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("NK5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("NeR", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);

    // Nce5
    checkException("+ce5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("NKe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("NcK5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("NceR", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_FILE_WRONG_DESTINATION_RANK);

    // N4e5
    checkException("+4e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("NRe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("N4K5", SanValidationProblem.FORMAT_RNBQ_RANK_WRONG_THIRD_CHARACTER);
    checkException("N4eR", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_RANK_WRONG_DESTINATION_RANK);

    // Nd3e5
    checkException("+d3e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("NK3e5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("NdRe5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("Nd3K5", SanValidationProblem.FORMAT_RNBQ_SQUARE_WRONG_THIRD_CHARACTER);
    checkException("Nd3eR", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_SQUARE_WRONG_DESTINATION_RANK);

    // Nxe5
    checkException("+xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("N=e5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("NxK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("NxeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_WRONG_DESTINATION_RANK);

    // Ncxe5
    checkException("+cxe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("NKxe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("Nc=e5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("NcxK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_WRONG_DESTINATION_FILE);
    checkException("NcxeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_WRONG_DESTINATION_RANK);

    // N4xe5
    checkException("+4xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("NRxe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("N4=e5", SanValidationProblem.FORMAT_RNBQ_RANK_WRONG_THIRD_CHARACTER);
    checkException("N4xK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_WRONG_DESTINATION_FILE);
    checkException("N4xeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_WRONG_DESTINATION_RANK);

    // Nd3xe5
    checkException("+d3xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("NK3xe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("NdRxe5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("Nd3=e5", SanValidationProblem.FORMAT_RNBQ_SQUARE_WRONG_THIRD_CHARACTER);
    checkException("Nd3xK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_SQUARE_WRONG_DESTINATION_FILE);
    checkException("Nd3xeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_SQUARE_WRONG_DESTINATION_RANK);

    // Be5
    checkException("+e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("BK5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("BeR", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);

    // Bbe5
    checkException("+be5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("BKe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("BbK5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("BbeR", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_FILE_WRONG_DESTINATION_RANK);

    // B2e5
    checkException("+2e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("BRe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("B2K5", SanValidationProblem.FORMAT_RNBQ_RANK_WRONG_THIRD_CHARACTER);
    checkException("B2eR", SanValidationProblem.FORMAT_RNBQ_NON_CAPTURE_RANK_WRONG_DESTINATION_RANK);

    // Bxe5
    checkException("+xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("B=e5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("BxK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("BxeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_WRONG_DESTINATION_RANK);

    // Bbxe5
    checkException("+bxe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("BKxe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("Bb=e5", SanValidationProblem.FORMAT_RNBQ_FILE_WRONG_THIRD_CHARACTER);
    checkException("BbxK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_WRONG_DESTINATION_FILE);
    checkException("BbxeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_FILE_WRONG_DESTINATION_RANK);

    // B2xe5
    checkException("+2xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("BRxe5", SanValidationProblem.FORMAT_RNBQ_WRONG_SECOND_CHARACTER);
    checkException("B2=e5", SanValidationProblem.FORMAT_RNBQ_RANK_WRONG_THIRD_CHARACTER);
    checkException("B2xK5", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_WRONG_DESTINATION_FILE);
    checkException("B2xeR", SanValidationProblem.FORMAT_RNBQ_CAPTURE_RANK_WRONG_DESTINATION_RANK);

    // Ke5
    checkException("+e5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("KK5", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("KeR", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_RANK);

    // Kxe5
    checkException("+xe5", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("K=e5", SanValidationProblem.FORMAT_KING_NON_CASTLING_NON_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("KxK5", SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_WRONG_DESTINATION_FILE);
    checkException("KxeR", SanValidationProblem.FORMAT_KING_NON_CASTLING_CAPTURE_WRONG_DESTINATION_RANK);

    // O-O
    checkException("+-O", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("O=O", SanValidationProblem.FORMAT_KING_CASTLING);
    checkException("O-x", SanValidationProblem.FORMAT_KING_CASTLING);

    // O-O-O
    checkException("+-O-O", SanValidationProblem.FORMAT_FIRST_CHARACTER);
    checkException("O=O-O", SanValidationProblem.FORMAT_KING_CASTLING);
    checkException("O-x-O", SanValidationProblem.FORMAT_KING_CASTLING);
    checkException("O-O=O", SanValidationProblem.FORMAT_KING_CASTLING);
    checkException("O-O-x", SanValidationProblem.FORMAT_KING_CASTLING);

  }

  @SuppressWarnings("static-method")
  @Test
  void testPromotionValid() {

    checkValid("d1=Q");
    checkValid("cxd1=Q");

    checkValid("d8=Q");
    checkValid("cxd8=Q");
    checkValid("d8=R");
    checkValid("d8=B");
    checkValid("d8=N");
    checkValid("d8=Q");

    checkValid("cxd8=R");
    checkValid("cxd8=B");
    checkValid("cxd8=N");
    checkValid("cxd8=Q");

    checkValid("c1=R");
    checkValid("c1=B");
    checkValid("c1=N");
    checkValid("c1=Q");

    checkValid("cxb1=R");
    checkValid("cxb1=B");
    checkValid("cxb1=N");
    checkValid("cxb1=Q");

  }

  @SuppressWarnings("static-method")
  @Test
  void testPromotionMissingPromotionPiece() {
    checkException("d8=", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_PIECE);
    checkException("d1=", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_NO_PROMOTION_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPromotionToKing() {
    checkException("d8=K", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_WRONG_PROMOTION_PIECE);
    checkException("d1=K", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_WRONG_PROMOTION_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPromotionToPawn() {
    checkException("d8=P", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_WRONG_PROMOTION_PIECE);
    checkException("d1=P", SanValidationProblem.FORMAT_PAWN_FORWARD_PROMOTION_WRONG_PROMOTION_PIECE);
  }

  @SuppressWarnings("static-method")
  @Test
  void testPromotionMiddleOfBoard() {
    checkValid("d8=Q");
    checkValid("d1=N");
  }

  @SuppressWarnings("static-method")
  @Test
  void testValid() {
    // for pawn moves we are here not checking if valid destination or promotion rank
    // so we can use the same test for white and black

    // (1a) pawnNonCapturingNonPromotionMoves d3 not d8
    {
      final SanFormat expectedSanFormat = SanFormat.PAWN_NON_CAPTURING_NON_PROMOTION;
      final SanConversion expectedSanConversion = new SanConversion(PieceType.PAWN, File.NONE, Rank.NONE, Square.D3,
          PromotionPieceType.NONE, SanTerminalMarker.NONE);
      final String san = "d3";
      checkValid(expectedSanFormat, expectedSanConversion, san);
    }
    {
      final SanFormat expectedSanFormat = SanFormat.PAWN_NON_CAPTURING_PROMOTION;
      final SanConversion expectedSanConversion = new SanConversion(PieceType.PAWN, File.NONE, Rank.NONE, Square.D8,
          PromotionPieceType.KNIGHT, SanTerminalMarker.NONE);
      final String san = "d8=N";
      checkValid(expectedSanFormat, expectedSanConversion, san);
    }
    {
      final SanFormat expectedSanFormat = SanFormat.PAWN_NON_CAPTURING_PROMOTION;
      final SanConversion expectedSanConversion = new SanConversion(PieceType.PAWN, File.NONE, Rank.NONE, Square.D1,
          PromotionPieceType.QUEEN, SanTerminalMarker.NONE);
      final String san = "d1=Q";
      checkValid(expectedSanFormat, expectedSanConversion, san);
    }
    // (1b) pawnCapturingNonPromotionMoves dxe5 not dxe8
    {
      final SanFormat expectedSanFormat = SanFormat.PAWN_CAPTURING_NON_PROMOTION;
      final SanConversion expectedSanConversion = new SanConversion(PieceType.PAWN, File.FILE_D, Rank.NONE, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE);
      final String san = "dxe5";
      checkValid(expectedSanFormat, expectedSanConversion, san);
    }
    {
      final SanFormat expectedSanFormat = SanFormat.PAWN_CAPTURING_PROMOTION;
      final SanConversion expectedSanConversion = new SanConversion(PieceType.PAWN, File.FILE_D, Rank.NONE, Square.E8,
          PromotionPieceType.QUEEN, SanTerminalMarker.NONE);
      final String san = "dxe8=Q";
      checkValid(expectedSanFormat, expectedSanConversion, san);
    }
    {
      final SanFormat expectedSanFormat = SanFormat.PAWN_CAPTURING_PROMOTION;
      final SanConversion expectedSanConversion = new SanConversion(PieceType.PAWN, File.FILE_D, Rank.NONE, Square.E1,
          PromotionPieceType.QUEEN, SanTerminalMarker.NONE);
      final String san = "dxe1=Q";
      checkValid(expectedSanFormat, expectedSanConversion, san);
    }
    // (1c) pawnNonCapturingPromotionMoves d8=Q
    {
      final SanFormat expectedSanFormat = SanFormat.PAWN_NON_CAPTURING_PROMOTION;
      final SanConversion expectedSanConversion = new SanConversion(PieceType.PAWN, File.NONE, Rank.NONE, Square.D8,
          PromotionPieceType.QUEEN, SanTerminalMarker.NONE);
      final String san = "d8=Q";
      checkValid(expectedSanFormat, expectedSanConversion, san);
    }
    {
      final SanFormat expectedSanFormat = SanFormat.PAWN_NON_CAPTURING_PROMOTION;
      final SanConversion expectedSanConversion = new SanConversion(PieceType.PAWN, File.NONE, Rank.NONE, Square.D1,
          PromotionPieceType.QUEEN, SanTerminalMarker.NONE);
      final String san = "d1=Q";
      checkValid(expectedSanFormat, expectedSanConversion, san);
    }
    {
      final String san = "d7=Q";
      checkException(san, SanValidationProblem.FORMAT_PAWN_FORWARD_NON_PROMOTION_OVERLENGTH);
    }
    // (1d) pawnCapturingPromotionMoves dxe8=Q
    {
      final SanFormat expectedSanFormat = SanFormat.PAWN_CAPTURING_PROMOTION;
      final SanConversion expectedSanConversion = new SanConversion(PieceType.PAWN, File.FILE_D, Rank.NONE, Square.E8,
          PromotionPieceType.QUEEN, SanTerminalMarker.NONE);
      final String san = "dxe8=Q";
      checkValid(expectedSanFormat, expectedSanConversion, san);
    }
    {
      final SanFormat expectedSanFormat = SanFormat.PAWN_CAPTURING_PROMOTION;
      final SanConversion expectedSanConversion = new SanConversion(PieceType.PAWN, File.FILE_D, Rank.NONE, Square.E1,
          PromotionPieceType.QUEEN, SanTerminalMarker.NONE);
      final String san = "dxe1=Q";
      checkValid(expectedSanFormat, expectedSanConversion, san);
    }
    {
      final String san = "dxe7=Q";
      checkException(san, SanValidationProblem.FORMAT_PAWN_CAPTURE_NON_PROMOTION_OVERLENGTH);
    }
    // (2a) queenNonCapturingMoves Qe5, Qae5, Q2e5, Qc3e5
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_NEITHER, new SanConversion(PieceType.QUEEN, File.NONE, Rank.NONE,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Qe5");
    }
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_FILE, new SanConversion(PieceType.QUEEN, File.FILE_A, Rank.NONE,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Qae5");
    }
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_RANK, new SanConversion(PieceType.QUEEN, File.NONE, Rank.RANK_2,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Q2e5");
    }
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_SQUARE, new SanConversion(PieceType.QUEEN, File.FILE_C, Rank.RANK_3,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Qc3e5");
    }
    // (2b) queenCapturingMoves Qxe5, Qaxe5, Q2xe5, Qc3xe5
    {
      checkValid(SanFormat.RNBQ_CAPTURING_NEITHER, new SanConversion(PieceType.QUEEN, File.NONE, Rank.NONE, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "Qxe5");
    }
    {
      checkValid(SanFormat.RNBQ_CAPTURING_FILE, new SanConversion(PieceType.QUEEN, File.FILE_A, Rank.NONE, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "Qaxe5");
    }
    {
      checkValid(SanFormat.RNBQ_CAPTURING_RANK, new SanConversion(PieceType.QUEEN, File.NONE, Rank.RANK_2, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "Q2xe5");
    }
    {
      checkValid(SanFormat.RNBQ_CAPTURING_SQUARE, new SanConversion(PieceType.QUEEN, File.FILE_C, Rank.RANK_3,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Qc3xe5");
    }
    // (3a) rookNonCapturingMoves Re5, Rae5, R2e5
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_NEITHER, new SanConversion(PieceType.ROOK, File.NONE, Rank.NONE,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Re5");
    }
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_FILE, new SanConversion(PieceType.ROOK, File.FILE_A, Rank.NONE, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "Rae5");
    }
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_RANK, new SanConversion(PieceType.ROOK, File.NONE, Rank.RANK_2, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "R2e5");
    }
    // (3b) rookCapturingMoves Rxe5, Raxe5, R2xe5
    {
      checkValid(SanFormat.RNBQ_CAPTURING_NEITHER, new SanConversion(PieceType.ROOK, File.NONE, Rank.NONE, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "Rxe5");
    }
    {
      checkValid(SanFormat.RNBQ_CAPTURING_FILE, new SanConversion(PieceType.ROOK, File.FILE_A, Rank.NONE, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "Raxe5");
    }
    {
      checkValid(SanFormat.RNBQ_CAPTURING_RANK, new SanConversion(PieceType.ROOK, File.NONE, Rank.RANK_2, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "R2xe5");
    }
    // (4a) knightNonCapturingMoves Ne5, Nce5, N4e5, Nd3e5
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_NEITHER, new SanConversion(PieceType.KNIGHT, File.NONE, Rank.NONE,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Ne5");
    }
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_FILE, new SanConversion(PieceType.KNIGHT, File.FILE_C, Rank.NONE,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Nce5");
    }
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_RANK, new SanConversion(PieceType.KNIGHT, File.NONE, Rank.RANK_4,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "N4e5");
    }
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_SQUARE, new SanConversion(PieceType.KNIGHT, File.FILE_D, Rank.RANK_3,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Nd3e5");
    }
    // (4b) knightCapturingMoves Nxe5, Ncxe5, N4xe5, Nd3xe5
    {
      checkValid(SanFormat.RNBQ_CAPTURING_NEITHER, new SanConversion(PieceType.KNIGHT, File.NONE, Rank.NONE, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "Nxe5");
    }
    {
      checkValid(SanFormat.RNBQ_CAPTURING_FILE, new SanConversion(PieceType.KNIGHT, File.FILE_C, Rank.NONE, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "Ncxe5");
    }
    {
      checkValid(SanFormat.RNBQ_CAPTURING_RANK, new SanConversion(PieceType.KNIGHT, File.NONE, Rank.RANK_4, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "N4xe5");
    }
    {
      checkValid(SanFormat.RNBQ_CAPTURING_SQUARE, new SanConversion(PieceType.KNIGHT, File.FILE_D, Rank.RANK_3,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Nd3xe5");
    }
    // (5a) bishopNonCapturingMoves Be5, Bbe5, B2e5
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_NEITHER, new SanConversion(PieceType.BISHOP, File.NONE, Rank.NONE,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Be5");
    }
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_FILE, new SanConversion(PieceType.BISHOP, File.FILE_B, Rank.NONE,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Bbe5");
    }
    {
      checkValid(SanFormat.RNBQ_NON_CAPTURING_RANK, new SanConversion(PieceType.BISHOP, File.NONE, Rank.RANK_2,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "B2e5");
    }
    // (5b) bishopCapturingMoves Bxe5, Bbxe5, B2xe5
    {
      checkValid(SanFormat.RNBQ_CAPTURING_NEITHER, new SanConversion(PieceType.BISHOP, File.NONE, Rank.NONE, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "Bxe5");
    }
    {
      checkValid(SanFormat.RNBQ_CAPTURING_FILE, new SanConversion(PieceType.BISHOP, File.FILE_B, Rank.NONE, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "Bbxe5");
    }
    {
      checkValid(SanFormat.RNBQ_CAPTURING_RANK, new SanConversion(PieceType.BISHOP, File.NONE, Rank.RANK_2, Square.E5,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), "B2xe5");
    }
    // (6a) kingNonCastlingNonCapturingMoves Ke5
    {
      checkValid(SanFormat.KING_NON_CASTLING_NON_CAPTURING, new SanConversion(PieceType.KING, File.NONE, Rank.NONE,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Ke5");
    }
    // (6b) kingNonCastlingCapturingMoves Kxe5
    {
      checkValid(SanFormat.KING_NON_CASTLING_CAPTURING, new SanConversion(PieceType.KING, File.NONE, Rank.NONE,
          Square.E5, PromotionPieceType.NONE, SanTerminalMarker.NONE), "Kxe5");
    }
    // (6c) kingMovesCastling O-O and O-O-O
    {
      checkValid(SanFormat.KING_CASTLING_KING_SIDE, new SanConversion(PieceType.NONE, File.NONE, Rank.NONE, Square.NONE,
          PromotionPieceType.NONE, SanTerminalMarker.NONE), CastlingConstants.SAN_CASTLING_KING_SIDE);
    }
    {
      checkValid(SanFormat.KING_CASTLING_QUEEN_SIDE, new SanConversion(PieceType.NONE, File.NONE, Rank.NONE,
          Square.NONE, PromotionPieceType.NONE, SanTerminalMarker.NONE), CastlingConstants.SAN_CASTLING_QUEEN_SIDE);
    }
  }

  private static void checkValid(SanFormat expectedSanFormat, SanConversion expectedSanConversion, String san) {
    final SanParse expectedSanExtract = new SanParse(expectedSanFormat, expectedSanConversion);

    final SanParse calculatedSanExtract = SanValidateFormat.validateFormat(san);
    assertEquals(expectedSanExtract, calculatedSanExtract);
    assertEquals(validateFormatReference(san), calculatedSanExtract);
  }

  private static void checkValid(String san) {
    checkValidateFormat(san, false, SanValidationProblem.NONE);
  }

  private static void checkException(String san, SanValidationProblem expectedProblem) {
    checkValidateFormat(san, true, expectedProblem);
  }

  private static void checkValidateFormat(String san, boolean isExceptionExpected,
      SanValidationProblem expectedProblem) {
    checkValidateFormatTest(san, isExceptionExpected, expectedProblem);
    checkValidateFormatReference(san, isExceptionExpected);

  }

  private static void checkValidateFormatTest(String san, boolean isExceptionExpected,
      SanValidationProblem expectedProblem) {
    boolean isException;
    try {
      SanValidateFormat.validateFormat(san);
      isException = false;
    } catch (final SanValidationException e) {
      isException = true;
      System.out.println("SAN: " + san + " -> " + e.getMessage());
      assertEquals(expectedProblem, e.getSanValidationProblem());
    }
    assertEquals(isExceptionExpected, isException);
  }

  private static void checkValidateFormatReference(String san, boolean isExceptionExpected) {

    boolean isException;
    try {
      validateFormatReference(san);
      isException = false;
    } catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
      isException = true;
    }
    assertEquals(isExceptionExpected, isException);
  }

  private static SanParse validateFormatReference(String san) {
    return SanValidateFormatReference.validateFormat(san);
  }

}
