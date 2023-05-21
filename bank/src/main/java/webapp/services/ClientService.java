package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.bank.model.klient.Klient;
import webapp.bank.model.konto.Konto;
import webapp.bank.model.uzytkownik.Uzytkownik;
import webapp.repositories.ClientRepository;

import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository userRepository;

    public boolean login(long id, String password) {
        Uzytkownik user = userRepository.findLogin(id, password);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    public void register(Uzytkownik user, Klient klient) {
        userRepository.save(user, klient);
    }
    public Uzytkownik findById(long id) {
        return userRepository.findById(id);
    }

    public List<Konto> getUserAccounts(long id) { return userRepository.findAccounts(id);}

    public List<String> getUserParameters() { return userRepository.getParameters();}

    public Klient findKlientById(long id) { return userRepository.findKlientById(id); }

}


