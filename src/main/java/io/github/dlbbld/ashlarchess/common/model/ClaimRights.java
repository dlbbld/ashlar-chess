// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.model;

import java.util.List;

import io.github.dlbbld.ashlarchess.common.Nulls;

/**
 * Snapshot of the side-to-move's right to claim a draw under one specific FIDE rule (either 9.2 threefold or 9.3 fifty-
 * move), expressed as the list of legal moves that, if announced before being played, would satisfy the rule.
 *
 * <p>
 * {@code canClaim} is the rule-existence boolean: {@code true} iff {@code claimableMoves} is non-empty. The compact
 * constructor enforces this invariant so the two fields cannot disagree.
 *
 * <p>
 * {@code claimableMoves} is defensively copied to an immutable list at construction; callers cannot mutate it after the
 * fact. Move order follows the order of {@code Board.getLegalMoves()} at the time the rights were calculated.
 *
 * <p>
 * Produced by {@code Board.calculateFiftyMoveRuleClaimRights()} and
 * {@code Board.calculateThreefoldRepetitionRuleClaimRights()}.
 */
public record ClaimRights(boolean canClaim, List<ClaimableMove> claimableMoves) {

  public ClaimRights {
    claimableMoves = Nulls.copyOfList(claimableMoves);
    if (canClaim == claimableMoves.isEmpty()) {
      throw new IllegalArgumentException("canClaim must equal !claimableMoves.isEmpty(); got canClaim=" + canClaim
          + ", claimableMoves.size()=" + claimableMoves.size());
    }
  }
}
