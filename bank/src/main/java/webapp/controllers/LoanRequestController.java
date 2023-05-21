package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import webapp.bank.model.kredyt.Kredyt;
import webapp.services.ClientService;
import webapp.services.EmployeeService;
import webapp.services.LoanService;

import java.time.LocalDate;
import java.util.Objects;

@Controller
@SessionAttributes({"id", "accounts"})
public class LoanRequestController {

    @Autowired
    private ClientService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LoanService loanService;

    @GetMapping("/client/loan/request")
    public String loanRequest(Model model) {
        if (model.getAttribute("id") == null) {
            return "error-access-denied";
        }
        else {
            model.addAttribute("accounts", userService.getUserAccounts((Long) model.getAttribute("id")));
            return "loan-request-form";
        }
    }

    @PostMapping("/client/loan/request")
    public String sendRequest(@RequestParam("account_number") Long num,
                              @RequestParam("loan_size") String loan_s,
                              @RequestParam("oprocentowanie") String typ_opr,
                              @RequestParam("okres") String raty,
                              @RequestParam("raty_typ") String typ_rat, Model model) {
        boolean success;
        try {
            Long.parseLong(loan_s);
            success = true;

        } catch(NumberFormatException e){
            success = false;
        }
        if (Objects.equals(typ_opr, "Zmienne") && typ_rat.equals("Stałe")) {
            model.addAttribute("error", "Nie jest dostępny kredyt o stałych ratach i zmiennym oprocentowaniu");
            return "loan-request-form";
        }
        if (Objects.equals(raty, "Kwartalne") && typ_opr.equals("Zmienne")) {
            model.addAttribute("error", "Nie jest dostępny kredyt o ratach kwartalnych i zmiennym oprocentowaniu");
            return "loan-request-form";
        }
        if (success) {
            boolean czy_zmienne;
            czy_zmienne = typ_opr.equals("Zmienne");
            int okres;
            if (Objects.equals(raty, "Kwartalne")) okres = 90;
            else okres = 30;
            if (Objects.equals(typ_rat, "Malejące")) typ_rat = "malejace";
            else typ_rat = "stale";
            long id_pracownik = employeeService.getAllEmployees().get(0).getId();
            Kredyt k = new Kredyt(0, (int) (long) num, (int) id_pracownik, (int) (long) Long.parseLong(loan_s),
                    0, 5, okres, java.sql.Date.valueOf(LocalDate.now()), czy_zmienne,
                    "nierozpatrzony", typ_rat, 0, 0);
            long pierwszarata = k.getFirstInstallment();
            k.setKolejna_rata((int) pierwszarata);
            loanService.addLoan(k);
            return "loan-request-sent";
        } else {
            model.addAttribute("error", "Podany napis nie jest numerem");
            return "loan-request-form";
        }
    }
}
