package webapp.bank.model.historia;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IHistoriaDao {

    void addHistoria(Historia h, Connection con)  throws SQLException;

    List<Historia> getKontoHistoria(long id, Connection con) throws SQLException;

    long getLastHistoria(Connection con) throws SQLException;
}
