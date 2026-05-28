package io.github.dlbbld.ashlarchess.test.model;

import java.util.List;

import io.github.dlbbld.ashlarchess.test.pgntest.enums.PgnTest;

public record PgnTestCaseList(PgnTest pgnTest, List<PgnFen> list) {

}
