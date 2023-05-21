package webapp.controllers;

import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webapp.bank.model.historia.Historia;
import webapp.bank.model.historia_kredyt.Historia_kredyt;
import webapp.bank.model.pracownik.Pracownik;
import webapp.services.AccountService;
import webapp.services.EmployeeService;
import webapp.services.LoanService;

import java.util.Collections;
import java.util.List;

@Controller
@SessionAttributes("id")
public class LoanPageController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LoanService loanService;

    @RequestMapping("/client/loan")
    public String getLoans(Model model) {
        Object id = model.getAttribute("id");
        if (id == null) {
            return "error-access-denied";
        }
        else {
            model.addAttribute("loans", loanService.getUserLoans((Long) id));
            return "loan";
        }
    }

    @PostMapping("/client/loan/redirect")
    public String redirectRequest(Model model) {
        return "redirect:/client/loan/request";
    }

    @RequestMapping(value = "employee/loans")
    public String showAllLoans(Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null) return "error-access-denied";
        model.addAttribute("loans", loanService.getAll());
        return "loans-emp";
    }
}
