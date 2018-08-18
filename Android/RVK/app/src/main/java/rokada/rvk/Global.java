package rokada.rvk;

import android.app.Application;

public class Global extends Application {

    private String koncnica;
    private int tabPozicija;
    private String skl;
    private String filterSkl;
    private String filterSif;
    private String sifrDelNaloga;
    private String vrstaDokumenta;
    private String stevFN;
    private String sklNaziv;
    private String vtNaziv;
    private String sifArt;
    private String nazArt;
    private String vracam;
    private String izdano;
    private String uporabnik;
    private String vrdok;
    private String stev;
    private String prevzeto;
    private String url;
    //private String url = "http://10.1.1.22/api/";
    //private String url = "http://192.168.0.26:8080/api/"; //TESTNA RVK
    //private String url = "http://192.168.0.26:8000/api/"; //PRODUKCIJSKA RVK

    public void setUrl(String someVariable) {
        this.url = someVariable;
    }

    public String getKoncnica() {
        return koncnica;
    }

    public void setKoncnica(String someVariable) {
        this.koncnica = someVariable;
    }

    public int getTabPozicija() {
        return tabPozicija;
    }

    public void setTabPozicija(int someVariable) {
        this.tabPozicija = someVariable;
    }

    public String getSkl() {
        return skl;
    }

    public void setSkl(String someVariable) { this.skl = someVariable; }

    public String getFilterSkl() {
        return filterSkl;
    }

    public void setFilterSkl(String someVariable) { this.filterSkl = someVariable; }

    public String getFilterSif() { return filterSif; }

    public void setFilterSif(String someVariable) { this.filterSif = someVariable; }

    public String getSifrDelNaloga() {
        return sifrDelNaloga;
    }

    public void setSifrDelNaloga(String someVariable) { this.sifrDelNaloga = someVariable; }

    public String getVrstaDokumenta() {
        return vrstaDokumenta;
    }

    public void setVrstaDokumenta(String someVariable) { this.vrstaDokumenta = someVariable; }

    public String getStevFN() {
        return stevFN;
    }

    public void setStevFN(String someVariable) { this.stevFN = someVariable; }

    public String getSklNaziv() {
        return sklNaziv;
    }

    public void setSklNaziv(String someVariable) { this.sklNaziv = someVariable; }

    public String getVtNaziv() {
        return vtNaziv;
    }

    public void setVtNaziv(String someVariable) { this.vtNaziv = someVariable; }

    public String getSifArt() {
        return sifArt;
    }

    public void setSifArt(String someVariable) { this.sifArt = someVariable; }

    public String getNazArt() {
        return nazArt;
    }

    public void setNazArt(String someVariable) { this.nazArt = someVariable; }

    public String getVracam() {
        return vracam;
    }

    public void setVracam(String someVariable) { this.vracam = someVariable; }

    public String getIzdano() {
        return izdano;
    }

    public void setIzdano(String someVariable) { this.izdano = someVariable; }

    public String getUrl() {
        return url;
    }

    public String getUporabnik() {
        return uporabnik;
    }

    public void setUporabnik(String someVariable) { this.uporabnik = someVariable; }

    public String getVrdok() {
        return vrdok;
    }

    public void setVrdok(String someVariable) { this.vrdok = someVariable; }

    public String getStev() {
        return stev;
    }

    public void setStev(String someVariable) { this.stev = someVariable; }

    public String getPrevzeto() {
        return prevzeto;
    }

    public void setPrevzeto(String someVariable) { this.prevzeto = someVariable; }

}