package webapp.bank.model.uzytkownik;

import java.sql.Connection;
import java.sql.SQLException;

public interface IUzytkownikDao {

    long addUser(Uzytkownik uzytkownik, Connection con)  throws SQLException;

    void updateUser(long id, Uzytkownik uzytkownik, Connection con) throws SQLException;

    Uzytkownik getUser(long id, Connection con) throws SQLException;

    Uzytkownik loginUser(int id, String password, Connection con) throws SQLException;

    Uzytkownik loginEmployee(int id, String password, Connection con) throws SQLException;

}