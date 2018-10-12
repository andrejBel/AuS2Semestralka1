package Model;

import structures.AvlTree;

import javax.xml.ws.Holder;
import java.util.Comparator;
import java.util.Optional;

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
    private AvlTree<Nehnutelnost> nehnutelnostiNaListeVlastnictva_;
    private AvlTree<ObcanSPodielom> vlastniciSPodielom_;

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
        return pridajAleboPonechajVlastnika(obcan, 0.0);
    }

    public boolean pridajAleboPonechajVlastnika(Obcan obcan, double podiel) {
        ObcanSPodielom obcanNaPridanie = new ObcanSPodielom(obcan, podiel);
        boolean inserted = vlastniciSPodielom_.insert(obcanNaPridanie);
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
                povodnyMajitel.odstranListVlastnictva(this);
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
        return false;
    }

    public boolean presunVsetokObsahNaInyListVlastnictva(ListVlastnictva inyListVlastnictva) {
        boolean inserted = false;
        for (Nehnutelnost nehnutelnost: nehnutelnostiNaListeVlastnictva_) {
            nehnutelnost.setListVlastnictva(inyListVlastnictva);
            inserted = inyListVlastnictva.vlozNehnutelnostNaListVlastnictva(nehnutelnost);
            if (!inserted) {
                System.out.println("tu by si nemal prist");
                return false;
            }
        }
        for (ObcanSPodielom obcanSPodielom : vlastniciSPodielom_) {

            if (inyListVlastnictva.vlastniciSPodielom_.getSize() > 0) {
                // mohlo dojst k mergu, nastavim podiel na 0
                obcanSPodielom.setPodiel(0.0);
            }
            inyListVlastnictva.vlastniciSPodielom_.insert(obcanSPodielom);
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
            zmenene = obcanSPodielom.getObcan().zmenCisloKatastralnehoUzemiaPreListyVlastnictva(katastralneUzemiePovodne.getCisloKatastralnehoUzemia(), katastralneUzemie.getCisloKatastralnehoUzemia());
            if (!zmenene) {
                return false;
            }
        }

        return true;
    }


}
