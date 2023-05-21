package webapp.bank.model.konto;

public class Konto {
    private int numer;
    private String waluta;
    private int stan;

    public Konto(int numer, String waluta, int stan) {
        this.numer = numer;
        this.waluta = waluta;
        this.stan = stan;
    }

    public int getNumer() {
        return numer;
    }

    public void setNumer(int numer) {
        this.numer = numer;
    }

    public String getWaluta() {
        return waluta;
    }

    public void setWaluta(String waluta) {
        this.waluta = waluta;
    }

    public int getStan() {
        return stan;
    }

    public void setStan(int stan) {
        this.stan = stan;
    }
}
