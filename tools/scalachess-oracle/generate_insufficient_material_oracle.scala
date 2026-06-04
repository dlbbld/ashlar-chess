//> using scala 3.8.2
//> using repository https://jitpack.io
//> using dep com.github.lichess-org.scalachess::scalachess:17.15.5

/*
 * Generate the scalachess insufficient-material oracle.
 *
 * Insufficient material is a function of the position alone, not the path that led
 * to it, so this oracle is position-only: for every PGN in the four core
 * insufficient-material buckets it replays the game with scalachess, takes the final
 * position, and records scalachess's three verdicts plus the final FEN. The Java test
 * (TestInsufficientMaterialAgainstScalachessOracle) builds a history-less ashlar board
 * from the recorded FEN and asserts ashlar's three predicates against these values -
 * mirroring TestInsufficientMaterialAgainstPythonChessOracle, but with scalachess as
 * the third independent witness on this subtle predicate.
 *
 * scalachess frames the per-side verdict relative to the side to move
 * (player/opponent); this generator maps it to absolute white/black via the position's
 * colour, matching python-chess's has_insufficient_material(WHITE|BLACK).
 *
 * Output: src/test/resources/oracle/scalachess/insufficient-material/<bucket>.jsonl,
 * one JSON object per PGN, keys sorted alphabetically:
 *
 *   {"fen": <string>, "hasInsufficientMaterialBlack": <bool>,
 *    "hasInsufficientMaterialWhite": <bool>, "isInsufficientMaterial": <bool>,
 *    "pgn": <string>}
 *
 * Reproducibility: scalachess 17.15.5 from JitPack, JDK 21, Bloop disabled. Run with:
 *   scala-cli run tools/scalachess-oracle/generate_insufficient_material_oracle.scala \
 *     --server=false --jvm temurin:21 -- <repoRoot>
 */

import java.nio.file.{ Files, Paths }
import java.nio.charset.StandardCharsets
import scala.jdk.CollectionConverters.*
import scala.util.Using

import chess.{ Replay, Color }
import chess.format.Fen
import chess.format.pgn.PgnStr

val ScalachessVersion = "17.15.5"

val Buckets = List(
  "basic/insufficientMaterial/both",
  "basic/insufficientMaterial/onlyWhite",
  "basic/insufficientMaterial/onlyBlack",
  "basic/insufficientMaterial/none"
)

final case class Verdict(fen: String, both: Boolean, white: Boolean, black: Boolean)

def finalVerdict(pgnText: String, pgnName: String): Verdict =
  val replay = Replay.mainline(PgnStr(pgnText)) match
    case Left(err)  => throw new RuntimeException(s"$pgnName: parse failed: $err")
    case Right(res) => res.valid.fold(err => throw new RuntimeException(s"$pgnName: invalid: $err"), identity)
  val game = replay.state
  val pos = game.position
  val variant = pos.variant
  val player = variant.playerHasInsufficientMaterial(pos)   // side to move
  val opponent = variant.opponentHasInsufficientMaterial(pos)
  val whiteToMove = pos.color == Color.White
  Verdict(
    fen = Fen.write(game).value,
    both = variant.isInsufficientMaterial(pos),
    white = if whiteToMove then player else opponent,
    black = if whiteToMove then opponent else player
  )

def jsonEscape(s: String): String =
  val sb = new StringBuilder
  s.foreach {
    case '"'           => sb.append("\\\"")
    case '\\'          => sb.append("\\\\")
    case '\n'          => sb.append("\\n")
    case '\r'          => sb.append("\\r")
    case '\t'          => sb.append("\\t")
    case c if c < 0x20 => sb.append("\\u%04x".format(c.toInt))
    case c             => sb.append(c)
  }
  sb.toString

def recordJson(pgnName: String, v: Verdict): String =
  s"""{"fen":"${jsonEscape(v.fen)}","hasInsufficientMaterialBlack":${v.black},""" +
    s""""hasInsufficientMaterialWhite":${v.white},"isInsufficientMaterial":${v.both},""" +
    s""""pgn":"${jsonEscape(pgnName)}"}"""

@main def generate(repoRootArg: String*): Unit =
  val repoRoot = Paths.get(if repoRootArg.nonEmpty then repoRootArg.head else System.getProperty("user.dir"))
  val pgnRoot = repoRoot.resolve("src/test/resources/pgn")
  val oracleRoot = repoRoot.resolve("src/test/resources/oracle/scalachess/insufficient-material")

  var totalFixtures = 0
  for bucket <- Buckets do
    val pgnDir = pgnRoot.resolve(bucket)
    if !Files.isDirectory(pgnDir) then throw new RuntimeException(s"PGN directory not found: $pgnDir")

    val pgnPaths = Using.resource(Files.list(pgnDir)) { stream =>
      stream.iterator.asScala.filter(_.getFileName.toString.endsWith(".pgn")).toList
    }.sortBy(_.getFileName.toString)
    if pgnPaths.isEmpty then throw new RuntimeException(s"no PGN files in $pgnDir")

    val outputPath = oracleRoot.resolve(s"$bucket.jsonl")
    Files.createDirectories(outputPath.getParent)

    val sb = new StringBuilder
    for p <- pgnPaths do
      val name = p.getFileName.toString
      val text = Files.readString(p, StandardCharsets.UTF_8)
      sb.append(recordJson(name, finalVerdict(text, name))).append('\n')
    Files.writeString(outputPath, sb.toString, StandardCharsets.UTF_8)

    totalFixtures += pgnPaths.size
    println(f"$bucket%-42s ${pgnPaths.size}%4d fixtures -> ${repoRoot.relativize(outputPath)}")

  println(s"\n${Buckets.size} buckets, $totalFixtures fixtures total")
  println(s"scalachess version: $ScalachessVersion")
