package GUI.View.ViewItems;

public class TableItemNehnutelnost {
    private final long supisneCislo_;
    private final String adresa_;
    private final String popis_;

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
