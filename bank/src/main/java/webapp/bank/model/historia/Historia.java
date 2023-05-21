package webapp.bank.model.historia;

import java.util.Date;

public class Historia {
    private int id;
    private int nr_konto;
    private String typ_operacji;
    private int kwota;
    private Date data;

    public Historia(int id, int nr_konto, String typ_operacji, int kwota, Date data) {
        this.id = id;
        this.nr_konto = nr_konto;
        this.typ_operacji = typ_operacji;
        this.kwota = kwota;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNr_konto() {
        return nr_konto;
    }

    public void setNr_konto(int nr_konto) {
        this.nr_konto = nr_konto;
    }

    public String getTyp_operacji() {
        return typ_operacji;
    }

    public void setTyp_operacji(String typ_operacji) {
        this.typ_operacji = typ_operacji;
    }

    public int getKwota() {
        return kwota;
    }

    public void setKwota(int kwota) {
        this.kwota = kwota;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Historia{" +
                "id=" + id +
                ", nr_konto=" + nr_konto +
                ", typ_operacji='" + typ_operacji + '\'' +
                ", kwota=" + kwota +
                ", data=" + data +
                '}';
    }
}
