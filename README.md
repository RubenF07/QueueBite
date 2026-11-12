# QueueBite

A command-line restaurant order queue built with MVC. Orders are placed at the
back of a custom linked queue and completed from the front (FIFO). The model
pushes a snapshot of the queue to the view through an observer
(`OrderQueueListener`) every time the queue changes.

## Running

- **App:** run `controller.QueueBiteApp`. Menu: `1` view queue, `2` add order,
  `3` complete next order. When adding an order, type item names and
  quantities, then `done` to submit. End input (Ctrl+Z then Enter on Windows,
  Ctrl+D on Mac/Linux) to quit.
- **Tests:** JUnit 4 tests are in `test/model/` (`OrderItemTest`, `OrderTest`,
  `OrderManagerTest`). In Eclipse, right-click the `test` folder and choose
  *Run As > JUnit Test*. They only use classes from the `model` package. No view,
  controller, or `Scanner` is created.

## Project layout

| Package      | Classes |
|--------------|---------|
| `model`      | `QueueInterface`, `LinkedQueue<T>`, `EmptyQueueException`, `OrderItem`, `Order`, `OrderQueueListener`, `OrderManager` |
| `view`       | `QueueBiteView` |
| `controller` | `QueueBiteController`, `QueueBiteApp` |
| `test/model` | `OrderItemTest`, `OrderTest`, `OrderManagerTest`, `RecordingListener` |

---

## Reflection

### What changed from the specification?

Our spec had two halves that disagreed with each other. The **Class Design**
section describes `OrderManager`, `QueueBiteView`, `EmptyQueueException`, and an
observer that pushes `Order[]` snapshots. The **Data & State** and **Method
Signatures** sections describe an older design with `OrderModel`, `ConsoleView`,
`NoSuchElementException`, `List<String>` ticket items, and a controller that
hands queue data to the view directly. We built the **Class Design** version,
because that is what the system diagram, validation table, and test plan use.
Specific differences:

- **Packages:** the spec says `queuebite.model` / `queuebite.view` /
  `queuebite.controller`. We used `model`, `view`, `controller` because that is
  how the project was set up.
- **`QueueInterface`:** the spec calls for a generic `QueueInterface<T>`
  (enqueue, dequeue, peek, isEmpty, size) that `LinkedQueue<T>` implements.
  What exists is a concrete class wrapping a `LinkedQueue<Order>` with
  order-specific method names, and nothing uses it yet. `LinkedQueue` does not
  implement an interface, so the queue isn't swappable yet.
- **`LinkedQueue<T>`:**
  - Its nodes have a `prev` pointer, which makes the list doubly linked even
    though the spec says singly linked.
  - It exposes `toList()` (a new `ArrayList`) instead of `toArray()`.
  - `enqueue(null)` is not rejected yet.
  - `head` and `tail` are package-private instead of private.
  - `dequeue()` and `peek()` were `// TODO` on an empty queue, which caused a
    `NullPointerException`. They now throw `EmptyQueueException` as the spec
    says.
- **`Order`:** holds a `List<OrderItem>` (name + quantity), as in Class Design,
  instead of `List<String> ticketItems`. The getter is `getItems()`, not
  `getTicketItems()`. It also rejects a `null` entry inside the list.
- **`OrderManager`:** `getPendingOrders()` returns an `Order[]` copy, because
  the listener signature takes `Order[]`. We did not build
  `getPendingOrderCount()`, `hasPendingOrders()`, `peekNextOrder()`, or the
  `IllegalStateException` for exhausted IDs, because nothing in the MVP calls
  them.
- **`QueueBiteView`:** the validation table mentions a `readItem()` method.
  One method can't hand back two raw values (name and quantity), so the view
  has `readLine(prompt)` and `readInt(prompt)`, and the controller calls each
  one.
- **`QueueBiteController`:**
  - **No exit option.** Class Design and the validation table say there are
    three options and that `0` is not one, while Method Signatures says `0`
    exits. We followed the validation table, so the loop only ends when input
    ends.
  - **Ending an order.** The spec never says how a customer finishes adding
    items. We used the word `done`.
  - **Error messages.** These come from the model's exception messages (for
    example "Quantity has to be at least 1"), so the wording matches the
    validation table.
  - **Fewer public methods.** `placeOrder`, `showQueue`, and
    `completeNextOrder` are private helpers, not the public methods listed in
    Method Signatures. "View queue" asks the view to reprint the snapshot it
    already has.
- **Where validation lives:** matches the spec's three layers. The view checks
  that input is an integer, the controller checks the menu range, and
  `OrderItem` / `Order` check the data rules.
- **Tests:** match the plan for `OrderItem`, `Order`, and `OrderManager`
  (including `RecordingListener`). We used JUnit 4 in a separate `test/`
  source folder. The `LinkedQueue` tests from the test plan are not written
  yet (see below).

### Did the model really stand alone?

Yes. Nothing in `model` imports anything from `view` or `controller`. The model
only knows about `OrderQueueListener`, which lives in the model package. All
`OrderManager` tests run against a `RecordingListener` stand-in, with no view
or controller. We did not have to move anything across the model/view line to
make the app run. The one thing that comes close is that the controller shows
the model's exception messages to the user as they are, so the model decides
some of the user-facing wording. If we swapped to a GUI, those messages would
still make sense, but a different interface might want its own wording.

### What happened to our unknowns?

- **Free-text items or a fixed menu:** resolved. Items are free text with a
  quantity, and `OrderItem` validates them.
- **How the view stays up to date without depending on the model:** resolved
  with the observer. `OrderManager.addListener` sends the current snapshot
  right away, and every successful place or complete sends a new one.
- **Priority scoring / inserting into the middle of the chain:** still open.
  We dropped it from the MVP. It comes back as our change feature (below).
- **"Realtime stream" of orders:** dropped. The app handles one console action
  at a time.

### What doesn't work yet?

- **No exit command.** The user has to end input to quit.
- **`LinkedQueue` has no tests yet.** The test plan's queue tests are missing:
  enqueue, dequeue, peek, isEmpty, size, the tail-pointer edge case, and the
  snapshot.
- **`enqueue(null)` is accepted** instead of throwing
  `IllegalArgumentException`. The app never enqueues `null`, but the spec says
  it should be rejected.
- **`QueueInterface` is not a generic interface yet**, so the queue can't be
  swapped.
- **Order IDs past `Integer.MAX_VALUE` are not handled.** We can't reach this
  in practice, but the spec mentions it.
- **"done" can't be ordered as an item name**, because it's the word that ends
  an order.

---

## Next Steps

### What is left to finish the MVP (in order)

1. Make `QueueInterface<T>` a real generic interface. Have `LinkedQueue<T>`
   implement it, make it singly linked, reject `null` in `enqueue`, and add
   `toArray()`. Then delete the old order-specific wrapper.
2. Write `LinkedQueueTest` from the test plan, including the "dequeue the only
   item, then enqueue again" tail-pointer test.
3. Add an exit option to the menu and update the "That isn't an option"
   message to match.
4. Have `OrderManager` hold its queue as a `QueueInterface<Order>` so it can be
   swapped.

### Change feature: rush (priority) lane

Customers can mark an order as **rush**. `completeNextOrder()` always serves
the oldest rush order first, and only then the oldest regular order. Orders
are FIFO within each lane, and IDs stay unique across both lanes. This adds
model state (a second queue and a rush flag on each order) and a routing rule,
and the user reaches it from the "add order" flow.

**Predicted to change:**
- `Order` (model): add a `boolean rush` field and getter. It stays immutable.
- `OrderManager` (model): own a second `LinkedQueue<Order>` for rush orders,
  route on `placeOrder`, serve rush first in `completeNextOrder`, and build a
  snapshot of rush orders followed by regular orders.
- `QueueBiteController` (controller): ask whether the order is rush and pass it
  to `placeOrder`.
- `QueueBiteView` (view): a yes/no prompt, and a `[RUSH]` tag in the queue
  display.

**Predicted *not* to change:** `LinkedQueue`, `QueueInterface`,
`EmptyQueueException`, `OrderItem`, `OrderQueueListener`, and `QueueBiteApp`.
If the design holds, a second lane is just a second instance of the same queue,
and the observer still sends the same `Order[]` snapshot type.

### New tests needed

- `LinkedQueueTest`: every case in the test plan, `enqueue(null)`, and
  `toArray()` on an empty queue returning length 0.
- `OrderTest`: a rush order reports `isRush() == true`, and regular orders
  default to false.
- `OrderManagerTest`:
  - Rush orders are completed before older regular orders.
  - Orders are FIFO within each lane.
  - IDs are shared across lanes and never reused.
  - The snapshot lists rush orders first.
  - Completing with both lanes empty throws `EmptyQueueException` and does not
    notify listeners.
  - An invalid rush order uses up no ID.

### Who does what

| Member    | Remaining work |
|-----------|----------------|
| Ruben     | Generic `QueueInterface<T>`, `LinkedQueue<T>` fixes (singly linked, null check, `toArray`), `LinkedQueueTest` |
| Adarsh    | Rush flag on `Order`, updated `OrderTest` |
| Elsie     | Rush-lane routing and snapshot in `OrderManager`, switch to `QueueInterface<Order>`, updated `OrderManagerTest` and `RecordingListener` if needed |
| Krishanth | Exit option, rush prompt and `[RUSH]` display in `QueueBiteView` / `QueueBiteController` |

---

## Attribution

| Class / test file | Owner | How |
|-------------------|-------|-----|
| `QueueInterface` | Ruben | Wrote |
| `LinkedQueue<T>` | Ruben | Wrote. The empty-queue `EmptyQueueException` throws (replacing two TODOs) were GenAI-assisted (Claude Code) to unblock the prototype |
| `EmptyQueueException` | Ruben | GenAI-assisted (Claude Code) |
| `OrderItem` | Adarsh | GenAI-assisted (Claude Code) |
| `Order` | Adarsh | GenAI-assisted (Claude Code) |
| `OrderQueueListener` | Adarsh | GenAI-assisted (Claude Code) |
| `OrderItemTest` | Adarsh | GenAI-assisted (Claude Code) |
| `OrderTest` | Adarsh | GenAI-assisted (Claude Code) |
| `OrderManager` | Elsie | GenAI-assisted (Claude Code) |
| `QueueBiteApp` | Elsie | GenAI-assisted (Claude Code) |
| `RecordingListener` | Elsie | GenAI-assisted (Claude Code) |
| `OrderManagerTest` | Elsie | GenAI-assisted (Claude Code) |
| `QueueBiteView` | Krishanth | GenAI-assisted (Claude Code), reviewed |
| `QueueBiteController` | Krishanth | GenAI-assisted (Claude Code), reviewed |

### GenAI note

We used Claude Code (Anthropic) to draft the view, controller, model data
classes, `OrderManager`, the app wiring, and the model tests from our spec. We
also used it to fill in the empty-queue handling in `LinkedQueue`. It followed
the Class Design section where the spec contradicted itself, and it listed
those choices so we could check them. We ran the 26 JUnit tests and walked
through the MVP path (including each bad-input case in the validation table)
to confirm the behavior matched the spec. `QueueInterface` and the core of
`LinkedQueue` were written without GenAI.
