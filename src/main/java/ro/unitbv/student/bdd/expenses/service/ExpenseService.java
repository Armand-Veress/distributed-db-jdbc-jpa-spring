package ro.unitbv.student.bdd.expenses.service;

import ro.unitbv.student.bdd.expenses.model.Category;
import ro.unitbv.student.bdd.expenses.model.Expense;
import ro.unitbv.student.bdd.expenses.repository.ExpenseRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ExpenseService {
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

    public List<Expense> findAllSortedByAmountDesc() {
        List<Expense> all = new ArrayList<>();
        for (ExpenseRepository repository : repositories.values()) {
            all.addAll(repository.findAll());
        }
        all.sort(Comparator.comparing(Expense::getAmount).reversed());
        return all;
    }

    public Map<Category, Optional<BigDecimal>> averagesByCategory() {
        Map<Category, Optional<BigDecimal>> averages = new EnumMap<>(Category.class);
        for (ExpenseRepository repository : repositories.values()) {
            averages.put(repository.getCategory(), repository.getAverage());
        }
        return averages;
    }
}
