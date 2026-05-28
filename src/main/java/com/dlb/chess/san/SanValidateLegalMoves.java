package com.dlb.chess.san;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.dlb.chess.bitboard.BitboardPosition;
import com.dlb.chess.bitboard.KingAttacks;
import com.dlb.chess.board.Board;
import com.dlb.chess.board.enums.*;
import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.constants.CastlingConstants;
import com.dlb.chess.common.constants.EnumConstants;
import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.dlb.chess.common.model.MoveSpecification;
import com.dlb.chess.common.utility.ListUtility;
import com.dlb.chess.common.utility.SetUtility;
import com.dlb.chess.enums.CastlingCheck;
import com.dlb.chess.enums.KingSafetyCheck;
import com.dlb.chess.enums.MovementCheck;
import com.dlb.chess.messages.Message;
import com.dlb.chess.model.LegalMove;
import com.dlb.chess.model.LegalMoveKind;
import com.dlb.chess.moves.CastlingUtility;

abstract class SanValidateLegalMoves extends AbstractSan implements EnumConstants {

  public static MoveSpecification calculateMoveSpecificationForSan(Board board, Side havingMove, SanFormat sanFormat,
      SanConversion sanConversion, MoveSpecification legalMoveOnlyCandidate) {

    if (sanFormat == SanFormat.KING_CASTLING_QUEEN_SIDE) {
      return new MoveSpecification(CastlingMove.QUEEN_SIDE);
    }
    if (sanFormat == SanFormat.KING_CASTLING_KING_SIDE) {
      return new MoveSpecification(CastlingMove.KING_SIDE);
    }

    final Square toSquare = sanConversion.toSquare();

    switch (sanFormat) {
      case KING_CASTLING_QUEEN_SIDE:
      case KING_CASTLING_KING_SIDE:
        throw new ProgrammingMistakeException("Castling is handled before switch");
      case PAWN_NON_CAPTURING_NON_PROMOTION: {
        if (!Rank.calculateIsPawnTwoSquareAdvanceRank(havingMove, toSquare.getRank())) {
          // one square advance, san information is enough
          // from file equals to file and from rank is the rank before to rank
          final File fromFile = toSquare.getFile(); // moving straight forward
          final Rank fromRank = Rank.calculatePreviousRank(havingMove, toSquare.getRank());
          final Square fromSquare = Square.calculate(fromFile, fromRank);
          return new MoveSpecification(fromSquare, toSquare);
        }
        // we calculate this with san information and knowing it's a legal move (so e4
        // is e2-e4 xor e3-e4)
        final Square potentialJumpOverSquare = Square.calculateJumpOverSquare(havingMove, toSquare);
        if (board.getBitboardPosition().get(potentialJumpOverSquare) == Piece.NONE) {
          // two square advance
          final File fromFile = toSquare.getFile(); // moving straight forward
          final Rank fromRank = Rank.calculatePawnInitialRank(havingMove);
          final Square fromSquare = Square.calculate(fromFile, fromRank);
          return new MoveSpecification(fromSquare, toSquare);
        }

        // one square advance
        final Square fromSquare = potentialJumpOverSquare;
        return new MoveSpecification(fromSquare, toSquare);
      }
      case PAWN_CAPTURING_NON_PROMOTION: {
        // from file is in the san and from rank is the rank before to rank

        final Rank fromRank = Rank.calculatePreviousRank(havingMove, toSquare.getRank());
        final Square fromSquare = Square.calculate(sanConversion.fromFile(), fromRank);
        return new MoveSpecification(fromSquare, toSquare);
      }
      case PAWN_NON_CAPTURING_PROMOTION: {
        // from file equals to file and from rank is the rank before to rank
        final File fromFile = toSquare.getFile(); // moving straight forward
        final Rank fromRank = Rank.calculatePreviousRank(havingMove, toSquare.getRank());
        final Square fromSquare = Square.calculate(fromFile, fromRank);
        return new MoveSpecification(fromSquare, toSquare, sanConversion.promotionPieceType());
      }
      case PAWN_CAPTURING_PROMOTION: {
        // from file is in the san and from rank is the rank before to rank
        final Rank fromRank = Rank.calculatePreviousRank(havingMove, toSquare.getRank());
        final Square fromSquare = Square.calculate(sanConversion.fromFile(), fromRank);
        return new MoveSpecification(fromSquare, toSquare, sanConversion.promotionPieceType());
      }
      case RNBQ_CAPTURING_SQUARE: {
        // san is enough to determine from square
        final Square fromSquare = AbstractSan.calculateFromSquare(sanConversion);
        return new MoveSpecification(fromSquare, toSquare);
      }
      case KING_NON_CASTLING_CAPTURING:
      case KING_NON_CASTLING_NON_CAPTURING:
      case RNBQ_CAPTURING_FILE:
      case RNBQ_CAPTURING_NEITHER:
      case RNBQ_CAPTURING_RANK:
      case RNBQ_NON_CAPTURING_SQUARE:
      case RNBQ_NON_CAPTURING_FILE:
      case RNBQ_NON_CAPTURING_NEITHER:
      case RNBQ_NON_CAPTURING_RANK: {
        // legal move is required to determine from square
        final Square fromSquare = legalMoveOnlyCandidate.fromSquare();
        return new MoveSpecification(fromSquare, toSquare);
      }
      default:
        throw new IllegalArgumentException();

    }
  }

  public static List<LegalMove> calculateLegalMovesCandidates(Board board, Side havingMove, SanParse sanParse) {
    final SanFormat sanFormat = sanParse.sanFormat();
    final SanConversion sanConversion = sanParse.sanConversion();

    // for castling we need to filter the castling moves
    if (sanFormat.isKingCastlingMove()) {
      return filterCastlingMove(board.getLegalMoves());
    }

    final PieceType pieceType = sanConversion.movingPieceType();
    final Piece piece = PieceType.calculate(havingMove, pieceType);

    final List<LegalMove> legalMovesForMovingPiece = MoveToSan.calculateLegalMovesForMovingPiece(piece,
        board.getLegalMoves());
    // for non castling moves we need to filter by the to square (which is always set for non castling)
    final Square toSquare = sanConversion.toSquare();
    final List<LegalMove> legalMovesCandidates = filterLegalMovesCandidates(legalMovesForMovingPiece, toSquare);

    // for pawn moves we must filter additionally by the from file!!
    if (sanFormat == SanFormat.PAWN_CAPTURING_NON_PROMOTION || sanFormat == SanFormat.PAWN_CAPTURING_PROMOTION) {
      return calculateLegalMovesCandidates(legalMovesCandidates, sanConversion.fromFile());
    }
    return legalMovesCandidates;
  }

  private static List<LegalMove> filterCastlingMove(List<LegalMove> allLegalMoves) {
    final List<LegalMove> filteredLegalMoves = new ArrayList<>();
    for (final LegalMove legalMove : allLegalMoves) {
      if (legalMove.kind() == LegalMoveKind.CASTLING) {
        filteredLegalMoves.add(legalMove);
      }
    }
    return filteredLegalMoves;
  }

  public static LegalMove calculateOnlyPossibleLegalMove(SanFormat sanFormat, SanConversion sanConversion,
      List<LegalMove> legalMovesForSanValidation) {

    final List<LegalMove> filtered0 = legalMovesForSanValidation;

    final List<LegalMove> filtered1 = filterLegalMovesCandidatesForFrom(sanFormat, sanConversion, filtered0);

    final List<LegalMove> filtered2 = filterLegalMovesCandidatesForPromotion(sanFormat, sanConversion, filtered1);

    final List<LegalMove> filtered3 = filterLegalMovesCandidatesForCastling(sanFormat, filtered2);

    if (filtered3.size() != 1) {
      throw new ProgrammingMistakeException(
          "At this point it is expected that filtering the legal moves against the SAN result in exactly one legal move");
    }
    return ListUtility.getOnly(filtered3);
  }

  public static void validateAgainstLegalMoves(Board board, Side havingMove, List<LegalMove> legalMovesCandidates,
      SanFormat sanFormat, SanConversion sanConversion) {

    final BitboardPosition bitboardPosition = board.getBitboardPosition();
    final Square epTarget = board.getEnPassantCaptureTargetSquare();
    final long epBit = epTarget == Square.NONE ? 0L : 1L << epTarget.ordinal();

    // we need an early return for castling first so for the remaining cases we can
    // calculate the to square
    if (sanFormat == SanFormat.KING_CASTLING_QUEEN_SIDE) {
      if (!isContained(legalMovesCandidates, havingMove, sanFormat)) {
        throwCastlingException(board, havingMove, "Queen-side", CastlingMove.QUEEN_SIDE);
      }
      return;
    }
    if (sanFormat == SanFormat.KING_CASTLING_KING_SIDE) {
      if (!isContained(legalMovesCandidates, havingMove, sanFormat)) {
        throwCastlingException(board, havingMove, "King-side", CastlingMove.KING_SIDE);
      }
      return;
    }

    // only in non castling case we can calculate the to square!
    final Square toSquare = sanConversion.toSquare();
    final PieceType pieceType = sanConversion.movingPieceType();

    switch (sanFormat) {
      case KING_CASTLING_QUEEN_SIDE, KING_CASTLING_KING_SIDE -> throw new ProgrammingMistakeException(
          "Invalid program flow, the castling must be handled at this point");
      case KING_NON_CASTLING_NON_CAPTURING, KING_NON_CASTLING_CAPTURING -> validateAgainstLegalMovesForKingNonCastling(
          bitboardPosition, havingMove, legalMovesCandidates, toSquare, epBit);
      case PAWN_NON_CAPTURING_NON_PROMOTION, PAWN_CAPTURING_NON_PROMOTION, PAWN_NON_CAPTURING_PROMOTION, PAWN_CAPTURING_PROMOTION -> validateAgainstLegalMovesForPawn(
          bitboardPosition, havingMove, legalMovesCandidates, pieceType, sanFormat, sanConversion, toSquare, epBit);
      case RNBQ_NON_CAPTURING_NEITHER, RNBQ_CAPTURING_NEITHER -> validateAgainstLegalMovesForPieceNeither(
          bitboardPosition, havingMove, legalMovesCandidates, pieceType, toSquare);
      case RNBQ_NON_CAPTURING_FILE, RNBQ_CAPTURING_FILE -> validateAgainstLegalMovesForPieceFile(bitboardPosition,
          havingMove, legalMovesCandidates, pieceType, sanFormat, sanConversion, toSquare);
      case RNBQ_NON_CAPTURING_RANK, RNBQ_CAPTURING_RANK -> validateAgainstLegalMovesForPieceRank(bitboardPosition,
          havingMove, legalMovesCandidates, pieceType, sanFormat, sanConversion, toSquare);
      case RNBQ_NON_CAPTURING_SQUARE, RNBQ_CAPTURING_SQUARE -> validateAgainstLegalMovesForPieceSquare(bitboardPosition,
          havingMove, legalMovesCandidates, pieceType, sanFormat, sanConversion, toSquare);
      default -> throw new IllegalArgumentException();
    }
  }

  private static void validateAgainstLegalMovesForKingNonCastling(BitboardPosition bitboardPosition, Side havingMove,
      List<LegalMove> legalMovesCandidates, Square toSquare, long epBit) {
    if (!legalMovesCandidates.isEmpty()) {
      return;
    }
    final Set<Square> pseudoLegalFromSquares = calculatePseudoLegalKingNonCastlingFromSquares(bitboardPosition,
        havingMove, toSquare, epBit);
    if (pseudoLegalFromSquares.isEmpty()) {
      throw new SanValidationException(SanValidationProblem.NOT_REACHABLE_KING_NON_CASTLING,
          Message.getString("validation.san.notReachable.king.nonCastling", toSquare.getName()));
    }
    // Pseudo-legal but not legal: classify via the bitboard king-classification helper. For king moves the safety
    // reasons (KING_CAPTURES_GUARDED_PIECE / KING_MOVES_NEXT_TO_OPPONENT_KING /
    // KING_MOVES_TO_ATTACKED_EMPTY_SQUARE) live in MovementCheck rather than the LEFT/EXPOSED
    // distinction used for non-king pieces.
    final MovementCheck movementCheck = classifyKingNonCastlingMovementCheck(bitboardPosition, havingMove, toSquare);
    switch (movementCheck) {
      case KING_CAPTURES_GUARDED_PIECE -> throw new SanValidationException(
          SanValidationProblem.KING_CAPTURES_GUARDED_PIECE,
          Message.getString("validation.san.king.capturesGuardedPiece", toSquare.getName()));
      case KING_MOVES_NEXT_TO_OPPONENT_KING -> throw new SanValidationException(
          SanValidationProblem.KING_MOVES_NEXT_TO_OPPONENT_KING,
          Message.getString("validation.san.king.movesNextToOpponentKing", toSquare.getName()));
      case KING_MOVES_TO_ATTACKED_EMPTY_SQUARE -> throw new SanValidationException(
          SanValidationProblem.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE,
          Message.getString("validation.san.king.movesToAttackedEmptySquare", toSquare.getName()));
      default -> throw new ProgrammingMistakeException(
          "Unexpected MovementCheck for king non-castling pseudo-legal move: " + movementCheck);
    }
  }

  /**
   * Bitboard sibling of the reference {@code ChessRuleAnalyzer.analyzeKing} king-safety branch - called only after SAN
   * validation has established that the king move (own king to {@code toSquare}) is pseudo-legal but not legal
   * (king-unsafe after the move). Returns the {@link MovementCheck} that classifies why. Precedence matches the
   * reference: NEXT_TO_OPPONENT_KING wins first, then CAPTURES_GUARDED_PIECE (opponent piece on destination), then
   * MOVES_TO_ATTACKED_EMPTY_SQUARE (empty destination, attacked).
   */
  private static MovementCheck classifyKingNonCastlingMovementCheck(BitboardPosition bitboardPosition, Side havingMove,
      Square toSquare) {
    final Square opponentKingSquare = bitboardPosition.kingSquare(havingMove.getOppositeSide());
    if ((KingAttacks.attacks(opponentKingSquare) & 1L << toSquare.ordinal()) != 0L) {
      return MovementCheck.KING_MOVES_NEXT_TO_OPPONENT_KING;
    }
    final Piece pieceOnTo = bitboardPosition.get(toSquare);
    if (pieceOnTo != Piece.NONE && pieceOnTo.getSide() == havingMove.getOppositeSide()) {
      return MovementCheck.KING_CAPTURES_GUARDED_PIECE;
    }
    return MovementCheck.KING_MOVES_TO_ATTACKED_EMPTY_SQUARE;
  }

  private static Set<Square> calculatePseudoLegalKingNonCastlingFromSquares(BitboardPosition bitboardPosition,
      Side havingMove, Square toSquare, long epBit) {
    final Set<Square> result = new TreeSet<>();
    for (final Square fromSquare : Square.REAL) {
      if (!bitboardPosition.isOwnPiece(fromSquare, havingMove, KING)) {
        continue;
      }
      if (!bitboardPosition.potentialToSquares(fromSquare, epBit).contains(toSquare)) {
        continue;
      }
      // Skip king-capture moves (toSquare has opponent king) to match reference semantics.
      final Piece pieceOnTo = bitboardPosition.get(toSquare);
      if (pieceOnTo != Piece.NONE && pieceOnTo.getPieceType() == KING) {
        continue;
      }
      final MoveSpecification spec = new MoveSpecification(fromSquare, toSquare);
      if (bitboardPosition.afterMove(spec, havingMove).isInCheck(havingMove)) {
        result.add(fromSquare);
      }
    }
    return result;
  }

  private static void validateAgainstLegalMovesForPawn(BitboardPosition bitboardPosition, Side havingMove,
      List<LegalMove> legalMovesCandidates, PieceType pieceType, SanFormat sanFormat, SanConversion sanConversion,
      Square toSquare, long epBit) {
    if (!legalMovesCandidates.isEmpty()) {
      return;
    }
    final boolean isCapturing = sanFormat == SanFormat.PAWN_CAPTURING_NON_PROMOTION
        || sanFormat == SanFormat.PAWN_CAPTURING_PROMOTION;
    final Set<Square> pseudoLegalFromSquares = calculatePseudoLegalPawnFromSquares(bitboardPosition, havingMove,
        isCapturing, sanConversion, toSquare, epBit);
    if (pseudoLegalFromSquares.isEmpty()) {
      if (isCapturing) {
        throw new SanValidationException(SanValidationProblem.NOT_REACHABLE_PAWN_CAPTURING,
            Message.getString("validation.san.notReachable.pawn.capturing", pieceType.getName(), toSquare.getName()));
      }
      throw new SanValidationException(SanValidationProblem.NOT_REACHABLE_PAWN_NON_CAPTURING,
          Message.getString("validation.san.notReachable.pawn.nonCapturing", pieceType.getName(), toSquare.getName()));
    }
    final KingSafetyCheck reason = calculatePseudoLegalKingSafety(bitboardPosition, havingMove);
    if (reason == KingSafetyCheck.NON_KING_LEFT_IN_CHECK) {
      throw new SanValidationException(SanValidationProblem.KING_LEFT_IN_CHECK_PAWN,
          Message.getString("validation.san.kingLeftInCheck.pawn", pieceType.getName(), toSquare.getName()));
    }
    throw new SanValidationException(SanValidationProblem.KING_EXPOSED_TO_CHECK_PAWN,
        Message.getString("validation.san.kingExposedToCheck.pawn", pieceType.getName(), toSquare.getName()));
  }

  private static Set<Square> calculatePseudoLegalPawnFromSquares(BitboardPosition bitboardPosition, Side havingMove,
      boolean isCapturing, SanConversion sanConversion, Square toSquare, long epBit) {
    final File filterFromFile = isCapturing ? sanConversion.fromFile() : toSquare.getFile();
    return calculatePseudoLegalFromSquaresOnFile(bitboardPosition, havingMove, PAWN, toSquare, epBit, filterFromFile);
  }

  private static void validateAgainstLegalMovesForPieceNeither(BitboardPosition bitboardPosition, Side havingMove,
      List<LegalMove> legalMovesCandidates, PieceType pieceType, Square toSquare) {
    if (legalMovesCandidates.isEmpty()) {
      final Set<Square> pseudoLegalFromSquares = calculatePseudoLegalFromSquaresAny(bitboardPosition, havingMove,
          pieceType, toSquare);

      if (pseudoLegalFromSquares.isEmpty()) {
        if (countPiecesOfType(bitboardPosition, havingMove, pieceType) == 1) {
          throw new SanValidationException(SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_SINGLE, Message
              .getString("validation.san.notReachable.rnbq.neither.single", pieceType.getName(), toSquare.getName()));
        }
        throw new SanValidationException(SanValidationProblem.NOT_REACHABLE_RNBQ_NEITHER_MULTIPLE, Message
            .getString("validation.san.notReachable.rnbq.neither.multiple", pieceType.getName(), toSquare.getName()));
      }
      final KingSafetyCheck reason = calculatePseudoLegalKingSafety(bitboardPosition, havingMove);
      if (pseudoLegalFromSquares.size() == 1) {
        final Square fromSquare = SetUtility.getOnly(pseudoLegalFromSquares);
        if (reason == KingSafetyCheck.NON_KING_LEFT_IN_CHECK) {
          throw new SanValidationException(SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_NEITHER_SINGLE,
              Message.getString("validation.san.kingLeftInCheck.rnbq.neither.single", pieceType.getName(),
                  fromSquare.getName(), toSquare.getName()));
        }
        throw new SanValidationException(SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_NEITHER_SINGLE,
            Message.getString("validation.san.kingExposedToCheck.rnbq.neither.single", pieceType.getName(),
                fromSquare.getName(), toSquare.getName()));
      }
      if (reason == KingSafetyCheck.NON_KING_LEFT_IN_CHECK) {
        throw new SanValidationException(SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_NEITHER_MULTIPLE,
            Message.getString("validation.san.kingLeftInCheck.rnbq.neither.multiple", pieceType.getName(),
                toSquare.getName()));
      }
      throw new SanValidationException(SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_NEITHER_MULTIPLE,
          Message.getString("validation.san.kingExposedToCheck.rnbq.neither.multiple", pieceType.getName(),
              toSquare.getName()));
    }
    if (legalMovesCandidates.size() > 1) {
      throw new SanValidationException(
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_NEITHER_EITHER_FILE_OR_RANK_OR_SQUARE_REQUIRED,
          Message.getString("validation.san.insufficientlySpecified.rnbq.neither.eitherFileOrRankOrSquareRequired",
              pieceType.getName(), toSquare.getName()));
    }
  }

  private static void throwCastlingException(Board board, Side havingMove, String sideLabel,
      CastlingMove castlingMove) {
    final CastlingRight castlingRight = board.getCastlingRight(havingMove);
    final CastlingCheck castlingCheck = castlingMove == CastlingMove.QUEEN_SIDE
        ? CastlingUtility.calculateQueenSideCastlingCheck(board.getBitboardPosition(), havingMove, castlingRight)
        : CastlingUtility.calculateKingSideCastlingCheck(board.getBitboardPosition(), havingMove, castlingRight);

    final CastlingRightLoss castlingRightLoss = board.getCastlingRightLoss(havingMove, castlingMove);
    final String message;

    switch (castlingCheck) {
      case FINAL_NO_RIGHT: {
        final String rookLabel = castlingMove == CastlingMove.QUEEN_SIDE ? "queen-side" : "king-side";
        message = switch (castlingRightLoss) {
          case KING_MOVED -> Message.getString("validation.san.kingCastling.finalNoRight.kingMoved", sideLabel);
          case ROOK_MOVED -> Message.getString("validation.san.kingCastling.finalNoRight.rookMoved", sideLabel,
              rookLabel);
          case ROOK_CAPTURED -> Message.getString("validation.san.kingCastling.finalNoRight.rookCaptured", sideLabel,
              rookLabel);
          case CASTLED -> Message.getString("validation.san.kingCastling.finalNoRight.castled", sideLabel);
          case UNKNOWN_FEN_IMPORT -> Message.getString("validation.san.kingCastling.finalNoRight.unknownFenImport",
              sideLabel);
          default -> throw new IllegalArgumentException();
        };
        break;
      }
      case TEMPORARY_SQUARES_NOT_EMPTY:
        message = Message.getString("validation.san.kingCastling.temporary.squaresNotEmpty", sideLabel);
        break;
      case TEMPORARY_KING_IN_CHECK:
        message = Message.getString("validation.san.kingCastling.temporary.kingInCheck", sideLabel);
        break;
      case TEMPORARY_KING_TRAVELS_THROUGH_CHECK:
        message = Message.getString("validation.san.kingCastling.temporary.kingTravelsThroughCheck", sideLabel);
        break;
      case TEMPORARY_KING_ENDS_IN_CHECK:
        message = Message.getString("validation.san.kingCastling.temporary.kingEndsInCheck", sideLabel);
        break;
      case SUCCESS:
        throw new ProgrammingMistakeException("Castling check returned SUCCESS but move is not in legal moves");
      default:
        throw new ProgrammingMistakeException("Unexpected castling check result: " + castlingCheck);
    }

    throw new SanValidationException(CastlingCheckMapper.map(castlingCheck, castlingRightLoss), message,
        castlingCheck.toMoveCheck(castlingRightLoss), castlingRightLoss);
  }

  private static KingSafetyCheck calculatePseudoLegalKingSafety(BitboardPosition bitboardPosition, Side havingMove) {
    // is check works because already narrowed down by legal move calculation to one or the other case
    if (bitboardPosition.isInCheck(havingMove)) {
      return KingSafetyCheck.NON_KING_LEFT_IN_CHECK;
    }
    return KingSafetyCheck.NON_KING_EXPOSED_TO_CHECK;
  }

  /**
   * Returns the from-squares of own {@code pieceType} pieces whose move to {@code toSquare} is pseudo-legal but
   * king-unsafe (i.e. it geometrically reaches {@code toSquare} but {@code afterMove(spec, side).isInCheck(side)}).
   * Mirrors the {@code AbstractLegalMoves.calculateLegalMoveCalculation(...).pseudoLegalMoveSet()} surface used by SAN
   * error reporting. Reference behavior: king captures (toSquare carries a king) are skipped.
   */
  private static Set<Square> calculatePseudoLegalFromSquaresAny(BitboardPosition bitboardPosition, Side havingMove,
      PieceType pieceType, Square toSquare) {
    return calculatePseudoLegalFromSquaresFiltered(bitboardPosition, havingMove, pieceType, toSquare, 0L, File.NONE,
        Rank.NONE);
  }

  private static Set<Square> calculatePseudoLegalFromSquaresOnFile(BitboardPosition bitboardPosition, Side havingMove,
      PieceType pieceType, Square toSquare, long epBit, File file) {
    return calculatePseudoLegalFromSquaresFiltered(bitboardPosition, havingMove, pieceType, toSquare, epBit, file,
        Rank.NONE);
  }

  private static Set<Square> calculatePseudoLegalFromSquaresOnRank(BitboardPosition bitboardPosition, Side havingMove,
      PieceType pieceType, Square toSquare, Rank rank) {
    return calculatePseudoLegalFromSquaresFiltered(bitboardPosition, havingMove, pieceType, toSquare, 0L, File.NONE,
        rank);
  }

  private static Set<Square> calculatePseudoLegalFromSquaresFiltered(BitboardPosition bitboardPosition, Side havingMove,
      PieceType pieceType, Square toSquare, long epBit, File fileFilter, Rank rankFilter) {
    final Set<Square> result = new TreeSet<>();
    for (final Square fromSquare : Square.REAL) {
      if (fileFilter != File.NONE && fromSquare.getFile() != fileFilter) {
        continue;
      }
      if (rankFilter != Rank.NONE && fromSquare.getRank() != rankFilter) {
        continue;
      }
      if (!bitboardPosition.isOwnPiece(fromSquare, havingMove, pieceType)) {
        continue;
      }
      if (!bitboardPosition.potentialToSquares(fromSquare, epBit).contains(toSquare)) {
        continue;
      }
      // Skip king-capture moves (toSquare carries opponent king) to match reference.
      final Piece pieceOnTo = bitboardPosition.get(toSquare);
      if (pieceOnTo != Piece.NONE && pieceOnTo.getPieceType() == KING) {
        continue;
      }
      final MoveSpecification spec = new MoveSpecification(fromSquare, toSquare);
      if (bitboardPosition.afterMove(spec, havingMove).isInCheck(havingMove)) {
        result.add(fromSquare);
      }
    }
    return result;
  }

  private static int countPiecesOfType(BitboardPosition bitboardPosition, Side havingMove, PieceType pieceType) {
      int count = 0;
    for (final Square square : Square.REAL) {
      if (bitboardPosition.isOwnPiece(square, havingMove, pieceType)) {
        count++;
      }
    }
    return count;
  }

  private static int countPiecesOfTypeOnFile(BitboardPosition bitboardPosition, Side havingMove, PieceType pieceType,
      File file) {
      int count = 0;
    for (final Square square : Square.REAL) {
      if (square.getFile() == file && bitboardPosition.isOwnPiece(square, havingMove, pieceType)) {
        count++;
      }
    }
    return count;
  }

  private static int countPiecesOfTypeOnRank(BitboardPosition bitboardPosition, Side havingMove, PieceType pieceType,
      Rank rank) {
      int count = 0;
    for (final Square square : Square.REAL) {
      if (square.getRank() == rank && bitboardPosition.isOwnPiece(square, havingMove, pieceType)) {
        count++;
      }
    }
    return count;
  }

  private static void validateAgainstLegalMovesForPieceFile(BitboardPosition bitboardPosition, Side havingMove,
      List<LegalMove> legalMovesCandidates, PieceType pieceType, SanFormat sanFormat, SanConversion sanConversion,
      Square toSquare) {
    final File fromFile = sanConversion.fromFile();
    final Set<Square> pieceCandidates = calculatePieceCandidateSquareSet(bitboardPosition, havingMove, pieceType,
        sanFormat, sanConversion);
    final Set<Square> movementCandidates = filterCandidateSquaresForPotentialMove(bitboardPosition, toSquare,
        pieceCandidates);
    if (movementCandidates.isEmpty()) {
      if (countPiecesOfTypeOnFile(bitboardPosition, havingMove, pieceType, fromFile) == 1) {
        final Square pieceSquare = SetUtility.getOnly(pieceCandidates);
        throw new SanValidationException(SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_SINGLE,
            Message.getString("validation.san.notReachable.rnbq.file.single", pieceType.getName(),
                pieceSquare.getName(), toSquare.getName()));
      }
      throw new SanValidationException(SanValidationProblem.NOT_REACHABLE_RNBQ_FILE_MULTIPLE,
          Message.getString("validation.san.notReachable.rnbq.file.multiple", pieceType.getName(),
              fromFile.getLetterString(), toSquare.getName()));
    }

    final int numberOfLegalMovesFromSameFile = calculateNumberOfLegalMovesFromFile(fromFile, legalMovesCandidates);
    if (numberOfLegalMovesFromSameFile == 0) {
      final Set<Square> pseudoLegalFromSquares = calculatePseudoLegalFromSquaresOnFile(bitboardPosition, havingMove,
          pieceType, toSquare, 0L, fromFile);
      final KingSafetyCheck reason = calculatePseudoLegalKingSafety(bitboardPosition, havingMove);
      if (pseudoLegalFromSquares.size() == 1) {
        final Square pieceSquare = SetUtility.getOnly(pseudoLegalFromSquares);
        if (reason == KingSafetyCheck.NON_KING_LEFT_IN_CHECK) {
          throw new SanValidationException(SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_FILE_SINGLE,
              Message.getString("validation.san.kingLeftInCheck.rnbq.file.single", pieceType.getName(),
                  pieceSquare.getName(), toSquare.getName()));
        }
        throw new SanValidationException(SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_FILE_SINGLE,
            Message.getString("validation.san.kingExposedToCheck.rnbq.file.single", pieceType.getName(),
                pieceSquare.getName(), toSquare.getName()));
      }
      if (reason == KingSafetyCheck.NON_KING_LEFT_IN_CHECK) {
        throw new SanValidationException(SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_FILE_MULTIPLE,
            Message.getString("validation.san.kingLeftInCheck.rnbq.file.multiple", pieceType.getName(),
                fromFile.getLetterString(), toSquare.getName()));
      }
      throw new SanValidationException(SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_FILE_MULTIPLE,
          Message.getString("validation.san.kingExposedToCheck.rnbq.file.multiple", pieceType.getName(),
              fromFile.getLetterString(), toSquare.getName()));
    }

    if (legalMovesCandidates.size() == 1) {
      throw new SanValidationException(SanValidationProblem.OVERSPECIFIED_RNBQ_FILE_ONLY_ONE_LEGAL_MOVE,
          Message.getString("validation.san.overspecified.rnbq.file.onlyOneLegalMove"));
    }

    if (!calculateHasOtherFilesHavingLegalMoves(sanConversion.fromFile(), legalMovesCandidates)) {
      if (numberOfLegalMovesFromSameFile < 2) {
        throw new ProgrammingMistakeException("A programming assumption about the rank turned out to be wrong");
      }
      throw new SanValidationException(SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED,
          Message.getString("validation.san.insufficientlySpecified.rnbq.file.rankRequired", pieceType.getName(),
              sanConversion.fromFile().getLetterString(), toSquare.getName()));
    }

    if (numberOfLegalMovesFromSameFile >= 2) {
      if (pieceType == ROOK) {
        throw new SanValidationException(SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_RANK_REQUIRED,
            Message.getString("validation.san.insufficientlySpecified.rnbq.file.rankRequired", pieceType.getName(),
                sanConversion.fromFile().getLetterString(), toSquare.getName()));
      }
      throw new SanValidationException(
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_FILE_EITHER_RANK_OR_SQUARE_REQUIRED,
          Message.getString("validation.san.insufficientlySpecified.rnbq.file.eitherRankOrSquareRequired",
              pieceType.getName(), sanConversion.fromFile().getLetterString(), toSquare.getName()));
    }
  }

  private static void validateAgainstLegalMovesForPieceRank(BitboardPosition bitboardPosition, Side havingMove,
      List<LegalMove> legalMovesCandidates, PieceType pieceType, SanFormat sanFormat, SanConversion sanConversion,
      Square toSquare) {
    final Rank fromRank = sanConversion.fromRank();
    final Set<Square> pieceCandidates = calculatePieceCandidateSquareSet(bitboardPosition, havingMove, pieceType,
        sanFormat, sanConversion);
    final Set<Square> movementCandidates = filterCandidateSquaresForPotentialMove(bitboardPosition, toSquare,
        pieceCandidates);
    if (movementCandidates.isEmpty()) {
      if (countPiecesOfTypeOnRank(bitboardPosition, havingMove, pieceType, fromRank) == 1) {
        final Square pieceSquare = SetUtility.getOnly(pieceCandidates);
        throw new SanValidationException(SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_SINGLE,
            Message.getString("validation.san.notReachable.rnbq.rank.single", pieceType.getName(),
                pieceSquare.getName(), toSquare.getName()));
      }
      throw new SanValidationException(SanValidationProblem.NOT_REACHABLE_RNBQ_RANK_MULTIPLE,
          Message.getString("validation.san.notReachable.rnbq.rank.multiple", pieceType.getName(),
              Nulls.valueOf(fromRank.getNumber()), toSquare.getName()));
    }

    final int numberOfLegalMovesFromSameRank = calculateNumberOfLegalMovesFromRank(fromRank, legalMovesCandidates);
    if (numberOfLegalMovesFromSameRank == 0) {
      final Set<Square> pseudoLegalFromSquares = calculatePseudoLegalFromSquaresOnRank(bitboardPosition, havingMove,
          pieceType, toSquare, fromRank);
      final KingSafetyCheck reason = calculatePseudoLegalKingSafety(bitboardPosition, havingMove);
      if (pseudoLegalFromSquares.size() == 1) {
        final Square pieceSquare = SetUtility.getOnly(pseudoLegalFromSquares);
        if (reason == KingSafetyCheck.NON_KING_LEFT_IN_CHECK) {
          throw new SanValidationException(SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_RANK_SINGLE,
              Message.getString("validation.san.kingLeftInCheck.rnbq.rank.single", pieceType.getName(),
                  pieceSquare.getName(), toSquare.getName()));
        }
        throw new SanValidationException(SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_RANK_SINGLE,
            Message.getString("validation.san.kingExposedToCheck.rnbq.rank.single", pieceType.getName(),
                pieceSquare.getName(), toSquare.getName()));
      }
      if (reason == KingSafetyCheck.NON_KING_LEFT_IN_CHECK) {
        throw new SanValidationException(SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_RANK_MULTIPLE,
            Message.getString("validation.san.kingLeftInCheck.rnbq.rank.multiple", pieceType.getName(),
                Nulls.valueOf(fromRank.getNumber()), toSquare.getName()));
      }
      throw new SanValidationException(SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_RANK_MULTIPLE,
          Message.getString("validation.san.kingExposedToCheck.rnbq.rank.multiple", pieceType.getName(),
              Nulls.valueOf(fromRank.getNumber()), toSquare.getName()));
    }

    if (legalMovesCandidates.size() == 1) {
      throw new SanValidationException(SanValidationProblem.OVERSPECIFIED_RNBQ_RANK_ONLY_ONE_LEGAL_MOVE,
          Message.getString("validation.san.overspecified.rnbq.rank.onlyOneLegalMove"));
    }

    if (!calculateHasOtherRanksHavingLegalMoves(sanConversion.fromRank(), legalMovesCandidates)) {
      if (numberOfLegalMovesFromSameRank < 2) {
        throw new ProgrammingMistakeException("A programming assumption about the file turned out to be wrong");
      }
      throw new SanValidationException(SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_FILE_REQUIRED,
          Message.getString("validation.san.insufficientlySpecified.rnbq.rank.fileRequired", pieceType.getName(),
              Nulls.valueOf(sanConversion.fromRank().getNumber()), toSquare.getName()));
    }

    if (numberOfLegalMovesFromSameRank >= 2) {
      if (pieceType == ROOK) {
        throw new SanValidationException(SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_FILE_REQUIRED,
            Message.getString("validation.san.insufficientlySpecified.rnbq.rank.fileRequired", pieceType.getName(),
                sanConversion.fromFile().getLetterString(), toSquare.getName()));
      }
      throw new SanValidationException(
          SanValidationProblem.INSUFFICIENTLY_SPECIFIED_RNBQ_RANK_EITHER_FILE_OR_SQUARE_REQUIRED,
          Message.getString("validation.san.insufficientlySpecified.rnbq.rank.eitherFileOrSquareRequired",
              pieceType.getName(), Nulls.valueOf(sanConversion.fromRank().getNumber()), toSquare.getName()));
    }

    final File onlyPossibleFromFile = calculateOnlyPossibleFile(legalMovesCandidates, sanConversion);
    if (onlyPossibleFromFile == File.NONE) {
      throw new ProgrammingMistakeException(
          "The program made the wrong assumption that the from file is determined at this point");
    }
    final int numberOfLegalMovesFromSameFile = calculateNumberOfLegalMovesFromFile(onlyPossibleFromFile,
        legalMovesCandidates);

    if (numberOfLegalMovesFromSameFile == 1) {
      throw new SanValidationException(SanValidationProblem.NON_STANDARD_SPECIFIED_RNBQ_RANK_INSTEAD_OF_FILE,
          Message.getString("validation.san.nonStandardSpecified.rnbq.rank.rankInsteadOfFile"));
    }
  }

  private static void validateAgainstLegalMovesForPieceSquare(BitboardPosition bitboardPosition, Side havingMove,
      List<LegalMove> legalMovesCandidates, PieceType pieceType, SanFormat sanFormat, SanConversion sanConversion,
      Square toSquare) {
    final Square fromSquare = calculateFromSquare(sanConversion);
    final Set<Square> pieceCandidates = calculatePieceCandidateSquareSet(bitboardPosition, havingMove, pieceType,
        sanFormat, sanConversion);
    final Set<Square> movementCandidates = filterCandidateSquaresForPotentialMove(bitboardPosition, toSquare,
        pieceCandidates);
    if (movementCandidates.isEmpty()) {
      throw new SanValidationException(SanValidationProblem.NOT_REACHABLE_RNBQ_SQUARE, Message.getString(
          "validation.san.notReachable.rnbq.square", pieceType.getName(), fromSquare.getName(), toSquare.getName()));
    }
    if (calculateNumberOfLegalMovesFromSquare(fromSquare, legalMovesCandidates) == 0) {
      final KingSafetyCheck reason = calculatePseudoLegalKingSafety(bitboardPosition, havingMove);
      if (reason == KingSafetyCheck.NON_KING_LEFT_IN_CHECK) {
        throw new SanValidationException(SanValidationProblem.KING_LEFT_IN_CHECK_RNBQ_SQUARE,
            Message.getString("validation.san.kingLeftInCheck.rnbq.square", pieceType.getName(), fromSquare.getName(),
                toSquare.getName()));
      }
      throw new SanValidationException(SanValidationProblem.KING_EXPOSED_TO_CHECK_RNBQ_SQUARE,
          Message.getString("validation.san.kingExposedToCheck.rnbq.square", pieceType.getName(), fromSquare.getName(),
              toSquare.getName()));
    }

    if (legalMovesCandidates.size() == 1) {
      throw new SanValidationException(SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_ONLY_ONE_LEGAL_MOVE,
          Message.getString("validation.san.overspecified.rnbq.square.onlyOneLegalMove"));
    }

    final int numberOfLegalMovesFromOtherFiles = calculateNumberOfLegalMovesFromOtherFiles(sanConversion.fromFile(),
        legalMovesCandidates);

    final int numberOfLegalMovesFromFile = calculateNumberOfLegalMovesFromFile(sanConversion.fromFile(),
        legalMovesCandidates);

    if (numberOfLegalMovesFromFile == 2 && numberOfLegalMovesFromOtherFiles == 0) {
      throw new SanValidationException(SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_FILE_NOT_NECESSARY,
          Message.getString("validation.san.overspecified.rnbq.square.fileNotNecessary"));
    }

    if (numberOfLegalMovesFromFile == 1 && numberOfLegalMovesFromOtherFiles >= 1) {
      throw new SanValidationException(SanValidationProblem.OVERSPECIFIED_RNBQ_SQUARE_RANK_NOT_NECESSARY,
          Message.getString("validation.san.overspecified.rnbq.square.rankNotNecessary"));
    }
  }

  private static Set<Square> calculatePieceCandidateSquareSet(BitboardPosition bitboardPosition, Side havingMove,
      PieceType pieceType, SanFormat sanFormat, SanConversion sanConversion) {
    final Set<Square> result = new TreeSet<>();
    for (final Square square : Square.REAL) {
      if (!bitboardPosition.isOwnPiece(square, havingMove, pieceType)) {
        continue;
      }
      switch (sanFormat) {
        case RNBQ_NON_CAPTURING_NEITHER:
        case RNBQ_CAPTURING_NEITHER:
          result.add(square);
          break;
        case RNBQ_NON_CAPTURING_FILE:
        case RNBQ_CAPTURING_FILE:
          if (square.getFile() == sanConversion.fromFile()) {
            result.add(square);
          }
          break;
        case RNBQ_NON_CAPTURING_RANK:
        case RNBQ_CAPTURING_RANK:
          if (square.getRank() == sanConversion.fromRank()) {
            result.add(square);
          }
          break;
        case RNBQ_NON_CAPTURING_SQUARE:
        case RNBQ_CAPTURING_SQUARE:
          if (square == calculateFromSquare(sanConversion)) {
            result.add(square);
          }
          break;
        default:
          throw new IllegalArgumentException();
      }
    }
    return result;
  }

  private static Set<Square> filterCandidateSquaresForPotentialMove(BitboardPosition bitboardPosition, Square toSquare,
      Set<Square> candidateSquares) {
    final Set<Square> result = new TreeSet<>();
    for (final Square candidateSquare : candidateSquares) {
      final Set<Square> potentialToSquares = bitboardPosition.potentialToSquares(candidateSquare, 0L);
      if (potentialToSquares.contains(toSquare)) {
        result.add(candidateSquare);
      }
    }
    return result;
  }

  private static List<LegalMove> filterLegalMovesCandidatesForFrom(SanFormat sanFormat, SanConversion sanConversion,
      List<LegalMove> legalMoveSet) {

    switch (sanFormat) {
      case KING_CASTLING_QUEEN_SIDE:
      case KING_CASTLING_KING_SIDE:
      case KING_NON_CASTLING_CAPTURING:
      case KING_NON_CASTLING_NON_CAPTURING:
        // no from restriction
        return new ArrayList<>(legalMoveSet);
      // $CASES-OMITTED$
      default:
        break;
    }

    final List<LegalMove> legalMovesForFrom = new ArrayList<>();

    // always set for non castling
    final File sanFromFile = sanConversion.fromFile();
    // attention - empty for some san formats
    final Rank sanFromRank = sanConversion.fromRank();

    for (final LegalMove moveCandidate : legalMoveSet) {
      final File candidateFromFile = moveCandidate.moveSpecification().fromSquare().getFile();
      final Rank candidateFromRank = moveCandidate.moveSpecification().fromSquare().getRank();

      final boolean isFromFileMatch = candidateFromFile == sanFromFile;
      // attention does not make sense for all san formats
      final boolean isFromRankMatch = candidateFromRank == sanFromRank;

      switch (sanFormat) {
        case KING_CASTLING_QUEEN_SIDE:
        case KING_CASTLING_KING_SIDE:
        case KING_NON_CASTLING_CAPTURING:
        case KING_NON_CASTLING_NON_CAPTURING:
          throw new ProgrammingMistakeException("Handled before");
        case PAWN_CAPTURING_NON_PROMOTION:
        case PAWN_CAPTURING_PROMOTION:
          if (isFromFileMatch) {
            legalMovesForFrom.add(moveCandidate);
          }
          break;
        case PAWN_NON_CAPTURING_NON_PROMOTION:
        case PAWN_NON_CAPTURING_PROMOTION:
          // no from restriction
          legalMovesForFrom.add(moveCandidate);
          break;
        case RNBQ_NON_CAPTURING_NEITHER:
        case RNBQ_CAPTURING_NEITHER:
          // no from restriction
          legalMovesForFrom.add(moveCandidate);
          break;
        case RNBQ_NON_CAPTURING_FILE:
        case RNBQ_CAPTURING_FILE:
          if (isFromFileMatch) {
            legalMovesForFrom.add(moveCandidate);
          }
          break;
        case RNBQ_NON_CAPTURING_RANK:
        case RNBQ_CAPTURING_RANK:
          if (isFromRankMatch) {
            legalMovesForFrom.add(moveCandidate);
          }
          break;
        case RNBQ_NON_CAPTURING_SQUARE:
        case RNBQ_CAPTURING_SQUARE:
          if (isFromFileMatch && isFromRankMatch) {
            legalMovesForFrom.add(moveCandidate);
          }
          break;
        default:
          throw new IllegalArgumentException();
      }
    }
    return legalMovesForFrom;
  }

  private static List<LegalMove> filterLegalMovesCandidatesForPromotion(SanFormat sanFormat,
      SanConversion sanConversion, List<LegalMove> legalMoveSet) {
    final List<LegalMove> legalMovesForPromotion = new ArrayList<>();
    for (final LegalMove moveCandidate : legalMoveSet) {
      switch (sanFormat) {
        case KING_CASTLING_KING_SIDE:
        case KING_CASTLING_QUEEN_SIDE:
        case KING_NON_CASTLING_CAPTURING:
        case KING_NON_CASTLING_NON_CAPTURING:
        case PAWN_CAPTURING_NON_PROMOTION:
        case PAWN_NON_CAPTURING_NON_PROMOTION:
        case RNBQ_CAPTURING_SQUARE:
        case RNBQ_CAPTURING_FILE:
        case RNBQ_CAPTURING_NEITHER:
        case RNBQ_CAPTURING_RANK:
        case RNBQ_NON_CAPTURING_SQUARE:
        case RNBQ_NON_CAPTURING_FILE:
        case RNBQ_NON_CAPTURING_NEITHER:
        case RNBQ_NON_CAPTURING_RANK:
          legalMovesForPromotion.add(moveCandidate);
          break;
        case PAWN_CAPTURING_PROMOTION:
        case PAWN_NON_CAPTURING_PROMOTION:
          if (moveCandidate.moveSpecification().promotionPieceType() == sanConversion.promotionPieceType()) {
            legalMovesForPromotion.add(moveCandidate);
          }
          break;
        default:
          throw new IllegalArgumentException();
      }
    }
    return legalMovesForPromotion;
  }

  private static List<LegalMove> filterLegalMovesCandidatesForCastling(SanFormat sanFormat,
      List<LegalMove> legalMoveSet) {
    final List<LegalMove> legalMovesForCastling = new ArrayList<>();
    for (final LegalMove moveCandidate : legalMoveSet) {
      switch (sanFormat) {
        case KING_CASTLING_KING_SIDE:
          if (moveCandidate.moveSpecification().castlingMove() == CastlingMove.KING_SIDE) {
            legalMovesForCastling.add(moveCandidate);
          }
          break;
        case KING_CASTLING_QUEEN_SIDE:
          if (moveCandidate.moveSpecification().castlingMove() == CastlingMove.QUEEN_SIDE) {
            legalMovesForCastling.add(moveCandidate);
          }
          break;
        case KING_NON_CASTLING_CAPTURING:
        case KING_NON_CASTLING_NON_CAPTURING:
        case PAWN_CAPTURING_NON_PROMOTION:
        case PAWN_NON_CAPTURING_NON_PROMOTION:
        case RNBQ_CAPTURING_SQUARE:
        case RNBQ_CAPTURING_FILE:
        case RNBQ_CAPTURING_NEITHER:
        case RNBQ_CAPTURING_RANK:
        case RNBQ_NON_CAPTURING_SQUARE:
        case RNBQ_NON_CAPTURING_FILE:
        case RNBQ_NON_CAPTURING_NEITHER:
        case RNBQ_NON_CAPTURING_RANK:
        case PAWN_CAPTURING_PROMOTION:
        case PAWN_NON_CAPTURING_PROMOTION:
          legalMovesForCastling.add(moveCandidate);
          break;
        default:
          throw new IllegalArgumentException();
      }
    }
    return legalMovesForCastling;
  }

  private static File calculateOnlyPossibleFile(List<LegalMove> legalMovesForSanValidation,
      SanConversion sanConversion) {
      int countMatches = 0;
    for (final LegalMove legalMove : legalMovesForSanValidation) {
      if (legalMove.moveSpecification().fromSquare().getRank() == sanConversion.fromRank()) {
        countMatches++;
      }
    }
    if (countMatches != 1) {
      throw new ProgrammingMistakeException(
          "The program made the wrong assumption that the from file is determined at this point");
    }

    // now return first match, which is the only match
    for (final LegalMove legalMove : legalMovesForSanValidation) {
      if (legalMove.moveSpecification().fromSquare().getRank() == sanConversion.fromRank()) {
        return legalMove.moveSpecification().fromSquare().getFile();
      }
    }
    throw new ProgrammingMistakeException("The program in mistake failed to determine the file");
  }

  private static boolean isContained(List<LegalMove> legalMoves, Side havingMove, SanFormat sanFormat) {
    return legalMoves.contains(calculateCastlingMove(havingMove, sanFormat));
  }

  private static LegalMove calculateCastlingMove(Side havingMove, SanFormat sanFormat) {
    switch (havingMove) {
      case WHITE:
        if (sanFormat == SanFormat.KING_CASTLING_KING_SIDE) {
          return CastlingConstants.WHITE_KING_SIDE_CASTLING_MOVE;
        }
        if (sanFormat == SanFormat.KING_CASTLING_QUEEN_SIDE) {
          return CastlingConstants.WHITE_QUEEN_SIDE_CASTLING_MOVE;
        }
        throw new IllegalArgumentException();
      case BLACK:
        if (sanFormat == SanFormat.KING_CASTLING_KING_SIDE) {
          return CastlingConstants.BLACK_KING_SIDE_CASTLING_MOVE;
        }
        if (sanFormat == SanFormat.KING_CASTLING_QUEEN_SIDE) {
          return CastlingConstants.BLACK_QUEEN_SIDE_CASTLING_MOVE;
        }
        throw new IllegalArgumentException();
      case NONE:
      default:
        throw new IllegalArgumentException();
    }
  }
}
