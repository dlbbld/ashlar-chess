// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Game adjudication for terminations the rules hand to the caller rather than the move pipeline: flag-fall (FIDE 6.9)
 * and resignation (FIDE 5.1.2). Both apply the same draw exception - the game is drawn if the opponent cannot
 * checkmate by any possible series of legal moves - decided here via the material-only insufficient-material check and
 * the position-wise quick unwinnability analyzer.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.adjudication;

import org.eclipse.jdt.annotation.NonNullByDefault;
