// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.CastleRight;
import com.github.bhlangonijr.chesslib.MoveBackup;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.ChessConstants;
import io.github.dlbbld.ashlarchess.common.constants.DynamicPositionConstants;
import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.model.LegalMoveKind;
import io.github.dlbbld.ashlarchess.moves.EnPassantCaptureUtility;
import io.github.dlbbld.ashlarchess.san.SanSymbol;
import io.github.dlbbld.ashlarchess.san.SanTerminalMarker;
import io.github.dlbbld.ashlarchess.test.librarycarlos.NullsCarlos;
import io.github.dlbbld.ashlarchess.test.librarycarlos.utility.MoveConversionUtility;
import io.github.dlbbld.ashlarchess.test.librarycomparison.utility.BoardConversionUtitlity;
import io.github.dlbbld.ashlarchess.test.librarycomparison.utility.EnumConversionUtility;

public class LibraryCarlosBoard {

  private final Board board = new Board();

  private int performedMoveCount;
  private final List<LegalMove> performedLegalMoveList;
  private final List<DynamicPosition> dynamicPositionList;

  public LibraryCarlosBoard() {

    performedMoveCount = 0;
    performedLegalMoveList = new ArrayList<>();
    dynamicPositionList = new ArrayList<>();
    dynamicPositionList.add(DynamicPositionConstants.INITIAL);

  }

  public boolean move(MoveSpecification moveSpecification) {
    final Side havingMove = getHavingMove();
    final boolean result = board.doMove(MoveConversionUtility.convertMoveSpecification(havingMove, moveSpecification));
    populateMoveHistory(moveSpecification);
    return result;
  }

  public io.github.dlbbld.ashlarchess.san.StrictSanParserValidationResult moveStrict(String san) {
    board.doMove(san);
    final MoveSpecification lastMoveSpecification = calculateLastMoveSpecification();
    populateMoveHistory(lastMoveSpecification);
    return new io.github.dlbbld.ashlarchess.san.StrictSanParserValidationResult(lastMoveSpecification);
  }

  public io.github.dlbbld.ashlarchess.san.LenientSanParserValidationResult moveLenient(String san) {
    // Carlos's chesslib doesn't have a lenient SAN concept; delegate to strict, then wrap into the lenient result
    // shape with empty forgiven items. Cross-validation tests only need the move to land on the board.
    final io.github.dlbbld.ashlarchess.san.StrictSanParserValidationResult strict = moveStrict(san);
    return new io.github.dlbbld.ashlarchess.san.LenientSanParserValidationResult(strict.moveSpecification(),
        io.github.dlbbld.ashlarchess.san.ForgivenItem.EMPTY_LIST);
  }

  private MoveSpecification calculateLastMoveSpecification() {
    final MoveBackup moveBackup = board.getBackup().getLast();
    @SuppressWarnings("null") @NonNull final Move move = moveBackup.getMove();
    final com.github.bhlangonijr.chesslib.Side havingMove = NullsCarlos.getSideToMove(moveBackup);
    final com.github.bhlangonijr.chesslib.Square fromSquare = NullsCarlos.getFrom(move);
    com.github.bhlangonijr.chesslib.Piece movingPiece;
    if (moveBackup.isCastleMove()) {
      movingPiece = switch (havingMove) {
        case WHITE -> com.github.bhlangonijr.chesslib.Piece.WHITE_KING;
        case BLACK -> com.github.bhlangonijr.chesslib.Piece.BLACK_KING;
        default -> throw new IllegalArgumentException();
      };
    } else {
      movingPiece = NullsCarlos.getPiece(this.board, fromSquare);
    }
    return MoveConversionUtility.convertMove(move, movingPiece);
  }

  private void populateMoveHistory(MoveSpecification moveSpecification) {
    performedMoveCount++;

    final MoveBackup moveBackup = NullsCarlos.getLast(this.board);
    final LegalMove legalMove = calculateLegalMove(moveSpecification, moveBackup);
    performedLegalMoveList.add(legalMove);
    final Square normalizedEnPassantCaptureTargetSquare = isEnPassantCapturePossible()
        ? getEnPassantCaptureTargetSquare()
        : Square.NONE;
    final BitboardPosition bitboardPosition = StaticPositionBridge.fromStaticPosition(getStaticPosition());
    dynamicPositionList.add(new DynamicPosition(getHavingMove(), bitboardPosition,
        normalizedEnPassantCaptureTargetSquare, getCastlingRightWhite(), getCastlingRightBlack()));
  }

  public void unmove() {
    board.undoMove();

    performedMoveCount--;
    performedLegalMoveList.remove(performedLegalMoveList.size() - 1);
    dynamicPositionList.remove(dynamicPositionList.size() - 1);
  }

  public boolean canClaimFiftyMoveRuleWithOwnMove() {
    final int halfMoveClock = getHalfMoveClock();
    if (halfMoveClock == 99) {
      final List<Move> legalMoveList = LibraryCarlosImplementationUtility.generateLegalMoves(this.board);
      // need to check if there is a legal move which has halfmove clock 100
      for (final Move legalMove : legalMoveList) {
        board.doMove(legalMove);
        final int halfMoveClockAfterNextHalfMove = getHalfMoveClock();
        if (halfMoveClockAfterNextHalfMove == 100 && !board.isMated() && !board.isStaleMate()) {
          board.undoMove();
          return true;
        }
        board.undoMove();
      }
    }
    return false;
  }

  public boolean canClaimThreefoldRepetitionRuleWithOwnMove() {
    for (final MoveSpecification moveSpecification : getPossibleMoveSpecificationList()) {
      move(moveSpecification);
      if (isThreefoldRepetition()) {
        unmove();
        return true;
      }
      unmove();
    }
    return false;
  }

  public boolean isCheck() {
    return board.isKingAttacked();
  }

  public boolean isCheckmate() {
    return board.isMated();
  }

  public boolean isStalemate() {
    return board.isStaleMate();
  }

  public int getHalfMoveClock() {
    return board.getHalfMoveCounter();
  }

  @SuppressWarnings("null")
  public int getRepetitionCount() {
    int rep = 1;
    final List<Long> history = board.getHistory();
    final int historySize = board.getHistory().size();
    final long lastKey = history.get(historySize - 1);
    for (int i = 0; i <= historySize - 2; i++) {
      final long currentKey = history.get(i);
      if (currentKey == lastKey) {
        rep++;
      }
    }
    return rep;
  }

  public boolean isInsufficientMaterial() {
    return LibraryCarlosImplementationUtility.calculateIsInsufficientMaterial(this.board);
  }

  public boolean isInsufficientMaterial(Side side) {
    return LibraryCarlosImplementationUtility.calculateIsInsufficientMaterial(side, this.board);
  }

  public String getFen() {
    return NullsCarlos.getFen(this.board);
  }

  @SuppressWarnings("static-method")
  public Fen getInitialFen() {
    // always using initial position, starting from FEN is not supported
    return FenConstants.FEN_INITIAL;
  }

  public String getSan() {
    if (board.getBackup().isEmpty()) {
      throw new IllegalStateException("There is no last move");
    }

    final MoveBackup lastMoveBackup = NullsCarlos.getLast(board);
    final String sanTest = lastMoveBackup.getMove().getSan();
    if (sanTest != null) {
      return sanTest;
    }

    final MoveList moveList = new MoveList();
    moveList.addAll(calculateMoveList(this.board));
    try {
      final String[] sanArray = moveList.toSanArray();
      @SuppressWarnings("null") final String last = Nulls.getLast(sanArray);
      return last;
    } catch (final MoveConversionException e) {
      throw new RuntimeException("San generation in Carlos's API failed", e);
    }
  }

  private static List<Move> calculateMoveList(Board board) {
    final List<Move> result = new ArrayList<>();
    for (final MoveBackup moveBackup : NullsCarlos.getBackup(board)) {
      result.add(NullsCarlos.getMove(moveBackup));
    }
    return result;
  }

  public String getLan() {
    if (board.getBackup().isEmpty()) {
      throw new IllegalStateException("There is no last move");
    }
    final MoveBackup moveBackup = NullsCarlos.getLast(this.board);
    final Move move = NullsCarlos.getMove(moveBackup);
    final com.github.bhlangonijr.chesslib.Piece movingPiece = NullsCarlos.getMovingPiece(moveBackup);

    if ((movingPiece == com.github.bhlangonijr.chesslib.Piece.WHITE_KING
        || movingPiece == com.github.bhlangonijr.chesslib.Piece.BLACK_KING) && board.getContext().isCastleMove(move)) {
      if (board.getContext().isKingSideCastle(move)) {
        return "O-O";
      }
      if (board.getContext().isQueenSideCastle(move)) {
        return "O-O-O";
      }
      throw new ProgrammingMistakeException(
          "There must be a programming mistake in the API, as castling is either kingside or queenside");
    }
    final StringBuilder lan = new StringBuilder();
    // need to workaround a bug that after promotion the piece move is given as promoted piece
    if (!calculateIsPawnMove(moveBackup)) {
      final String movingPieceFenSymbol = movingPiece.getFenSymbol();
      final String movingPieceSymbol = movingPieceFenSymbol.toUpperCase();
      lan.append(movingPieceSymbol);
    }
    lan.append(move.getFrom().toString().toLowerCase());
    if (isCapture()) {
      lan.append(SanSymbol.CAPTURE.getSymbol());
    }
    lan.append(move.getTo().toString().toLowerCase());
    if (calculateIsPromotion(moveBackup)) {
      lan.append(SanSymbol.PROMOTION.getSymbol());
      final com.github.bhlangonijr.chesslib.Piece promotionPiece = move.getPromotion();
      final String promotionPieceFenSymbol = promotionPiece.getFenSymbol();
      final String promotionPieceSymbol = promotionPieceFenSymbol.toUpperCase();
      lan.append(promotionPieceSymbol);
    }

    final SanTerminalMarker sanTerminalMarker = SanTerminalMarker.calculate(isCheck(), isCheckmate());
    sanTerminalMarker.append(lan);

    return Nulls.toString(lan);
  }

  public Piece getMovingPiece() {
    if (board.getBackup().isEmpty()) {
      throw new IllegalStateException("There is no last move");
    }
    final MoveBackup moveBackup = NullsCarlos.getLast(this.board);
    if (moveBackup.isCastleMove()) {
      return Piece.NONE;
    }
    final com.github.bhlangonijr.chesslib.Piece movingPiece = NullsCarlos.getMovingPiece(moveBackup);
    return EnumConversionUtility.convertToMyPiece(movingPiece);
  }

  public boolean isCapture() {
    if (board.getBackup().isEmpty()) {
      throw new IllegalStateException("There is no last move");
    }
    final MoveBackup moveBackup = NullsCarlos.getLast(this.board);
    return moveBackup.getCapturedPiece() != com.github.bhlangonijr.chesslib.Piece.NONE;
  }

  @SuppressWarnings("static-method")
  public int getInitialFenFullMoveNumber() {
    // currently playing from FEN not supported
    return 1;
  }

  public int getFullMoveNumber() {
    if (board.getBackup().isEmpty()) {
      throw new IllegalStateException("There is no last move");
    }
    final MoveBackup moveBackup = NullsCarlos.getLast(this.board);
    return moveBackup.getMoveCounter();
  }

  public boolean isFiftyMove() {
    return getHalfMoveClock() >= ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
  }

  public boolean isThreefoldRepetition() {
    return board.isRepetition();
  }

  public boolean isSeventyFiveMove() {
    return getHalfMoveClock() >= ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
  }

  public boolean isFivefoldRepetition() {
    return board.isRepetition(5);
  }

  public Side getHavingMove() {
    return EnumConversionUtility.convertToMySide(NullsCarlos.getSideToMove(this.board));
  }

  public StaticPosition getStaticPosition() {
    return BoardConversionUtitlity.convertBoardToStaticPosition(this.board);
  }

  public boolean isEnPassantCapturePossible() {
    return LibraryCarlosImplementationUtility.calculateIsEnPassantCapturePossible(this.board);
  }

  public CastlingRight getCastlingRightWhite() {
    @SuppressWarnings("null") final EnumMap<com.github.bhlangonijr.chesslib.Side, CastleRight> castlingRightMap = board
        .getCastleRight();
    @SuppressWarnings("null") final CastleRight castlingRightWhite = castlingRightMap
        .get(com.github.bhlangonijr.chesslib.Side.WHITE);
    return mapCastlingRight(castlingRightWhite);
  }

  public CastlingRight getCastlingRightBlack() {
    @SuppressWarnings("null") final EnumMap<com.github.bhlangonijr.chesslib.Side, CastleRight> castlingRightMap = board
        .getCastleRight();
    @SuppressWarnings("null") final CastleRight castlingRightBlack = castlingRightMap
        .get(com.github.bhlangonijr.chesslib.Side.BLACK);
    return mapCastlingRight(castlingRightBlack);
  }

  private static CastlingRight mapCastlingRight(CastleRight carlosCastlingRight) {
    return switch (carlosCastlingRight) {
      case KING_AND_QUEEN_SIDE -> CastlingRight.KING_AND_QUEEN_SIDE;
      case KING_SIDE -> CastlingRight.KING_SIDE;
      case QUEEN_SIDE -> CastlingRight.QUEEN_SIDE;
      case NONE -> CastlingRight.NONE;
      default -> throw new IllegalArgumentException();
    };
  }

  public int getPerformedMoveCount() {
    return performedMoveCount;
  }

  public ImmutableList<DynamicPosition> getDynamicPositionList() {
    return Nulls.copyOfList(dynamicPositionList);
  }

  public DynamicPosition getDynamicPosition() {
    return Nulls.getLast(dynamicPositionList);
  }

  public ImmutableList<MoveSpecification> getPossibleMoveSpecificationList() {
    return Nulls.copyOfList(generateMoveSpecificationSortedSet(this.board));
  }

  // the API does not return null
  @SuppressWarnings("null")
  private static List<Move> generateLegalMoveList(Board board) {
    try {
      return MoveGenerator.generateLegalMoves(board);
    } catch (final MoveGeneratorException e) {
      throw new RuntimeException("Problem with legal move generation", e);
    }
  }

  @SuppressWarnings("null")
  private static List<MoveBackup> generateLegalMoveBackupList(Board board) {
    List<Move> legalMoveList;
    try {
      legalMoveList = MoveGenerator.generateLegalMoves(board);
    } catch (final MoveGeneratorException e) {
      throw new RuntimeException("Problem with legal move generation", e);
    }

    final List<MoveBackup> moveBackupList = new ArrayList<>();
    for (final Move move : legalMoveList) {
      board.doMove(move);
      final MoveBackup moveBackup = board.getBackup().getLast();
      board.undoMove();
      moveBackupList.add(moveBackup);
    }
    return moveBackupList;
  }

  private static Set<MoveSpecification> generateMoveSpecificationSortedSet(Board board) {
    final List<Move> moveList = generateLegalMoveList(board);

    final Set<MoveSpecification> result = new TreeSet<>();
    for (final Move move : moveList) {
      final MoveSpecification moveSpecification = convertMove(board, move);
      result.add(moveSpecification);
    }
    return result;
  }

  private static MoveSpecification convertMove(Board board, Move move) {
    final com.github.bhlangonijr.chesslib.Square fromSquare = NullsCarlos.getFrom(move);
    final com.github.bhlangonijr.chesslib.Piece movingPiece = NullsCarlos.getPiece(board, fromSquare);
    return MoveConversionUtility.convertMove(move, movingPiece);
  }

  private static Set<LegalMove> generateLegalMoveSortedSet(Board board) {
    final List<MoveBackup> moveBackupList = generateLegalMoveBackupList(board);

    final Set<LegalMove> result = new TreeSet<>();
    for (final MoveBackup moveBackup : moveBackupList) {
      final Move move = NullsCarlos.getMove(moveBackup);
      final MoveSpecification moveSpecification = convertMove(board, move);
      final Piece movingPiece = EnumConversionUtility.convertPiece(NullsCarlos.getMovingPiece(moveBackup));
      final Piece pieceCaptured = EnumConversionUtility.convertPiece(NullsCarlos.getCapturedPiece(moveBackup));
      final LegalMove legalMove = new LegalMove(moveSpecification, movingPiece, pieceCaptured,
          calculateKind(moveBackup));
      result.add(legalMove);
    }
    return result;
  }

  public boolean isFirstMove() {
    return board.getBackup().isEmpty();
  }

  public LegalMove getLastMove() {
    return Nulls.getLast(performedLegalMoveList);
  }

  private static LegalMove calculateLegalMove(MoveSpecification moveSpecification, MoveBackup moveBackup) {
    final Piece movingPiece = EnumConversionUtility.convertToMyPiece(NullsCarlos.getMovingPiece(moveBackup));
    final Piece pieceCaptured = EnumConversionUtility.convertToMyPiece(NullsCarlos.getCapturedPiece(moveBackup));
    return new LegalMove(moveSpecification, movingPiece, pieceCaptured, calculateKind(moveBackup));
  }

  private static LegalMoveKind calculateKind(MoveBackup moveBackup) {
    if (moveBackup.isCastleMove()) {
      return LegalMoveKind.CASTLING;
    }
    if (moveBackup.isEnPassantMove()) {
      return LegalMoveKind.EN_PASSANT_CAPTURE;
    }
    if (calculateIsPromotion(moveBackup)) {
      return LegalMoveKind.PROMOTION;
    }
    if (calculateIsPawnTwoSquareAdvance(moveBackup)) {
      return LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE;
    }
    return LegalMoveKind.NORMAL;
  }

  private static boolean calculateIsPawnTwoSquareAdvance(MoveBackup moveBackup) {
    if (!calculateIsPawnMove(moveBackup)) {
      return false;
    }
    final Move move = NullsCarlos.getMove(moveBackup);
    final int fromRank = move.getFrom().getRank().ordinal();
    final int toRank = move.getTo().getRank().ordinal();
    return Math.abs(fromRank - toRank) == 2;
  }

  public Square getEnPassantCaptureTargetSquare() {
    return EnPassantCaptureUtility.calculateEnPassantCaptureTargetSquare(getLastMove());
  }

  public ImmutableList<MoveSpecification> getPerformedMoveSpecificationList() {
    final List<MoveSpecification> moveSpecificationList = new ArrayList<>();
    for (final MoveBackup moveBackup : NullsCarlos.getBackup(this.board)) {

      final Move move = NullsCarlos.getMove(moveBackup);
      final com.github.bhlangonijr.chesslib.Piece movingPiece = NullsCarlos.getMovingPiece(moveBackup);

      moveSpecificationList.add(MoveConversionUtility.convertMove(move, movingPiece));
    }
    return Nulls.copyOfList(moveSpecificationList);
  }

  public ImmutableList<LegalMove> getLegalMoves() {
    return Nulls.copyOfList(generateLegalMoveSortedSet(this.board));
  }

  private static boolean calculateIsPawnMove(MoveBackup moveBackup) {
    final com.github.bhlangonijr.chesslib.Piece movingPiece = NullsCarlos.getMovingPiece(moveBackup);

    return movingPiece == com.github.bhlangonijr.chesslib.Piece.WHITE_PAWN
        || movingPiece == com.github.bhlangonijr.chesslib.Piece.BLACK_PAWN;
  }

  private static boolean calculateIsPromotion(MoveBackup moveBackup) {
    if (!calculateIsPawnMove(moveBackup)) {
      return false;
    }
    final Move move = NullsCarlos.getMove(moveBackup);
    final com.github.bhlangonijr.chesslib.Piece movingPiece = NullsCarlos.getMovingPiece(moveBackup);

    return switch (movingPiece.getPieceSide()) {
      case WHITE -> switch (move.getTo().getRank()) {
        case RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7 -> false;
        case RANK_8 -> true;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case BLACK -> switch (move.getTo().getRank()) {
        case RANK_1 -> true;
        case RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8 ->
            // would be illegal for a white pawn, but we are not checking here
            false;
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      }; // would be illegal for a white pawn, but we are not checking here

      default -> throw new IllegalArgumentException();
    };
  }

  public StaticPosition getStaticPositionBeforeLastMove() {
    if (isFirstMove()) {
      throw new ProgrammingMistakeException("The method cannot be called if no move was yet made");
    }
    final Move lastMove = board.undoMove();
    final StaticPosition staticPosition = getStaticPosition();
    board.doMove(lastMove);
    return staticPosition;
  }

  // Bitboard accessors mirror the Board API for the CommonTestUtility cross-comparison. LibraryCarlosBoard has no
  // native bitboard of its own; both derive from the chesslib-derived StaticPosition via the bridge utility.
  public io.github.dlbbld.ashlarchess.bitboard.BitboardPosition getBitboardPosition() {
    return io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge.fromStaticPosition(getStaticPosition());
  }

  public io.github.dlbbld.ashlarchess.bitboard.BitboardPosition getBitboardPositionBeforeLastMove() {
    return io.github.dlbbld.ashlarchess.bitboard.StaticPositionBridge
        .fromStaticPosition(getStaticPositionBeforeLastMove());
  }

  public boolean movesStrict(String... sanArray) {
    for (final String san : sanArray) {
      if (san == null) {
        throw new IllegalArgumentException();
      }
      this.moveStrict(san);
    }
    return true;
  }

  public boolean movesLenient(String... sanArray) {
    for (final String san : sanArray) {
      if (san == null) {
        throw new IllegalArgumentException();
      }
      this.moveLenient(san);
    }
    return true;
  }

  public ImmutableList<LegalMove> getPerformedLegalMoveList() {
    return Nulls.copyOfList(performedLegalMoveList);
  }

  // ===== Methods previously inherited as `default` from the (now-removed) ChessBoard interface =====
  // Only the ones still cross-validated in CommonTestUtility are kept.

  public boolean canClaimFiftyMoveRule() {
    if (isFiftyMove()) {
      return true;
    }
    return canClaimFiftyMoveRuleWithOwnMove();
  }

  public boolean canClaimThreefoldRepetitionRule() {
    if (isThreefoldRepetition()) {
      return true;
    }
    return canClaimThreefoldRepetitionRuleWithOwnMove();
  }

  public ImmutableList<String> getLegalMovesSan() {
    final List<String> result = new ArrayList<>();
    for (final MoveSpecification moveSpecification : getPossibleMoveSpecificationList()) {
      this.move(moveSpecification);
      result.add(getSan());
      this.unmove();
    }
    return Nulls.copyOfList(result);
  }
}
