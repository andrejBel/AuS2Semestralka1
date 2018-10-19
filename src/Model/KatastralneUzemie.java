package Model;

import Utils.Helper;
import Structures.AvlTree;

import javax.xml.ws.Holder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class KatastralneUzemie {

    private long cisloKatastralnehoUzemia_;
    private String nazov_;
    private final AvlTree<Nehnutelnost> nehnutelnostiVkatastralnomUzemi_;
    private final AvlTree<ListVlastnictva> listyVlastnictvaVKatastralnomUzemi_;

    private static final ListVlastnictva dummyListVlastnictva = new ListVlastnictva();
    private static final Nehnutelnost dummyNehnutelnost = new Nehnutelnost();

    public KatastralneUzemie(long cisloKatastralnehoUzemia_, String nazov_) {
        this.cisloKatastralnehoUzemia_ = cisloKatastralnehoUzemia_;
        this.nazov_ = nazov_;
        nehnutelnostiVkatastralnomUzemi_ = new AvlTree<>(Comparator.comparingLong(Nehnutelnost::getSupisneCislo));
        listyVlastnictvaVKatastralnomUzemi_ = new AvlTree<>(Comparator.comparingLong(ListVlastnictva::getCisloListuVlastnictva));
    }

    public KatastralneUzemie() {
        this(0, "");
    }

    public long getCisloKatastralnehoUzemia() {
        return cisloKatastralnehoUzemia_;
    }

    public String getNazov() {
        return nazov_;
    }

    public AvlTree<Nehnutelnost> getNehnutelnostiVKatastralnomUzemi() {
        return nehnutelnostiVkatastralnomUzemi_;
    }

    public Nehnutelnost getNehnutelnostVKatastralnomUzemi(long supisneCisloNehnutelnosti) {
        dummyNehnutelnost.setSupisneCislo(supisneCisloNehnutelnosti);
        return nehnutelnostiVkatastralnomUzemi_.findData(dummyNehnutelnost);
    }

    public AvlTree<ListVlastnictva> getListyVlastnictvaVKatastralnomUzemi() {
        return listyVlastnictvaVKatastralnomUzemi_;
    }

    public boolean vlozListVlastnictva(long cisloListuVlastnictva, Optional<Holder<ListVlastnictva>> vlozenyListVlastnictva) {
        ListVlastnictva listVlastnictva = new ListVlastnictva(this, cisloListuVlastnictva);
        boolean inserted = listyVlastnictvaVKatastralnomUzemi_.insert(listVlastnictva);
        vlozenyListVlastnictva.ifPresent(listVlastnictvaHolder -> listVlastnictvaHolder.value = inserted ? listVlastnictva : null);
        return inserted;
    }

    public boolean odstranListVlastnictva(ListVlastnictva listVlastnictva) {
        return listyVlastnictvaVKatastralnomUzemi_.remove(listVlastnictva) != null;
    }

    public ListVlastnictva najdiListVlastnictva(long cisloListuVlastnictva) {
        dummyListVlastnictva.setCisloListuVlastnictva(cisloListuVlastnictva);
        return listyVlastnictvaVKatastralnomUzemi_.findData(dummyListVlastnictva);
    }

    public boolean vlozNehnutelnostNaListVlastnictva(long cisloListuVlastnictva, long supisneCisloNehnutelnosti, String adresaNehnutelnosti, String popisNehnutelnosti, Optional<Holder<Nehnutelnost>> vlozenaNehnutelnost) {
        boolean inserted = false;
        dummyListVlastnictva.setCisloListuVlastnictva(cisloListuVlastnictva);
        ListVlastnictva listVlastnictva = listyVlastnictvaVKatastralnomUzemi_.findData(dummyListVlastnictva);
        if (listVlastnictva != null) {
            return vlozNehnutelnostNaListVlastnictva(listVlastnictva, supisneCisloNehnutelnosti, adresaNehnutelnosti, popisNehnutelnosti, vlozenaNehnutelnost);
        }
        return false;
    }

    public boolean vlozNehnutelnostNaListVlastnictva(ListVlastnictva listVlastnictva, long supisneCisloNehnutelnosti, String adresaNehnutelnosti, String popisNehnutelnosti, Optional<Holder<Nehnutelnost>> vlozenaNehnutelnost) {
        boolean inserted = false;
        Nehnutelnost nehnutelnost = null;
        if (listVlastnictva != null && listVlastnictva.getKatastralneUzemie() == this) {
            nehnutelnost = new Nehnutelnost(supisneCisloNehnutelnosti, adresaNehnutelnosti, popisNehnutelnosti, listVlastnictva);
            inserted = nehnutelnostiVkatastralnomUzemi_.insert(nehnutelnost);
            if (inserted) {
                inserted = listVlastnictva.vlozNehnutelnostNaListVlastnictva(nehnutelnost);
                if (!inserted) {
                    nehnutelnostiVkatastralnomUzemi_.remove(nehnutelnost);
                }
            }
        }

        boolean finalInserted = inserted;
        Nehnutelnost finalNehnutelnost = nehnutelnost;
        vlozenaNehnutelnost.ifPresent(nehnutelnostHolder -> nehnutelnostHolder.value = finalInserted ? finalNehnutelnost : null);
        return inserted;
    }

    public Nehnutelnost najdiNehnutelnostVkatastralnomUzemi(long supisneCisloNehnutelnosti) {
        dummyNehnutelnost.setSupisneCislo(supisneCisloNehnutelnosti);
        return nehnutelnostiVkatastralnomUzemi_.findData(dummyNehnutelnost);
    }

    public void setCisloKatastralnehoUzemia(long cisloKatastralnehoUzemia) {
        this.cisloKatastralnehoUzemia_ = cisloKatastralnehoUzemia;
    }

    public void setNazov(String nazov) {
        this.nazov_ = nazov;
    }

    public Nehnutelnost odstranNehnutelnostZKU(Nehnutelnost nehnutelnost) {
        return nehnutelnostiVkatastralnomUzemi_.remove(nehnutelnost);
    }

    public boolean presunVsetkoDoInehoKatastralnehoUzemia(KatastralneUzemie ineKatastralneUzemie) {
        long pocetNehnutelnostiNaPresun =  nehnutelnostiVkatastralnomUzemi_.getSize();
        ArrayList<Long> pouziteIndexyPreNehnutelnosti = new ArrayList<>((int) ineKatastralneUzemie.nehnutelnostiVkatastralnomUzemi_.getSize());
        ArrayList<Long> supisneCislaPreNehnutelnosti = new ArrayList<>((int) nehnutelnostiVkatastralnomUzemi_.getSize());
        ineKatastralneUzemie.nehnutelnostiVkatastralnomUzemi_.forEach(nehnutelnost -> pouziteIndexyPreNehnutelnosti.add(nehnutelnost.getSupisneCislo()));
        Helper.generujUnikatneIndexy(pouziteIndexyPreNehnutelnosti, (int) pocetNehnutelnostiNaPresun, 1, supisneCislaPreNehnutelnosti);

        int indexSupisneCislo = 0;
        for (Nehnutelnost nehnutelnost: nehnutelnostiVkatastralnomUzemi_) {
            nehnutelnost.setSupisneCislo(supisneCislaPreNehnutelnosti.get(indexSupisneCislo++));
            boolean inserted = ineKatastralneUzemie.nehnutelnostiVkatastralnomUzemi_.insert(nehnutelnost);
            if (!inserted) {
                return false;
            }
        }

        System.out.println("generovane supisne cisla: " + supisneCislaPreNehnutelnosti);

        long pocetListovVlNaPresun =  listyVlastnictvaVKatastralnomUzemi_.getSize();
        ArrayList<Long> pouziteIndexyPreListyVlastnictva = new ArrayList<>((int) ineKatastralneUzemie.listyVlastnictvaVKatastralnomUzemi_.getSize());
        ArrayList<Long> cislaPreListyVlatnictvaNehnutelnosti = new ArrayList<>((int) nehnutelnostiVkatastralnomUzemi_.getSize());
        ineKatastralneUzemie.listyVlastnictvaVKatastralnomUzemi_.forEach(listVlastnictva -> pouziteIndexyPreListyVlastnictva.add(listVlastnictva.getCisloListuVlastnictva()));
        Helper.generujUnikatneIndexy(pouziteIndexyPreListyVlastnictva, (int) pocetListovVlNaPresun, 1, cislaPreListyVlatnictvaNehnutelnosti);

        System.out.println("Indexy pre listy vlastnictva: " + cislaPreListyVlatnictvaNehnutelnosti);

        int indexCisloVlastnictva = 0;
        boolean zmenene = false;
        for (ListVlastnictva listVlastnictva: listyVlastnictvaVKatastralnomUzemi_) {
            listVlastnictva.setCisloListuVlastnictva(cislaPreListyVlatnictvaNehnutelnosti.get(indexCisloVlastnictva++));
        }
        for (ListVlastnictva listVlastnictva: listyVlastnictvaVKatastralnomUzemi_) {
            zmenene = listVlastnictva.zmenKatastralneUzemie(ineKatastralneUzemie);
            if (!zmenene) {
                return false;
            }
            boolean inserted = ineKatastralneUzemie.listyVlastnictvaVKatastralnomUzemi_.insert(listVlastnictva);
            if (!inserted) {
                return false;
            }
        }

        return true;
    }

}
