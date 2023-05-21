package webapp.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import webapp.bank.model.historia.Historia;
import webapp.bank.model.historia.HistoriaDao;
import webapp.bank.model.historia_kredyt.Historia_kredyt;
import webapp.bank.model.historia_kredyt.Historia_kredytDao;
import webapp.bank.model.kredyt.Kredyt;
import webapp.bank.model.kredyt.KredytDao;
import webapp.bank.model.uzytkownik.Uzytkownik;
import webapp.services.AccountService;
import webapp.services.EmailService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class LoanRepository implements ILoanRepository{

    private final DataSource dataSource;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    public LoanRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Kredyt getLoan(long id) {
        try (Connection connection = dataSource.getConnection()) {
            KredytDao kd = new KredytDao();
            return kd.getKredyt((int)id, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void addLoan(Kredyt loan) {
        try (Connection connection = dataSource.getConnection()) {
            KredytDao kd = new KredytDao();
            kd.addKredyt(loan, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeStatus(int id, String status) {
        try (Connection connection = dataSource.getConnection()) {
            KredytDao kd = new KredytDao();
            kd.changeStatus(id, status, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changePercentage(int id, int procent) {
        try (Connection connection = dataSource.getConnection()) {
            KredytDao kd = new KredytDao();
            kd.zmienOprocentowanie(id, procent, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeStatus(int id, boolean accepted) {
        try (Connection connection = dataSource.getConnection()) {
            KredytDao kd = new KredytDao();
            kd.decyzjaKredyt(id, accepted, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Kredyt> getUserLoan(long id) {
        try (Connection connection = dataSource.getConnection()) {
            KredytDao kd = new KredytDao();
            List<Kredyt> l = kd.kredytyKlienta(id, connection);
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Kredyt> getLoanRequests() {
        try (Connection connection = dataSource.getConnection()) {
            KredytDao kd = new KredytDao();
            List<Kredyt> l = kd.wnioskiOKredyt(connection);
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Kredyt> getAll() {
        try (Connection connection = dataSource.getConnection()) {
            KredytDao kd = new KredytDao();
            List<Kredyt> l = kd.getAll(connection);
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Historia_kredyt> getLoanHistory(long num) {
        try (Connection connection = dataSource.getConnection()) {
            Historia_kredytDao kd = new Historia_kredytDao();
            List<Historia_kredyt> l = kd.getHistoriaKredyt(num, connection);
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String stanAktualnejRaty(long num) {
        try (Connection connection = dataSource.getConnection()) {
            Historia_kredytDao hd = new Historia_kredytDao();
            Historia_kredyt latest = hd.getLastHistoriaById(num, connection);
            Kredyt k = this.getLoan(num);
            if (Objects.equals(k.getStan(), "splacony")) return "kredytsplacony";
            Date terminSplaty = k.getData();
            Date teraz = java.sql.Date.valueOf(LocalDate.now());
            if (teraz.after(terminSplaty)) return "spozniony";
            else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(terminSplaty);
                calendar.add(Calendar.DATE, -k.getOkres()); // subtract days
                Date poprzedniTermin = calendar.getTime();
                if (latest == null) {
                    return "rataoczekuje";
                }
                if (latest.getData().after(poprzedniTermin) || latest.getData().equals(poprzedniTermin)) return "rataoplacona";
                else return "rataoczekuje";
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateLoan(long num, long installment) {
        try (Connection connection = dataSource.getConnection()) {
            Historia_kredyt h = new Historia_kredyt(0, (int) num, "Wplata", (int) installment, java.sql.Date.valueOf(LocalDate.now()));
            Historia_kredytDao hd = new Historia_kredytDao();
            hd.addHistoriaKredyt(h, connection);

            KredytDao kd = new KredytDao();
            kd.dodajSplate((int) num, (int) installment, connection);

            HistoriaDao hist = new HistoriaDao();
            hist.addHistoria(new Historia(0, kd.getKredyt((int) num, connection).getNr_konta(), "Wyplata",
                    (int) installment, java.sql.Date.valueOf(LocalDate.now())), connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(long num, Kredyt k) {
        try (Connection connection = dataSource.getConnection()) {

            KredytDao kd = new KredytDao();
            kd.updateKredyt((int) num, k, connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAllDates() {
        try (Connection connection = dataSource.getConnection()) {
            KredytDao kd = new KredytDao();
            List<Kredyt> l = kd.getAll(connection);
            for (Kredyt k : l) {
                System.out.println("Kredyt przetwarzany przez scheduler");
                if (Objects.equals(k.getData(), java.sql.Date.valueOf(LocalDate.now())) && k.getStan() != "odrzucony" && k.getStan() != "nierozpatrzony" && k.getStan() != "splacony") {
                    String stan = this.stanAktualnejRaty(k.getId());
                    if (stan.equals("spozniony") || stan.equals("rataoczekuje")) {
                        long staraRata = k.getKolejna_rata();
                        long nowaRata;
                        if (k.getRaty().equals("stale")) {
                            nowaRata = (long) (2 * staraRata + 0.1 * staraRata);
                        }
                        else {
                            nowaRata = (long) ((float)(k.getInstallment() + staraRata) * 1.1);
                        }
                        k.setKolejna_rata((int) nowaRata);
                    }
                    if (k.isCzy_zmienne()) {
                        Random r = new Random();
                        k.setOprocentowanie(r.nextInt(1, 20));
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(k.getData());
                    calendar.add(Calendar.DATE, k.getOkres()); // add days
                    Date nowyTermin = calendar.getTime();
                    k.setData(nowyTermin);
                    kd.updateKredyt(k.getId(), k, connection);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
