package webapp.bank.model.wlasnosc;

import webapp.bank.model.konto.Konto;
import webapp.bank.model.uzytkownik.Uzytkownik;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IWlasnoscDao {
    void addWlasnosc(Wlasnosc w, Connection con)  throws SQLException;

    List<Konto> getKontoFromUzytkownik(long id_user, Connection con) throws SQLException;

    List<Uzytkownik> getUzytkownikFromKonto(long id_konto, Connection con) throws SQLException;
}
