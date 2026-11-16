package ro.unitbv.student.bdd.bills.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ro.unitbv.student.bdd.bills.service.ExpenseService;

@Controller
@RequestMapping("/bills")
public class ExpenseWebController {

    private final ExpenseService service;

    public ExpenseWebController(ExpenseService service) {
        this.service = service;
    }

    @GetMapping
    public String showBills(Model model) {
        model.addAttribute("expenses", service.findAllSortedByAmountDesc());
        model.addAttribute("average", service.getAverage().orElse(null));
        return "bills";
    }
}
