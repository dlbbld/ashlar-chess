// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

public enum LenientPgnParserValidationProblem {
  UNKNOWN_ERROR,
  OK,
  /**
   * The PGN input is empty - zero bytes, only the UTF-8 BOM, or whitespace-only (spaces, tabs, newlines in any
   * combination). Even the lenient parser refuses to fabricate a game out of no signal. Symmetric with
   * {@link StrictPgnParserValidationProblem#FILE_EMPTY}. Callers who genuinely want the initial position have
   * {@code new Board()}.
   */
  FILE_EMPTY,
  TAG_REAPPEAR,
  TAG_FORMAT_INVALID,
  TAG_NAME_NOT_UNIQUE,
  TAG_RESULT_VALUE_INVALID,
  TAG_RESULT_BOTH_SET_BUT_DIFFERENT,
  TAG_SET_UP_VALUE_INVALID,
  TAG_SET_UP_VALUE_ZERO_BUT_FEN_PROVIDED,
  MOVETEXT_COMMENTARY_START_BRACE_NOT_FOLLOWED_BY_END_BRACE,
  MOVETEXT_COMMENTARY_END_BRACE_WITHOUT_START_BRACE,
  MOVETEXT_COMMENTARY_CONTAINS_FORBIDDEN_CHARACTER,
  MOVETEXT_COMMENTARY_NOT_ALLOWED_IN_SAN,
  MOVETEXT_CONTENT_AFTER_TERMINATION,
  EXCEPTION_CAUGHT_FROM_STRICT_VALIDATION,
  SAN,
  /**
   * The PGN's FEN tag value cannot be parsed even by the lenient FEN parser, or the resulting position fails strict
   * semantic validation (missing king, impossible double-check, etc.). The PGN's structure is otherwise fine; the
   * failure is in the position encoded by the FEN tag.
   */
  FEN_TAG_INVALID
}
