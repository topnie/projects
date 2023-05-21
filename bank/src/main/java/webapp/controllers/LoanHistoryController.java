package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webapp.bank.model.historia_kredyt.Historia_kredyt;
import webapp.bank.model.kredyt.Kredyt;
import webapp.services.AccountService;
import webapp.services.LoanService;

import java.util.Collections;
import java.util.List;

@Controller
@SessionAttributes({"id", "number", "err"})
public class LoanHistoryController {
    @Autowired
    private LoanService loanService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/loan")
    public String accessLoan(@RequestParam("number") long num, Model model) {
        if (accountService.isThisUserAnOwner((Long) model.getAttribute("id"), loanService.getLoan(num).getNr_konta()) ||
                accountService.isThisUserAnEmployee((Long) model.getAttribute("id"))) {
            model.addAttribute("number", num);
            List<Historia_kredyt> h = loanService.getLoanHistory(num);
            Collections.reverse(h);
            model.addAttribute("historia_operacji", h);
            model.addAttribute("stan_zaplaty", loanService.stanAktualnejRaty(num));
            model.addAttribute("data_zaplaty", loanService.getLoan(num).getData());
            model.addAttribute("kwota_do_zaplacenia", loanService.getLoan(num).getKolejna_rata());
            if (model.containsAttribute("err")) {
                model.addAttribute("error", "Niewystarczająca ilość środków na koncie połączonym z kredytem");
            }
            return "loan-page-client";
        }
        else return "error-access-denied";
    }

    @GetMapping("/simulation")
    public String accessSim(@RequestParam("number") long num, Model model) {
        if (accountService.isThisUserAnOwner((Long) model.getAttribute("id"), loanService.getLoan(num).getNr_konta()) ||
                accountService.isThisUserAnEmployee((Long) model.getAttribute("id"))) {
            model.addAttribute("number", num);
            List<Kredyt> h = loanService.getLoan(num).getPrediction();
            model.addAttribute("historia", h);
            return "simulation";
        }
        else return "error-access-denied";
    }

}
