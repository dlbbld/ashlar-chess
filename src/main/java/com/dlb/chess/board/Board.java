package com.dlb.chess.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.bitboard.BitboardLegalMoveFactory;
import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.enums.CastlingMove;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.CastlingRightLoss;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ChessConstants;
import com.dlb.chess.common.constants.DynamicPositionConstants;
import com.dlb.chess.common.enums.InsufficientMaterial;
import com.dlb.chess.common.enums.Termination;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.ClaimRights;
import com.dlb.chess.common.model.ClaimableMove;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.GameEndFacts;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.model.Outcome;
import com.dlb.chess.common.ucimove.utility.UciMoveUtility;
import com.dlb.chess.common.utility.BasicChessUtility;
import com.dlb.chess.common.utility.RepetitionUtility;
import com.dlb.chess.exceptions.InvalidMoveException;
import com.dlb.chess.fen.FenBoard;
import com.dlb.chess.fen.FenParserAdvanced;
import com.dlb.chess.fen.LenientFenParser;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.CastlingRightBoth;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.moves.CastlingUtility;
import com.dlb.chess.moves.EnPassantCaptureUtility;
import com.dlb.chess.san.LenientSanParser;
import com.dlb.chess.san.LenientSanParserValidationException;
import com.dlb.chess.san.LenientSanParserValidationResult;
import com.dlb.chess.san.MoveToLan;
import com.dlb.chess.san.MoveToSan;
import com.dlb.chess.san.SanTerminalMarker;
import com.dlb.chess.san.StrictSanParser;
import com.dlb.chess.san.StrictSanParserValidationResult;
import com.dlb.chess.unwinnability.DeadPositionFull;
import com.dlb.chess.unwinnability.DeadPositionQuick;
import com.dlb.chess.unwinnability.UnwinnabilityFullVerdict;
import com.dlb.chess.unwinnability.UnwinnabilityQuickVerdict;
import com.dlb.chess.unwinnability.UnwinnableFullAnalyzer;
import com.dlb.chess.unwinnability.UnwinnableQuickAnalyzer;
import com.google.common.collect.ImmutableList;

/**
 * The library's central type - a chess <em>game</em>, not merely a position. A {@code Board} carries the position
 * <strong>plus</strong> the move history from its initial FEN: every halfmove ever performed, the legal-move set after
 * each, the halfmove clock, repetition counts, castling-right loss reasons, derived SAN/LAN strings - everything needed
 * to answer rule-level questions about the game so far.
 *
 * <h2>Construction</h2>
 *
 * <p>
 * Three constructors:
 *
 * <ul>
 * <li>{@link #Board()} - start at the initial position.</li>
 * <li>{@link #Board(String)} - start at the position given by a FEN string. Validated by the advanced FEN parser; see
 * the {@code com.dlb.chess.fen} package documentation for the validation contract.</li>
 * <li>{@link #Board(Fen)} - start at a pre-parsed {@link Fen} value.</li>
 * </ul>
 *
 * <h2>Mutating the game</h2>
 *
 * <p>
 * Move execution happens through {@link #moveStrict(String)}, {@link #moveLenient(String)},
 * {@link #move(MoveSpecification)}, {@link #movesStrict(String...)}, and is undone by {@link #unmove()}. Both
 * move-pipelines validate the candidate against the current legal-move set; an invalid move throws (see
 * {@link com.dlb.chess.exceptions.InvalidMoveException} from the {@code MoveSpecification} pipeline,
 * {@code SanValidationException} from the SAN pipeline). Checkmate, stalemate, and mutual insufficient material block
 * further moves. Fivefold repetition, the 75-move rule, and analyzer-driven dead positions are queryable states; the
 * move pipeline does <em>not</em> reject moves on those conditions. The package-level Javadoc on
 * {@link com.dlb.chess.board} documents the strict-game invariant in detail.
 *
 * <h2>Querying the game</h2>
 *
 * <p>
 * Beyond move execution, {@code Board} exposes the standard rule-level predicates: {@link #isCheckmate()},
 * {@link #isStalemate()}, {@link #isThreefoldRepetition()}, {@link #isFiftyMove()}, {@link #isFivefoldRepetition()},
 * {@link #isSeventyFiveMove()}, plus the unwinnability/dead-position pair ({@code isUnwinnableQuick},
 * {@code isUnwinnableFull}, {@code isDeadPositionQuick}, {@code isDeadPositionFull} - the library's flagship CHA
 * feature; see {@link com.dlb.chess.unwinnability}). Position-state accessors return Guava
 * {@code ImmutableList}/{@code ImmutableSet}; mutation is exclusively via {@code move}/{@code unmove}.
 *
 * <p>
 * For game-level reports (threefold-claim-ahead, repetition listings, no-progress sequences), use
 * {@link com.dlb.chess.report.Reporter}.
 *
 * <h2>Thread-safety</h2>
 *
 * <p>
 * {@code Board} is mutable and <strong>not thread-safe</strong>. Use one {@code Board} per thread, or synchronize
 * externally. {@link #equals(Object)} and {@link #hashCode()} reflect the current game state, so a {@code Board} placed
 * in a {@link java.util.HashMap} or {@link java.util.HashSet} and then mutated will violate the collection's invariants
 * - don't do that.
 */
public class Board {

  private final Fen initialFen;
  private final List<LegalMove> performedLegalMoveList;
  private final List<ImmutableList<LegalMove>> legalMoveListPerPly;
  private final List<Boolean> isCheckList;
  private final List<Boolean> isCheckmateList;
  private final List<Boolean> isStalemateList;
  private final List<DynamicPosition> dynamicPositionList;
  private final List<Integer> halfMoveClockList;
  private final List<Integer> repetitionCountList;
  private final List<String> sanList;
  private final List<String> lanList;
  // halfMoveList intentionally NOT stored: HalfMove rows are derived on demand by getHalfMoveList()
  // from the other per-ply parallel stores (performedLegalMoveList + sanList + halfMoveClockList +
  // dynamicPositionList + repetitionCountList + initial-FEN fullmove anchor). Dropping the stored
  // field eliminates one duplicated mutable list and removes HalfMove from Board's state model.
  private final List<CastlingRightLoss> whiteKingSideLossList;
  private final List<CastlingRightLoss> whiteQueenSideLossList;
  private final List<CastlingRightLoss> blackKingSideLossList;
  private final List<CastlingRightLoss> blackQueenSideLossList;

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
    final Side initialHavingMove = initialFenUse.havingMove();
    final CastlingRight initialCastlingRight = CastlingUtility.getCastlingRight(initialFenUse, initialHavingMove);
    final Square initialEnPassantCaptureTargetSquare = initialFenUse.enPassantCaptureTargetSquare();

    this.initialFen = initialFenUse;

    final BitboardPosition initialBitboardPosition = initialFenUse.bitboardPosition();
    final long initialEnPassantBit = initialEnPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << initialEnPassantCaptureTargetSquare.ordinal();

    // Normalize: keep the target square on DynamicPosition only when an opposing pawn can actually capture there.
    // The raw FEN-spec square is preserved on Board (see getEnPassantCaptureTargetSquare()) for FEN export.
    final Square initialNormalizedEnPassantCaptureTargetSquare = calculateIsEnPassantCapturePossible(
        initialEnPassantCaptureTargetSquare, initialHavingMove, initialBitboardPosition)
            ? initialEnPassantCaptureTargetSquare
            : Square.NONE;

    this.performedLegalMoveList = new ArrayList<>();
    this.legalMoveListPerPly = new ArrayList<>();
    final ImmutableList<LegalMove> legalMoves = BitboardLegalMoveFactory.calculateLegalMoves(initialBitboardPosition,
        initialHavingMove, initialCastlingRight, initialEnPassantBit);
    this.legalMoveListPerPly.add(legalMoves);

    this.isCheckList = new ArrayList<>();
    final boolean isCheck = initialBitboardPosition.isInCheck(initialHavingMove);
    this.isCheckList.add(isCheck);

    this.isCheckmateList = new ArrayList<>();
    final boolean isCheckmate = isCheck && legalMoves.isEmpty();
    this.isCheckmateList.add(isCheckmate);

    this.isStalemateList = new ArrayList<>();
    final boolean isStalemate = !isCheck && legalMoves.isEmpty();
    this.isStalemateList.add(isStalemate);

    this.dynamicPositionList = new ArrayList<>();
    // attention - must be after we calculated the legal moves - we need them to check if en passant capture is possible
    // order of instructions dependency!!
    final CastlingRight initialCastlingRightWhite = CastlingUtility.getCastlingRight(initialFenUse, Side.WHITE);
    final CastlingRight initialCastlingRightBlack = CastlingUtility.getCastlingRight(initialFenUse, Side.BLACK);
    if (initialFenUse.equals(FenConstants.FEN_INITIAL)) {
      this.dynamicPositionList.add(DynamicPositionConstants.INITIAL);
    } else {
      this.dynamicPositionList.add(new DynamicPosition(initialHavingMove, initialBitboardPosition,
          initialNormalizedEnPassantCaptureTargetSquare, initialCastlingRightWhite, initialCastlingRightBlack));
    }
    this.halfMoveClockList = new ArrayList<>();
    this.halfMoveClockList.add(initialFenUse.halfMoveClock());

    this.repetitionCountList = new ArrayList<>();
    this.repetitionCountList.add(1);

    this.sanList = new ArrayList<>();
    // halfMoveList intentionally not initialized - derived on demand from the parallel stores.
    this.lanList = new ArrayList<>();

    this.whiteKingSideLossList = new ArrayList<>();
    this.whiteQueenSideLossList = new ArrayList<>();
    this.blackKingSideLossList = new ArrayList<>();
    this.blackQueenSideLossList = new ArrayList<>();
    this.whiteKingSideLossList.add(initialCastlingRightWhite == CastlingRight.KING_AND_QUEEN_SIDE
        || initialCastlingRightWhite == CastlingRight.KING_SIDE ? CastlingRightLoss.NOT_LOST
            : CastlingRightLoss.UNKNOWN_FEN_IMPORT);
    this.whiteQueenSideLossList.add(initialCastlingRightWhite == CastlingRight.KING_AND_QUEEN_SIDE
        || initialCastlingRightWhite == CastlingRight.QUEEN_SIDE ? CastlingRightLoss.NOT_LOST
            : CastlingRightLoss.UNKNOWN_FEN_IMPORT);
    this.blackKingSideLossList.add(initialCastlingRightBlack == CastlingRight.KING_AND_QUEEN_SIDE
        || initialCastlingRightBlack == CastlingRight.KING_SIDE ? CastlingRightLoss.NOT_LOST
            : CastlingRightLoss.UNKNOWN_FEN_IMPORT);
    this.blackQueenSideLossList.add(initialCastlingRightBlack == CastlingRight.KING_AND_QUEEN_SIDE
        || initialCastlingRightBlack == CastlingRight.QUEEN_SIDE ? CastlingRightLoss.NOT_LOST
            : CastlingRightLoss.UNKNOWN_FEN_IMPORT);
  }

  /**
   * Constructs a {@code Board} at the standard initial position.
   */
  public Board() {
    this(FenConstants.FEN_INITIAL);
  }

  /**
   * Constructs a {@code Board} from a FEN string, validated by the advanced FEN parser. Enforces structural and
   * rule-consistency checks (piece counts within physical bounds, no pawns on rank 1 or 8, castling rights consistent
   * with king/rook static positions, en-passant target consistent with the side to move, halfmove clock consistent with
   * the fullmove number, etc.). The halfmove clock itself is not capped - the FIDE 75-move rule is a queryable
   * predicate on {@code Board}, not enforced at FEN import. Does not prove full game reachability - see the
   * {@code com.dlb.chess.fen} package documentation for the full contract.
   */
  public Board(String fen) {
    this(FenParserAdvanced.parseFenAdvanced(fen));
  }

  /**
   * Creates a new board whose initial position is this board's current position, without carrying over the move
   * history.
   *
   */
  public Board copyCurrentPositionWithoutHistory() {
    final Fen currentPosition = new Fen(getFen(), getBitboardPosition(), getHavingMove(), getCastlingRightWhite(),
        getCastlingRightBlack(), getEnPassantCaptureTargetSquare(), 0, getFullMoveNumber());
    return new Board(currentPosition);
  }

  /**
   * Constructs a {@code Board} from a FEN string via {@link LenientFenParser}. The lenient layer applies a
   * syntactic-tolerance pass (whitespace, casing, missing halfmove/fullmove counters, non-canonical castling order,
   * non-ASCII dashes, trailing garbage) before delegating to {@link FenParserAdvanced}. Strict semantic invariants are
   * unchanged: a FEN with a missing king, a pawn on rank 1, an impossible double-check, or castling rights that
   * contradict the piece placement still fails. Callers who need to see the list of tolerated deviations should invoke
   * {@link LenientFenParser#validateText(String)} directly.
   *
   * @throws com.dlb.chess.fen.LenientFenParserValidationException when the input cannot be recovered or fails the
   *                                                               strict semantic checks
   */
  public static Board fromFenLenient(String fen) {
    return new Board(LenientFenParser.parseText(fen));
  }

  public boolean isFirstMove() {
    return this.performedLegalMoveList.isEmpty();
  }

  /**
   * Plays the given move on this board. The {@code MoveSpecification} is validated against the current legal-move set;
   * an illegal move (or a move on a game already terminated) throws {@link InvalidMoveException}.
   */
  public boolean move(MoveSpecification moveSpecification) throws InvalidMoveException {
    ValidateNewMove.validateNewMove(this, moveSpecification);
    return performMoveWithoutValidation(moveSpecification);
  }

  /**
   * Plays the given move on this board, specified in canonical SAN. The result carries the resolved
   * {@link MoveSpecification}; for callers that only need success / fail, the absence of a thrown exception is the
   * answer. Use {@link #moveLenient(String)} when parsing real-world PGN that may contain forgivable deviations.
   *
   * @throws com.dlb.chess.san.SanValidationException if {@code san} is not canonical SAN, or is canonical but does not
   *                                                  represent a legal move
   */
  public StrictSanParserValidationResult moveStrict(String san) {
    final StrictSanParserValidationResult result = StrictSanParser.parseText(san, this);
    this.performMoveWithoutValidation(result.moveSpecification());
    if (!san.equals(this.getSan())) {
      throw new ProgrammingMistakeException("The provided SAN and generated SAN are different, this should not happen");
    }
    return result;
  }

  /**
   * Plays the given move on this board, specified in lenient SAN. Accepts inputs the strict pipeline rejects when those
   * inputs uniquely identify a legal move and the deviation matches a supported tolerance category (case variation,
   * long-algebraic / UCI form, castling with digit zero, missing or wrong check / checkmate suffix, over-specification,
   * missing or spurious capture marker, missing promotion equals, explicit pawn letter). The returned
   * {@link LenientSanParserValidationResult} carries the resolved {@code MoveSpecification} together with one
   * {@code ForgivenItem} per deviation that was forgiven; on canonical input the forgiven-items list is empty.
   *
   * @throws com.dlb.chess.san.LenientSanParserValidationException if the input cannot be resolved to a legal move even
   *                                                               after applying every supported tolerance
   */
  public LenientSanParserValidationResult moveLenient(String san) {
    final LenientSanParserValidationResult result = LenientSanParser.parseText(san, this);
    this.performMoveWithoutValidation(result.moveSpecification());
    return result;
  }

  /**
   * Plays the given sequence of canonical SAN moves on this board, in order. Convenience for batch play; the absence of
   * a thrown exception means every move was canonical and legal.
   */
  public boolean movesStrict(String... sanArray) {
    for (final String san : sanArray) {
      if (san == null) {
        throw new IllegalArgumentException("The SAN cannot be null");
      }
      moveStrict(san);
    }
    return true;
  }

  /**
   * Plays the given sequence of canonical SAN moves on this board, in order. Convenience for batch play; the absence of
   * a thrown exception means every move was canonical and legal.
   */
  public boolean movesLenient(String... sanArray) {
    for (final String san : sanArray) {
      if (san == null) {
        throw new IllegalArgumentException("The SAN cannot be null");
      }
      moveLenient(san);
    }
    return true;
  }

  private boolean performMoveWithoutValidation(MoveSpecification moveSpecification) throws InvalidMoveException {

    final CastlingRight beforeCastlingRightWhite = Nulls.getLast(dynamicPositionList).castlingRightWhite();
    final CastlingRight beforeCastlingRightBlack = Nulls.getLast(dynamicPositionList).castlingRightBlack();

    final Side havingMove = this.getHavingMove();
    final BitboardPosition beforeBitboardPosition = Nulls.getLast(dynamicPositionList).bitboardPosition();
    final LegalMove moveToPerform = BitboardLegalMoveFactory.toLegalMove(beforeBitboardPosition, moveSpecification,
        havingMove);

    final Side afterHavingMove = havingMove.getOppositeSide();
    final CastlingRightBoth afterCastlingRightBoth = CastlingUtility
        .calculateCastlingRightBoth(beforeCastlingRightWhite, beforeCastlingRightBlack, moveToPerform);
    final CastlingRight afterCastlingRightHavingMove = CastlingUtility.getCastlingRight(afterCastlingRightBoth,
        afterHavingMove);
    final Square afterEnPassantCaptureTargetSquare = EnPassantCaptureUtility
        .calculateEnPassantCaptureTargetSquare(moveToPerform);

    final BitboardPosition afterBitboardPosition = beforeBitboardPosition.afterMove(moveSpecification, havingMove);

    // Normalize for DynamicPosition; see initial-position construction site for the rationale.
    final Square afterNormalizedEnPassantCaptureTargetSquare = calculateIsEnPassantCapturePossible(
        afterEnPassantCaptureTargetSquare, afterHavingMove, afterBitboardPosition) ? afterEnPassantCaptureTargetSquare
            : Square.NONE;

    // update castling loss reasons
    this.whiteKingSideLossList.add(CastlingUtility.calculateCastlingRightLoss(moveToPerform,
        Nulls.getLast(whiteKingSideLossList), Side.WHITE, CastlingMove.KING_SIDE));
    this.whiteQueenSideLossList.add(CastlingUtility.calculateCastlingRightLoss(moveToPerform,
        Nulls.getLast(whiteQueenSideLossList), Side.WHITE, CastlingMove.QUEEN_SIDE));
    this.blackKingSideLossList.add(CastlingUtility.calculateCastlingRightLoss(moveToPerform,
        Nulls.getLast(blackKingSideLossList), Side.BLACK, CastlingMove.KING_SIDE));
    this.blackQueenSideLossList.add(CastlingUtility.calculateCastlingRightLoss(moveToPerform,
        Nulls.getLast(blackQueenSideLossList), Side.BLACK, CastlingMove.QUEEN_SIDE));

    // now changing board class state, so performing the move!
    this.performedLegalMoveList.add(moveToPerform);

    final long afterEnPassantBit = afterEnPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << afterEnPassantCaptureTargetSquare.ordinal();

    // now we have a depencency on instruction execution: the move must be performed before calling the legal moves
    final ImmutableList<LegalMove> legalMovesAfterMove = BitboardLegalMoveFactory
        .calculateLegalMoves(afterBitboardPosition, afterHavingMove, afterCastlingRightHavingMove, afterEnPassantBit);
    this.legalMoveListPerPly.add(legalMovesAfterMove);

    final boolean isCheck = afterBitboardPosition.isInCheck(afterHavingMove);
    this.isCheckList.add(isCheck);

    final boolean isCheckmate = isCheck && legalMovesAfterMove.isEmpty();
    this.isCheckmateList.add(isCheckmate);

    final boolean isStalemate = !isCheck && legalMovesAfterMove.isEmpty();
    this.isStalemateList.add(isStalemate);

    final DynamicPosition newDynamicPosition = new DynamicPosition(afterHavingMove, afterBitboardPosition,
        afterNormalizedEnPassantCaptureTargetSquare, afterCastlingRightBoth.castlingRightWhite(),
        afterCastlingRightBoth.castlingRightBlack());
    this.dynamicPositionList.add(newDynamicPosition);

    // order of instructions dependency!! - must be after adding the move
    final int lastHalfMoveClock = Nulls.getLast(halfMoveClockList);
    this.halfMoveClockList.add(calculateNewHalfMoveClock(lastHalfMoveClock));

    // timely dependency - dynamic position list must be updated
    final int newRepetitionCount = RepetitionUtility.calculateCountRepetition(performedLegalMoveList,
        dynamicPositionList, newDynamicPosition);
    this.repetitionCountList.add(newRepetitionCount);

    final ImmutableList<LegalMove> legalMovesBeforeLastHalfMove = Nulls.get(legalMoveListPerPly,
        legalMoveListPerPly.size() - 2);

    final SanTerminalMarker sanTerminalMarker = SanTerminalMarker.calculate(isCheck, isCheckmate);

    this.sanList.add(MoveToSan.calculateSanLastMove(moveToPerform, legalMovesBeforeLastHalfMove, sanTerminalMarker));
    this.lanList.add(MoveToLan.calculateLanLastMove(moveToPerform, sanTerminalMarker));

    return true;

  }

  /**
   * Undoes the most recently played halfmove, restoring the board to the state immediately before that move. Throws if
   * no move has been played from the initial FEN.
   */
  public void unmove() {
    if (isFirstMove()) {
      throw new ProgrammingMistakeException("Undo move requested but no move to undo");
    }

    this.performedLegalMoveList.remove(performedLegalMoveList.size() - 1);
    this.legalMoveListPerPly.remove(legalMoveListPerPly.size() - 1);

    this.isCheckList.remove(isCheckList.size() - 1);
    this.isCheckmateList.remove(isCheckmateList.size() - 1);
    this.isStalemateList.remove(isStalemateList.size() - 1);

    this.dynamicPositionList.remove(dynamicPositionList.size() - 1);
    this.halfMoveClockList.remove(halfMoveClockList.size() - 1);
    this.repetitionCountList.remove(repetitionCountList.size() - 1);

    this.sanList.remove(sanList.size() - 1);
    this.lanList.remove(lanList.size() - 1);

    this.whiteKingSideLossList.remove(whiteKingSideLossList.size() - 1);
    this.whiteQueenSideLossList.remove(whiteQueenSideLossList.size() - 1);
    this.blackKingSideLossList.remove(blackKingSideLossList.size() - 1);
    this.blackQueenSideLossList.remove(blackQueenSideLossList.size() - 1);

  }

  public LegalMove getLastMove() {
    if (isFirstMove()) {
      throw new IllegalArgumentException("There is no last move");
    }
    return Nulls.getLast(this.performedLegalMoveList);
  }

  public ImmutableList<LegalMove> getLegalMoves() {
    return Nulls.getLast(legalMoveListPerPly);
  }

  public ImmutableList<MoveSpecification> getPerformedMoveSpecificationList() {
    final List<MoveSpecification> moveSpecificationList = new ArrayList<>();
    for (final LegalMove legalMove : this.performedLegalMoveList) {
      moveSpecificationList.add(legalMove.moveSpecification());
    }
    return Nulls.copyOfList(moveSpecificationList);
  }

  private boolean calculateIsCapture() {
    if (isFirstMove()) {
      throw new IllegalStateException("There is no last move");
    }
    final LegalMove lastMove = getLastMove();
    return lastMove.pieceCaptured() != Piece.NONE;
  }

  public boolean isCheck() {
    return Nulls.getLast(isCheckList);
  }

  /** True iff the side to move is in check and has no legal move (FIDE 5.1.1). */
  public boolean isCheckmate() {
    return Nulls.getLast(isCheckmateList);
  }

  /** True iff the side to move is not in check but has no legal move (FIDE 5.2.1). */
  public boolean isStalemate() {
    return Nulls.getLast(isStalemateList);
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
   * consideration. clean-chess takes the strict FIDE 9.3 reading at this edge; the practical impact is zero (the player
   * would play the mate rather than claim) but the predicate is honest about what FIDE actually says.
   */
  public boolean canClaimFiftyMoveRuleWithOwnMove() {
    if (getHalfMoveClock() < 99) {
      return false;
    }
    for (final LegalMove legalMove : getLegalMoves()) {
      if (!BasicChessUtility.calculateIsResetHalfMoveClock(legalMove)) {
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
    return !BasicChessUtility.calculateIsResetHalfMoveClock(legalMove);
  }

  /**
   * SAN convenience overload of {@link #canClaimFiftyMoveRuleFor(MoveSpecification)}: parses {@code san} via the
   * lenient SAN pipeline against the current position and delegates. Throws on invalid input -
   * {@link LenientSanParserValidationException} when {@code san} is unparseable / ambiguous / illegal under the lenient
   * pipeline, and {@link IllegalArgumentException} (from the {@link MoveSpecification} overload) when the parsed move
   * is not in the current legal-moves set.
   */
  public boolean canClaimFiftyMoveRuleFor(String san) throws LenientSanParserValidationException {
    return canClaimFiftyMoveRuleFor(LenientSanParser.parseText(san, this).moveSpecification());
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
    if (BasicChessUtility.calculateIsResetHalfMoveClock(legalMove)) {
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
    return canClaimThreefoldRepetitionRuleFor(LenientSanParser.parseText(san, this).moveSpecification());
  }

  public boolean canClaimThreefoldRepetitionRuleWithOwnMove() {
    for (final LegalMove legalMove : getLegalMoves()) {
      // we must not check moves creating a position that never occurred so far
      if (!BasicChessUtility.calculateIsResetHalfMoveClock(legalMove)) {
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
  public ClaimRights calculateFiftyMoveRuleClaimRights() {
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
  public ClaimRights calculateThreefoldRepetitionRuleClaimRights() {
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
      // Capture canonical SAN of the candidate via transient push. Symmetric in shape with the
      // claim-ahead report builders. For threefold the predicate also pushed-and-popped internally;
      // this is a second push purely to read the resulting SAN.
      this.move(spec);
      final String san = getSan();
      this.unmove();
      claimable.add(new ClaimableMove(spec, san));
    }
    return new ClaimRights(!claimable.isEmpty(), claimable);
  }

  public int getHalfMoveClock() {
    return Nulls.getLast(halfMoveClockList);
  }

  private int calculateNewHalfMoveClock(int lastHalfMoveClock) {
    final LegalMove legalMove = getLastMove();
    if (BasicChessUtility.calculateIsResetHalfMoveClock(legalMove)) {
      return 0;
    }
    return lastHalfMoveClock + 1;
  }

  public int getRepetitionCount() {
    return Nulls.getLast(repetitionCountList);
  }

  public boolean isInsufficientMaterial() {
    return isInsufficientMaterial(Side.WHITE) && isInsufficientMaterial(Side.BLACK);
  }

  public boolean isInsufficientMaterial(Side side) {
    return InsufficientMaterialUtility.calculateIsInsufficientMaterial(side, getBitboardPosition());
  }

  public String getFen() {
    if (isFirstMove()) {
      return initialFen.fen();
    }
    return FenBoard.calculateFen(this);
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
    final int fullMoveNumber = calculateFullMoveNumber(isFirstMove(), initialFen.fullMoveNumber(),
        initialFen.havingMove(), getHavingMove(), getPerformedHalfMoveCount());

    return switch (getHavingMove()) {
      case WHITE -> fullMoveNumber - 1;
      case BLACK -> fullMoveNumber;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static int calculateFullMoveNumber(boolean isFirstMove, int initialFenFullMoveNumber,
      Side initialFenHavingMove, Side havingMove, int halfMoveCount) {
    if (isFirstMove) {
      return initialFenFullMoveNumber;
    }

    return switch (havingMove) {
      case WHITE -> switch (initialFenHavingMove) {
        case BLACK -> {
          // must be even
          checkIsEven(halfMoveCount + 1);
          yield (halfMoveCount + 1) / 2 + initialFenFullMoveNumber;
        }
        case WHITE -> {
          // must be even
          checkIsEven(halfMoveCount);
          yield halfMoveCount / 2 + initialFenFullMoveNumber;
        }
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      }; // must be even // must be even
      case BLACK -> switch (initialFenHavingMove) {
        case BLACK -> {
          // must be even
          checkIsEven(halfMoveCount);
          yield halfMoveCount / 2 + initialFenFullMoveNumber;
        }
        case WHITE -> {
          // must be even
          checkIsEven(halfMoveCount - 1);
          yield (halfMoveCount - 1) / 2 + initialFenFullMoveNumber;
        }
        case NONE -> throw new IllegalArgumentException();
        default -> throw new IllegalArgumentException();
      }; // must be even // must be even
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  /**
   * Raw condition predicate (FIDE 9.3 threshold): returns {@code true} iff the halfmove clock has reached the 50-move-
   * rule threshold ({@code halfMoveClock >= 100}) on the current position. Reports the fact independently of any other
   * game-end condition that may also hold - at a checkmate position with clock past 100, this still returns
   * {@code true}. Game-end precedence belongs to
   * {@link com.dlb.chess.common.utility.BasicChessUtility#calculateOutcome} and not to this predicate. (Deliberate
   * divergence from python-chess at game-end positions, where {@code is_fifty_moves} folds in a precedence guard.)
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
   * precedence belongs to {@link com.dlb.chess.common.utility.BasicChessUtility#calculateOutcome} and not to this
   * predicate. (Deliberate divergence from python-chess at game-end positions, where {@code is_seventyfive_moves} folds
   * in a precedence guard.)
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
   * Rich snapshot of all game-end-relevant facts on the current position together with the precedence-projected
   * {@link Outcome}. The fact booleans are independent and condition-only - each is the raw truth of its rule on the
   * current board, not suppressed by any higher-precedence condition that may also hold. See {@link GameEndFacts} for
   * the field-by-field semantics and the precedence rules used to project the {@code outcome} field.
   *
   * <p>
   * Invokes the unwinnability quick analyzer to compute {@code deadPosition}; the cost is microseconds. Callers that do
   * not need the analyzer-driven dead-position fact can call the individual condition predicates directly.
   */
  public GameEndFacts calculateGameEndFacts() {
    final boolean checkmate = isCheckmate();
    final boolean stalemate = isStalemate();
    final boolean insufficientMaterial = isInsufficientMaterial();
    final boolean deadPosition = isDeadPosition();
    final boolean fivefoldRepetition = isFivefoldRepetition();
    final boolean seventyFiveMove = isSeventyFiveMove();
    final Outcome outcome = BasicChessUtility.calculateOutcome(this);
    return new GameEndFacts(checkmate, stalemate, insufficientMaterial, deadPosition, fivefoldRepetition,
        seventyFiveMove, outcome);
  }

  /**
   * Convenience: {@code true} iff a termination condition fires on the current position (i.e. the projected
   * {@link Outcome}'s termination is not {@link com.dlb.chess.common.enums.Termination#NONE}). Equivalent to
   * {@code BasicChessUtility.calculateOutcome(this).termination() != Termination.NONE}.
   */
  public boolean isGameEnd() {
    return BasicChessUtility.calculateOutcome(this).termination() != Termination.NONE;
  }

  public ImmutableList<String> getSanList() {
    return Nulls.copyOfList(sanList);
  }

  public String getSan() {
    if (isFirstMove()) {
      throw new IllegalStateException("There is no last move");
    }
    return Nulls.getLast(sanList);
  }

  public String getLan() {
    if (isFirstMove()) {
      throw new IllegalStateException("There is no last move");
    }
    return Nulls.getLast(lanList);
  }

  public Side getHavingMove() {
    if (isFirstMove()) {
      return initialFen.havingMove();
    }
    final LegalMove lastMove = getLastMove();
    return lastMove.havingMove().getOppositeSide();
  }

  /**
   * Current position as a {@link BitboardPosition}. Carried directly on every {@link DynamicPosition} in
   * {@link #dynamicPositionList} (appended on every {@link #move}, popped on every {@link #unmove}). O(1) per call. The
   * bitboard is the single source of truth for piece placement on the board; the StaticPosition reference layer lives
   * in {@code src/test/} as the permanent differential-test oracle.
   */
  public BitboardPosition getBitboardPosition() {
    return Nulls.getLast(dynamicPositionList).bitboardPosition();
  }

  BitboardPosition getBitboardPositionBeforeLastMove() {
    if (isFirstMove()) {
      throw new ProgrammingMistakeException("The method cannot be called if no move was yet made");
    }
    return Nulls.get(dynamicPositionList, this.dynamicPositionList.size() - 2).bitboardPosition();
  }

  public boolean isEnPassantCapturePossible() {
    return Nulls.getLast(dynamicPositionList).enPassantCaptureTargetSquare() != Square.NONE;
  }

  private static boolean calculateIsEnPassantCapturePossible(Square enPassantCaptureTargetSquare, Side havingMove,
      BitboardPosition bitboardPosition) {
    if (enPassantCaptureTargetSquare == Square.NONE) {
      return false;
    }
    // two potential capture moves
    if (!Square.calculateHasBehindSquare(havingMove, enPassantCaptureTargetSquare)) {
      // cannot be for en en passant target square
      throw new ProgrammingMistakeException();
    }
    final Square squareBehind = Square.calculateBehindSquare(havingMove, enPassantCaptureTargetSquare);
    final Piece ownPawn = Piece.calculate(havingMove, PieceType.PAWN);

    // capture move from right square
    if (Square.calculateHasRightSquare(havingMove, squareBehind)) {
      final Square squareRight = Square.calculateRightSquare(havingMove, squareBehind);
      if (bitboardPosition.get(squareRight) == ownPawn) {
        final MoveSpecification moveSpecification = new MoveSpecification(squareRight, enPassantCaptureTargetSquare);
        if (!bitboardPosition.afterMove(moveSpecification, havingMove).isInCheck(havingMove)) {
          return true;
        }
      }
    }

    // capture move from left square
    if (Square.calculateHasLeftSquare(havingMove, squareBehind)) {
      final Square squareLeft = Square.calculateLeftSquare(havingMove, squareBehind);
      if (bitboardPosition.get(squareLeft) == ownPawn) {
        final MoveSpecification moveSpecification = new MoveSpecification(squareLeft, enPassantCaptureTargetSquare);
        if (!bitboardPosition.afterMove(moveSpecification, havingMove).isInCheck(havingMove)) {
          return true;
        }
      }
    }
    return false;
  }

  public int getPerformedHalfMoveCount() {
    return performedLegalMoveList.size();
  }

  ImmutableList<DynamicPosition> getDynamicPositionList() {
    return Nulls.copyOfList(dynamicPositionList);
  }

  /**
   * Derived/compatibility view: reconstructs the played-move history as a {@link HalfMove} list from the per-ply
   * parallel stores (performedLegalMoveList + sanList + halfMoveClockList + dynamicPositionList + repetitionCountList +
   * the initial-FEN fullmove anchor). Board no longer maintains the list as state - each call builds a fresh list, so
   * the operation is {@code O(plies)} per call rather than {@code O(1)}.
   *
   * <p>
   * Kept as part of the public API in this release so consumers in {@code com.dlb.chess.report} and downstream code are
   * not forced to migrate in one step. A future release may remove this method entirely as part of finishing the
   * {@link HalfMove} decommission; callers that hold a long-lived reference should cache the returned list rather than
   * calling this method per access.
   */
  public ImmutableList<HalfMove> getHalfMoveList() {
    final int plies = performedLegalMoveList.size();
    if (plies == 0) {
      return Nulls.listOf();
    }
    final List<HalfMove> result = new ArrayList<>(plies);
    for (int i = 0; i < plies; i++) {
      result.add(buildHalfMoveAtPly(i));
    }
    return Nulls.copyOfList(result);
  }

  /**
   * Derived {@link HalfMove} for the most recently played ply - {@code O(1)} reconstruction from the per-ply parallel
   * stores. Use this instead of {@code Nulls.getLast(getHalfMoveList())} when only the last entry is needed, otherwise
   * the full {@code O(plies)} reconstruction runs for every call. Throws {@link IllegalStateException} when no move has
   * been played, matching {@link #getLastMove}.
   */
  public HalfMove getLastHalfMove() {
    if (isFirstMove()) {
      throw new IllegalStateException("There is no last half-move");
    }
    return buildHalfMoveAtPly(performedLegalMoveList.size() - 1);
  }

  public DynamicPosition getInitialDynamicPosition() {
    return Nulls.getFirst(dynamicPositionList);
  }

  public DynamicPosition getDynamicPosition() {
    return Nulls.getLast(dynamicPositionList);
  }

  public ImmutableList<MoveSpecification> getPossibleMoveSpecificationList() {
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

  public ImmutableList<LegalMove> getPerformedLegalMoveList() {
    return Nulls.copyOfList(performedLegalMoveList);
  }

  @Override
  public int hashCode() {
    // halfMoveList intentionally absent - it's now a derived view of the other per-ply lists, so it
    // carries no information not already covered by sanList + dynamicPositionList +
    // halfMoveClockList + repetitionCountList + performedLegalMoveList + initialFen.
    return Objects.hash(dynamicPositionList, halfMoveClockList, initialFen, isCheckList, isCheckmateList,
        isStalemateList, lanList, legalMoveListPerPly, performedLegalMoveList, repetitionCountList, sanList);
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Board other = (Board) obj;
    return Objects.equals(dynamicPositionList, other.dynamicPositionList)
        && Objects.equals(halfMoveClockList, other.halfMoveClockList) && Objects.equals(initialFen, other.initialFen)
        && Objects.equals(isCheckList, other.isCheckList) && Objects.equals(isCheckmateList, other.isCheckmateList)
        && Objects.equals(isStalemateList, other.isStalemateList) && Objects.equals(lanList, other.lanList)
        && Objects.equals(legalMoveListPerPly, other.legalMoveListPerPly)
        && Objects.equals(performedLegalMoveList, other.performedLegalMoveList)
        && Objects.equals(repetitionCountList, other.repetitionCountList) && Objects.equals(sanList, other.sanList);
  }

  public CastlingRightLoss getWhiteKingSideLoss() {
    return Nulls.getLast(whiteKingSideLossList);
  }

  public CastlingRightLoss getWhiteQueenSideLoss() {
    return Nulls.getLast(whiteQueenSideLossList);
  }

  public CastlingRightLoss getBlackKingSideLoss() {
    return Nulls.getLast(blackKingSideLossList);
  }

  public CastlingRightLoss getBlackQueenSideLoss() {
    return Nulls.getLast(blackQueenSideLossList);
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
    return calculateFullMoveNumber(isFirstMove(), initialFen.fullMoveNumber(), initialFen.havingMove(), getHavingMove(),
        getPerformedHalfMoveCount());
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

  public InsufficientMaterial calculateInsufficientMaterial() {
    if (isInsufficientMaterial()) {
      return InsufficientMaterial.BOTH;
    }
    if (isInsufficientMaterial(Side.WHITE)) {
      return InsufficientMaterial.WHITE_ONLY;
    }
    if (isInsufficientMaterial(Side.BLACK)) {
      return InsufficientMaterial.BLACK_ONLY;
    }
    return InsufficientMaterial.NONE;
  }

  public DeadPositionQuick isDeadPositionQuick() {
    final UnwinnabilityQuickVerdict unwinnableWhite = UnwinnableQuickAnalyzer.unwinnableQuick(this, Side.WHITE)
        .verdict();
    final UnwinnabilityQuickVerdict unwinnableBlack = UnwinnableQuickAnalyzer.unwinnableQuick(this, Side.BLACK)
        .verdict();
    if (unwinnableWhite == UnwinnabilityQuickVerdict.UNWINNABLE
        && unwinnableBlack == UnwinnabilityQuickVerdict.UNWINNABLE) {
      return DeadPositionQuick.DEAD_POSITION;
    }
    if (unwinnableWhite == UnwinnabilityQuickVerdict.WINNABLE
        && unwinnableBlack == UnwinnabilityQuickVerdict.WINNABLE) {
      return DeadPositionQuick.NON_DEAD_POSITION;
    }
    return DeadPositionQuick.POSSIBLY_NON_DEAD_POSITION;
  }

  /**
   * FIDE 5.2.2 dead position: either both sides insufficient material (cheap, exact) or both sides UNWINNABLE per the
   * quick analyzer. The cheap predicate short-circuits the expensive one.
   */
  public boolean isDeadPosition() {
    return isInsufficientMaterial() || isDeadPositionQuick() == DeadPositionQuick.DEAD_POSITION;
  }

  public DeadPositionFull isDeadPositionFull() {
    final UnwinnabilityFullVerdict unwinnableWhite = UnwinnableFullAnalyzer.unwinnableFull(this, Side.WHITE).verdict();
    if (unwinnableWhite == UnwinnabilityFullVerdict.WINNABLE) {
      return DeadPositionFull.NON_DEAD_POSITION;
    }
    final UnwinnabilityFullVerdict unwinnableBlack = UnwinnableFullAnalyzer.unwinnableFull(this, Side.BLACK).verdict();
    if (unwinnableBlack == UnwinnabilityFullVerdict.WINNABLE) {
      return DeadPositionFull.NON_DEAD_POSITION;
    }
    if (unwinnableWhite == UnwinnabilityFullVerdict.UNWINNABLE
        && unwinnableBlack == UnwinnabilityFullVerdict.UNWINNABLE) {
      return DeadPositionFull.DEAD_POSITION;
    }
    return DeadPositionFull.UNDETERMINED;
  }

  public UnwinnabilityQuickVerdict isUnwinnableQuick(Side side) {
    return UnwinnableQuickAnalyzer.unwinnableQuick(this, side).verdict();
  }

  public UnwinnabilityFullVerdict isUnwinnableFull(Side side) {
    return UnwinnableFullAnalyzer.unwinnableFull(this, side).verdict();
  }

  public CastlingRight getCastlingRight(Side havingMove) {
    return switch (havingMove) {
      case WHITE -> getDynamicPosition().castlingRightWhite();
      case BLACK -> getDynamicPosition().castlingRightBlack();
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
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

  public ImmutableList<String> getLegalMovesUci() {
    final List<String> result = new ArrayList<>();
    final Side havingMove = getHavingMove();
    for (final MoveSpecification moveSpecification : getPossibleMoveSpecificationList()) {
      final String uci = UciMoveUtility.convertMoveSpecificationToUci(havingMove, moveSpecification).text();
      result.add(uci);
    }
    return Nulls.copyOfList(result);
  }

  /**
   * Reconstructs the {@link HalfMove} that was the result of the move at {@code plyIndex} (0-based) in the played
   * history. Reads every field from the per-ply parallel stores: {@code performedLegalMoveList[plyIndex]} for the move
   * specification and moving piece, {@code sanList[plyIndex]} for the SAN, and the three "+1-indexed" stores
   * ({@code halfMoveClockList}, {@code dynamicPositionList}, {@code repetitionCountList} - each carries the initial-
   * FEN state at index 0 and the after-ply-i state at index i+1) for the clock, position, and repetition count.
   *
   * <p>
   * The full-move number derives from the initial FEN's fullmove + having-move anchors and the played ply index via the
   * same {@link #calculateFullMoveNumber} helper that powers {@link #getLastPlayedFullMoveNumber}, just generalised to
   * take an arbitrary historical {@code havingMove} / {@code halfMoveCount} pair instead of the current one.
   */
  private HalfMove buildHalfMoveAtPly(int plyIndex) {
    final LegalMove legalMove = Nulls.get(performedLegalMoveList, plyIndex);
    final int halfMoveCount = plyIndex + 1;
    final int halfMoveClock = Nulls.get(halfMoveClockList, plyIndex + 1);
    final int countRepetition = Nulls.get(repetitionCountList, plyIndex + 1);
    final DynamicPosition dynamicPosition = Nulls.get(dynamicPositionList, plyIndex + 1);
    final String san = Nulls.get(sanList, plyIndex);
    final Piece movingPiece = legalMove.movingPiece();
    final Side havingMoveAfter = movingPiece.getSide().getOppositeSide();
    // Reuse the existing private fullmove helper; it returns the fullmove number for the side
    // currently to move, so subtract 1 when that side is WHITE to recover the just-played move's
    // fullmove number (matches getLastPlayedFullMoveNumber's adjustment).
    final int fullMoveNumberOfNext = calculateFullMoveNumber(false, initialFen.fullMoveNumber(),
        initialFen.havingMove(), havingMoveAfter, halfMoveCount);
    final int fullMoveNumber = switch (havingMoveAfter) {
      case WHITE -> fullMoveNumberOfNext - 1;
      case BLACK -> fullMoveNumberOfNext;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
    return new HalfMove(halfMoveCount, fullMoveNumber, halfMoveClock, dynamicPosition, countRepetition, san,
        movingPiece, legalMove.moveSpecification());
  }

}
