package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.bank.model.historia_kredyt.Historia_kredyt;
import webapp.bank.model.kredyt.Kredyt;
import webapp.repositories.LoanRepository;

import java.text.ParseException;
import java.util.List;

@Service
public class LoanService {
    @Autowired
    private LoanRepository loanRepository;

    public Kredyt getLoan(long id) {
        return  loanRepository.getLoan(id);
    }

    public void addLoan(Kredyt loan) {
        loanRepository.addLoan(loan);
    }

    public void changeLoanStatus(int id, String status) {
        loanRepository.changeStatus(id, status);
    }

    public void changeLoanPersentage(int id, int procent) {
        loanRepository.changePercentage(id, procent);
    }

    public void changeLoanStatus(int id, boolean accepted) {
        loanRepository.changeStatus(id, accepted);
    }

    public List<Kredyt> getUserLoans(long id) {
        return loanRepository.getUserLoan(id);
    }

    public List<Kredyt> getLoanRequests() { return loanRepository.getLoanRequests(); }

    public List<Kredyt> getAll() { return loanRepository.getAll(); }

    public List<Historia_kredyt> getLoanHistory(long num) {
        return  loanRepository.getLoanHistory(num);
    }

    public String stanAktualnejRaty(long num) { return loanRepository.stanAktualnejRaty(num);}

    public void updateLoan(long num, long installment) { loanRepository.updateLoan(num, installment);
    }

    public void update(int id, Kredyt k) { loanRepository.update(id, k);
    }
}
