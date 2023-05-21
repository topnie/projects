package webapp.bank.model.wniosek;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IWniosekZmianaDao {

    List<WniosekZmiana> getAll(Connection con) throws SQLException;

    void addWniosek(WniosekZmiana w, Connection con) throws SQLException;

    void deleteWniosek(long id, Connection con) throws SQLException;

    WniosekZmiana getWniosek(long id, Connection con) throws SQLException;

    long getLastWniosek(Connection con) throws SQLException;
}
