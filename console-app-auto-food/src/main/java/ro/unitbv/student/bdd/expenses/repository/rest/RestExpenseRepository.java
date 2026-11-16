package ro.unitbv.student.bdd.expenses.repository.rest;

import ro.unitbv.student.bdd.expenses.model.Category;
import ro.unitbv.student.bdd.expenses.model.Expense;
import ro.unitbv.student.bdd.expenses.repository.ExpenseRepository;
import ro.unitbv.student.bdd.expenses.repository.RepositoryException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestExpenseRepository implements ExpenseRepository {
    private final String baseUrl;
    private final Category category;
    private final HttpClient http = HttpClient.newHttpClient();
    private final JsonMapper json = JsonMapper.builder().build();

    public RestExpenseRepository(String baseUrl, Category category) {
        this.baseUrl = baseUrl;
        this.category = category;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    record BillJson(Long expenseId, String description, BigDecimal amount, LocalDate date) {}
    record NewBillJson(String description, BigDecimal amount, LocalDate date) {}
    record AverageJson(BigDecimal average) {}

    @Override
    public List<Expense> findAll() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl)).GET().build();
        String body = send(request, 200);

        List<BillJson> bills = json.readValue(body, new TypeReference<List<BillJson>>() {});

        List<Expense> expenses = new ArrayList<>();
        for (BillJson bill : bills) {
            Expense expense = new Expense(bill.description(), bill.amount(), bill.date(), category);
            expense.setExpenseId(bill.expenseId());
            expenses.add(expense);
        }
        return expenses;
    }

    @Override
    public void save(Expense expense) {
        NewBillJson newBill = new NewBillJson(expense.getDescription(), expense.getAmount(), expense.getDate());

        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(newBill)))
                .build();

        BillJson saved = json.readValue(send(request, 201), BillJson.class);
        expense.setExpenseId(saved.expenseId());
    }

    @Override
    public Optional<BigDecimal> getAverage() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/average")).GET().build();
        AverageJson avg = json.readValue(send(request, 200), AverageJson.class);
        return Optional.ofNullable(avg.average());
    }

    private String send(HttpRequest request, int expectedStatus) {
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != expectedStatus) {
                throw new RepositoryException("Unexpected response from " + category + " service",
                        new IllegalStateException("HTTP " + response.statusCode() + ": " + response.body()));
            }
            return response.body();
        } catch (IOException e) {
            throw new RepositoryException("Could not reach " + category + " service", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RepositoryException("Request to " + category + " service was interrupted", e);
        }
    }
}
