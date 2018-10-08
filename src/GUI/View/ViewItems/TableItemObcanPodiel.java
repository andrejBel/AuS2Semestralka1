package GUI.View.ViewItems;

import Model.ListVlastnictva;

public class TableItemObcanPodiel extends TableItemObcan {

    double staryPodiel_;
    double novyPodiel_;
    ListVlastnictva.ObcanSPodielom obcanSPodielom_;

    public TableItemObcanPodiel(ListVlastnictva.ObcanSPodielom obcanSPodielom) {
        super(obcanSPodielom.getObcan().getMenoPriezvisko(), obcanSPodielom.getObcan().getRodneCislo(), obcanSPodielom.getObcan().getDatumNarodenia());
        obcanSPodielom_ = obcanSPodielom;
        this.staryPodiel_ = obcanSPodielom.getPodiel();
        this.novyPodiel_ = obcanSPodielom.getPodiel();
    }

    public double getStaryPodiel() {
        return staryPodiel_;
    }

    public double getNovyPodiel() {
        return novyPodiel_;
    }

    public void setNovyPodiel(double novyPodiel) {
        this.novyPodiel_ = novyPodiel;
    }

    public void setObcanoviNovyPodiel() {
        obcanSPodielom_.setPodiel(novyPodiel_);
    }

}
