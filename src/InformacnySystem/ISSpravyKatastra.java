package InformacnySystem;

import Model.KatastralneUzemie;
import Model.Obcan;
import structures.AvlTree;

import java.util.Comparator;

public class ISSpravyKatastra {

    AvlTree<Obcan> obcania_;
    AvlTree<KatastralneUzemie> katastralneUzemieCislo_;
    AvlTree<KatastralneUzemie> katastralneUzemieNapis_;

    public ISSpravyKatastra() {
        this.obcania_ = new AvlTree<>(Comparator.comparing(Obcan::getRodneCislo));
        this.katastralneUzemieCislo_ = new AvlTree<>(Comparator.comparing(KatastralneUzemie::getCisloKatastralnehoUzemia));
        this.katastralneUzemieNapis_ = new AvlTree<>(Comparator.comparing(KatastralneUzemie::getNazov));
    }


    public boolean pridajObcana(String menoPriezvisko, String rodneCislo, long datumNarodenia) {
        return true;
    }

}
