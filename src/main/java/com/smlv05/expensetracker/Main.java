package com.smlv05.expensetracker;

import com.smlv05.expensetracker.service.ExpenseService;
import com.smlv05.expensetracker.storage.CsvStorage;
import com.smlv05.expensetracker.ui.ConsoleMenu;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        ExpenseService service = new ExpenseService();
        ConsoleMenu menu = new ConsoleMenu(service, new CsvStorage(), Path.of("expenses.csv"));
        menu.run();
    }
}