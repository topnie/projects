package webapp.bank.model.uzytkownik;



public class Uzytkownik {

    private long id;
    private String imie;
    private String mail;
    private String nazwisko;
    private String haslo;

    public Uzytkownik(String imie, String nazwisko, String haslo, String mail, int id) {
        this.id = id;
        this.imie = imie;
        this.haslo = haslo;
        this.nazwisko = nazwisko;
        this.mail = mail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNazwisko() { return nazwisko; }

    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
}

