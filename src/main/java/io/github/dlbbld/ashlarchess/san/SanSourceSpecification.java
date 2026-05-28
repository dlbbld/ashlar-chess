// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.san;

enum SanSourceSpecification {

  SOURCE_NOT_REQUIRED,
  SOURCE_REQUIRED_FILE_BUT_NOT_RANK,
  SOURCE_REQUIRED_RANK_BUT_NOT_FILE,
  SOURCE_REQUIRED_SQUARE,
}
