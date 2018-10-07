package GUI.View.ViewItems;

import Model.ListVlastnictva;
import Model.Obcan;

public class TableItemObcanPodiel {

    double staryPodiel_;
    double novyPodiel_;
    ListVlastnictva.ObcanSPodielom obcanSPodielom_;

    public TableItemObcanPodiel(ListVlastnictva.ObcanSPodielom obcanSPodielom) {
        obcanSPodielom_ = obcanSPodielom;
        this.staryPodiel_ = obcanSPodielom.getPodiel();
        this.novyPodiel_ = obcanSPodielom.getPodiel();
    }

    public String getMenoPriezvisko() {
        return obcanSPodielom_.getObcan().getMenoPriezvisko();
    }

    public String getRodneCislo() {
        return obcanSPodielom_.getObcan().getRodneCislo();
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
