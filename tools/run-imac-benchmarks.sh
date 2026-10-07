#!/bin/bash
set -euo pipefail
benchmark_script=$(cd "$(dirname "$0")" && pwd)/$(basename "$0")

for tool in git java mvn pwsh zip shasum sysctl sw_vers; do
  command -v "$tool" >/dev/null || { echo "Missing command: $tool" >&2; exit 1; }
done
if [ "$(uname -s)" != Darwin ] || [ "$(uname -m)" != arm64 ]; then
  echo 'Run this procedure in a native Apple Silicon macOS Terminal.' >&2
  exit 1
fi
java_properties=$(java -XshowSettings:properties -version 2>&1)
case "$java_properties" in
  *'java.specification.version = 17'*) ;;
  *) echo 'Select JDK 17 before starting.' >&2; exit 1 ;;
esac
case "$java_properties" in
  *'os.arch = aarch64'*) ;;
  *) echo 'Select an ARM64 JDK; the current Java runs under another architecture.' >&2; exit 1 ;;
esac
if [ -n "${JAVA_TOOL_OPTIONS:-}" ] || [ -n "${JDK_JAVA_OPTIONS:-}" ] || [ -n "${_JAVA_OPTIONS:-}" ]; then
  echo 'Start without global Java option overrides so both builds use the documented defaults.' >&2
  exit 1
fi

benchmark_root=${1:-"$PWD/ashlar-imac-bench-$(date +%Y%m%d-%H%M%S)"}
if [ -e "$benchmark_root" ]; then
  echo "Choose a new output directory; this one already exists: $benchmark_root" >&2
  exit 1
fi
mkdir -p "$benchmark_root"
cd "$benchmark_root"
benchmark_root=$PWD
mkdir results
exec > >(tee "$benchmark_root/results/session.log") 2>&1

before_commit=84b9f0b202865a2381c826b6c961b0d008f183c8
after_commit=2702d82d752a9187dc75a3ad0083ed31451023a2
{
  date -u
  hostname
  sw_vers
  uname -m
  sysctl machdep.cpu.brand_string hw.model hw.memsize hw.physicalcpu hw.logicalcpu
  java -XshowSettings:properties -version
  mvn -version
  pwsh -NoProfile -Command '$PSVersionTable | Out-String'
} > "$benchmark_root/results/machine.txt" 2>&1

git clone --no-checkout https://github.com/dlbbld/ashlar-chess.git repo
git -C repo fetch origin claude/magic-bitboards-kbn-fixes-569a91
git -C repo cat-file -e "$before_commit^{commit}"
git -C repo cat-file -e "$after_commit^{commit}"
git -C repo worktree add --detach "$benchmark_root/before" "$before_commit"
git -C repo worktree add --detach "$benchmark_root/after" "$after_commit"

# Keep the old production implementation while using identical measured loops and fixtures.
bench_package=src/bench/java/io/github/dlbbld/ashlarchess/bench
for file in BenchmarkPositions.java MoveGenerationBenchmark.java UnwinnabilityBenchmark.java package-info.java; do
  cp "$benchmark_root/after/$bench_package/$file" "$benchmark_root/before/$bench_package/$file"
  shasum -a 256 "$benchmark_root/after/$bench_package/$file" >> "$benchmark_root/results/harness-sha256.txt"
done
cp "$benchmark_root/after/tools/bench.ps1" "$benchmark_root/before/tools/bench.ps1"
git -C before diff --binary > "$benchmark_root/results/baseline-harness.patch"
git -C before diff --exit-code -- src/main pom.xml src/test/resources
git -C after diff --exit-code
printf 'before=%s\nafter=%s\n' "$before_commit" "$after_commit" > "$benchmark_root/results/commits.txt"

# Verify result coverage as well as the runner's execution and score checks.
cat > "$benchmark_root/verify-results.ps1" <<'POWERSHELL'
param([string] $Path, [ValidateSet('workload', 'micro')][string] $Kind)
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$rows = @(Get-Content -Raw $Path | ConvertFrom-Json)
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
  if ($null -eq $score -or [double]::IsNaN([double]$score) -or [double]::IsInfinity([double]$score)) {
    throw 'Result contains a missing or non-finite score'
  }
  if ($_.benchmark.Contains('MoveGenerationBenchmark.')) {
    $_.benchmark + '|' + $_.params.positionCount
  } else { $_.benchmark }
})
if ($actual.Count -ne $expected.Count -or @(Compare-Object $expected $actual).Count -ne 0) {
  throw "Unexpected benchmark coverage in $Path"
}
Write-Output "Verified $($rows.Count) expected results"
POWERSHELL

for build in before after; do
  cd "$benchmark_root/$build"
  # Bootstrap test plugins as well as dependencies on a fresh Mac before using offline mode.
  mvn -q test > "$benchmark_root/results/$build-tests.log" 2>&1
  mvn -q -Pbench test-compile dependency:build-classpath -Dmdep.outputFile=target/bench-classpath.txt -Dmdep.includeScope=test
  pwsh -NoProfile -File tools/bench.ps1 -Label "imac-$build-smoke" -Include 'MoveGenerationBenchmark\.legalMoves$' -SkipBuild -Quick
done

run_workload() {
  build=$1
  label=$2
  cd "$benchmark_root/$build"
  pwsh -NoProfile -File tools/bench.ps1 -Label "$label" -Include 'MoveGenerationBenchmark\.|UnwinnabilityBenchmark\.' -SkipBuild
  pwsh -NoProfile -File "$benchmark_root/verify-results.ps1" -Path "target/bench/$label.json" -Kind workload
  cp "target/bench/$label".* "$benchmark_root/results/"
}

# ABBA reduces the influence of drift; these are still sequential measurements.
run_workload before imac-before-1
run_workload after imac-after-1
run_workload after imac-after-2
run_workload before imac-before-2

cd "$benchmark_root/after"
pwsh -NoProfile -File tools/bench.ps1 -Label imac-slider-micro -Include 'SliderAttacksBenchmark\.' -SkipBuild
pwsh -NoProfile -File "$benchmark_root/verify-results.ps1" -Path target/bench/imac-slider-micro.json -Kind micro
cp target/bench/imac-slider-micro.* "$benchmark_root/results/"
cp "$benchmark_root/verify-results.ps1" "$benchmark_root/results/"
cp "$benchmark_script" "$benchmark_root/results/run-imac-benchmarks.sh"
cd "$benchmark_root"
zip -q -r imac-results.zip results
echo "Completed. Results: $benchmark_root/imac-results.zip"
