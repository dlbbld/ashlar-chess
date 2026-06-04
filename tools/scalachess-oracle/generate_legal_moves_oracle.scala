//> using scala 3.8.2
//> using repository https://jitpack.io
//> using dep com.github.lichess-org.scalachess::scalachess:17.15.5

/*
 * Generate the scalachess legal-move-generation oracle.
 *
 * Scans every PGN under each covered move-rule-mechanics bucket, replays the game
 * with scalachess (honouring the optional [FEN]/[SetUp] header), and records the
 * sorted set of legal UCIs at each visited position (before each move, plus the
 * position after the final move). Writes one JSON object per PGN to
 * src/test/resources/oracle/scalachess/move-gen/<bucket>.jsonl.
 *
 * This is the scalachess counterpart of src/test/python/generate_move_gen_oracle.py:
 * identical bucket scope, identical JSONL schema, so the same Java reader consumes
 * both oracles. The JSONL files are committed and read by the Java cross-validation
 * test; mvn never invokes scala-cli.
 *
 * JSONL schema (one record per line, keys sorted alphabetically -> "perPly" < "pgn"):
 *
 *   Record := {"perPly": [Ply, ...], "pgn": <string>}
 *   Ply    := {"legalMovesUci": [<string>, ...]}   # sorted ascending
 *
 * perPly[0]            = legal moves at the start position (start FEN)
 * perPly[k]            = legal moves after the k-th played move
 * perPly[halfMoveCount]= empty list for mate/stalemate
 *
 * Reproducibility: scalachess 17.15.5 is the pinned version of record, resolved
 * from JitPack. Requires a JDK 21 runtime (scalachess class files are Java 21);
 * the Bloop build server is disabled on Windows. Run with:
 *
 *   scala-cli run tools/scalachess-oracle/generate_legal_moves_oracle.scala \
 *     --server=false --jvm temurin:21 -- <repoRoot>
 *
 * <repoRoot> defaults to the process working directory when omitted.
 */

import java.nio.file.{ Files, Path, Paths }
import java.nio.charset.StandardCharsets
import scala.jdk.CollectionConverters.*
import scala.util.Using

import chess.{ Replay, Position }
import chess.format.pgn.PgnStr

val ScalachessVersion = "17.15.5"

// Same 22 move-rule-mechanics buckets as generate_move_gen_oracle.py. Excludes
// stalemate/checkmate-only and long-game corpora by design (see that script).
val Buckets = List(
  "parserFenMechanics",
  "basic/movingPiece/white",
  "basic/movingPiece/black",
  "basic/capture/systematic/white",
  "basic/capture/systematic/black",
  "basic/capture/lastMove",
  "basic/enPassantCapture/white",
  "basic/enPassantCapture/black",
  "basic/promotionPiece/white",
  "basic/promotionPiece/black",
  "basic/promotionSquare/white",
  "basic/promotionSquare/black",
  "basic/check/white",
  "basic/check/black",
  "basic/doubleCheck/noCheckmate/white",
  "basic/doubleCheck/noCheckmate/black",
  "basic/doubleCheck/checkmate/white",
  "basic/doubleCheck/checkmate/black",
  "basic/castling/simplest/white",
  "basic/castling/simplest/black",
  "basic/castling/special/white",
  "basic/castling/special/black"
)

// scalachess encodes castling as king-to-rook-square (e1h1 / e1a1, UCI_Chess960
// style); ashlar-chess and python-chess use the standard king-two-squares form
// (e1g1 / e1c1). Normalise genuine castles only -- a blind e1h1->e1g1 rewrite
// would corrupt a real rook move from e1 to h1. Detection is structural via
// chess.Move.castle; the king's standard target file is g (kingside) or c
// (queenside), on the king's own rank.
def uciOf(m: chess.Move): String =
  if m.castle.isDefined then
    val orig = m.orig.key // king origin, e.g. "e1"
    val dest = m.dest.key // rook square,  e.g. "h1" / "a1"
    val targetFile = if dest.charAt(0) > orig.charAt(0) then 'g' else 'c'
    s"$orig$targetFile${orig.charAt(1)}"
  else m.toUci.uci

def legalUcisPerPly(pgnText: String, pgnName: String): List[List[String]] =
  val replay = Replay.mainline(PgnStr(pgnText)) match
    case Left(err)  => throw new RuntimeException(s"$pgnName: parse failed: $err")
    case Right(res) => res.valid.fold(err => throw new RuntimeException(s"$pgnName: invalid: $err"), identity)
  val positions: List[Position] = replay.setup.position :: replay.chronoMoves.map(_.after)
  // scalachess lists each castle twice (king-two-squares e1g1 AND king-to-rook
  // e1h1); both normalise to e1g1 via uciOf, so .distinct collapses the pair to
  // the single entry python-chess / ashlar emit.
  positions.map(_.legalMoves.map(uciOf).distinct.sorted)

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

// Matches python json.dumps(sort_keys=True, separators=(",", ":")): compact, no
// spaces, "perPly" before "pgn". UCIs are [a-h][1-8](+promo) so need no escaping.
def recordJson(pgnName: String, perPly: List[List[String]]): String =
  val pliesJson = perPly
    .map(ucis => s"""{"legalMovesUci":[${ucis.map(u => s"\"$u\"").mkString(",")}]}""")
    .mkString(",")
  s"""{"perPly":[$pliesJson],"pgn":"${jsonEscape(pgnName)}"}"""

@main def generate(repoRootArg: String*): Unit =
  val repoRoot = Paths.get(if repoRootArg.nonEmpty then repoRootArg.head else System.getProperty("user.dir"))
  val pgnRoot = repoRoot.resolve("src/test/resources/pgn")
  val oracleRoot = repoRoot.resolve("src/test/resources/oracle/scalachess/move-gen")

  var totalFixtures = 0
  var totalPositions = 0

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
      val perPly = legalUcisPerPly(text, name)
      totalPositions += perPly.size
      sb.append(recordJson(name, perPly)).append('\n')
    Files.writeString(outputPath, sb.toString, StandardCharsets.UTF_8)

    totalFixtures += pgnPaths.size
    println(f"$bucket%-45s ${pgnPaths.size}%4d fixtures -> ${repoRoot.relativize(outputPath)}")

  println(s"\n${Buckets.size} buckets, $totalFixtures fixtures total, $totalPositions positions")
  println(s"scalachess version: $ScalachessVersion")
