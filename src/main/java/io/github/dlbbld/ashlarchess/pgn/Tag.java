// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.pgn;

public record Tag(String name, String value) implements Comparable<Tag> {

  @Override
  public int compareTo(Tag o) {
    if (StandardTag.exists(name)) {
      if (!StandardTag.exists(o.name)) {
        // Standard tags sort before custom tags; names always differ here, so no tie-break is needed.
        return -1;
      }
      final int bySortOrder = Integer.compare(StandardTag.parse(name).getSortOrder(),
          StandardTag.parse(o.name).getSortOrder());
      if (bySortOrder != 0) {
        return bySortOrder;
      }
    } else if (StandardTag.exists(o.name)) {
      return 1;
    }

    // Same category and primary rank: order by name, then value, so compareTo stays consistent with equals
    // (which compares both name and value).
    final int byName = name.compareTo(o.name);
    return byName != 0 ? byName : value.compareTo(o.value);
  }

}
