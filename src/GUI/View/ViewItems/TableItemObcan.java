package GUI.View.ViewItems;

public class TableItemObcan {

    private String menoPriezvisko_;
    private String rodneCislo_;
    private long datumNarodenia_;

    public TableItemObcan(String menoPriezvisko, String rodneCislo, long datumNarodenia) {
        this.menoPriezvisko_ = menoPriezvisko;
        this.rodneCislo_ = rodneCislo;
        this.datumNarodenia_ = datumNarodenia;
    }

    public String getMenoPriezvisko() {
        return menoPriezvisko_;
    }

    public String getRodneCislo() {
        return rodneCislo_;
    }

    public long getDatumNarodenia() {
        return datumNarodenia_;
    }
}
