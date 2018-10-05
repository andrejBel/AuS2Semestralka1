package Model;

import structures.AvlTree;

import javax.xml.ws.Holder;
import java.util.Comparator;
import java.util.Optional;

public class KatastralneUzemie {

    private long cisloKatastralnehoUzemia_;
    private String nazov_;
    private AvlTree<Nehnutelnost> nehnutelnostiVkatastralnomUzemi_;
    private AvlTree<ListVlastnictva> listyVlastnictvaVKatastralnomUzemi_;

    public KatastralneUzemie(long cisloKatastralnehoUzemia_, String nazov_) {
        this.cisloKatastralnehoUzemia_ = cisloKatastralnehoUzemia_;
        this.nazov_ = nazov_;
        nehnutelnostiVkatastralnomUzemi_ = new AvlTree<>(Comparator.comparing(Nehnutelnost::getSupisneCislo));
        listyVlastnictvaVKatastralnomUzemi_ = new AvlTree<>(Comparator.comparing(ListVlastnictva::getCisloListuVlastnictva));
    }

    public KatastralneUzemie() {
        this(0, "");
    }

    public long getCisloKatastralnehoUzemia() {
        return cisloKatastralnehoUzemia_;
    }

    public String getNazov() {
        return nazov_;
    }

    public boolean vlozListVlastnictva(long cisloListuVlastnictva, Optional<Holder<ListVlastnictva>> vlozenyListVlastnictva) {
        ListVlastnictva listVlastnictva = new ListVlastnictva(this, cisloListuVlastnictva);
        boolean inserted = listyVlastnictvaVKatastralnomUzemi_.insert(listVlastnictva);
        vlozenyListVlastnictva.ifPresent(listVlastnictvaHolder -> listVlastnictvaHolder.value = inserted ? listVlastnictva : null);
        return inserted;
    }

    public void setCisloKatastralnehoUzemia(long cisloKatastralnehoUzemia) {
        this.cisloKatastralnehoUzemia_ = cisloKatastralnehoUzemia;
    }

    public void setNazov(String nazov) {
        this.nazov_ = nazov;
    }
}
