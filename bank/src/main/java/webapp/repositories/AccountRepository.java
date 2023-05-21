package webapp.repositories;

import org.springframework.stereotype.Repository;
import webapp.bank.model.historia.Historia;
import webapp.bank.model.historia.HistoriaDao;
import webapp.bank.model.konto.Konto;
import webapp.bank.model.konto.KontoDao;
import webapp.bank.model.pracownik.Pracownik;
import webapp.bank.model.pracownik.PracownikDao;
import webapp.bank.model.uzytkownik.Uzytkownik;
import webapp.bank.model.uzytkownik.UzytkownikDao;
import webapp.bank.model.wlasnosc.WlasnoscDao;
import webapp.bank.utils.Waluta;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class AccountRepository implements IAccountRepository{

    private final DataSource dataSource;

    public AccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isThisUserAnOwner(long user_id, long account_number) {
        try (Connection connection = dataSource.getConnection()) {
            WlasnoscDao ud = new WlasnoscDao();
            List<Uzytkownik> l = ud.getUzytkownikFromKonto(account_number, connection);
            for (Uzytkownik u : l) {
                if (u.getId() == user_id) return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isThisUserAnEmployee(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PracownikDao ud = new PracownikDao();
            List<Pracownik> l = ud.getAll(connection);
            for (Pracownik u : l) {
                if (u.getId() == id) return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Konto findKontoById(long id) {
        try (Connection connection = dataSource.getConnection()) {
            KontoDao ud = new KontoDao();
            return ud.getKonto(id, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Historia> getAccountHistory(long num) {
        try (Connection connection = dataSource.getConnection()) {
            HistoriaDao ud = new HistoriaDao();
            List<Historia> l = ud.getKontoHistoria(num, connection);
            return l;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean doesThisAccountExist(long num) {
        try (Connection connection = dataSource.getConnection()) {
            KontoDao ud = new KontoDao();
            Konto k = ud.getKonto(num, connection);
            if (k == null) return false;
            else return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean doesThisAccountHaveEnough(Long num, long amount) {
        try (Connection connection = dataSource.getConnection()) {
            KontoDao ud = new KontoDao();
            Konto k = ud.getKonto(num, connection);
            if (k == null) return false;
            else {
                if (k.getStan() >= amount) return true;
                else return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void sendTransfer(Long num, long receiver, long amount) {
        try (Connection connection = dataSource.getConnection()) {
            KontoDao ud = new KontoDao();
            Konto send = ud.getKonto(num, connection);
            Konto receive = ud.getKonto(receiver, connection);
            Waluta w = new Waluta();
            long amount2 = w.convertWaluta(send.getWaluta(), receive.getWaluta(), amount);
            ud.changeStan(send.getNumer(), (int) -amount, connection);
            ud.changeStan(receive.getNumer(), (int) amount2, connection);
            HistoriaDao h = new HistoriaDao();
            h.addHistoria(new Historia(0, send.getNumer(), "Wyplata", (int) amount, java.sql.Date.valueOf(LocalDate.now())), connection);
            h.addHistoria(new Historia(0, receive.getNumer(), "Wplata", (int) amount2, java.sql.Date.valueOf(LocalDate.now())), connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<Uzytkownik> getAccountOwners(long num) {
        try (Connection connection = dataSource.getConnection()) {
            WlasnoscDao ud = new WlasnoscDao();
            return ud.getUzytkownikFromKonto(num, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void addAccount(Konto k, List<Integer> id_wlascicieli) {
        try (Connection connection = dataSource.getConnection()) {
            KontoDao ud = new KontoDao();
            ud.addKonto(k, id_wlascicieli, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void pay(int nrKonta, int installment) {
        try (Connection connection = dataSource.getConnection()) {
            KontoDao ud = new KontoDao();
            ud.changeStan(nrKonta, -installment, connection);
            HistoriaDao h = new HistoriaDao();
            h.addHistoria(new Historia(0, nrKonta, "Wyplata", installment, java.sql.Date.valueOf(LocalDate.now())), connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendLoan(long id, int kwota_pozyczki) {
        try (Connection connection = dataSource.getConnection()) {
            KontoDao ud = new KontoDao();
            ud.changeStan(id, kwota_pozyczki, connection);
            HistoriaDao h = new HistoriaDao();
            h.addHistoria(new Historia(0, (int) id, "Wplata", (int) kwota_pozyczki, java.sql.Date.valueOf(LocalDate.now())), connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}