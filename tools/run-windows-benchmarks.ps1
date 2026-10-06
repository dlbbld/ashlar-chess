#requires -Version 7.4
param(
  [string] $OutputDirectory = (Join-Path $env:USERPROFILE 'ashlar-ideacentre-bench-2702d82d'),
  [string] $PowerModeNote = 'not supplied',
  [switch] $SmokeOnly
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$PSNativeCommandUseErrorActionPreference = $false

function Invoke-Checked {
  param([string] $Command, [string[]] $CommandArguments, [string] $LogPath = '')
  if ($LogPath) {
    & $Command @CommandArguments 2>&1 | Tee-Object -FilePath $LogPath
  } else {
    & $Command @CommandArguments 2>&1
  }
  if ($LASTEXITCODE -ne 0) { throw "$Command failed with exit code $LASTEXITCODE" }
}

function Assert-Results {
  param([string] $Path, [ValidateSet('workload', 'micro')] [string] $Kind)
  $rows = @(Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json)
  $prefix = 'io.github.dlbbld.ashlarchess.bench.'
  $expected = @()
  if ($Kind -eq 'workload') {
    foreach ($method in @('legalMoves', 'attackedSquares', 'pinnedPieces', 'isInCheck')) {
      foreach ($count in @('8', '512')) { $expected += "${prefix}MoveGenerationBenchmark.$method|$count" }
    }
    foreach ($method in @('unwinnableQuick', 'unwinnableFullTypical', 'unwinnableFullHard')) {
      $expected += "${prefix}UnwinnabilityBenchmark.$method"
    }
  } else {
    foreach ($method in @('bishopRay', 'bishopMagic', 'rookRay', 'rookMagic', 'queenRay', 'queenMagic')) {
      $expected += "${prefix}SliderAttacksBenchmark.$method"
    }
  }
  $actual = @($rows | ForEach-Object {
    $score = $_.primaryMetric.score
    if ($null -eq $score -or [double]::IsNaN([double]$score) -or
        [double]::IsInfinity([double]$score) -or [double]$score -le 0) {
      throw "Missing, non-finite, or non-positive score in $Path"
    }
    if ($_.benchmark.Contains('MoveGenerationBenchmark.')) {
      $_.benchmark + '|' + $_.params.positionCount
    } else { $_.benchmark }
  })
  if ($actual.Count -ne $expected.Count -or @(Compare-Object $expected $actual).Count -ne 0) {
    throw "Unexpected benchmark coverage in $Path"
  }
  Write-Host "Verified $($rows.Count) expected results"
}

if (-not $IsWindows -or [Runtime.InteropServices.RuntimeInformation]::ProcessArchitecture -ne 'X64') {
  throw 'Run with native x64 PowerShell 7 on Windows.'
}
foreach ($command in @('git', 'java', 'mvn', 'pwsh', 'powercfg')) {
  Get-Command $command -CommandType Application -ErrorAction Stop | Out-Null
}
foreach ($option in @('JAVA_TOOL_OPTIONS', 'JDK_JAVA_OPTIONS', '_JAVA_OPTIONS')) {
  if ([Environment]::GetEnvironmentVariable($option)) {
    throw "Start without $option overrides so both builds use the documented defaults."
  }
}
$javaProperties = (Invoke-Checked 'java' @('-XshowSettings:properties', '-version') | Out-String)
if ($javaProperties -notmatch '(?m)^\s*java.specification.version = 17\s*$' -or
    $javaProperties -notmatch '(?m)^\s*os.arch = (amd64|x86_64)\s*$') {
  throw 'Select a native Windows x64 JDK 17 before starting.'
}
$javaVersion = [regex]::Match($javaProperties, '(?m)^\s*java.version = (\S+)').Groups[1].Value
$mavenVersion = (Invoke-Checked 'mvn' @('-version') | Out-String)
if ($mavenVersion -notmatch "Java version: $([regex]::Escape($javaVersion))[,\s]") {
  throw 'Maven and java must use the same Java 17 version. Set JAVA_HOME and PATH to that JDK.'
}

$benchmarkRoot = [IO.Path]::GetFullPath($OutputDirectory)
if (Test-Path -LiteralPath $benchmarkRoot) { throw "Choose a new output directory: $benchmarkRoot already exists." }
$results = Join-Path $benchmarkRoot 'results'
New-Item -ItemType Directory -Path $results | Out-Null
$initialLocation = Get-Location
$benchmarkScript = $PSCommandPath
$beforeCommit = '84b9f0b202865a2381c826b6c961b0d008f183c8'
$afterCommit = '2702d82d752a9187dc75a3ad0083ed31451023a2'
$runPrefix = if ($SmokeOnly) { 'windows-smoke' } else { 'ideacentre' }
$transcribing = $false
$sleepHeld = $false
$completed = $false

try {
  Start-Transcript -LiteralPath (Join-Path $results 'session.log') | Out-Null
  $transcribing = $true
  Add-Type -TypeDefinition @'
using System.Runtime.InteropServices;
public static class BenchmarkSleep {
  [DllImport("kernel32.dll", SetLastError = true)]
  public static extern uint SetThreadExecutionState(uint flags);
}
'@
  if ([BenchmarkSleep]::SetThreadExecutionState([uint32]2147483649) -eq 0) { throw 'Could not prevent idle sleep.' }
  $sleepHeld = $true
  Set-Location -LiteralPath $benchmarkRoot
  $powerPlan = (Invoke-Checked 'powercfg' @('/getactivescheme') | Out-String).Trim()
  $machine = [ordered]@{
    timestamp = [DateTimeOffset]::Now.ToString('o')
    hostname = [Environment]::MachineName
    operatingSystem = Get-CimInstance Win32_OperatingSystem | Select-Object Caption, Version, BuildNumber
    computer = Get-CimInstance Win32_ComputerSystem | Select-Object Manufacturer, Model, TotalPhysicalMemory
    processor = @(Get-CimInstance Win32_Processor | Select-Object Name, NumberOfCores,
      NumberOfLogicalProcessors, MaxClockSpeed, L2CacheSize, L3CacheSize)
    activePowerPlan = $powerPlan
    powerModeNote = $PowerModeNote
    parentAffinity = '0x' + [Diagnostics.Process]::GetCurrentProcess().ProcessorAffinity.ToInt64().ToString('X')
    javaProperties = $javaProperties
    mavenVersion = $mavenVersion
    powershellVersion = $PSVersionTable.PSVersion.ToString()
    mode = if ($SmokeOnly) { 'QUICK - smoke test only, NOT decision-grade' } else { 'full' }
  }
  $machine | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath (Join-Path $results 'machine.json') -Encoding utf8
  @("before=$beforeCommit", "after=$afterCommit") | Set-Content -LiteralPath (Join-Path $results 'commits.txt')

  Invoke-Checked 'git' @('clone', '--no-checkout', 'https://github.com/dlbbld/ashlar-chess.git', 'repo')
  Invoke-Checked 'git' @('-C', 'repo', 'fetch', 'origin', 'claude/magic-bitboards-kbn-fixes-569a91')
  Invoke-Checked 'git' @('-C', 'repo', 'cat-file', '-e', "$beforeCommit^{commit}")
  Invoke-Checked 'git' @('-C', 'repo', 'cat-file', '-e', "$afterCommit^{commit}")
  Invoke-Checked 'git' @('-C', 'repo', 'worktree', 'add', '--detach', (Join-Path $benchmarkRoot 'before'), $beforeCommit)
  Invoke-Checked 'git' @('-C', 'repo', 'worktree', 'add', '--detach', (Join-Path $benchmarkRoot 'after'), $afterCommit)

  # Identical timed code and fixtures keep attribution on the production change.
  $benchPackage = 'src/bench/java/io/github/dlbbld/ashlarchess/bench'
  $hashes = @()
  foreach ($file in @('BenchmarkPositions.java', 'MoveGenerationBenchmark.java', 'UnwinnabilityBenchmark.java', 'package-info.java')) {
    $source = Join-Path $benchmarkRoot "after/$benchPackage/$file"
    Copy-Item -LiteralPath $source -Destination (Join-Path $benchmarkRoot "before/$benchPackage/$file")
    $hashes += (Get-FileHash -LiteralPath $source -Algorithm SHA256).Hash.ToLower() + "  $benchPackage/$file"
  }
  $hashes | Set-Content -LiteralPath (Join-Path $results 'harness-sha256.txt')
  Copy-Item -LiteralPath (Join-Path $benchmarkRoot 'after/tools/bench.ps1') -Destination (Join-Path $benchmarkRoot 'before/tools/bench.ps1')
  # PowerShell 7.4+ preserves native stdout bytes, making this patch directly comparable with Git.
  & git -C before diff --binary > (Join-Path $results 'baseline-harness.patch')
  if ($LASTEXITCODE -ne 0) { throw 'Could not record the baseline patch.' }
  Invoke-Checked 'git' @('-C', 'before', 'diff', '--exit-code', '--', 'src/main', 'pom.xml', 'src/test/resources')
  Invoke-Checked 'git' @('-C', 'after', 'diff', '--exit-code')

  foreach ($build in @('before', 'after')) {
    Set-Location -LiteralPath (Join-Path $benchmarkRoot $build)
    # Populate test plugins and dependencies on a fresh machine before the offline measurements.
    Invoke-Checked 'mvn' @('-q', 'test') (Join-Path $results "$build-tests.log")
    $testReports = Join-Path $results "$build-surefire-reports"
    New-Item -ItemType Directory -Path $testReports | Out-Null
    Get-ChildItem -LiteralPath 'target/surefire-reports' -Filter 'TEST-*.xml' | Copy-Item -Destination $testReports
    Invoke-Checked 'mvn' @('-q', '-Pbench', 'test-compile', 'dependency:build-classpath',
      '-Dmdep.outputFile=target/bench-classpath.txt', '-Dmdep.includeScope=test')
    Invoke-Checked 'pwsh' @('-NoProfile', '-File', 'tools/bench.ps1', '-Label', "$runPrefix-$build-smoke",
      '-Include', 'MoveGenerationBenchmark\.legalMoves$', '-SkipBuild', '-Quick')
  }

  $sequence = @(
    @{ build = 'before'; label = "$runPrefix-before-1" },
    @{ build = 'after'; label = "$runPrefix-after-1" },
    @{ build = 'after'; label = "$runPrefix-after-2" },
    @{ build = 'before'; label = "$runPrefix-before-2" }
  )
  foreach ($run in $sequence) {
    Set-Location -LiteralPath (Join-Path $benchmarkRoot $run.build)
    $benchmarkArguments = @('-NoProfile', '-File', 'tools/bench.ps1', '-Label', $run.label,
      '-Include', 'MoveGenerationBenchmark\.|UnwinnabilityBenchmark\.', '-SkipBuild')
    if ($SmokeOnly) { $benchmarkArguments += '-Quick' }
    Invoke-Checked 'pwsh' $benchmarkArguments
    Assert-Results "target/bench/$($run.label).json" workload
    Get-ChildItem -LiteralPath 'target/bench' -Filter "$($run.label).*" | Copy-Item -Destination $results
  }

  Set-Location -LiteralPath (Join-Path $benchmarkRoot 'after')
  $microLabel = "$runPrefix-slider-micro"
  $microArguments = @('-NoProfile', '-File', 'tools/bench.ps1', '-Label', $microLabel,
    '-Include', 'SliderAttacksBenchmark\.', '-SkipBuild')
  if ($SmokeOnly) { $microArguments += '-Quick' }
  Invoke-Checked 'pwsh' $microArguments
  Assert-Results "target/bench/$microLabel.json" micro
  Get-ChildItem -LiteralPath 'target/bench' -Filter "$microLabel.*" | Copy-Item -Destination $results
  Copy-Item -LiteralPath $benchmarkScript -Destination (Join-Path $results 'run-windows-benchmarks.ps1')
  $completed = $true
  Write-Host "All 50 result rows verified. Mode: $($machine.mode)"
} finally {
  if ($sleepHeld) { [BenchmarkSleep]::SetThreadExecutionState([uint32]2147483648) | Out-Null }
  if ($transcribing) { Stop-Transcript | Out-Null }
  Set-Location -LiteralPath $initialLocation.Path
}

if ($completed) {
  $archiveName = if ($SmokeOnly) { 'windows-smoke-results.zip' } else { 'ideacentre-results.zip' }
  $archive = Join-Path $benchmarkRoot $archiveName
  Compress-Archive -LiteralPath $results -DestinationPath $archive
  Write-Host "Completed. Results: $archive"
}
