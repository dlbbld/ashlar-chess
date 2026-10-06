// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

/**
 * JMH benchmarks that exist to decide whether an optimization is worth its complexity.
 *
 * <p>
 * These are a decision tool, not a regression suite: a benchmark here answers "does this change pay for itself on this
 * machine", and the answer is a measured number plus the machine it was measured on. Nothing in this package is wired
 * into surefire, and the whole source root only compiles under the {@code bench} profile.
 *
 * <p>
 * Every benchmark reports per-position averages via {@code @OperationsPerInvocation} so a number stays comparable when
 * the fixture count changes. Fixtures are built in {@code @Setup}, never inside a measured method.
 */
package io.github.dlbbld.ashlarchess.bench;
