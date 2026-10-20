package com.smlv05.expensetracker.storage;

import com.smlv05.expensetracker.model.Category;
import com.smlv05.expensetracker.model.Expense;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CsvStorage {
    private static final String HEADER = "id,amount,category,date,description";

    public void save(List<Expense> expenses, Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Expense expense : expenses) {
            lines.add(expense.getId() + "," + expense.getAmount().toPlainString() + ","
                    + expense.getCategory() + "," + expense.getDate() + "," + expense.getDescription());
        }
        Files.write(file, lines);
    }

    public List<Expense> load(Path file) throws IOException {
        List<Expense> expenses = new ArrayList<>();
        if (!Files.exists(file)) {
            return expenses;
        }

        List<String> lines = Files.readAllLines(file);
        // first line is the header
        for (int i = 1; i < lines.size(); i++) {
            Expense expense = parseLine(lines.get(i));
            if (expense != null) {
                expenses.add(expense);
            }
        }
        return expenses;
    }

    private Expense parseLine(String line) {
        String[] parts = line.split(",", 5);
        if (parts.length != 5) {
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            BigDecimal amount = new BigDecimal(parts[1].trim());
            Category category = Category.valueOf(parts[2].trim());
            LocalDate date = LocalDate.parse(parts[3].trim());
            return new Expense(id, amount, category, date, parts[4]);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return null;
        }
    }
}