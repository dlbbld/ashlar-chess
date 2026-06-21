# Agents

## Coding conventions

See [coding-conventions.md](coding-conventions.md).

## Commit messages

Commit messages should be concise. Use a short imperative subject by default.
Add a body only for non-obvious design decisions, migrations, or rule semantics.
Do not include routine test results or PR-style summaries in commit bodies.

## Comments

- **Keep:** decisions, trade-offs, spec references (e.g. `PGN spec §8.2.5`, `FIDE 9.6.2`), subtle invariants, counter-intuitive behaviour.
- **Drop:** restating the code, narration of implementation steps, double-bookkeeping of test intent, and especially **filesystem paths or other physically-mirrored facts in prose** — those duplicate information the code already carries and silently rot when the code is reorganised.

No fixed line-count rule; a longer comment is fine when the content is genuinely irreplaceable. Rule of thumb: if an AI could regenerate the comment from the code, the comment is a maintenance liability.

## Testing

Verify changes with the **default** Maven profile: `mvn -o -q test` (and `mvn -o -q test-compile` for a quick compile check). It is fast because `RestrictTestConstants` shrinks the heavy exhaustive suites.

Do **not** run `mvn -Pfull test` for routine work — reserve it for **release sign-offs**. The full profile un-restricts three deliberately-exhaustive tests (`TestSanValidateFormatFailureOracleComplement` ~75s, `TestFenRoundtripPgn` ~36s, `TestLegalMovesAgainstCreatedUsingValidation` ~33s) and runs ~3 min sequentially, which is impractical for iteration. The default profile already covers the fast path.
