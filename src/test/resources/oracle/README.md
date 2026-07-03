# Chess-engine oracles

This folder separates oracle files by the engine that produced the verdicts and
by the source of the tested positions:

```text
cha/ashlar-pgn/        D3-Chess/CHA output over Ashlar's PGN final positions
chasolver/ashlar-pgn/  Rust chasolver output over the same Ashlar positions
chasolver/curated/     the upstream curated chasolver position set
d3chess/               the upstream D3-Chess ground-truth test vectors
python-chess/          python-chess output over Ashlar PGN fixtures
```

The `ashlar-pgn` files are generated from the final FENs cached in the
`PgnFen` fixtures. The CHA generator calls Miguel Ambrona's D3-Chess C++
implementation through WSL. The chasolver generator calls Miguel Ambrona's Rust
`chasolver` implementation through WSL. Both write one row per distinct final
FEN.

`chasolver/curated/positions.txt` is different: it is copied from Miguel
Ambrona's chasolver repository and is the upstream curated position set itself.
The tests under `againstchasolvercurated` read its `WB`, `W-`, `-B`, and `--`
classifications directly instead of treating it as generated output from this
project's PGN fixtures.

`d3chess/test-vectors.txt` is copied from Miguel Ambrona's D3-Chess repository:
the ground-truth test vectors of the FUN 2022 paper, one position per line with
a `WB`/`W-`/`-B`/`--` helpmate classification. `TestUnwinnableSemiStatic` runs
the permanent Theorem 12 soundness sweep over it.

The TSV columns are:

```text
fen	fullWhite	fullBlack	quickWhite	quickBlack
```

`cha/ashlar-pgn/mobility.tsv` uses the same final FEN source, but writes one
row per piece in every distinct position; the `toSquares` column is the
saturated mobility region CHA computed for that piece:

```text
fen	side	pieceType	from	toSquares
```

With the 22.0.0 paper-formulation engine this dump is compared **by
implication, not equality** (`TestMobilityAgainstChaMobilityOracle`): cha
evolved beyond the paper (text footnote 12) and tightens mobility below the
paper's Figure 6/7 fixpoint in locked pawn structures, so cha's region must be
a *subset* of ashlar's for every piece - ashlar computes the least fixpoint,
and any cha square outside it would mean ashlar under-approximates, breaking
the Theorem 12 admissibility argument. Measured split, pinned in the test:
16758 of 16845 rows bit-identical, 87 rows cha-strictly-tighter.

Until 22.0.0 this folder also carried `cha/ashlar-pgn/semistatic.tsv`, a dump
of CHA's semi-static verdicts and helper sets compared field-by-field against
the former cha-port internals. It was retired with the port: cha's semi-static
carries beyond-paper case-splitting while the paper's α-reading is stronger
elsewhere, so no implication holds in either direction at that layer - the
semi-static layer is instead covered by the Theorem 12 soundness sweep over the
D3-Chess ground truth (`TestUnwinnableSemiStatic`) and by the public-verdict
oracles.

## One-time Windows setup

1. Install WSL with Ubuntu from PowerShell or Windows Terminal:

```powershell
wsl --install -d Ubuntu
```

2. Reboot Windows if the installer asks for it.

3. Start Ubuntu from the Start menu, Windows Terminal, or PowerShell:

```powershell
wsl -d Ubuntu
```

4. In Ubuntu, install the build tools:

```bash
sudo apt update
sudo apt install -y build-essential git make
```

## Build D3-Chess in Ubuntu

Clone into the Ubuntu home directory, not into `/mnt/c/...`. Git operations on
the Windows-mounted filesystem can fail on permission/file-mode updates.

```bash
cd ~
git clone https://github.com/miguel-ambrona/D3-Chess.git
```

Build and install the Stockfish library used by D3-Chess:

```bash
cd ~/D3-Chess/lib/stockfish
make get-stockfish
make
sudo make install
sudo ldconfig
```

Build D3-Chess itself:

```bash
cd ~/D3-Chess/src
make
```

Optional smoke test:

```bash
LD_LIBRARY_PATH=/usr/local/lib ./cha
```

## Regenerate the oracle

Run this from the ashlar-chess checkout on Windows:

```powershell
mvn -q org.codehaus.mojo:exec-maven-plugin:3.6.2:java "-Dexec.classpathScope=test" "-Dexec.mainClass=io.github.dlbbld.ashlarchess.test.generate.GenerateAmbronaUnwinnabilityOracle"
```

By default the generator asks WSL for `$HOME/D3-Chess`. If D3-Chess lives
somewhere else inside Ubuntu, pass that path as the only Java argument:

```powershell
mvn -q org.codehaus.mojo:exec-maven-plugin:3.6.2:java "-Dexec.classpathScope=test" "-Dexec.mainClass=io.github.dlbbld.ashlarchess.test.generate.GenerateAmbronaUnwinnabilityOracle" "-Dexec.args=/home/<user>/D3-Chess"
```

Equivalent system-property form:

```powershell
mvn -q org.codehaus.mojo:exec-maven-plugin:3.6.2:java "-Dexec.classpathScope=test" "-Dexec.mainClass=io.github.dlbbld.ashlarchess.test.generate.GenerateAmbronaUnwinnabilityOracle" "-Dambrona.d3.path=/home/<user>/D3-Chess"
```

The unwinnability generator compiles `tools/ambrona-oracle/cha_oracle.cpp` into `/tmp` inside
WSL, streams every distinct final FEN to it, and rewrites
`src/test/resources/oracle/cha/ashlar-pgn/unwinnability.tsv` with LF line endings.

The oracle comparison tests live in the unwinnability test package, which is
excluded from default Maven test runs. Run them explicitly with:

```powershell
mvn -q "-Dtest.excludes=" "-Dtest=TestAmbronaUnwinnabilityFullOracleComparison,TestAmbronaUnwinnabilityQuickOracleComparison,TestChasolverUnwinnabilityFullOracleComparison,TestChasolverUnwinnabilityQuickOracleComparison" test
```
