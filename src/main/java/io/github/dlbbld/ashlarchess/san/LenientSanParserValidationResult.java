package io.github.dlbbld.ashlarchess.san;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import io.github.dlbbld.ashlarchess.common.Nulls;
import io.github.dlbbld.ashlarchess.common.model.MoveSpecification;

/**
 * Outcome of a successful lenient SAN parse: the resolved move, plus the list of deviations the parser forgave to get
 * there. {@code forgivenItems} is empty when the input was already canonical SAN.
 */
@SuppressWarnings("null")
public record LenientSanParserValidationResult(@NonNull MoveSpecification moveSpecification,
    @NonNull ImmutableList<@NonNull ForgivenItem> forgivenItems) {

  public LenientSanParserValidationResult {
    forgivenItems = Nulls.copyOfList(forgivenItems);
  }
}
