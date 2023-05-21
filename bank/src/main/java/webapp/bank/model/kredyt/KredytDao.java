package webapp.bank.model.kredyt;

import webapp.bank.model.konto.Konto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class KredytDao implements IKredytDao{

    @Override
    public Kredyt getKredyt(int id, Connection con) throws SQLException, ParseException {
        Statement stmt = con.createStatement();
        String idString = Integer.toString(id);
        String query = "Select * from Kredyt where id = " + idString;
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            boolean czyZmienne = !rs.getString(9).equals("0");
            Kredyt k = new Kredyt(Integer.parseInt(rs.getString(1)),Integer.parseInt(rs.getString(2)),
                    Integer.parseInt(rs.getString(3)), Integer.parseInt(rs.getString(4)),
                    Integer.parseInt(rs.getString(5)), Integer.parseInt(rs.getString(6)),
                    Integer.parseInt(rs.getString(7)), formatter.parse(rs.getString(8)),
                    Boolean.parseBoolean(rs.getString(9)), rs.getString(10), rs.getString(11),
                    Integer.parseInt(rs.getString(12)), Integer.parseInt(rs.getString(13)));
            return k;
        } else return null;
    }
    @Override
    public void addKredyt(Kredyt k, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = formatter.format(k.getData());
        int czyzmienne = k.isCzy_zmienne() ? 1 : 0;
        String query = "INSERT INTO Kredyt (id, nr_konta, id_pracownik, kwota_pozyczki, kwota_splacona, oprocentowanie, okres, data, czy_zmienne, stan, raty, kolejna_rata, ile_rat_zaplaconych) VALUES ("
                + Long.toString(this.getLastKredyt(con) + 1) + ", " + Integer.toString(k.getNr_konta()) + ", " + Integer.toString(k.getId_pracownik()) + ", "
                + Integer.toString(k.getKwota_pozyczki()) + ", " +Integer.toString(k.getKwota_splacona()) + ", " + Integer.toString(k.getOprocentowanie()) + ", " + Integer.toString(k.getOkres())
                + ", TO_DATE('" + dataString + "', 'DD/MM/YYYY')" + ", " + Integer.toString(czyzmienne) + ", '" +k.getStan()+ "', '" + k.getRaty() + "'," + Integer.toString(k.getKolejna_rata()) + ", 0)";
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public void changeStatus(int id, String status, Connection con) throws SQLException {
        if ((!Objects.equals(status, "nierozpatrzony") && (!Objects.equals(status, "odrzucony"))) && (!Objects.equals(status, "splacony") && !Objects.equals(status, "w trakcie"))) {
            return;
        }
        try {
            Statement stmt = con.createStatement();
            String query = "UPDATE Kredyt SET stan= '" + status + "' WHERE id = " + Integer.toString(id);
            ResultSet rs = stmt.executeQuery(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateKredyt (int id, Kredyt k, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        int kwota_splacona = k.getKwota_splacona();
        int oprocentowanie = k.getOprocentowanie();
        int okres = k.getOkres();
        String stan = k.getStan();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = formatter.format(k.getData());
        int kolejna_rata = k.getKolejna_rata();
        int ile_rat_zaplaconych = k.getIle_rat_zaplaconych();
        String query = "UPDATE Kredyt SET kwota_splacona= " + Integer.toString(kwota_splacona) +
                ", oprocentowanie = " + Integer.toString(oprocentowanie) +
                ", okres = " + Integer.toString(okres) +
                ", stan = " + "'" + stan + "'" +
                ", data = TO_DATE('" + dataString + "', 'DD/MM/YYYY')" +
                ", kolejna_rata = " + Integer.toString(kolejna_rata) +
                ", ile_rat_zaplaconych = " + Integer.toString(ile_rat_zaplaconych) +
                " WHERE id = " + Integer.toString(id);
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public void dodajSplate(int id, int splata, Connection con) throws SQLException, ParseException {
        Kredyt k = this.getKredyt(id, con);
        int s = k.getKwota_pozyczki() / ((5 * 360) / k.getOkres());
        k.setKwota_splacona(k.getKwota_splacona() + s);
        k.setIle_rat_zaplaconych(k.getIle_rat_zaplaconych()+1);
        this.updateKredyt(id, k, con);
        if (k.getKwota_pozyczki() <= k.getKwota_splacona()) {
            this.changeStatus(id, "splacony", con);
        }
        else {
            k.setKolejna_rata((int) k.getInstallment());
            this.updateKredyt(id, k, con);
        }
    }

    @Override
    public void zmienOprocentowanie(int id, int procent, Connection con) throws SQLException, ParseException {
        Statement stmt = con.createStatement();
        String query = "UPDATE Kredyt SET oprocentowanie= " + Integer.toString(procent) + " WHERE numer = " + Integer.toString(id);
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public void decyzjaKredyt(int id, boolean zatwierdzony, Connection con) throws SQLException {
        if (zatwierdzony) {
            this.changeStatus(id, "w trakcie", con);
        }
        else {
            this.changeStatus(id, "odrzucony", con);
        }
    }

    @Override
    public List<Kredyt> kredytyKlienta(long id_user, Connection con) throws SQLException, ParseException {
        List<Kredyt> ans = new LinkedList<>();
        Statement stmt = con.createStatement();
        String idString = Long.toString(id_user);
        String query = "SELECT * FROM Kredyt JOIN Konto ON konto.numer = kredyt.nr_konta JOIN wlasnosc ON wlasnosc.nr_konto = konto.numer WHERE id_wlasc = " + idString;
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            int id = Integer.parseInt(rs.getString(1));
            int nr_konta = Integer.parseInt(rs.getString(2));
            int id_pracownik = Integer.parseInt(rs.getString(3));
            int kwota_pozyczki = Integer.parseInt(rs.getString(4));
            int kwota_splacona = Integer.parseInt(rs.getString(5));
            int oprocentowanie = Integer.parseInt(rs.getString(6));
            int okres = Integer.parseInt(rs.getString(7));
            Date data = formatter.parse(rs.getString(8));
            boolean czy_zmienne = !rs.getString(9).equals("0");
            String stan = rs.getString(10);
            String raty = rs.getString(11);
            int kolejna_rata = Integer.parseInt(rs.getString(12));
            int ile_rat_zaplaconych = Integer.parseInt(rs.getString(13));
            Kredyt k = new Kredyt(id, nr_konta, id_pracownik, kwota_pozyczki, kwota_splacona, oprocentowanie, okres,
                    data, czy_zmienne, stan, raty, kolejna_rata, ile_rat_zaplaconych);
            ans.add(k);
        } return ans;
    }

    public long getLastKredyt(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "Select max(id) from kredyt";
        ResultSet rs = stmt.executeQuery(query);
        if(rs.next()) {
            if (rs.getString(1) == null) return 0L;
            return Long.parseLong(rs.getString(1));
        }
        else return 0L;
    }

    public List<Kredyt> wnioskiOKredyt(Connection con) throws SQLException, ParseException {
        List<Kredyt> ans = new LinkedList<>();
        Statement stmt = con.createStatement();
        String query = "SELECT * FROM Kredyt WHERE stan = 'nierozpatrzony'";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            int id = Integer.parseInt(rs.getString(1));
            int nr_konta = Integer.parseInt(rs.getString(2));
            int id_pracownik = Integer.parseInt(rs.getString(3));
            int kwota_pozyczki = Integer.parseInt(rs.getString(4));
            int kwota_splacona = Integer.parseInt(rs.getString(5));
            int oprocentowanie = Integer.parseInt(rs.getString(6));
            int okres = Integer.parseInt(rs.getString(7));
            Date data = formatter.parse(rs.getString(8));
            boolean czy_zmienne = !rs.getString(9).equals("0");
            String stan = rs.getString(10);
            String raty = rs.getString(11);
            int kolejna_rata = Integer.parseInt(rs.getString(12));
            int ile_rat_zaplaconych = Integer.parseInt(rs.getString(13));
            Kredyt k = new Kredyt(id, nr_konta, id_pracownik, kwota_pozyczki, kwota_splacona, oprocentowanie, okres,
                    data, czy_zmienne, stan, raty, kolejna_rata, ile_rat_zaplaconych);
            ans.add(k);
        } return ans;
    }

    @Override
    public List<Kredyt> getAll(Connection con) throws SQLException, ParseException {
        List<Kredyt> ans = new LinkedList<>();
        Statement stmt = con.createStatement();
        String query = "SELECT * FROM Kredyt";
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            int id = Integer.parseInt(rs.getString(1));
            int nr_konta = Integer.parseInt(rs.getString(2));
            int id_pracownik = Integer.parseInt(rs.getString(3));
            int kwota_pozyczki = Integer.parseInt(rs.getString(4));
            int kwota_splacona = Integer.parseInt(rs.getString(5));
            int oprocentowanie = Integer.parseInt(rs.getString(6));
            int okres = Integer.parseInt(rs.getString(7));
            Date data = formatter.parse(rs.getString(8));
            boolean czy_zmienne = !rs.getString(9).equals("0");
            String stan = rs.getString(10);
            String raty = rs.getString(11);
            int kolejna_rata = Integer.parseInt(rs.getString(12));
            int ile_rat_zaplaconych = Integer.parseInt(rs.getString(13));
            Kredyt k = new Kredyt(id, nr_konta, id_pracownik, kwota_pozyczki, kwota_splacona, oprocentowanie, okres,
                    data, czy_zmienne, stan, raty, kolejna_rata, ile_rat_zaplaconych);
            ans.add(k);
        } return ans;
    }
}
