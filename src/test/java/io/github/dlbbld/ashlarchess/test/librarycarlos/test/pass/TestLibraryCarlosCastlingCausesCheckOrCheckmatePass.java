// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.librarycarlos.test.pass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.github.bhlangonijr.chesslib.move.MoveList;

import io.github.dlbbld.ashlarchess.common.Nulls;

class TestLibraryCarlosCastlingCausesCheckOrCheckmatePass {

  @SuppressWarnings("static-method")
  @Test
  void testCheck() throws Exception {
    final MoveList moveList = new MoveList();
    final String san = "1. f4 f5 2. Nh3 Nf6 3. e4 fxe4 4. Bb5 Nh5 5. a3 Nxf4 6. a4 Nh5 7. a5 Kf7 8. O-O+";
    moveList.loadFromSan(san);
    final String[] moveListSan = moveList.toSanArray();
    assertNotNull(moveListSan);
    final String sanGeneratedLastMove = Nulls.getLast(moveListSan);
    assertEquals("O-O+", sanGeneratedLastMove);
  }

  @SuppressWarnings("static-method")
  @Test
  void testCheckmate() throws Exception {
    final MoveList moveList = new MoveList();
    final String san = """
        1. d4 e6 2. Nf3 f5 3. Nc3 Nf6 4. Bg5 Be7 5. Bxf6 Bxf6 6. e4 fxe4 7. Nxe4 b6 \
        8. Ne5 O-O 9. Bd3 Bb7 10. Qh5 Qe7 11. Qxh7+ Kxh7 12. Nxf6+ Kh6 13. Neg4+ Kg5 14. h4+ Kf4 \
        15. g3+ Kf3 16. Be2+ Kg2 17. Rh2+ Kg1 18. O-O-O#""";
    moveList.loadFromSan(san);
    final String[] moveListSan = moveList.toSanArray();
    assertNotNull(moveListSan);
    final String sanGeneratedLastMove = Nulls.getLast(moveListSan);
    assertEquals("O-O-O#", sanGeneratedLastMove);

  }
}
