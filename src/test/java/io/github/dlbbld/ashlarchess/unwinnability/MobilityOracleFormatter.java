// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.Board;
import io.github.dlbbld.ashlarchess.board.StaticPosition;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;

public final class MobilityOracleFormatter {

  public static final String HEADER = "fen\tside\tpieceType\tfrom\ttoSquares";

  private MobilityOracleFormatter() {
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException("Usage: MobilityOracleFormatter <fen>");
    }
    final String fen = Nulls.join(" ", Nulls.argsAsList(args));
    System.out.println(HEADER);
    for (final String row : calculateRows(fen)) {
      System.out.println(row);
    }
  }

  public static List<String> calculateRows(String fen) {
    final Board board = new Board(fen);
    final MobilitySolution mobilitySolution = Mobility.mobility(board);
    final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    final List<String> rowList = new ArrayList<>();
    for (final Square source : Square.REAL) {
      final Piece piece = staticPosition.get(source);
      if (piece == Piece.NONE) {
        continue;
      }
      final PiecePlacement piecePlacement = new PiecePlacement(piece.getPieceType(), piece.getSide(), source);
      final Set<Square> toSquareSet = mobilitySolution.calculateSquaresWithValueOne(piecePlacement);
      rowList.add(fen + "\t" + piece.getSide().name() + "\t" + piece.getPieceType().name() + "\t" + source.getName()
          + "\t" + formatSquareList(toSquareSet));
    }
    return rowList;
  }

  private static String formatSquareList(Set<Square> squareSet) {
    final List<String> squareNameList = new ArrayList<>();
    for (final Square square : squareSet) {
      squareNameList.add(square.getName());
    }
    return Nulls.join(",", squareNameList);
  }
}
