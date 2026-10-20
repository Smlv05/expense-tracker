package com.smlv05.expensetracker;

import com.smlv05.expensetracker.service.ExpenseService;
import com.smlv05.expensetracker.ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("Expense Tracker");

        ExpenseService service = new ExpenseService();
        ConsoleMenu menu = new ConsoleMenu(service);
        menu.run();
    }
}
