package com.smlv05.expensetracker.ui;

import com.smlv05.expensetracker.model.Category;
import com.smlv05.expensetracker.model.Expense;
import com.smlv05.expensetracker.service.ExpenseService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final ExpenseService service;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(ExpenseService service) {
        this.service = service;
    }

    public void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addExpense();
                case "2" -> showAllExpenses();
                case "3" -> deleteExpense();
                case "4" -> editExpense();
                case "5" -> showTotalForMonth();
                case "0" -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Unknown option, try again");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Expense Tracker ===");
        System.out.println("1. Add expense");
        System.out.println("2. Show all expenses");
        System.out.println("3. Delete expense");
        System.out.println("4. Edit expense");
        System.out.println("5. Total for month");
        System.out.println("0. Exit");
        System.out.print("Choose option: ");
    }

    private void addExpense() {
        try {
            System.out.print("Amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            service.validateAmount(amount);

            System.out.print("Category " + Arrays.toString(Category.values()) + ": ");
            Category category = parseCategory(scanner.nextLine());

            System.out.print("Date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine().trim());
            service.validateDate(date);

            System.out.print("Description (optional): ");
            String description = scanner.nextLine().trim();

            Expense expense = service.addExpense(amount, category, date, description);
            System.out.println("Expense added with id " + expense.getId());
        } catch (NumberFormatException e) {
            System.out.println("Amount must be a number, for example 12.50");
        } catch (DateTimeParseException e) {
            System.out.println("Date must be in format YYYY-MM-DD, for example 2026-10-20");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private Category parseCategory(String text) {
        try {
            return Category.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown category. Choose one of: "
                    + Arrays.toString(Category.values()));
        }
    }

    private void showAllExpenses() {
        List<Expense> expenses = service.getAllExpenses();
        if (expenses.isEmpty()) {
            System.out.println("No expenses yet");
            return;
        }

        System.out.printf("%-4s %-12s %-15s %12s  %s%n", "ID", "Date", "Category", "Amount", "Description");
        for (Expense expense : expenses) {
            System.out.printf("%-4d %-12s %-15s %12.2f  %s%n",
                    expense.getId(), expense.getDate(), expense.getCategory(),
                    expense.getAmount(), expense.getDescription());
        }
    }

    private void deleteExpense() {
        try {
            System.out.print("Expense ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            service.deleteExpense(id);
            System.out.println("Expense deleted");
        } catch (NumberFormatException e) {
            System.out.println("ID must be a whole number");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void editExpense() {
        try {
            System.out.print("Expense ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            Expense expense = service.findById(id);
            System.out.println("Current: " + expense.getDate() + " | " + expense.getCategory()
                    + " | " + expense.getAmount() + " | " + expense.getDescription());

            System.out.print("New amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            service.validateAmount(amount);

            System.out.print("New category " + Arrays.toString(Category.values()) + ": ");
            Category category = parseCategory(scanner.nextLine());

            System.out.print("New date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine().trim());
            service.validateDate(date);

            System.out.print("New description (optional): ");
            String description = scanner.nextLine().trim();

            service.updateExpense(id, amount, category, date, description);
            System.out.println("Expense updated");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        } catch (DateTimeParseException e) {
            System.out.println("Date must be in format YYYY-MM-DD, for example 2026-10-20");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showTotalForMonth() {
        try {
            System.out.print("Year: ");
            int year = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Month (1-12): ");
            int month = Integer.parseInt(scanner.nextLine().trim());

            BigDecimal total = service.getTotalForMonth(year, month);
            System.out.printf("Total for %d-%02d: %.2f%n", year, month, total);
        } catch (NumberFormatException e) {
            System.out.println("Year and month must be whole numbers");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}