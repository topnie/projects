package webapp.bank.model.klient;

public class Klient {
    private int id;
    private String adres;

    private String numer_telefonu;

    public Klient() {}

    public Klient(int id, String adres, String numer_telefonu) {
        this.id = id;
        this.adres = adres;
        this.numer_telefonu = numer_telefonu;
    }

    public String getNumer_telefonu() {
        return numer_telefonu;
    }

    public void setNumer_telefonu(String numer_telefonu) {
        this.numer_telefonu = numer_telefonu;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }
}
