package com.dlb.chess.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.analyze.ChessRuleAnalyzer;
import com.dlb.chess.bitboard.BitboardLegalMoveFactory;
import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.BitboardPositionUtility;
import com.dlb.chess.board.enums.CastlingMove;
import com.dlb.chess.board.enums.CastlingRight;
import com.dlb.chess.board.enums.CastlingRightLoss;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.ChessConstants;
import com.dlb.chess.common.constants.DynamicPositionConstants;
import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.common.enums.InsufficientMaterial;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.common.model.HalfMove;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.ucimove.utility.UciMoveUtility;
import com.dlb.chess.common.utility.BasicChessUtility;
import com.dlb.chess.common.utility.RepetitionUtility;
import com.dlb.chess.common.utility.StaticPositionUtility;
import com.dlb.chess.exceptions.InvalidMoveException;
import com.dlb.chess.fen.FenBoard;
import com.dlb.chess.fen.FenParserAdvanced;
import com.dlb.chess.fen.LenientFenParser;
import com.dlb.chess.fen.constants.FenConstants;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.CastlingRightBoth;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.LegalMoveKind;
import com.dlb.chess.moves.CastlingUtility;
import com.dlb.chess.moves.EnPassantCaptureUtility;
import com.dlb.chess.moves.PromotionUtility;
import com.dlb.chess.san.LenientSanParser;
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
 * The library's central type â€” a chess <em>game</em>, not merely a position. A {@code Board} carries the position
 * <strong>plus</strong> the move history from its initial FEN: every halfmove ever performed, the legal-move set after
 * each, the halfmove clock, repetition counts, castling-right loss reasons, derived SAN/LAN strings â€” everything
 * needed to answer rule-level questions about the game so far.
 *
 * <h2>Construction</h2>
 *
 * <p>
 * Three constructors:
 *
 * <ul>
 * <li>{@link #Board()} â€” start at the initial position.</li>
 * <li>{@link #Board(String)} â€” start at the position given by a FEN string. Validated by the advanced FEN parser; see
 * the {@code com.dlb.chess.fen} package documentation for the validation contract.</li>
 * <li>{@link #Board(Fen)} â€” start at a pre-parsed {@link Fen} value.</li>
 * </ul>
 *
 * <h2>Mutating the game</h2>
 *
 * <p>
 * Move execution happens through {@link #moveStrict(String)}, {@link #moveLenient(String)},
 * {@link #move(MoveSpecification)}, {@link #movesStrict(String...)}, and is undone by {@link #unmove()}. Both
 * move-pipelines validate the candidate against the current legal-move set; an invalid move throws (see
 * {@link com.dlb.chess.exceptions.InvalidMoveException} from the {@code MoveSpecification} pipeline,
 * {@code SanValidationException} from the SAN pipeline). Once the game has reached any FIDE-automatic termination
 * (checkmate, stalemate, mutual insufficient material, fivefold repetition, 75-move rule), neither pipeline accepts
 * further moves â€” the package-level Javadoc on {@link com.dlb.chess.board} documents the strict-game invariant in
 * detail.
 *
 * <h2>Querying the game</h2>
 *
 * <p>
 * Beyond move execution, {@code Board} exposes the standard rule-level predicates: {@link #isCheckmate()},
 * {@link #isStalemate()}, {@link #isThreefoldRepetition()}, {@link #isFiftyMove()}, {@link #isFivefoldRepetition()},
 * {@link #isSeventyFiveMove()}, plus the unwinnability/dead-position pair ({@code isUnwinnableQuick},
 * {@code isUnwinnableFull}, {@code isDeadPositionQuick}, {@code isDeadPositionFull} â€” the library's flagship CHA
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
 * â€” don't do that.
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
  private final List<HalfMove> halfMoveList;
  private final List<CastlingRightLoss> whiteKingSideLossList;
  private final List<CastlingRightLoss> whiteQueenSideLossList;
  private final List<CastlingRightLoss> blackKingSideLossList;
  private final List<CastlingRightLoss> blackQueenSideLossList;

  /**
   * Constructor flag: auto-detect {@link GameStatus#DEAD_POSITION_UNWINNABLE_QUICK} after every move. {@code true} is
   * the FIDE-compliant default. {@code false} skips the expensive analyzer-driven check; mechanical
   * insufficient-material detection is unaffected.
   *
   * <p>
   * {@code transient} so reflective equality treats boards with different detection flags as equal — the flag governs
   * detection behaviour, not position state.
   */
  private final transient boolean isDetectDeadPositionUnwinnable;

  /**
   * Per-ply boolean: did the quick analyzer find both sides UNWINNABLE at this ply? Computed eagerly during
   * {@link #performMoveWithoutValidation} (and the constructor) and read by {@link #isDeadPositionUnwinnableQuick()}.
   * Same list-per-ply pattern as {@link #isCheckmateList}.
   */
  private final List<Boolean> isDeadPositionUnwinnableQuickList;

  /**
   * Constructs a {@code Board} at the position carried by the given pre-parsed {@link Fen}, with dead-position
   * unwinnable-quick detection enabled (FIDE-compliant default — game terminates automatically when both sides reach a
   * position the quick unwinnability analyzer classifies as unwinnable).
   */
  public Board(Fen initialFen) {
    this(initialFen, true);
  }

  /**
   * Constructs a {@code Board} at the position carried by the given pre-parsed {@link Fen}, with explicit control of
   * the {@link GameStatus#DEAD_POSITION_UNWINNABLE_QUICK} auto-detection. Pass {@code false} to skip the expensive
   * per-move analyzer-driven check; mechanical insufficient-material detection (cheap, exact) always runs regardless.
   *
   * @param detectDeadPositionUnwinnable {@code true} for FIDE-compliant behaviour (default); {@code false} for callers
   *                                     that have their own dead-position pipeline or for test corpora that replay
   *                                     large numbers of games and can't afford the per-move analyzer cost
   */
  public Board(Fen initialFen, boolean detectDeadPositionUnwinnable) {

    this.isDetectDeadPositionUnwinnable = detectDeadPositionUnwinnable;
    this.isDeadPositionUnwinnableQuickList = new ArrayList<>();

    // using the static fen in case saves a bit of memory
    Fen initialFenUse;
    if (initialFen.equals(FenConstants.FEN_INITIAL)) {
      initialFenUse = FenConstants.FEN_INITIAL;
    } else {
      initialFenUse = initialFen;
    }

    // values used in the following not to be get from board methods!!!
    final StaticPosition initialStaticPosition = initialFenUse.staticPosition();
    final Side initialHavingMove = initialFenUse.havingMove();
    final CastlingRight initialCastlingRight = CastlingUtility.getCastlingRight(initialFenUse, initialHavingMove);
    final var initialEnPassantCaptureTargetSquare = initialFenUse.enPassantCaptureTargetSquare();
    // Normalize: keep the target square on DynamicPosition only when an opposing pawn can actually capture there.
    // The raw FEN-spec square is preserved on Board (see getEnPassantCaptureTargetSquare()) for FEN export.
    final var initialNormalizedEnPassantCaptureTargetSquare = calculateIsEnPassantCapturePossible(
        initialEnPassantCaptureTargetSquare, initialHavingMove, initialStaticPosition)
            ? initialEnPassantCaptureTargetSquare
            : Square.NONE;

    this.initialFen = initialFenUse;

    final BitboardPosition initialBitboardPosition = BitboardPositionUtility.fromStaticPosition(initialStaticPosition);
    final long initialEnPassantBit = initialEnPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << initialEnPassantCaptureTargetSquare.ordinal();

    this.performedLegalMoveList = new ArrayList<>();
    this.legalMoveListPerPly = new ArrayList<>();
    final ImmutableList<LegalMove> legalMoves = BitboardLegalMoveFactory.calculateLegalMoves(initialBitboardPosition,
        initialStaticPosition, initialHavingMove, initialCastlingRight, initialEnPassantBit);
    this.legalMoveListPerPly.add(legalMoves);

    this.isCheckList = new ArrayList<>();
    final var isCheck = initialBitboardPosition.isInCheck(initialHavingMove);
    this.isCheckList.add(isCheck);

    this.isCheckmateList = new ArrayList<>();
    final var isCheckmate = isCheck && legalMoves.isEmpty();
    this.isCheckmateList.add(isCheckmate);

    this.isStalemateList = new ArrayList<>();
    final var isStalemate = !isCheck && legalMoves.isEmpty();
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
    this.lanList = new ArrayList<>();

    this.halfMoveList = new ArrayList<>();

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

    // Eager initial-position dead-position-unwinnable-quick value. Must be after all other state is initialised
    // since the analyzer reads the board via the regular API.
    this.isDeadPositionUnwinnableQuickList.add(computeDeadPositionUnwinnableQuick());
  }

  /**
   * Constructs a {@code Board} at the standard initial position with dead-position-unwinnable-quick auto-detection
   * enabled (FIDE-compliant default).
   */
  public Board() {
    this(FenConstants.FEN_INITIAL, true);
  }

  /**
   * Constructs a {@code Board} at the standard initial position with explicit control of the
   * {@link GameStatus#DEAD_POSITION_UNWINNABLE_QUICK} auto-detection. See {@link #Board(Fen, boolean)}.
   */
  public Board(boolean detectDeadPositionUnwinnable) {
    this(FenConstants.FEN_INITIAL, detectDeadPositionUnwinnable);
  }

  /**
   * Constructs a {@code Board} from a FEN string, validated by the advanced FEN parser. Enforces structural and
   * rule-consistency checks (piece counts within physical bounds, no pawns on rank 1 or 8, castling rights consistent
   * with king/rook static positions, en-passant target consistent with the side to move, halfmove clock not above the
   * 75-move-rule threshold of 150, etc.). Does not prove full game reachability — see the {@code com.dlb.chess.fen}
   * package documentation for the full contract.
   */
  public Board(String fen) {
    this(FenParserAdvanced.parseFenAdvanced(fen), true);
  }

  /**
   * Constructs a {@code Board} from a FEN string with explicit control of the
   * {@link GameStatus#DEAD_POSITION_UNWINNABLE_QUICK} auto-detection. See {@link #Board(Fen, boolean)} for the meaning
   * of the flag.
   */
  public Board(String fen, boolean detectDeadPositionUnwinnable) {
    this(FenParserAdvanced.parseFenAdvanced(fen), detectDeadPositionUnwinnable);
  }

  /**
   * Creates a new board whose initial position is this board's current position, without carrying over the move
   * history.
   *
   * <p>
   * Auto-detection of {@code DEAD_POSITION_UNWINNABLE_QUICK} follows the {@code detectDeadPositionUnwinnable} flag.
   */
  public Board copyCurrentPositionWithoutHistory(boolean detectDeadPositionUnwinnable) {
    final Fen currentPosition = new Fen(getFen(), getStaticPosition(), getHavingMove(), getCastlingRightWhite(),
        getCastlingRightBlack(), getEnPassantCaptureTargetSquare(), 0, getFullMoveNumberForNextHalfMove());
    return new Board(currentPosition, detectDeadPositionUnwinnable);
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
    final LegalMove moveToPerform = calculateLegalMove(this.getStaticPosition(), havingMove, moveSpecification);

    // values used in the following not to be get from board methods!!!
    final StaticPosition afterStaticPosition = StaticPositionUtility.createPositionAfterMove(this.getStaticPosition(),
        havingMove, moveSpecification);
    final Side afterHavingMove = havingMove.getOppositeSide();
    final CastlingRightBoth afterCastlingRightBoth = CastlingUtility
        .calculateCastlingRightBoth(beforeCastlingRightWhite, beforeCastlingRightBlack, moveToPerform);
    final CastlingRight afterCastlingRightHavingMove = CastlingUtility.getCastlingRight(afterCastlingRightBoth,
        afterHavingMove);
    final var afterEnPassantCaptureTargetSquare = EnPassantCaptureUtility
        .calculateEnPassantCaptureTargetSquare(moveToPerform);
    // Normalize for DynamicPosition; see initial-position construction site for the rationale.
    final var afterNormalizedEnPassantCaptureTargetSquare = calculateIsEnPassantCapturePossible(
        afterEnPassantCaptureTargetSquare, afterHavingMove, afterStaticPosition) ? afterEnPassantCaptureTargetSquare
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

    final BitboardPosition afterBitboardPosition = Nulls.getLast(dynamicPositionList).bitboardPosition()
        .afterMove(moveSpecification, havingMove);
    final long afterEnPassantBit = afterEnPassantCaptureTargetSquare == Square.NONE ? 0L
        : 1L << afterEnPassantCaptureTargetSquare.ordinal();

    // now we have a depencency on instruction execution: the move must be performed before calling the legal moves
    final ImmutableList<LegalMove> legalMovesAfterMove = BitboardLegalMoveFactory.calculateLegalMoves(
        afterBitboardPosition, afterStaticPosition, afterHavingMove, afterCastlingRightHavingMove, afterEnPassantBit);
    this.legalMoveListPerPly.add(legalMovesAfterMove);

    final var isCheck = afterBitboardPosition.isInCheck(afterHavingMove);
    this.isCheckList.add(isCheck);

    final var isCheckmate = isCheck && legalMovesAfterMove.isEmpty();
    this.isCheckmateList.add(isCheckmate);

    final var isStalemate = !isCheck && legalMovesAfterMove.isEmpty();
    this.isStalemateList.add(isStalemate);

    final var newDynamicPosition = new DynamicPosition(afterHavingMove, afterBitboardPosition,
        afterNormalizedEnPassantCaptureTargetSquare, afterCastlingRightBoth.castlingRightWhite(),
        afterCastlingRightBoth.castlingRightBlack());
    this.dynamicPositionList.add(newDynamicPosition);

    // order of instructions dependency!! - must be after adding the move
    final int lastHalfMoveClock = Nulls.getLast(halfMoveClockList);
    this.halfMoveClockList.add(calculateNewHalfMoveClock(lastHalfMoveClock));

    // timely dependency - dynamic position list must be updated
    final var newRepetitionCount = RepetitionUtility.calculateCountRepetition(performedLegalMoveList,
        dynamicPositionList, newDynamicPosition);
    this.repetitionCountList.add(newRepetitionCount);

    final ImmutableList<LegalMove> legalMovesBeforeLastHalfMove = Nulls.get(legalMoveListPerPly,
        legalMoveListPerPly.size() - 2);

    final SanTerminalMarker sanTerminalMarker = SanTerminalMarker.calculate(isCheck, isCheckmate);

    this.sanList.add(MoveToSan.calculateSanLastMove(moveToPerform, legalMovesBeforeLastHalfMove, sanTerminalMarker));
    this.lanList.add(MoveToLan.calculateLanLastMove(moveToPerform, sanTerminalMarker));

    final HalfMove halfMove = buildHalfMove(moveSpecification);
    this.halfMoveList.add(halfMove);

    // Eager per-ply dead-position-unwinnable-quick value (false when detection disabled or recursion-suppressed).
    this.isDeadPositionUnwinnableQuickList.add(computeDeadPositionUnwinnableQuick());

    return true;

  }

  // Package-private â€” a LegalMove can only be safely constructed when the caller has already validated the
  // moveSpecification as legal, and that's an invariant only the rule pipeline can guarantee. If a non-pipeline
  // caller passed an unvalidated MoveSpecification here, the result would silently carry incorrect derived data
  // (wrong moving piece, wrong captured piece, wrong en-passant role).
  private static LegalMove calculateLegalMove(StaticPosition staticPosition, Side havingMove,
      MoveSpecification moveSpecification) {

    if (CastlingUtility.calculateIsCastlingMove(moveSpecification)) {
      final Piece king = Piece.calculateKingPiece(havingMove);
      return new LegalMove(moveSpecification, king, Piece.NONE, LegalMoveKind.CASTLING);
    }

    final Piece movingPiece = staticPosition.get(moveSpecification.fromSquare());

    if (EnPassantCaptureUtility.calculateIsEnPassantCaptureNewMove(staticPosition, moveSpecification)) {
      final Square squareOfCapturedPawnForEnPassantCapture = EnPassantCaptureUtility
          .calculateSquareOfCapturedPawnForEnPassantCapture(havingMove, moveSpecification);
      final Piece pieceCaptured = staticPosition.get(squareOfCapturedPawnForEnPassantCapture);
      return new LegalMove(moveSpecification, movingPiece, pieceCaptured, LegalMoveKind.EN_PASSANT_CAPTURE);
    }
    if (PromotionUtility.calculateIsPromotionNewMove(moveSpecification)) {
      final Piece pieceCaptured = staticPosition.get(moveSpecification.toSquare());
      return new LegalMove(moveSpecification, movingPiece, pieceCaptured, LegalMoveKind.PROMOTION);
    }
    final Piece pieceCaptured = staticPosition.get(moveSpecification.toSquare());
    final var kind = EnPassantCaptureUtility.calculateIsPawnTwoSquareAdvanceMove(movingPiece, moveSpecification)
        ? LegalMoveKind.PAWN_TWO_SQUARE_ADVANCE
        : LegalMoveKind.NORMAL;
    return new LegalMove(moveSpecification, movingPiece, pieceCaptured, kind);
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

    this.halfMoveList.remove(halfMoveList.size() - 1);

    this.whiteKingSideLossList.remove(whiteKingSideLossList.size() - 1);
    this.whiteQueenSideLossList.remove(whiteQueenSideLossList.size() - 1);
    this.blackKingSideLossList.remove(blackKingSideLossList.size() - 1);
    this.blackQueenSideLossList.remove(blackQueenSideLossList.size() - 1);

    this.isDeadPositionUnwinnableQuickList.remove(isDeadPositionUnwinnableQuickList.size() - 1);

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

  public boolean canClaimFiftyMoveRuleWithOwnMove() {
    final var halfMoveCounterNow = this.getHalfMoveClock();
    if (halfMoveCounterNow >= 99) {
      for (final LegalMove legalMove : getLegalMoves()) {
        // we must not perform the move to check, which is crucial for performance reasons
        if (!BasicChessUtility.calculateIsResetHalfMoveClock(legalMove)) {
          return true;
        }
      }
    }
    return false;
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
    return InsufficientMaterialUtility.calculateIsInsufficientMaterial(side, getStaticPosition());
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

  public int getFullMoveNumber() {
    // because I implemented the full move number calculation for the next half move to be played, I now calculate the
    // full move number using this existing method. nicer would be to implement full move counter calculation and then
    // derive the full move counter for the next half move from it.
    final var fullMoveNumberForNextHalfMove = calculateFullMoveNumberForNextHalfMove(isFirstMove(),
        initialFen.fullMoveNumber(), initialFen.havingMove(), getHavingMove(), getPerformedHalfMoveCount());

    return switch (getHavingMove()) {
      case WHITE -> fullMoveNumberForNextHalfMove - 1;
      case BLACK -> fullMoveNumberForNextHalfMove;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }

  private static int calculateFullMoveNumberForNextHalfMove(boolean isFirstMove, int initialFenFullMoveNumber,
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
   * True iff the halfmove clock has reached the 50-move-rule threshold (FIDE 9.3). This is the on-board predicate
   * (claimable rule); the game continues until claimed.
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
   * True iff the halfmove clock has reached the 75-move-rule threshold (FIDE 9.6.2). This is an automatic FIDE
   * termination â€” once true, the game has ended in a draw and no further moves are accepted.
   */
  public boolean isSeventyFiveMove() {
    return getHalfMoveClock() >= ChessConstants.SEVENTY_FIVE_MOVE_RULE_HALF_MOVE_CLOCK_THRESHOLD;
  }

  /**
   * True iff the current position has occurred at least five times in the game (FIDE 9.6.1). This is an automatic FIDE
   * termination â€” once true, the game has ended in a draw and no further moves are accepted.
   */
  public boolean isFivefoldRepetition() {
    return getRepetitionCount() >= ChessConstants.FIVEFOLD_REPETITION_RULE_THRESHOLD;
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
   * Current position as a {@link StaticPosition} — derived on demand from the cached {@link BitboardPosition} via
   * {@link BitboardPositionUtility#toStaticPosition(BitboardPosition)}. {@code Board} no longer caches a {@code
   * StaticPosition} alongside the bitboard; the bitboard is the single source of truth on the data path.
   */
  public StaticPosition getStaticPosition() {
    return BitboardPositionUtility.toStaticPosition(Nulls.getLast(dynamicPositionList).bitboardPosition());
  }

  /**
   * Current position as a {@link BitboardPosition}. Carried directly on every {@link DynamicPosition} in
   * {@link #dynamicPositionList} (appended on every {@link #move}, popped on every {@link #unmove}). O(1) per call.
   * Bit-exact with {@code BitboardPositionUtility.fromStaticPosition(getStaticPosition())} on every halfmove of
   * every game (validated by the differential test).
   */
  public BitboardPosition getBitboardPosition() {
    return Nulls.getLast(dynamicPositionList).bitboardPosition();
  }

  StaticPosition getStaticPositionBeforeLastMove() {
    if (isFirstMove()) {
      throw new ProgrammingMistakeException("The method cannot be called if no move was yet made");
    }
    return BitboardPositionUtility
        .toStaticPosition(Nulls.get(dynamicPositionList, this.dynamicPositionList.size() - 2).bitboardPosition());
  }

  public boolean isEnPassantCapturePossible() {
    return Nulls.getLast(dynamicPositionList).enPassantCaptureTargetSquare() != Square.NONE;
  }

  private static boolean calculateIsEnPassantCapturePossible(Square enPassantCaptureTargetSquare, Side havingMove,
      StaticPosition staticPosition) {
    if (enPassantCaptureTargetSquare == Square.NONE) {
      return false;
    }
    // two potential capture moves
    if (!Square.calculateHasBehindSquare(havingMove, enPassantCaptureTargetSquare)) {
      // cannot be for en en passant target square
      throw new ProgrammingMistakeException();
    }
    final Square squareBehind = Square.calculateBehindSquare(havingMove, enPassantCaptureTargetSquare);

    // capture move from right square
    if (Square.calculateHasRightSquare(havingMove, squareBehind)) {
      final Square squareRight = Square.calculateRightSquare(havingMove, squareBehind);
      if (staticPosition.isOwnPawn(squareRight, havingMove)) {
        final MoveSpecification moveSpecification = new MoveSpecification(squareRight, enPassantCaptureTargetSquare);
        if (ChessRuleAnalyzer.isMoveKingSafe(staticPosition, havingMove, moveSpecification)) {
          return true;
        }
      }
    }

    // capture move from left square
    if (Square.calculateHasLeftSquare(havingMove, squareBehind)) {
      final Square squareLeft = Square.calculateLeftSquare(havingMove, squareBehind);
      if (staticPosition.isOwnPawn(squareLeft, havingMove)) {
        final MoveSpecification moveSpecification = new MoveSpecification(squareLeft, enPassantCaptureTargetSquare);
        if (ChessRuleAnalyzer.isMoveKingSafe(staticPosition, havingMove, moveSpecification)) {
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

  public ImmutableList<HalfMove> getHalfMoveList() {
    return Nulls.copyOfList(halfMoveList);
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
    return getStaticPosition().toString();
  }

  public Square getEnPassantCaptureTargetSquare() {
    if (isFirstMove()) {
      return initialFen.enPassantCaptureTargetSquare();
    }
    return EnPassantCaptureUtility.calculateEnPassantCaptureTargetSquare(getLastMove());
  }

  private static void checkIsEven(int intValue) {
    final var valueFloor = intValue / 2;
    final var valueRounded = (int) StrictMath.round(intValue / 2.0);
    if (valueFloor != valueRounded) {
      throw new ProgrammingMistakeException("The programmer overlooked something");
    }
  }

  public ImmutableList<LegalMove> getPerformedLegalMoveList() {
    return Nulls.copyOfList(performedLegalMoveList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dynamicPositionList, halfMoveClockList, halfMoveList, initialFen, isCheckList, isCheckmateList,
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
    final var other = (Board) obj;
    return Objects.equals(dynamicPositionList, other.dynamicPositionList)
        && Objects.equals(halfMoveClockList, other.halfMoveClockList)
        && Objects.equals(halfMoveList, other.halfMoveList) && Objects.equals(initialFen, other.initialFen)
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

  // ===== Methods previously inherited as `default` from the (now-removed) ChessBoard interface =====

  public int getFullMoveNumberForNextHalfMove() {
    if (isFirstMove()) {
      return getInitialFenFullMoveNumber();
    }
    return switch (getHavingMove()) {
      case BLACK -> getFullMoveNumber();
      case WHITE -> getFullMoveNumber() + 1;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
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
    final UnwinnabilityQuickVerdict unwinnableWhite = UnwinnableQuickAnalyzer.unwinnableQuick(this, Side.WHITE);
    final UnwinnabilityQuickVerdict unwinnableBlack = UnwinnableQuickAnalyzer.unwinnableQuick(this, Side.BLACK);
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
   * Per-ply: did the quick analyzer find both sides UNWINNABLE at the current position? Read against the eager per-ply
   * value; returns {@code false} when detection is disabled.
   */
  public boolean isDeadPositionUnwinnableQuick() {
    if (!isDetectDeadPositionUnwinnable) {
      return false;
    }
    return Nulls.getLast(isDeadPositionUnwinnableQuickList);
  }

  /**
   * FIDE 5.2.2 dead position: either both sides insufficient material (cheap, exact) or both sides UNWINNABLE per the
   * quick analyzer ({@link #isDeadPositionUnwinnableQuick}). The cheap predicate short-circuits the expensive one.
   */
  public boolean isDeadPosition() {
    return isInsufficientMaterial() || isDeadPositionUnwinnableQuick();
  }

  /**
   * Eager compute, called once per ply from the constructor and {@link #performMoveWithoutValidation}. Returns
   * {@code false} when detection is disabled; otherwise delegates to the quick analyzer, which isolates itself by
   * running on its own fresh detection-off board (no recursion concern).
   */
  private boolean computeDeadPositionUnwinnableQuick() {
    if (!isDetectDeadPositionUnwinnable) {
      return false;
    }
    if (UnwinnableQuickAnalyzer.unwinnableQuick(this, Side.WHITE) != UnwinnabilityQuickVerdict.UNWINNABLE) {
      return false;
    }
    return UnwinnableQuickAnalyzer.unwinnableQuick(this, Side.BLACK) == UnwinnabilityQuickVerdict.UNWINNABLE;
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
    return UnwinnableQuickAnalyzer.unwinnableQuick(this, side);
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
