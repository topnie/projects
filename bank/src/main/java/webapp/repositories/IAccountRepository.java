package webapp.repositories;

import org.springframework.stereotype.Repository;
import webapp.bank.model.historia.Historia;
import webapp.bank.model.konto.Konto;
import webapp.bank.model.uzytkownik.Uzytkownik;

import java.util.List;

@Repository
public interface IAccountRepository {
    boolean isThisUserAnOwner(long user_id, long account_number);
    boolean isThisUserAnEmployee(Long id);

    List<Historia> getAccountHistory(long num);

    boolean doesThisAccountExist(long num);

    boolean doesThisAccountHaveEnough(Long num, long amount);

    void sendTransfer(Long num, long receiver, long amount);

    List<Uzytkownik> getAccountOwners(long num);

    void addAccount(Konto k, List<Integer> id_wlascicieli);
}
