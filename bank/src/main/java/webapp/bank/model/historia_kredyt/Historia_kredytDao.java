package webapp.bank.model.historia_kredyt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Historia_kredytDao implements IHistoria_kredytDao{
    @Override
    public void addHistoriaKredyt(Historia_kredyt h, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = formatter.format(h.getData());
        int id = (int) (this.getLastHistoriaKredyt(con) + 1);
        int nr_kredytu = h.getNr_kredytu();
        int kwota = h.getKwota();
        String query = "INSERT INTO Historia_kredyt (id, nr_kredyt, typ_operacji, kwota, data) VALUES (" + Integer.toString(id) + ", " +
                Integer.toString(nr_kredytu) + ", '" + h.getTyp_operacji() + "', " + Integer.toString(kwota) + ", TO_DATE('" + dataString +
                "', 'DD/MM/YYYY'))";
        ResultSet rs = stmt.executeQuery(query);
    }

    @Override
    public List<Historia_kredyt> getHistoriaKredyt(long id, Connection con) throws SQLException {
        List<Historia_kredyt> ans = new LinkedList<>();
        Statement stmt = con.createStatement();
        String idString = Long.toString(id);
        String query = "SELECT * FROM Historia_kredyt WHERE nr_kredyt = " + idString;
        ResultSet rs = stmt.executeQuery(query);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        while (rs.next()) {
            try {
                String k_id = rs.getString(1);
                String nr_kredytu = rs.getString(2);
                String typ_operacji = rs.getString(3);
                String kwota = rs.getString(4);
                String data = rs.getString(5);
                Historia_kredyt k = new Historia_kredyt(Integer.parseInt(k_id), Integer.parseInt(nr_kredytu), typ_operacji,
                        Integer.parseInt(kwota), formatter.parse(data));
                ans.add(k);
            }
            catch (ParseException ignored) {}

        } return ans;
    }

    @Override
    public long getLastHistoriaKredyt(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "Select max(id) from Historia_kredyt";
        ResultSet rs = stmt.executeQuery(query);
        if(rs.next()) {
            if (rs.getString(1) == null) return 0L;
            return Long.parseLong(rs.getString(1));
        }
        else return 0L;
    }

    @Override
    public Historia_kredyt getLastHistoriaById(long num, Connection con) throws SQLException, ParseException {
        Statement stmt = con.createStatement();
        String query = "SELECT * FROM historia_kredyt WHERE nr_kredyt = " + Long.toString(num) +
                " ORDER BY data DESC";
        ResultSet rs = stmt.executeQuery(query);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        if(rs.next()) {
            String k_id = rs.getString(1);
            String nr_kredytu = rs.getString(2);
            String typ_operacji = rs.getString(3);
            String kwota = rs.getString(4);
            String data = rs.getString(5);
            return new Historia_kredyt(Integer.parseInt(k_id), Integer.parseInt(nr_kredytu), typ_operacji,
                    Integer.parseInt(kwota), formatter.parse(data));
        }
        else return null;
    }

}
