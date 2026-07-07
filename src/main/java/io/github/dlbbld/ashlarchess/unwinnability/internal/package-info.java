// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * Internal unwinnability helpers that are not part of the public {@link io.github.dlbbld.ashlarchess.unwinnability}
 * face. The sole occupant is the finite-state
 * {@link io.github.dlbbld.ashlarchess.unwinnability.internal.BasicHelpmateExistenceTheorem}: an independently proven
 * elementary-material shortcut used as a fast adjudication pre-check and, in the test suite, as an oracle for the
 * FUN 2022 search analyzer. It is deliberately <em>not</em> exported from the module ({@code module-info.java} exports
 * only {@code unwinnability}), so it never became public API. The classes stay {@code public} (rather than
 * package-private) so the adjudicator and the white-box tests, which live in sibling packages, can use them across
 * packages.
 */
@NonNullByDefault
package io.github.dlbbld.ashlarchess.unwinnability.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
