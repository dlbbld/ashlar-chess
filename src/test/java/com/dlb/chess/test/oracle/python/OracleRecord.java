package com.dlb.chess.test.oracle.python;

import java.util.List;

public record OracleRecord(String pgn, String startFen, List<OracleMove> moves, String finalFen) {
}
