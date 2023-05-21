package webapp.bank.model.kredyt;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public interface IKredytDao {

    Kredyt getKredyt(int id, Connection con) throws SQLException, ParseException;

    void addKredyt(Kredyt k, Connection con) throws SQLException;

    void changeStatus(int id, String status, Connection con) throws SQLException;

    void dodajSplate(int id, int splata, Connection con) throws SQLException, ParseException;
    void zmienOprocentowanie(int id, int procent, Connection con) throws SQLException, ParseException;

    void decyzjaKredyt(int id, boolean zatwierdzony, Connection con) throws SQLException;

    List<Kredyt> kredytyKlienta(long id, Connection connection) throws SQLException, ParseException;

    List<Kredyt> wnioskiOKredyt(Connection con) throws SQLException, ParseException;

    List<Kredyt> getAll(Connection con) throws SQLException, ParseException;
}
