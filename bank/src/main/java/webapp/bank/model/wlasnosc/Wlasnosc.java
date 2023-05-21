package webapp.bank.model.wlasnosc;

public class Wlasnosc {

    private int id_wlasc;
    private int nr_konto;


    public Wlasnosc(int id_wlasc, int nr_konto) {
        this.id_wlasc = id_wlasc;
        this.nr_konto = nr_konto;
    }

    public int getId_wlasc() {
        return id_wlasc;
    }

    public void setId_wlasc(int id_wlasc) {
        this.id_wlasc = id_wlasc;
    }

    public int getNr_konto() {
        return nr_konto;
    }

    public void setNr_konto(int nr_konto) {
        this.nr_konto = nr_konto;
    }
}
