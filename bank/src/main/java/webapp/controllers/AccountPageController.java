package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import webapp.bank.model.historia.Historia;
import webapp.services.AccountService;

import java.util.Collections;
import java.util.List;

@Controller
@SessionAttributes("id")
public class AccountPageController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/account")
    public String accessAccount(@RequestParam("number") long num, Model model) {
        if (accountService.isThisUserAnOwner((Long) model.getAttribute("id"), num) ||
        accountService.isThisUserAnEmployee((Long) model.getAttribute("id"))) {
            model.addAttribute("acc_number", num);
            List<Historia> h = accountService.getAccountHistory(num);
            Collections.reverse(h);
            model.addAttribute("historia_operacji", h);
            model.addAttribute("owners", accountService.getAccountOwners(num));
            if (accountService.isThisUserAnOwner((Long) model.getAttribute("id"), num)) return "account-page-client";
            else return "account-page-employee";
        }
        else return "error-access-denied";

    }


}
