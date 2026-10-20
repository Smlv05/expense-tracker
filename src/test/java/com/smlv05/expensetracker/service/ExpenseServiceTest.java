package com.smlv05.expensetracker.service;

import com.smlv05.expensetracker.model.Category;
import com.smlv05.expensetracker.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpenseServiceTest {
    private static final LocalDate DATE = LocalDate.of(2026, 10, 15);

    private ExpenseService service;

    @BeforeEach
    void setUp() {
        service = new ExpenseService();
    }

    @Test
    void addExpenseWithValidData() {
        Expense expense = service.addExpense(new BigDecimal("12.50"), Category.FOOD, DATE, "lunch");

        assertEquals(1, expense.getId());
        assertEquals(new BigDecimal("12.50"), expense.getAmount());
        assertEquals(Category.FOOD, expense.getCategory());
        assertEquals(DATE, expense.getDate());
        assertEquals("lunch", expense.getDescription());
        assertEquals(1, service.getAllExpenses().size());
    }

    @Test
    void newExpensesGetIncreasingIds() {
        Expense first = service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "");
        Expense second = service.addExpense(new BigDecimal("20"), Category.RENT, DATE, "");

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    void nullDescriptionBecomesEmpty() {
        Expense expense = service.addExpense(new BigDecimal("10"), Category.OTHER, DATE, null);

        assertEquals("", expense.getDescription());
    }

    @Test
    void getAllExpensesReturnsEmptyListWhenNothingAdded() {
        assertTrue(service.getAllExpenses().isEmpty());
    }

    @Test
    void getAllExpensesReturnsExpensesSortedByDate() {
        service.addExpense(new BigDecimal("10"), Category.FOOD, LocalDate.of(2026, 10, 15), "");
        service.addExpense(new BigDecimal("20"), Category.TRANSPORT, LocalDate.of(2026, 10, 1), "");
        service.addExpense(new BigDecimal("30"), Category.RENT, LocalDate.of(2026, 10, 10), "");

        List<Expense> expenses = service.getAllExpenses();

        assertEquals(LocalDate.of(2026, 10, 1), expenses.get(0).getDate());
        assertEquals(LocalDate.of(2026, 10, 10), expenses.get(1).getDate());
        assertEquals(LocalDate.of(2026, 10, 15), expenses.get(2).getDate());
    }

    @Test
    void changingReturnedListDoesNotChangeService() {
        service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "");

        service.getAllExpenses().clear();

        assertEquals(1, service.getAllExpenses().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.01", "10.12", "12.500", "999999999.99"})
    void addExpenseWithValidAmount(String amount) {
        Expense expense = service.addExpense(new BigDecimal(amount), Category.FOOD, DATE, "");

        assertEquals(new BigDecimal(amount), expense.getAmount());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.00", "-1", "-0.01", "10.123"})
    void addExpenseWithInvalidAmountThrows(String amount) {
        assertThrows(IllegalArgumentException.class,
                () -> service.addExpense(new BigDecimal(amount), Category.FOOD, DATE, ""));
    }

    @Test
    void addExpenseWithNullAmountThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addExpense(null, Category.FOOD, DATE, ""));
    }

    @Test
    void addExpenseWithNullCategoryThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addExpense(new BigDecimal("10"), null, DATE, ""));
    }

    @Test
    void addExpenseWithNullDateThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addExpense(new BigDecimal("10"), Category.FOOD, null, ""));
    }

    @Test
    void addExpenseWithFutureDateThrows() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.addExpense(new BigDecimal("10"), Category.FOOD, tomorrow, ""));

        assertEquals("Date cannot be in the future", exception.getMessage());
    }

    @Test
    void addExpenseWithTodayDateIsAllowed() {
        Expense expense = service.addExpense(new BigDecimal("10"), Category.FOOD, LocalDate.now(), "");

        assertEquals(LocalDate.now(), expense.getDate());
    }

    @Test
    void invalidExpenseIsNotAdded() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addExpense(new BigDecimal("-5"), Category.FOOD, DATE, ""));

        assertTrue(service.getAllExpenses().isEmpty());
    }

    @Test
    void deleteExpenseRemovesIt() {
        Expense expense = service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "");

        service.deleteExpense(expense.getId());

        assertTrue(service.getAllExpenses().isEmpty());
    }

    @Test
    void deleteExpenseKeepsOtherExpenses() {
        Expense first = service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "");
        Expense second = service.addExpense(new BigDecimal("20"), Category.RENT, DATE.plusDays(1), "");
        Expense third = service.addExpense(new BigDecimal("30"), Category.OTHER, DATE.plusDays(2), "");

        service.deleteExpense(second.getId());

        List<Expense> expenses = service.getAllExpenses();
        assertEquals(2, expenses.size());
        assertEquals(first.getId(), expenses.get(0).getId());
        assertEquals(third.getId(), expenses.get(1).getId());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 99})
    void deleteExpenseWithUnknownIdThrows(int id) {
        service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "");

        assertThrows(IllegalArgumentException.class, () -> service.deleteExpense(id));
    }

    @Test
    void deleteExpenseFromEmptyServiceThrows() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteExpense(1));
    }

    @Test
    void deleteSameExpenseTwiceThrows() {
        Expense expense = service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "");
        service.deleteExpense(expense.getId());

        assertThrows(IllegalArgumentException.class, () -> service.deleteExpense(expense.getId()));
    }

    @Test
    void newExpenseAfterDeleteGetsNextId() {
        service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "");
        service.addExpense(new BigDecimal("20"), Category.FOOD, DATE, "");
        service.deleteExpense(1);

        Expense newExpense = service.addExpense(new BigDecimal("30"), Category.FOOD, DATE, "");

        assertEquals(3, newExpense.getId());
    }

    @Test
    void updateExpenseChangesAllFields() {
        Expense expense = service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "old");

        service.updateExpense(expense.getId(), new BigDecimal("25.50"), Category.RENT, DATE.minusDays(5), "new");

        Expense updated = service.findById(expense.getId());
        assertEquals(new BigDecimal("25.50"), updated.getAmount());
        assertEquals(Category.RENT, updated.getCategory());
        assertEquals(DATE.minusDays(5), updated.getDate());
        assertEquals("new", updated.getDescription());
    }

    @Test
    void updateExpenseKeepsId() {
        Expense expense = service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "");

        service.updateExpense(expense.getId(), new BigDecimal("20"), Category.RENT, DATE, "");

        assertEquals(1, service.getAllExpenses().size());
        assertEquals(expense.getId(), service.getAllExpenses().get(0).getId());
    }

    @Test
    void updateExpenseWithUnknownIdThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateExpense(99, new BigDecimal("10"), Category.FOOD, DATE, ""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-5", "10.123"})
    void updateExpenseWithInvalidAmountThrows(String amount) {
        Expense expense = service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "");

        assertThrows(IllegalArgumentException.class,
                () -> service.updateExpense(expense.getId(), new BigDecimal(amount), Category.FOOD, DATE, ""));
    }

    @Test
    void failedUpdateDoesNotChangeExpense() {
        Expense expense = service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "lunch");
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        assertThrows(IllegalArgumentException.class,
                () -> service.updateExpense(expense.getId(), new BigDecimal("99"), Category.RENT, tomorrow, "changed"));

        assertEquals(new BigDecimal("10"), expense.getAmount());
        assertEquals(Category.FOOD, expense.getCategory());
        assertEquals(DATE, expense.getDate());
        assertEquals("lunch", expense.getDescription());
    }

    @Test
    void updateDateChangesOrder() {
        Expense first = service.addExpense(new BigDecimal("10"), Category.FOOD, DATE, "");
        Expense second = service.addExpense(new BigDecimal("20"), Category.FOOD, DATE.plusDays(1), "");

        service.updateExpense(first.getId(), new BigDecimal("10"), Category.FOOD, DATE.plusDays(2), "");

        List<Expense> expenses = service.getAllExpenses();
        assertEquals(second.getId(), expenses.get(0).getId());
        assertEquals(first.getId(), expenses.get(1).getId());
    }

    @Test
    void totalForMonthSumsExpensesInThatMonth() {
        service.addExpense(new BigDecimal("0.10"), Category.FOOD, LocalDate.of(2026, 9, 5), "");
        service.addExpense(new BigDecimal("0.20"), Category.FOOD, LocalDate.of(2026, 9, 20), "");

        assertEquals(new BigDecimal("0.30"), service.getTotalForMonth(2026, 9));
    }

    @Test
    void totalForMonthWithNoExpensesIsZero() {
        assertEquals(BigDecimal.ZERO, service.getTotalForMonth(2026, 9));
    }

    @Test
    void totalForMonthIncludesFirstAndLastDay() {
        service.addExpense(new BigDecimal("10"), Category.FOOD, LocalDate.of(2026, 9, 1), "");
        service.addExpense(new BigDecimal("20"), Category.FOOD, LocalDate.of(2026, 9, 30), "");

        assertEquals(new BigDecimal("30"), service.getTotalForMonth(2026, 9));
    }

    @Test
    void totalForMonthIgnoresNeighbourMonths() {
        service.addExpense(new BigDecimal("5"), Category.FOOD, LocalDate.of(2026, 8, 31), "");
        service.addExpense(new BigDecimal("10"), Category.FOOD, LocalDate.of(2026, 9, 15), "");
        service.addExpense(new BigDecimal("7"), Category.FOOD, LocalDate.of(2026, 10, 1), "");

        assertEquals(new BigDecimal("10"), service.getTotalForMonth(2026, 9));
    }

    @Test
    void totalForMonthIgnoresSameMonthOfOtherYear() {
        service.addExpense(new BigDecimal("50"), Category.FOOD, LocalDate.of(2025, 9, 10), "");
        service.addExpense(new BigDecimal("10"), Category.FOOD, LocalDate.of(2026, 9, 10), "");

        assertEquals(new BigDecimal("10"), service.getTotalForMonth(2026, 9));
    }

    @Test
    void totalForMonthIncludesLeapDay() {
        service.addExpense(new BigDecimal("15"), Category.FOOD, LocalDate.of(2024, 2, 29), "");

        assertEquals(new BigDecimal("15"), service.getTotalForMonth(2024, 2));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 13, -1})
    void totalForMonthWithInvalidMonthThrows(int month) {
        assertThrows(IllegalArgumentException.class, () -> service.getTotalForMonth(2026, month));
    }
}