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