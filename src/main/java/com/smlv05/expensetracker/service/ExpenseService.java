package com.smlv05.expensetracker.service;

import com.smlv05.expensetracker.model.Category;
import com.smlv05.expensetracker.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExpenseService {
    private final List<Expense> expenses = new ArrayList<>();
    private int nextId = 1;

    public void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Amount can have at most 2 decimal places");
        }
    }

    public void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date is required");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
    }

    public Expense addExpense(BigDecimal amount, Category category, LocalDate date, String description) {
        validateAmount(amount);
        validateDate(date);
        if (category == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (description == null) {
            description = "";
        }

        Expense expense = new Expense(nextId, amount, category, date, description);
        nextId++;
        expenses.add(expense);
        return expense;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> sorted = new ArrayList<>(expenses);
        sorted.sort(Comparator.comparing(Expense::getDate));
        return sorted;
    }
}