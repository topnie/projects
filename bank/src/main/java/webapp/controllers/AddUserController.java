package webapp.controllers;

import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import webapp.bank.model.klient.Klient;
import webapp.bank.model.pracownik.Pracownik;
import webapp.bank.model.uzytkownik.Uzytkownik;
import webapp.bank.model.uzytkownik.UzytkownikDao;
import webapp.repositories.EmployeeRepository;
import webapp.services.ClientService;
import webapp.services.EmployeeService;

@Controller
@SessionAttributes("id")
public class AddUserController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ClientService clientService;

    @RequestMapping(value = "/employee/add/user")
    public String addForm(Model model) {
        Pracownik p = employeeService.findPracownikById((Long) model.getAttribute("id"));
        if (p == null) return "error-access-denied";
        else return "adduser";
    }

    @PostMapping(value = "/employee/add/user")
    public String addUser(@RequestParam("imie") String imie, @RequestParam("nazwisko") String nazwisko,
                          @RequestParam("adres") String adres, @RequestParam("nr_tel") String numer_tel,
                          @RequestParam("mail") String mail, @RequestParam("haslo") String haslo, Model model) {
        try {
            Long.parseLong(numer_tel);
        } catch(NumberFormatException e){
            model.addAttribute("error", "Niepoprawny numer telefonu");
            return "adduser";
        }
        Uzytkownik u = new Uzytkownik(imie, nazwisko, haslo, mail, 0);
        Klient k = new Klient(0, adres, numer_tel);
        clientService.register(u, k);
        model.addAttribute("nowy", k.getId());
        return "addeduser";
    }
}
