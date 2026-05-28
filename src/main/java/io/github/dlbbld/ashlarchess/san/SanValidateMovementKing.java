package io.github.dlbbld.ashlarchess.san;

/**
 * Intentional no-op validator for king SAN movement at the abstract movement layer.
 *
 * <p>
 * The SAN format already constrains king moves sufficiently for this layer. Position-dependent king legality such as
 * moving into check, moving next to the opponent king, or castling restrictions is validated later against the actual
 * board state.
 */
abstract class SanValidateMovementKing extends AbstractSan {

  public static void validateKingMovement(@SuppressWarnings("unused") SanParse sanParse) {
    // intentional no-op
  }

}
