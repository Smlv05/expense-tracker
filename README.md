# Expense Tracker

Console app for tracking personal expenses, written in Java.
I made it as a learning project to practice Java, unit testing with JUnit 5 and working with Git.

## Features

- add, edit and delete expenses (amount, category, date, optional description)
- show all expenses sorted by date
- show the total for a chosen month
- show totals by category
- save expenses to a CSV file and load them back

Categories: FOOD, TRANSPORT, RENT, ENTERTAINMENT, OTHER.

Input is checked: the amount must be greater than 0 and have at most 2 decimal places,
the date must be in format YYYY-MM-DD and not in the future. If the input is wrong,
the app shows a message and keeps working.

## Requirements

- Java 17 or newer
- Maven

## How to run

```
mvn compile exec:java
```

Expenses are kept in memory while the app is running. To keep them after exit,
choose `7. Save to file` - they are saved to `expenses.csv` in the project folder.
`8. Load from file` loads them back.

## How to run tests

```
mvn test
```

Tests are in `src/test/java`. There are unit tests for `ExpenseService`
(adding, editing, deleting, totals, validation) and tests for saving and loading CSV,
which use a temporary file.

## Example

```
=== Expense Tracker ===
1. Add expense
2. Show all expenses
3. Delete expense
4. Edit expense
5. Total for month
6. Totals by category
7. Save to file
8. Load from file
0. Exit
Choose option: 1
Amount: 12.50
Category [FOOD, TRANSPORT, RENT, ENTERTAINMENT, OTHER]: food
Date (YYYY-MM-DD): 2026-10-15
Description (optional): lunch
Expense added with id 1

Choose option: 2
ID   Date         Category              Amount  Description
1    2026-10-15   FOOD                   12.50  lunch
```

## Project structure

```
src/main/java/com/smlv05/expensetracker/
  model/      Expense, Category
  service/    ExpenseService - all logic and validation
  storage/    CsvStorage - reading and writing the CSV file
  ui/         ConsoleMenu - console input and output
  Main.java
```

## Testing documents

- [docs/test-cases.md](docs/test-cases.md) - manual test cases
- [docs/bug-reports.md](docs/bug-reports.md) - bugs I found during development