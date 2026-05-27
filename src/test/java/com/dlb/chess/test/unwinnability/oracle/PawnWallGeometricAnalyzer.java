package com.dlb.chess.test.unwinnability.oracle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.bitboard.StaticPositionBridge;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.StaticPosition;
import com.dlb.chess.board.enums.Piece;
import com.dlb.chess.board.enums.PieceType;
import com.dlb.chess.board.enums.Rank;
import com.dlb.chess.board.enums.Side;
import com.dlb.chess.board.enums.Square;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.utility.BasicUtility;
import com.dlb.chess.common.utility.StaticPositionUtility;
import com.google.common.collect.ImmutableList;

public class PawnWallGeometricAnalyzer {

  private static final boolean IS_IGNORE_PAWN_OWN_PAWN_OUTSIDE_PAWN_WALL_LINE = true;

  /**
   * Classifier: returns {@link PawnWallVerdict#YES} when the geometric pawn-wall check accepts the position as a sound
   * unwinnable-by-pawn-barrier configuration, {@link PawnWallVerdict#UNKNOWN} otherwise. See
   * {@code pawn-wall-soundness.md} for the full design.
   *
   * <p>
   * The verdict requires the position to satisfy:
   *
   * <ol>
   * <li>The chain check ({@link #calculateHasPawnWall(Board)}) — orthogonally-adjacent barrier squares span the board
   * from leftmost file to rightmost file, and both kings sit on opposite sides of the chain.</li>
   * <li>The all-pawns-involved check ({@link #areAllPawnsInvolvedInPawnWall(Board)}) — every pawn on the board is
   * either a chain element (if it's an own-side pawn at a chain-included square) or provides at least one chain attack
   * square (if it's an opposing pawn attacking a chain element). Floating pawns — those not contributing to the barrier
   * — admit helpmates where the king captures the floater (or allows the opposing king to capture it) and a promotion
   * follows. Without this check, positions like {@code 7k/8/1p6/1Pp5/2Pp4/pB1Pp1p1/P1B1P1P1/3B2K1 b - -} would slip
   * through: the chain {@code a5-b5-b4-c4-c3-d3-d2-e2-f2-g2-h2} is pawn/attack-only but the floating
   * {@code a2}/{@code a3} pair (White's outside pawn + the Black pawn it interacts with) makes the position
   * winnable.</li>
   * </ol>
   *
   * <p>
   * Bishops are tolerated: a position with own bishops on one colour and opposing pawns on the other colour
   * (colour-locked) can still satisfy both checks. {@link PawnWallKingWalkOracle} verifies the king-trapped conclusion;
   * the corpus test additionally cross-checks {@code UnwinnableQuick} agrees that the position is unwinnable for both
   * sides.
   */
  public static PawnWallVerdict calculate(Board board) {
    if (!calculateHasPawnWall(board)) {
      return PawnWallVerdict.UNKNOWN;
    }
    if (!areAllPawnsInvolvedInPawnWall(board)) {
      return PawnWallVerdict.UNKNOWN;
    }
    return PawnWallVerdict.YES;
  }

  /**
   * Returns {@code true} iff some chain that spans the board from leftmost to rightmost file (for each side
   * independently) has every same-side pawn as a chain element and every opposing pawn providing at least one chain
   * attack square. See {@link #calculate(Board)} for the rationale.
   */
  static boolean areAllPawnsInvolvedInPawnWall(Board board) {
    return areAllPawnsInvolvedInPawnWall(board, Side.WHITE) && areAllPawnsInvolvedInPawnWall(board, Side.BLACK);
  }

  private static boolean areAllPawnsInvolvedInPawnWall(Board board, Side side) {
    final List<List<Square>> chains = findAllPawnWallLines(board, side);
    final StaticPosition position = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
    for (final List<Square> chain : chains) {
      if (allPawnsInvolvedInSpecificChain(position, chain)) {
        return true;
      }
    }
    return false;
  }

  private static boolean allPawnsInvolvedInSpecificChain(StaticPosition position, List<Square> chain) {
    final Set<Square> chainSet = new HashSet<>(chain);
    for (final Square square : Square.REAL) {
      final Piece piece = position.get(square);
      if (piece == Piece.NONE || piece.getPieceType() != PieceType.PAWN) {
        continue;
      }
      // A pawn is involved if it sits on a chain square (it IS a chain element) OR it attacks a chain square
      // (its attack contributes the attack-square barrier entry in the chain). A pawn satisfying neither is
      // "floating" - not contributing to the barrier. Floating pawns are the leak the
      // 7k/8/1p6/1Pp5/2Pp4/pB1Pp1p1/P1B1P1P1/3B2K1 b - - position demonstrates: a2/a3 (White's outside pawn and
      // the Black pawn ahead of it) are not part of the chain a5-b5-b4-c4-c3-d3-d2-e2-f2-g2-h2 and don't attack
      // any chain element. The king can capture a3 and the a2 pawn promotes.
      if (chainSet.contains(square)) {
        continue;
      }
      if (!pawnAttacksAnyChainSquare(square, piece.getSide(), chainSet)) {
        return false;
      }
    }
    return true;
  }

  private static boolean pawnAttacksAnyChainSquare(Square pawnSquare, Side pawnSide, Set<Square> chainSet) {
    if (Square.calculateHasLeftDiagonalSquare(pawnSide, pawnSquare)) {
      final Square left = Square.calculateLeftDiagonalSquare(pawnSide, pawnSquare);
      if (chainSet.contains(left)) {
        return true;
      }
    }
    if (Square.calculateHasRightDiagonalSquare(pawnSide, pawnSquare)) {
      final Square right = Square.calculateRightDiagonalSquare(pawnSide, pawnSquare);
      if (chainSet.contains(right)) {
        return true;
      }
    }
    return false;
  }

  public static boolean calculateHasPawnWall(Board board) {

    final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());

    // 1) we do not consider positions with rooks, knights or queens
    // 2) we need at least a pawn for a pawn wall (the minimum is most likely much higher, but not having a better
    // assessment for now)
    if (hasAnyPieceType(staticPosition, PieceType.ROOK) || hasAnyPieceType(staticPosition, PieceType.KNIGHT)
        || hasAnyPieceType(staticPosition, PieceType.QUEEN) || !hasAnyPieceType(staticPosition, PieceType.PAWN)) {
      return false;
    }

    // here we branch between having bishops and no bishops
    if (hasAnyPieceType(staticPosition, PieceType.BISHOP)) {
      // we only look at the case if no pawn can move
      if (!calculateIsAllPawnsBlocked(board)) {
        return false;
      }
      // A bishop only threatens the wall if it can actually reach an opponent pawn through a
      // sequence of diagonal moves. Same-square-colour is necessary but not sufficient: a bishop
      // trapped behind its own pawns (e.g. h2 with f4 and d4 of the same side on its diagonals)
      // cannot reach any opponent pawn no matter what colour the opponent pawns sit on.
      for (final Side side : Side.REAL) {
        if (calculateAnyBishopCanReachOpponentPawn(staticPosition, side)) {
          return false;
        }
      }
      return calculateIsHasPawnWallAfterPrecheck(board);
    }

    // from here we only have kings and pawns
    if (!calculateIsAllPawnsBlocked(board)) {
      // 1) if one reason for not blocked is capturing, we give up the analysis, too complex

      // 2) first we check that the non blocked pawns
      // 2a) have another pawn in front of any color
      // 2b) when moving can never capture another pawn

      // 1) and 2a)
      if (!calculateIsAllPawnsCannotCapture(board) || !calculateIsAllPawnsCanReachPawnAhead(board)) {
        return false;
      }

      // 2b)
      // For this we calculate all squares where the pawns as is can capture plus the squares resulting in moving per
      // side. When the intersection is empty we think we hold the promise.
      final Set<Square> attackingSquaresWhite = calculateAttackingSquares(board, Side.WHITE);
      final Set<Square> pawnSquaresBlack = calculatePawnSquares(board, Side.BLACK);
      if (!BasicUtility.calculateIsDisjoint(attackingSquaresWhite, pawnSquaresBlack)) {
        return false;
      }

      final Set<Square> attackingSquaresBlack = calculateAttackingSquares(board, Side.BLACK);
      final Set<Square> pawnSquaresWhite = calculatePawnSquares(board, Side.WHITE);
      if (!BasicUtility.calculateIsDisjoint(attackingSquaresBlack, pawnSquaresWhite)) {
        return false;
      }
    }

    // from here we have all pawns blocked
    return calculateIsHasPawnWallAfterPrecheck(board);

  }

  private static boolean calculateIsHasPawnWallAfterPrecheck(Board board) {
    // check pawn wall line
    final var hasPawnWallWhite = calculateHasPawnWallLine(board, Side.WHITE);
    if (!hasPawnWallWhite) {
      return false;
    }

    final var hasPawnWallBlack = calculateHasPawnWallLine(board, Side.BLACK);
    if (!hasPawnWallBlack) {
      return false;
    }

    // check if both kings are on their side of the board
    final var isKingBehindPawnWallWhite = calculateIsKingBehindPawnWall(board, Side.WHITE);
    if (!isKingBehindPawnWallWhite) {
      return false;
    }

    return calculateIsKingBehindPawnWall(board, Side.BLACK);
  }

  // calculate capturing squares
  protected static Set<Square> calculateAttackingSquares(Board board, Side side) {
    final Set<Square> attackingSquares = new TreeSet<>(calculateAttackingSquareAsIs(board, side));
    attackingSquares.addAll(calculateAttackingSquareAfterMoving(board, side));
    return attackingSquares;
  }

  private static Set<Square> calculateAttackingSquareAsIs(Board board, Side side) {
    final Set<Square> attackingSquaresAsIs = new TreeSet<>();
    for (final Square square : Square.REAL) {
      if (StaticPositionBridge.toStaticPosition(board.getBitboardPosition()).isOwnPawn(square, side)) {
        if (Square.calculateHasLeftDiagonalSquare(side, square)) {
          final Square squareLeftDiagonal = Square.calculateLeftDiagonalSquare(side, square);
          attackingSquaresAsIs.add(squareLeftDiagonal);
        }
        if (Square.calculateHasRightDiagonalSquare(side, square)) {
          final Square squareRightDiagonal = Square.calculateRightDiagonalSquare(side, square);
          attackingSquaresAsIs.add(squareRightDiagonal);
        }
      }
    }
    return attackingSquaresAsIs;
  }

  private static Set<Square> calculateAttackingSquareAfterMoving(Board board, Side side) {
    final Set<Square> attackingSquaresAll = new TreeSet<>();

    final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
    for (final Square square : Square.REAL) {
      if (StaticPositionBridge.toStaticPosition(board.getBitboardPosition()).isOwnPawn(square, side)) {
        final Set<Square> attackingSquaresAfterMoving = new TreeSet<>();
        addAttackingSquaresAfterMovingForFile(attackingSquaresAfterMoving, square, staticPosition, side);
        attackingSquaresAll.addAll(attackingSquaresAfterMoving);
      }
    }
    return attackingSquaresAll;
  }

  private static void addAttackingSquaresAfterMovingForFile(Set<Square> foundSquares, Square squareLookAhead,
      StaticPosition staticPosition, Side side) {
    if (Square.calculateHasAheadSquare(side, squareLookAhead)) {
      final Square squareAhead = Square.calculateAheadSquare(side, squareLookAhead);
      if (Rank.calculateIsPromotionRank(side, squareAhead.getRank())) {
        return;
      }
      // now we assume the square is empty
      if (!staticPosition.isEmpty(squareAhead)) {
        final Square ownKingSquare = StaticPositionUtility.calculateKingSquare(staticPosition, side);
        if (squareAhead != ownKingSquare) {
          return;
        }
      }

      if (Square.calculateHasLeftDiagonalSquare(side, squareAhead)) {
        final Square squareLeftDiagonal = Square.calculateLeftDiagonalSquare(side, squareAhead);
        foundSquares.add(squareLeftDiagonal);
      }
      if (Square.calculateHasRightDiagonalSquare(side, squareAhead)) {
        final Square squareRightDiagonal = Square.calculateRightDiagonalSquare(side, squareAhead);
        foundSquares.add(squareRightDiagonal);
      }

      // check if can potentially further advance

      addAttackingSquaresAfterMovingForFile(foundSquares, squareAhead, staticPosition, side);
    }
  }

  // same for pawn squares
  protected static Set<Square> calculatePawnSquares(Board board, Side side) {
    final Set<Square> pawnSquares = new TreeSet<>(calculatePawnSquareAsIs(board, side));
    pawnSquares.addAll(calculatePawnSquareAfterMoving(board, side));
    return pawnSquares;
  }

  private static Set<Square> calculatePawnSquareAsIs(Board board, Side side) {
    final Set<Square> pawnSquaresAsIs = new TreeSet<>();
    for (final Square square : Square.REAL) {
      if (StaticPositionBridge.toStaticPosition(board.getBitboardPosition()).isOwnPawn(square, side)) {
        pawnSquaresAsIs.add(square);
      }
    }
    return pawnSquaresAsIs;
  }

  private static Set<Square> calculatePawnSquareAfterMoving(Board board, Side side) {
    final Set<Square> pawnSquaresAll = new TreeSet<>();

    final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
    for (final Square square : Square.REAL) {
      if (StaticPositionBridge.toStaticPosition(board.getBitboardPosition()).isOwnPawn(square, side)) {
        final Set<Square> pawnSquaresAfterMoving = new TreeSet<>();
        addPawnSquaresAfterMovingForFile(pawnSquaresAfterMoving, square, staticPosition, side);
        pawnSquaresAll.addAll(pawnSquaresAfterMoving);
      }
    }
    return pawnSquaresAll;
  }

  private static void addPawnSquaresAfterMovingForFile(Set<Square> foundSquares, Square squareLookAhead,
      StaticPosition staticPosition, Side side) {
    if (Square.calculateHasAheadSquare(side, squareLookAhead)) {
      final Square squareAhead = Square.calculateAheadSquare(side, squareLookAhead);
      if (Rank.calculateIsPromotionRank(side, squareAhead.getRank())) {
        return;
      }
      // now we assume the square is empty
      if (!staticPosition.isEmpty(squareAhead)) {
        final Square ownKingSquare = StaticPositionUtility.calculateKingSquare(staticPosition, side);
        if (squareAhead != ownKingSquare) {
          return;
        }
      }

      foundSquares.add(squareAhead);

      // check if can potentially further advance
      addPawnSquaresAfterMovingForFile(foundSquares, squareAhead, staticPosition, side);
    }
  }

  private static boolean calculateIsKingBehindPawnWall(Board board, Side side) {
    final StaticPosition blockedSquares = calculateBlockedSquares(board, side);
    final Square kingSquare = StaticPositionUtility
        .calculateKingSquare(StaticPositionBridge.toStaticPosition(board.getBitboardPosition()), side);
    return calculateIsKingBehindPawnWall(blockedSquares, kingSquare, side);
  }

  private static boolean calculateIsKingBehindPawnWall(StaticPosition blockedSquares, Square kingSquare, Side side) {
    for (final Square square : Square.REAL) {
      if (square.getFile() == kingSquare.getFile() && blockedSquares.isPawn(square)) {
        switch (side) {
          case WHITE:
            if (square.getRank().getNumber() < kingSquare.getRank().getNumber()) {
              return false;
            }
            break;
          case BLACK:
            if (square.getRank().getNumber() > kingSquare.getRank().getNumber()) {
              return false;
            }
            break;
          case NONE:
          default:
            throw new IllegalArgumentException();
        }
      }
    }
    return true;
  }

  protected static boolean calculateIsAllPawnsHavePawnAhead(Board board) {
    // loop over all square and check each pawn
    final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
    for (final Square square : Square.REAL) {
      if (staticPosition.isPawn(square)) {
        final Piece piece = staticPosition.get(square);
        if (!Square.calculateHasAheadSquare(piece.getSide(), square)) {
          throw new ProgrammingMistakeException(
              "We are not expecting to find pawn on promotion rank, as we only allow legal positions");
        }
        final Square squareAhead = Square.calculateAheadSquare(piece.getSide(), square);
        if (staticPosition.isEmpty(squareAhead)) {
          return false;
        }
        final Piece pieceAhead = staticPosition.get(squareAhead);
        // we only require a piece ahead, no matter which side
        if (pieceAhead.getPieceType() != PieceType.PAWN) {
          return false;
        }
      }
    }
    return true;
  }

  protected static boolean calculateIsAllPawnsCanReachPawnAhead(Board board) {
    // loop over all square and check each pawn
    final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
    for (final Square square : Square.REAL) {
      if (!staticPosition.isEmpty(square)) {
        final Piece piece = staticPosition.get(square);
        if (piece.getPieceType() == PieceType.PAWN && !calculateHasPawnAhead(staticPosition, square, piece.getSide())) {
          return false;
        }
      }
    }
    return true;
  }

  private static boolean calculateHasPawnAhead(StaticPosition staticPosition, Square square, Side side) {

    if (!Square.calculateHasAheadSquare(side, square)) {
      throw new ProgrammingMistakeException("We are not expecting this to happen, as we only allow legal positions");
    }

    final var direction = side == Side.WHITE ? 1 : -1;
    final var promotionRankNumber = Rank.calculatePromotionRank(side).getNumber();
    final var fileNumber = square.getFile().getNumber();

    for (var rankNumber = square.getRank().getNumber()
        + direction; rankNumber != promotionRankNumber; rankNumber += direction) {
      final Square squareAhead = Square.calculate(fileNumber, rankNumber);
      if (staticPosition.isPawn(squareAhead)) {
        // we only require a piece ahead, no matter which side
        return true;
      }
    }

    return false;
  }

  protected static boolean calculateIsAllPawnsCannotCapture(Board board) {
    // we must check the en passant capture as not seen by following static checks
    if (board.isEnPassantCapturePossible()) {
      return false;
    }

    // loop over all square and check each pawn
    final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
    for (final Square square : Square.REAL) {
      if (staticPosition.isPawn(square)) {
        final Piece piece = staticPosition.get(square);
        if (Square.calculateHasLeftDiagonalSquare(piece.getSide(), square)) {
          final Square squareLeftDiagonal = Square.calculateLeftDiagonalSquare(piece.getSide(), square);
          if (!staticPosition.isEmpty(squareLeftDiagonal)) {
            // if opponent piece not ok
            final Piece pieceLeftDiagonal = staticPosition.get(squareLeftDiagonal);
            if (pieceLeftDiagonal.getSide() != piece.getSide()) {
              return false;
            }
          }
        }
        if (Square.calculateHasRightDiagonalSquare(piece.getSide(), square)) {
          final Square squareRightDiagonal = Square.calculateRightDiagonalSquare(piece.getSide(), square);
          if (!staticPosition.isEmpty(squareRightDiagonal)) {
            // if opponent piece not ok
            final Piece pieceRightDiagonal = staticPosition.get(squareRightDiagonal);
            if (pieceRightDiagonal.getSide() != piece.getSide()) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  protected static boolean calculateIsAllPawnsBlocked(Board board) {
    return calculateIsAllPawnsHavePawnAhead(board) && calculateIsAllPawnsCannotCapture(board);
  }

  private static StaticPosition calculateBlockedSquares(Board board, Side side) {
    final StaticPosition staticPosition = StaticPositionBridge.toStaticPosition(board.getBitboardPosition());
    final Square ownKingSquare = StaticPositionUtility.calculateKingSquare(staticPosition, side);

    // we make a board full of white pawns to mark all blocked squares
    StaticPosition calculateBlockedSquares = StaticPosition.EMPTY_POSITION;
    for (final Square square : Square.REAL) {
      if (staticPosition.isOwnPawn(square, side)) {
        // we ignore pawn with empty squares ahead, but such pawns must be checked prior calling to have a pawn
        // ahead
        if (Square.calculateHasAheadSquare(side, square)) {
          final Square squareAhead = Square.calculateAheadSquare(side, square);
          // now we assume the square is empty
          if (staticPosition.isEmpty(squareAhead) || squareAhead == ownKingSquare) {
            // we ignore pawns which can still move forward
            continue;
          }
          calculateBlockedSquares = createChangedPositionIfChange(calculateBlockedSquares, square);
        }
      } else if (staticPosition.isOpponentPawn(square, side)) {
        if (Square.calculateHasLeftDiagonalSquare(side.getOppositeSide(), square)) {
          final Square squareLeftDiagonal = Square.calculateLeftDiagonalSquare(side.getOppositeSide(), square);
          calculateBlockedSquares = createChangedPositionIfChange(calculateBlockedSquares, squareLeftDiagonal);
        }
        if (Square.calculateHasRightDiagonalSquare(side.getOppositeSide(), square)) {
          final Square squareRightDiagonal = Square.calculateRightDiagonalSquare(side.getOppositeSide(), square);
          calculateBlockedSquares = createChangedPositionIfChange(calculateBlockedSquares, squareRightDiagonal);
        }
      }
    }
    return calculateBlockedSquares;
  }

  private static StaticPosition createChangedPositionIfChange(StaticPosition staticPosition, Square square) {
    if (staticPosition.get(square) != Piece.WHITE_PAWN) {
      return staticPosition.createChangedPosition(square, Piece.WHITE_PAWN);
    }
    return staticPosition;
  }

  protected static boolean calculateHasPawnWallLine(Board board, Side side) {
    return calculateHasPawnWallLine(StaticPositionBridge.toStaticPosition(board.getBitboardPosition()),
        findAllPawnWallLines(board, side), side);
  }

  private static boolean calculateHasPawnWallLine(StaticPosition staticPosition, List<List<Square>> resultList,
      Side side) {
    // we want all own pawns behind the pawn line for the one example
    for (final List<Square> pawnWallLine : resultList) {
      // we check to find one such line
      if (calculateHasAllPawnsBehindLine(staticPosition, pawnWallLine, side)) {
        return true;
      }
    }
    // no such line found
    return false;
  }

  /**
   * Returns every chain of orthogonally-adjacent barrier squares spanning {@code side}'s leftmost file to the rightmost
   * file. Barrier squares are own pawns blocked by a piece ahead and squares attacked by opposing pawns. The chain
   * elements are never own non-pawn pieces - those squares do not appear in {@code blockedSquares}.
   */
  static List<List<Square>> findAllPawnWallLines(Board board, Side side) {
    final StaticPosition blockedSquares = calculateBlockedSquares(board, side);

    final List<Square> startCandidates = new ArrayList<>();
    final List<Square> leftMostFile = leftmostFile(side);

    for (final Square squareLeftMostFile : leftMostFile) {
      if (!blockedSquares.isEmpty(squareLeftMostFile)) {
        startCandidates.add(squareLeftMostFile);
      }
    }

    final List<Square> currentLine = new ArrayList<>();
    final List<List<Square>> resultList = new ArrayList<>();
    for (final Square squareCandidate : startCandidates) {
      currentLine.add(squareCandidate);
      calculatePawnWallLines(blockedSquares, squareCandidate, true, side, currentLine, resultList);
      currentLine.remove(currentLine.size() - 1);
    }
    return resultList;
  }

  private static boolean calculateHasAllPawnsBehindLine(StaticPosition staticPosition, List<Square> pawnWallLine,
      Side side) {
    if (IS_IGNORE_PAWN_OWN_PAWN_OUTSIDE_PAWN_WALL_LINE) {
      return true;
    }
    for (final Square pawnWallSquare : pawnWallLine) {
      // we found one element with own pawns not behind, so returning false
      if (!calculateHasAllPawnsBehindSquare(staticPosition, pawnWallSquare, side)) {
        return false;
      }
    }
    // all own pawns behind the line
    return true;
  }

  private static boolean calculateHasAllPawnsBehindSquare(StaticPosition staticPosition, Square squareToCheck,
      Side side) {
    // we want all own pawns behind the pawn line for the one example
    if (Square.calculateHasAheadSquare(side, squareToCheck)) {
      final Square squareAhead = Square.calculateAheadSquare(side, squareToCheck);
      final Piece pieceOnSquareAhead = staticPosition.get(squareAhead);
      if (pieceOnSquareAhead.getPieceType() == PieceType.PAWN && pieceOnSquareAhead.getSide() == side) {
        // found a own pawn outside the pawn wall line
        return false;
      }
      return calculateHasAllPawnsBehindSquare(staticPosition, squareAhead, side);

    }
    return true;
  }

  protected static void calculatePawnWallLines(StaticPosition blockedSquares, Square squareCandidate,
      boolean isNeedNeighbor, Side side, List<Square> currentLine, List<List<Square>> resultList) {
    if (isNeedNeighbor) {
      final Square squareNeighbor = Square.calculateRightSquare(side, squareCandidate);
      if (!blockedSquares.isEmpty(squareNeighbor)) {
        currentLine.add(squareNeighbor);
        if (Square.calculateIsRightMostFile(squareNeighbor, side)) {
          final List<Square> foundPawnWallLine = new ArrayList<>(currentLine);
          resultList.add(foundPawnWallLine);
          return;
        }
        calculatePawnWallLines(blockedSquares, squareNeighbor, false, side, currentLine, resultList);
        currentLine.remove(currentLine.size() - 1);
      }
      return;
    }

    // check all candidates to continue pawn wall

    // first - square behind
    if (Square.calculateHasBehindSquare(side, squareCandidate)) {
      final Square squareBehind = Square.calculateBehindSquare(side, squareCandidate);
      if (!blockedSquares.isEmpty(squareBehind)) {
        currentLine.add(squareBehind);
        calculatePawnWallLines(blockedSquares, squareBehind, true, side, currentLine, resultList);
        currentLine.remove(currentLine.size() - 1);
      }
    }

    // second - square right
    if (Square.calculateHasRightSquare(side, squareCandidate)) {
      final Square squareRight = Square.calculateRightSquare(side, squareCandidate);
      if (!blockedSquares.isEmpty(squareRight)) {
        currentLine.add(squareRight);
        if (Square.calculateIsRightMostFile(squareRight, side)) {
          final List<Square> foundPawnWallLine = new ArrayList<>(currentLine);
          resultList.add(foundPawnWallLine);
          return;
        }
        calculatePawnWallLines(blockedSquares, squareRight, false, side, currentLine, resultList);
        currentLine.remove(currentLine.size() - 1);
      }
    }

    // first - square ahead
    if (Square.calculateHasAheadSquare(side, squareCandidate)) {
      final Square squareAhead = Square.calculateAheadSquare(side, squareCandidate);
      if (!blockedSquares.isEmpty(squareAhead)) {
        currentLine.add(squareAhead);
        calculatePawnWallLines(blockedSquares, squareAhead, true, side, currentLine, resultList);
        currentLine.remove(currentLine.size() - 1);
      }
    }
  }

  /**
   * Returns {@code true} iff any bishop of {@code side} has at least one opponent pawn in its diagonal-reachability set
   * on the current static position. Reachability is computed by BFS from each bishop along all four diagonal
   * directions: empty squares are passed through, opponent pieces can be captured (terminate that ray), own pieces
   * block the bishop entirely.
   *
   * <p>
   * Used by the pawn-wall heuristic: a bishop can only threaten the wall if it can actually capture a pawn that's part
   * of the wall. A bishop trapped behind its own pawns is harmless even if opponent pawns share its square colour.
   */
  private static boolean calculateAnyBishopCanReachOpponentPawn(StaticPosition staticPosition, Side side) {
    for (final Square bishopSquare : Square.REAL) {
      final Piece piece = staticPosition.get(bishopSquare);
      if (piece == Piece.NONE || piece.getSide() != side || piece.getPieceType() != PieceType.BISHOP) {
        continue;
      }
      final Set<Square> reach = calculateBishopReach(staticPosition, bishopSquare, side);
      for (final Square reachSquare : reach) {
        final Piece pieceOnReach = staticPosition.get(reachSquare);
        if (pieceOnReach != Piece.NONE && pieceOnReach.getSide() != side
            && pieceOnReach.getPieceType() == PieceType.PAWN) {
          return true;
        }
      }
    }
    return false;
  }

  private static final int[][] BISHOP_DIAGONALS = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

  /**
   * BFS the bishop's transitive diagonal reach from {@code bishopSquare} on the current position. A square is in the
   * returned set if the bishop can stand on it after some sequence of legal bishop moves (sliding through empty squares
   * only; opponent pieces can be the terminal square — captured; own pieces block the ray).
   */
  private static Set<Square> calculateBishopReach(StaticPosition staticPosition, Square bishopSquare, Side side) {
    final Set<Square> reachable = new TreeSet<>();
    final Set<Square> visited = new HashSet<>();
    final Deque<Square> queue = new ArrayDeque<>();
    queue.add(bishopSquare);
    visited.add(bishopSquare);

    while (!queue.isEmpty()) {
      @SuppressWarnings("null") final Square current = queue.poll();
      for (final int[] dir : BISHOP_DIAGONALS) {
        var file = current.getFile().getNumber();
        var rank = current.getRank().getNumber();
        while (true) {
          file += dir[0];
          rank += dir[1];
          if (file < 1 || file > 8 || rank < 1 || rank > 8) {
            break;
          }
          final Square next = Square.calculate(file, rank);
          final Piece pieceOnNext = staticPosition.get(next);
          if (pieceOnNext != Piece.NONE) {
            if (pieceOnNext.getSide() != side) {
              // opponent piece — bishop can capture (terminal); ray stops here
              reachable.add(next);
            }
            break;
          }
          reachable.add(next);
          if (visited.add(next)) {
            queue.add(next);
          }
        }
      }
    }
    return reachable;
  }

  private static final ImmutableList<Square> LEFTMOST_FILE_WHITE = Nulls.listOf(Square.A1, Square.A2, Square.A3,
      Square.A4, Square.A5, Square.A6, Square.A7, Square.A8);

  private static final ImmutableList<Square> LEFTMOST_FILE_BLACK = Nulls.listOf(Square.H8, Square.H7, Square.H6,
      Square.H5, Square.H4, Square.H3, Square.H2, Square.H1);

  // Local material checks. Inlined from the former public MaterialUtility so that
  // material arithmetic is not re-exposed on the public API surface for this test helper.
  private static boolean hasAnyPieceType(StaticPosition staticPosition, PieceType pieceType) {
    for (final Square boardSquare : Square.REAL) {
      final Piece pieceOnSquare = staticPosition.get(boardSquare);
      if (pieceOnSquare != Piece.NONE && pieceOnSquare.getPieceType() == pieceType) {
        return true;
      }
    }
    return false;
  }

  private static List<Square> leftmostFile(Side side) {
    return switch (side) {
      case WHITE -> LEFTMOST_FILE_WHITE;
      case BLACK -> LEFTMOST_FILE_BLACK;
      case NONE -> throw new IllegalArgumentException();
      default -> throw new IllegalArgumentException();
    };
  }
}
