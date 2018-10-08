package Model;

import structures.AvlTree;

import java.util.Comparator;

public class Nehnutelnost {

    private long supisneCislo_;
    private String adresa_;
    private String popis_;
    private AvlTree<Obcan> obcaniaSTravalymPobytom_;
    private ListVlastnictva listVlastnictva_; // list vlastnictva, na ktoromn sa nachadza nehnutelnost

    public Nehnutelnost(long supisneCislo, String adresa, String popis, ListVlastnictva listVlastnictva) {
        this.supisneCislo_ = supisneCislo;
        this.adresa_ = adresa;
        this.popis_ = popis;
        obcaniaSTravalymPobytom_ = new AvlTree<>((o1, o2) -> o1.getRodneCislo().compareTo(o2.getRodneCislo()));
        listVlastnictva_ = listVlastnictva;
    }

    public Nehnutelnost() {
        this(0, "", "", null);
    }

    public boolean pridajObcanaSTrvalymPobytom(Obcan obcan) {
        return obcaniaSTravalymPobytom_.insert(obcan);
    }

    public boolean odstranObcanaSTrvalymPobytom(Obcan obcan) {
        return obcaniaSTravalymPobytom_.remove(obcan);
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

    public ListVlastnictva getListVlastnictva() {
        return listVlastnictva_;
    }

    public AvlTree<Obcan> getObcaniaSTravalymPobytom() {
        return obcaniaSTravalymPobytom_;
    }

    public void setObcaniaSTravalymPobytom_(AvlTree<Obcan> obcaniaSTravalymPobytom_) {
        this.obcaniaSTravalymPobytom_ = obcaniaSTravalymPobytom_;
    }

    public void setSupisneCislo(long supisneCislo) {
        this.supisneCislo_ = supisneCislo;
    }

    public void setListVlastnictva(ListVlastnictva listVlastnictva) {
        this.listVlastnictva_ = listVlastnictva;
    }
}
