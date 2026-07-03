# Manifesto

Why ashlar-chess exists — the motivation behind the project, stated once, in one place. The technical scope manifest
(what is in, what is deliberately out) lives in [`specification.md`](specification.md); this document is the *why*.

## The gap

When you ask a chess library "can this side still win?" — the unwinnability question behind FIDE's dead-position rule
(Article 5.2.2) and the mating-material exception in timeout adjudication (Article 6.9) — almost every library gets
the hard cases wrong. Blocked pawn walls, wrong-bishop endgames, forced-only-moves continuations: dead, but not
"insufficient material", and therefore misjudged.

Miguel Ambrona solved this problem. His FUN 2022 paper gave the algorithm, and his own implementations — the C++
`cha`/D3-Chess and its Rust successor [`chasolver`](https://github.com/miguel-ambrona/chasolver) — carry it in those
languages. On the Java side, the topic was simply not covered. I experienced that as a gap, and ashlar-chess exists
to close it: the unwinnability problem, treated as a first-class rule-correctness domain, in Java.

## An algorithm with a proof

It is rare, in practical software, to implement an algorithm that comes with an actual mathematical proof. The FUN
2022 paper, [*A Practical Algorithm for Chess Unwinnability*](https://chasolver.org/FUN22-full.pdf), proves its
semi-static core sound — the mobility over-approximation, the lemmas, Theorem 12. An algorithm like that deserves a
*faithful* implementation: code traceable to the paper and its proofs, not to another codebase.

That is what ashlar-chess carries: a clean-room implementation of the paper, governed by the committed specification
[`fun22-spec.md`](fun22-spec.md) — including the paper's own footnotes where they are load-bearing (the Figure 5
reward-chaining heuristic, the Figure 10 loop guard and material leaves; the exact implemented/not-implemented
inventory is in `fun22-spec.md` section 6). It *tries* to be a reference implementation of the paper. It does not
claim the title — the claim is made for it by validation: every definite verdict cross-checked against Ambrona's own
implementations and the published ground-truth test vectors, with zero soundness contradictions.

## The values

Everything else in this repository restates, in its own domain, the same few convictions:

- **Correctness first.** Every optimized production path answers to a readable oracle — the from-scratch reference
  implementation was relocated into the test tree, never deleted, and every release re-proves the bitboard backend
  against it.
- **Honor the spec.** Where an algorithm has a paper, the code follows the paper; deviations are documented with
  their reasons, or they do not happen.
- **Verdicts are proofs.** A definite answer is always sound; where the engine cannot prove, it says so
  (`UNDETERMINED`) instead of guessing.
- **Finish it.** A fixed, deliberately chosen scope, brought to exceptional quality — then maintained, not grown
  forever.
