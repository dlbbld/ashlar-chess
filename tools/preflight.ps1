param(
  [switch] $SkipExcludedSuite
)

# Release pre-flight (workflows.md, "Cutting a release" step 5) as one fail-fast command.
#
# Gate ORDER is the point: cheap, likely-to-fail, fix-requires-a-commit gates run FIRST, the expensive
# read-only suites LAST. Any fix committed after a gate invalidates every gate already passed (the
# artifact you validate must be the artifact you ship), so a failure in a cheap late gate would force
# re-running the 30-40 minute suites. Front-loading the mutation-prone gates makes that rare.
#
# Gates, in order:
#   1. worktree clean            (free; everything after assumes a committed tree)
#   2. license headers exact     (seconds; a miss means a fix commit)
#   3. JavaDoc gates             (~2 min; doc errors mean fix commits): mvn clean javadoc:javadoc javadoc:test-javadoc -Dshow=private
#   4. mvn test -Pfull           (the long suite; read-only)
#   5. mvn test -Pfull -Dtest.excludes=   (the excluded unwinnability suite; longest; read-only;
#                                          skip with -SkipExcludedSuite for a quick pass)
#
# Every gate's verdict comes from the tool's own exit code - no log grepping, no shell masking. The
# script stops at the first red gate and exits non-zero; a green run ends with an explicit summary.
# If ANY gate forced a fix commit, re-run the script from the top on the new tree.
# tasks.md confirmation and the board burn-in decision remain human steps by design.

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

Invoke-Gate "worktree clean" {
  $status = git status --porcelain
  if ($LASTEXITCODE -ne 0) {
    Write-Host "git status failed (exit $LASTEXITCODE); cannot verify the worktree - failing closed." -ForegroundColor Red
    $global:LASTEXITCODE = 1
  } elseif ($status) {
    Write-Host "Worktree is not clean:" -ForegroundColor Red
    Write-Host $status
    $global:LASTEXITCODE = 1
  } else {
    Write-Host "Worktree clean."
    $global:LASTEXITCODE = 0
  }
}

Invoke-Gate "license headers" {
  & (Join-Path $PSScriptRoot "java-license-headers.ps1") -Check
  # java-license-headers.ps1 uses Write-Error/exit on drift; reaching here cleanly means exact.
  if ($null -eq $LASTEXITCODE) { $global:LASTEXITCODE = 0 }
}

Invoke-Gate "JavaDoc gates" {
  mvn clean javadoc:javadoc javadoc:test-javadoc "-Dshow=private"
}

Invoke-Gate "mvn test -Pfull" {
  mvn test -Pfull
}

if (-not $SkipExcludedSuite) {
  Invoke-Gate "mvn test -Pfull -Dtest.excludes=" {
    mvn test -Pfull "-Dtest.excludes="
  }
} else {
  Write-Host ""
  Write-Host "=== GATE: excluded unwinnability suite - SKIPPED on request ===" -ForegroundColor Yellow
  $gates.Add(("{0,-42} SKIP" -f "mvn test -Pfull -Dtest.excludes="))
}

Write-Host ""
Write-Host "================ PRE-FLIGHT SUMMARY ================" -ForegroundColor Green
foreach ($line in $gates) { Write-Host $line }
Write-Host ("{0,-42} {1}" -f "total", $stopwatchTotal.Elapsed.ToString('hh\:mm\:ss'))
Write-Host ""
Write-Host "All automated gates green. Remaining human steps: tasks.md all done, board burn-in if board logic changed." -ForegroundColor Green
exit 0
