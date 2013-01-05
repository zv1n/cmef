CMEF - Cognitive Memory Experimentation Framework
===

CME Framework is used to conduct simple Cognitive Memory experiments.

Version 2.0 - 2.1 Formatting Changes

# RANDOM_POOL now requires ',' be placed in between values after the first ':'.
# POOL can now also be SET_POOL, resulting in values being assigned per Item per Set in a State.
  * This means the Random Sequences generated are SCOPED to the SET not the STATE!
# STATE_POOL can be used to set 1 value per Set in a State.
  * This means the Random Sequences generated are SCOPED to the STATE not the SET!
# ContinuousTimer property was added.
  * When set to "yes" or "true", the Clock Timer will run from the start of the State/Set.
  * ResetOnNext, as usual, will allow this clock to be reset per Set.
# ContinuousAfterFirstSelection was added.
  * When set to "yes" or "true", the Clock Timer will start only after the first item is selected.
  * The Clock will thereafter continuously run until it reaches zero.
  * ResetOnNext, as usual, will allow this clock to be reset per Set.
