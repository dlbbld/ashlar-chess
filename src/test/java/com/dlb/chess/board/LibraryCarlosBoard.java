package com.dlb.chess.board;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.NonNull;

import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ChessConstants;
import com.dlb.chess.common.constants.DynamicPositionConstants;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.LegalMoveKind;
import com.dlb.chess.moves.EnPassantCaptureUtility;
import com.dlb.chess.san.SanSymbol;
import com.dlb.chess.san.SanTerminalMarker;
import com.dlb.chess.test.librarycarlos.NullsCarlos;
import com.dlb.chess.test.librarycarlos.utility.MoveConversionUtility;
import com.dlb.chess.test.librarycomparison.utility.BoardConversionUtitlity;
import com.dlb.chess.test.librarycomparison.utility.EnumConversionUtility;
import com.dlb.chess.bitboard.StaticPositionBridge;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.CastleRight;
import com.github.bhlangonijr.chesslib.MoveBackup;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.google.common.collect.ImmutableList;

public class LibraryCarlosBoard {

  private final Board board = new Board();

  private int performedHalfMoveCount;
  private final List<LegalMove> performedLegalMoveList;
  private final List<DynamicPosition> dynamicPositionList;
  private final List<HalfMove> halfMoveList;

  public LibraryCarlosBoard() {

    performedHalfMoveCount = 0;
    performedLegalMoveList = new ArrayList<>();
    dynamicPositionList = new ArrayList<>();
    dynamicPositionList.add(DynamicPositionConstants.INITIAL);
    halfMoveList = new ArrayList<>();

  }

  public boolean move(MoveSpecification moveSpecification) {
    final Side havingMove = getHavingMove();
    final var result = board.doMove(MoveConversionUtility.convertMoveSpecification(havingMove, moveSpecification));
    populateMoveHistory(moveSpecification);
    return result;
  }

  public com.dlb.chess.san.StrictSanParserValidationResult moveStrict(String san) {
    board.doMove(san);
    final MoveSpecification lastMoveSpecification = calculateLastMoveSpecification();
    populateMoveHistory(lastMoveSpecification);
    return new com.dlb.chess.san.StrictSanParserValidationResult(lastMoveSpecification);
  }

  public com.dlb.chess.san.LenientSanParserValidationResult moveLenient(String san) {
    // Carlos's chesslib doesn't have a lenient SAN concept; delegate to strict, then wrap into the lenient result
    // shape with empty forgiven items. Cross-validation tests only need the move to land on the board.
    final com.dlb.chess.san.StrictSanParserValidationResult strict = moveStrict(san);
    return new com.dlb.chess.san.LenientSanParserValidationResult(strict.moveSpecification(),
        com.dlb.chess.san.ForgivenItem.EMPTY_LIST);
  }

  private MoveSpecification calculateLastMoveSpecification() {
    final var moveBackup = board.getBackup().getLast();
    @SuppressWarnings("null") @NonNull final Move move = moveBackup.getMove();
    final var havingMove = NullsCarlos.getSideToMove(moveBackup);
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
    performedHalfMoveCount++;

    final MoveBackup moveBackup = NullsCarlos.getLast(this.board);
    final LegalMove legalMove = calculateLegalMove(moveSpecification, moveBackup);
    performedLegalMoveList.add(legalMove);
    final var normalizedEnPassantCaptureTargetSquare = isEnPassantCapturePossible() ? getEnPassantCaptureTargetSquare()
        : Square.NONE;
    final var bitboardPosition = StaticPositionBridge.fromStaticPosition(getStaticPosition());
    dynamicPositionList.add(new DynamicPosition(getHavingMove(), bitboardPosition,
        normalizedEnPassantCaptureTargetSquare, getCastlingRightWhite(), getCastlingRightBlack()));

    // ATTENTION: timely dependency, must be after the above code is very very dangerous
    final HalfMove halfMove = buildHalfMove(moveSpecification);
    halfMoveList.add(halfMove);
  }

  public void unmove() {
    board.undoMove();

    performedHalfMoveCount--;
    performedLegalMoveList.remove(performedLegalMoveList.size() - 1);
    dynamicPositionList.remove(dynamicPositionList.size() - 1);
    halfMoveList.remove(halfMoveList.size() - 1);
  }

  public boolean canClaimFiftyMoveRuleWithOwnMove() {
    final var halfMoveClock = getHalfMoveClock();
    if (halfMoveClock == 99) {
      final List<Move> legalMoveList = LibraryCarlosImplementationUtility.generateLegalMoves(this.board);
      // need to check if there is a legal move which has half move clock 100
      for (final Move legalMove : legalMoveList) {
        board.doMove(legalMove);
        final var halfMoveClockAfterNextHalfMove = getHalfMoveClock();
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
    var rep = 1;
    final List<Long> history = board.getHistory();
    final var historySize = board.getHistory().size();
    final long lastKey = history.get(historySize - 1);
    for (var i = 0; i <= historySize - 2; i++) {
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
    final var sanTest = lastMoveBackup.getMove().getSan();
    if (sanTest != null) {
      return sanTest;
    }

    final MoveList moveList = new MoveList();
    moveList.addAll(calculateMoveList(this.board));
    try {
      final var sanArray = moveList.toSanArray();
      @SuppressWarnings("null") final var last = Nulls.getLast(sanArray);
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
      final var movingPieceFenSymbol = movingPiece.getFenSymbol();
      final var movingPieceSymbol = movingPieceFenSymbol.toUpperCase();
      lan.append(movingPieceSymbol);
    }
    lan.append(move.getFrom().toString().toLowerCase());
    if (isCapture()) {
      lan.append(SanSymbol.CAPTURE.getSymbol());
    }
    lan.append(move.getTo().toString().toLowerCase());
    if (calculateIsPromotion(moveBackup)) {
      lan.append(SanSymbol.PROMOTION.getSymbol());
      final var promotionPiece = move.getPromotion();
      final var promotionPieceFenSymbol = promotionPiece.getFenSymbol();
      final var promotionPieceSymbol = promotionPieceFenSymbol.toUpperCase();
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

  public int getPerformedHalfMoveCount() {
    return performedHalfMoveCount;
  }

  public ImmutableList<DynamicPosition> getDynamicPositionList() {
    return Nulls.copyOfList(dynamicPositionList);
  }

  public ImmutableList<HalfMove> getHalfMoveList() {
    return Nulls.copyOfList(halfMoveList);
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
      final var moveBackup = board.getBackup().getLast();
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
    final var fromRank = move.getFrom().getRank().ordinal();
    final var toRank = move.getTo().getRank().ordinal();
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
    final var lastMove = board.undoMove();
    final StaticPosition staticPosition = getStaticPosition();
    board.doMove(lastMove);
    return staticPosition;
  }

  // Bitboard accessors mirror the Board API for the CommonTestUtility cross-comparison. LibraryCarlosBoard has no
  // native bitboard of its own; both derive from the chesslib-derived StaticPosition via the bridge utility.
  public com.dlb.chess.bitboard.BitboardPosition getBitboardPosition() {
    return com.dlb.chess.bitboard.StaticPositionBridge.fromStaticPosition(getStaticPosition());
  }

  public com.dlb.chess.bitboard.BitboardPosition getBitboardPositionBeforeLastMove() {
    return com.dlb.chess.bitboard.StaticPositionBridge.fromStaticPosition(getStaticPositionBeforeLastMove());
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

  private HalfMove buildHalfMove(MoveSpecification moveSpecification) {
    final var halfMoveCount = getPerformedHalfMoveCount();
    final var index = halfMoveCount - 1;
    final var halfMoveClock = getHalfMoveClock();
    final var fullMoveNumber = getFullMoveNumber();
    final String fen = getFen();
    final var isCapture = isCapture();
    final var countRepetition = getRepetitionCount();
    final DynamicPosition dynamicPosition = getDynamicPosition();
    final Piece movingPiece = getMovingPiece();
    return new HalfMove(index, halfMoveCount, fullMoveNumber, halfMoveClock, isCapture, fen, dynamicPosition,
        countRepetition, getSan(), movingPiece, moveSpecification);
  }
}
