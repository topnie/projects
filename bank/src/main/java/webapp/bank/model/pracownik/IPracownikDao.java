package webapp.bank.model.pracownik;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IPracownikDao {
    void addPracownik(Pracownik p, Connection con)  throws SQLException;

    void updatePracownik(long id, Pracownik p, Connection con) throws SQLException;

    Pracownik getPracownik(long id, Connection con) throws SQLException;

    List<Pracownik> getAll(Connection connection) throws SQLException;
}
