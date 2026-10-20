# Bug reports

Bugs I found while I was testing my app.

--- 

## BUG-01: App crashes when input is closed (Ctrl+Z) in main menu

**Status:** Open
**Severity:** Major
**Priority:** Low

**Environment:** Windows 11, Java 21.0.5, Maven 3.10.0, branch main (commit 5e8ddc3)

**Steps to reproduce:**
1. Start the app with `mvn compile exec:java`.
2. When "Choose option:" is shown, press `Ctrl+Z` and then `Enter`.

**Expected result:** the app does not crash. It shows a message and exits normally.

**Actual result:** the app crashes with an exception and Maven shows BUILD FAILURE.
All expenses that were not saved to the file are lost.

**Notes:** `scanner.nextLine()` in `ConsoleMenu.run` is called without checking if there is
more input. `NoSuchElementException` is not caught anywhere.

---

## BUG-02: Amount is checked only after all fields are entered

**Status:** Fixed in commit 46f8eb0
**Severity:** Minor
**Priority:** Medium

**Environment:** Windows 11, Java 21.0.5, Maven 3.10.0.
Found during development, in a local version before the console menu was committed.

**Steps to reproduce:**
1. Start the app with `mvn compile exec:java`.
2. Enter `1` (Add expense).
3. Amount: `-5`
4. Category: `food`
5. Date: `2026-05-05`
6. Description: leave empty, press Enter.

**Expected result:** message "Amount must be greater than 0" is shown right after step 3.
The app does not ask for category, date and description.

**Actual result:** the app asks for all fields first and shows
"Amount must be greater than 0" only at the end. The expense is not added,
but the user has to type everything before seeing the error.

**Fix:** amount and date are now validated right after input in `ConsoleMenu`,
using `validateAmount` and `validateDate` from `ExpenseService`.