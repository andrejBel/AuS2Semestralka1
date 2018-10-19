package GUI.Controller;

import InformacnySystem.ISSpravyKatastra;
import Model.KatastralneUzemie;
import Utils.Helper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import structures.AvlTree;

public class C15VypisKatastralnychUzemi extends ControllerBase {

    private static class TableItemKatastralneUzemie {

        private long cisloKU_;
        private String nazovKU_;

        public TableItemKatastralneUzemie(long cisloKU, String nazovKU) {
            this.cisloKU_ = cisloKU;
            this.nazovKU_ = nazovKU;
        }

        public long getCisloKU() {
            return cisloKU_;
        }

        public String getNazovKU() {
            return nazovKU_;
        }
    }

    @FXML
    private TableView<TableItemKatastralneUzemie> tableViewKatastralneUzemia;

    @FXML
    private TableColumn<TableItemKatastralneUzemie, Number> tableColumnCisloKU;

    @FXML
    private TableColumn<TableItemKatastralneUzemie, String> tableColumnNazovKU;

    public C15VypisKatastralnychUzemi(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        loadView();
        tableColumnCisloKU.setCellValueFactory(param -> {
            return new SimpleLongProperty( param.getValue().getCisloKU() );
        });
        tableColumnNazovKU.setCellValueFactory(param -> {
            return new SimpleStringProperty( param.getValue().getNazovKU());
        });
        Helper.InstallCopyPasteHandler(tableViewKatastralneUzemia);
    }

    private void nacitajKatastralneUzemia() {
        ObservableList<TableItemKatastralneUzemie> items = tableViewKatastralneUzemia.getItems();
        items.clear();
        AvlTree<KatastralneUzemie> katastralneUzemia = isSpravyKatastra_.getKatastralneUzemieUtriedenychPodlaNazvu();
        for (KatastralneUzemie uzemie: katastralneUzemia) {
            TableItemKatastralneUzemie item = new TableItemKatastralneUzemie(uzemie.getCisloKatastralnehoUzemia(), uzemie.getNazov());
            items.add(item);
        }
    }

    @Override
    protected void initView() {
        loadView();
    }

    @Override
    public Runnable getRunnableOnSelection() {
        return this::nacitajKatastralneUzemia;
    }

    @Override
    protected String getViewFileName() {
        return "15vypisKatastralnychUzemi.fxml";
    }

    @Override
    public String getViewName() {
        return "15. Výpis katastrálnych území";
    }
}
