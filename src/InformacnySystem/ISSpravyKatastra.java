package InformacnySystem;

import Model.KatastralneUzemie;
import Model.ListVlastnictva;
import Model.Nehnutelnost;
import Model.Obcan;
import Utils.Helper;
import Utils.Pair;
import Structures.AvlTree;

import javax.xml.ws.Holder;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;


public class ISSpravyKatastra {

    private final AvlTree<Obcan> obcania_;
    private final AvlTree<KatastralneUzemie> katastralneUzemieCislo_;
    private final AvlTree<KatastralneUzemie> katastralneUzemieNazov_;

    private final Obcan dummyObcan = new Obcan();
    private final KatastralneUzemie dummyKatastralneUzemie = new KatastralneUzemie();
    private final Nehnutelnost dummyNehnutelnost = new Nehnutelnost();

    private static final char DEFAULT_SEPARATOR = ';';

    public ISSpravyKatastra() {
        this.obcania_ = new AvlTree<>((o1, o2) -> o1.getRodneCislo().compareTo(o2.getRodneCislo()));
        this.katastralneUzemieCislo_ = new AvlTree<>(Comparator.comparingLong(KatastralneUzemie::getCisloKatastralnehoUzemia));
        this.katastralneUzemieNazov_ = new AvlTree<>((o1, o2) -> o1.getNazov().compareTo(o2.getNazov()));
        pridajObcana("Andrej Beliancin", "1111111111111111", Helper.GetNahodnyDatumNarodenia());
        pridajObcana("Gabriela Beliancinova", "1111111111111112", Helper.GetNahodnyDatumNarodenia());
        pridajObcana("Mirka Beliancinova", "1111111111111113", Helper.GetNahodnyDatumNarodenia());


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

        pridajNehnutelnostNaListVlastnictva(1, 2, 4, "Tarchava 4", "Popis 1");
        pridajNehnutelnostNaListVlastnictva(1, 2, 5, "Tarchava 5", "Popis 2");
        pridajNehnutelnostNaListVlastnictva(1, 2, 6, "Tarchava 6", "Popis 3");

        pridajNehnutelnostNaListVlastnictva(2, 1, 1, "Bela 1", "Popis 1");
        pridajNehnutelnostNaListVlastnictva(2, 1, 2, "Bela 2", "Popis 2");
        pridajNehnutelnostNaListVlastnictva(2, 1, 3, "Bela 3", "Popis 3");


        zapisObcanoviTrvalyPobyt("Terchova", 1, "1111111111111111");
        zapisObcanoviTrvalyPobyt("Bela", 1, "1111111111111112");

        upravMajetkovyPodielNaListeVlastnictva(1,1, "1111111111111111");
        upravMajetkovyPodielNaListeVlastnictva(1,2, "1111111111111111");
        upravMajetkovyPodielNaListeVlastnictva(1,2, "1111111111111112");

        //exportujData("exportPred.csv");
        //boolean presun = odstranAPresunKatastralneUzemie(1, 2, "Ide to");

        //System.out.println(presun ? " presun ok" : "presun nie OK!!!!!!");
        //importujData("Export/skutocnetest.csv");
    }

    // 1
    public Nehnutelnost najdiNehnutelnostVKU(long cisloKatastralnehoUzemia, long supisneCisloNehnutelnosti) {
        dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemia);
        KatastralneUzemie katastralneUzemie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
        if (katastralneUzemie != null) {
            return katastralneUzemie.najdiNehnutelnostVkatastralnomUzemi(supisneCisloNehnutelnosti);
        }
        return null;
    }

    // 2
    // pouziva najdi obcana a obcan ma atribut nehnutelnost

    // 3
    public Nehnutelnost najdiNehnutelnost(long cisloKatastralnehoUzemia, long cisloListuVlastnictva, long supisneCisloNehnutelnosti) {
        dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemia);
        KatastralneUzemie katastralneUzemie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
        if (katastralneUzemie != null) {
            ListVlastnictva listVlastnictva = katastralneUzemie.najdiListVlastnictva(cisloListuVlastnictva);
            if (listVlastnictva != null) {
                dummyNehnutelnost.setSupisneCislo(supisneCisloNehnutelnosti);
                return listVlastnictva.getNehnutelnostiNaListeVlastnictva().findData(dummyNehnutelnost);
            }
        }
        return null;
    }

    // 4
    public ListVlastnictva najdiListVlastnictva(long cisloKatastralnehoUzemia, long cisloListuVlastnictva) {
        dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemia);
        KatastralneUzemie katastralneUzemie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
        if (katastralneUzemie != null) {
            return katastralneUzemie.najdiListVlastnictva(cisloListuVlastnictva);
        }
        return null;
    }

    // 5
    public Nehnutelnost najdiNehnutelnostVKU(String nazovKatastralnehoUzemia, long supisneCisloNehnutelnosti) {
        dummyKatastralneUzemie.setNazov(nazovKatastralnehoUzemia);
        KatastralneUzemie katastralneUzemie = katastralneUzemieNazov_.findData(dummyKatastralneUzemie);
        if (katastralneUzemie != null) {
            return katastralneUzemie.najdiNehnutelnostVkatastralnomUzemi(supisneCisloNehnutelnosti);
        }
        return null;
    }

    // 6
    public ListVlastnictva najdiListVlastnictva(String nazovKatastralnehoUzemia, long cisloListuVlastnictva) {
        dummyKatastralneUzemie.setNazov(nazovKatastralnehoUzemia);
        KatastralneUzemie katastralneUzemie = katastralneUzemieNazov_.findData(dummyKatastralneUzemie);
        if (katastralneUzemie != null) {
            return katastralneUzemie.najdiListVlastnictva(cisloListuVlastnictva);
        }
        return null;
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

    // 8
    public Pair<Obcan, AvlTree<ListVlastnictva>> getObcanoveListyVlastnictvaPodlaKatastralnehoUzemia(long cisloKatastralnehoUzemia, String rodneCislo) {
        dummyObcan.setRodneCislo(rodneCislo);
        Obcan obcan = obcania_.findData(dummyObcan);
        if (obcan != null) {
            return new Pair<>(obcan, obcan.dajVsetkyListyVlastnictvaVkatastralnomUzemi(cisloKatastralnehoUzemia));
        }
        return null;
    }

    // 9
    public Obcan najdiObcana(String rodneCislo) {
        dummyObcan.setRodneCislo(rodneCislo);
        return obcania_.findData(dummyObcan);
    }

    // 10
    public boolean zapisObcanoviTrvalyPobyt(String nazovKatastralnehoUzemia, long supisneCisloNehnutelnosti, String rodneCislo) {
        dummyKatastralneUzemie.setNazov(nazovKatastralnehoUzemia);
        dummyObcan.setRodneCislo(rodneCislo);
        Obcan obcan = obcania_.findData(dummyObcan);
        if (obcan != null) {
            KatastralneUzemie katastralneUzemie = katastralneUzemieNazov_.findData(dummyKatastralneUzemie);
            if (katastralneUzemie != null) {
                Nehnutelnost nehnutelnost = katastralneUzemie.getNehnutelnostVKatastralnomUzemi(supisneCisloNehnutelnosti);
                if (nehnutelnost != null) {
                    return zapisObcanoviTrvalyPobyt(obcan, nehnutelnost);
                }
            }
        }
        return false;
    }

    private boolean zapisObcanoviTrvalyPobyt(Obcan obcan, Nehnutelnost nehnutelnost)
    {
        Nehnutelnost aktualnyTrvalyPobytObcana = obcan.getTrvalyPobyt();
        if (aktualnyTrvalyPobytObcana != null) {
            aktualnyTrvalyPobytObcana.odstranObcanaSTrvalymPobytom(obcan);
        }
        nehnutelnost.pridajObcanaSTrvalymPobytom(obcan);
        obcan.setTrvalyPobyt(nehnutelnost);
        return true;
    }

    // 11
    public boolean zmenaMajitelaNehnutelnosti(long cisloKatastralnehoUzemia, long supisneCisloNehnutelnosti, String povodnymajitelRC, String novyMajitelRC) {
        if (!povodnymajitelRC.equals(novyMajitelRC)) {
            Obcan povodnyMajitel = najdiObcana(povodnymajitelRC);
            if (povodnyMajitel != null) {
                Obcan novyMajitel = najdiObcana(novyMajitelRC);
                if (novyMajitel != null) {
                    dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemia);
                    KatastralneUzemie katastralneUzemie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
                    if (katastralneUzemie != null) {
                        Nehnutelnost nehnutelnost = katastralneUzemie.najdiNehnutelnostVkatastralnomUzemi(supisneCisloNehnutelnosti);
                        if (nehnutelnost != null) {
                            ListVlastnictva listVlastnictva = nehnutelnost.getListVlastnictva();
                            return listVlastnictva.zmenVlastnikaNaListeVlastnictva(povodnyMajitel, novyMajitel);
                        }
                    }
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

    // 13
    public ListVlastnictva odstranMajetkovyPodielNaListeVlastnictva(long cisloKatastralnehoUzemia, long cisloListuVlastnictva, String rodneCislo) {
        dummyObcan.setRodneCislo(rodneCislo);
        Obcan obcan = obcania_.findData(dummyObcan);
        if (obcan != null) {
            dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemia);
            KatastralneUzemie katastralneUzemie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
            if (katastralneUzemie != null) {
                ListVlastnictva listVlastnictva = katastralneUzemie.najdiListVlastnictva(cisloListuVlastnictva);
                if (listVlastnictva != null) {
                    boolean odstranenyVlastnik = listVlastnictva.odstranVlastnika(obcan);
                    if (odstranenyVlastnik) {
                        return obcan.odstranListVlastnictva(listVlastnictva) ? listVlastnictva : null;
                    }
                }
            }
        }
        return null;
    }


    // 15
    public AvlTree<KatastralneUzemie> getKatastralneUzemieUtriedenychPodlaNazvu() {
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

    private boolean pridajNehnutelnostNaListVlastnictva(long cisloKatastralnehoUzemia, long cisloListuVlastnictva, long supisneCisloNehnutelnosti, String adresaNehnutelnosti, String popisNehnutelnosti, Optional<Holder<Nehnutelnost>> vlozenaNehnutelnost) {
        dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemia);
        KatastralneUzemie katastralneUzemie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
        if (katastralneUzemie != null) {
            return katastralneUzemie.vlozNehnutelnostNaListVlastnictva(cisloListuVlastnictva, supisneCisloNehnutelnosti, adresaNehnutelnosti, popisNehnutelnosti, vlozenaNehnutelnost);
        }
        vlozenaNehnutelnost.ifPresent(nehnutelnostHolder -> nehnutelnostHolder.value = null);
        return false;
    }

    // 19
    public ListVlastnictva odstranAPresunListVlatnictva(long cisloKatastralnehoUzemia, long cisloListuVlastnictvaOdstraneneho, long cisloListuVlastnictvaNoveho) {
        if (cisloListuVlastnictvaOdstraneneho != cisloListuVlastnictvaNoveho) {
            dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemia);
            KatastralneUzemie katastralneUzemie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
            if (katastralneUzemie != null) {
                ListVlastnictva listVlastnictvaNaOdstranenie = katastralneUzemie.najdiListVlastnictva(cisloListuVlastnictvaOdstraneneho);
                if (listVlastnictvaNaOdstranenie != null) {
                    ListVlastnictva listVlastnictvaNove = katastralneUzemie.najdiListVlastnictva(cisloListuVlastnictvaNoveho);
                    if (listVlastnictvaNove == null) {
                        Optional<Holder<ListVlastnictva>> listVlastnictvaHolder = Optional.of(new Holder<>());
                        boolean inserted = katastralneUzemie.vlozListVlastnictva(cisloListuVlastnictvaNoveho, listVlastnictvaHolder);
                        if (!inserted) {
                            return null;
                        }
                        listVlastnictvaNove = listVlastnictvaHolder.get().value;
                    }
                    boolean status = listVlastnictvaNaOdstranenie.presunVsetokObsahNaInyListVlastnictva(listVlastnictvaNove);
                    if (status) {
                        status = katastralneUzemie.odstranListVlastnictva(listVlastnictvaNaOdstranenie);
                        if (status) {
                            return listVlastnictvaNove;
                        }
                    }
                }
            }
        }
        return null;
    }

    // 20
    public boolean odstranNehnutelnost(long cisloKatastralnehoUzemia, long cisloListuVlastnictva,long supisneCisloNehnutelnosti) {
        dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemia);
        KatastralneUzemie katastralneUzemie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
        if (katastralneUzemie != null) {
            Nehnutelnost nehnutelnost = katastralneUzemie.najdiNehnutelnostVkatastralnomUzemi(supisneCisloNehnutelnosti);
            if (nehnutelnost != null) {
                ListVlastnictva listVlastnictva = nehnutelnost.getListVlastnictva();
                if (listVlastnictva.getCisloListuVlastnictva() == cisloListuVlastnictva) {
                    listVlastnictva.odstranNehnutelnostZListuVlastnictva(supisneCisloNehnutelnosti);
                    nehnutelnost.odstranObcanomTrvalyPobytVNehnutelnosti();
                    return katastralneUzemie.odstranNehnutelnostZKU(nehnutelnost) != null;
                }
            }
        }
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

    // 22
    public boolean odstranAPresunKatastralneUzemie(long cisloKatastralnehoUzemiaOdstraneneho, long cisloKatastralnehoUzemiaNoveho, String nazovNovehoKatastralnehoUzemia) {
        if (cisloKatastralnehoUzemiaOdstraneneho != cisloKatastralnehoUzemiaNoveho) {
            dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemiaOdstraneneho);
            KatastralneUzemie katastralneUzemienaOdstranenie = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
            if (katastralneUzemienaOdstranenie != null) {
                dummyKatastralneUzemie.setCisloKatastralnehoUzemia(cisloKatastralnehoUzemiaNoveho);
                KatastralneUzemie katastralneUzemieNove = katastralneUzemieCislo_.findData(dummyKatastralneUzemie);
                if (katastralneUzemieNove == null) {
                    Optional<Holder<KatastralneUzemie>> katastralneUzemieHolder = Optional.of(new Holder<>());
                    boolean inserted = pridajKatastralneUzemie(cisloKatastralnehoUzemiaNoveho, nazovNovehoKatastralnehoUzemia, katastralneUzemieHolder);
                    if (!inserted) {
                        return false;
                    }
                    katastralneUzemieNove = katastralneUzemieHolder.get().value;
                }
                // metoda na presun
                boolean moved = katastralneUzemienaOdstranenie.presunVsetkoDoInehoKatastralnehoUzemia(katastralneUzemieNove);
                if (moved) {
                    return katastralneUzemieCislo_.remove(katastralneUzemienaOdstranenie) != null && katastralneUzemieNazov_.remove(katastralneUzemienaOdstranenie) != null;
                }
            }
        }
        return false;
    }

    private void vycistiData() {
        this.obcania_.clear();
        this.katastralneUzemieNazov_.clear();
        this.katastralneUzemieCislo_.clear();
    }

    public boolean generujData(int pocetKatastralnychUzemi, int celkovyPocetObcanov, int pocetObyvatelovStrvalymPobytom, int pocetListovVlastnictvaVKatastralnomUzemi, int pocetVlastnikovNaListeVlastnictva, int pocetNehnutelnostiNaListeVlastnictva) {
        boolean inserted = false;
        vycistiData();

        ArrayList<Obcan> vlozeniObcania = new ArrayList<>(celkovyPocetObcanov);
        ArrayList<Nehnutelnost> vlozeneNehnutelnosti = new ArrayList<>(pocetObyvatelovStrvalymPobytom);
        Optional<Holder<KatastralneUzemie>> katastralneUzemieHolder = Optional.of(new Holder<>());
        Optional<Holder<Nehnutelnost>> nehnutelnostHolder = Optional.of(new Holder<>());
        Optional<Holder<ListVlastnictva>> listVlastnictvaHolder = Optional.of(new Holder<>());
        Optional<Holder<Obcan>> obcanHolder = Optional.of(new Holder<>());
        Helper.ResetRodneCisloGenerator();

        for (int indexPocetObcanov = 0; indexPocetObcanov < celkovyPocetObcanov; indexPocetObcanov++) {
            inserted = pridajObcana(Helper.GetNahodneMenoAPriezvisko(), Helper.GetRodneCisloSoSekvencie(), Helper.GetNahodnyDatumNarodenia(), obcanHolder);
            vlozeniObcania.add(obcanHolder.get().value);
            if (!inserted) {
                indexPocetObcanov--;
            }
        }

        for (int indexKatastralneUzemie = 1; indexKatastralneUzemie <= pocetKatastralnychUzemi; indexKatastralneUzemie++) {
            inserted = pridajKatastralneUzemie(indexKatastralneUzemie, Helper.GetNahodnyNazovKatastralnehoUzemie() + indexKatastralneUzemie, katastralneUzemieHolder);
            if (!inserted) {
                return false;
            }
            KatastralneUzemie katastralneUzemie = katastralneUzemieHolder.get().value;
            long posledneSupisneCislo = 1;
            int randomPocetListovVlastnictvaVKatastralnomUzemi = Helper.GetNahodneCislo(pocetListovVlastnictvaVKatastralnomUzemi);
            for (int indexPocetListovVlastnictvaVkatastralnomUzemi = 1; indexPocetListovVlastnictvaVkatastralnomUzemi <= randomPocetListovVlastnictvaVKatastralnomUzemi; indexPocetListovVlastnictvaVkatastralnomUzemi++) {
                inserted = katastralneUzemie.vlozListVlastnictva(indexPocetListovVlastnictvaVkatastralnomUzemi, listVlastnictvaHolder);
                //System.out.println("    Cislo listu vlastnictva: " + indexPocetListovVlastnictvaVkatastralnomUzemi);
                if (!inserted) {
                    return false;
                }
                ListVlastnictva listVlastnictva = listVlastnictvaHolder.get().value;
                int randomPocetNehnutelnostiNaListeVlastnictva = Helper.GetNahodneCislo(pocetNehnutelnostiNaListeVlastnictva);
                for (int indexPocetNehnutelnostinaListeVlastnictva = 1; indexPocetNehnutelnostinaListeVlastnictva <= randomPocetNehnutelnostiNaListeVlastnictva; indexPocetNehnutelnostinaListeVlastnictva++) {
                    inserted = katastralneUzemie.vlozNehnutelnostNaListVlastnictva(listVlastnictva, posledneSupisneCislo++, Helper.GetNahodnaAdresa(), Helper.GetNahodnyPopis(), nehnutelnostHolder);
                    if (vlozeneNehnutelnosti.size() < pocetObyvatelovStrvalymPobytom) {
                        vlozeneNehnutelnosti.add(nehnutelnostHolder.get().value);
                    }
                    if (!inserted) {
                        return false;
                    }

                }
                if (vlozeniObcania.size() > 0 && pocetVlastnikovNaListeVlastnictva > 0) {
                    int randomPocetVlastnikovNaListeVlastnictva = Helper.GetNahodneCislo(pocetVlastnikovNaListeVlastnictva);
                    if (randomPocetVlastnikovNaListeVlastnictva == 0) {
                        randomPocetVlastnikovNaListeVlastnictva = 1;
                    }
                    int priemernyPodiel = 100 / randomPocetVlastnikovNaListeVlastnictva;
                    int celkovyPodiel = 100;
                    for (int ndexPocetVlastnikovNaListeVlastnictva = 1; ndexPocetVlastnikovNaListeVlastnictva <= randomPocetVlastnikovNaListeVlastnictva ; ndexPocetVlastnikovNaListeVlastnictva++) {
                        int diferencia =  Helper.GetNahodneCislo( priemernyPodiel / 2);
                        diferencia *= Helper.GetNahodneCislo() % 2 == 0 ? -1 : 1;
                        int vyslednyPodiel = priemernyPodiel + diferencia;

                        if (ndexPocetVlastnikovNaListeVlastnictva == randomPocetVlastnikovNaListeVlastnictva) {
                            vyslednyPodiel = celkovyPodiel;
                        } else {
                            celkovyPodiel -= vyslednyPodiel;
                        }
                        int nahodnyIndexObcan = Helper.GetNahodneCislo(celkovyPocetObcanov -1);
                        vlozeniObcania.get(nahodnyIndexObcan).pridajAleboPonechajListVlastnictva(listVlastnictva);
                        listVlastnictva.pridajAleboPonechajVlastnika(vlozeniObcania.get(nahodnyIndexObcan), vyslednyPodiel);
                    }
                }
            }
        }

        if (vlozeniObcania.size() > 0 && pocetObyvatelovStrvalymPobytom > 0 && vlozeneNehnutelnosti.size() > 0) {
            int kolkymPriraditPobyt = pocetObyvatelovStrvalymPobytom > vlozeniObcania.size() ? vlozeniObcania.size() : pocetObyvatelovStrvalymPobytom;
            int indexNehnutelnosti = 0;
            for (int indexRadeneho = 0; indexRadeneho < kolkymPriraditPobyt; indexRadeneho++) {
                Nehnutelnost nehnutelnost = vlozeneNehnutelnosti.get(indexNehnutelnosti);
                Obcan obcan = vlozeniObcania.get(indexRadeneho);
                nehnutelnost.pridajObcanaSTrvalymPobytom(obcan);
                obcan.setTrvalyPobyt(nehnutelnost);
                ++indexNehnutelnosti;
                if (indexNehnutelnosti == vlozeneNehnutelnosti.size()) {
                    indexNehnutelnosti = 0;
                }
            }
        }
        return true;
    }


    public boolean exportujData(String cestaKSuboru) {

        try (PrintWriter writer = new PrintWriter(cestaKSuboru)) {
            StringBuilder stringBuilder = new StringBuilder();
            writer.println("Pocet obcannov: " + DEFAULT_SEPARATOR + obcania_.getSize() + DEFAULT_SEPARATOR);
            Iterator<Obcan> obcanLevelOrderIterator = obcania_.levelOrderIterator();
            while (obcanLevelOrderIterator.hasNext()) {
                Obcan obcan = obcanLevelOrderIterator.next();
                stringBuilder.append(obcan.getRodneCislo());
                stringBuilder.append(DEFAULT_SEPARATOR);
                stringBuilder.append(obcan.getMenoPriezvisko());
                stringBuilder.append(DEFAULT_SEPARATOR);
                stringBuilder.append(obcan.getDatumNarodenia());
                stringBuilder.append(DEFAULT_SEPARATOR);
                writer.println(stringBuilder.toString());
                stringBuilder.setLength(0); // vycistenie
            }

            writer.println("Pocet katastralnych uzemi: " + DEFAULT_SEPARATOR + katastralneUzemieCislo_.getSize() + DEFAULT_SEPARATOR);

            Iterator<KatastralneUzemie> katastralneUzemieLevelOrderIterator = katastralneUzemieCislo_.levelOrderIterator();
            while (katastralneUzemieLevelOrderIterator.hasNext()) {
                KatastralneUzemie katastralneUzemie = katastralneUzemieLevelOrderIterator.next();

                stringBuilder.append(katastralneUzemie.getCisloKatastralnehoUzemia());
                stringBuilder.append(DEFAULT_SEPARATOR);
                stringBuilder.append(katastralneUzemie.getNazov());
                stringBuilder.append(DEFAULT_SEPARATOR);
                writer.println(stringBuilder.toString());
                stringBuilder.setLength(0);
                AvlTree<ListVlastnictva> listyVlastnictva = katastralneUzemie.getListyVlastnictvaVKatastralnomUzemi();
                writer.println("Pocet listov vlastnictva v KU: " + DEFAULT_SEPARATOR + listyVlastnictva.getSize() + DEFAULT_SEPARATOR);
                Iterator<ListVlastnictva> listyVlastnictvaIterator = listyVlastnictva.levelOrderIterator();
                while (listyVlastnictvaIterator.hasNext()) {
                    ListVlastnictva listVlastnictva = listyVlastnictvaIterator.next();
                    stringBuilder.append(listVlastnictva.getCisloListuVlastnictva());
                    stringBuilder.append(DEFAULT_SEPARATOR);
                    writer.println(stringBuilder.toString());
                    stringBuilder.setLength(0);
                    writer.println("Pocet majitelov na LV: " + DEFAULT_SEPARATOR + listVlastnictva.getVlastniciSPodielom().getSize() + DEFAULT_SEPARATOR);
                    Iterator<ListVlastnictva.ObcanSPodielom> obcanSPodielomLevelOrderIterator = listVlastnictva.getVlastniciSPodielom().levelOrderIterator();
                    while (obcanSPodielomLevelOrderIterator.hasNext()) {
                        ListVlastnictva.ObcanSPodielom obcanSPodielom = obcanSPodielomLevelOrderIterator.next();
                        stringBuilder.append(obcanSPodielom.getObcan().getRodneCislo());
                        stringBuilder.append(DEFAULT_SEPARATOR);
                        stringBuilder.append(obcanSPodielom.getPodiel());
                        stringBuilder.append(DEFAULT_SEPARATOR);
                        writer.println(stringBuilder.toString());
                        stringBuilder.setLength(0);
                    }

                    writer.println("Pocet nehnutelnosti na LV: " + DEFAULT_SEPARATOR + listVlastnictva.getNehnutelnostiNaListeVlastnictva().getSize() + DEFAULT_SEPARATOR);
                    Iterator<Nehnutelnost> nehnutelnostLevelOrderIterator = listVlastnictva.getNehnutelnostiNaListeVlastnictva().levelOrderIterator();
                    while (nehnutelnostLevelOrderIterator.hasNext()) {
                        Nehnutelnost nehnutelnost = nehnutelnostLevelOrderIterator.next();
                        stringBuilder.append(nehnutelnost.getSupisneCislo());
                        stringBuilder.append(DEFAULT_SEPARATOR);
                        stringBuilder.append(nehnutelnost.getAdresa());
                        stringBuilder.append(DEFAULT_SEPARATOR);
                        stringBuilder.append(nehnutelnost.getPopis());
                        stringBuilder.append(DEFAULT_SEPARATOR);
                        writer.println(stringBuilder.toString());
                        stringBuilder.setLength(0);
                        writer.println("Pocet obcanov s trvalym pobytom v NEH: " + DEFAULT_SEPARATOR + nehnutelnost.getObcaniaSTrvalymPobytom().getSize() + DEFAULT_SEPARATOR);
                        Iterator<Obcan> iteratorObcaniaSTrvalymPobytom = nehnutelnost.getObcaniaSTrvalymPobytom().levelOrderIterator();
                        while (iteratorObcaniaSTrvalymPobytom.hasNext()) {
                            Obcan obcan = iteratorObcaniaSTrvalymPobytom.next();
                            stringBuilder.append(obcan.getRodneCislo());
                            stringBuilder.append(DEFAULT_SEPARATOR);
                            writer.println(stringBuilder.toString());
                            stringBuilder.setLength(0);
                        }
                    }
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean importujData(String cestaKSuboru) {
        try(BufferedReader br = new BufferedReader(new FileReader(cestaKSuboru))) {
            vycistiData();
            Optional<Holder<KatastralneUzemie>> katastralneUzemieHolder = Optional.of(new Holder<>());
            Optional<Holder<Nehnutelnost>> nehnutelnostHolder = Optional.of(new Holder<>());
            Optional<Holder<ListVlastnictva>> listVlastnictvaHolder = Optional.of(new Holder<>());
            Optional<Holder<Obcan>> obcanHolder = Optional.of(new Holder<>());

            boolean inserted = false;

            ArrayList<String> parsedLine = new ArrayList<>();
            for(String line; (line = br.readLine()) != null; ) {
                System.out.println("tu iba raz");
                parseLine(line, parsedLine);
                int pocetObcanov = Integer.parseInt(parsedLine.get(1));
                for (int indexObcania = 0; indexObcania < pocetObcanov; ++indexObcania) {
                    line = br.readLine();
                    parseLine(line, parsedLine);
                    inserted = pridajObcana(parsedLine.get(1), parsedLine.get(0), Long.parseLong(parsedLine.get(2)));
                    if (!inserted) {
                        return false;
                    }
                }
                line = br.readLine();
                parseLine(line, parsedLine);
                int pocetKatastralnychUzemi = Integer.parseInt(parsedLine.get(1));
                for (int indexKatastralneUzemie = 0; indexKatastralneUzemie < pocetKatastralnychUzemi; indexKatastralneUzemie++) {
                    line = br.readLine();
                    parseLine(line, parsedLine);
                    inserted = pridajKatastralneUzemie(Long.parseLong(parsedLine.get(0)), parsedLine.get(1), katastralneUzemieHolder);
                    if (!inserted) {
                        return false;
                    }
                    KatastralneUzemie katastralneUzemie = katastralneUzemieHolder.get().value;
                    line = br.readLine();
                    parseLine(line, parsedLine);
                    int pocetListovVlastnictvaVKU = Integer.parseInt(parsedLine.get(1));
                    for (int indexListVlastnictva = 0; indexListVlastnictva < pocetListovVlastnictvaVKU; indexListVlastnictva++) {
                        line = br.readLine();
                        parseLine(line, parsedLine);
                        long cisloListuVlastnictva = Long.parseLong(parsedLine.get(0));
                        inserted = katastralneUzemie.vlozListVlastnictva(cisloListuVlastnictva, listVlastnictvaHolder);
                        if (!inserted) {
                            return false;
                        }
                        ListVlastnictva listVlastnictva = listVlastnictvaHolder.get().value;
                        line = br.readLine();
                        parseLine(line, parsedLine);
                        int pocetMajitelovNaListeVlastnictva = Integer.parseInt(parsedLine.get(1));
                        for (int indexPocetMajitelovNaLV = 0; indexPocetMajitelovNaLV < pocetMajitelovNaListeVlastnictva; indexPocetMajitelovNaLV++) {
                            line = br.readLine();
                            parseLine(line, parsedLine);
                            String rodneCislo = parsedLine.get(0);
                            double podiel = Double.parseDouble(parsedLine.get(1));
                            Obcan obcan = najdiObcana(rodneCislo);
                            if (obcan == null) {
                                return false;
                            }

                            inserted = listVlastnictva.pridajAleboPonechajVlastnika(obcan, podiel);
                            if (!inserted) {
                                return false;
                            }
                            inserted = obcan.pridajAleboPonechajListVlastnictva(listVlastnictva);
                            if (!inserted) {
                                return false;
                            }
                        }
                        line = br.readLine();
                        parseLine(line, parsedLine);
                        int pocetNehnutelnostiNaListeVlastnictva = Integer.parseInt(parsedLine.get(1));
                        for (int indexNehnutelnostiNaLV = 0; indexNehnutelnostiNaLV < pocetNehnutelnostiNaListeVlastnictva; indexNehnutelnostiNaLV++) {
                            line = br.readLine();
                            parseLine(line, parsedLine);
                            katastralneUzemie.vlozNehnutelnostNaListVlastnictva(listVlastnictva, Long.parseLong(parsedLine.get(0)), parsedLine.get(1), parsedLine.get(2), nehnutelnostHolder);
                            Nehnutelnost nehnutelnost = nehnutelnostHolder.get().value;
                            line = br.readLine();
                            parseLine(line, parsedLine);
                            int pocetObcanovSTrvalymPobytomVNe = Integer.parseInt(parsedLine.get(1));
                            for (int indexPocetObcanovSTrPoVNe = 0; indexPocetObcanovSTrPoVNe < pocetObcanovSTrvalymPobytomVNe; indexPocetObcanovSTrPoVNe++) {
                                line = br.readLine();
                                parseLine(line, parsedLine);
                                String rodneCislo = parsedLine.get(0);
                                Obcan obcan = najdiObcana(rodneCislo);
                                if (obcan == null) {
                                    return false;
                                }
                                zapisObcanoviTrvalyPobyt(obcan, nehnutelnost);
                            }
                        }
                    }

                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void parseLine(String cvsLine,ArrayList<String> result) {

        result.clear();
        //if empty, return!
        if (cvsLine == null || cvsLine.isEmpty()) {
            return;
        }
        char separator = DEFAULT_SEPARATOR;
        StringBuffer curVal = new StringBuffer();
        char[] chars = cvsLine.toCharArray();
        for (char ch : chars) {
            if (ch == separator) {
                result.add(curVal.toString());
                curVal = new StringBuffer();
            } else if (ch == '\r') {
                //ignore LF characters
                continue;
            } else if (ch == '\n') {
                //the end, break!
                break;
            } else {
                curVal.append(ch);
            }
        }
        if (curVal.length() > 0) {
            result.add(curVal.toString());
        }

    }

    public AvlTree<Obcan> getObcania() {
        return obcania_;
    }

    public AvlTree<KatastralneUzemie> getKatastralneUzemia() {
        return katastralneUzemieCislo_;
    }

}
