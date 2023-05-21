package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import webapp.services.AccountService;
import webapp.services.ClientService;
import webapp.services.EmployeeService;

@Controller
@SessionAttributes({"id", "accounts"})
public class TransferPageController {

    @Autowired
    private ClientService userService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/client/transfer")
    public String transferPage(Model model) {
        if (model.getAttribute("id") == null || accountService.isThisUserAnEmployee((Long) model.getAttribute("id"))) {
            return "error-access-denied";
        }
        else {
            model.addAttribute("accounts", userService.getUserAccounts((Long) model.getAttribute("id")));
            return "transfer";
        }
    }

    @PostMapping("/client/transfer")
    public String makeTransfer(@RequestParam("num") Long num,
                               @RequestParam("receiver") String receiver,
                               @RequestParam("amount") String amount, Model model) {
        boolean success;
        try {
            Long.parseLong(receiver);

            success = accountService.doesThisAccountExist(Long.parseLong(receiver));

        } catch(NumberFormatException e){
            model.addAttribute("error", "Podany napis nie jest poprawnym numerem konta");
            return "transfer";
        }
        if (success) {
            try {
                Long.parseLong(amount);
                success = accountService.doesThisAccountHaveEnough(num, Long.parseLong(amount));
            }
            catch(NumberFormatException e) {
                model.addAttribute("error", "Podany napis nie jest poprawną kwotą");
                return "transfer";
            }
            if (success) {
                if (num == Long.parseLong(receiver)) {
                    model.addAttribute("error", "Nie można wykonać przelewu na to samo konto");
                    return "transfer";
                }
                accountService.sendTransfer(num, Long.parseLong(receiver), Long.parseLong(amount));
                return "transfer-sent";
            }
            else {
                model.addAttribute("error", "Twoje konto nie ma wystarczającej ilości środków");
                return "transfer";
            }
        }
        else {
            model.addAttribute("error", "Konto o danym numerze nie istnieje");
            return "transfer";
        }
    }
}
