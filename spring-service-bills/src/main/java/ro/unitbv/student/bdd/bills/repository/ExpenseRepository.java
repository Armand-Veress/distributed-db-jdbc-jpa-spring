package ro.unitbv.student.bdd.bills.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ro.unitbv.student.bdd.bills.entity.Expense;

import java.util.List;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {
    List<Expense> findAllByOrderByAmountDesc();

    @Query("SELECT AVG(e.amount) FROM Expense e")
    Double findAverageAmount();
}
