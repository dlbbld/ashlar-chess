param(
  [Parameter(Mandatory = $true)] [string] $Version,
  [Parameter(Mandatory = $true)] [string] $Title,
  [string] $OutFile
)

# Builds the GitHub Release notes body from the CHANGELOG.md [X.Y.Z] entry (runbook step 10).
#
# Why this exists (22.0.0 lesson): CHANGELOG.md is hard-wrapped at ~120 columns - a source-file
# convention that is invisible when GitHub renders a FILE (single newlines reflow). But GitHub
# renders RELEASE NOTES (and issues/comments) with hard newlines: a single \n becomes <br>. Pasting
# the changelog entry verbatim therefore breaks every paragraph mid-sentence on the release page.
# This script unwraps: each paragraph and each list item becomes one logical line; headings and
# blank lines are preserved. Never hand-paste changelog text into a release body.
#
# Usage:
#   .\tools\build-release-notes.ps1 -Version 22.0.0 -Title "Unwinnability now straight from the FUN 2022 paper"
#   gh release create 22.0.0 --verify-tag --title "22.0.0" --notes-file release-notes-22.0.0.md

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$changelog = Get-Content (Join-Path $repoRoot "CHANGELOG.md") -Encoding UTF8

# Extract the [X.Y.Z] entry body (between its header and the next release header).
$body = New-Object System.Collections.Generic.List[string]
$inEntry = $false
foreach ($line in $changelog) {
  if ($line -match "^## \[$([regex]::Escape($Version))\]") { $inEntry = $true; continue }
  if ($inEntry -and $line -match "^## \[") { break }
  if ($inEntry) { $body.Add($line) }
}
if ($body.Count -eq 0) {
  Write-Error "No CHANGELOG.md entry found for version $Version."
}

# Unwrap: join continuation lines into one logical line per paragraph / list item.
$out = New-Object System.Collections.Generic.List[string]
$buffer = $null
function Flush-Buffer {
  if ($null -ne $script:buffer) { $script:out.Add($script:buffer); $script:buffer = $null }
}
foreach ($line in $body) {
  if ($line.Trim() -eq "") { Flush-Buffer; $out.Add("") }
  elseif ($line.StartsWith("#")) { Flush-Buffer; $out.Add($line) }
  elseif ($line.TrimStart().StartsWith("- ")) { Flush-Buffer; $buffer = $line.TrimEnd() }
  elseif ($null -ne $buffer) { $buffer = $buffer + " " + $line.Trim() }
  else { $buffer = $line.TrimEnd() }
}
Flush-Buffer

$notes = "# $Title`n`n" + (($out -join "`n").Trim() + "`n")

if (-not $OutFile) { $OutFile = Join-Path $repoRoot "release-notes-$Version.md" }
[System.IO.File]::WriteAllText($OutFile, $notes.Replace("`r`n", "`n"), (New-Object System.Text.UTF8Encoding($false)))
Write-Host "Release notes written to $OutFile ($($out.Count + 1) logical lines)."
Write-Host "Publish with: gh release create $Version --verify-tag --title `"$Version`" --notes-file `"$OutFile`""
