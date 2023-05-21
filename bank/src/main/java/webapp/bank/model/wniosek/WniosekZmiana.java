package webapp.bank.model.wniosek;

public class WniosekZmiana {
    private long id_klient;
    private String nazwa_pola;
    private String nowa_wartosc;
    private long id;


    public WniosekZmiana(long id_klient, String nazwa_pola, String nowa_wartosc, long id) {
        this.id_klient = id_klient;
        this.nazwa_pola = nazwa_pola;
        this.nowa_wartosc = nowa_wartosc;
        this.id = id;
    }

    public long getId_klient() {
        return id_klient;
    }

    public void setId_klient(long id_klient) {
        this.id_klient = id_klient;
    }

    public String getNazwa_pola() {
        return nazwa_pola;
    }

    public void setNazwa_pola(String nazwa_pola) {
        this.nazwa_pola = nazwa_pola;
    }

    public String getNowa_wartosc() {
        return nowa_wartosc;
    }

    public void setNowa_wartosc(String nowa_wartosc) {
        this.nowa_wartosc = nowa_wartosc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
