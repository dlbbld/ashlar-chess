package com.dlb.chess.unwinnability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.DynamicPosition;
import com.dlb.chess.fen.model.Fen;
import com.dlb.chess.model.LegalMove;

public class UnwinnableQuickAnalyzer {

  private static final boolean IS_ALIGN_QUICK_WITH_AMBRONA_REFERENCE_IMPLEMENTATION = true;

  public static UnwinnabilityQuickVerdict unwinnableQuick(Board board, Side c) {
    return unwinnableQuick(board, c, false, new MobilitySolution());
  }

  /**
   * Runs the algorithm on a fresh detection-off board built from the caller's FEN. Isolation has two effects: (1) the
   * caller's board is not mutated, and (2) the analyzer's internal {@code board.move(...)} calls don't trigger the
   * dead-position auto-detect (which itself runs this analyzer). Repetition history from the caller's game is lost on
   * the fresh board — acceptable for the quick check, whose verdict is conservative anyway.
   */
  public static UnwinnabilityQuickVerdict unwinnableQuick(Board input, Side c, boolean isHasMobilitySolution,
      MobilitySolution calculatedMobilitySolution) {
    final Board board = copyCurrentPositionForQuickSearch(input);

    final String invariant = board.getFen();

    // 1: advance the position as long as there is only one legal move
    // if position is advanced cannot use the provided mobility solution if any
    var isCanUseMobilitySolution = true;
    var isForcedMove = true;
    var countHalfmoves = 0;
    final Set<DynamicPosition> forcedPositionSet = new HashSet<>();
    while (isForcedMove && forcedPositionSet.add(board.getDynamicPosition())) {
      isCanUseMobilitySolution = false;
      if (board.isCheckmate()) {
        // crucial, store the side before undoing moves, as it can change with undoing moves!!
        final Side sideBeingCheckmated = board.getHavingMove();
        unperformHalfmoves(board, countHalfmoves);
        if (!invariant.equals(board.getFen())) {
          throw new ProgrammingMistakeException("Board was changed");
        }
        if (sideBeingCheckmated == c) {
          return UnwinnabilityQuickVerdict.UNWINNABLE;
        }
        return calculateWinnableVerdict();
      }

      if (board.isInsufficientMaterial(c) || board.isStalemate()) {
        unperformHalfmoves(board, countHalfmoves);
        if (!invariant.equals(board.getFen())) {
          throw new ProgrammingMistakeException("Board was changed");
        }
        return UnwinnabilityQuickVerdict.UNWINNABLE;
      }

      isForcedMove = board.getLegalMoves().size() == 1;
      if (isForcedMove) {
        final LegalMove legalMove = Nulls.getFirst(board.getLegalMoves());
        board.move(legalMove.moveSpecification());
        countHalfmoves++;
      }
    }

    // 2: perform a depth-first search over the tree of variations of pos and interrupt the
    // search if (i) checkmate is found for player c or (ii) depth D is reached
    final String invariantTwo = board.getFen();
    final var checkmateSearchResult = FindHelpMateInterrupt.calculateHelpmate(board, c);
    if (!invariantTwo.equals(board.getFen())) {
      throw new ProgrammingMistakeException("Board was changed");
    }

    switch (checkmateSearchResult) {
      case YES:
        // 3: if checkmate was found on the previous search then return Winnable
        unperformHalfmoves(board, countHalfmoves);
        if (!invariant.equals(board.getFen())) {
          throw new ProgrammingMistakeException("Board was changed");
        }
        return calculateWinnableVerdict();
      case NO:
        // 4: else if the search was not interrupted then return Unwinnable
        unperformHalfmoves(board, countHalfmoves);
        if (!invariant.equals(board.getFen())) {
          throw new ProgrammingMistakeException("Board was changed");
        }
        return UnwinnabilityQuickVerdict.UNWINNABLE;
      case UNKNOWN:
        break;
      default:
        throw new IllegalArgumentException();
    }

    // 5: else if the position only contains pieces of type P,B,K and there are no semi-open
    // files in the position then
    if (calculateHasOnlyPawnsBishopsAndKings(board.getBitboardPosition())
        && !SemiOpenFilesUtility.calculateHasSemiOpenFile(board.getBitboardPosition())) {

      // 6: if true UnwinnableSS(pos, c, Mobility(pos)) then return Unwinnable
      final MobilitySolution mobilitySolution;
      if (isHasMobilitySolution && isCanUseMobilitySolution) {
        mobilitySolution = calculatedMobilitySolution;
      } else {
        mobilitySolution = Mobility.mobility(board);
      }
      if (UnwinnableSemiStatic.unwinnableSemiStatic(board, c, mobilitySolution)) {
        unperformHalfmoves(board, countHalfmoves);
        if (!invariant.equals(board.getFen())) {
          throw new ProgrammingMistakeException("Board was changed");
        }
        return UnwinnabilityQuickVerdict.UNWINNABLE;
      }
    }

    if (IS_ALIGN_QUICK_WITH_AMBRONA_REFERENCE_IMPLEMENTATION) {
      var isUnwinnable = false;
      final var hasOnlyPawnsAndBishops = calculateHasOnlyPawnsBishopsAndKings(board.getBitboardPosition());
      final var isBlockedCandidate = calculateIsBlockedCandidate(board.getBitboardPosition());
      if (isBlockedCandidate && hasOnlyPawnsAndBishops) {
        final MobilitySolution mobilitySolution = Mobility.mobility(board);
        isUnwinnable = UnwinnableSemiStatic.unwinnableSemiStatic(board, c, mobilitySolution);
      }

      if (isBlockedCandidate && !isUnwinnable && calculateIsAlmostOnlyPawnsBishopsAndKings(board.getBitboardPosition())
          && (board.isCheck() || UnwinnabilityMaterialBitboard.calculateHasKnight(board.getBitboardPosition()))) {
        isUnwinnable = calculateIsUnwinnableAfterOneMove(board, c);
      }

      final MovedKings movedKings = new MovedKings();
      final var isDynamicSearchCandidate = hasOnlyPawnsAndBishops && board.getLegalMoves().size() <= 8;
      if (!isUnwinnable && isDynamicSearchCandidate) {
        isUnwinnable = calculateIsDynamicallyUnwinnable(board, c, 7, movedKings, new HashMap<>());
      }
      if (!isUnwinnable && isDynamicSearchCandidate && movedKings.value != 3) {
        isUnwinnable = calculateIsDynamicallyUnwinnable(board, c, 15, movedKings, new HashMap<>());
      }

      if (isUnwinnable) {
        unperformHalfmoves(board, countHalfmoves);
        if (!invariant.equals(board.getFen())) {
          throw new ProgrammingMistakeException("Board was changed");
        }
        return UnwinnabilityQuickVerdict.UNWINNABLE;
      }
    }

    // 7: return PossiblyWinnable ( -> Unwinnability could not be determined)
    unperformHalfmoves(board, countHalfmoves);
    if (!invariant.equals(board.getFen())) {
      throw new ProgrammingMistakeException("Board was changed");
    }
    return UnwinnabilityQuickVerdict.POSSIBLY_WINNABLE;
  }

  private static boolean calculateHasOnlyPawnsBishopsAndKings(BitboardPosition bitboardPosition) {
    return !UnwinnabilityMaterialBitboard.calculateHasRook(bitboardPosition)
        && !UnwinnabilityMaterialBitboard.calculateHasKnight(bitboardPosition)
        && !UnwinnabilityMaterialBitboard.calculateHasQueen(bitboardPosition);
  }

  private static Board copyCurrentPositionForQuickSearch(Board input) {
    final Fen fen = new Fen(input.getFen(), input.getStaticPosition(), input.getHavingMove(),
        input.getCastlingRightWhite(), input.getCastlingRightBlack(), input.getEnPassantCaptureTargetSquare(), 0,
        input.getFullMoveNumberForNextHalfMove());
    return new Board(fen, false);
  }

  private static boolean calculateIsAlmostOnlyPawnsBishopsAndKings(BitboardPosition bitboardPosition) {
    final long heavyPieces = bitboardPosition.whiteKnights() | bitboardPosition.blackKnights()
        | bitboardPosition.whiteRooks() | bitboardPosition.blackRooks()
        | bitboardPosition.whiteQueens() | bitboardPosition.blackQueens();
    return Long.bitCount(heavyPieces) <= 1;
  }

  private static boolean calculateIsDynamicallyUnwinnable(Board board, Side intendedWinner, int depth,
      MovedKings movedKings, Map<DynamicSearchKey, Boolean> transpositionMap) {
    if (board.isInsufficientMaterial(intendedWinner)) {
      return true;
    }

    if (board.getLegalMoves().isEmpty() && board.isCheck()) {
      return board.getHavingMove() == intendedWinner;
    }

    if (depth <= 0) {
      return false;
    }

    final var cacheKey = new DynamicSearchKey(board.getDynamicPosition(), depth);
    if (transpositionMap.containsKey(cacheKey)) {
      return Nulls.get(transpositionMap, cacheKey);
    }

    for (final LegalMove legalMove : board.getLegalMoves()) {
      if (legalMove.movingPiece().getPieceType() == PieceType.KING) {
        movedKings.value |= board.getHavingMove() == Side.WHITE ? 2 : 1;
      }
      board.move(legalMove.moveSpecification());
      final var isUnwinnable = calculateIsDynamicallyUnwinnable(board, intendedWinner, depth - 1, movedKings,
          transpositionMap);
      board.unmove();
      if (!isUnwinnable) {
        transpositionMap.put(cacheKey, false);
        return false;
      }
    }

    transpositionMap.put(cacheKey, true);
    return true;
  }

  private static boolean calculateIsUnwinnableAfterOneMove(Board board, Side intendedWinner) {
    if (board.getLegalMoves().isEmpty()) {
      return !board.isCheck() || board.getHavingMove() == intendedWinner;
    }

    for (final LegalMove legalMove : board.getLegalMoves()) {
      board.move(legalMove.moveSpecification());
      final MobilitySolution mobilitySolution = Mobility.mobility(board);
      final var isUnwinnable = UnwinnableSemiStatic.unwinnableSemiStatic(board, intendedWinner, mobilitySolution);
      board.unmove();
      if (!isUnwinnable) {
        return false;
      }
    }
    return true;
  }

  private static boolean calculateIsBlockedCandidate(BitboardPosition bitboardPosition) {
    return calculateNumberOfBlockedPawns(bitboardPosition) >= 1 && !calculateHasLonelyPawns(bitboardPosition);
  }

  private static int calculateNumberOfBlockedPawns(BitboardPosition bitboardPosition) {
    // A white pawn one rank below a black pawn = the white pawn's bit shifted up 8 lands on a black pawn.
    // popcount of that intersection = number of such (white, black) blocking pairs (counted once per pair).
    return Long.bitCount((bitboardPosition.whitePawns() << 8) & bitboardPosition.blackPawns());
  }

  // Mask matching the reference: ranks 1..6 (1-indexed) for white, ranks 3..8 for black — i.e. excludes
  // the rank-immediately-before-promotion on the respective side. 0-indexed: white < rank 7, black > rank 2.
  private static final long WHITE_LONELY_RANK_MASK = 0x0000FFFFFFFFFFFFL; // bits 0..47 = ranks 1..6 (0-indexed)
  private static final long BLACK_LONELY_RANK_MASK = 0xFFFFFFFFFFFF0000L; // bits 16..63 = ranks 3..8 (0-indexed)

  private static boolean calculateHasLonelyPawns(BitboardPosition bitboardPosition) {
    final int whitePawnFileMask = projectToFiles(bitboardPosition.whitePawns() & WHITE_LONELY_RANK_MASK);
    final int blackPawnFileMask = projectToFiles(bitboardPosition.blackPawns() & BLACK_LONELY_RANK_MASK);
    return whitePawnFileMask != blackPawnFileMask;
  }

  private static int projectToFiles(long bitboard) {
    long result = bitboard;
    result |= result >>> 32;
    result |= result >>> 16;
    result |= result >>> 8;
    return (int) (result & 0xFFL);
  }

  private static UnwinnabilityQuickVerdict calculateWinnableVerdict() {
    return UnwinnabilityQuickVerdict.WINNABLE;
  }

  private static void unperformHalfmoves(Board board, int countHalfmoves) {
    for (var i = 1; i <= countHalfmoves; i++) {
      board.unmove();
    }
  }

  private static final class MovedKings {
    private int value;
  }

  private record DynamicSearchKey(DynamicPosition dynamicPosition, int depth) {
  }
}
