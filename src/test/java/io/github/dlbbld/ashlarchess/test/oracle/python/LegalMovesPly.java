package io.github.dlbbld.ashlarchess.test.oracle.python;

import java.util.List;

/**
 * One ply's legal-move snapshot in a {@link LegalMovesRecord}. UCI list is sorted alphabetically.
 */
public record LegalMovesPly(List<String> legalMovesUci) {
}
