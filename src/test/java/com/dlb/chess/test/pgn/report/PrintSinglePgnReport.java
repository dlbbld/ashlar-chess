package com.dlb.chess.test.pgn.report;

import com.dlb.chess.report.Reporter;
import com.dlb.chess.test.pgn.setup.PgnTestCaseCatalog;
import com.dlb.chess.test.pgntest.enums.PgnTest;

public class PrintSinglePgnReport {

  public static void main(String[] args) throws Exception {
    printReportForPgn();
    System.out.println("=============================================");
    printReportForPgnString();

  }

  private static void printReportForPgn() throws Exception {
    printReport("05_claim_for_own_move_correct_but_makes_move_on_board.pgn");
  }

  private static void printReportForPgnString() {
    final var pgnString = """
        [Event "Rated Blitz game"]
            [Site "https://lichess.org/2PEOuq3S"]
            [Date "2022.07.31"]
            [White "cab1974"]
            [Black "ThiagoSSoler"]
            [Result "1-0"]
            [UTCDate "2022.07.31"]
            [UTCTime "17:57:13"]
            [WhiteElo "2061"]
            [BlackElo "2174"]
            [WhiteRatingDiff "+8"]
            [BlackRatingDiff "-7"]
            [Variant "Standard"]
            [TimeControl "300+0"]
            [ECO "A35"]
            [Opening "English Opening: Symmetrical Variation, Four Knights Variation"]
            [Termination "Normal"]
            [Annotator "lichess.org"]

            1. c4 c5 2. Nc3 Nc6 3. Nf3 Nf6 { A35 English Opening: Symmetrical Variation, Four Knights Variation } 4. d4 cxd4 5. Nxd4 g6 6. e4 Bg7 7. Be3 O-O 8. Be2 d6 9. O-O Bd7 10. f3 Rc8 11. Qd2 Na5 12. b3 Ne8 13. Rac1 Nc7 14. Rfd1 Ne6 15. Qe1 Qb6 16. Qf2 Nc5 17. Nd5 Qd8 18. b4 Na4 19. bxa5 e6 20. Nc3 Qxa5 21. Nxa4 Bxa4 22. Rd2 Rc5 23. Nb3 Bxb3 24. axb3 Rc6 25. Rcd1 Qc7 26. Bd4 Bh6 27. Be3 Bg7 28. f4 e5 29. f5 f6 30. g4 Rd8 31. h4 Qa5 32. Bf3 Qb4 33. Rd3 b6 34. Bd2 Qa3 35. Be3 Qb4 36. Qd2 Qa3 37. Kg2 a6 38. Qf2 b5 39. cxb5 axb5 40. R1d2 Qb4 41. Kg3 Rc1 42. Rd1 Rc6 43. R1d2 Rc1 44. Rd1 Rc6 45. Bd2 Qa3 46. Be3 Qb4 47. Qd2 Qa3 48. Rc1 Rb6 49. Qc2 Ra6 50. Rc3 Rda8 51. Rc8+ Rxc8 52. Qxc8+ Bf8 53. Rc7 Ra8 54. Qd7 { Black resigns. } 1-0
        """;
    Reporter.printReport(pgnString);
  }

  static void printReport(String pgnName) throws Exception {
    final PgnTest pgnTest = PgnTestCaseCatalog.findPgnTestPgnNotListed(pgnName);
    System.out.println(pgnName);
    Reporter.printReport(pgnTest.getFolderPath(), pgnName);
  }

}
