package ro.unitbv.student.bdd.expenses.repository.jdbc;

import ro.unitbv.student.bdd.expenses.config.DbSettings;
import ro.unitbv.student.bdd.expenses.model.Category;
import ro.unitbv.student.bdd.expenses.model.Expense;
import ro.unitbv.student.bdd.expenses.repository.ExpenseRepository;
import ro.unitbv.student.bdd.expenses.repository.RepositoryException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExpenseRepository implements ExpenseRepository {
    private final DbSettings settings;
    private final Category category;

    private static final String INSERT_SQL = "INSERT INTO expense (description, amount, expense_date) VALUES (?, ?, ?)";
    private static final String SELECT_SQL = "SELECT id, description, amount, expense_date FROM expense ORDER BY amount DESC";
    private static final String AVG_SQL = "SELECT AVG(amount) AS average FROM expense";

    public JdbcExpenseRepository(DbSettings settings, Category category) {
        this.settings = settings;
        this.category = category;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(settings.url(), settings.user(), settings.password());
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public List<Expense> findAll() {
        List<Expense> expenses = new ArrayList<>();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(SELECT_SQL);
             ResultSet rows = statement.executeQuery()) {

            while (rows.next()) {
                expenses.add(mapRow(rows));
            }

        } catch (SQLException e) {
            throw new RepositoryException("Could not load expenses from " + category + " database", e);
        }
        return expenses;
    }

    private Expense mapRow(ResultSet row) throws SQLException {
        Long id = row.getLong("id");
        String description = row.getString("description");
        BigDecimal amount = row.getBigDecimal("amount");
        LocalDate date = row.getObject("expense_date", LocalDate.class);

        Expense expense = new Expense(description, amount, date, category);
        expense.setExpenseId(id);

        return expense;
    }

    @Override
    public void save(Expense expense) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, new String[] {"id"})) {

            statement.setString(1, expense.getDescription());
            statement.setBigDecimal(2, expense.getAmount());
            statement.setObject(3, expense.getDate());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    expense.setExpenseId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not save expense to " + category + " database", e);
        }
    }

    @Override
    public Optional<BigDecimal> getAverage() {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(AVG_SQL);
             ResultSet result = statement.executeQuery()) {

            result.next();
            return Optional.ofNullable(result.getBigDecimal("average"));
        } catch (SQLException e) {
            throw new RepositoryException("Could not compute average for " + category + " database", e);
        }
    }
}
