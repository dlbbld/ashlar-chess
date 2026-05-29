// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.fen.model.Fen;

/**
 * Prepares a {@link PgnGame} for PGN spec section 8.1.1 archival-storage output. Operates on the in-memory model only;
 * the actual line emission is unchanged from semantic mode. Five transformations:
 *
 * <ol>
 * <li><b>FEN / SetUp pair follows {@code startFen}.</b> If {@code startFen} is the initial position, any FEN/SetUp tag
 * in the input is dropped (they would describe a position that is already the implicit default). Otherwise both tags
 * are emitted: {@code SetUp "1"} and {@code FEN <startFen>}. Any pre-existing FEN/SetUp values are replaced by the
 * canonical pair derived from {@code startFen} to guarantee internal consistency.</li>
 * <li><b>Result tag from the termination marker.</b> If the input has no Result tag, one is added with the termination
 * marker's value (or {@code *} if no marker either).</li>
 * <li><b>Seven Tag Roster fill.</b> Any missing roster entry is filled with the spec-defined placeholder:
 * {@code ????.??.??} for Date (per spec section 8.1.1.3), {@code *} for Result (per section 8.1.1.7), {@code ?} for the
 * rest (per sections 8.1.1.1, 8.1.1.2, 8.1.1.4, 8.1.1.5, 8.1.1.6).</li>
 * <li><b>Canonical tag order.</b> Tags are sorted into STR-first-by-sort-order via {@link Tag#compareTo}.</li>
 * <li><b>Termination marker guaranteed.</b> The returned {@code PgnGame.terminationMarker} is non-null; semantic-mode
 * emission then writes it as the movetext trailer.</li>
 * </ol>
 */
final class PgnArchivalNormalization {

  private PgnArchivalNormalization() {
  }

  static PgnGame apply(PgnGame input) {
    final List<Tag> tagList = new ArrayList<>(input.tagList());
    final Fen startFen = input.startFen();

    final ResultTagValue marker = decideTerminationMarker(tagList, input.terminationMarker());

    rewriteFenAndSetUpTags(tagList, startFen);
    addResultTagIfMissing(tagList, marker);
    fillMissingSevenTagRoster(tagList);
    Collections.sort(tagList);

    return new PgnGame(Nulls.copyOfList(tagList), startFen, input.pregameCommentary(), input.halfMoveList(), marker);
  }

  /**
   * Termination-marker decision: prefer the parse-model marker; fall back to the Result tag value; default to
   * {@link ResultTagValue#ONGOING}.
   */
  private static ResultTagValue decideTerminationMarker(List<Tag> tagList, @Nullable ResultTagValue currentMarker) {
    if (currentMarker != null) {
      return currentMarker;
    }
    if (TagUtility.hasResult(tagList)) {
      return ResultTagValue.calculate(TagUtility.readResult(tagList));
    }
    return ResultTagValue.ONGOING;
  }

  /**
   * Drops any existing FEN/SetUp tags, then re-adds the canonical pair when {@code startFen} is non-initial. The
   * remove-then-readd shape guarantees the canonical pair regardless of inconsistencies in the input tagList.
   */
  private static void rewriteFenAndSetUpTags(List<Tag> tagList, Fen startFen) {
    TagUtility.removeFenTag(tagList);
    TagUtility.removeSetUpTag(tagList);
    if (!startFen.equals(FenConstants.FEN_INITIAL)) {
      tagList.add(new Tag(StandardTag.SET_UP.getName(), SetUpTagValue.START_FROM_SETUP_POSITION.getValue()));
      tagList.add(new Tag(StandardTag.FEN.getName(), startFen.fen()));
    }
  }

  private static void addResultTagIfMissing(List<Tag> tagList, ResultTagValue marker) {
    if (!TagUtility.hasResult(tagList)) {
      tagList.add(new Tag(StandardTag.RESULT.getName(), marker.getValue()));
    }
  }

  private static void fillMissingSevenTagRoster(List<Tag> tagList) {
    for (final StandardTag standardTag : TagUtility.SEVEN_TAG_ROSTER_TAG_LIST) {
      if (TagUtility.existsTag(tagList, standardTag)) {
        continue;
      }
      tagList.add(new Tag(standardTag.getName(), placeholderValue(standardTag)));
    }
  }

  private static String placeholderValue(StandardTag tag) {
    return switch (tag) {
      case DATE -> "????.??.??";
      case RESULT -> ResultTagValue.ONGOING.getValue();
      default -> "?";
    };
  }
}
