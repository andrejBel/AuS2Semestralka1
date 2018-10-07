package Model;

import structures.AvlTree;

import java.util.Comparator;

public class Obcan {

    private static class Pair<K,V> {

         K key;
         V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }

    private String menoPriezvisko_;
    private String rodneCislo_;
    long datumNarodenia_;
    Nehnutelnost trvalyPobyt_;
    // kluc je cislo katastralnehoo uzemia, hodnota su nehnutelnosti obcana v danom uzemi
    AvlTree<Pair< Long, AvlTree<ListVlastnictva> > > listyVlatnictva_;

    private static final  Pair< Long, AvlTree<ListVlastnictva> > dummyPair = new Pair<>((long) 0, null);

    public Obcan(String menoPriezvisko, String rodneCislo, long datumNarodenia) {
        this.menoPriezvisko_ = menoPriezvisko;
        this.rodneCislo_ = rodneCislo;
        this.datumNarodenia_ = datumNarodenia;
        trvalyPobyt_ = null;
        this.listyVlatnictva_ = new AvlTree<>(Comparator.comparingLong(Pair::getKey));
    }

    public Obcan() {
        this("", "", 0);
    }

    public boolean pridajAleboPonechajListVlastnictva(ListVlastnictva listVlastnictva) {
        long cisloKatastralnehoUzemia = listVlastnictva.getKatastralneUzemie().getCisloKatastralnehoUzemia();
        dummyPair.setKey(cisloKatastralnehoUzemia);
        Pair< Long, AvlTree<ListVlastnictva> > listyVlastnictvaVKatastralnomUzemi = listyVlatnictva_.findData(dummyPair);
        if (listyVlastnictvaVKatastralnomUzemi == null) {
            listyVlastnictvaVKatastralnomUzemi = vyrobParPreStromListovVlastnictva(cisloKatastralnehoUzemia);
            listyVlatnictva_.insert(listyVlastnictvaVKatastralnomUzemi);
        }
        listyVlastnictvaVKatastralnomUzemi.getValue().insert(listVlastnictva);
        return true;
    }

    private Pair< Long, AvlTree<ListVlastnictva> > vyrobParPreStromListovVlastnictva(long cisloKatastralnehoUzemia) {
        return new Pair<>(cisloKatastralnehoUzemia, new AvlTree<>(Comparator.comparingLong(ListVlastnictva::getCisloListuVlastnictva)));
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

    public Nehnutelnost getTrvalyPobyt() {
        return trvalyPobyt_;
    }

    public void setRodneCislo(String rodneCislo) {
        this.rodneCislo_ = rodneCislo;
    }

    public void setTrvalyPobyt(Nehnutelnost trvalyPobyt) {
        this.trvalyPobyt_ = trvalyPobyt;
    }
}
