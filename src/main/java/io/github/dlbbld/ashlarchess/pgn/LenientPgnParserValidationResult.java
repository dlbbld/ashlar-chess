// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.san.ForgivenItem;
import io.github.dlbbld.ashlarchess.san.SanValidationProblem;

/**
 * Outcome of a lenient PGN parse-with-validation. On success, {@link #pgnGame} carries the parsed model and the two
 * forgiven-items channels list every deviation the lenient layer tolerated without rejecting:
 * <ul>
 * <li>{@link #sanForgivenItems} - SAN-level deviations during movetext replay (e.g. {@code e2-e4} normalised to
 * {@code e4}, redundant disambiguation, bogus check suffix).</li>
 * <li>{@link #tagForgivenItems} - tag-level deviations in the header (e.g. missing Seven Tag Roster entries, Result tag
 * absent, FEN without SetUp). The parse model preserves all inputs as-given; archival-mode export via
 * {@code WriteMode.ARCHIVAL} is the path that produces a normalised PGN.</li>
 * </ul>
 * Both lists are empty when the input was already canonical. On failure, {@link #pgnGame} is {@code null} and the two
 * lists contain whatever was accumulated up to the failure point.
 */
@SuppressWarnings("null")
public record LenientPgnParserValidationResult(@NonNull LenientPgnParserValidationProblem problemParser,
    @NonNull SanValidationProblem problemSan, @NonNull String message, @Nullable PgnGame pgnGame,
    @NonNull ImmutableList<@NonNull ForgivenItem> sanForgivenItems,
    @NonNull ImmutableList<@NonNull ForgivenTagItem> tagForgivenItems) {

  public LenientPgnParserValidationResult {
    sanForgivenItems = Nulls.copyOfList(sanForgivenItems);
    tagForgivenItems = Nulls.copyOfList(tagForgivenItems);
  }

  public boolean isValid() {
    return problemParser == LenientPgnParserValidationProblem.OK;
  }
}