/*
  Local Stockfish runner for BoardReplayPerformanceSurvey.

  Input rows are tab-separated: initial FEN, then a space-separated UCI move
  list. The timed loops stay inside WSL so process I/O is not part of the
  measurement.
*/

#include "movegen.h"
#include "position.h"
#include "stockfish.h"
#include "thread.h"
#include "uci.h"

#include <chrono>
#include <cstdint>
#include <cstdlib>
#include <iostream>
#include <sstream>
#include <stdexcept>
#include <string>
#include <vector>

namespace {

struct Game {
  std::string fen;
  std::vector<std::string> moves;
};

struct Measurement {
  long long nanoseconds;
  std::uint64_t checksum;
};

std::vector<std::string> split_moves(const std::string& move_text) {
  std::vector<std::string> moves;
  std::istringstream stream(move_text);
  std::string move;
  while (stream >> move) {
    moves.push_back(move);
  }
  return moves;
}

std::vector<Game> read_games() {
  std::vector<Game> games;
  std::string line;
  while (std::getline(std::cin, line)) {
    if (!line.empty() && line.back() == '\r') {
      line.pop_back();
    }
    if (line.empty()) {
      continue;
    }

    const std::size_t separator = line.find('\t');
    if (separator == std::string::npos) {
      throw std::runtime_error("Missing tab separator in input row");
    }

    games.push_back({line.substr(0, separator), split_moves(line.substr(separator + 1))});
  }
  return games;
}

Move parse_legal_move(Position& pos, const std::string& move_text) {
  std::string mutable_move_text = move_text;
  const Move move = UCI::to_move(pos, mutable_move_text);
  if (move == MOVE_NONE || !MoveList<LEGAL>(pos).contains(move)) {
    throw std::runtime_error("Illegal Stockfish move: " + move_text + " in " + pos.fen());
  }
  return move;
}

Measurement measure_construct(const std::vector<Game>& games, int rounds) {
  std::uint64_t checksum = 0;
  const auto start = std::chrono::steady_clock::now();
  for (int round = 0; round < rounds; ++round) {
    for (const Game& game : games) {
      Position pos;
      StateInfo state;
      pos.set(game.fen, false, &state, Threads.main());
      checksum += pos.key();
    }
  }
  const auto end = std::chrono::steady_clock::now();
  return {std::chrono::duration_cast<std::chrono::nanoseconds>(end - start).count(), checksum};
}

Measurement measure_replay(const std::vector<Game>& games, int rounds) {
  std::uint64_t checksum = 0;
  const auto start = std::chrono::steady_clock::now();
  for (int round = 0; round < rounds; ++round) {
    for (const Game& game : games) {
      std::vector<StateInfo> states(game.moves.size() + 1);
      Position pos;
      pos.set(game.fen, false, &states[0], Threads.main());
      for (std::size_t i = 0; i < game.moves.size(); ++i) {
        const Move move = parse_legal_move(pos, game.moves[i]);
        pos.do_move(move, states[i + 1]);
      }
      checksum += static_cast<std::uint64_t>(pos.game_ply());
    }
  }
  const auto end = std::chrono::steady_clock::now();
  return {std::chrono::duration_cast<std::chrono::nanoseconds>(end - start).count(), checksum};
}

Measurement measure_replay_with_probe(const std::vector<Game>& games, int rounds) {
  std::uint64_t checksum = 0;
  const auto start = std::chrono::steady_clock::now();
  for (int round = 0; round < rounds; ++round) {
    for (const Game& game : games) {
      std::vector<StateInfo> states(game.moves.size() + 1);
      Position pos;
      pos.set(game.fen, false, &states[0], Threads.main());
      for (std::size_t i = 0; i < game.moves.size(); ++i) {
        const Move move = parse_legal_move(pos, game.moves[i]);
        StateInfo probe_state;
        pos.do_move(move, probe_state);
        pos.undo_move(move);
        pos.do_move(move, states[i + 1]);
      }
      checksum += static_cast<std::uint64_t>(pos.game_ply());
    }
  }
  const auto end = std::chrono::steady_clock::now();
  return {std::chrono::duration_cast<std::chrono::nanoseconds>(end - start).count(), checksum};
}

void print_measurement(const char* name, const Measurement& measurement) {
  std::cout << name << '\t' << measurement.nanoseconds << '\t' << measurement.checksum << '\n';
}

}  // namespace

int main(int argc, char* argv[]) {
  if (argc != 3) {
    std::cerr << "Usage: board_replay_stockfish <warmup-rounds> <measure-rounds>\n";
    return 2;
  }

  try {
    init_stockfish();
    CommandLine::init(argc, argv);

    const int warmup_rounds = std::atoi(argv[1]);
    const int measure_rounds = std::atoi(argv[2]);
    const std::vector<Game> games = read_games();

    for (int i = 0; i < warmup_rounds; ++i) {
      measure_construct(games, 1);
      measure_replay(games, 1);
      measure_replay_with_probe(games, 1);
    }

    print_measurement("construct", measure_construct(games, measure_rounds));
    print_measurement("replay", measure_replay(games, measure_rounds));
    print_measurement("replayWithProbe", measure_replay_with_probe(games, measure_rounds));

    Threads.stop = true;
    Threads.set(0);
    return 0;
  } catch (const std::exception& exception) {
    std::cerr << exception.what() << '\n';
    Threads.stop = true;
    Threads.set(0);
    return 1;
  }
}
