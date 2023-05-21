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
import webapp.bank.model.wniosek.WniosekZmiana;
import webapp.services.AccountService;
import webapp.services.ChangeInfoRequestService;
import webapp.services.ClientService;
import webapp.services.EmployeeService;

import java.util.List;
import java.util.Objects;

@Controller
@SessionAttributes("id")
public class ChangeInfoEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ChangeInfoRequestService changeInfoRequestService;

    @RequestMapping(value = "employee/changeinfo")
    public String changeInfoRequest(Model model) {
        List<WniosekZmiana> requests = changeInfoRequestService.getAlL();
        model.addAttribute("requests", requests);
        return "changeInfo";
    }

    @PostMapping(value = "/employee/data/accept")
    public String acceptChangeRequest(@RequestParam("nr_wniosku") long id, Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null || !Objects.equals(p.getDzial(), "Obslugi")) return "error-access-denied";
        changeInfoRequestService.acceptWniosek(id);
        changeInfoRequestService.deleteWniosek(id);
        return "redirect:/employee/changeinfo";
    }

    @PostMapping(value = "/employee/data/decline")
    public String declineChangeRequest(@RequestParam("nr_wniosku") long id, Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null || !Objects.equals(p.getDzial(), "Obslugi")) return "error-access-denied";
        changeInfoRequestService.deleteWniosek(id);
        return "redirect:/employee/changeinfo";
    }
}
