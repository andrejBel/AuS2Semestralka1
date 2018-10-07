package GUI.View.ViewItems;

public class TableItemNehnutelnostListVlastnictva extends TableItemNehnutelnost {

    private double podiel_;
    private long cisloListuVlastnictva_;

    public TableItemNehnutelnostListVlastnictva(long supisneCislo, String adresa, String popis, double podiel, long cisloListuVlastnictva) {
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
