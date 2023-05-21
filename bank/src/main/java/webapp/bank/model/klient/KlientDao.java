package webapp.bank.model.klient;

import webapp.bank.model.uzytkownik.Uzytkownik;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class KlientDao implements IKlientDao {
    @Override
    public void addKlient(Klient k, Connection con)  throws SQLException {
        Statement stmt = con.createStatement();
        String query = "INSERT INTO Klient (id, adres, numer_tel) VALUES ('" + Integer.toString(k.getId()) + "', '" +
                k.getAdres() + "', '" + k.getNumer_telefonu() + "')";
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public void updateKlient(long id, Klient k, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "UPDATE Klient SET adres = '" +
                k.getAdres() + "', numer_tel= '" + k.getNumer_telefonu() + "' WHERE id = " + Long.toString(id);
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public Klient getKlient(long id, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String idString = Long.toString(id);
        String query = "Select * from Klient where id = " + idString;
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            Klient k = new Klient(Integer.parseInt(rs.getString(1)), rs.getString(2), rs.getString(3) );
            return k;
        } else return null;
    }

    @Override
    public List<String> getPola(Connection connection) {
        List<String> ans = new LinkedList<>();
        ans.add("imię");
        ans.add("nazwisko");
        ans.add("hasło");
        ans.add("mail");
        ans.add("adres");
        ans.add("numer telefonu");
        return ans;
    }
}
