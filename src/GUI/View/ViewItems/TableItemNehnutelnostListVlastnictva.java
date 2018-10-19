package GUI.View.ViewItems;


public class TableItemNehnutelnostListVlastnictva extends TableItemNehnutelnost {

    private final long cisloListuVlastnictva_;

    public TableItemNehnutelnostListVlastnictva(long supisneCislo, String adresa, String popis, long cisloListuVlastnictva) {
        super(supisneCislo, adresa, popis);
        cisloListuVlastnictva_ = cisloListuVlastnictva;
    }

    public long getCisloListuVlastnictva() {
        return cisloListuVlastnictva_;
    }

}