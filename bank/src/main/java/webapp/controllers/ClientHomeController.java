package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webapp.services.ClientService;

@Controller
@SessionAttributes("id")
public class ClientHomeController {

    @Autowired
    private ClientService userService;

    @GetMapping("/client/home")
    public String clientHome(Model model) {
        model.addAttribute("name", userService.findById((Long) model.getAttribute("id")).getImie());
        model.addAttribute("accounts", userService.getUserAccounts((Long) model.getAttribute("id")));
        return "client-home";
    }

    @PostMapping("/client/loan")
    public String loanButton(Model model) {
        return "redirect:/client/loan";
    }

    @PostMapping("/client/data")
    public String dataButton(Model model) {
        return "redirect:/client/data";
    }
}
