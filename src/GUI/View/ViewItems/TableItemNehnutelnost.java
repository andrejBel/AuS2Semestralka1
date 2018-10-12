package GUI.View.ViewItems;

public class TableItemNehnutelnost {
    private long supisneCislo_;
    private String adresa_;
    private String popis_;

    public TableItemNehnutelnost(long supisneCislo, String adresa, String popis) {
        this.supisneCislo_ = supisneCislo;
        this.adresa_ = adresa;
        this.popis_ = popis;
    }

    public long getSupisneCislo() {
        return supisneCislo_;
    }

    public String getAdresa() {
        return adresa_;
    }

    public String getPopis() {
        return popis_;
    }
}
