package GUI.View.ViewItems;

public class TableItemObcanPodiel {

    String menoPriezvisko_;
    String rodneCislo_;
    double staryPodiel_;
    double novyPodiel_;

    public TableItemObcanPodiel(String menoPriezvisko, String rodneCislo, double staryPodiel, double novyPodiel) {
        this.menoPriezvisko_ = menoPriezvisko;
        this.rodneCislo_ = rodneCislo;
        this.staryPodiel_ = staryPodiel;
        this.novyPodiel_ = novyPodiel;
    }

    public TableItemObcanPodiel(String menoPriezvisko, String rodneCislo, double staryPodiel) {
        this(menoPriezvisko, rodneCislo, staryPodiel, 0.0);
    }

    public String getMenoPriezvisko() {
        return menoPriezvisko_;
    }

    public String getRodneCislo() {
        return rodneCislo_;
    }

    public double getStaryPodiel() {
        return staryPodiel_;
    }

    public double getNovyPodiel() {
        return novyPodiel_;
    }

    public void setNovyPodiel(double novyPodiel) {
        this.novyPodiel_ = novyPodiel;
    }
}
