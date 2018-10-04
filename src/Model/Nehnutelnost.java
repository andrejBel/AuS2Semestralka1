package Model;

import structures.AvlTree;

import java.util.Comparator;

public class Nehnutelnost {

    private long supisneCislo_;
    private String adresa_;
    private String popis_;
    private AvlTree<Obcan> obcaniaSTravalymPobytom_;

    public Nehnutelnost(long supisneCislo, String adresa, String popis) {
        this.supisneCislo_ = supisneCislo;
        this.adresa_ = adresa;
        this.popis_ = popis;
        obcaniaSTravalymPobytom_ = new AvlTree<>(Comparator.comparing(Obcan::getRodneCislo));
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
