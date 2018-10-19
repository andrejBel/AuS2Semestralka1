package GUI.Controller;

import GUI.AsyncTask;
import InformacnySystem.ISSpravyKatastra;
import Model.KatastralneUzemie;
import Model.ListVlastnictva;
import Model.Nehnutelnost;
import Model.Obcan;
import Utils.Pair;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import Structures.AvlTree;

public class CVypisVsetkychInformacii extends  ControllerBase {

    @FXML
    private JFXButton buttonNacitaj;

    @FXML
    private JFXTreeView treeView;

    public CVypisVsetkychInformacii(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

        treeView.setShowRoot(false);
        buttonNacitaj.setOnAction(event -> {
            new NacitajInformacie().execute();
        });
    }

    private TreeItem<String> napln() {
        TreeItem<String> root = new TreeItem<>("Root");


        TreeItem<String> udajeOObcanoch = new TreeItem<>("Občania");
        ObservableList<TreeItem<String>> udajeOObcanochChildrens = udajeOObcanoch.getChildren();

        TreeItem<String> udajeOKatastralnycUzemiach = new TreeItem<>("Katastrálne územia");
        ObservableList<TreeItem<String>> udajeOKatastralnycUzemiachChildrens = udajeOKatastralnycUzemiach.getChildren();


        root.getChildren().addAll(udajeOObcanoch, udajeOKatastralnycUzemiach);


        AvlTree<Obcan> obcania = isSpravyKatastra_.getObcania();
        for (Obcan obcan: obcania) {
            TreeItem<String> obcanTreeItem = new TreeItem<>(obcan.getRodneCislo());
            ObservableList<TreeItem<String>> udajeOObcanovi = obcanTreeItem.getChildren();

            Nehnutelnost trvaleBydliskoObcana = obcan.getTrvalyPobyt();
            TreeItem<String> obcanTrvaleBydlisko = null;
            if (trvaleBydliskoObcana == null) {
                obcanTrvaleBydlisko = new TreeItem<>("Občan nemá trvalé bydlisko");
            } else {
                obcanTrvaleBydlisko = getTreeItemNehnutelnost(trvaleBydliskoObcana, "Trvalé bydlisko ");
            }
            udajeOObcanovi.add(obcanTrvaleBydlisko);

            TreeItem listyVlastnictvaTreeItem = new TreeItem<>("Listy vlastníctva");
            udajeOObcanovi.add(listyVlastnictvaTreeItem);


            AvlTree<Pair<Long, AvlTree<ListVlastnictva>>> listyVlastnictva =  obcan.getListyVlatnictva();
            for (Pair<Long, AvlTree<ListVlastnictva>> listyVlastnictvaVKatastralnomUzemi: listyVlastnictva) {
                TreeItem<String> listyVlastnictvaVKatastralnomUzemiTreeItem = new TreeItem<>("KÚ číslo " + listyVlastnictvaVKatastralnomUzemi.getKey());

                listyVlastnictvaTreeItem.getChildren().add(listyVlastnictvaVKatastralnomUzemiTreeItem);

                AvlTree<ListVlastnictva> konecnelistyVlastnictva = listyVlastnictvaVKatastralnomUzemi.getValue();
                for (ListVlastnictva listVlastnictva: konecnelistyVlastnictva) {
                    TreeItem<String> listVlastnictvaTreeItem = getTreeItemListVlastnictva(listVlastnictva);
                    listyVlastnictvaVKatastralnomUzemiTreeItem.getChildren().add(listVlastnictvaTreeItem);

                }


            }
            udajeOObcanochChildrens.add(obcanTreeItem);
        }

        AvlTree<KatastralneUzemie> katastralneUzemia = isSpravyKatastra_.getKatastralneUzemia();
        for(KatastralneUzemie katastralneUzemie: katastralneUzemia) {
            TreeItem<String> kuTreeItem = new TreeItem<>(katastralneUzemie.getCisloKatastralnehoUzemia() + ". " + katastralneUzemie.getNazov());
            udajeOKatastralnycUzemiachChildrens.add(kuTreeItem);

            TreeItem<String> nehnutelnostiTreeItem = new TreeItem<>("Nehnuteľnosti");
            ObservableList<TreeItem<String>> nehnutelnostiTreeItemChildrens = nehnutelnostiTreeItem.getChildren();


            TreeItem<String> listyVlastnictvaTreeItem = new TreeItem<>("Listy vlastníctva");
            ObservableList<TreeItem<String>> listyVlastnictvaTreeItemChildrens = listyVlastnictvaTreeItem.getChildren();
            kuTreeItem.getChildren().addAll(nehnutelnostiTreeItem, listyVlastnictvaTreeItem);


            for (Nehnutelnost nehnutelnost: katastralneUzemie.getNehnutelnostiVKatastralnomUzemi()) {
                nehnutelnostiTreeItemChildrens.add(getTreeItemNehnutelnost(nehnutelnost));
            }

            for (ListVlastnictva listVlastnictva: katastralneUzemie.getListyVlastnictvaVKatastralnomUzemi()) {
                TreeItem<String> listVlastnictvaTreeItem = getTreeItemListVlastnictva(listVlastnictva);
                listyVlastnictvaTreeItemChildrens.add(listVlastnictvaTreeItem);

            }
        }
        return root;
    }

    private TreeItem<String> getTreeItemNehnutelnost(Nehnutelnost nehnutelnost) {
        return getTreeItemNehnutelnost(nehnutelnost, "");
    }

    private TreeItem<String> getTreeItemNehnutelnost(Nehnutelnost nehnutelnost, String before) {
        TreeItem<String> mainTreeItem = new TreeItem<>(before+
                "Súpisné číslo: "  + nehnutelnost.getSupisneCislo() +
                ", list vlastníctva: "  + nehnutelnost.getListVlastnictva().getCisloListuVlastnictva() +
                ", katastrálne územie: " + nehnutelnost.getListVlastnictva().getKatastralneUzemie().getCisloKatastralnehoUzemia());
        TreeItem<String> obcaniaSTrvalymPobytomTreeItem = new TreeItem<>("Občania s trvalým pobytom");
        ListVlastnictva listVlastnictva = nehnutelnost.getListVlastnictva();
        TreeItem<String> listVlastnictvaTreeItem = new TreeItem<>(
                "Č.l. vlastníctva: " + listVlastnictva.getCisloListuVlastnictva() +
                        ", KÚ: " + listVlastnictva.getKatastralneUzemie().getCisloKatastralnehoUzemia() +
                        ", počet vlastníkov: " + listVlastnictva.getVlastniciSPodielom().getSize() +
                        ", počet nehnuteľností: " + listVlastnictva.getNehnutelnostiNaListeVlastnictva().getSize()
        );
        TreeItem<String> vlastnici = new TreeItem<>("Vlastníci");
        TreeItem<String> nehnutelnosti = new TreeItem<>("Nehnuteľnosti");
        listVlastnictvaTreeItem.getChildren().addAll(vlastnici, nehnutelnosti);

        for (ListVlastnictva.ObcanSPodielom obcanSPodielom: listVlastnictva.getVlastniciSPodielom()) {
            vlastnici.getChildren().add(getTreeItemObcanNehnutelnost(obcanSPodielom));
        }
        for (Nehnutelnost nehnutelnostVListeVlastnictva: listVlastnictva.getNehnutelnostiNaListeVlastnictva()) {
            nehnutelnosti.getChildren().add(new TreeItem<>(
                    "Súpisné číslo: "  + nehnutelnostVListeVlastnictva.getSupisneCislo() +
                    ", list vlastníctva: "  + nehnutelnostVListeVlastnictva.getListVlastnictva().getCisloListuVlastnictva() +
                    ", katastrálne územie: " + nehnutelnostVListeVlastnictva.getListVlastnictva().getKatastralneUzemie().getCisloKatastralnehoUzemia()));
        }



        for (Obcan obcan: nehnutelnost.getObcaniaSTrvalymPobytom()) {
            obcaniaSTrvalymPobytomTreeItem.getChildren().add(new TreeItem<>(
                  "RČ: " + obcan.getRodneCislo()
            ));
        }
        mainTreeItem.getChildren().addAll(obcaniaSTrvalymPobytomTreeItem, listVlastnictvaTreeItem);
        return mainTreeItem;
    }

    private TreeItem<String> getTreeItemListVlastnictva(ListVlastnictva listVlastnictva) {
        return getTreeItemListVlastnictva(listVlastnictva, "");
    }

    private TreeItem<String> getTreeItemListVlastnictva(ListVlastnictva listVlastnictva, String before) {
        TreeItem<String> mainTreeitem = new TreeItem<>(
                "Č.l. vlastníctva: " + listVlastnictva.getCisloListuVlastnictva() +
                        ", KÚ: " + listVlastnictva.getKatastralneUzemie().getCisloKatastralnehoUzemia() +
                        ", počet vlastníkov: " + listVlastnictva.getVlastniciSPodielom().getSize() +
                        ", počet nehnuteľností: " + listVlastnictva.getNehnutelnostiNaListeVlastnictva().getSize()
        );
        TreeItem<String> vlastnici = new TreeItem<>("Vlastníci");
        TreeItem<String> nehnutelnosti = new TreeItem<>("Nehnuteľnosti");
        mainTreeitem.getChildren().addAll(vlastnici, nehnutelnosti);

        for (ListVlastnictva.ObcanSPodielom obcanSPodielom: listVlastnictva.getVlastniciSPodielom()) {
            vlastnici.getChildren().add(getTreeItemObcanNehnutelnost(obcanSPodielom));
        }
        for (Nehnutelnost nehnutelnost: listVlastnictva.getNehnutelnostiNaListeVlastnictva()) {
            nehnutelnosti.getChildren().add(getTreeItemNehnutelnost(nehnutelnost));
        }

        return mainTreeitem;
    }

    private TreeItem<String> getTreeItemObcanNehnutelnost(ListVlastnictva.ObcanSPodielom obcanSPodielom) {
        return getTreeItemObcanNehnutelnost(obcanSPodielom, "");
    }

    private TreeItem<String> getTreeItemObcanNehnutelnost(ListVlastnictva.ObcanSPodielom obcanSPodielom, String before) {
        return new TreeItem<>(
                "RČ: " + obcanSPodielom.getObcan().getRodneCislo() + ", podiel: " + obcanSPodielom.getPodiel()
        );
    }

    private void clearFormulars() {
        treeView.setRoot(null);
    }

    @Override
    public Runnable getRunnableOnSelection() {
        return this::clearFormulars;
    }

    @Override
    protected void initView() {
        loadView();
    }

    @Override
    protected String getViewFileName() {
        return "vypisVsetkychInformacii.fxml";
    }

    @Override
    public String getViewName() {
        return "Výpis všetkých informácii";
    }

    private class NacitajInformacie extends AsyncTask {

        @Override
        public void onPreExecute() {
            showSpinner("Načítavanie dát");
        }

        @Override
        public Object doInBackground(Object[] params) {
            return napln();
        }

        @Override
        public void onPostExecute(Object params) {
            TreeItem<String> root = (TreeItem<String>) params;
            treeView.setRoot(root);
            showSuccessDialog("Dáta úspešne načítané", false);
        }

        @Override
        public void progressCallback(Object[] params) {

        }

        @Override
        public void onFail(Exception e) {
            showWarningDialog("Neúspešné načítanie dát- vážna chyba");
        }
    }

}
