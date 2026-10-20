package com.smlv05.expensetracker.storage;

import com.smlv05.expensetracker.model.Category;
import com.smlv05.expensetracker.model.Expense;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvStorageTest {
    private static final String HEADER = "id,amount,category,date,description";

    @TempDir
    Path tempDir;

    private final CsvStorage storage = new CsvStorage();

    @Test
    void saveAndLoadReturnsSameExpenses() throws IOException {
        Path file = tempDir.resolve("expenses.csv");
        List<Expense> expenses = List.of(
                new Expense(1, new BigDecimal("12.50"), Category.FOOD, LocalDate.of(2026, 10, 15), "lunch"),
                new Expense(2, new BigDecimal("1500"), Category.RENT, LocalDate.of(2026, 10, 1), ""));

        storage.save(expenses, file);
        List<Expense> loaded = storage.load(file);

        assertEquals(2, loaded.size());
        Expense first = loaded.get(0);
        assertEquals(1, first.getId());
        assertEquals(new BigDecimal("12.50"), first.getAmount());
        assertEquals(Category.FOOD, first.getCategory());
        assertEquals(LocalDate.of(2026, 10, 15), first.getDate());
        assertEquals("lunch", first.getDescription());
        assertEquals("", loaded.get(1).getDescription());
    }

    @Test
    void descriptionWithCommaIsKept() throws IOException {
        Path file = tempDir.resolve("expenses.csv");
        List<Expense> expenses = List.of(
                new Expense(1, new BigDecimal("8"), Category.FOOD, LocalDate.of(2026, 10, 15), "lunch, coffee"));

        storage.save(expenses, file);

        assertEquals("lunch, coffee", storage.load(file).get(0).getDescription());
    }

    @Test
    void loadMissingFileReturnsEmptyList() throws IOException {
        assertTrue(storage.load(tempDir.resolve("missing.csv")).isEmpty());
    }

    @Test
    void loadEmptyFileReturnsEmptyList() throws IOException {
        Path file = tempDir.resolve("expenses.csv");
        Files.writeString(file, "");

        assertTrue(storage.load(file).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1,abc,FOOD,2026-10-15,bad amount",
            "1,10.00,PIZZA,2026-10-15,bad category",
            "1,10.00,FOOD,15.10.2026,bad date",
            "x,10.00,FOOD,2026-10-15,bad id",
            "1,10.00,FOOD",
            "garbage",
            ""
    })
    void loadSkipsBrokenLine(String line) throws IOException {
        Path file = tempDir.resolve("expenses.csv");
        Files.write(file, List.of(HEADER, line));

        assertTrue(storage.load(file).isEmpty());
    }

    @Test
    void loadKeepsGoodLinesWhenSomeAreBroken() throws IOException {
        Path file = tempDir.resolve("expenses.csv");
        Files.write(file, List.of(
                HEADER,
                "1,10.00,FOOD,2026-10-15,ok",
                "2,abc,FOOD,2026-10-15,broken",
                "3,20.00,RENT,2026-10-01,ok"));

        List<Expense> loaded = storage.load(file);

        assertEquals(2, loaded.size());
        assertEquals(1, loaded.get(0).getId());
        assertEquals(3, loaded.get(1).getId());
    }
}