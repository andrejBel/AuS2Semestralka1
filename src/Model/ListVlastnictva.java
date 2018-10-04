package Model;

import structures.AvlTree;

import java.util.Comparator;

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

    KatastralneUzemie katastralneUzemie_;
    long cisloListuVlastnictva_;
    AvlTree<Nehnutelnost> nehnutelnostiNaListeVlastnictva_;
    AvlTree<ObcanSPodielom> vlastniciSPodielom_;

    public ListVlastnictva(KatastralneUzemie katastralneUzemie, long cisloListuVlastnictva) {
        this.katastralneUzemie_ = katastralneUzemie;
        this.cisloListuVlastnictva_ = cisloListuVlastnictva;
        nehnutelnostiNaListeVlastnictva_ = new AvlTree<>(Comparator.comparing(Nehnutelnost::getSupisneCislo));
        vlastniciSPodielom_ = new AvlTree<>(Comparator.comparing(obcanSPodielom -> obcanSPodielom.getObcan().getRodneCislo()));
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

}
