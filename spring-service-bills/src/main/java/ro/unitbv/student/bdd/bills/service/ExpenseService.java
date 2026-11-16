package ro.unitbv.student.bdd.bills.service;

import org.springframework.stereotype.Service;
import ro.unitbv.student.bdd.bills.entity.Expense;
import ro.unitbv.student.bdd.bills.repository.ExpenseRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<Expense> findAllSortedByAmountDesc() {
        return repository.findAllByOrderByAmountDesc();
    }

    public Expense add(String description, BigDecimal amount, LocalDate date) {
        return repository.save(new Expense(description, amount, date));
    }

    public Optional<BigDecimal> getAverage() {
        return Optional.ofNullable(repository.findAverageAmount()).map(BigDecimal::valueOf);
    }
}
