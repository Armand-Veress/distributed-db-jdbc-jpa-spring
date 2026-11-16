package ro.unitbv.student.bdd.bills.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ro.unitbv.student.bdd.bills.entity.Expense;
import ro.unitbv.student.bdd.bills.service.ExpenseService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseRestController {
    private final ExpenseService service;

    public ExpenseRestController(ExpenseService service) {
        this.service = service;
    }

    @GetMapping
    public List<Expense> findAll() {
        return service.findAllSortedByAmountDesc();
    }

    public record AverageResponse(BigDecimal average) {}

    @GetMapping("/average")
    public AverageResponse average() {
        return new AverageResponse(service.getAverage().orElse(null));
    }

    public record ExpenseRequest(
            @NotBlank String description,
            @NotNull @Positive BigDecimal amount,
            @NotNull LocalDate date) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense add(@Valid @RequestBody ExpenseRequest request) {
        return service.add(request.description(), request.amount(), request.date());
    }
}
