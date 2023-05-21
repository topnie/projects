package webapp.bank.model.historia_kredyt;

import webapp.bank.model.historia.Historia;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public interface IHistoria_kredytDao {
    void addHistoriaKredyt(Historia_kredyt h, Connection con)  throws SQLException;

    List<Historia_kredyt> getHistoriaKredyt(long id, Connection con) throws SQLException;

    long getLastHistoriaKredyt(Connection con) throws SQLException;

    Historia_kredyt getLastHistoriaById(long num, Connection con) throws SQLException, ParseException;
}
