param(
  [Parameter(Mandatory = $true)]
  [string] $Label,
  [string] $Include = "",
  [switch] $SkipBuild,
  [switch] $Quick
)

# Runs the JMH harness in src/bench/java with pinned settings and records the result under target/bench.
#
# Why a script rather than a bare `java -jar benchmarks.jar`: a benchmark number is only worth keeping if
# it is comparable to the number it is being compared against. That needs the fork / warmup / iteration
# counts pinned (JMH's command line silently overrides the annotations), and it needs the machine the
# number came from recorded next to it. Both are easy to forget by hand and invisible afterwards, so the
# script does them and names the output file after -Label.
#
# -Label names the run, and the comparison is between labels: `-Label before` then `-Label after`.
# -Include is a JMH benchmark-name regex, e.g. "SliderAttacks" to iterate on one class.
# -Quick cuts forks and iterations for a smoke test. Quick numbers are NOT decision-grade and the
#   environment block in the output says so.

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $repoRoot

$outputFolder = Join-Path $repoRoot "target/bench"
New-Item -ItemType Directory -Force -Path $outputFolder | Out-Null
$jsonPath = Join-Path $outputFolder "$Label.json"
$logPath = Join-Path $outputFolder "$Label.log"
$environmentPath = Join-Path $outputFolder "$Label.env.txt"

if (-not $SkipBuild) {
  Write-Host "=== Building benchmark classes ===" -ForegroundColor Cyan
  & mvn -o -q -Pbench test-compile
  if ($LASTEXITCODE -ne 0) { throw "Benchmark compile failed with exit code $LASTEXITCODE" }
}

$classpathFile = Join-Path $repoRoot "target/bench-classpath.txt"
Write-Host "=== Resolving classpath ===" -ForegroundColor Cyan
& mvn -o -q -Pbench dependency:build-classpath "-Dmdep.outputFile=$classpathFile" "-Dmdep.includeScope=test"
if ($LASTEXITCODE -ne 0) { throw "Classpath resolution failed with exit code $LASTEXITCODE" }

$dependencyClasspath = (Get-Content -Raw $classpathFile).Trim()
# Java wants the platform classpath separator - ";" on Windows, ":" elsewhere - and the Maven-generated
# dependency list already uses it, so only the two leading entries need joining.
$pathSeparator = [System.IO.Path]::PathSeparator
$classpath = "target/classes$pathSeparator" + "target/test-classes$pathSeparator" + $dependencyClasspath

# The machine, not just the number. A magic-bitboard verdict is cache-size and ISA dependent, so a score
# without its host is not interpretable and definitely not comparable across machines.
# Get-CimInstance is Windows-only, so the CPU fingerprint is gathered per platform. This block is the reason a
# score is interpretable later: a magic-bitboard verdict is cache-size and ISA dependent, so the host is part of
# the result, not metadata about it.
$processorName = "unknown"
$physicalCores = "?"
$logicalCores = [System.Environment]::ProcessorCount
$maxClockMhz = "n/a"
if ($IsWindows) {
  $processor = (Get-CimInstance Win32_Processor | Select-Object -First 1)
  $processorName = $processor.Name.Trim()
  $physicalCores = $processor.NumberOfCores
  $logicalCores = $processor.NumberOfLogicalProcessors
  $maxClockMhz = $processor.MaxClockSpeed
} elseif ($IsMacOS) {
  $processorName = (& sysctl -n machdep.cpu.brand_string).Trim()
  $physicalCores = (& sysctl -n hw.physicalcpu).Trim()
  $logicalCores = (& sysctl -n hw.logicalcpu).Trim()
  # Apple Silicon does not publish a max frequency, and its cores are heterogeneous anyway: a benchmark thread
  # scheduled onto an efficiency core reads as a regression that is really just scheduling. Run in the foreground.
} elseif ($IsLinux) {
  $processorName = ((Get-Content /proc/cpuinfo | Select-String -Pattern "^model name" | Select-Object -First 1) -split ":")[1].Trim()
  $physicalCores = (Get-Content /proc/cpuinfo | Select-String -Pattern "^cpu cores" | Select-Object -First 1).ToString().Split(":")[1].Trim()
}
$environment = @(
  "label                 : $Label",
  "timestamp             : $([DateTimeOffset]::Now.ToString('o'))",
  "git commit            : $(& git rev-parse --short HEAD)",
  "git dirty             : $([bool](& git status --porcelain))",
  "os                    : $([System.Environment]::OSVersion.VersionString)",
  "cpu                   : $processorName",
  "cpu cores / threads   : $physicalCores / $logicalCores",
  "cpu max clock (MHz)   : $maxClockMhz",
  "jdk                   : $((& java -version 2>&1)[0])",
  "mode                  : $(if ($Quick) { 'QUICK - smoke test only, NOT decision-grade' } else { 'full' })"
)
$environment | Set-Content -Path $environmentPath -Encoding UTF8
Write-Host ($environment -join "`n") -ForegroundColor DarkGray

$forks = if ($Quick) { 1 } else { 3 }
$jmhArguments = @(
  "-cp", $classpath,
  "org.openjdk.jmh.Main"
)
if ($Include -ne "") { $jmhArguments += $Include }
$jmhArguments += @("-f", "$forks", "-rf", "json", "-rff", $jsonPath)
if ($Quick) { $jmhArguments += @("-wi", "1", "-i", "2", "-w", "1s", "-r", "1s") }

Write-Host "=== Running JMH (forks=$forks) ===" -ForegroundColor Cyan
$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
& java @jmhArguments 2>&1 | Tee-Object -FilePath $logPath
$jmhExitCode = $LASTEXITCODE
$stopwatch.Stop()

if ($jmhExitCode -ne 0) { throw "JMH failed with exit code $jmhExitCode" }

Write-Host ""
Write-Host "=== Done in $([int]$stopwatch.Elapsed.TotalSeconds)s ===" -ForegroundColor Green
Write-Host "scores      : $jsonPath"
Write-Host "console log : $logPath"
Write-Host "environment : $environmentPath"
