package InformacnySystem;

import Model.KatastralneUzemie;
import Model.ListVlastnictva;
import Model.Nehnutelnost;
import Model.Obcan;
import Utils.Helper;
import Utils.Status;
import structures.AvlTree;

import javax.xml.ws.Holder;
import java.util.Comparator;
import java.util.Optional;


public class ISSpravyKatastra {

    AvlTree<Obcan> obcania_;
    AvlTree<KatastralneUzemie> katastralneUzemieCislo_;
    AvlTree<KatastralneUzemie> katastralneUzemieNazov_;


    Obcan dummyObcan = new Obcan();
    KatastralneUzemie dummyKatastralneUzemie = new KatastralneUzemie();
    Nehnutelnost dummyNehnutelnost = new Nehnutelnost();

    public ISSpravyKatastra() {
        this.obcania_ = new AvlTree<>(Comparator.comparing(Obcan::getRodneCislo));
        this.katastralneUzemieCislo_ = new AvlTree<>(Comparator.comparing(KatastralneUzemie::getCisloKatastralnehoUzemia));
        this.katastralneUzemieNazov_ = new AvlTree<>(Comparator.comparing(KatastralneUzemie::getNazov));
        pridajObcana("Andrej Beliancin", "1111111111111111", Helper.GetNahodnyDatumNarodenia());
        pridajObcana("Gabriela Beliancinova", "1111111111111112", Helper.GetNahodnyDatumNarodenia());

        pridajKatastralneUzemie(1, "Terchova");
        pridajKatastralneUzemie(2, "Bela");
        pridajKatastralneUzemie(3, "Krasnany");

        pridajListVlastnictva("Terchova", 1);
        pridajListVlastnictva("Terchova", 2);
        pridajListVlastnictva("Terchova", 3);
        pridajListVlastnictva("Bela", 1);
        pridajListVlastnictva("Bela", 2);
        pridajListVlastnictva("Bela", 3);

        pridajNehnutelnostNaListVlastnictva(1, 1, 1, "Tarchava 1", "Popis 1");
        pridajNehnutelnostNaListVlastnictva(1, 1, 2, "Tarchava 2", "Popis 2");
        pridajNehnutelnostNaListVlastnictva(1, 1, 3, "Tarchava 3", "Popis 3");

        pridajNehnutelnostNaListVlastnictva(1, 1, 1, "Bela 1", "Popis 1");
        pridajNehnutelnostNaListVlastnictva(2, 1, 2, "Bela 2", "Popis 2");
        pridajNehnutelnostNaListVlastnictva(3, 1, 3, "Bela 3", "Popis 3");

        zapisObcanoviTrvalyPobyt("Terchova", 1, "1111111111111111");
        zapisObcanoviTrvalyPobyt("Bela", 1, "1111111111111112");

    }


    // 7
    public AvlTree<Nehnutelnost> getNehnutelnostiKatastralnehoUzemia(String nazovKatastralnehoUzemia) {
        dummyKatastralneUzemie.setNazov(nazovKatastralnehoUzemia);
        KatastralneUzemie katastralneUzemie = katastralneUzemieNazov_.findData(dummyKatastralneUzemie);
        if (katastralneUzemie != null) {
            return katastralneUzemie.getNehnutelnostiVKatastralnomUzemi();
        }
        return null;
    }

    // C10
    public boolean zapisObcanoviTrvalyPobyt(String nazovKatastralnehoUzemia, long supisneCisloNehnutelnosti, String rodneCislo) {
        dummyKatastralneUzemie.setNazov(nazovKatastralnehoUzemia);
        dummyObcan.setRodneCislo(rodneCislo);
        Obcan obcan = obcania_.findData(dummyObcan);
        if (obcan != null) {
            KatastralneUzemie katastralneUzemie = katastralneUzemieNazov_.findData(dummyKatastralneUzemie);
            if (katastralneUzemie != null) {
                Nehnutelnost nehnutelnost = katastralneUzemie.getNehnutelnostVKatastralnomUzemi(supisneCisloNehnutelnosti);
                if (nehnutelnost != null) {
                    Nehnutelnost aktualnyTrvalyPobytObcana = obcan.getTrvalyPobyt();
                    if (aktualnyTrvalyPobytObcana != null) {
                        nehnutelnost.odstranObcanaSTrvalymPobytom(obcan);
                    }
                    nehnutelnost.pridajObcanaSTrvalymPobytom(obcan);
                    obcan.setTrvalyPobyt(nehnutelnost);
                    return true;
                }
            }
        }
        return false;
    }

    // 12
    public ListVlastnictva upravMajetkovyPodielNaListeVlastnictva(long cisloKatastralnehoUzemia, long cisloListuVlastnictva, String rodneCislo) {
        dummyObcan.setRodneCislo(rodneCislo);
        Obcan obcan = obcania_.findData(dummyObcan);
        if (obcan != null) {
            dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemia);
            KatastralneUzemie katastralneUzemie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
            if (katastralneUzemie != null) {
                ListVlastnictva listVlastnictva = katastralneUzemie.najdiListVlastnictva(cisloListuVlastnictva);
                if (listVlastnictva != null) {
                    listVlastnictva.pridajAleboPonechajVlastnika(obcan);
                    obcan.pridajAleboPonechajListVlastnictva(listVlastnictva);
                    return listVlastnictva;
                }
            }
        }
        return null;
    }


    // 15
    public AvlTree<KatastralneUzemie> getKatastralneUzemieUtriedenychPodlaNapisu() {
        return katastralneUzemieNazov_;
    }

    // 16
    public boolean pridajObcana(String menoAPriezvisko, String rodneCislo, long datumNarodenia) {
        return pridajObcana(menoAPriezvisko, rodneCislo, datumNarodenia, Optional.empty());
    }

    private boolean pridajObcana(String menoAPriezvisko, String rodneCislo, long datumNarodenia, Optional<Holder<Obcan>> vlozenyObcan) {
        Obcan obcan = new Obcan(menoAPriezvisko, rodneCislo, datumNarodenia);
        boolean inserted = this.obcania_.insert(obcan);
        vlozenyObcan.ifPresent(obcanHolder -> obcanHolder.value = inserted ? obcan : null);
        return inserted;
    }

    // 17

    public boolean pridajListVlastnictva(String nazovKatastralnehoUzemia, long cisloListuVlastnictva) {
        return pridajListVlastnictva(nazovKatastralnehoUzemia, cisloListuVlastnictva, Optional.empty());
    }


    private boolean pridajListVlastnictva(String nazovKatastralnehoUzemia, long cisloListuVlastnictva, Optional<Holder<ListVlastnictva>> vlozenyListVlastnictva) {
        dummyKatastralneUzemie.setNazov(nazovKatastralnehoUzemia);
        KatastralneUzemie hladaneKatastralneUzemie = katastralneUzemieNazov_.findData(dummyKatastralneUzemie);
        if (hladaneKatastralneUzemie == null) {
            vlozenyListVlastnictva.ifPresent(listVlastnictvaHolder -> listVlastnictvaHolder.value = null);
            return false;
        }
        return  hladaneKatastralneUzemie.vlozListVlastnictva(cisloListuVlastnictva, vlozenyListVlastnictva);
    }

    // 18
    public boolean pridajNehnutelnostNaListVlastnictva(long cisloKatastralnehoUzemia, long cisloListuVlastnictva, long supisneCisloNehnutelnosti, String adresaNehnutelnosti, String popisNehnutelnosti) {
        return pridajNehnutelnostNaListVlastnictva(cisloKatastralnehoUzemia, cisloListuVlastnictva, supisneCisloNehnutelnosti, adresaNehnutelnosti, popisNehnutelnosti, Optional.empty());
    }

    public boolean pridajNehnutelnostNaListVlastnictva(long cisloKatastralnehoUzemia, long cisloListuVlastnictva, long supisneCisloNehnutelnosti, String adresaNehnutelnosti, String popisNehnutelnosti, Optional<Holder<Nehnutelnost>> vlozenaNehnutelnost) {
        dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemia);
        KatastralneUzemie katastralneUzemie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
        if (katastralneUzemie != null) {
            return katastralneUzemie.vlozNehnutelnostNaListVlastnictva(cisloListuVlastnictva, supisneCisloNehnutelnosti, adresaNehnutelnosti, popisNehnutelnosti, vlozenaNehnutelnost);
        }
        vlozenaNehnutelnost.ifPresent(nehnutelnostHolder -> nehnutelnostHolder.value = null);
        return false;
    }

    // 21
    public boolean pridajKatastralneUzemie(long cisloKatastralnehoUzemia, String nazovKatastralnehoUzemia) {
        return pridajKatastralneUzemie(cisloKatastralnehoUzemia, nazovKatastralnehoUzemia, Optional.empty());
    }

    private boolean pridajKatastralneUzemie(long cisloKatastralnehoUzemia, String nazovKatastralnehoUzemia, Optional<Holder<KatastralneUzemie>> vlozeneKatastralneUzemie) {
        KatastralneUzemie katastralneUzemie = new KatastralneUzemie(cisloKatastralnehoUzemia, nazovKatastralnehoUzemia);
        boolean inserted = katastralneUzemieCislo_.insert(katastralneUzemie);
        if (!inserted) {
            return false;
        }
        inserted = katastralneUzemieNazov_.insert(katastralneUzemie);
        if (!inserted) {
            katastralneUzemieCislo_.remove(katastralneUzemie);
            vlozeneKatastralneUzemie.ifPresent(katastralneUzemieHolder -> katastralneUzemieHolder.value = null);
            return false;
        }
        vlozeneKatastralneUzemie.ifPresent(katastralneUzemieHolder -> katastralneUzemieHolder.value = katastralneUzemie);
        return true;
    }

    public boolean generujData(int pocetKatastralnychUzemi, int celkovyPocetObcanov, int pocetListovVlastnictvaVKatastralnomUzemi, int pocetVlastnikovNaListeVlastnictva, int pocetNehnutelnostiNaListeVlastnictva) {
        boolean inserted = false;

        this.obcania_.clear();
        this.katastralneUzemieNazov_.clear();
        this.katastralneUzemieCislo_.clear();
        Optional<Holder<KatastralneUzemie>> katastralneUzemieHolder = Optional.of(new Holder<>());
        Optional<Holder<Nehnutelnost>> nehnutelnostHolder = Optional.of(new Holder<>());
        Optional<Holder<ListVlastnictva>> listVlastnictvaHolder = Optional.of(new Holder<>());

        for (int indexKatastralneUzemie = 1; indexKatastralneUzemie <= pocetKatastralnychUzemi; indexKatastralneUzemie++) {
            inserted = pridajKatastralneUzemie(indexKatastralneUzemie, Helper.GetNahodnyNazovKatastralnehoUzemie() + indexKatastralneUzemie, katastralneUzemieHolder);
            if (!inserted) {
                return false;
            }
            KatastralneUzemie katastralneUzemie = katastralneUzemieHolder.get().value;
            for (int indexPocetListovVlastnictvaVkatastralnomUzemi = 1; indexPocetListovVlastnictvaVkatastralnomUzemi <= pocetListovVlastnictvaVKatastralnomUzemi; indexPocetListovVlastnictvaVkatastralnomUzemi++) {
                inserted = katastralneUzemie.vlozListVlastnictva(indexPocetListovVlastnictvaVkatastralnomUzemi, listVlastnictvaHolder);
                if (!inserted) {
                    return false;
                }
                ListVlastnictva listVlastnictva = listVlastnictvaHolder.get().value;
                for (int indexPocetNehnutelnostinaListeVlastnictva = 1; indexPocetNehnutelnostinaListeVlastnictva <= pocetNehnutelnostiNaListeVlastnictva; indexPocetNehnutelnostinaListeVlastnictva++) {
                    long supisneCislo = indexPocetListovVlastnictvaVkatastralnomUzemi * indexPocetNehnutelnostinaListeVlastnictva;
                    inserted = katastralneUzemie.vlozNehnutelnostNaListVlastnictva(listVlastnictva, supisneCislo, Helper.GetNahodnaAdresa(), Helper.GetNahodnyPopis(), nehnutelnostHolder);
                }
                if (!inserted) {
                    return false;
                }
            }
        }

        for (int indexPocetObcanov = 0; indexPocetObcanov < celkovyPocetObcanov; indexPocetObcanov++) {
            inserted = pridajObcana(Helper.GetNahodneMenoAPriezvisko(), Helper.GetNahodneRodneCislo(), Helper.GetNahodnyDatumNarodenia());;
            if (!inserted) {
                indexPocetObcanov--;
            }
        }
        return true;
    }



    private boolean skontrolujVkladanieKatastralnehoUzemia(Optional<Status> status, KatastralneUzemie katastralneUzemie, boolean result) {
        if (!result) {
            status.ifPresent(s -> s.setStatus_(false));
            status.ifPresent(s -> s.setMessage("Nepodarilo sa vložiť katastrálne územie: " + katastralneUzemie) );
        }
        return result;
    }

    private boolean skontrolujVkladanieObcana(Optional<Status> status, Obcan obcan, boolean result) {
        if (!result) {
            status.ifPresent(s -> s.setStatus_(false));
            status.ifPresent(s -> s.setMessage("Nepodarilo sa vložiť občana: " + obcan) );
        }
        return result;
    }

}
