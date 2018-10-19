package GUI.View.ViewItems;

public class TableItemNehnutelnostListVlastnictvaKU extends TableItemNehnutelnostListVlastnictvaPodiel {

    private final long cisloKatastralnehoUzemia_;

    public TableItemNehnutelnostListVlastnictvaKU(long supisneCislo, String adresa, String popis, double podiel, long cisloListuVlastnictva, long cisloKatastralnehoUzemia) {
        super(supisneCislo, adresa, popis, podiel, cisloListuVlastnictva);
        cisloKatastralnehoUzemia_ = cisloKatastralnehoUzemia;
    }

    public long getCisloKatastralnehoUzemia() {
        return cisloKatastralnehoUzemia_;
    }

}
