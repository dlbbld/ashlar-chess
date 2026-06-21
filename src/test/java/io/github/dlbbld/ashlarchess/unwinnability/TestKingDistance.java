// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.unwinnability;

import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A1;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.A8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.B8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.C8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D4;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D6;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.D8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.E8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F2;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.F8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.G8;
import static io.github.dlbbld.ashlarchess.common.constants.EnumConstants.H8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestKingDistance {

  @SuppressWarnings("static-method")
  @Test
  void test() {
    assertEquals(0, KingDistance.distance(A1, A1));
    assertEquals(1, KingDistance.distance(A1, A2));

    assertEquals(7, KingDistance.distance(A1, A8));
    assertEquals(7, KingDistance.distance(A1, B8));
    assertEquals(7, KingDistance.distance(A1, C8));
    assertEquals(7, KingDistance.distance(A1, D8));
    assertEquals(7, KingDistance.distance(A1, E8));
    assertEquals(7, KingDistance.distance(A1, F8));
    assertEquals(7, KingDistance.distance(A1, G8));
    assertEquals(7, KingDistance.distance(A1, H8));

    assertEquals(3, KingDistance.distance(A1, D4));
    assertEquals(5, KingDistance.distance(A1, D6));
    assertEquals(5, KingDistance.distance(A1, F2));

  }
}
