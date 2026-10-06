# Run the magic-bitboard comparison on the Windows 11 IdeaCentre

Compare the frozen ray implementation at `84b9f0b2` with the corrected magic-bitboard implementation at `2702d82d`. This follows the [M3 iMac procedure](imac-benchmarks.md), with native Windows x64 Java 17. It isolates magics: neither build adds the UCI removal, map-sizing change, or KBN fix.

## 1. Install and select the tools

Use **PowerShell 7.4 or newer, x64**, Git, **Temurin JDK 17 x64**, and Maven 3.9.x. Skip installation for tools already present. PowerShell 7 is launched with `pwsh`, and can coexist with Windows PowerShell 5.1.

Install PowerShell, Java, and Git from a terminal if needed:

```powershell
winget install --id Microsoft.PowerShell --exact --source winget --installer-type wix
winget install --id EclipseAdoptium.Temurin.17.JDK --exact --source winget --architecture x64
winget install --id Git.Git --exact --source winget
```

Install Maven using the [Apache Maven instructions](https://maven.apache.org/install.html), adding its `bin` directory to PATH. If Scoop is already installed, `scoop install maven` is an alternative. Close and reopen the terminal after installing tools, then start **PowerShell 7**.

Sources: [Microsoft PowerShell installation](https://learn.microsoft.com/en-us/powershell/scripting/install/install-powershell-on-windows), [Temurin installation](https://adoptium.net/installation), [Git for Windows](https://git-scm.com/install/windows).

Select the installed Temurin 17 for this terminal session:

```powershell
$benchmarkJdk = Get-ChildItem -LiteralPath 'C:\Program Files\Eclipse Adoptium' -Directory -Filter 'jdk-17*' |
  Sort-Object LastWriteTime -Descending | Select-Object -First 1
if ($null -eq $benchmarkJdk) { throw 'Install Temurin JDK 17 x64 first.' }
$env:JAVA_HOME = $benchmarkJdk.FullName
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

$PSVersionTable.PSVersion
java -XshowSettings:properties -version
mvn -version
git --version
```

Java must report `java.specification.version = 17` and `os.arch = amd64`. Maven must use the same Java 17 version. If your JDK was installed elsewhere, set `JAVA_HOME` to that actual JDK directory. Keep the selected JDK unchanged throughout the experiment; using Temurin 17.0.19 matches the iMac, but another Java 17 patch is acceptable when both arms use it and its version is recorded.

## 2. Prepare the machine

Close games, virtual machines, video processing, other builds, and heavy browser sessions. Let installation, indexing, and update activity settle before starting. Keep the Windows power mode and CPU affinity unchanged through all runs. The Ryzen 5 220 has two Zen 4 and four Zen 4c cores; the first experiment uses the normal Windows scheduler and repeated runs to assess variation. [AMD specifications](https://www.amd.com/en/products/processors/laptop/ryzen/200-series/amd-ryzen-5-220.html).

Note the selected power mode in **Settings > System > Power & battery** (or **Power** on a desktop). Supply that text as `-PowerModeNote` below. The script records the active power plan and temporarily prevents idle system sleep. It restores normal idle-sleep behavior when it exits; leave the terminal open until completion.

## 3. Download and run

In the same PowerShell 7 session where Java 17 was selected:

```powershell
$benchmarkScript = Join-Path $env:USERPROFILE 'Downloads\run-windows-benchmarks.ps1'
Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/dlbbld/ashlar-chess/claude/magic-bitboards-kbn-fixes-569a91/tools/run-windows-benchmarks.ps1' -OutFile $benchmarkScript
Unblock-File -LiteralPath $benchmarkScript

pwsh -NoProfile -File $benchmarkScript `
  -OutputDirectory "$env:USERPROFILE\ashlar-ideacentre-bench-2702d82d" `
  -PowerModeNote 'Balanced'
```

Replace `Balanced` with the actual power mode you noted. Choose an output directory that does not already exist; use a new name for another experiment. The script makes a fresh clone and detached before/after worktrees, leaving existing development checkouts untouched. It must have internet access during Git and Maven preparation.

If Windows blocks the reviewed downloaded script despite `Unblock-File`, launch that one process with `pwsh -NoProfile -ExecutionPolicy Bypass -File ...` and the same arguments. This does not change the saved machine/user execution policy.

Allow approximately **45-90 minutes**, potentially longer for hard unwinnability positions. Initial dependency downloads add preparation time. The script stops if a native command fails or expected results are missing. On failure, retain the directory and its `results/session.log` for diagnosis.

The script performs:

1. Machine/software fingerprint, including CPU, memory, Windows build, hostname, Java, Maven, PowerShell, active power plan, and the supplied power-mode note.
2. Default Maven correctness tests on both builds, with their logs and Surefire XML retained.
3. Identical corrected benchmark code and fixtures in both builds. Only benchmark sources and the runner are copied to the baseline; its production code remains frozen. The overlay patch and source hashes are saved.
4. Quick smoke checks, followed by full **before, after, after, before** workload runs, with three JVM forks per benchmark. Every workload run has 11 rows: eight move-generation rows and three unwinnability rows.
5. The six ray-versus-magic microbenchmark rows on the corrected build.
6. Validation of all 50 expected result rows and their finite positive scores, then a results ZIP.

The runner follows the same fork, warmup, iteration, and fixture settings as the iMac experiment. ABBA helps assess drift but does not eliminate it. The microbenchmark methods also run sequentially in separate forks.

## 4. Return the evidence

When it prints `Completed`, send back:

```text
%USERPROFILE%\ashlar-ideacentre-bench-2702d82d\ideacentre-results.zip
```

The ZIP contains raw JSON, per-run logs and fingerprints, both test reports, the baseline patch, harness hashes, and the executed script. Judge each workload from its own fork and run variation; pin timing is a control, not a universal noise threshold. Compare each machine's before/after percentage within that machine.

For troubleshooting the procedure, `-SmokeOnly` executes the workflow with quick measurements and creates `windows-smoke-results.zip`. Its numbers are explicitly not performance evidence. Omit that switch for the IdeaCentre experiment.

The default test suite, failure guards, and complete quick workflow were verified on the Windows review machine. The full performance experiment must still be run on the IdeaCentre.
