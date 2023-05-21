package webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import webapp.bank.model.kredyt.Kredyt;
import webapp.bank.model.wniosek.WniosekZmiana;
import webapp.services.AccountService;
import webapp.services.ChangeInfoRequestService;
import webapp.services.ClientService;
import webapp.services.EmployeeService;

import java.time.LocalDate;

@Controller
@SessionAttributes({"id"})
public class DataRequestController {

    @Autowired
    private ClientService userService;

    @Autowired
    private ChangeInfoRequestService changeInfoRequestService;

    @RequestMapping("/client/data")
    public String loanRequest(Model model) {
        if (model.getAttribute("id") == null) {
            return "error-access-denied";
        }
        else {
            model.addAttribute("options", userService.getUserParameters());
            return "data";
        }
    }

    @PostMapping("/client/data/request")
    public String sendRequest(@RequestParam("element") String element,
                              @RequestParam("new_value") String new_value, Model model) {
        boolean success = true;
        long id = changeInfoRequestService.getNewId();
        changeInfoRequestService.addWniosek(new WniosekZmiana((Long) model.getAttribute("id"), element, new_value, id));
        return "loan-request-sent";
    }
}
