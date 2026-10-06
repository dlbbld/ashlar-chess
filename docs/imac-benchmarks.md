# Run the magic-bitboard performance comparison on an Apple Silicon iMac

Compare the frozen ray implementation at `84b9f0b2` with the reviewed magic-bitboard implementation at `2702d82d`. These instructions apply to an Apple Silicon iMac, including the M3. The comparison can run before the release-procedure merge.

Use native macOS and an ARM64 Java 17 installation. Run both the original and the magic implementation on the iMac; comparing its new-build timing directly with the ThinkPad's old-build timing cannot establish the improvement.

## 1. Install the tools

If Homebrew is already installed, open Terminal and run:

```bash
brew install maven
brew install --cask temurin@17
```

Install stable ARM64 PowerShell using Microsoft's macOS package installer, then reopen Terminal. It provides the `pwsh` command. If the tools are already present, installation is unnecessary.

Sources: [Maven installation](https://maven.apache.org/install), [Temurin 17 Homebrew package](https://formulae.brew.sh/cask/temurin@17), [Microsoft's PowerShell installation instructions](https://learn.microsoft.com/en-us/powershell/scripting/install/install-powershell-on-macos).

Select Java 17 in this Terminal session:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
uname -m
java -XshowSettings:properties -version
mvn -version
pwsh -NoProfile -Command '$PSVersionTable.PSVersion'
```

Check that `uname -m` reports `arm64`, Java reports `os.arch = aarch64` and version 17, and Maven uses that same Java installation. The script also checks Java's version and architecture before proceeding. Use an ordinary native Terminal window.

## 2. Prepare the iMac

Close games, video editing, virtual machines, and other substantial background work. Pause scheduled builds and large downloads. Keep the same macOS power setting throughout; record whether Low Power Mode is enabled if the machine offers it. Let the machine finish installation and indexing activity before starting.

Leave Terminal open and the machine otherwise idle during measurement. The command below prevents idle sleep. It does not pin the benchmark to a particular performance core, so repeated runs remain necessary.

## 3. Download and run the script

Download [the benchmark script](../tools/run-imac-benchmarks.sh) directly on the iMac. In the same Terminal session where Java 17 was selected, run:

```bash
curl -fL 'https://raw.githubusercontent.com/dlbbld/ashlar-chess/claude/magic-bitboards-kbn-fixes-569a91/tools/run-imac-benchmarks.sh' \
  -o "$HOME/Downloads/run-imac-benchmarks.sh"
caffeinate -i bash "$HOME/Downloads/run-imac-benchmarks.sh" "$HOME/ashlar-imac-bench-2702d82d"
```

The output directory must not already exist. Choose another name for a repeated experiment. The script uses a fresh clone and separate detached worktrees, leaving any existing development checkout alone.

Allow approximately 45-90 minutes, including preparation; the full unwinnability tail can make this longer. The script stops on compilation, test, benchmark, or result-validation failure. Do not treat an interrupted session as a completed comparison. Internet access is needed initially for Git and Maven dependencies. Measurements use Maven offline mode after preparation.

The script performs:

1. A machine and software fingerprint, including hostname, macOS, CPU, memory, Java architecture/version, Maven, and PowerShell.
2. Separate builds of frozen original commit `84b9f0b2` and reviewed magic commit `2702d82d`.
3. Identical corrected fixtures and measured workload code in both builds. Only benchmark sources and the runner are copied to the original build; its production code stays at the original commit. The intentional benchmark changes are saved as a patch.
4. Default correctness tests and quick smoke runs on both builds.
5. Four full workload runs in **before, after, after, before** order, using three JVM forks per benchmark. Each run covers legal moves, attacked squares, pins, and check detection at both fixture sizes, plus quick, typical, and hard unwinnability analysis.
6. A separate ray-versus-magic microbenchmark on the corrected build.
7. Verification of all expected benchmark rows and finite scores, followed by a results ZIP.

The copied baseline benchmark harness was compiled successfully against the original production code on the Windows review machine. The macOS procedure itself has not been executed here.

## 4. Return the results

When the script prints `Completed`, send back:

```text
~/ashlar-imac-bench-2702d82d/imac-results.zip
```

The ZIP contains raw JSON, console and environment logs, machine details, test logs, benchmark-source checksums, and the baseline patch. If a run fails, keep the output directory and send its `results/session.log`; more diagnostics may be in the relevant worktree's `target/bench` directory.

For each matching workload/fixture row, compare the two before runs with the two after runs. All these timings are average time, so lower is better:

```text
time reduction (%) = 100 * (before mean - after mean) / before mean
micro speedup       = ray time / magic time
```

Use fork variation and reported uncertainty when judging a difference. If the two repeats disagree materially, repeat the experiment under steadier conditions. ABBA helps assess drift but does not remove it. Quick smoke timings are not performance evidence.

The main verdict is the production legal-move result at both fixture sizes. Pin timings provide a useful control because their implementation did not change. Report unwinnability separately, including its hard tail. Compare the iMac's percentage improvement with the ThinkPad's percentage improvement; retain their absolute timings as separate machine-specific results.
