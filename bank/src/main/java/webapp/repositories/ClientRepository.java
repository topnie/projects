package webapp.repositories;

import org.springframework.stereotype.Repository;
import webapp.bank.model.klient.Klient;
import webapp.bank.model.klient.KlientDao;
import webapp.bank.model.konto.Konto;
import webapp.bank.model.uzytkownik.Uzytkownik;
import webapp.bank.model.uzytkownik.UzytkownikDao;
import webapp.bank.model.wlasnosc.WlasnoscDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ClientRepository implements IClientRepository {
    private final DataSource dataSource;
    public ClientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public Uzytkownik findById(long id) {
        try (Connection connection = dataSource.getConnection()) {
            UzytkownikDao ud = new UzytkownikDao();
            return ud.getUser(id, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uzytkownik findLogin(long id, String password) {
        try (Connection connection = dataSource.getConnection()) {
            UzytkownikDao ud = new UzytkownikDao();
            return ud.loginUser(((Number) id).intValue(), password, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Klient findKlientById(long id) {
        try (Connection connection = dataSource.getConnection()) {
            KlientDao ud = new KlientDao();
            return ud.getKlient(id, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void save(Uzytkownik user, Klient klient) {
        try (Connection connection = dataSource.getConnection()) {
            UzytkownikDao ud = new UzytkownikDao();
            long id = ud.addUser(user, connection);
            KlientDao kd = new KlientDao();
            klient.setId((int) id);
            kd.addKlient(klient, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void update(Uzytkownik user) {
        try (Connection connection = dataSource.getConnection()) {
            UzytkownikDao ud = new UzytkownikDao();
            ud.updateUser(user.getId(), user, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Konto> findAccounts(long id) {
        try (Connection connection = dataSource.getConnection()) {
            WlasnoscDao ud = new WlasnoscDao();
            return ud.getKontoFromUzytkownik(id, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getParameters() {
        try (Connection connection = dataSource.getConnection()) {
            KlientDao ud = new KlientDao();
            return ud.getPola(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
