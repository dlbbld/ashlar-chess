// Copyright (C) 2020-2026 Daniel Baechli
// SPDX-License-Identifier: GPL-3.0-only

package io.github.dlbbld.ashlarchess.common.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.jdt.annotation.Owning;

import io.github.dlbbld.ashlarchess.common.exceptions.ProgrammingMistakeException;

public abstract class IoUtility {

  private IoUtility() {
  }

  public static Process startProcess(ProcessBuilder processBuilder) throws IOException {
    final Process result = processBuilder.start();
    if (result == null) {
      throw new ProgrammingMistakeException("Assumed value is not null");
    }
    return result;
  }

  @Owning
  public static InputStream getInputStream(Process process) {
    final InputStream result = process.getInputStream();
    if (result == null) {
      throw new ProgrammingMistakeException("Assumed value is not null");
    }
    return result;
  }

  @Owning
  public static InputStream getErrorStream(Process process) {
    final InputStream result = process.getErrorStream();
    if (result == null) {
      throw new ProgrammingMistakeException("Assumed value is not null");
    }
    return result;
  }

  @Owning
  public static OutputStream getOutputStream(Process process) {
    final OutputStream result = process.getOutputStream();
    if (result == null) {
      throw new ProgrammingMistakeException("Assumed value is not null");
    }
    return result;
  }

}
