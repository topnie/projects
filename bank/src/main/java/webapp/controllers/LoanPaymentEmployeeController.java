package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import webapp.bank.model.kredyt.Kredyt;
import webapp.bank.model.pracownik.Pracownik;
import webapp.services.AccountService;
import webapp.services.EmployeeService;
import webapp.services.LoanService;

import java.time.LocalDate;
import java.util.*;

@Controller
@SessionAttributes("id")
public class LoanPaymentEmployeeController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "employee/loan/payment")
    public String loanPayments(Model model) {
        List<Kredyt> loans = loanService.getAll();
        List<Kredyt> loansToRemove = new ArrayList<>();
        for (Kredyt loan : loans) {
            String currentStatus = loanService.stanAktualnejRaty(loan.getId());
            if (!Objects.equals(currentStatus, "rataoplacona") || loan.getStan().equals("splacony")) {
                loansToRemove.add(loan);
            }
        }
        loans.removeAll(loansToRemove);
        model.addAttribute("kredyty_nadplata", loans);
        return "nadplaty";
    }

    @PostMapping(value = "/employee/loan/payment/overpay")
    public String overPay(@RequestParam("nr_kredytu") long num, Model model) {
        Kredyt k = loanService.getLoan(num);
        int installment = k.getKolejna_rata();
        if (accountService.doesThisAccountHaveEnough((long) loanService.getLoan(num).getNr_konta(), installment)) {
            accountService.pay(loanService.getLoan(num).getNr_konta(), installment);
            loanService.updateLoan(num, installment);
            k = loanService.getLoan(num);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(java.sql.Date.valueOf(LocalDate.now()));
            calendar.add(Calendar.DATE, k.getOkres()); // add days
            Date nowyTermin = calendar.getTime();
            k.setData(nowyTermin);
            loanService.update(k.getId(), k);
            model.addAttribute("error", "Nadpłata wykonana");
            List<Kredyt> loans = loanService.getAll();
            List<Kredyt> loansToRemove = new ArrayList<>();
            for (Kredyt loan : loans) {
                String currentStatus = loanService.stanAktualnejRaty(loan.getId());
                if (!Objects.equals(currentStatus, "rataoplacona")) {
                    loansToRemove.add(loan);
                }
            }
            loans.removeAll(loansToRemove);
            model.addAttribute("kredyty_nadplata", loans);

        }
        else {
            List<Kredyt> loans = loanService.getAll();
            List<Kredyt> loansToRemove = new ArrayList<>();
            for (Kredyt loan : loans) {
                String currentStatus = loanService.stanAktualnejRaty(loan.getId());
                if (!Objects.equals(currentStatus, "rataoplacona")) {
                    loansToRemove.add(loan);
                }
            }
            loans.removeAll(loansToRemove);
            model.addAttribute("kredyty_nadplata", loans);
            model.addAttribute("error", "Na koncie nie ma wystarczająco dużo środków");
        }
        return "nadplaty";
    }

}
