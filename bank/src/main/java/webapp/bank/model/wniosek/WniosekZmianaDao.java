package webapp.bank.model.wniosek;

import webapp.bank.model.historia.Historia;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WniosekZmianaDao implements IWniosekZmianaDao{
    @Override
    public List<WniosekZmiana> getAll(Connection con) throws SQLException {
        List<WniosekZmiana> ans = new LinkedList<>();
        Statement stmt = con.createStatement();
        String query = "SELECT * FROM Wniosek_o_zmiane";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            long id_klienta = Long.parseLong(rs.getString(1));
            String nazwa_pola = rs.getString(2);
            String nowa_wart = rs.getString(3);
            long id = Long.parseLong(rs.getString(4));
            WniosekZmiana w = new WniosekZmiana(id_klienta, nazwa_pola, nowa_wart, id);
            ans.add(w);
        } return ans;
    }

    public long getLastWniosek(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "Select max(id) from Wniosek_o_zmiane";
        ResultSet rs = stmt.executeQuery(query);
        if(rs.next()) {
            if (rs.getString(1) == null) return 0L;
            return Long.parseLong(rs.getString(1));
        }
        else return 0L;

    }
    @Override
    public void addWniosek(WniosekZmiana w, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        long id_klient = w.getId_klient();
        String pole = w.getNazwa_pola();
        if (Objects.equals(pole, "imię")) {pole = "imie";}
        if (Objects.equals(pole, "hasło")) {pole = "haslo";}
        if (Objects.equals(pole, "numer telefonu")) {pole = "numer_tel";}
        String query = "INSERT INTO Wniosek_o_zmiane (id_klient, nazwa_pola, nowa_wartosc, id) VALUES (" + Long.toString(id_klient) + ", '" + pole + "', '" + w.getNowa_wartosc() + "', " + Long.toString(this.getLastWniosek(con) + 1) + ")";
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public void deleteWniosek(long id, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String idString = Long.toString(id);
        String query = "DELETE FROM Wniosek_o_zmiane WHERE id =" + idString;
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public WniosekZmiana getWniosek(long id, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String idString = Long.toString(id);
        String query = "SELECT * FROM Wniosek_o_zmiane WHERE id =" + idString;
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            long id_klienta = Long.parseLong(rs.getString(1));
            String nazwa_pola = rs.getString(2);
            String nowa_wart = rs.getString(3);
            long id_w = Long.parseLong(rs.getString(4));
            WniosekZmiana w = new WniosekZmiana(id_klienta, nazwa_pola, nowa_wart, id_w);
            return w;
        }
        return null;
    }
}
