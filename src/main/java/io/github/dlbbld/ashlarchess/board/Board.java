// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.bitboard.BitboardLegalMoveFactory;
import io.github.dlbbld.ashlarchess.bitboard.BitboardPosition;
import io.github.dlbbld.ashlarchess.board.enums.CastlingMove;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRight;
import io.github.dlbbld.ashlarchess.board.enums.CastlingRightLoss;
import io.github.dlbbld.ashlarchess.board.enums.Piece;
import io.github.dlbbld.ashlarchess.board.enums.PieceType;
import io.github.dlbbld.ashlarchess.board.enums.Side;
import io.github.dlbbld.ashlarchess.board.enums.Square;
import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.constants.ChessConstants;
import io.github.dlbbld.ashlarchess.common.constants.DynamicPositionConstants;
import io.github.dlbbld.ashlarchess.common.enums.Termination;
import io.github.dlbbld.ashlarchess.exceptions.ProgrammingMistakeException;
import io.github.dlbbld.ashlarchess.common.model.ClaimRights;
import io.github.dlbbld.ashlarchess.common.model.ClaimableMove;
import io.github.dlbbld.ashlarchess.common.model.DynamicPosition;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;
import io.github.dlbbld.ashlarchess.common.model.Outcome;
import io.github.dlbbld.ashlarchess.common.ucimove.utility.UciMoveUtility;
import io.github.dlbbld.ashlarchess.fen.FenBoard;
import io.github.dlbbld.ashlarchess.fen.LenientFenParser;
import io.github.dlbbld.ashlarchess.fen.StrictFenParser;
import io.github.dlbbld.ashlarchess.fen.FenConstants;
import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.model.CastlingRightBoth;
import io.github.dlbbld.ashlarchess.model.LegalMove;
import io.github.dlbbld.ashlarchess.moves.CastlingUtility;
import io.github.dlbbld.ashlarchess.moves.EnPassantCaptureUtility;
import io.github.dlbbld.ashlarchess.san.LenientSanParseResult;
import io.github.dlbbld.ashlarchess.san.LenientSanParser;
import io.github.dlbbld.ashlarchess.san.LenientSanParserValidationException;
import io.github.dlbbld.ashlarchess.san.MoveToLan;
import io.github.dlbbld.ashlarchess.san.MoveToSan;
import io.github.dlbbld.ashlarchess.san.SanTerminalMarker;
import io.github.dlbbld.ashlarchess.san.SanTerminalMarkerUtility;
import io.github.dlbbld.ashlarchess.san.StrictSanParser;
import io.github.dlbbld.ashlarchess.unwinnability.DeadPositionAnalyzer;
import io.github.dlbbld.ashlarchess.unwinnability.DeadPositionFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.DeadPositionQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityFullVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnabilityQuickVerdict;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableFullAnalyzer;
import io.github.dlbbld.ashlarchess.unwinnability.UnwinnableQuickAnalyzer;

/**
 * The library's central type - a chess <em>game</em>, not merely a position. A {@code Board} carries the position
 * <strong>plus</strong> the move history from its initial FEN: one record per position reached (the move played, the
 * check / checkmate / stalemate flags, the dynamic position, the halfmove clock, the castling-right loss reasons, and
 * the derived SAN / LAN strings), plus the legal moves of the <em>current</em> position - everything needed to answer
 * rule-level questions about the game so far. Legal moves are derived cache, not history: past positions do not retain
 * their legal-move lists, and the current set is recomputed on {@link #unmove()}.
 *
 * <h2>Construction</h2>
 *
 * <p>
 * Core construction entry points:
 *
 * <ul>
 * <li>{@link #Board()} - start at the initial position.</li>
 * <li>{@link #fromFenStrict(String)} - start at the position given by a strict FEN string.</li>
 * <li>{@link #fromFenLenient(String)} - start at a recovered FEN from common display/short-form variants.</li>
 * <li>{@link #Board(Fen)} - start at a pre-parsed {@link Fen} value.</li>
 * </ul>
 *
 * <h2>Mutating the game</h2>
 *
 * <p>
 * Move execution happens through {@link #moveStrict(String)}, {@link #moveLenient(String)},
 * {@link #move(MoveSpecification)}, {@link #movesStrict(String...)}, and is undone by {@link #unmove()}. Both
 * move-pipelines validate the candidate against the current legal-move set; an invalid move throws (see
 * {@link io.github.dlbbld.ashlarchess.board.InvalidMoveException} from the {@code MoveSpecification} pipeline,
 * {@code SanValidationException} from the SAN pipeline). The move pipeline does <em>not</em> gate on game-end states:
 * at checkmate and stalemate the legal-move set is empty, so any attempted move fails through ordinary legality; at
 * mutual insufficient material, fivefold repetition, the 75-move rule, and analyzer-driven dead positions, legal moves
 * still exist and the pipeline accepts them (the caller polls and decides whether to adjudicate). The package-level
 * Javadoc on {@link io.github.dlbbld.ashlarchess.board} documents the strict-game invariant in detail.
 *
 * <h2>Querying the game</h2>
 *
 * <p>
 * Beyond move execution, {@code Board} exposes the standard rule-level predicates: {@link #isCheckmate()},
 * {@link #isStalemate()}, {@link #isThreefoldRepetition()}, {@link #isFiftyMove()}, {@link #isFivefoldRepetition()},
 * {@link #isSeventyFiveMove()}, plus the side-specific unwinnability verdict methods ({@code unwinnableQuick},
 * {@code unwinnableFull}) - the library's flagship CHA feature. Whole-position dead-position checks (no intended
 * winner) live on the analyzers; see {@link io.github.dlbbld.ashlarchess.unwinnability}. Position-state accessors
 * return unmodifiable JDK {@code List}/{@code Set}; mutation is exclusively via {@code move}/{@code unmove}.
 *
 * <p>
 * For game-level reports (threefold-claim-ahead, repetition listings, no-progress sequences), use
 * {@link io.github.dlbbld.ashlarchess.report.Reporter}.
 *
 * <h2>Thread-safety</h2>
 *
 * <p>
 * {@code Board} is mutable and <strong>not thread-safe</strong>. Use one {@code Board} per thread, or synchronize
 * externally. {@link #equals(Object)} and {@link #hashCode()} reflect the current game state, so a {@code Board} placed
 * in a {@link java.util.HashMap} or {@link java.util.HashSet} and then mutated will violate the collection's invariants
 * - don't do that.
 */
public final class Board {

  private final Fen initialFen;
  // One BoardState per position the game has passed through.
  // Entry i is the board's full derived state at the position reached after move i; entry 0 is the initial
  // position, whose move/san/lan are null (no move produced it). The move-indexed data (move, san, lan)
  // rides on the position the move produced; the position-indexed data (legal moves, check flags, dynamic
  // position, halfmove clock, castling-loss reasons) is that position's own state. A move appends one entry;
  // unmove pops one. report.MoveRecords still derives its rows on demand through the public accessors.
  private final List<BoardState> boardStates;

  // Repetition-count index for the current history prefix: how many times each DynamicPosition has occurred. A
  // DynamicPosition is the exact FIDE repetition identity (side to move, piece placement, normalized en-passant
  // square, castling rights), and identical positions can only recur within one no-progress window - a pawn move or
  // capture is irreversible - so this occurrence count IS the FIDE repetition count. Maintained incrementally by
  // move() / unmove() so getRepetitionCount() is O(1); repetition count is a property of the history prefix, not of
  // any one stored position, so it is not carried on BoardState.
  private final Map<DynamicPosition, Integer> repetitionCounts;

  // Legal moves of the CURRENT position only. Legal moves are derived cache, not game history, so historical
  // positions do not retain them; this is recomputed on unmove() for the restored position.
  private List<LegalMove> currentLegalMoves;

  private record BoardState(@Nullable LegalMove move, @Nullable String san, @Nullable String lan, boolean isCheck,
      boolean isCheckmate, boolean isStalemate, DynamicPosition dynamicPosition, int halfMoveClock,
      CastlingRightLoss whiteKingSideLoss, CastlingRightLoss whiteQueenSideLoss, CastlingRightLoss blackKingSideLoss,
      CastlingRightLoss blackQueenSideLoss) {
  }

  /**
   * Constructs a {@code Board} at the position carried by the given pre-parsed {@link Fen}.
   */
  public Board(Fen initialFen) {

    // using the static fen in case saves a bit of memory
    Fen initialFenUse;
    if (initialFen.equals(FenConstants.FEN_INITIAL)) {
      initialFenUse = FenConstants.FEN_INITIAL;
    } else {
      initialFenUse = initialFen;
    }

    // values used in the following not to be get from board methods!!!
    final Side initialSideToMove = initialFenUse.sideToMove();
    final CastlingRight initialCastlingRight = CastlingUtility.getCastlingRight(initialFenUse, initialSideToMove);
    final Square initialEnPassantCaptureTargetSquare = initialFenUse.enPassantCaptureTargetSquare();

    this.initialFen = initialFenUse;

    final BitboardPosition initialBitboardPosition = initialFenUse.bitboardPosition();
    final long initialEnPassantBit = initialEnPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << initialEnPassantCaptureTargetSquare.ordinal();

    // Normalize: keep the target square on DynamicPosition only when an opposing pawn can actually capture there.
    // The raw FEN-spec square is preserved on Board (see getEnPassantCaptureTargetSquare()) for FEN export.
    final Square initialNormalizedEnPassantCaptureTargetSquare = calculateIsEnPassantCapturePossible(
        initialEnPassantCaptureTargetSquare, initialSideToMove, initialBitboardPosition)
            ? initialEnPassantCaptureTargetSquare
            : Square.NONE;

    final List<LegalMove> legalMoves = BitboardLegalMoveFactory.calculateLegalMoves(initialBitboardPosition,
        initialSideToMove, initialCastlingRight, initialEnPassantBit);
    final boolean isCheck = initialBitboardPosition.isInCheck(initialSideToMove);
    final boolean isCheckmate = isCheck && legalMoves.isEmpty();
    final boolean isStalemate = !isCheck && legalMoves.isEmpty();

    // attention - must be after we calculated the legal moves - we need them to check if en passant capture is possible
    // order of instructions dependency!!
    final CastlingRight initialCastlingRightWhite = CastlingUtility.getCastlingRight(initialFenUse, Side.WHITE);
    final CastlingRight initialCastlingRightBlack = CastlingUtility.getCastlingRight(initialFenUse, Side.BLACK);
    final DynamicPosition initialDynamicPosition;
    if (initialFenUse.equals(FenConstants.FEN_INITIAL)) {
      initialDynamicPosition = DynamicPositionConstants.INITIAL;
    } else {
      initialDynamicPosition = new DynamicPosition(initialSideToMove, initialBitboardPosition,
          initialNormalizedEnPassantCaptureTargetSquare, initialCastlingRightWhite, initialCastlingRightBlack);
    }

    final CastlingRightLoss initialWhiteKingSideLoss = initialCastlingRightWhite == CastlingRight.KING_AND_QUEEN_SIDE
        || initialCastlingRightWhite == CastlingRight.KING_SIDE ? CastlingRightLoss.NOT_LOST
            : CastlingRightLoss.UNKNOWN_FEN_IMPORT;
    final CastlingRightLoss initialWhiteQueenSideLoss = initialCastlingRightWhite == CastlingRight.KING_AND_QUEEN_SIDE
        || initialCastlingRightWhite == CastlingRight.QUEEN_SIDE ? CastlingRightLoss.NOT_LOST
            : CastlingRightLoss.UNKNOWN_FEN_IMPORT;
    final CastlingRightLoss initialBlackKingSideLoss = initialCastlingRightBlack == CastlingRight.KING_AND_QUEEN_SIDE
        || initialCastlingRightBlack == CastlingRight.KING_SIDE ? CastlingRightLoss.NOT_LOST
            : CastlingRightLoss.UNKNOWN_FEN_IMPORT;
    final CastlingRightLoss initialBlackQueenSideLoss = initialCastlingRightBlack == CastlingRight.KING_AND_QUEEN_SIDE
        || initialCastlingRightBlack == CastlingRight.QUEEN_SIDE ? CastlingRightLoss.NOT_LOST
            : CastlingRightLoss.UNKNOWN_FEN_IMPORT;

    this.boardStates = new ArrayList<>();
    this.boardStates.add(new BoardState(null, null, null, isCheck, isCheckmate, isStalemate, initialDynamicPosition,
        initialFenUse.halfMoveClock(), initialWhiteKingSideLoss, initialWhiteQueenSideLoss, initialBlackKingSideLoss,
        initialBlackQueenSideLoss));
    this.repetitionCounts = new HashMap<>();
    this.repetitionCounts.put(initialDynamicPosition, 1);
    this.currentLegalMoves = legalMoves;
  }

  /**
   * Constructs a {@code Board} at the standard initial position.
   */
  public Board() {
    this(FenConstants.FEN_INITIAL);
  }

  /**
   * Creates a new board whose initial position is this board's current position, without carrying over the move
   * history.
   *
   */
  public Board copyCurrentPositionWithoutHistory() {
    final Fen currentPosition = new Fen(getFen(), getBitboardPosition(), getSideToMove(), getCastlingRightWhite(),
        getCastlingRightBlack(), getEnPassantCaptureTargetSquare(), 0, getFullMoveNumber());
    return new Board(currentPosition);
  }

  /**
   * Creates a {@code Board} from a strict FEN string. Enforces structural and rule-consistency checks (piece counts
   * within physical bounds, no pawns on rank 1 or 8, castling rights consistent with king/rook static positions,
   * en-passant target consistent with the side to move, halfmove clock consistent with the fullmove number, etc.). The
   * halfmove clock itself is not capped - the FIDE 75-move rule is a queryable predicate on {@code Board}, not enforced
   * at FEN import. Does not prove full game reachability - see the {@code io.github.dlbbld.ashlarchess.fen} package
   * documentation for the full contract.
   */
  public static Board fromFenStrict(String fen) {
    return new Board(StrictFenParser.parse(fen));
  }

  /**
   * Constructs a {@code Board} from a FEN string via {@link LenientFenParser}. The lenient layer applies a
   * syntactic-tolerance pass (whitespace, casing, missing halfmove clock and fullmove number, non-canonical castling
   * order, non-ASCII dashes, trailing garbage) before delegating to {@link StrictFenParser}. Strict semantic invariants
   * are unchanged: a FEN with a missing king, a pawn on rank 1, an impossible double-check, or castling rights that
   * contradict the piece placement still fails. Callers who need to see the list of tolerated deviations should invoke
   * {@link LenientFenParser#validate(String)} directly.
   *
   * @throws io.github.dlbbld.ashlarchess.fen.LenientFenParserValidationException when the input cannot be recovered or
   *                                                                              fails the strict semantic checks
   */
  public static Board fromFenLenient(String fen) {
    return new Board(LenientFenParser.parse(fen));
  }

  public boolean isFirstMove() {
    return boardStates.size() == 1;
  }

  /**
   * Plays the given move on this board. The {@code MoveSpecification} is validated against the current legal-move set;
   * an illegal move throws {@link InvalidMoveException}. (The pipeline does not gate on game-end states; see the class
   * Javadoc.)
   */
  public void move(MoveSpecification moveSpecification) throws InvalidMoveException {
    ValidateNewMove.validateNewMove(this, moveSpecification);
    performMoveWithoutValidation(moveSpecification);
  }

  /**
   * Plays the given move on this board, specified in canonical SAN, and returns the resolved {@link MoveSpecification};
   * for callers that only need success / fail, the absence of a thrown exception is the answer. Use
   * {@link #moveLenient(String)} when parsing real-world PGN that may contain forgivable deviations.
   *
   * @throws io.github.dlbbld.ashlarchess.san.SanValidationException if {@code san} is not canonical SAN, or is
   *                                                                 canonical but does not represent a legal move
   */
  public MoveSpecification moveStrict(String san) {
    final MoveSpecification moveSpecification = StrictSanParser.parse(san, this);
    this.performMoveWithoutValidation(moveSpecification);
    if (!san.equals(this.getSan())) {
      throw new ProgrammingMistakeException("The provided SAN and generated SAN are different, this should not happen");
    }
    return moveSpecification;
  }

  /**
   * Plays the given move on this board, specified in lenient SAN. Accepts inputs the strict pipeline rejects when those
   * inputs uniquely identify a legal move and the deviation matches a supported tolerance category (case variation,
   * long-algebraic / UCI form, castling with digit zero, missing or wrong check / checkmate suffix, over-specification,
   * missing or spurious capture marker, missing promotion equals, explicit pawn letter). The returned
   * {@link LenientSanParseResult} carries the resolved {@code MoveSpecification} together with one
   * {@code ForgivenSanItem} per deviation that was forgiven; on canonical input the forgiven-items list is empty.
   *
   * @throws io.github.dlbbld.ashlarchess.san.LenientSanParserValidationException if the input cannot be resolved to a
   *                                                                              legal move even after applying every
   *                                                                              supported tolerance
   */
  public LenientSanParseResult moveLenient(String san) {
    final LenientSanParseResult result = LenientSanParser.parse(san, this);
    this.performMoveWithoutValidation(result.moveSpecification());
    return result;
  }

  /**
   * Plays the given sequence of canonical SAN moves on this board, in order. Convenience for batch play; the absence of
   * a thrown exception means every move was canonical and legal.
   */
  public void movesStrict(String... sanArray) {
    for (final String san : sanArray) {
      if (san == null) {
        throw new IllegalArgumentException("The SAN cannot be null");
      }
      moveStrict(san);
    }
  }

  /**
   * Plays the given sequence of canonical SAN moves on this board, in order. Convenience for batch play; the absence of
   * a thrown exception means every move was canonical and legal.
   */
  public void movesLenient(String... sanArray) {
    for (final String san : sanArray) {
      if (san == null) {
        throw new IllegalArgumentException("The SAN cannot be null");
      }
      moveLenient(san);
    }
  }

  private void performMoveWithoutValidation(MoveSpecification moveSpecification) throws InvalidMoveException {

    final BoardState beforeState = Nulls.getLast(boardStates);
    final CastlingRight beforeCastlingRightWhite = beforeState.dynamicPosition().castlingRightWhite();
    final CastlingRight beforeCastlingRightBlack = beforeState.dynamicPosition().castlingRightBlack();

    final Side sideToMove = this.getSideToMove();
    final BitboardPosition beforeBitboardPosition = beforeState.dynamicPosition().bitboardPosition();
    final LegalMove moveToPerform = BitboardLegalMoveFactory.toLegalMove(beforeBitboardPosition, moveSpecification,
        sideToMove);

    final Side afterSideToMove = sideToMove.getOppositeSide();
    final CastlingRightBoth afterCastlingRightBoth = CastlingUtility
        .calculateCastlingRightBoth(beforeCastlingRightWhite, beforeCastlingRightBlack, moveToPerform);
    final CastlingRight afterCastlingRightSideToMove = CastlingUtility.getCastlingRight(afterCastlingRightBoth,
        afterSideToMove);
    final Square afterEnPassantCaptureTargetSquare = EnPassantCaptureUtility
        .calculateEnPassantCaptureTargetSquare(moveToPerform);

    final BitboardPosition afterBitboardPosition = beforeBitboardPosition.afterMove(moveSpecification, sideToMove);

    // Normalize for DynamicPosition; see initial-position construction site for the rationale.
    final Square afterNormalizedEnPassantCaptureTargetSquare = calculateIsEnPassantCapturePossible(
        afterEnPassantCaptureTargetSquare, afterSideToMove, afterBitboardPosition) ? afterEnPassantCaptureTargetSquare
            : Square.NONE;

    // castling loss reasons, derived from the previous position's reasons
    final CastlingRightLoss whiteKingSideLoss = CastlingUtility.calculateCastlingRightLoss(moveToPerform,
        beforeState.whiteKingSideLoss(), Side.WHITE, CastlingMove.KING_SIDE);
    final CastlingRightLoss whiteQueenSideLoss = CastlingUtility.calculateCastlingRightLoss(moveToPerform,
        beforeState.whiteQueenSideLoss(), Side.WHITE, CastlingMove.QUEEN_SIDE);
    final CastlingRightLoss blackKingSideLoss = CastlingUtility.calculateCastlingRightLoss(moveToPerform,
        beforeState.blackKingSideLoss(), Side.BLACK, CastlingMove.KING_SIDE);
    final CastlingRightLoss blackQueenSideLoss = CastlingUtility.calculateCastlingRightLoss(moveToPerform,
        beforeState.blackQueenSideLoss(), Side.BLACK, CastlingMove.QUEEN_SIDE);

    final long afterEnPassantBit = afterEnPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << afterEnPassantCaptureTargetSquare.ordinal();

    final List<LegalMove> legalMovesAfterMove = BitboardLegalMoveFactory
        .calculateLegalMoves(afterBitboardPosition, afterSideToMove, afterCastlingRightSideToMove, afterEnPassantBit);

    final boolean isCheck = afterBitboardPosition.isInCheck(afterSideToMove);
    final boolean isCheckmate = isCheck && legalMovesAfterMove.isEmpty();
    final boolean isStalemate = !isCheck && legalMovesAfterMove.isEmpty();

    final DynamicPosition newDynamicPosition = new DynamicPosition(afterSideToMove, afterBitboardPosition,
        afterNormalizedEnPassantCaptureTargetSquare, afterCastlingRightBoth.castlingRightWhite(),
        afterCastlingRightBoth.castlingRightBlack());

    final int newHalfMoveClock = calculateNewHalfMoveClock(moveToPerform, beforeState.halfMoveClock());

    // SAN disambiguation uses the legal moves of the position the move is played FROM (the current position)
    final SanTerminalMarker sanTerminalMarker = SanTerminalMarkerUtility.calculate(isCheck, isCheckmate);
    final String san = MoveToSan.toSan(moveToPerform, currentLegalMoves, sanTerminalMarker);
    final String lan = MoveToLan.toLan(moveToPerform, sanTerminalMarker);

    // now changing board class state, so performing the move! Keep the repetition index in lockstep with the list.
    incrementRepetitionCount(newDynamicPosition);
    this.boardStates.add(new BoardState(moveToPerform, san, lan, isCheck, isCheckmate, isStalemate, newDynamicPosition,
        newHalfMoveClock, whiteKingSideLoss, whiteQueenSideLoss, blackKingSideLoss, blackQueenSideLoss));
    this.currentLegalMoves = legalMovesAfterMove;
  }

  // The move that reached the position at {@code positionIndex} (>= 1); never the initial position (index 0).
  private LegalMove moveAt(int positionIndex) {
    final LegalMove move = Nulls.get(boardStates, positionIndex).move();
    if (move == null) {
      throw new ProgrammingMistakeException("the board state at index " + positionIndex + " carries no move");
    }
    return move;
  }

  // The SAN of the move that reached the position at {@code positionIndex} (>= 1).
  private String sanAt(int positionIndex) {
    final String san = Nulls.get(boardStates, positionIndex).san();
    if (san == null) {
      throw new ProgrammingMistakeException("the board state at index " + positionIndex + " carries no SAN");
    }
    return san;
  }

  // The LAN of the move that reached the position at {@code positionIndex} (>= 1).
  private String lanAt(int positionIndex) {
    final String lan = Nulls.get(boardStates, positionIndex).lan();
    if (lan == null) {
      throw new ProgrammingMistakeException("the board state at index " + positionIndex + " carries no LAN");
    }
    return lan;
  }

  // Repetition-index maintenance, kept in lockstep with boardStates: move() increments the entered position,
  // unmove() decrements the left position. The count of a DynamicPosition is its number of occurrences in the current
  // history prefix, which equals the FIDE repetition count (identical positions only recur within a no-progress
  // window). Returns the new count for the entered position; callers that only need the side effect ignore it.
  private int incrementRepetitionCount(DynamicPosition dynamicPosition) {
    return repetitionCounts.merge(dynamicPosition, 1, Integer::sum);
  }

  private void decrementRepetitionCount(DynamicPosition dynamicPosition) {
    if (!repetitionCounts.containsKey(dynamicPosition)) {
      throw new ProgrammingMistakeException("missing repetition count for position being removed");
    }
    final Integer oldCount = Nulls.get(repetitionCounts, dynamicPosition);
    if (oldCount.intValue() == 1) {
      repetitionCounts.remove(dynamicPosition);
    } else {
      repetitionCounts.put(dynamicPosition, oldCount - 1);
    }
  }

  // Recomputes a position's legal moves from its DynamicPosition. Legal moves are derived cache, not retained
  // per historical position; getLegalMoves() serves the current position and unmove() restores it here. The
  // DynamicPosition carries the normalized en-passant square, which is legal-move-equivalent to the raw square
  // (normalization only zeroes the target when no legal en-passant capture exists).
  private static List<LegalMove> legalMovesFor(DynamicPosition dynamicPosition) {
    final Side side = dynamicPosition.sideToMove();
    final CastlingRight castlingRight = side == Side.WHITE ? dynamicPosition.castlingRightWhite()
        : dynamicPosition.castlingRightBlack();
    final Square enPassantCaptureTargetSquare = dynamicPosition.enPassantCaptureTargetSquare();
    final long enPassantBit = enPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << enPassantCaptureTargetSquare.ordinal();
    return BitboardLegalMoveFactory.calculateLegalMoves(dynamicPosition.bitboardPosition(), side, castlingRight,
        enPassantBit);
  }

  // Computes the canonical SAN of a candidate legal move without mutating the board. Disambiguation uses the
  // current legal-move set; the check / checkmate suffix is derived from the position after the move - the
  // checkmate test generates the opponent's replies only when the move gives check.
  private String sanForCandidate(LegalMove legalMove) {
    final DynamicPosition current = Nulls.getLast(boardStates).dynamicPosition();
    final Side sideToMove = current.sideToMove();
    final Side opponent = sideToMove.getOppositeSide();
    final BitboardPosition afterPosition = current.bitboardPosition().afterMove(legalMove.moveSpecification(),
        sideToMove);
    final boolean isCheck = afterPosition.isInCheck(opponent);
    final boolean isCheckmate = isCheck && opponentHasNoReply(afterPosition, opponent, current, legalMove);
    final SanTerminalMarker sanTerminalMarker = SanTerminalMarkerUtility.calculate(isCheck, isCheckmate);
    return MoveToSan.toSan(legalMove, currentLegalMoves, sanTerminalMarker);
  }

  // True iff the opponent has no legal reply in the position after {@code legalMove} - with check, that is mate.
  private static boolean opponentHasNoReply(BitboardPosition afterPosition, Side opponent,
      DynamicPosition beforeDynamicPosition, LegalMove legalMove) {
    final CastlingRightBoth afterCastlingRightBoth = CastlingUtility.calculateCastlingRightBoth(
        beforeDynamicPosition.castlingRightWhite(), beforeDynamicPosition.castlingRightBlack(), legalMove);
    final CastlingRight afterCastlingRightOpponent = CastlingUtility.getCastlingRight(afterCastlingRightBoth, opponent);
    final Square afterEnPassantCaptureTargetSquare = EnPassantCaptureUtility
        .calculateEnPassantCaptureTargetSquare(legalMove);
    final long afterEnPassantBit = afterEnPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << afterEnPassantCaptureTargetSquare.ordinal();
    return BitboardLegalMoveFactory
        .calculateLegalMoves(afterPosition, opponent, afterCastlingRightOpponent, afterEnPassantBit).isEmpty();
  }

  /**
   * Undoes the most recently played move, restoring the board to the state immediately before that move. Throws if no
   * move has been played from the initial FEN.
   */
  public void unmove() {
    if (isFirstMove()) {
      throw new ProgrammingMistakeException("Undo move requested but no move to undo");
    }

    decrementRepetitionCount(Nulls.getLast(boardStates).dynamicPosition());
    this.boardStates.remove(boardStates.size() - 1);
    this.currentLegalMoves = legalMovesFor(Nulls.getLast(boardStates).dynamicPosition());
  }

  public LegalMove getLastMove() {
    if (isFirstMove()) {
      throw new IllegalArgumentException("There is no last move");
    }
    return moveAt(boardStates.size() - 1);
  }

  public List<LegalMove> getLegalMoves() {
    return currentLegalMoves;
  }

  /**
   * The moves played so far, in order, as {@link MoveSpecification}s. Builds and returns a fresh immutable list on each
   * call - the history is held as one {@code BoardState} record list, not a standing per-accessor list - so fetch it
   * once and reuse the result rather than calling it repeatedly; for the count use {@link #getPerformedMoveCount()}
   * (O(1)).
   */
  public List<MoveSpecification> getPerformedMoveSpecifications() {
    final List<MoveSpecification> moveSpecifications = new ArrayList<>();
    for (int i = 1; i < boardStates.size(); i++) {
      moveSpecifications.add(moveAt(i).moveSpecification());
    }
    return Nulls.copyOfList(moveSpecifications);
  }

  private boolean calculateIsCapture() {
    if (isFirstMove()) {
      throw new IllegalStateException("There is no last move");
    }
    final LegalMove lastMove = getLastMove();
    return lastMove.capturedPiece() != Piece.NONE;
  }

  public boolean isCheck() {
    return Nulls.getLast(boardStates).isCheck();
  }

  /** True iff the side to move is in check and has no legal move (FIDE 5.1.1). */
  public boolean isCheckmate() {
    return Nulls.getLast(boardStates).isCheckmate();
  }

  /** True iff the side to move is not in check but has no legal move (FIDE 5.2.1). */
  public boolean isStalemate() {
    return Nulls.getLast(boardStates).isStalemate();
  }

  /**
   * Claim-ahead for FIDE 9.3: at halfmove clock &gt;= 99, the claim is available if at least one legal move would
   * complete the 50 non-progress moves - i.e. is neither a pawn move nor a capture. FIDE 9.3 frames the claim as
   * announced before the move is played; the 50 moves are about history; the outcome of the candidate move (whether it
   * would deliver mate, stalemate, or continue the game) does not affect whether the no-progress condition has been
   * met.
   *
   * <p>
   * <em>Deliberate divergence from python-chess at one corner case.</em> python-chess's {@code can_claim_fifty_moves}
   * pushes the candidate move and re-checks {@code is_fifty_moves} on the post-position; that reuse means the
   * {@code any(legal_moves)} guard inside {@code is_fifty_moves} (which is deliberately there for the precedence stack
   * when checking the <em>current</em> position) transitively rejects candidate moves that themselves deliver mate or
   * stalemate. The maintainer's tests and docstrings document the deliberate intent for the current-position case
   * (commit {@code 1064bf59}, with tests pinning "once checkmated, it is too late to claim" and "a stalemate is a
   * draw"); they do not address the candidate-move-is-mate case, which falls out of code reuse rather than separate
   * consideration. ashlar-chess takes the strict FIDE 9.3 reading at this edge; the practical impact is zero (the
   * player would play the mate rather than claim) but the predicate is honest about what FIDE actually says.
   */
  public boolean canClaimFiftyMoveRuleWithOwnMove() {
    if (getHalfMoveClock() < 99) {
      return false;
    }
    for (final LegalMove legalMove : getLegalMoves()) {
      if (!legalMove.resetsHalfMoveClock()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Per-move FIDE 9.3 claim predicate: returns {@code true} iff {@code move} would, if announced as the next move,
   * complete the 50 non-progress moves and is therefore a valid 50-move claim under FIDE 9.3. The conditions are: the
   * move is legal on the current position, it is neither a pawn move nor a capture (so the halfmove clock would not
   * reset), and the current halfmove clock is at least 99 (so playing {@code move} would push it to at least 100).
   *
   * <p>
   * Per-move shape rather than the existence shape ({@link #canClaimFiftyMoveRuleWithOwnMove}) because FIDE 9.3 frames
   * the claim as a per-move act - the player announces the specific move they intend to play and claims the draw on
   * that announcement. The existence predicate answers "could any move satisfy the claim from here?", which is a
   * convenience derived from this one. python-chess also collapses to the existence shape ({@code
   * can_claim_fifty_moves()} takes no move parameter); the per-move predicate is the FIDE-faithful API that neither
   * library exposed historically. See the upstream python-chess issue filed during 15.0.0 work for the cross-library
   * context: <a href="https://github.com/niklasf/python-chess/issues/1188">niklasf/python-chess#1188</a>.
   *
   * <p>
   * The move's chess effect - whether it would deliver checkmate, stalemate, or continue the game - does not affect
   * whether the no-progress condition has been met. A non-pawn, non-capture mate-in-one at clock 99 is a valid claim
   * under FIDE 9.3. (In practice the player would play the mate; the predicate is honest about what the rule says.)
   */
  public boolean canClaimFiftyMoveRuleFor(MoveSpecification move) {
    final LegalMove legalMove = requireLegalMove(move);
    if (getHalfMoveClock() < 99) {
      return false;
    }
    return !legalMove.resetsHalfMoveClock();
  }

  /**
   * SAN convenience overload of {@link #canClaimFiftyMoveRuleFor(MoveSpecification)}: parses {@code san} via the
   * lenient SAN pipeline against the current position and delegates. Throws on invalid input -
   * {@link LenientSanParserValidationException} when {@code san} is unparseable / ambiguous / illegal under the lenient
   * pipeline, and {@link IllegalArgumentException} (from the {@link MoveSpecification} overload) when the parsed move
   * is not in the current legal-moves set.
   */
  public boolean canClaimFiftyMoveRuleFor(String san) throws LenientSanParserValidationException {
    return canClaimFiftyMoveRuleFor(LenientSanParser.parse(san, this).moveSpecification());
  }

  /**
   * Per-move FIDE 9.2 claim predicate: returns {@code true} iff {@code move} is legal on the current position AND
   * playing it would produce a position that has occurred at least three times in the game (counting the new
   * occurrence). The player announces {@code move} and claims the draw on that announcement; the move is not played.
   *
   * <p>
   * Clock-resetting candidates (pawn moves and captures) are rejected without simulation - they produce a position that
   * cannot have appeared before in the game, so they cannot satisfy the threefold condition. This matches the existing
   * {@link #canClaimThreefoldRepetitionRuleWithOwnMove} short-circuit.
   *
   * <p>
   * Per-move shape rather than the existence shape because FIDE 9.2 frames the claim as a per-move act. See the
   * {@link #canClaimFiftyMoveRuleFor} JavaDoc for the cross-library context with python-chess.
   */
  public boolean canClaimThreefoldRepetitionRuleFor(MoveSpecification move) {
    final LegalMove legalMove = requireLegalMove(move);
    if (legalMove.resetsHalfMoveClock()) {
      return false;
    }
    this.move(move);
    final boolean threefold = isThreefoldRepetition();
    this.unmove();
    return threefold;
  }

  /**
   * SAN convenience overload of {@link #canClaimThreefoldRepetitionRuleFor(MoveSpecification)}: parses {@code san} via
   * the lenient SAN pipeline against the current position and delegates. Throws on invalid input -
   * {@link LenientSanParserValidationException} when {@code san} is unparseable / ambiguous / illegal under the lenient
   * pipeline, and {@link IllegalArgumentException} (from the {@link MoveSpecification} overload) when the parsed move
   * is not in the current legal-moves set.
   */
  public boolean canClaimThreefoldRepetitionRuleFor(String san) throws LenientSanParserValidationException {
    return canClaimThreefoldRepetitionRuleFor(LenientSanParser.parse(san, this).moveSpecification());
  }

  public boolean canClaimThreefoldRepetitionRuleWithOwnMove() {
    for (final LegalMove legalMove : getLegalMoves()) {
      // we must not check moves creating a position that never occurred so far
      if (!legalMove.resetsHalfMoveClock()) {
        this.move(legalMove.moveSpecification());
        if (isThreefoldRepetition()) {
          this.unmove();
          return true;
        }
        this.unmove();
      }
    }
    return false;
  }

  /**
   * Returns the {@link LegalMove} matching {@code move} in the current legal-moves set, throwing
   * {@link IllegalArgumentException} if no match exists. Used by the per-move claim predicates to make "move not legal
   * here" a loud, immediate failure rather than a silent {@code false}.
   */
  private LegalMove requireLegalMove(MoveSpecification move) {
    for (final LegalMove legalMove : getLegalMoves()) {
      if (legalMove.moveSpecification().equals(move)) {
        return legalMove;
      }
    }
    throw new IllegalArgumentException("move " + move + " is not a legal move in the current position");
  }

  /**
   * Returns the side-to-move's FIDE 9.3 (50-move) claim rights at the current position: one {@link ClaimableMove} per
   * legal move that, if announced before being played, would entitle the announcer to claim a draw under the 50-move
   * rule (halfmove clock would reach 100; move is neither a pawn move nor a capture).
   *
   * <p>
   * Each candidate move is admitted via the per-move predicate {@link #canClaimFiftyMoveRuleFor(MoveSpecification)} -
   * the single source of truth - so any future tightening of FIDE 9.3 semantics flows through automatically. Move order
   * in the returned list matches {@link #getLegalMoves()} order. The board state is unchanged after the call.
   */
  public ClaimRights fiftyMoveRuleClaimRights() {
    return calculateClaimRights(/* threefoldRather */ false);
  }

  /**
   * Returns the side-to-move's FIDE 9.2 (threefold repetition) claim rights at the current position: one
   * {@link ClaimableMove} per legal move that, if announced before being played, would produce a position with at least
   * three occurrences (including the announced-but-not-yet-played one).
   *
   * <p>
   * Each candidate move is admitted via the per-move predicate
   * {@link #canClaimThreefoldRepetitionRuleFor(MoveSpecification)} - the single source of truth. Move order matches
   * {@link #getLegalMoves()} order. The board state is unchanged after the call.
   */
  public ClaimRights threefoldRepetitionRuleClaimRights() {
    return calculateClaimRights(/* threefoldRather */ true);
  }

  /**
   * Shared body for the two claim-rights calculations: iterates the current legal-moves list, applies the per-rule
   * predicate, and for accepted moves captures the canonical SAN via a transient {@code move}/{@code unmove} pair. The
   * SAN of the just-pushed move is read from {@link #getSan()} on the pushed board, then the push is reverted, so the
   * board is in the same state when the method returns as when it was called.
   */
  private ClaimRights calculateClaimRights(boolean threefoldRather) {
    final List<ClaimableMove> claimable = new ArrayList<>();
    for (final LegalMove legalMove : getLegalMoves()) {
      final MoveSpecification spec = legalMove.moveSpecification();
      final boolean accepted = threefoldRather ? canClaimThreefoldRepetitionRuleFor(spec)
          : canClaimFiftyMoveRuleFor(spec);
      if (!accepted) {
        continue;
      }
      // SAN of the candidate is computed directly from the current position - no transient push/pop.
      claimable.add(new ClaimableMove(spec, sanForCandidate(legalMove)));
    }
    return new ClaimRights(!claimable.isEmpty(), Nulls.copyOfList(claimable));
  }

  public int getHalfMoveClock() {
    return Nulls.getLast(boardStates).halfMoveClock();
  }

  private static int calculateNewHalfMoveClock(LegalMove move, int lastHalfMoveClock) {
    if (move.resetsHalfMoveClock()) {
      return 0;
    }
    return lastHalfMoveClock + 1;
  }

  public int getRepetitionCount() {
    if (!repetitionCounts.containsKey(getDynamicPosition())) {
      throw new ProgrammingMistakeException("missing repetition count for current position");
    }
    final Integer count = Nulls.get(repetitionCounts, getDynamicPosition());
    return count;
  }

  public boolean isInsufficientMaterial() {
    return isInsufficientMaterial(Side.WHITE) && isInsufficientMaterial(Side.BLACK);
  }

  public boolean isInsufficientMaterial(Side side) {
    return InsufficientMaterialUtility.isInsufficientMaterial(side, getBitboardPosition());
  }

  public String getFen() {
    if (isFirstMove()) {
      return initialFen.fen();
    }
    return FenBoard.toFen(this);
  }

  public Fen getInitialFen() {
    return initialFen;
  }

  public Piece getMovingPiece() {
    if (isFirstMove()) {
      throw new IllegalStateException("There is no last move");
    }
    return getLastMove().movingPiece();
  }

  public boolean isCapture() {
    if (isFirstMove()) {
      throw new IllegalStateException("There is no last move");
    }
    return calculateIsCapture();
  }

  int getInitialFenFullMoveNumber() {
    return initialFen.fullMoveNumber();
  }

  public int getLastPlayedFullMoveNumber() {
    if (isFirstMove()) {
      throw new IllegalStateException("There is no last move");
    }
    final int fullMoveNumber = fullMoveNumberFor(isFirstMove(), initialFen.fullMoveNumber(), initialFen.sideToMove(),
        getSideToMove(), getPerformedMoveCount());

    return switch (getSideToMove()) {
      case WHITE -> fullMoveNumber - 1;
      case BLACK -> fullMoveNumber;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static int fullMoveNumberFor(boolean isFirstMove, int initialFenFullMoveNumber, Side initialFenSideToMove,
      Side sideToMove, int performedMoveCount) {
    if (isFirstMove) {
      return initialFenFullMoveNumber;
    }

    return switch (sideToMove) {
      case WHITE -> switch (initialFenSideToMove) {
        case BLACK -> {
          // must be even
          checkIsEven(performedMoveCount + 1);
          yield (performedMoveCount + 1) / 2 + initialFenFullMoveNumber;
        }
        case WHITE -> {
          // must be even
          checkIsEven(performedMoveCount);
          yield performedMoveCount / 2 + initialFenFullMoveNumber;
        }
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case BLACK -> switch (initialFenSideToMove) {
        case BLACK -> {
          // must be even
          checkIsEven(performedMoveCount);
          yield performedMoveCount / 2 + initialFenFullMoveNumber;
        }
        case WHITE -> {
          // must be even
          checkIsEven(performedMoveCount - 1);
          yield (performedMoveCount - 1) / 2 + initialFenFullMoveNumber;
        }
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      };
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  /**
   * Current-position outcome query: returns the most-specific {@link Outcome} for this board, or
   * {@link Outcome#ONGOING} (the singleton with {@code termination == Termination.NONE}) when no termination condition
   * fires. Never returns {@code null}.
   *
   * <p>
   * Precedence (python-chess parity): {@link Termination#CHECKMATE} -&gt; {@link Termination#INSUFFICIENT_MATERIAL}
   * -&gt; {@link Termination#STALEMATE} -&gt; {@link Termination#SEVENTY_FIVE_MOVES} -&gt;
   * {@link Termination#FIVEFOLD_REPETITION}. Returns the first matching condition under that order. The library is
   * permissive at the move pipeline - none of these block further moves; callers poll this method to decide whether a
   * game should be adjudicated as over.
   *
   * <p>
   * Does <em>not</em> invoke any unwinnability analyzer. Callers that want the analyzer-driven dead-position verdict
   * call {@link #deadPositionQuick()} or {@link #deadPositionFull()} directly. Single-side insufficient-material states
   * are diagnostic, not terminations, and are not surfaced here; callers query {@link #isInsufficientMaterial(Side)}
   * directly.
   */
  public Outcome outcome() {
    if (isCheckmate()) {
      // Side to move is the loser; the other side delivered mate and is the winner.
      return new Outcome(Termination.CHECKMATE, getSideToMove().getOppositeSide());
    }
    if (isInsufficientMaterial()) {
      return new Outcome(Termination.INSUFFICIENT_MATERIAL, Side.NONE);
    }
    if (isStalemate()) {
      return new Outcome(Termination.STALEMATE, Side.NONE);
    }
    if (isSeventyFiveMove()) {
      return new Outcome(Termination.SEVENTY_FIVE_MOVES, Side.NONE);
    }
    if (isFivefoldRepetition()) {
      return new Outcome(Termination.FIVEFOLD_REPETITION, Side.NONE);
    }
    return Outcome.ONGOING;
  }

  /**
   * Raw condition predicate (FIDE 9.3 threshold): returns {@code true} iff the halfmove clock has reached the 50-move-
   * rule threshold ({@code halfMoveClock >= 100}) on the current position. Reports the fact independently of any other
   * game-end condition that may also hold - at a checkmate position with clock past 100, this still returns
   * {@code true}. Game-end precedence belongs to {@link #outcome()} and not to this predicate. (Deliberate divergence
   * from python-chess at game-end positions, where {@code is_fifty_moves} folds in a precedence guard.)
   */
  public boolean isFiftyMove() {
    return getHalfMoveClock() >= ChessConstants.FIFTY_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
  }

  /**
   * True iff the current position has occurred at least three times in the game (FIDE 9.2). This is the on-board
   * predicate (claimable rule); the game continues until claimed.
   */
  public boolean isThreefoldRepetition() {
    return getRepetitionCount() >= ChessConstants.THREEFOLD_REPETITION_RULE_THRESHOLD;
  }

  /**
   * Raw condition predicate (FIDE 9.6.2 threshold): returns {@code true} iff the halfmove clock has reached the 75-
   * move-rule threshold ({@code halfMoveClock >= 150}) on the current position. Reports the fact independently of any
   * other game-end condition - at a checkmate position with clock past 150, this still returns {@code true}. Game-end
   * precedence belongs to {@link #outcome()} and not to this predicate. (Deliberate divergence from python-chess at
   * game-end positions, where {@code is_seventyfive_moves} folds in a precedence guard.)
   */
  public boolean isSeventyFiveMove() {
    return getHalfMoveClock() >= ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
  }

  /**
   * True iff the current position has occurred at least five times in the game (FIDE 9.6.1). In this library the
   * fivefold-repetition rule is surfaced as a queryable predicate rather than an enforced termination: the move
   * pipeline does NOT reject moves on this condition. Consumers that want to surface the rule call this predicate
   * themselves.
   */
  public boolean isFivefoldRepetition() {
    return getRepetitionCount() >= ChessConstants.FIVEFOLD_REPETITION_RULE_THRESHOLD;
  }

  /**
   * The moves played so far, in order, as canonical SAN strings. Builds and returns a fresh immutable list on each call
   * - the history is held as one {@code BoardState} record list, not a standing per-accessor list - so fetch it once
   * and reuse the result rather than calling it repeatedly; for the count use {@link #getPerformedMoveCount()} (O(1)).
   */
  public List<String> getPerformedMovesAsSan() {
    final List<String> result = new ArrayList<>();
    for (int i = 1; i < boardStates.size(); i++) {
      result.add(sanAt(i));
    }
    return Nulls.copyOfList(result);
  }

  public String getSan() {
    if (isFirstMove()) {
      throw new IllegalStateException("There is no last move");
    }
    return sanAt(boardStates.size() - 1);
  }

  public String getLan() {
    if (isFirstMove()) {
      throw new IllegalStateException("There is no last move");
    }
    return lanAt(boardStates.size() - 1);
  }

  public Side getSideToMove() {
    if (isFirstMove()) {
      return initialFen.sideToMove();
    }
    final LegalMove lastMove = getLastMove();
    return lastMove.movingSide().getOppositeSide();
  }

  /**
   * Current position as a {@link BitboardPosition}. Carried directly on every {@link DynamicPosition} in the last
   * {@link #boardStates} entry (appended on every {@link #move}, popped on every {@link #unmove}). O(1) per call. The
   * bitboard is the single source of truth for piece placement on the board; the StaticPosition reference layer lives
   * in {@code src/test/} as the permanent differential-test oracle.
   */
  public BitboardPosition getBitboardPosition() {
    return Nulls.getLast(boardStates).dynamicPosition().bitboardPosition();
  }

  BitboardPosition getBitboardPositionBeforeLastMove() {
    if (isFirstMove()) {
      throw new ProgrammingMistakeException("The method cannot be called if no move was yet made");
    }
    return Nulls.get(boardStates, boardStates.size() - 2).dynamicPosition().bitboardPosition();
  }

  public boolean isEnPassantCapturePossible() {
    return Nulls.getLast(boardStates).dynamicPosition().enPassantCaptureTargetSquare() != Square.NONE;
  }

  private static boolean calculateIsEnPassantCapturePossible(Square enPassantCaptureTargetSquare, Side sideToMove,
      BitboardPosition bitboardPosition) {
    if (enPassantCaptureTargetSquare == Square.NONE) {
      return false;
    }
    // two potential capture moves
    if (!enPassantCaptureTargetSquare.hasBehindSquare(sideToMove)) {
      // cannot be for en en passant target square
      throw new ProgrammingMistakeException();
    }
    final Square squareBehind = enPassantCaptureTargetSquare.getBehindSquare(sideToMove);
    final Piece ownPawn = Piece.of(sideToMove, PieceType.PAWN);

    // capture move from right square
    if (squareBehind.hasRightSquare(sideToMove)) {
      final Square squareRight = squareBehind.getRightSquare(sideToMove);
      if (bitboardPosition.get(squareRight) == ownPawn) {
        final MoveSpecification moveSpecification = new MoveSpecification(squareRight, enPassantCaptureTargetSquare);
        if (!bitboardPosition.afterMove(moveSpecification, sideToMove).isInCheck(sideToMove)) {
          return true;
        }
      }
    }

    // capture move from left square
    if (squareBehind.hasLeftSquare(sideToMove)) {
      final Square squareLeft = squareBehind.getLeftSquare(sideToMove);
      if (bitboardPosition.get(squareLeft) == ownPawn) {
        final MoveSpecification moveSpecification = new MoveSpecification(squareLeft, enPassantCaptureTargetSquare);
        if (!bitboardPosition.afterMove(moveSpecification, sideToMove).isInCheck(sideToMove)) {
          return true;
        }
      }
    }
    return false;
  }

  public int getPerformedMoveCount() {
    return boardStates.size() - 1;
  }

  List<DynamicPosition> getDynamicPositions() {
    final List<DynamicPosition> result = new ArrayList<>();
    for (final BoardState state : boardStates) {
      result.add(state.dynamicPosition());
    }
    return Nulls.copyOfList(result);
  }

  public DynamicPosition getInitialDynamicPosition() {
    return Nulls.getFirst(boardStates).dynamicPosition();
  }

  public DynamicPosition getDynamicPosition() {
    return Nulls.getLast(boardStates).dynamicPosition();
  }

  public List<MoveSpecification> getLegalMoveSpecifications() {
    final List<MoveSpecification> result = new ArrayList<>();
    for (final LegalMove legalMove : this.getLegalMoves()) {
      result.add(legalMove.moveSpecification());
    }
    return Nulls.copyOfList(result);
  }

  @Override
  public String toString() {
    return getFen();
  }

  public Square getEnPassantCaptureTargetSquare() {
    if (isFirstMove()) {
      return initialFen.enPassantCaptureTargetSquare();
    }
    return EnPassantCaptureUtility.calculateEnPassantCaptureTargetSquare(getLastMove());
  }

  private static void checkIsEven(int intValue) {
    final int valueFloor = intValue / 2;
    final int valueRounded = (int) StrictMath.round(intValue / 2.0);
    if (valueFloor != valueRounded) {
      throw new ProgrammingMistakeException("The programmer overlooked something");
    }
  }

  /**
   * The moves played so far, in order, as {@link LegalMove} records. Builds and returns a fresh immutable list on each
   * call - the history is held as one {@code BoardState} record list, not a standing per-accessor list - so fetch it
   * once and reuse the result rather than calling it repeatedly; for the count use {@link #getPerformedMoveCount()}
   * (O(1)).
   */
  public List<LegalMove> getPerformedMoves() {
    final List<LegalMove> result = new ArrayList<>();
    for (int i = 1; i < boardStates.size(); i++) {
      result.add(moveAt(i));
    }
    return Nulls.copyOfList(result);
  }

  @Override
  public int hashCode() {
    // Deliberate design decision: hash an O(1) SUMMARY of the game (initial FEN + number of moves played + current
    // position), NOT the full move history. The contract only requires that equal games hash equally, and two games
    // that are equals() necessarily share the same initial FEN, the same move count, and the same current dynamic
    // position - so this is correct. We deliberately do NOT hash the whole boardStates: that is O(history) per call
    // (it grew quadratic under per-ply hashing in the burn-in survey) for no real benefit, since Board is a mutable
    // game object and the class javadoc already directs callers not to use it as a HashMap/HashSet key. The current
    // position is highly discriminating, so distribution is good; only different games of equal length reaching the
    // identical current position (rare transpositions) collide, and equals() resolves those. Do not "promote" this
    // back to hashing the full history.
    return Objects.hash(initialFen, boardStates.size(), getDynamicPosition());
  }

  /**
   * Game equality: two boards are equal when they share the same initial FEN and the same full per-position history
   * (the move played, the derived check / checkmate / stalemate flags, the dynamic position, the halfmove clock, and
   * the castling-right-loss reasons at every position passed through). This is <em>game</em> identity, not
   * <em>position</em> identity - two boards that reach the same current position by different move orders are
   * <strong>not</strong> equal.
   *
   * <p>
   * To compare positions rather than games, compare {@link #getDynamicPosition()} instead: it is the exact,
   * collision-free value record for the current position (side to move, piece placement, castling rights, and
   * en-passant target). Because {@code Board} is mutable, its equality changes as moves are played - see the
   * class-level thread-safety note before using a {@code Board} as a {@link java.util.Map} key or {@link java.util.Set}
   * element.
   */
  @Override
  public boolean equals(@Nullable Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Board other = (Board) obj;
    return Objects.equals(initialFen, other.initialFen) && Objects.equals(boardStates, other.boardStates);
  }

  public CastlingRightLoss getWhiteKingSideLoss() {
    return Nulls.getLast(boardStates).whiteKingSideLoss();
  }

  public CastlingRightLoss getWhiteQueenSideLoss() {
    return Nulls.getLast(boardStates).whiteQueenSideLoss();
  }

  public CastlingRightLoss getBlackKingSideLoss() {
    return Nulls.getLast(boardStates).blackKingSideLoss();
  }

  public CastlingRightLoss getBlackQueenSideLoss() {
    return Nulls.getLast(boardStates).blackQueenSideLoss();
  }

  public CastlingRightLoss getCastlingRightLoss(Side side, CastlingMove castlingSide) {
    return switch (side) {
      case WHITE -> castlingSide == CastlingMove.KING_SIDE ? getWhiteKingSideLoss() : getWhiteQueenSideLoss();
      case BLACK -> castlingSide == CastlingMove.KING_SIDE ? getBlackKingSideLoss() : getBlackQueenSideLoss();
      case NONE -> throw new IllegalArgumentException();
    };
  }

  public CastlingRight getCastlingRightWhite() {
    return getDynamicPosition().castlingRightWhite();
  }

  public CastlingRight getCastlingRightBlack() {
    return getDynamicPosition().castlingRightBlack();
  }

  public int getFullMoveNumber() {
    return fullMoveNumberFor(isFirstMove(), initialFen.fullMoveNumber(), initialFen.sideToMove(), getSideToMove(),
        getPerformedMoveCount());
  }

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

  /**
   * Per-move composed convenience: returns {@code true} iff {@code move}, when announced as the next move under FIDE
   * 9.2 or 9.3, would entitle the announcer to claim a draw. Equivalent to
   * {@code canClaimFiftyMoveRuleFor(move) || canClaimThreefoldRepetitionRuleFor(move)}.
   */
  public boolean canClaimDrawFor(MoveSpecification move) {
    return canClaimFiftyMoveRuleFor(move) || canClaimThreefoldRepetitionRuleFor(move);
  }

  public UnwinnabilityQuickVerdict unwinnableQuick(Side side) {
    return UnwinnableQuickAnalyzer.unwinnableQuick(this, side).verdict();
  }

  public UnwinnabilityFullVerdict unwinnableFull(Side side) {
    return UnwinnableFullAnalyzer.unwinnableFull(this, side).verdict();
  }

  /**
   * Quick whole-position dead check (FIDE 5.2.2): {@link DeadPositionQuickVerdict#DEAD} when neither side can deliver
   * checkmate by any sequence of legal moves, {@link DeadPositionQuickVerdict#POSSIBLY_ALIVE} otherwise. The cheap,
   * during-the-game counterpart to {@link #deadPositionFull()}.
   */
  public DeadPositionQuickVerdict deadPositionQuick() {
    return DeadPositionAnalyzer.deadPositionQuick(this);
  }

  /**
   * Complete whole-position dead check (FIDE 5.2.2): {@link DeadPositionFullVerdict#DEAD}, {@code ALIVE}, or
   * {@code UNDETERMINED}. The complete check suggested at game end (resignation or flag-fall); during the game prefer
   * the cheaper {@link #deadPositionQuick()}.
   */
  public DeadPositionFullVerdict deadPositionFull() {
    return DeadPositionAnalyzer.deadPositionFull(this);
  }

  public CastlingRight getCastlingRight(Side sideToMove) {
    return switch (sideToMove) {
      case WHITE -> getDynamicPosition().castlingRightWhite();
      case BLACK -> getDynamicPosition().castlingRightBlack();
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  public List<String> getLegalMovesAsSan() {
    final List<String> result = new ArrayList<>();
    for (final LegalMove legalMove : getLegalMoves()) {
      result.add(sanForCandidate(legalMove));
    }
    return Nulls.copyOfList(result);
  }

  public List<String> getLegalMovesAsUci() {
    final List<String> result = new ArrayList<>();
    final Side sideToMove = getSideToMove();
    for (final MoveSpecification moveSpecification : getLegalMoveSpecifications()) {
      final String uci = UciMoveUtility.toUci(sideToMove, moveSpecification).uci();
      result.add(uci);
    }
    return Nulls.copyOfList(result);
  }

}
