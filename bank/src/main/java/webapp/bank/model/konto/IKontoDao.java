package webapp.bank.model.konto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IKontoDao {

    void addKonto(Konto k, List<Integer> id_wlascicieli, Connection con) throws SQLException;

    void updateKonto(long id, Konto k, Connection con) throws SQLException;
    void changeStan(long id, int delta, Connection con) throws SQLException;

    Konto getKonto(long id, Connection con) throws SQLException;
}
