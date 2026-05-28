package io.github.dlbbld.ashlarchess.pgn;

/**
 * Tag-level deviations the lenient PGN parser accepts. Surfaced on the validation result so consumers can see what the
 * parser tolerated without rejecting. None of these deviations alter the parsed model - the parser preserves input as
 * given; archival-mode export {@code WriteMode.ARCHIVAL} is the path that produces a normalised PGN.
 */
public enum ForgivenTagItemCode {

  /**
   * A Seven Tag Roster entry other than Result was absent. Emitted once per missing tag; the tag name is on
   * {@link ForgivenTagItem#tagName()}. Result has its own dedicated codes below (the termination marker interaction
   * makes it a richer case).
   */
  STR_TAG_MISSING,

  /**
   * The Result tag was absent in the tag section but the movetext provided a game-termination marker. The marker value
   * is on {@link ForgivenTagItem#detail()}. Archival-mode export synthesises a Result tag from the marker.
   */
  RESULT_TAG_MISSING_BUT_TERMINATION_MARKER_PRESENT,

  /**
   * Neither the Result tag nor a termination marker was provided. Archival-mode export defaults the Result tag value to
   * {@code *} per PGN spec section 8.1.1.7.
   */
  RESULT_TAG_AND_TERMINATION_MARKER_BOTH_MISSING,

  /**
   * The FEN tag was present but no SetUp tag was given. Archival-mode export emits the canonical pair {@code SetUp "1"}
   * + the existing FEN.
   */
  SETUP_TAG_MISSING_BUT_FEN_PRESENT,

  /**
   * The SetUp tag was present (with any value) but no FEN tag was given. Archival-mode export drops the SetUp tag since
   * the startFen is then the implicit initial position.
   */
  SETUP_TAG_PRESENT_BUT_FEN_MISSING,

  /**
   * Both FEN and SetUp tags were present and the FEN value describes the initial position. The signals are redundant
   * (the initial position is the implicit default). Archival-mode export drops them.
   */
  REDUNDANT_FEN_AND_SETUP_FOR_INITIAL_POSITION,

}
