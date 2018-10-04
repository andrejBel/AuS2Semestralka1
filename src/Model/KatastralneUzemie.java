package Model;

import structures.AvlTree;

import java.util.Comparator;

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

    public long getCisloKatastralnehoUzemia() {
        return cisloKatastralnehoUzemia_;
    }

    public String getNazov() {
        return nazov_;
    }
}
