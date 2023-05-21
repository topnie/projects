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
import webapp.bank.model.uzytkownik.Uzytkownik;
import webapp.repositories.EmployeeRepository;
import webapp.services.AccountService;
import webapp.services.EmailService;
import webapp.services.EmployeeService;
import webapp.services.LoanService;

import java.util.*;

@Controller
@SessionAttributes("id")
public class LoanRequestEmployeeController {

    private int errNum = 0;
    @Autowired
    private LoanService loanService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/employee/loan/requests")
    public String activeLoanRequests(Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null || !Objects.equals(p.getDzial(), "Kredytu")) return "error-access-denied";
        String dzial = p.getDzial();
        List<Kredyt> requests = loanService.getLoanRequests();
        model.addAttribute("requests", requests);
        if (errNum == 1) model.addAttribute("error", "Kredyt zaakceptowany");
        else if (errNum == 2) model.addAttribute("error", "Kredyt odrzucony");
        errNum = 0;
        return "loanrequests";
    }

    @PostMapping(value = "/employee/loan/accept")
    public String acceptLoanRequest(@RequestParam("nr_kredytu") long id, Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null || !Objects.equals(p.getDzial(), "Kredytu")) return "error-access-denied";
        loanService.changeLoanStatus((int) id, true);
        accountService.sendLoan(loanService.getLoan(id).getNr_konta(), loanService.getLoan(id).getKwota_pozyczki());
        Kredyt k = loanService.getLoan(id);
        if (k.isCzy_zmienne())  {
            Random r = new Random();
            k.setOprocentowanie(r.nextInt(1, 20));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(k.getData());
        calendar.add(Calendar.DATE, k.getOkres()); // add days
        Date nowyTermin = calendar.getTime();
        k.setData(nowyTermin);
        loanService.update(k.getId(), k);
        errNum = 1;
        return "redirect:/employee/loan/requests";
    }

    @PostMapping(value = "/employee/loan/decline")
    public String declineLoanRequest(@RequestParam("nr_kredytu") long id, Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null || !Objects.equals(p.getDzial(), "Kredytu")) return "error-access-denied";
        loanService.changeLoanStatus((int) id, false);
        errNum = 2;
        return "redirect:/employee/loan/requests";
    }
}
