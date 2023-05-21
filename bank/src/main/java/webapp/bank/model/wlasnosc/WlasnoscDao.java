package webapp.bank.model.wlasnosc;

import webapp.bank.model.konto.Konto;
import webapp.bank.model.uzytkownik.Uzytkownik;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class WlasnoscDao implements IWlasnoscDao {
    @Override
    public void addWlasnosc(Wlasnosc w, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "INSERT INTO Wlasnosc (id_wlasc, nr_konto) VALUES (" + Integer.toString(w.getId_wlasc()) + ", " +
                Integer.toString(w.getNr_konto()) + ")";
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public List<Konto> getKontoFromUzytkownik(long id_user, Connection con) throws SQLException {
        List<Konto> ans = new LinkedList<>();
        Statement stmt = con.createStatement();
        String idString = Long.toString(id_user);
        String query = "SELECT * FROM Konto JOIN wlasnosc ON konto.numer = wlasnosc.nr_konto JOIN uzytkownik ON wlasnosc.id_wlasc = uzytkownik.id " +
                "WHERE uzytkownik.id = " + idString;
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            Konto k = new Konto(Integer.parseInt(rs.getString(1)), rs.getString(2), Integer.parseInt(rs.getString(3)));
            ans.add(k);
        } return ans;
    }

    @Override
    public List<Uzytkownik> getUzytkownikFromKonto(long id_konto, Connection con) throws SQLException {
        List<Uzytkownik> ans = new LinkedList<>();
        Statement stmt = con.createStatement();
        String idString = Long.toString(id_konto);
        String query = "SELECT * FROM Uzytkownik JOIN wlasnosc ON uzytkownik.id = wlasnosc.id_wlasc JOIN konto ON wlasnosc.nr_konto = konto.numer " +
                "WHERE konto.numer = " + idString;
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            Uzytkownik k = new Uzytkownik(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), Integer.parseInt(rs.getString(5)));
            ans.add(k);
        } return ans;
    }
}
