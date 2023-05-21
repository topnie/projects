package webapp.bank.model.historia_kredyt;

import java.util.Date;

public class Historia_kredyt {

    private int id;
    private int nr_kredytu;
    private String typ_operacji;
    private int kwota;
    private Date data;

    public Historia_kredyt(int id, int nr_kredytu, String typ_operacji, int kwota, Date data) {
        this.id = id;
        this.nr_kredytu = nr_kredytu;
        this.typ_operacji = typ_operacji;
        this.kwota = kwota;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public int getNr_kredytu() {
        return nr_kredytu;
    }

    public String getTyp_operacji() {
        return typ_operacji;
    }

    public int getKwota() {
        return kwota;
    }

    public Date getData() {
        return data;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNr_kredytu(int nr_kredytu) {
        this.nr_kredytu = nr_kredytu;
    }

    public void setTyp_operacji(String typ_operacji) {
        this.typ_operacji = typ_operacji;
    }

    public void setKwota(int kwota) {
        this.kwota = kwota;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
