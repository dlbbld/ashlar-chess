package io.github.dlbbld.ashlarchess.test.fen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dlbbld.ashlarchess.fen.FenParserAdvanced;
import io.github.dlbbld.ashlarchess.fen.constants.FenConstants;
import io.github.dlbbld.ashlarchess.fen.model.Fen;

class TestFenParserInitial {

  @SuppressWarnings("static-method")
  @Test
  void testInitial() {
    final Fen fen = FenParserAdvanced.parseFenAdvanced(FenConstants.FEN_INITIAL_STR);
    assertEquals(FenConstants.FEN_INITIAL_STR, fen.fen());
  }

}
