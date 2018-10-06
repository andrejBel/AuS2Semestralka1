package Model;

import structures.AvlTree;

import javax.xml.ws.Holder;
import java.util.Comparator;
import java.util.Optional;

public class ListVlastnictva {

    public static class ObcanSPodielom {

        private Obcan obcan_;
        private double podiel_;

        public ObcanSPodielom(Obcan obcan) {
            this.obcan_ = obcan;
            this.podiel_ = 0.0;
        }

        public ObcanSPodielom(Obcan obcan, double podiel) {
            this.obcan_ = obcan;
            this.podiel_ = podiel;
        }

        public Obcan getObcan() {
            return obcan_;
        }

        public double getPodiel() {
            return podiel_;
        }

        public void setPodiel(double podiel) {
            this.podiel_ = podiel;
        }
    }

    private KatastralneUzemie katastralneUzemie_;
    private long cisloListuVlastnictva_;
    private AvlTree<Nehnutelnost> nehnutelnostiNaListeVlastnictva_;
    private AvlTree<ObcanSPodielom> vlastniciSPodielom_;

    public ListVlastnictva(KatastralneUzemie katastralneUzemie, long cisloListuVlastnictva) {
        this.katastralneUzemie_ = katastralneUzemie;
        this.cisloListuVlastnictva_ = cisloListuVlastnictva;
        nehnutelnostiNaListeVlastnictva_ = new AvlTree<>(Comparator.comparing(Nehnutelnost::getSupisneCislo));
        vlastniciSPodielom_ = new AvlTree<>(Comparator.comparing(obcanSPodielom -> obcanSPodielom.getObcan().getRodneCislo()));
    }

    public ListVlastnictva() {
        this(null, 0);
    }

    public boolean vlozNehnutelnostNaListVlastnictva(long supisneCisloNehnutelnosti, String adresaNehnutelnosti, String popisNehnutelnosti, Optional<Holder<Nehnutelnost>> vlozenaNehnutelnost) {
        Nehnutelnost nehnutelnost = new Nehnutelnost(supisneCisloNehnutelnosti, adresaNehnutelnosti, popisNehnutelnosti);
        boolean inserted = nehnutelnostiNaListeVlastnictva_.insert(nehnutelnost);
        vlozenaNehnutelnost.ifPresent(nehnutelnostHolder -> nehnutelnostHolder.value = inserted ? nehnutelnost: null);
        return inserted;
    }

    public KatastralneUzemie getKatastralneUzemie() {
        return katastralneUzemie_;
    }

    public long getCisloListuVlastnictva() {
        return cisloListuVlastnictva_;
    }

    public AvlTree<Nehnutelnost> getNehnutelnostiNaListeVlastnictva() {
        return nehnutelnostiNaListeVlastnictva_;
    }

    public AvlTree<ObcanSPodielom> getVlastniciSPodielom() {
        return vlastniciSPodielom_;
    }


    public void setCisloListuVlastnictva(long cisloListuVlastnictva) {
        this.cisloListuVlastnictva_ = cisloListuVlastnictva;
    }
}
