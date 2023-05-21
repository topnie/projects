package webapp.controllers;

import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webapp.bank.model.historia.Historia;
import webapp.bank.model.historia_kredyt.Historia_kredyt;
import webapp.bank.model.kredyt.Kredyt;
import webapp.bank.model.pracownik.Pracownik;
import webapp.services.AccountService;
import webapp.services.EmployeeService;
import webapp.services.LoanService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
@SessionAttributes({"id", "number", "err"})
public class LoanPayController {
    @Autowired
    private LoanService loanService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/client/loan/pay/request")
    public String payLoan(Model model, RedirectAttributes redirectAttributes) {
        long num = (Long) model.getAttribute("number");
        redirectAttributes.addAttribute("number", model.getAttribute("number"));
        if (accountService.isThisUserAnOwner((Long) model.getAttribute("id"), loanService.getLoan(num).getNr_konta())) {
            long installment = loanService.getLoan(num).getKolejna_rata();
            if (accountService.doesThisAccountHaveEnough((long) loanService.getLoan(num).getNr_konta(), installment)) {
                if (!Objects.equals(loanService.getLoan(num).getStan(), "w trakcie")) {
//
                }
                else {
                    accountService.pay(loanService.getLoan(num).getNr_konta(), (int) installment);
                    loanService.updateLoan(num, installment);
                }
            }
            else {
                redirectAttributes.addAttribute("err", "Za malo pieniedzy");
            }
            return "redirect:/loan";
        }
        else return "error-access-denied";
    }
}
