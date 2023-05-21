package webapp.bank.model.pracownik;

public class Pracownik {

    private int id;
    private String dzial;

    public Pracownik(int id, String dzial) {
        this.id = id;
        this.dzial = dzial;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDzial() {
        return dzial;
    }

    public void setDzial(String dzial) {
        this.dzial = dzial;
    }
}
