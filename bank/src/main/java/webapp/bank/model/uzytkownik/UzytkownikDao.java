package webapp.bank.model.uzytkownik;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UzytkownikDao implements IUzytkownikDao {
    @Override
    public long addUser(Uzytkownik uzytkownik, Connection con)  throws SQLException {
        Statement stmt = con.createStatement();
        long id = this.getLastUser(con) + 1;
        String query = "INSERT INTO Uzytkownik (imie, nazwisko, haslo, mail, id) VALUES ('" + uzytkownik.getImie() + "', '" +
                uzytkownik.getNazwisko() + "', '" + uzytkownik.getHaslo() + "', '" + uzytkownik.getMail() + "', " + Long.toString(id) + ")";
        ResultSet rs = stmt.executeQuery(query);
        return id;
    }

    public long getLastUser(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "Select max(id) from uzytkownik";
        ResultSet rs = stmt.executeQuery(query);
        if(rs.next()) {
            if (rs.getString(1) == null) return 0L;
            return Long.parseLong(rs.getString(1));
        }
        else return 0L;
    }

    @Override
    public void updateUser(long id, Uzytkownik uzytkownik, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "UPDATE Uzytkownik SET imie = '" +
                uzytkownik.getImie() + "', nazwisko= '" + uzytkownik.getNazwisko() + "', haslo = '" +
                uzytkownik.getHaslo() + "', mail = '" + uzytkownik.getMail() + "' WHERE id = " + Long.toString(id);
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public Uzytkownik getUser(long id, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String idString = Long.toString(id);
        String query = "Select * from uzytkownik where id = " + idString;
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            Uzytkownik uzytkownik = new Uzytkownik(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), Integer.parseInt(rs.getString(5)));
            return uzytkownik;
        } else return null;
    }

    @Override
    public Uzytkownik loginUser(int id, String password, Connection con) throws SQLException {
        Statement stmt1 = con.createStatement();
        String idString = Long.toString(id);
        ResultSet checkIfUser = stmt1.executeQuery("Select * from klient where id = " + idString);
        if (!checkIfUser.next()) {
            return null;
        }
        Statement stmt = con.createStatement();
        String query = "Select * from uzytkownik where id = " + idString + " and haslo = '" + password +"'";
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            Uzytkownik uzytkownik = new Uzytkownik(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), Integer.parseInt(rs.getString(5)));
            return uzytkownik;
        }
        else return null;
    }

    public Uzytkownik loginEmployee(int id, String password, Connection con) throws SQLException {
        Statement stmt1 = con.createStatement();
        String idString = Long.toString(id);
        ResultSet checkIfUser = stmt1.executeQuery("Select * from Pracownik where id = " + idString);
        if (!checkIfUser.next()) return null;
        Statement stmt = con.createStatement();
        String query = "Select * from uzytkownik where id = " + idString + " and haslo = '" + password +"'";
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            Uzytkownik uzytkownik = new Uzytkownik(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), Integer.parseInt(rs.getString(5)));
            return uzytkownik;
        }
        else return null;
    }

}
