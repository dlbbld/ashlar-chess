package com.dlb.chess.unwinnability;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;

public final class SemiStaticOracleFormatter {

  public static final String HEADER = "fen\twinner\tkind\tsubject\tvalue";

  private SemiStaticOracleFormatter() {
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException("Usage: SemiStaticOracleFormatter <fen>");
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
    final List<String> rowList = new ArrayList<>();

    for (final Square source : Square.REAL) {
      if (board.getBitboardPosition().isEmpty(source)) {
        continue;
      }
      final Piece piece = board.getBitboardPosition().get(source);
      final PiecePlacement piecePlacement = calculatePiecePlacement(piece.getPieceType(), piece.getSide(), source,
          mobilitySolution);
      addRow(rowList, fen, "-", "JAVA_ATTACKED_REGION", formatPiece(piecePlacement),
          formatSquares(SemiStaticFunctions.attackedRegion(piecePlacement, mobilitySolution)));
    }

    for (final Side winner : Side.REAL) {
      final PiecePlacement loserKing = calculateKing(winner.getOppositeSide(), mobilitySolution);
      final Set<Square> loserKingRegion = SemiStaticFunctions.region(loserKing, mobilitySolution);

      addRow(rowList, fen, Nulls.name(winner), "VERDICT", "-",
          UnwinnableSemiStatic.unwinnableSemiStatic(board, winner, mobilitySolution) ? "UNWINNABLE"
              : "POSSIBLY_WINNABLE");
      addRow(rowList, fen, Nulls.name(winner), "JAVA_LOSER_KING_REGION", "-", formatSquares(loserKingRegion));
      addRow(rowList, fen, Nulls.name(winner), "JAVA_INTRUDERS", "-",
          formatPieces(SemiStaticFunctions.intruders(loserKing, mobilitySolution)));
      addRow(rowList, fen, Nulls.name(winner), "AMBRONA_VISITORS_EXPANDED", "-",
          formatPieces(UnwinnableSemiStatic.calculateVisitorsExpanded(loserKingRegion, winner, mobilitySolution)));

      for (final Square square : loserKingRegion) {
        addRow(rowList, fen, Nulls.name(winner), "JAVA_BLOCKERS", square.getName(),
            formatPieces(SemiStaticFunctions.blockers(square, winner, mobilitySolution)));
        addRow(rowList, fen, Nulls.name(winner), "JAVA_ASSISTANTS", square.getName(),
            formatPieces(SemiStaticFunctions.assistants(square, winner, mobilitySolution)));
      }
    }
    return rowList;
  }

  private static PiecePlacement calculateKing(Side side, MobilitySolution mobilitySolution) {
    for (final PiecePlacement piecePlacement : mobilitySolution.getPiecePlacementSet()) {
      if (piecePlacement.side() == side && piecePlacement.pieceType() == PieceType.KING) {
        return piecePlacement;
      }
    }
    throw new IllegalArgumentException("King not in the mobility solution");
  }

  private static PiecePlacement calculatePiecePlacement(PieceType pieceType, Side side, Square square,
      MobilitySolution mobilitySolution) {
    for (final PiecePlacement piecePlacement : mobilitySolution.getPiecePlacementSet()) {
      if (piecePlacement.pieceType() == pieceType && piecePlacement.side() == side
          && piecePlacement.squareOriginal() == square) {
        return piecePlacement;
      }
    }
    throw new IllegalArgumentException("Piece not in the mobility solution");
  }

  private static void addRow(List<String> rowList, String fen, String winner, String kind, String subject,
      String value) {
    rowList.add(fen + "\t" + winner + "\t" + kind + "\t" + subject + "\t" + value);
  }

  private static String formatSquares(Set<Square> squareSet) {
    final List<String> valueList = new ArrayList<>();
    for (final Square square : squareSet) {
      valueList.add(square.getName());
    }
    return formatValueList(valueList);
  }

  private static String formatPieces(Set<PiecePlacement> piecePlacementSet) {
    final List<String> valueList = new ArrayList<>();
    for (final Square square : Square.REAL) {
      for (final PiecePlacement piecePlacement : piecePlacementSet) {
        if (piecePlacement.squareOriginal() == square) {
          valueList.add(formatPiece(piecePlacement));
        }
      }
    }
    return formatValueList(valueList);
  }

  private static String formatPiece(PiecePlacement piecePlacement) {
    return piecePlacement.side().name() + ":" + piecePlacement.pieceType().name() + ":"
        + piecePlacement.squareOriginal().getName();
  }

  private static String formatValueList(List<String> valueList) {
    if (valueList.isEmpty()) {
      return "-";
    }
    return Nulls.join(",", valueList);
  }
}
