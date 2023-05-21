package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.bank.model.historia.Historia;
import webapp.bank.model.konto.Konto;
import webapp.bank.model.uzytkownik.Uzytkownik;
import webapp.repositories.AccountRepository;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public boolean isThisUserAnOwner(long user_id, long account_number) {
        return accountRepository.isThisUserAnOwner(user_id, account_number);
    }

    public boolean isThisUserAnEmployee(Long id) {
        return accountRepository.isThisUserAnEmployee(id);
    }

    public List<Historia> getAccountHistory(long num) {
        return accountRepository.getAccountHistory(num);
    }

    public boolean doesThisAccountExist(long num) { return accountRepository.doesThisAccountExist(num); }

    public boolean doesThisAccountHaveEnough(Long num, long amount) { return accountRepository.doesThisAccountHaveEnough(num, amount);
    }

    public void sendTransfer(Long num, long receiver, long amount) { accountRepository.sendTransfer(num, receiver, amount);
    }

    public List<Uzytkownik> getAccountOwners(long num) { return accountRepository.getAccountOwners(num);
    }

    public void addAccount(Konto k, List<Integer> id_w) { accountRepository.addAccount(k, id_w);}

    public void pay(int nrKonta, int installment) { accountRepository.pay(nrKonta, installment);
    }
    public void sendLoan(long id, int kwota_pozyczki) {
        accountRepository.sendLoan(id, kwota_pozyczki);
    }
}
