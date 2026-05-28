package io.github.dlbbld.ashlarchess.fen;

import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;

/**
 * One forgiven FEN-level deviation surfaced by the lenient FEN parser. The parser preserves the user's intent and
 * applies a purely syntactic normalisation; this record is the diagnostic channel telling consumers which normalisation
 * steps fired.
 *
 * @param code      the typed deviation classifier
 * @param original  the deviating fragment as the user wrote it (e.g. the uppercase {@code W} for
 *                  {@link ForgivenFenItemCode#UPPERCASE_SIDE_TO_MOVE}, or the dropped trailing token for
 *                  {@link ForgivenFenItemCode#TRAILING_GARBAGE_TOKEN}). Empty if the deviation does not reference a
 *                  specific fragment (e.g. {@link ForgivenFenItemCode#MISSING_HALFMOVE_AND_FULLMOVE}).
 * @param canonical the canonical equivalent the parser substituted, or empty if the normalisation is a deletion
 *                  (whitespace-stripping, dropping trailing garbage)
 */
public record ForgivenFenItem(ForgivenFenItemCode code, String original, String canonical) {

  /**
   * Shared empty list for the "no deviations forgiven" case. Centralised here so the {@code @NonNull} suppression on
   * Guava's {@code ImmutableList.of()} (which JDT cannot statically prove is non-null with non-null elements) lives in
   * one place rather than at every caller.
   */
  @SuppressWarnings("null")
  public static final @NonNull ImmutableList<@NonNull ForgivenFenItem> EMPTY_LIST = ImmutableList.of();

}
