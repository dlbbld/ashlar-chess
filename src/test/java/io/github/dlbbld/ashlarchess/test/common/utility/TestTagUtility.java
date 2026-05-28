// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.common.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.common.constants.EnumConstants;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.pgn.ResultTagValue;
import io.github.dlbbld.ashlarchess.pgn.StandardTag;
import io.github.dlbbld.ashlarchess.pgn.Tag;
import io.github.dlbbld.ashlarchess.pgn.TagUtility;

class TestTagUtility implements EnumConstants {

  @SuppressWarnings("static-method")
  @Test
  void test() {

    final List<Tag> tagList = new ArrayList<>();

    tagList.add(new Tag(StandardTag.RESULT.getName(), ResultTagValue.WHITE_WON.getValue()));
    tagList.add(new Tag(StandardTag.FEN.getName(), FenConstants.FEN_INITIAL_STR));

    assertTrue(TagUtility.hasResult(tagList));
    assertTrue(TagUtility.hasFen(tagList));

    assertEquals(ResultTagValue.WHITE_WON.getValue(), TagUtility.readResult(tagList));
    assertEquals(FenConstants.FEN_INITIAL_STR, TagUtility.readFen(tagList));

    assertFalse(TagUtility.hasEvent(tagList));
    assertFalse(TagUtility.hasSetUp(tagList));
  }
}