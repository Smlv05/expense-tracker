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
}