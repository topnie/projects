package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import webapp.bank.model.klient.Klient;
import webapp.bank.model.konto.Konto;
import webapp.bank.model.pracownik.Pracownik;
import webapp.bank.utils.Waluta;
import webapp.services.AccountService;
import webapp.services.ClientService;
import webapp.services.EmployeeService;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Controller
@SessionAttributes("id")
public class AddAccountController {

    private int err = 0;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/employee/add/account")
    public String addAccountForm(Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null) return "error-access-denied";
        Waluta w = new Waluta();
        model.addAttribute("waluty", w.getAll());
        if (err == 1) {
            model.addAttribute("error", "Podane dane nie są poprawne");
            err = 0;
        }
        return "addaccountform";
    }

    @PostMapping(value = "/employee/add/account")
    public String addAccount(@RequestParam("klientid1") String id1,
                             @RequestParam("klientid2") String id2,
                             @RequestParam("stan") String stan,
                             @RequestParam("waluta") String waluta, Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null) return "error-access-denied";
        boolean success = true;
        int pocz_stan = 0;
        List<Integer> wlasciciele = new LinkedList<>();
        try {
            pocz_stan = Integer.parseInt(stan);
            int idpierwszego = Integer.parseInt(id1);
            Klient k1 = clientService.findKlientById(idpierwszego);
            if (k1 == null) throw new NumberFormatException();
            wlasciciele.add(idpierwszego);
            if (!Objects.equals(id2, "")) {
                int iddrugiego = Integer.parseInt(id2);
                Klient k2 = clientService.findKlientById(iddrugiego);
                if (k2 == null) throw new NumberFormatException();
                wlasciciele.add(iddrugiego);
            }
        }
        catch (NumberFormatException e) {
            success = false;
        }
        if (success) {
            accountService.addAccount(new Konto(0, waluta, pocz_stan), wlasciciele);
            return "addaccountsuccess";
        }
        else {
            err = 1;
            return "redirect:/employee/add/account";
        }
    }

}
