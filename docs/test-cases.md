# Manual test cases

Manual test cases for the Expense Tracker console app.
Start the app with `mvn compile exec:java` before each test case.
Unless written otherwise, the precondition is: app is started, there are no expenses, main menu is shown.

---

### TC-01: Add expense with valid data

**Steps:**
1. Enter `1` (Add expense).
2. Amount: `12.50`
3. Category: `FOOD`
4. Date: `2026-10-15`
5. Description: `lunch`
6. Enter `2` (Show all expenses).

**Expected:** message "Expense added with id 1". The list shows one expense: id 1, 2026-10-15, FOOD, 12.50, lunch.

**Result:** Pass

---

### TC-02: Add expense with negative amount

**Steps:**
1. Enter `1`.
2. Amount: `-5`

**Expected:** message "Amount must be greater than 0" right after the amount. The app does not ask for category and goes back to the main menu. No expense is added.

**Result:** Pass

---

### TC-03: Add expense with letters instead of amount

**Steps:**
1. Enter `1`.
2. Amount: `abc`

**Expected:** message "Amount must be a number, for example 12.50". The app does not crash and goes back to the main menu.

**Result:** Pass

---

### TC-04: Add expense with 3 decimal places

**Steps:**
1. Enter `1`.
2. Amount: `10.123`

**Expected:** message "Amount can have at most 2 decimal places". No expense is added.

**Result:** Pass

---

### TC-05: Category in lowercase is accepted

**Steps:**
1. Enter `1`.
2. Amount: `20`
3. Category: `transport`
4. Date: `2026-10-10`
5. Description: leave empty, press Enter.
6. Enter `2`.

**Expected:** expense is added. The list shows category TRANSPORT and empty description.

**Result:** Pass

---

### TC-06: Unknown category

**Steps:**
1. Enter `1`.
2. Amount: `20`
3. Category: `pizza`

**Expected:** message "Unknown category. Choose one of: [FOOD, TRANSPORT, RENT, ENTERTAINMENT, OTHER]". The app goes back to the main menu, no expense is added.

**Result:** Pass

---

### TC-07: Date in the future

**Steps:**
1. Enter `1`.
2. Amount: `20`
3. Category: `FOOD`
4. Date: tomorrow's date (for example if today is 2026-10-20, enter `2026-10-21`)

**Expected:** message "Date cannot be in the future". No expense is added.

**Result:** Pass

---

### TC-08: Date in wrong format

**Steps:**
1. Enter `1`.
2. Amount: `20`
3. Category: `FOOD`
4. Date: `20.10.2026`

**Expected:** message "Date must be in format YYYY-MM-DD, for example 2026-10-20". The app does not crash.

**Result:** Pass
 
---

### TC-09: Expenses are sorted by date

**Steps:**
1. Add expense: `10`, `FOOD`, `2026-10-15`
2. Add expense: `20`, `RENT`, `2026-10-01`
3. Add expense: `30`, `OTHER`, `2026-10-10`
4. Enter `2`.

**Expected:** the list is sorted by date: 2026-10-01, 2026-10-10, 2026-10-15 (not by id).

**Result:** Pass

---

### TC-10: Delete expense with id that does not exist

**Precondition:** one expense is added (id 1).

**Steps:**
1. Enter `3` (Delete expense).
2. Expense ID: `99`
3. Enter `2`.

**Expected:** message "Expense with id 99 not found". The expense with id 1 is still in the list.

**Result:** Pass