package io.github.dlbbld.ashlarchess.san;

import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

/**
 * Outcome of a successful strict SAN parse: the resolved move. Symmetric in shape with
 * {@link LenientSanParserValidationResult} so callers can switch between strict and lenient parsing by changing one
 * method call rather than reshaping result-handling. The strict pipeline never produces forgiven items, so this record
 * carries only the {@link MoveSpecification}.
 */
public record StrictSanParserValidationResult(MoveSpecification moveSpecification) {

}
