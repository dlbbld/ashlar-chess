package com.dlb.chess.pgn;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.common.Nulls;
import com.dlb.chess.common.enums.GameStatus;
import com.dlb.chess.common.exceptions.UsageException;
import com.dlb.chess.san.ForgivenItem;
import com.dlb.chess.san.SanValidationProblem;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("null")
public class LenientPgnParserValidationException extends UsageException {

  private final LenientPgnParserValidationProblem lenientPgnParserValidationProblem;

  private final SanValidationProblem sanValidationProblem;

  /**
   * The {@link GameStatus} that ended the game when {@link #sanValidationProblem} is
   * {@link SanValidationProblem#GAME_ALREADY_ENDED}; {@code null} otherwise. Carried so the caller can distinguish the
   * enforced move-blocking terminations (checkmate, stalemate, mutual insufficient material) without having to parse
   * the human-readable message.
   */
  private final @Nullable GameStatus gameStatus;

  /**
   * SAN-level forgiven items accumulated during movetext replay before the failure point. Empty if the failure occurred
   * outside the movetext path (tag validation, structural error) or if no SAN deviation had been forgiven yet.
   */
  private final @NonNull ImmutableList<@NonNull ForgivenItem> sanForgivenItemsAccumulated;

  /**
   * Tag-level forgiven items accumulated before the failure point. Tag-level forgiveness happens after tag parsing and
   * before movetext replay, so this list is empty for any failure that originated in the tag section itself, and
   * fully-populated for any failure that originated downstream (in the movetext).
   */
  private final @NonNull ImmutableList<@NonNull ForgivenTagItem> tagForgivenItemsAccumulated;

  public LenientPgnParserValidationException(LenientPgnParserValidationProblem lenientPgnParserValidationProblem,
      SanValidationProblem sanValidationProblem, String message) {
    this(lenientPgnParserValidationProblem, sanValidationProblem, message, null, ImmutableList.of(),
        ImmutableList.of());
  }

  /**
   * Constructor used when a SAN validation failure carries a specific {@link GameStatus} (the GAME_ALREADY_ENDED case):
   * the parser propagates the status so callers can react to the specific termination cause without parsing the
   * message.
   */
  public LenientPgnParserValidationException(LenientPgnParserValidationProblem lenientPgnParserValidationProblem,
      SanValidationProblem sanValidationProblem, String message, @Nullable GameStatus gameStatus) {
    this(lenientPgnParserValidationProblem, sanValidationProblem, message, gameStatus, ImmutableList.of(),
        ImmutableList.of());
  }

  /**
   * Constructor used when the failure occurs during movetext replay and SAN-level forgiven items have already been
   * accumulated for earlier moves. Carries the accumulated SAN-level and tag-level items so callers can see partial
   * diagnostic data on failure.
   */
  public LenientPgnParserValidationException(LenientPgnParserValidationProblem lenientPgnParserValidationProblem,
      SanValidationProblem sanValidationProblem, String message, @Nullable GameStatus gameStatus,
      @NonNull ImmutableList<@NonNull ForgivenItem> sanForgivenItemsAccumulated,
      @NonNull ImmutableList<@NonNull ForgivenTagItem> tagForgivenItemsAccumulated) {
    super(message);
    this.lenientPgnParserValidationProblem = lenientPgnParserValidationProblem;
    this.sanValidationProblem = sanValidationProblem;
    this.gameStatus = gameStatus;
    this.sanForgivenItemsAccumulated = Nulls.copyOfList(sanForgivenItemsAccumulated);
    this.tagForgivenItemsAccumulated = Nulls.copyOfList(tagForgivenItemsAccumulated);
  }

  public LenientPgnParserValidationProblem getLenientPgnParserValidationProblem() {
    return lenientPgnParserValidationProblem;
  }

  public SanValidationProblem getSanValidationProblem() {
    return sanValidationProblem;
  }

  /**
   * The {@link GameStatus} that ended the game when {@link #getSanValidationProblem()} is
   * {@link SanValidationProblem#GAME_ALREADY_ENDED}; {@code null} otherwise.
   */
  public @Nullable GameStatus getGameStatus() {
    return gameStatus;
  }

  public @NonNull ImmutableList<@NonNull ForgivenItem> getSanForgivenItemsAccumulated() {
    return sanForgivenItemsAccumulated;
  }

  public @NonNull ImmutableList<@NonNull ForgivenTagItem> getTagForgivenItemsAccumulated() {
    return tagForgivenItemsAccumulated;
  }

}
