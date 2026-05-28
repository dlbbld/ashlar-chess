# Ambrona oracles

`ambrona-unwinnability.tsv` is generated from the final FENs cached in the
`PgnFen` fixtures. The generator calls Miguel Ambrona's D3-Chess C++
implementation through WSL and writes one row per distinct final FEN.

The TSV columns are:

```text
fen	fullWhite	fullBlack	quickWhite	quickBlack
```

`ambrona-mobility.tsv` uses the same final FEN source, but writes one row per
piece in every distinct position. The `toSquares` column is a comma-separated
list of squares reached by Ambrona's saturated semistatic movement variable for
that piece.

```text
fen	side	pieceType	from	toSquares
```

`ambrona-semistatic.tsv` uses the same final FEN source and writes Ambrona rows
for semistatic verdicts and helper sets.

```text
fen	side	kind	subject	value
```

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

Run this from the clean-chess checkout on Windows:

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
`src/test/resources/oracle/ambrona-unwinnability.tsv` with LF line endings.

To regenerate the mobility oracle, run:

```powershell
mvn -q org.codehaus.mojo:exec-maven-plugin:3.6.2:java "-Dexec.classpathScope=test" "-Dexec.mainClass=io.github.dlbbld.ashlarchess.test.generate.GenerateAmbronaMobilityOracle"
```

The optional D3-Chess path argument and `ambrona.d3.path` system property work
the same way as for the unwinnability oracle. The mobility generator compiles
`tools/ambrona-oracle/mobility_oracle.cpp`.

To regenerate the semistatic oracle, run:

```powershell
mvn -q org.codehaus.mojo:exec-maven-plugin:3.6.2:java "-Dexec.classpathScope=test" "-Dexec.mainClass=io.github.dlbbld.ashlarchess.test.generate.GenerateAmbronaSemiStaticOracle"
```

The optional D3-Chess path argument and `ambrona.d3.path` system property work
the same way as for the unwinnability oracle. The semistatic generator compiles
`tools/ambrona-oracle/semistatic_oracle.cpp`.

To compare the generated mobility oracle against the Java mobility
implementation without turning the mismatch report into a JUnit failure, run:

```powershell
mvn -q org.codehaus.mojo:exec-maven-plugin:3.6.2:java "-Dexec.classpathScope=test" "-Dexec.mainClass=io.github.dlbbld.ashlarchess.unwinnability.CompareAmbronaMobilityOracle"
```

To compare the generated semistatic oracle without turning the mismatch report
into a JUnit failure, run:

```powershell
mvn -q org.codehaus.mojo:exec-maven-plugin:3.6.2:java "-Dexec.classpathScope=test" "-Dexec.mainClass=io.github.dlbbld.ashlarchess.unwinnability.CompareAmbronaSemiStaticOracle"
```

The corresponding JUnit baseline tests live in the unwinnability test package,
which is excluded from default Maven test runs. Run them explicitly with:

```powershell
mvn -q "-Dtest.excludes=" "-Dtest=TestAmbronaMobilityOracleComparison,TestAmbronaSemiStaticOracleComparison" test
```
