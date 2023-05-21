package webapp.repositories;

import webapp.bank.model.konto.Konto;
import webapp.bank.model.pracownik.Pracownik;
import webapp.bank.model.uzytkownik.Uzytkownik;

import java.util.List;

public interface IEmployeeRepository {
    Uzytkownik findById(long id);
    Pracownik findPracownikById(long id);
    void save(Uzytkownik user);
    public Uzytkownik findLogin(long id, String password);
    void update(Uzytkownik user);
    List<Pracownik> getAllEmployees();
    List<Konto> getAllAcounts();
}
