param(
  [Parameter(Mandatory = $true)]
  [string] $Before,
  [Parameter(Mandatory = $true)]
  [string] $After
)

# Compares two JMH result files produced by tools/bench.ps1 and prints the per-benchmark delta.
#
# The verdict column is deliberately conservative. JMH reports a mean and a 99.9% confidence half-width
# per benchmark; a difference is only called a win or a loss when the two intervals do not overlap.
# Anything else prints "inconclusive", because at the effect sizes this project is deciding on - a slider
# optimization worth maybe 10-20% of move generation - run-to-run drift is easily mistaken for a result.
# Whether a non-overlapping difference is worth the complexity is a judgement call; this script only
# reports whether there is a difference at all.

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Read-Scores {
  param([string] $Path)
  if (-not (Test-Path $Path)) { throw "No such result file: $Path" }
  $scores = @{}
  foreach ($entry in (Get-Content -Raw $Path | ConvertFrom-Json)) {
    # Parameterized benchmarks share a benchmark name and differ only by @Param, so the key has to carry
    # the parameters or the rows collide and one silently overwrites the other.
    $name = $entry.benchmark -replace '^io\.github\.dlbbld\.ashlarchess\.bench\.', ''
    # Set-StrictMode makes a missing property an error, so probe for it rather than reading it.
    $parameterProperty = $entry.PSObject.Properties['params']
    if ($parameterProperty -and $parameterProperty.Value) {
      $parameterText = ($parameterProperty.Value.PSObject.Properties | Sort-Object Name | ForEach-Object { "$($_.Name)=$($_.Value)" }) -join ','
      $name = "$name[$parameterText]"
    }
    $scores[$name] = [PSCustomObject]@{
      Score = [double] $entry.primaryMetric.score
      Error = [double] $entry.primaryMetric.scoreError
      Unit  = [string] $entry.primaryMetric.scoreUnit
    }
  }
  return $scores
}

$beforeScores = Read-Scores -Path $Before
$afterScores = Read-Scores -Path $After

$rows = New-Object System.Collections.Generic.List[object]
foreach ($name in ($beforeScores.Keys | Sort-Object)) {
  if (-not $afterScores.ContainsKey($name)) { continue }
  $b = $beforeScores[$name]
  $a = $afterScores[$name]

  # A NaN error means a single-fork or single-iteration run, which carries no interval at all.
  $hasInterval = -not ([double]::IsNaN($b.Error) -or [double]::IsNaN($a.Error))
  $changePercent = if ($b.Score -ne 0) { 100.0 * ($a.Score - $b.Score) / $b.Score } else { [double]::NaN }

  $verdict = "inconclusive"
  if ($hasInterval) {
    $beforeLow = $b.Score - $b.Error
    $beforeHigh = $b.Score + $b.Error
    $afterLow = $a.Score - $a.Error
    $afterHigh = $a.Score + $a.Error
    # Lower is better: every benchmark in this harness reports average time per operation.
    if ($afterHigh -lt $beforeLow) { $verdict = "FASTER" }
    elseif ($afterLow -gt $beforeHigh) { $verdict = "SLOWER" }
  }

  # Hoisted to a local: PowerShell cannot parse a multi-line hashtable literal as a method-call argument.
  $row = [PSCustomObject]@{
    Benchmark = $name
    Before    = "{0:N3} +-{1:N3}" -f $b.Score, $b.Error
    After     = "{0:N3} +-{1:N3}" -f $a.Score, $a.Error
    Unit      = $b.Unit
    Change    = "{0,7:N1}%" -f $changePercent
    Verdict   = $verdict
  }
  $rows.Add($row)
}

if ($rows.Count -eq 0) { throw "No benchmarks in common between the two result files" }

# Explicit width: the default console width truncates the Change and Verdict columns, which are the point.
$rows | Format-Table -AutoSize | Out-String -Width 200 | Write-Host

$onlyBefore = $beforeScores.Keys | Where-Object { -not $afterScores.ContainsKey($_) }
$onlyAfter = $afterScores.Keys | Where-Object { -not $beforeScores.ContainsKey($_) }
if ($onlyBefore) { Write-Host "only in $Before : $($onlyBefore -join ', ')" -ForegroundColor DarkYellow }
if ($onlyAfter) { Write-Host "only in $After : $($onlyAfter -join ', ')" -ForegroundColor DarkYellow }
