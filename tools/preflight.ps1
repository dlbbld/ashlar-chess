param(
  [switch] $SkipExcludedSuite
)

# Release pre-flight (workflows.md, "Cutting a release" step 5) as one fail-fast command.
#
# Gates, in order:
#   1. worktree clean            (5.1)
#   2. license headers exact     (5.2)
#   3. mvn test -Pfull           (5.4)
#   4. mvn test -Pfull -Dtest.excludes=   (5.5; skip with -SkipExcludedSuite for a quick pass)
#   5. JavaDoc gates             (5.6): mvn clean javadoc:javadoc javadoc:test-javadoc -Dshow=private
#
# Every gate's verdict comes from the tool's own exit code - no log grepping, no shell masking. The
# script stops at the first red gate and exits non-zero; a green run ends with an explicit summary.
# 5.7 (tasks.md) and 5.8 (board burn-in decision) remain human steps by design.

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $repoRoot

$gates = New-Object System.Collections.Generic.List[string]
$stopwatchTotal = [System.Diagnostics.Stopwatch]::StartNew()

function Invoke-Gate {
  param(
    [string] $Name,
    [scriptblock] $Body
  )
  Write-Host ""
  Write-Host "=== GATE: $Name ===" -ForegroundColor Cyan
  $sw = [System.Diagnostics.Stopwatch]::StartNew()
  & $Body
  if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "PRE-FLIGHT FAILED at gate: $Name (exit $LASTEXITCODE, after $($sw.Elapsed.ToString('mm\:ss')))" -ForegroundColor Red
    exit 1
  }
  $script:gates.Add(("{0,-42} PASS  {1}" -f $Name, $sw.Elapsed.ToString('mm\:ss')))
}

Invoke-Gate "5.1 worktree clean" {
  $status = git status --porcelain
  if ($status) {
    Write-Host "Worktree is not clean:" -ForegroundColor Red
    Write-Host $status
    $global:LASTEXITCODE = 1
  } else {
    Write-Host "Worktree clean."
    $global:LASTEXITCODE = 0
  }
}

Invoke-Gate "5.2 license headers" {
  & (Join-Path $PSScriptRoot "java-license-headers.ps1") -Check
  # java-license-headers.ps1 uses Write-Error/exit on drift; reaching here cleanly means exact.
  if ($null -eq $LASTEXITCODE) { $global:LASTEXITCODE = 0 }
}

Invoke-Gate "5.4 mvn test -Pfull" {
  mvn test -Pfull
}

if (-not $SkipExcludedSuite) {
  Invoke-Gate "5.5 mvn test -Pfull -Dtest.excludes=" {
    mvn test -Pfull "-Dtest.excludes="
  }
} else {
  Write-Host ""
  Write-Host "=== GATE: 5.5 excluded unwinnability suite - SKIPPED on request ===" -ForegroundColor Yellow
  $gates.Add(("{0,-42} SKIP" -f "5.5 mvn test -Pfull -Dtest.excludes="))
}

Invoke-Gate "5.6 JavaDoc gates" {
  mvn clean javadoc:javadoc javadoc:test-javadoc "-Dshow=private"
}

Write-Host ""
Write-Host "================ PRE-FLIGHT SUMMARY ================" -ForegroundColor Green
foreach ($line in $gates) { Write-Host $line }
Write-Host ("{0,-42} {1}" -f "total", $stopwatchTotal.Elapsed.ToString('hh\:mm\:ss'))
Write-Host ""
Write-Host "All automated gates green. Remaining human steps: 5.7 (tasks.md all done), 5.8 (board burn-in if board logic changed)." -ForegroundColor Green
exit 0
