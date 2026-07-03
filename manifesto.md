# Manifesto

ashlar-chess exists because I wanted a Java chess library that treats the rules of chess as the product, not as
plumbing around an engine.

The center of the project is orthodox-chess correctness: legal moves, notation, game state, draw claims, automatic
terminations, and timeout adjudication — tracked across the whole game, not just the current position. The fifty-move
and repetition rules in particular are followed over the full game history, which I have not found done this way
elsewhere. These are small details until they are wrong; then they become the whole problem.

Most chess libraries serve engines, GUIs, or casual validation. ashlar-chess is deliberately narrower: a stable,
well-tested rules library with a fixed scope and a bias toward explicit, reproducible answers.

The unwinnability work is the essential hard case. Miguel Ambrona's FUN 2022 algorithm makes dead-position and
mating-material adjudication possible beyond simple insufficient-material heuristics — and it is rare, in practical
software, to implement an algorithm that comes with an actual mathematical proof. ashlar-chess implements that paper
independently and validates it against Ambrona's own tools, so this rule question can be answered in Java with the
same seriousness as ordinary move legality.

The project should not grow forever. It should become finished — boring in the best way — and trustworthy.
