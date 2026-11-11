package ro.unitbv.student.bdd.expenses.repository;

import ro.unitbv.student.bdd.expenses.model.Category;
import ro.unitbv.student.bdd.expenses.model.Expense;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {
    Category getCategory();
    List<Expense> findAll();
    void save(Expense expense);
    Optional<BigDecimal> getAverage();
}
