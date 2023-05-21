package webapp.repositories;

import org.springframework.stereotype.Repository;
import webapp.bank.model.klient.Klient;
import webapp.bank.model.klient.KlientDao;
import webapp.bank.model.uzytkownik.Uzytkownik;
import webapp.bank.model.uzytkownik.UzytkownikDao;
import webapp.bank.model.wniosek.WniosekZmiana;
import webapp.bank.model.wniosek.WniosekZmianaDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
public class ChangeInfoRequestRepository implements IChangeInfoRequestRepository {

    private final DataSource dataSource;

    public ChangeInfoRequestRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<WniosekZmiana> getAll() {
        try (Connection connection = dataSource.getConnection()) {
            WniosekZmianaDao ud = new WniosekZmianaDao();
            return ud.getAll(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void addWniosek(WniosekZmiana w) {
        try (Connection connection = dataSource.getConnection()) {
            WniosekZmianaDao ud = new WniosekZmianaDao();
            ud.addWniosek(w, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteWniosek(long w_id) {
        try (Connection connection = dataSource.getConnection()) {
            WniosekZmianaDao ud = new WniosekZmianaDao();
            ud.deleteWniosek(w_id, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getId() {
        try (Connection connection = dataSource.getConnection()) {
            WniosekZmianaDao ud = new WniosekZmianaDao();
            long wniosek = ud.getLastWniosek(connection);
            return wniosek+1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void acceptWniosek(long id) {
        try (Connection connection = dataSource.getConnection()) {
            WniosekZmianaDao ud = new WniosekZmianaDao();
            WniosekZmiana wniosek = ud.getWniosek(id, connection);
            if (Objects.equals(wniosek.getNazwa_pola(), "adres")) {
                KlientDao kd = new KlientDao();
                Klient k = kd.getKlient(wniosek.getId_klient(), connection);
                k.setAdres(wniosek.getNowa_wartosc());
                kd.updateKlient(wniosek.getId_klient(), k, connection);
            }
            else if (Objects.equals(wniosek.getNazwa_pola(), "numer_tel")) {
                KlientDao kd = new KlientDao();
                Klient k = kd.getKlient(wniosek.getId_klient(), connection);
                k.setNumer_telefonu(wniosek.getNowa_wartosc());
                kd.updateKlient(wniosek.getId_klient(), k, connection);
            }
            else if (Objects.equals(wniosek.getNazwa_pola(), "imie")) {
                UzytkownikDao kd = new UzytkownikDao();
                Uzytkownik k = kd.getUser(wniosek.getId_klient(), connection);
                k.setImie(wniosek.getNowa_wartosc());
                kd.updateUser(wniosek.getId_klient(), k, connection);
            }
            else if (Objects.equals(wniosek.getNazwa_pola(), "nazwisko")) {
                UzytkownikDao kd = new UzytkownikDao();
                Uzytkownik k = kd.getUser(wniosek.getId_klient(), connection);
                k.setNazwisko(wniosek.getNowa_wartosc());
                kd.updateUser(wniosek.getId_klient(), k, connection);
            }
            else if (Objects.equals(wniosek.getNazwa_pola(), "haslo")) {
                UzytkownikDao kd = new UzytkownikDao();
                Uzytkownik k = kd.getUser(wniosek.getId_klient(), connection);
                k.setHaslo(wniosek.getNowa_wartosc());
                kd.updateUser(wniosek.getId_klient(), k, connection);
            }
            else if (Objects.equals(wniosek.getNazwa_pola(), "mail")) {
                UzytkownikDao kd = new UzytkownikDao();
                Uzytkownik k = kd.getUser(wniosek.getId_klient(), connection);
                k.setHaslo(wniosek.getNowa_wartosc());
                kd.updateUser(wniosek.getId_klient(), k, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
