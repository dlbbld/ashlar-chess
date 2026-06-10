// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.test.readme;

/**
 * One registered README example: its {@code id} (matching the {@code // <readme:ID>} source markers and the
 * {@code README.template.md} placeholders), the {@code body} that produces it, and whether the generator should
 * {@code run} it to capture output.
 *
 * <p>
 * Compile-only examples ({@code run == false}) are sliced and shown to guarantee they compile, but not executed - used
 * where an example cannot run deterministically, for example file I/O against a literal path.
 */
public record ReadmeExample(String id, Runnable body, boolean run) {
}
