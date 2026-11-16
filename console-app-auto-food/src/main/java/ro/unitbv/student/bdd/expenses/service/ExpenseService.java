package ro.unitbv.student.bdd.expenses.service;

import ro.unitbv.student.bdd.expenses.model.Category;
import ro.unitbv.student.bdd.expenses.model.Expense;
import ro.unitbv.student.bdd.expenses.repository.ExpenseRepository;
import ro.unitbv.student.bdd.expenses.repository.RepositoryException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ExpenseService {
    public record ExpensesResult(List<Expense> expenses, List<Category> unavailable) {}
    private final Map<Category, ExpenseRepository> repositories = new EnumMap<>(Category.class);

    public ExpenseService(List<ExpenseRepository> repositories) {
        for (ExpenseRepository repository : repositories) {
            this.repositories.put(repository.getCategory(), repository);
        }
    }

    private ExpenseRepository repositoryFor(Category category) {
        ExpenseRepository repository = repositories.get(category);
        if (repository == null) {
            throw new IllegalArgumentException("No database configured for category " + category);
        }
        return repository;
    }

    public Expense addExpense(String description, BigDecimal amount, LocalDate date, Category category) {
        Expense expense = new Expense(description, amount, date, category);
        repositoryFor(category).save(expense);
        return expense;
    }

    public ExpensesResult findAllSortedByAmountDesc() {
        List<Expense> all = new ArrayList<>();
        List<Category> unavailable = new ArrayList<>();

        for (ExpenseRepository repository : repositories.values()) {
            try {
                all.addAll(repository.findAll());
            } catch (RepositoryException e) {
                unavailable.add(repository.getCategory());
            }
        }

        all.sort(Comparator.comparing(Expense::getAmount).reversed());
        return new ExpensesResult(all, unavailable);
    }

    public Map<Category, Optional<BigDecimal>> averagesByCategory() {
        Map<Category, Optional<BigDecimal>> averages = new EnumMap<>(Category.class);
        for (ExpenseRepository repository : repositories.values()) {
            try {
                averages.put(repository.getCategory(), repository.getAverage());
            } catch (RepositoryException e) {
                // source unavailable
            }
        }
        return averages;
    }
}
