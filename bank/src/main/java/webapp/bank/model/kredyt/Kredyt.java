package webapp.bank.model.kredyt;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Kredyt {

    private int id;
    private int nr_konta;
    private int id_pracownik;
    private int kwota_pozyczki;
    private int kwota_splacona;
    private int oprocentowanie;
    private int okres;
    private Date data;
    private boolean czy_zmienne;
    private String stan;
    private String raty;
    private int kolejna_rata;
    private int ile_rat_zaplaconych;

    public void setId(int id) {
        this.id = id;
    }

    public void setNr_konta(int nr_konta) {
        this.nr_konta = nr_konta;
    }

    public void setId_pracownik(int id_pracownik) {
        this.id_pracownik = id_pracownik;
    }

    public void setKwota_pozyczki(int kwota_pozyczki) {
        this.kwota_pozyczki = kwota_pozyczki;
    }

    public void setOprocentowanie(int oprocentowanie) {
        this.oprocentowanie = oprocentowanie;
    }

    public void setOkres(int okres) {
        this.okres = okres;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setCzy_zmienne(boolean czy_zmienne) {
        this.czy_zmienne = czy_zmienne;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public int getId() {
        return id;
    }

    public int getNr_konta() {
        return nr_konta;
    }

    public int getId_pracownik() {
        return id_pracownik;
    }

    public int getKwota_pozyczki() {
        return kwota_pozyczki;
    }

    public int getOprocentowanie() {
        return oprocentowanie;
    }

    public int getOkres() {
        return okres;
    }

    public Date getData() {
        return data;
    }

    public boolean isCzy_zmienne() {
        return czy_zmienne;
    }

    public String getStan() {
        return stan;
    }

    public Kredyt(int id, int nr_konta, int id_pracownik, int kwota_pozyczki, int kwotaSplacona, int oprocentowanie,
                  int okres, Date data, boolean czy_zmienne, String stan, String raty, int kolejna_rata, int ile_rat_zaplaconych) {
        this.id = id;
        this.nr_konta = nr_konta;
        this.id_pracownik = id_pracownik;
        this.kwota_pozyczki = kwota_pozyczki;
        kwota_splacona = kwotaSplacona;
        this.oprocentowanie = oprocentowanie;
        this.okres = okres;
        this.data = data;
        this.czy_zmienne = czy_zmienne;
        this.stan = stan;
        this.raty = raty;
        this.kolejna_rata = kolejna_rata;
        this.ile_rat_zaplaconych = ile_rat_zaplaconych;
    }

    public int getKwota_splacona() {
        return kwota_splacona;
    }

    public void setKwota_splacona(int kwota_splacona) {
        this.kwota_splacona = kwota_splacona;
    }

    public String getRaty() {
        return raty;
    }

    public void setRaty(String raty) {
        this.raty = raty;
    }

    public int getKolejna_rata() {
        return kolejna_rata;
    }

    public void setKolejna_rata(int kolejna_rata) {
        this.kolejna_rata = kolejna_rata;
    }

    public int getIle_rat_zaplaconych() {
        return ile_rat_zaplaconych;
    }

    public void setIle_rat_zaplaconych(int ile_rat_zaplaconych) {
        this.ile_rat_zaplaconych = ile_rat_zaplaconych;
    }

    public long getFirstInstallment() {
        if (!czy_zmienne) { //Stałe oprocentowanie
            if (raty.equals("stale")) { //Stałe raty
                int k = 360 / okres;
                int n = 5 * k;
                float x = (float) (((float)oprocentowanie) / 100) / (float) k;
                float y = (float) Math.pow(1 + x, n);
                return (long) (((float) kwota_pozyczki * x * y) / (y - 1));
            }
            else { //Raty malejące
                int k = 360 / okres;
                int n = 5 * k;
                long rata_pozyczka = kwota_pozyczki / n;
                long rata_oprocentowanie = (kwota_pozyczki * oprocentowanie) / 100;
                return rata_pozyczka + rata_oprocentowanie;
            }
        }
        else { //Zmienne oprocentowanie, malejące raty
            int k = 360 / okres;
            int n = 5 * k;
            long rata_pozyczka = kwota_pozyczki / n;
            long rata_oprocentowanie = (kwota_pozyczki * oprocentowanie) / 100;
            return rata_pozyczka + rata_oprocentowanie;
        }
    }

    public long getInstallment() {
        if (!czy_zmienne) { //Stałe oprocentowanie
            if (raty.equals("stale")) { //Stałe raty
                return this.getFirstInstallment();
            }
            else { //Raty malejące
                int k = 360 / okres;
                int n = 5 * k;
                long rata_pozyczka = kwota_pozyczki / n;
                long rata_oprocentowanie = (kwota_pozyczki * oprocentowanie - kwota_splacona) / 100;
                return rata_pozyczka + rata_oprocentowanie;
            }
        }
        else { //Zmienne oprocentowanie, malejące raty
            int k = 360 / okres;
            int n = 5 * k;
            long rata_pozyczka = kwota_pozyczki / n;
            long rata_oprocentowanie = (kwota_pozyczki * oprocentowanie  - kwota_splacona) / 100;
            return rata_pozyczka + rata_oprocentowanie;
        }
    }

    public List<Kredyt> getPrediction() {

        List<Kredyt> ans = new LinkedList<>();
        Kredyt next = new Kredyt(this.id, this.nr_konta, this.id_pracownik, this.kwota_pozyczki, this.kwota_splacona, this.oprocentowanie, this.okres, this.data, this.czy_zmienne, this.stan, this.raty, (int) this.getFirstInstallment(), this.ile_rat_zaplaconych);
        while (next.kwota_splacona < next.kwota_pozyczki) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(next.data);
            calendar.add(Calendar.DATE, next.okres);
            Date nowyTermin = calendar.getTime();
            next.setData(nowyTermin);

            next.kwota_splacona += next.kwota_pozyczki / ((5*360) / next.okres);
            next.ile_rat_zaplaconych ++;
            if (raty.equals("stale")) {}
            else { //Raty malejące
                int k = 360 / next.okres;
                int n = 5 * k;
                long rata_pozyczka = next.kwota_pozyczki / n;
                long rata_oprocentowanie = (next.kwota_pozyczki * next.oprocentowanie - next.kwota_splacona) / 100;
                next.kolejna_rata = (int) (rata_pozyczka + rata_oprocentowanie);
            }
            ans.add(new Kredyt(next.id, next.nr_konta, next.id_pracownik, next.kwota_pozyczki, next.kwota_splacona, next.oprocentowanie, next.okres, next.data, next.czy_zmienne, next.stan, next.raty, next.kolejna_rata, next.ile_rat_zaplaconych));
        }
        return ans;
    }

}
