package io.github.dlbbld.ashlarchess.model;

import io.github.dlbbld.ashlarchess.enums.MoveSuffixAnnotation;
import io.github.dlbbld.ashlarchess.pgn.PgnCommentary;

public record PgnHalfMove(String san, MoveSuffixAnnotation moveSuffixAnnotation, PgnCommentary commentary) {

}
