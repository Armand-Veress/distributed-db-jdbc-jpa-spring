package ro.unitbv.student.bdd.expenses;

import ro.unitbv.student.bdd.expenses.config.DbSettings;
import ro.unitbv.student.bdd.expenses.model.Category;
import ro.unitbv.student.bdd.expenses.model.Expense;
import ro.unitbv.student.bdd.expenses.repository.ExpenseRepository;
import ro.unitbv.student.bdd.expenses.repository.jdbc.JdbcExpenseRepository;
import ro.unitbv.student.bdd.expenses.repository.jpa.JpaExpenseRepository;
import ro.unitbv.student.bdd.expenses.service.ExpenseService;
import ro.unitbv.student.bdd.expenses.ui.ConsoleMenu;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ExpenseRepository auto = new JdbcExpenseRepository(DbSettings.load("auto"), Category.AUTO);
        JpaExpenseRepository food = new JpaExpenseRepository(DbSettings.load("food"), Category.FOOD);

        ExpenseService service = new ExpenseService(List.of(auto, food));
        new ConsoleMenu(service).run();

        food.close();
    }
}
