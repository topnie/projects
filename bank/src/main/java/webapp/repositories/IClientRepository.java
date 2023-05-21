package webapp.repositories;

import org.springframework.stereotype.Repository;
import webapp.bank.model.klient.Klient;
import webapp.bank.model.konto.Konto;
import webapp.bank.model.uzytkownik.Uzytkownik;

import java.util.List;

@Repository
public interface IClientRepository {
    Uzytkownik findById(long id);
    void save(Uzytkownik user, Klient klient);
    public Uzytkownik findLogin(long id, String password);
    void update(Uzytkownik user);
    public List<Konto> findAccounts(long id);

    Klient findKlientById(long id);

    List<String> getParameters();
}
