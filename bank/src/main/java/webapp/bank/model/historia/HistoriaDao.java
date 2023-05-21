package webapp.bank.model.historia;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class HistoriaDao implements IHistoriaDao {

    @Override
    public void addHistoria(Historia h, Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = formatter.format(h.getData());
        String query = "INSERT INTO Historia (id, nr_konto, typ_operacji, kwota, data) VALUES (" + Long.toString((this.getLastHistoria(con) + 1)) + ", " +
                Integer.toString(h.getNr_konto()) + ", '" + h.getTyp_operacji() + "', " + Integer.toString(h.getKwota()) + ", TO_DATE('" + dataString +
                "', 'DD/MM/YYYY'))";
        ResultSet rs = stmt.executeQuery(query);
    }

    public long getLastHistoria(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String query = "Select max(id) from historia";
        ResultSet rs = stmt.executeQuery(query);
        if(rs.next()) {
            if (rs.getString(1) == null) return 0L;
            return Long.parseLong(rs.getString(1));
        }
        else return 0L;
    }

    @Override
    public List<Historia> getKontoHistoria(long id, Connection con) throws SQLException {
        List<Historia> ans = new LinkedList<>();
        Statement stmt = con.createStatement();
        String idString = Long.toString(id);
        String query = "SELECT * FROM Historia WHERE nr_konto = " + idString;
        ResultSet rs = stmt.executeQuery(query);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        while (rs.next()) {
            try {
                Historia k = new Historia(Integer.parseInt(rs.getString(1)),
                        Integer.parseInt(rs.getString(2)), rs.getString(3), Integer.parseInt(rs.getString(4)),
                        formatter.parse(rs.getString(5)));
                ans.add(k);
            }
            catch (ParseException ignored) {
            }

        } return ans;
    }
}
