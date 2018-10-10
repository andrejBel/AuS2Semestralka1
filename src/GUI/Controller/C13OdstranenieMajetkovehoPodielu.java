package GUI.Controller;

import GUI.SimpleTask;
import GUI.View.ViewItems.TableItemNehnutelnost;
import GUI.View.ViewItems.TableItemObcanPodiel;
import InformacnySystem.ISSpravyKatastra;
import Model.ListVlastnictva;
import Model.Nehnutelnost;
import Model.Obcan;
import Utils.Helper;
import Utils.MyDoubleStringConverter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import structures.AvlTree;

import java.util.Arrays;
import java.util.List;

public class C13OdstranenieMajetkovehoPodielu extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldCisloListuVlastnictva;

    @FXML
    private JFXTextField textFieldRodneCislo;

    @FXML
    private JFXButton buttonOdstranMajetkovyPodiel;

    @FXML
    private TableView<TableItemNehnutelnost> tableViewNehnutelnosti;

    @FXML
    private TableColumn<TableItemNehnutelnost, Number> tableColumnSupisneCislo;

    @FXML
    private TableColumn<TableItemNehnutelnost, String> tableColumnAdresa;

    @FXML
    private TableColumn<TableItemNehnutelnost, String> tableColumnPopis;

    @FXML
    private TableView<TableItemObcanPodiel> tableViewObcanPodiely;

    @FXML
    private TableColumn<TableItemObcanPodiel, String> tableColumnMenoPriezvisko;

    @FXML
    private TableColumn<TableItemObcanPodiel, String> tableColumnDatumNarodenia;

    @FXML
    private TableColumn<TableItemObcanPodiel, String> tableColumnRodneCislo;

    @FXML
    private TableColumn<TableItemObcanPodiel, Number> tableColumnStaryPodiel;

    @FXML
    private TableColumn<TableItemObcanPodiel, Number> tableColumnNovyPodiel;

    @FXML
    private JFXButton buttonUlozMajetkovePodiely;

    private SimpleBooleanProperty isCisloKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCisloLVOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isRodneCisloOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isCisloKUOk,
            isCisloLVOk,
            isRodneCisloOk
    );

    private List<JFXTextField> textFields;

    public C13OdstranenieMajetkovehoPodielu(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

        textFields = Arrays.asList(
                textFieldCisloKatastralnehoUzemia,
                textFieldCisloListuVlastnictva,
                textFieldRodneCislo
        );

        Helper.DecorateNumberTextFieldWithValidator( textFieldCisloKatastralnehoUzemia, isCisloKUOk);
        Helper.DecorateNumberTextFieldWithValidator( textFieldCisloListuVlastnictva, isCisloLVOk);
        Helper.DecorateTextFieldWithValidator(textFieldRodneCislo, isRodneCisloOk, Obcan.RODNE_CISLO_LENGTH, "Rodné číslo");

        buttonOdstranMajetkovyPodiel.setOnAction(event -> {
            if (Helper.DisableButton(buttonOdstranMajetkovyPodiel, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new OdstranMajetkovyPodiel().execute();
        });
        Helper.SetActionOnEnter(textFields, () -> buttonOdstranMajetkovyPodiel.fire());

        tableColumnSupisneCislo.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getSupisneCislo()));
        tableColumnAdresa.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAdresa()));
        tableColumnPopis.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPopis()));

        tableViewObcanPodiely.setEditable(true);

        tableColumnMenoPriezvisko.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMenoPriezvisko()));
        tableColumnRodneCislo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRodneCislo()));
        tableColumnDatumNarodenia.setCellValueFactory(param -> new SimpleStringProperty(Helper.FormatujDatum(param.getValue().getDatumNarodenia())));
        tableColumnStaryPodiel.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getStaryPodiel()));


        tableColumnNovyPodiel.setCellFactory(
                TextFieldTableCell.forTableColumn(
                        new MyDoubleStringConverter()));

        tableColumnNovyPodiel.setCellValueFactory(param -> {
            return new SimpleDoubleProperty(param.getValue().getNovyPodiel());
        });

        tableColumnNovyPodiel.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ?
                    event.getNewValue().doubleValue() : (event.getOldValue() != null ? event.getOldValue().doubleValue() : 0.0);
            ((TableItemObcanPodiel) event.getTableView().getItems()
                    .get(event.getTablePosition().getRow())).setNovyPodiel(value);
            tableViewObcanPodiely.refresh();
        });


        buttonUlozMajetkovePodiely.setOnAction(event -> {
            ObservableList<TableItemObcanPodiel> tableViewObcanPodielyItems = tableViewObcanPodiely.getItems();
            if (tableViewObcanPodielyItems.size() == 0) {
                showInfoDialog("Tabuľka majiteľov a ich podielov je prázdna, nie sú honoty na úpravu");
                return;
            }
            if (!isSumaPodielovOk()) {
                showWarningDialog("Suma podielov musí byť rovná 100");
                return;
            }
            tableViewObcanPodielyItems.forEach(tableItemObcanPodiel -> tableItemObcanPodiel.setObcanoviNovyPodiel());
            showSuccessDialog("Nové podiely boli uložené");

        });
        Helper.InstallCopyPasteHandler(tableViewNehnutelnosti);
        Helper.InstallCopyPasteHandler(tableViewObcanPodiely);

    }

    private boolean isSumaPodielovOk() {
        double suma = 0.0;
        ObservableList<TableItemObcanPodiel> tableItems = tableViewObcanPodiely.getItems();
        for (TableItemObcanPodiel tableItemObcanPodiel: tableItems) {
            suma += tableItemObcanPodiel.getNovyPodiel();
        }
        return Math.abs(suma  - 100.0) < 0.1;
    }

    private void clearFormulars() {
        buttonOdstranMajetkovyPodiel.disableProperty().unbind();
        buttonOdstranMajetkovyPodiel.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
        tableViewNehnutelnosti.getItems().clear();
        tableViewObcanPodiely.getItems().clear();
    }

    @Override
    public Runnable getRunnableOnSelection() {
        return () -> clearFormulars();
    }

    @Override
    protected void initView() {
        loadView();
    }

    @Override
    protected String getViewFileName() {
        return "13odstranenieMajetkovehoPodielu.fxml";
    }

    @Override
    public String getViewName() {
        return "13. Odstránenie majetkového podielu";
    }

    private class OdstranMajetkovyPodiel extends SimpleTask {

        ListVlastnictva listVlastnictva = null;

        @Override
        public boolean compute() {
            long cisloKatastralnehoUzemia = 0;
            try {
                cisloKatastralnehoUzemia = Long.valueOf(textFieldCisloKatastralnehoUzemia.getText());
            } catch (NumberFormatException e) {
                return false;
            }

            long cisloListuVlastnictva = 0;
            try {
                cisloListuVlastnictva = Long.valueOf(textFieldCisloListuVlastnictva.getText());
            } catch (NumberFormatException e) {
                return false;
            }
            listVlastnictva = isSpravyKatastra_.odstranMajetkovyPodielNaListeVlastnictva(cisloKatastralnehoUzemia, cisloListuVlastnictva, textFieldRodneCislo.getText());
            return listVlastnictva != null;
        }

        @Override
        public void onSuccess() {
            ObservableList<TableItemNehnutelnost> tableViewItemsNehnutelnosti = tableViewNehnutelnosti.getItems();
            tableViewItemsNehnutelnosti.clear();
            AvlTree<Nehnutelnost> nehnutelnosti = listVlastnictva.getNehnutelnostiNaListeVlastnictva();
            TableItemNehnutelnost tableItemNehnutelnost = null;
            for (Nehnutelnost nehnutelnost: nehnutelnosti) {
                tableItemNehnutelnost = new TableItemNehnutelnost(nehnutelnost.getSupisneCislo(), nehnutelnost.getAdresa(), nehnutelnost.getPopis());
                tableViewItemsNehnutelnosti.add(tableItemNehnutelnost);
            }

            ObservableList<TableItemObcanPodiel> tableViewItemObcanSPodielmi = tableViewObcanPodiely.getItems();
            tableViewItemObcanSPodielmi.clear();
            AvlTree<ListVlastnictva.ObcanSPodielom> obcaniaSPodielom = listVlastnictva.getVlastniciSPodielom();
            TableItemObcanPodiel tableItemObcanPodiel = null;
            for (ListVlastnictva.ObcanSPodielom obcanSPodielom: obcaniaSPodielom ) {
                tableItemObcanPodiel = new TableItemObcanPodiel(obcanSPodielom);
                tableViewItemObcanSPodielmi.add(tableItemObcanPodiel);
            }
            if (obcaniaSPodielom.getSize() > 0) {
                showSuccessDialog("Občanovi bol úspešne odstránený majetkový posiel. Môžete upraviť podiely ostatných vlastníkov.");
            } else {
                showSuccessDialog("Občanovi bol úspešne odstránený majetkový posiel. Na liste vlastníctva nie sú žiadny iný vlastníci.");
            }
        }

        @Override
        public void onFail() {
            showWarningDialog("Nepodarilo sa odstrániť majetkový podiel");
        }
    }

}
