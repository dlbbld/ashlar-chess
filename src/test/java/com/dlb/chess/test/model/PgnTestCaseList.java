package com.dlb.chess.test.model;

import java.util.List;

import com.dlb.chess.test.pgntest.enums.PgnTest;

public record PgnTestCaseList(PgnTest pgnTest, List<PgnFen> list) {

}
