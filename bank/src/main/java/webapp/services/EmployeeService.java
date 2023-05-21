package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.bank.model.konto.Konto;
import webapp.bank.model.pracownik.Pracownik;
import webapp.bank.model.pracownik.PracownikDao;
import webapp.bank.model.uzytkownik.Uzytkownik;
import webapp.repositories.EmployeeRepository;

import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public boolean login(long id, String password) {
        Uzytkownik user = employeeRepository.findLogin(id, password);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    public void register(Uzytkownik user) {
        employeeRepository.save(user);
    }
    public Uzytkownik findById(long id) {
        return employeeRepository.findById(id);
    }

    public Pracownik findPracownikById(long id) { return employeeRepository.findPracownikById(id); }

    public List<Konto> getAllAccounts() { return employeeRepository.getAllAccounts(); }

    public List<Pracownik> getAllEmployees() { return employeeRepository.getAllEmployees(); }
}
