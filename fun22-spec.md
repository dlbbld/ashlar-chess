# FUN22 unwinnability — clean-room specification

This document is the **governing specification** for ashlar's unwinnability engine (the
`io.github.dlbbld.ashlarchess.unwinnability` package internals, vendored in 22.0.0 from the validated
`fun22-reference` project). It is derived **only** from Miguel Ambrona's FUN 2022 paper, *"A Practical Algorithm for
Chess Unwinnability"* — the **full version** (the one with complete proofs of Lemmas 10 and 11). Neither ashlar's
former cha port nor Ambrona's D3-Chess/`cha`/`chasolver` source was consulted. Review the engine code against this
document (and the paper), not against any other codebase.

Sections 1–5 below specify the semi-static algorithm (Theorem 12) in full detail; section 6 maps the remaining paper
figures (the search side) to their implementing classes and records their footnote treatments. Section 7 records what
ashlar deliberately adds on top of the paper.

Section/figure/line references point at the full version unless noted.

---

## 0. Scope

The heart of the engine is **Theorem 12**:

> `UnwinnableStatic(pos, c, Mobility(pos)) = true  ⟹  pos is unwinnable for c`

i.e. the composition of the **mobility over-approximation** (§3.1, Fig 6–7, Lemma 8, Corollary 9) with the
**semi-static check** (§3.2, Fig 8, Lemmas 10–11). The semi-static check is sound and deliberately incomplete: it may
decline on truly-unwinnable positions, but a `true` must never be wrong.

Around it, the paper's full algorithm (Fig 9 = semi-static shortcut + Find-Helpmate search under iterative deepening)
and quick algorithm (Fig 10) are implemented per section 6.

---

## 1. Board model and notation

Squares are integers `0..63` with `a1 = 0, b1 = 1, …, h8 = 63` (little-endian rank-file; the ashlar/Stockfish layout —
`file(s) = s & 7`, `rank(s) = s >> 3`, coinciding with `Square.ordinal()`).

A **piece** `P` is `(type, color, sq)` with `type ∈ {K,Q,R,B,N,P}`, `color ∈ {w,b}`, `sq ∈ 0..63`. A **position**
`pos` is a set of pieces on distinct squares (`SemiStaticPosition`/`SemiStaticPiece`). `color(s)` is the light/dark
colour of square `s`.

Per-square geometric sets (all "over an empty board"; implemented in `SquareGeometry`):

| set | meaning |
|-----|---------|
| `α(s)` | orthogonally adjacent squares (share a border; opposite square colour) |
| `β(s)` | diagonally adjacent squares (same square colour) |
| `δ(s) = α(s) ∪ β(s)` | all ≤ 8 king-neighbours — the **king's escape squares** |
| `ν(s)` | squares at knight-distance 1 |
| `ω_w(s)` / `ω_b(s)` | the ≤1 square from which a **white / black** pawn reaches `s` by one non-capturing push |
| `π_w(s)` / `π_b(s)` | the squares from which a **white / black** pawn attacks `s` |

Double pawn pushes need no special case: reachability of a rank-4 square is already implied stepwise through the
rank-3 square, and admissibility (§3) makes this over-approximation sound.

### 1.1 Predecessors (Fig 6 preamble, full version l.523–555)

Implemented in `Predecessors`. `predP(s)` — squares from which `P.type` reaches `s` in one non-capture move:

```
predP(s) = ν(s)              if P.type = N
         = β(s)              if P.type = B
         = α(s)              if P.type = R
         = δ(s)              if P.type ∈ {Q,K}
         = ω_{P.color}(s)    if P.type = P
```

Note sliders use only the **adjacent** squares (β/α/δ). Long slides emerge stepwise from the Fig 7 fixpoint; enemy
blockers are (soundly) ignored.

`pred-captP(s)` — squares from which `P` *captures* onto `s`:

```
pred-captP(s) = π_{P.color}(s)   if P.type = P
              = predP(s)         otherwise
```

`prom(P)` — promotion squares, defined only for pawns:

```
prom(P) = {a8..h8}   if P.color = w
        = {a1..h1}   if P.color = b
        = ∅          otherwise
```

`attackers(s) = { P ∈ pos : P.sq ∈ pred-captP(s) }` — pieces currently attacking `s`.

---

## 2. Mobility (Fig 6 rules + Fig 7 fixpoint)

Implemented in `Mobility`/`MobilitySolution`. Boolean variables over the current position:

- `M[P][s]` — piece `P` can *eventually* move to square `s`.
- `R[s][c]` — square `s` can eventually be reached (or is currently occupied) by a **non-king** piece of colour `c`.
- `C[P]` — piece `P` can be cleared from `P.sq` (by moving away or being captured).

### 2.1 Figure 6 — the implications (heads may repeat)

For a variable `V`, **`V` becomes 1 iff the body of *every* rule with head `V` is true** (conjunction *across* rules;
each body may itself be a disjunction/conjunction). All literals are positive ⇒ the system is monotone.

**Move** (non-pawn `P`, `s ≠ P.sq`):
```
M[P][s] ⟸ ⋁_{u ∈ predP(s)} M[P][u]
```

**Pawn move** (`P.type = P`, `s ≠ P.sq`). Let `F_s^c := C[P']` if some `P' ∈ pos` has `P'.color = c` and
`P'.sq = s`, else `F_s^c := true`:
```
M[P][s] ⟸ ( ⋁_{u ∈ predP(s)}      M[P][u] ) ∧ F_s^{¬P.color}     ← push (enemy on s clearable)
        ∨ ( ⋁_{u ∈ pred-captP(s)} M[P][u] ) ∧ R[s][¬P.color]     ← capture (enemy reaches s)
        ∨ ( ⋁_{u ∈ prom(P)}       M[P][u] )                       ← reached a promo sq ⇒ go anywhere
```
(The push's *own-colour* blocker on `s` is handled by the separate **not self-capture** rule below, so `F` here
guards the *enemy* on the push target; see §5 note P1.)

**Clearance** (`P ∈ pos`):
```
C[P] ⟸ ( ⋁_{s ≠ P.sq} M[P][s] ) ∨ ( ⋁_{P' ∈ pos, P'.color ≠ P.color} M[P'][P.sq] )
```

**Reachability** (`s ∈ S`, `c ∈ {w,b}`):
```
R[s][c] ⟸ ⋁_{P ∈ pos, P.color = c, P.type ≠ K} M[P][s]
```

**King attackers** (`P.type = K`, `s ≠ P.sq`):
```
M[P][s] ⟸ ⋀_{P' ∈ attackers(s), P'.color ≠ P.color} C[P']
```

**Not self-capture** (`P, P' ∈ pos`, `P ≠ P'`, `P.color = P'.color`):
```
M[P][P'.sq] ⟸ C[P']
```

A king thus needs **move ∧ king-attackers ∧ (not-self-capture if ally on `s`)** all true; a pawn needs **pawn-move ∧
(not-self-capture if ally on `s`)**.

### 2.2 Figure 7 — the fixpoint

```
1. M[P][s] = C[P] = R[s][c] = 0  for all P, s, c
2. M[P][P.sq] = 1  for all P ∈ pos          (a piece can "move" to its own square)
3. repeat: for every variable V still 0, if every Fig-6 rule with head V has a
   true body on the current state, set V = 1
4. until no variable changes
5. return { M[P][s] }
```

Monotone, so it converges; the least such fixpoint is **admissible**: `M[P][s] ≥ M*[P][s]` where `M*` is the true
mobility (Lemma 8 / Corollary 9). Over-approx is safe because the semi-static check is monotone-decreasing in `M`
(Lemma 10).

---

## 3. UnwinnableStatic (Fig 8)

Implemented in `UnwinnableSemiStatic`. Input `pos`, intended winner `c`, mobility `M`. Output `bool`.

```
1.  if en passant is possible OR any player has castling rights → return false
2.  region(P) := { s | M[P][s] = 1 }                       for each P ∈ pos
3.  K_c  := winner's king;   K_¬c := loser's king
4.  intruders := { P ∈ pos | P.color = c ∧ region(P) ∩ region(K_¬c) ≠ ∅ }
5.  if ∃ P ∈ intruders with P.type ≠ B                    → return false
6.  if ∃ P,P' ∈ intruders with color(P.sq) ≠ color(P'.sq) → return false
7.  att-region(P) := { s | pred-captP(s) ∩ region(P) ≠ ∅ } for each P ∈ pos
8.  blockers(s)   := { P ∈ pos | P.color ≠ c ∧ P.type ≠ K ∧ region(P)     ∩ α(s) ≠ ∅ }  (LOSER's non-king pieces — occupy a bordering square)
9.  assistants(s) := { P ∈ pos | P.color = c ∧            att-region(P) ∩ α(s) ≠ ∅ }  (WINNER's pieces — attack a bordering square)
10. if ∃ s ∈ region(K_¬c) such that
        |blockers(s)| + |assistants(s)| ≥ |α(s)|
        AND ∃ P ∈ pos with s ∈ att-region(P) ∧ P.color = c
    → return false
11. return true                                            (position is unwinnable for c)
```

Reading (§3.2, Lemma 11): a position is unwinnable if for **every** square either (i) the loser's king can't reach
it, or (ii) the winner can't attack it, or (iii) its **bordering squares `α(s)`** can't all be simultaneously blocked
(by a **loser** non-king piece occupying — the king can't capture its own piece) or covered (by a **winner** piece
attacking). Step 10 returns `false` as soon as one square fails all three — i.e. looks matable. Steps 5–6 enforce
Lemma 11's precondition: the only pieces that may enter the loser-king region (hence deliver check) are same-coloured
bishops (or none). In genuinely blocked positions the two kings' regions are disjoint, so the winner's king is *not*
an intruder and step 5 does not fire.

**Why `α(s)` and not all 8 neighbours `δ(s)`** (verified on the 500-dpi render: steps 8–10 carry the paper's ✣
share-a-border glyph): the `α(s)` squares have the *opposite* square colour to `s`, so once steps 5–6 guarantee that
only same-coloured bishops can enter the loser-king region, no bishop intruder can ever attack an `α(s)` square — the
only possible assistants there are pawns or the winner's king, and each of those covers at most one `α(s)` square
(Lemma 11 footnote). That is exactly what makes the step-10 *count* a sound proxy for simultaneous coverage. Figure
8's own footnote *a* confirms the restriction: "we could design a more complete check that looks at **all**
neighbours of `s`, but the condition on step 10 would be significantly more involved (to ensure monotonicity)." A
`δ`-based count is NOT covered by the paper's proof (a single piece can cover several `δ` squares at once, so the
count would under-estimate coverage and could wrongly conclude unwinnability).

**Soundness direction.** Every approximation widens the "possibly matable" set: `M` over-approximates reach (step
5/10 fire more easily), and step 10 counts *pieces* vs. `α(s)` squares (each blocker/assistant covers ≤ 1 of them
under the bishop precondition — Lemma 11 footnote — so the count over-estimates coverage). Over-estimating matability
can only turn a true `UNWINNABLE` into `false` (`UNKNOWN`), never the reverse. Hence `true` is always correct =
sound.

---

## 4. Theorem 12

`M ← Mobility(pos)` is admissible (Corollary 9); `UnwinnableStatic` is monotone decreasing in `M` (Lemma 10) and
sound on the true mobility `M*` (Lemma 11). Since `M ≥ M*`, `UnwinnableStatic(pos,c,M) = true ⟹
UnwinnableStatic(pos,c,M*) = true ⟹ pos unwinnable for c`. ∎

Precondition (step 1): the paper proves Lemma 8 only for positions **without castling rights and without a possible
en passant**; those cases return `false` (unknown) up front.

---

## 5. Resolved OCR ambiguities (audit trail)

The paper's figures use `≠ ∅ ∩ ∉ ≥` glyphs that do not survive text extraction. Each was resolved from the full
version's inline prose / proofs, not guessed:

- **Step 5** `P.type ≠ B`: full-version note — *"the set of intruders [must] be empty or formed entirely by
  bishops."*
- **Step 6** `color(P.sq) ≠ color(P'.sq)`: note — *"all intruders … be of the same square color."*
- **Step 8** `P.color ≠ c` (LOSER's non-king pieces), **step 9** `P.color = c` (WINNER's pieces): the two *differ* in
  side. The rendered Figure 8 annotates step 8 as *"the intended loser's pieces that can potentially block an
  adjacent square to s"* — the loser king cannot capture its own piece, so only a loser-owned piece is a reliable
  escape *blocker*; the winner's pieces instead *attack* escape squares (step 9). (An earlier draft wrongly set step
  8 to `= c` after a corrupted text extraction showed `= c` on both lines; corrected from the rendered figure — a
  soundness fix, since counting winner pieces here would undercount loser-owned blockers and could return
  `UNWINNABLE` for a matable position.)
- **Steps 8–10 neighbour set = `α(s)`, not `δ(s)`** (fixed 2026-07-03): at 220 dpi the ✣ share-a-border glyph was
  misread as the 8-neighbour glyph by two independent reviewers; the 500-dpi zoom is unambiguous, and both figure
  footnote *a* and the Lemma 11 footnote only make sense for `α`. An earlier draft (and implementation) used `δ(s)`
  with threshold `|δ(s)|` — empirically it produced zero contradictions over the corpus, but it is not covered by the
  paper's soundness proof. Lesson: verify glyph-level figure details at high DPI.
- **Note P1 — pawn push `F` factor (verified against the rendered figure):** the paper defines `F_s^c` via the piece
  of colour **≠ c** on `s` and uses `F_s^{P.color}`; this spec defines `F_s^c` via the piece of colour **= c** and
  uses `F_s^{¬P.color}`. Both denote exactly *"the enemy piece on the pawn's push target `s` can be cleared,"*
  matching the prose *"a possibly **enemy** piece on the target square can be cleared first,"* and the own-colour
  blocker is already handled by **not self-capture**. Either sign is a *valid* (sound) constraint — it only removes
  genuinely impossible mobility, preserving admissibility `M ≥ M*` — so this choice cannot break soundness; it is
  documented for fidelity.

---

## 6. The search side (Figures 5, 9, 10, 12, 13)

Implemented clean-room from the same paper, driven over ashlar's `Board` engine (make/unmake, legal move generation,
checkmate/stalemate — chess foundation, not unwinnability logic):

- **Figure 5 `Find-Helpmate`** → `FindHelpmate`: depth- and node-bounded DFS for a checkmate *by the intended
  winner*, with a depth-aware transposition table and the Figure 12 Score budget adjustment. Terminal leaves:
  checkmate, stalemate, the winner's bare king, and the Lemma 5/6 material shapes (`MaterialLemmas`). **Footnote b**
  (implemented; resolves deep helpmates like K+Q at small bounds): a Normal-scored move directly following a
  Reward-scored move is also rewarded. Footnote a (Stockfish move ordering) is deliberately not implemented —
  perf-only.
- **Figure 9 full routine** → `UnwinnableFullAnalyzer`: semi-static shortcut, then iterative deepening over
  Find-Helpmate; `WINNABLE` on an exhibited helpmate, `UNWINNABLE` on an uninterrupted exhaustion, else
  `UNDETERMINED` when the budget runs out. The paper leaves `bound(d)` as a parameter (its practical note, text
  footnote 13, fixes a small constant); ashlar uses one global 500 000-node budget across iterations with a depth
  ceiling of 100 (each iteration's node bound is the remaining global budget). **The transposition table is
  per-iteration — a deliberate deviation from the paper's prose, which says the table "can be shared between
  different calls".** The soundness of Figure 9 step 5-6 ("search not interrupted ⟹ Unwinnable") rests on the
  interrupted flag being monotone over every visit the table's entries summarize: within one iteration, an entry
  written by a depth-cut visit coexists with the raised flag, so that iteration can no longer claim `UNWINNABLE`. A
  *stale* entry from an earlier, depth-cut iteration would prune a later iteration's node *without re-raising its
  interrupt flag* — the later iteration could believe it exhausted the tree while cut lines hide behind the stale
  prune, a potential false `UNWINNABLE`. The absence-of-mate half of a stale entry is still a true statement; it is
  the exhaustion *witness* that does not survive sharing. (Surfaced by a Codex review of the transposition key;
  sharing was briefly implemented and reverted.) The search-state key includes the footnote-b reward-chain flag: a
  visit with the boost pending explores a strictly stronger budget shape than one without, so the two states must
  not prune each other.
- **Figure 10 quick routine** → `UnwinnableQuickAnalyzer`: forced-move advance, bounded DFS with a *global* depth-9
  interrupt, then the semi-static check gated to pawn/bishop/king material without semi-open files. **Footnote a**
  (implemented): the forced-move advance is loop-guarded (arbitrarily long single-move sequences exist; capping is
  verdict-preserving since a forced prefix is equivalence-preserving). **Footnote b** (implemented): the Lemma 5/6
  material positions are DFS leaves — that is what makes not-interrupted ⟹ `UNWINNABLE` fire on imminently
  terminating positions.
- **Figure 12 `Score`** → `Score`: Normal/Reward/Punish move classification; pure efficiency heuristic.
- **Figure 13 `Going-to-corner`** → `GoingToCorner`: rewards slow-piece (K/N) moves approaching the mating corner;
  the depth extension that lets bounded search finish long mating plans.
- **Lemmas 5/6** → `MaterialLemmas`: winner-side insufficient-material shapes (strict: *exactly one* knight; bishops
  of *one* square colour), plus the Figure 12 material condition (same shapes without the pawn-freeness gate).

Text footnote 4 (transposition cut) and footnote 13 (constant practical bound) are honoured as above. Text footnote
12 notes that `cha` evolved beyond the paper with extra heuristics — divergence between this engine and cha-lineage
implementations on *completeness* (who proves more) is therefore expected; divergence on *soundness* is a bug.

---

## 7. Ashlar extensions on top of the paper

Deliberate, clearly-layered additions — none alters the paper algorithms' verdict logic:

- **Basic-helpmate-existence theorem shortcut** (`BasicHelpmateExistenceTheorem`, between the Figure 9 semi-static
  step and the search): decides elementary mating-material classes (KRvK, KQvK, KBBvK opposite, KBNvK, KNNvK, KRvKB,
  KRvKN, KRRvK, KQQvK) by ashlar's separately proven finite-state theorem instead of search. Verdicts are
  theorem-certified (`WinnableProof.THEOREM`) and carry no mate line.
- **Mate line** (`UnwinnabilityFullAnalysis.mateLine()`): the Figure 5 search records the exhibited helpmate line on
  the unwind — bookkeeping only.
- **Three-valued quick verdict**: the paper's Figure 10 returns Winnable / Unwinnable / PossiblyWinnable; ashlar's
  `UnwinnabilityQuickVerdict` exposes exactly that (the pre-22.0.0 cha-port quick was two-valued and never claimed
  winnability).

---

## 8. Provenance

The engine was first built as the standalone clean-room project `fun22-reference` (github.com/dlbbld/fun22-reference)
against ashlar's public API only, and validated there before vendoring: over the D3-Chess ground-truth corpus and a
3 415-position chasolver-labelled corpus, every semi-static/full/quick definite verdict was confirmed (0
contradictions) against D3-Chess labels, ashlar's former cha-port analyzer, and Ambrona's independent Rust
`chasolver`. The D3-Chess corpus ships in ashlar's test resources (`oracle/d3chess/test-vectors.txt`) with a
permanent soundness sweep (`TestUnwinnableSemiStatic`).
