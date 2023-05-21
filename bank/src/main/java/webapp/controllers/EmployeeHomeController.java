package webapp.controllers;

import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import webapp.bank.model.pracownik.Pracownik;
import webapp.services.ClientService;
import webapp.services.EmployeeService;

import java.util.Objects;

@Controller
@SessionAttributes("id")
public class EmployeeHomeController {

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/employee/home-service")
    public String clientHome(Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null) return "error-access-denied";
        model.addAttribute("name", employeeService.findById((Long) model.getAttribute("id")).getImie());
        model.addAttribute("accounts", employeeService.getAllAccounts());
        return "employee-home-service";
    }

    @RequestMapping("/employee/home-loan")
    public String loanEmpHome(Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null) return "error-access-denied";
        model.addAttribute("name", employeeService.findById((Long) model.getAttribute("id")).getImie());
        model.addAttribute("accounts", employeeService.getAllAccounts());
        return "employee-home-loan";
    }

    @RequestMapping(value ="/employee/redirecthome")
    public String homeEmp(Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null) return "error-access-denied";
        String dzial = p.getDzial();
        if (Objects.equals(dzial, "Kredytu")) {
            return "redirect:/employee/home-loan";
        }
        else {
            return "redirect:/employee/home-service";
        }
    }

    @PostMapping(value = "employee/loanrequests")
    public String loanRequests(Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null) return "error-access-denied";
        String dzial = p.getDzial();
        if (Objects.equals(dzial, "Kredytu")) {
            return "redirect:/employee/loan/requests";
        }
        else {
            return "error-access-denied";
        }
    }

    @PostMapping(value = "employee/loanpayment")
    public String loanPayments(Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null) return "error-access-denied";
        String dzial = p.getDzial();
        if (Objects.equals(dzial, "Obslugi")) {
            return "redirect:/employee/loan/payment";
        }
        else {
            return "error-access-denied";
        }
    }

    @PostMapping(value = "employee/changeinforequest")
    public String changeInfoRequest(Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null) return "error-access-denied";
        String dzial = p.getDzial();
        if (Objects.equals(dzial, "Obslugi")) {
            return "redirect:/employee/changeinfo";
        }
        else {
            return "error-access-denied";
        }
    }

    @PostMapping(value = "employee/loans")
    public String showAllLoans(Model model) {
        return "redirect:/employee/loans";
    }

    @PostMapping(value = "/employee/adduser")
    public String addNewUser(Model model) { return "redirect:/employee/add/user";}

    @PostMapping(value = "/employee/addaccount")
    public String addNewAccount(Model model) { return "redirect:/employee/add/account";}

}
