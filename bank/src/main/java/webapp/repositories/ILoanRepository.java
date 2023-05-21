package webapp.repositories;

import org.springframework.stereotype.Repository;
import webapp.bank.model.historia_kredyt.Historia_kredyt;
import webapp.bank.model.kredyt.Kredyt;

import java.text.ParseException;
import java.util.List;

@Repository
public interface ILoanRepository {
    Kredyt getLoan(long id) throws ParseException;
    void addLoan(Kredyt loan);
    void changeStatus(int id, String status);
    void changePercentage(int id, int procent);
    void changeStatus(int id, boolean accepted);

    List<Kredyt> getUserLoan(long id);

    List<Kredyt> getLoanRequests();

    List<Kredyt> getAll();

    List<Historia_kredyt> getLoanHistory(long num);

    String stanAktualnejRaty(long num);

    void updateLoan(long num, long installment);

    void update(long num, Kredyt k);
}
