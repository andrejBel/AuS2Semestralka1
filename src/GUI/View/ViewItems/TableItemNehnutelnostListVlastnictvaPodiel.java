package GUI.View.ViewItems;

public class TableItemNehnutelnostListVlastnictvaPodiel extends TableItemNehnutelnost {

    private final double podiel_;
    private final long cisloListuVlastnictva_;

    public TableItemNehnutelnostListVlastnictvaPodiel(long supisneCislo, String adresa, String popis, double podiel, long cisloListuVlastnictva) {
        super(supisneCislo, adresa, popis);
        podiel_ = podiel;
        cisloListuVlastnictva_ = cisloListuVlastnictva;
    }

    public double getPodiel() {
        return podiel_;
    }

    public long getCisloListuVlastnictva() {
        return cisloListuVlastnictva_;
    }

}
