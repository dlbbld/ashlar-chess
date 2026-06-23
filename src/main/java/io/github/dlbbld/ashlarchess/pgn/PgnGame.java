// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.fen.model.Fen;
import io.github.dlbbld.ashlarchess.internal.Nulls;
import io.github.dlbbld.ashlarchess.pgn.internal.TagUtility;

/**
 * Parsed PGN model. Reflects what the source actually contained - tag presence/absence and tag order are preserved by
 * the parsers. The {@code terminationMarker} is the movetext game-termination marker (`1-0`, `0-1`, `1/2-1/2`, `*`) and
 * is independent of any {@code Result} tag in {@link #tags()}: both, either, or neither may be present in
 * lenient-parsed input, while strict-parsed input always has both (and they match).
 *
 * <p>
 * Invariant: when a Result tag is present in {@link #tags()} <em>and</em> {@link #terminationMarker()} is non-null, the
 * two must agree. The lenient and strict PGN parsers enforce this before constructing the {@code PgnGame} (via the
 * cross-signal consistency check); the {@code Board}-to-{@code PgnGame} path
 * ({@link PgnCreate#createPgnGame(io.github.dlbbld.ashlarchess.board.Board, java.util.List)}) is also guarded here by
 * the compact constructor - a caller that supplies a Result tag disagreeing with the board's game-status-derived marker
 * triggers an {@link IllegalArgumentException} rather than silently producing an internally inconsistent model that
 * archival export would then have to choose between.
 *
 * <p>
 * Don't use to construct PgnGame's on your own, intended as a parser result only, so holding valid data.
 */
@SuppressWarnings("null")
public record PgnGame(@NonNull List<@NonNull Tag> tags, @NonNull Fen startFen, @NonNull PgnCommentary pregameCommentary,
    @NonNull List<@NonNull PgnMove> moves, @Nullable ResultTagValue terminationMarker) {

  public PgnGame {
    tags = Nulls.copyOfList(tags);
    moves = Nulls.copyOfList(moves);
    if (terminationMarker != null && TagUtility.hasResult(tags)) {
      final String resultValue = TagUtility.readResult(tags);
      if (ResultTagValue.exists(resultValue)) {
        final ResultTagValue fromTag = ResultTagValue.parse(resultValue);
        if (fromTag != terminationMarker) {
          throw new IllegalArgumentException("The Result tag value \"" + resultValue
              + "\" disagrees with the termination marker \"" + terminationMarker.getValue()
              + "\". Both signals must agree when both are present; the lenient and strict parsers enforce this"
              + " before constructing PgnGame, and the Board-to-PgnGame path must pass a tag list consistent with"
              + " the board's game-status-derived result.");
        }
      }
    }
  }
}
