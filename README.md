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
JUnit Test*. All 52 tests run against the `model` package only. No view,
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
- **More tests.** There are 52 now (up from 26), including the rush-lane cases.
- **Generic `QueueInterface<T>`.** It used to be hard-coded to `Order`.

---

## Reflection

### How did our design absorb the change?

The rush lane touched exactly the four classes we predicted in Deliverable 3:

| Class | Predicted | Actual change |
|-------|-----------|---------------|
| `Order` (model) | Change | Added a `rush` field, `isRush()`, and a 3-argument constructor. The 2-argument constructor still makes a regular order. |
| `OrderManager` (model) | Change | Added a second `LinkedQueue<Order>` for rush orders and `placeOrder(items, rush)`. `completeNextOrder()` serves rush first, and the snapshot lists rush orders, then regular ones. |
| `QueueBiteController` (controller) | Change | Asks "Rush order?" after the items and passes the answer to `placeOrder`. |
| `QueueBiteView` (view) | Change | Added `readYesNo()` and the `[RUSH]` tag. |
| `LinkedQueue`, `OrderItem`, `OrderQueueListener`, `EmptyQueueException`, `QueueBiteApp` | No change | No change for the feature. |

**What matched:** everything on the prediction list. A second lane really was
just a second instance of the same `LinkedQueue`. `EmptyQueueException` still
works without changes: when both lanes are empty, the regular lane's
`dequeue()` throws it.

**What surprised us:**
- **Queue positions needed no change at all.** The model builds the snapshot in
  serving order (rush first), so the view's existing "position in line" numbers
  are automatically correct.
- **`QueueInterface` did change, but not because of the feature.** We predicted
  it wouldn't, but Ruben made it generic as part of the MVP cleanup from our
  Deliverable 3 to-do list.
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

- **`QueueInterface<T>` is generic but is still a class.** We planned a real
  interface that `LinkedQueue` implements. It still wraps a `LinkedQueue`
  internally. Because it isn't an interface yet, `OrderManager` holds both lanes
  as `LinkedQueue<Order>`, not `QueueInterface<Order>` as planned.
- **`LinkedQueue` wasn't cleaned up.** It's still doubly linked (the `prev`
  pointers aren't used), still uses `toList()` instead of `toArray()`, and still
  accepts `null`. `LinkedQueueTest` records the current behavior: its
  `enqueue_nullIsAcceptedAsAValue` test expects null to be stored.
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

- `LinkedQueue.enqueue(null)` is accepted. The spec says it should throw
  `IllegalArgumentException`. The app never enqueues null, so this can't happen
  through the menu.
- `QueueInterface` isn't a real interface, so the queue implementation can't be
  swapped.
- Order IDs past `Integer.MAX_VALUE` aren't handled. We can't reach this in
  practice.
- "done" can't be ordered as an item name, because it's the word that ends an
  order.
- An order can't be switched between rush and regular after it's placed.
- All 52 tests pass. There are no view or controller tests, because the rubric
  says tests target the model.

---

## Next Steps

### What is left for the final build (in order)

1. Make `QueueInterface<T>` a real interface (enqueue, dequeue, peek, isEmpty,
   size, toArray) and have `LinkedQueue<T>` implement it. Drop the unused `prev`
   pointer, and make `enqueue(null)` throw `IllegalArgumentException`. Update
   `LinkedQueueTest` to match.
2. Have `OrderManager` hold both lanes as `QueueInterface<Order>`.
3. Throw `IllegalStateException` in `placeOrder` when IDs run out, as in the
   spec, with a test.
4. Add more `OrderItem` edge-case tests, such as `"2 burgers"` and `"7Up"` with
   surrounding spaces.
5. Final README pass and a demo rehearsal.

### Stretch goals

- **Committing to:** the rush lane (done) and making the queue swappable behind
  `QueueInterface<T>` (step 1 above).
- **Dropping:** priority *scores* with mid-chain insertion, the "realtime
  stream" of orders, and completed-order history. Two FIFO lanes give us
  priority behavior without inserting into the middle of a linked list.

### Our 3-minute demo

1. Run the JUnit tests and show the green bar: 52 passing, no view needed.
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
| Ruben     | Step 1: real `QueueInterface<T>`, `LinkedQueue` cleanup, `LinkedQueueTest` updates |
| Elsie     | Steps 2–3: `OrderManager` on `QueueInterface<Order>`, ID-exhaustion check and test |
| Adarsh    | Step 4: extra `OrderItem` / `Order` edge-case tests |
| Krishanth | Step 5: README, demo script and rehearsal, any view/controller fixes from the demo |

---

## Attribution

| Class / test file | Owner | How |
|-------------------|-------|-----|
| `QueueInterface<T>` | Ruben | Wrote. Made generic for Deliverable 4 |
| `LinkedQueue<T>` | Ruben | Wrote. The empty-queue `EmptyQueueException` throws were GenAI-assisted (Claude Code) |
| `EmptyQueueException` | Ruben | GenAI-assisted (Claude Code) |
| `LinkedQueueTest` | Ruben | Wrote. Missing imports, `@BeforeEach`, and the Java 8 fix (`Arrays.asList`) were GenAI-assisted (Claude Code) |
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

`QueueInterface`, the core of `LinkedQueue`, and the `LinkedQueueTest` cases
were written by Ruben. We checked the work by running all 52 JUnit tests
and walking through the demo path, including every bad-input case listed above.
