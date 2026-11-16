package ro.unitbv.student.bdd.expenses.ui;

import ro.unitbv.student.bdd.expenses.model.Category;
import ro.unitbv.student.bdd.expenses.model.Expense;
import ro.unitbv.student.bdd.expenses.repository.RepositoryException;
import ro.unitbv.student.bdd.expenses.service.ExpenseService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ConsoleMenu {
    private static final String LINE = "----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8----+----9----+\n>> ";

    private final ExpenseService service;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(ExpenseService service) {
        this.service = service;
    }

    public void run() {
        printOptions();
        while (true) {
            System.out.print(LINE);
            String option = scanner.nextLine().trim();
            try {
                switch (option) {
                    case "1" -> showAllExpenses();
                    case "2" -> showAverages();
                    case "3" -> addExpense();
                    case "?" -> printOptions();
                    case "" -> { }
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Invalid option.\n?. Options");
                }
            } catch (RepositoryException e) {
                System.out.println("Database error: " + e.getMessage()
                        + " (" + e.getCause().getMessage() + ")");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private void printOptions() {
        System.out.println("1. List expenses (desc BY amount)");
        System.out.println("2. Averages per Category");
        System.out.println("3. Add expense");
        System.out.println("?. Options");
        System.out.println("0. Exit");
    }

    private void showAllExpenses() {
        ExpenseService.ExpensesResult result = service.findAllSortedByAmountDesc();

        for (Expense expense : result.expenses()) {
            System.out.println(expense);
        }
        for (Category category : result.unavailable()) {
            System.out.println("[!] " + category + " database unavailable");
        }

        if (result.expenses().isEmpty()) {
            System.out.println("No expenses found.");
        }
    }

    private void showAverages() {
        Map<Category, Optional<BigDecimal>> averages = service.averagesByCategory();
        for(Category c : Category.values()) {
            Optional<BigDecimal> average = averages.get(c);
            String text;
            if (average == null) {
                text = "source unavailable";
            } else if (average.isEmpty()) {
                text = "no expenses";
            } else {
                text = average.get().toString();
            }
            System.out.println(c.name() + ": " + text);
        }
    }

    private void addExpense() {
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

        System.out.print("Date (yyyy-mm-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().trim());

        System.out.print("Category (AUTO, FOOD, BILLS): ");
        Category category = Category.valueOf(scanner.nextLine().trim().toUpperCase());

        Expense expense = service.addExpense(description, amount, date, category);
        System.out.println("Saved: " + expense);
    }
}
