// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

/**
 * A single lexical unit produced by a PGN tokenizer. The {@code text} field carries the logical payload - for tag
 * names, tag values, SAN, comments and so on, this is the semantic content (values unquoted, comments without braces).
 * Structural tokens like {@link PgnTokenType#TAG_BRACKET_OPEN} or {@link PgnTokenType#NEWLINE} carry the literal text
 * of the character(s) consumed.
 *
 * <p>
 * Line and column are one-based and refer to the first character of the token in the source.
 */
record PgnToken(PgnTokenType type, String text, int line, int column) {
}
