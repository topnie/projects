package webapp.repositories;

import org.springframework.stereotype.Repository;
import webapp.bank.model.konto.Konto;
import webapp.bank.model.konto.KontoDao;
import webapp.bank.model.pracownik.Pracownik;
import webapp.bank.model.pracownik.PracownikDao;
import webapp.bank.model.uzytkownik.Uzytkownik;
import webapp.bank.model.uzytkownik.UzytkownikDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmployeeRepository {
    private final DataSource dataSource;
    public EmployeeRepository(DataSource dataSource) {
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

    public Pracownik findPracownikById(long id) {
        try (Connection connection = dataSource.getConnection()) {
            PracownikDao ud = new PracownikDao();
            return ud.getPracownik(id, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uzytkownik findLogin(long id, String password) {
        try (Connection connection = dataSource.getConnection()) {
            UzytkownikDao ud = new UzytkownikDao();
            return ud.loginEmployee(((Number) id).intValue(), password, connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void save(Uzytkownik user) {
        try (Connection connection = dataSource.getConnection()) {
            UzytkownikDao ud = new UzytkownikDao();
            ud.addUser(user, connection);
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

    public List<Konto> getAllAccounts() {
        try (Connection connection = dataSource.getConnection()) {
            KontoDao ud = new KontoDao();
            return ud.getAll(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Pracownik> getAllEmployees() {
        try (Connection connection = dataSource.getConnection()) {
            PracownikDao ud = new PracownikDao();
            return ud.getAll(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
