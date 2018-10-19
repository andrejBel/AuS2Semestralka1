package Model;

import Structures.AvlTree;

import java.util.Comparator;

public class ListVlastnictva {

    public static class ObcanSPodielom {

        private Obcan obcan_;
        private double podiel_;

        public ObcanSPodielom() {
            this(null, 0.0);
        }

        public ObcanSPodielom(Obcan obcan) {
            this(obcan, 0.0);
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

        public void setObcan(Obcan obcan) {
            this.obcan_ = obcan;
        }

    }

    private KatastralneUzemie katastralneUzemie_;
    private long cisloListuVlastnictva_;
    private final AvlTree<Nehnutelnost> nehnutelnostiNaListeVlastnictva_;
    private final AvlTree<ObcanSPodielom> vlastniciSPodielom_;

    private static final ObcanSPodielom dummyObcanSPodielom = new ObcanSPodielom();
    private static final Nehnutelnost dummyNehnutelnost = new Nehnutelnost();

    public ListVlastnictva(KatastralneUzemie katastralneUzemie, long cisloListuVlastnictva) {
        this.katastralneUzemie_ = katastralneUzemie;
        this.cisloListuVlastnictva_ = cisloListuVlastnictva;
        nehnutelnostiNaListeVlastnictva_ = new AvlTree<>(Comparator.comparingLong(Nehnutelnost::getSupisneCislo));
        vlastniciSPodielom_ = new AvlTree<>((o1, o2) -> o1.getObcan().getRodneCislo().compareTo(o2.getObcan().getRodneCislo()));
    }

    public ListVlastnictva() {
        this(null, 0);
    }

    public boolean vlozNehnutelnostNaListVlastnictva(Nehnutelnost vkladanaNehnutelnost) {
        if (vkladanaNehnutelnost.getListVlastnictva() != this) {
            return false;
        }
        return nehnutelnostiNaListeVlastnictva_.insert(vkladanaNehnutelnost);
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

    public ObcanSPodielom getObcanSPodielom(Obcan obcan) {
        dummyObcanSPodielom.setObcan(obcan);
        return vlastniciSPodielom_.findData(dummyObcanSPodielom);
    }

    public void setCisloListuVlastnictva(long cisloListuVlastnictva) {
        this.cisloListuVlastnictva_ = cisloListuVlastnictva;
    }

    public boolean pridajAleboPonechajVlastnika(Obcan obcan) {
        ObcanSPodielom obcanNaPridanie = new ObcanSPodielom(obcan, 0.0);
        vlastniciSPodielom_.insert(obcanNaPridanie);
        return true;
    }

    public boolean pridajAleboPonechajVlastnika(Obcan obcan, double podiel) {
        dummyObcanSPodielom.setObcan(obcan);
        ObcanSPodielom obcanSPodielom = vlastniciSPodielom_.findData(dummyObcanSPodielom);
        if (obcanSPodielom == null) {
            ObcanSPodielom obcanNaPridanie = new ObcanSPodielom(obcan, podiel);
            vlastniciSPodielom_.insert(obcanNaPridanie);
        } else {
            obcanSPodielom.setPodiel(obcanSPodielom.getPodiel() + podiel);
        }
        return true;
    }

    public boolean odstranVlastnika(Obcan obcan) {
        dummyObcanSPodielom.setObcan(obcan);
        return vlastniciSPodielom_.remove(dummyObcanSPodielom) != null;
    }

    public Nehnutelnost odstranNehnutelnostZListuVlastnictva(long supisneCisloNehnutelnosti) {
        dummyNehnutelnost.setSupisneCislo(supisneCisloNehnutelnosti);
        return nehnutelnostiNaListeVlastnictva_.remove(dummyNehnutelnost);
    }

    public boolean zmenVlastnikaNaListeVlastnictva(Obcan povodnyMajitel, Obcan novyMajitel) {
        dummyObcanSPodielom.setObcan(povodnyMajitel);
        ObcanSPodielom povodnyMajitelPodiel = vlastniciSPodielom_.findData(dummyObcanSPodielom);
        if (povodnyMajitelPodiel != null) {
            dummyObcanSPodielom.setObcan(novyMajitel);
            ObcanSPodielom novyObcanSPodielom = vlastniciSPodielom_.findData(dummyObcanSPodielom);
            boolean removedStaryMajitel = vlastniciSPodielom_.remove(povodnyMajitelPodiel) != null;
            if (removedStaryMajitel) {
                removedStaryMajitel = povodnyMajitel.odstranListVlastnictva(this);
                if (removedStaryMajitel) {
                    if (novyObcanSPodielom == null) { // ked este novy majitel nema podiel na LV
                        novyObcanSPodielom = new ObcanSPodielom(novyMajitel, povodnyMajitelPodiel.getPodiel());
                        novyMajitel.pridajAleboPonechajListVlastnictva(this);
                        return vlastniciSPodielom_.insert(novyObcanSPodielom);
                    } else {
                        novyMajitel.pridajAleboPonechajListVlastnictva(this);
                        novyObcanSPodielom.setPodiel(novyObcanSPodielom.getPodiel() + povodnyMajitelPodiel.getPodiel());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean presunVsetokObsahNaInyListVlastnictva(ListVlastnictva inyListVlastnictva) {
        boolean inserted = false;
        for (Nehnutelnost nehnutelnost: nehnutelnostiNaListeVlastnictva_) {
            nehnutelnost.setListVlastnictva(inyListVlastnictva);
            inserted = inyListVlastnictva.vlozNehnutelnostNaListVlastnictva(nehnutelnost);
            if (!inserted) {
                return false;
            }
        }
        long pocetVlastnikovPredMergom = inyListVlastnictva.vlastniciSPodielom_.getSize();
        for (ObcanSPodielom obcanSPodielom : vlastniciSPodielom_) {
            boolean novyVlastnik = inyListVlastnictva.vlastniciSPodielom_.insert(obcanSPodielom);
            if (novyVlastnik && pocetVlastnikovPredMergom > 0) {
                obcanSPodielom.setPodiel(0.0);
            }
            obcanSPodielom.getObcan().odstranListVlastnictva(this);
            obcanSPodielom.getObcan().pridajAleboPonechajListVlastnictva(inyListVlastnictva);
        }
        this.vlastniciSPodielom_.clear();
        this.nehnutelnostiNaListeVlastnictva_.clear();
        return true;
    }

    // metoda zmeni katastralne uzemia a zaroven presunie vlastnikom listy vlastnictva z 1 uzemia do druheho, konflikty nebudu
    public boolean zmenKatastralneUzemie(KatastralneUzemie katastralneUzemie) {
        boolean zmenene = false;
        KatastralneUzemie katastralneUzemiePovodne = this.katastralneUzemie_;
        this.katastralneUzemie_ = katastralneUzemie;
        for (ObcanSPodielom obcanSPodielom: vlastniciSPodielom_) {
            zmenene = obcanSPodielom.getObcan().zmenCisloKatastralnehoUzemiaPreListyVlastnictva(katastralneUzemiePovodne.getCisloKatastralnehoUzemia(), this.katastralneUzemie_.getCisloKatastralnehoUzemia());
            if (!zmenene) {
                return false;
            }
        }

        return true;
    }


}
