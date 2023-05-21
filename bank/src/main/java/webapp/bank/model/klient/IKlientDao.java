package webapp.bank.model.klient;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IKlientDao {

    void addKlient(Klient k, Connection con)  throws SQLException;

    void updateKlient(long id, Klient k, Connection con) throws SQLException;

    Klient getKlient(long id, Connection con) throws SQLException;

    List<String> getPola(Connection connection);
}
