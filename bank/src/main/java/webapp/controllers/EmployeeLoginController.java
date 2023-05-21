package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webapp.bank.model.pracownik.Pracownik;
import webapp.services.ClientService;
import webapp.services.EmployeeService;

import java.util.Objects;

@Controller
@SessionAttributes("id")
public class EmployeeLoginController {
    @Autowired
    private EmployeeService employeeService;

    @RequestMapping(value = "/employee", method = RequestMethod.GET)
    public String loginForm() {
        return "employee";
    }

    @RequestMapping(value = "/employee", method = RequestMethod.POST)
    public String login(@RequestParam("id") String id,
                        @RequestParam("password") String password,
                        Model model) {
        boolean success;
        try {
            Long.parseLong(id);
            success = employeeService.login(Long.parseLong(id), password);

        } catch(NumberFormatException e){
            success = false;
        }
        if (success) {
            model.addAttribute("id", Long.parseLong(id));
            Pracownik p = employeeService.findPracownikById(Long.parseLong(id));
            String dzial = p.getDzial();
            if (Objects.equals(dzial, "Obslugi")) {
                return "redirect:/employee/home-service";
            }
            else {
                return "redirect:/employee/home-loan";
            }

        } else {
            model.addAttribute("error", "Invalid email or password");
            return "employee";
        }
    }




}
