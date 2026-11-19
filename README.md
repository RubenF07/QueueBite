# QueueBite

A command-line restaurant order queue built with MVC. Orders are placed at the
back of a custom linked queue and completed from the front (FIFO). **Rush**
orders go in their own lane and are always served before regular orders. The
model pushes a snapshot of the queue to the view through an observer
(`OrderQueueListener`) every time the queue changes.

## How to use it

**Run the app:** run `controller.QueueBiteApp` (in Eclipse, right-click it and
choose *Run As > Java Application*).

| Menu option | What it does |
|-------------|--------------|
| `1` View queue | Shows every pending order and its position in line. Rush orders are listed first and tagged `[RUSH]`. |
| `2` Add order | Enter an item name, then a quantity. Repeat for more items, then type `done`. Answer `y` or `n` to "Rush order?". |
| `3` Complete next order | Removes the next order: the oldest rush order if there is one, otherwise the oldest regular order. |
| `0` Exit | Quits the program. |

Bad input never crashes the app:
- Non-numbers at the menu or the quantity prompt ask again.
- A blank or digit-only item name, or a quantity of 0 or less, shows an error
  and asks for that item again. Items already entered are kept.
- An empty order is rejected.
- Anything other than y/n at the rush prompt asks again.
- Completing when nothing is waiting shows "No orders waiting."

**Run the tests:** in Eclipse, right-click the `test` folder and choose *Run As >
JUnit Test*. All 53 tests run against the `model` package only. No view,
controller, or `Scanner` is created.

## Project layout

| Package      | Classes |
|--------------|---------|
| `model`      | `QueueInterface<T>`, `LinkedQueue<T>`, `EmptyQueueException`, `OrderItem`, `Order`, `OrderQueueListener`, `OrderManager` |
| `view`       | `QueueBiteView` |
| `controller` | `QueueBiteController`, `QueueBiteApp` |
| `test/model` | `LinkedQueueTest`, `OrderItemTest`, `OrderTest`, `OrderManagerTest`, `RecordingListener` |

## What changed since Deliverable 3

- **New feature: rush lane.** Orders can be marked rush. Rush orders are served
  first, each lane is FIFO, and IDs are shared across lanes.
- **Exit option.** `0` now quits. Before, you had to end input.
- **Queue tests.** `LinkedQueueTest` covers every queue method, including the
  tail-pointer edge case.
- **More tests.** There are 53 now (up from 26), including the rush-lane cases.
- **`QueueInterface<T>` is now a real interface.** `LinkedQueue<T>` implements
  it, and `OrderManager` stores both lanes as `QueueInterface<Order>`. To swap
  in a different queue, you'd only change the two lines in `OrderManager`'s
  constructor.
- **Cleaned-up `LinkedQueue`.** It's now singly linked, as the spec says (the
  unused `prev` pointers are gone), and `enqueue(null)` throws
  `IllegalArgumentException`.

---

## Reflection

### How did our design absorb the change?

The rush lane touched exactly the four classes we predicted in Deliverable 3:

| Class | Predicted | Actual change |
|-------|-----------|---------------|
| `Order` (model) | Change | Added a `rush` field, `isRush()`, and a 3-argument constructor. The 2-argument constructor still makes a regular order. |
| `OrderManager` (model) | Change | Added a second queue for rush orders and `placeOrder(items, rush)`. `completeNextOrder()` serves rush first, and the snapshot lists rush orders, then regular ones. |
| `QueueBiteController` (controller) | Change | Asks "Rush order?" after the items and passes the answer to `placeOrder`. |
| `QueueBiteView` (view) | Change | Added `readYesNo()` and the `[RUSH]` tag. |
| `LinkedQueue`, `QueueInterface`, `OrderItem`, `OrderQueueListener`, `EmptyQueueException`, `QueueBiteApp` | No change | No change for the feature. `LinkedQueue` and `QueueInterface` were changed separately, as MVP cleanup (see below). |

**What matched:** everything on the prediction list. A second lane really was
just a second instance of the same `LinkedQueue`. `EmptyQueueException` still
works without changes: when both lanes are empty, the regular lane's
`dequeue()` throws it.

**What surprised us:**
- **Queue positions needed no change at all.** The model builds the snapshot in
  serving order (rush first), so the view's existing "position in line" numbers
  are automatically correct.
- **`QueueInterface` and `LinkedQueue` did change, but not because of the
  feature.** We turned `QueueInterface` into a real interface and cleaned up
  `LinkedQueue` as part of the MVP cleanup from our Deliverable 3 to-do list.
  After that, `OrderManager` could hold both lanes as `QueueInterface<Order>`
  by changing only the two field types.
- **The exit option also changed the controller and view.** That was Deliverable
  3 cleanup, not the rush feature.

### Did the change cross the model/view line?

Only in the expected direction. The view learned one new thing about the model:
the read-only getter `Order.isRush()`, which it uses to print the tag. It still
has no reference to `OrderManager` or `LinkedQueue`, and the observer interface
(`ordersChanged(Order[])`) didn't change. The serving rule ("rush first") lives
only in `OrderManager`, so neither the view nor the controller knows about it.
The controller learned one new method signature, `placeOrder(items, rush)`. The
model still imports nothing from `view` or `controller`, and all its tests still
run with only a `RecordingListener`.

### What changed from our Deliverable 3 plan?

- **`QueueInterface<T>` uses `toList()`, not `toArray()`.** We planned a
  `toArray()` snapshot, but a generic method can't easily create a `T[]` in
  Java. `toList()` returns a new list, so changing it still doesn't change the
  queue.
- **Ruben's null test was flipped.** `LinkedQueueTest` first recorded the old
  behavior (null accepted). Once `enqueue(null)` started throwing, as the spec
  says, the test was changed to expect `IllegalArgumentException`. We also
  added a test that uses the queue only through `QueueInterface`.
- **We kept the old `placeOrder(items)`** as an overload that makes a regular
  order. That way none of the Deliverable 3 tests had to change.
- **The rush question is only asked when the order has items.** An empty order
  goes straight to the model, which rejects it, so the user isn't asked about
  rush for nothing.
- **Tests now mix JUnit 4 and JUnit 5.** `LinkedQueueTest` uses JUnit 5, so we
  switched the project to Eclipse's JUnit 5 library, which still runs the JUnit
  4 tests. We also gave `LinkedQueueTest` its missing `@Test` import and
  `@BeforeEach` annotation, and replaced `List.of` with `Arrays.asList` because
  the project targets Java 8.

### What doesn't work yet?

- Order IDs past `Integer.MAX_VALUE` aren't handled. We can't reach this in
  practice.
- "done" can't be ordered as an item name, because it's the word that ends an
  order.
- An order can't be switched between rush and regular after it's placed.
- All 53 tests pass. There are no view or controller tests, because the rubric
  says tests target the model.

---

## Next Steps

### What is left for the final build (in order)

1. Review the new `QueueInterface` / `LinkedQueue` code, and add queue tests
   for longer sequences (for example, enqueue and dequeue 100 entries, and
   check that `toList()` is still independent after a dequeue).
2. Throw `IllegalStateException` in `placeOrder` when IDs run out, as in the
   spec, with a test.
3. Add more `OrderItem` edge-case tests, such as `"2 burgers"` and `"7Up"` with
   surrounding spaces.
4. Final README pass and a demo rehearsal.

### Stretch goals

- **Committing to:** the rush lane and making the queue swappable behind
  `QueueInterface<T>`. Both are done.
- **Dropping:** priority *scores* with mid-chain insertion, the "realtime
  stream" of orders, and completed-order history. Two FIFO lanes give us
  priority behavior without inserting into the middle of a linked list.

### Our 3-minute demo

1. Run the JUnit tests and show the green bar: 53 passing, no view needed.
2. Start the app and view the empty queue.
3. Add a regular order: "Burger" × 2, "Fries" × 1, `done`, `n`.
4. Add a rush order: "Shake" × 1, `done`, `y`. The queue shows the shake at
   position 1, tagged `[RUSH]`.
5. Complete next. The rush order is served first, even though it was placed
   second.
6. Bad input:
   - `abc` and `7` at the menu
   - a blank item name, `123` as a name, and quantity `0`
   - `maybe` at the rush prompt
   - completing an empty queue

   The app keeps running through all of them.
7. `0` to exit.

### Who does what

| Member    | Remaining work |
|-----------|----------------|
| Ruben     | Step 1: queue code review and extra `LinkedQueueTest` cases |
| Elsie     | Step 2: ID-exhaustion check and test in `OrderManager` |
| Adarsh    | Step 3: extra `OrderItem` / `Order` edge-case tests |
| Krishanth | Step 4: README, demo script and rehearsal, any view/controller fixes from the demo |

---

## Attribution

| Class / test file | Owner | How |
|-------------------|-------|-----|
| `QueueInterface<T>` | Ruben | Wrote the first version and made it generic. Converting it to a real interface was GenAI-assisted (Claude Code) |
| `LinkedQueue<T>` | Ruben | Wrote the first version. The empty-queue throws, the singly-linked rewrite, the null check, and implementing `QueueInterface` were GenAI-assisted (Claude Code) |
| `EmptyQueueException` | Ruben | GenAI-assisted (Claude Code) |
| `LinkedQueueTest` | Ruben | Wrote. Missing imports, `@BeforeEach`, the Java 8 fix (`Arrays.asList`), the flipped null test, and the interface test were GenAI-assisted (Claude Code) |
| `OrderItem` | Adarsh | GenAI-assisted (Claude Code) |
| `Order` | Adarsh | GenAI-assisted (Claude Code), including the rush flag |
| `OrderQueueListener` | Adarsh | GenAI-assisted (Claude Code) |
| `OrderItemTest` | Adarsh | GenAI-assisted (Claude Code) |
| `OrderTest` | Adarsh | GenAI-assisted (Claude Code), including the rush tests |
| `OrderManager` | Elsie | GenAI-assisted (Claude Code), including the rush lane |
| `QueueBiteApp` | Elsie | GenAI-assisted (Claude Code) |
| `RecordingListener` | Elsie | GenAI-assisted (Claude Code) |
| `OrderManagerTest` | Elsie | GenAI-assisted (Claude Code), including the rush-lane tests |
| `QueueBiteView` | Krishanth | GenAI-assisted (Claude Code), reviewed. Added the rush prompt, `[RUSH]` tag, and exit option |
| `QueueBiteController` | Krishanth | GenAI-assisted (Claude Code), reviewed. Added the rush flow and exit option |

### GenAI note

We used Claude Code (Anthropic) to draft the view, controller, model data
classes, `OrderManager`, the app wiring, and the model tests from our spec. In
Deliverable 4 we also used it to:
- implement the rush lane in `Order` and `OrderManager`
- add the rush prompt and exit option to the view and controller
- write the rush-lane tests
- fix `LinkedQueueTest` so it compiles and runs
- turn `QueueInterface` into a real interface and clean up `LinkedQueue`

The first versions of `QueueInterface` and `LinkedQueue`, and most of the
`LinkedQueueTest` cases, were written by Ruben. We checked the work by running all 53 JUnit tests
and walking through the demo path, including every bad-input case listed above.
