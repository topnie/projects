package webapp.bank.model.konto;

import webapp.bank.model.historia.Historia;
import webapp.bank.model.historia.HistoriaDao;
import webapp.bank.model.wlasnosc.Wlasnosc;
import webapp.bank.model.wlasnosc.WlasnoscDao;

import java.util.LinkedList;
import java.util.List;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class KontoDao implements IKontoDao{
    @Override
    public void addKonto(Konto k, List<Integer> id_wlascicieli, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        long konto_id = this.getLastKonto(con) + 1;
        String query = "INSERT INTO Konto (numer, waluta, stan) VALUES (" + Long.toString(konto_id) + ", '" +
                k.getWaluta() + "', " + Integer.toString(k.getStan()) + ")";
        ResultSet rs = stmt.executeQuery(query);
        WlasnoscDao w = new WlasnoscDao();
        for (Integer id : id_wlascicieli) {
            w.addWlasnosc(new Wlasnosc(id, (int) konto_id), con);
        }
    }

    @Override
    public void updateKonto(long id, Konto k, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "UPDATE Konto SET waluta = '" +
                k.getWaluta() + "', stan= " + Integer.toString(k.getStan()) + " WHERE numer = " + Long.toString(id);
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public void changeStan(long id, int delta, Connection con) throws SQLException {
        Konto k = this.getKonto(id, con);
        k.setStan(k.getStan() + delta);
        HistoriaDao h = new HistoriaDao();
        this.updateKonto(id, k, con);
    }

    @Override
    public Konto getKonto(long id, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String idString = Long.toString(id);
        String query = "Select * from Konto where numer = " + idString;
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            Konto k = new Konto(Integer.parseInt(rs.getString(1)), rs.getString(2), Integer.parseInt(rs.getString(3)));
            return k;
        } else return null;
    }

    public List<Konto> getAll(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "Select * from Konto";
        ResultSet rs = stmt.executeQuery(query);
        List<Konto> l = new LinkedList<>();
        while (rs.next()) {
            Konto k = new Konto(Integer.parseInt(rs.getString(1)), rs.getString(2), Integer.parseInt(rs.getString(3)));
            l.add(k);
        }
        return l;
    }

    public long getLastKonto(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "Select max(numer) from konto";
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            if (rs.getString(1) == null) return 0L;
            return Long.parseLong(rs.getString(1));
        }
        else return 0L;
    }
}
