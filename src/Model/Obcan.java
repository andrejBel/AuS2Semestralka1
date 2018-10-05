package Model;

import javafx.util.Pair;
import structures.AvlTree;

import java.util.Comparator;

public class Obcan {

    private String menoPriezvisko_;
    private String rodneCislo_;
    long datumNarodenia_;
    Nehnutelnost trvalyPobyt_;
    // kluc je cislo katastralnehoo uzemia, hodnota su nehnutelnosti obcana v danom uzemi
    AvlTree<Pair< Long, AvlTree<Nehnutelnost> > > nehnutelnostiVoVlastnictve_;

    public Obcan(String menoPriezvisko, String rodneCislo, long datumNarodenia) {
        this.menoPriezvisko_ = menoPriezvisko;
        this.rodneCislo_ = rodneCislo;
        this.datumNarodenia_ = datumNarodenia;
        trvalyPobyt_ = null;
        this.nehnutelnostiVoVlastnictve_ = new AvlTree<>(Comparator.comparing(Pair::getKey));
    }

    public Obcan() {
        this("", "", 0);
    }

    public String getMenoPriezvisko() {
        return menoPriezvisko_;
    }

    public String getRodneCislo() {
        return rodneCislo_;
    }

    public long getDatumNarodenia() {
        return datumNarodenia_;
    }

    public void setRodneCislo_(String rodneCislo) {
        this.rodneCislo_ = rodneCislo;
    }
}
