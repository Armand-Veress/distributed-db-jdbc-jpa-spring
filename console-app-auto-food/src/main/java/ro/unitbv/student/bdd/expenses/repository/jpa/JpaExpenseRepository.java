package ro.unitbv.student.bdd.expenses.repository.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import ro.unitbv.student.bdd.expenses.config.DbSettings;
import ro.unitbv.student.bdd.expenses.model.Category;
import ro.unitbv.student.bdd.expenses.model.Expense;
import ro.unitbv.student.bdd.expenses.repository.ExpenseRepository;
import ro.unitbv.student.bdd.expenses.repository.RepositoryException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JpaExpenseRepository implements ExpenseRepository {
    private final EntityManagerFactory emf;
    private final Category category;

    public JpaExpenseRepository(DbSettings settings, Category category) {
        this.category = category;
        this.emf = Persistence.createEntityManagerFactory("expenses", Map.of(
                "jakarta.persistence.jdbc.url", settings.url(),
                "jakarta.persistence.jdbc.user", settings.user(),
                "jakarta.persistence.jdbc.password", settings.password()));
    }

    public void close() {
        emf.close();
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public List<Expense> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Expense> expenses = em
                    .createQuery("SELECT e FROM Expense e ORDER BY e.amount DESC", Expense.class)
                    .getResultList();
            for (Expense expense : expenses) {
                expense.setCategory(category);
            }
            return expenses;
        } catch (PersistenceException e) {
            throw new RepositoryException("Could not load expenses from " + category + " database", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Expense expense) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(expense);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RepositoryException("Could not save expense to " + category + " database", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<BigDecimal> getAverage() {
        EntityManager em = emf.createEntityManager();
        try {
            Double average = em
                    .createQuery("SELECT AVG(e.amount) FROM Expense e", Double.class)
                    .getSingleResult();
            return Optional.ofNullable(average).map(BigDecimal::valueOf);
        } catch (PersistenceException e) {
            throw new RepositoryException("Could not compute average for " + category + " database", e);
        } finally {
            em.close();
        }
    }
}