package webapp.bank.model.pracownik;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class PracownikDao implements IPracownikDao {

    @Override
    public void addPracownik(Pracownik p, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "INSERT INTO Pracownik (id, dzial) VALUES ('" + Integer.toString(p.getId()) + "', '" +
                p.getDzial() + "')";
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public void updatePracownik(long id, Pracownik p, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "UPDATE Pracownik SET dzial = '" +
                p.getDzial() + "' WHERE id = " + Long.toString(id);
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public Pracownik getPracownik(long id, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String idString = Long.toString(id);
        String query = "Select * from Pracownik where id = " + idString;
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            Pracownik p = new Pracownik(Integer.parseInt(rs.getString(1)), rs.getString(2));
            return p;
        } else return null;
    }

    public List<Pracownik> getAll(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        String query = "Select * from Pracownik";
        ResultSet rs = stmt.executeQuery(query);
        List<Pracownik> l = new LinkedList<>();
        while (rs.next()) {
            Pracownik p = new Pracownik(Integer.parseInt(rs.getString(1)), rs.getString(2));
            l.add(p);
        }
        return l;
    }
}
