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

public class C12ZapisZmenaMajetkovehoPodielu extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldCisloListuVlastnictva;

    @FXML
    private JFXTextField textFieldRodneCislo;

    @FXML
    private JFXButton buttonUpravMajetkovyPodiel;

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
    private TableColumn<TableItemObcanPodiel, String> tableColumnRodneCislo;

    @FXML
    private TableColumn<TableItemObcanPodiel, String> tableColumnDatumNarodenia;

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

    public C12ZapisZmenaMajetkovehoPodielu(ISSpravyKatastra isSpravyKatastra) {
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

        buttonUpravMajetkovyPodiel.setOnAction(event -> {
            if (Helper.DisableButton(buttonUpravMajetkovyPodiel, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new NacitajMajetkovePodiely().execute();
        });

        Helper.SetActionOnEnter(textFields, () -> buttonUpravMajetkovyPodiel.fire());

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

    private void clearFormulars() {
        buttonUpravMajetkovyPodiel.disableProperty().unbind();
        buttonUpravMajetkovyPodiel.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
        tableViewNehnutelnosti.getItems().clear();
        tableViewObcanPodiely.getItems().clear();
    }

    private boolean isSumaPodielovOk() {
        double suma = 0.0;
        ObservableList<TableItemObcanPodiel> tableItems = tableViewObcanPodiely.getItems();
        for (TableItemObcanPodiel tableItemObcanPodiel: tableItems) {
            suma += tableItemObcanPodiel.getNovyPodiel();
        }
        return Math.abs(suma  - 100.0) < 0.1;
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
        return "12zapisZmenaMajetkovehoPodielu.fxml";
    }

    @Override
    public String getViewName() {
        return "12. Zápis/zmena majetkového podielu";
    }

    private class NacitajMajetkovePodiely extends SimpleTask {

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
            listVlastnictva = isSpravyKatastra_.upravMajetkovyPodielNaListeVlastnictva(cisloKatastralnehoUzemia, cisloListuVlastnictva, textFieldRodneCislo.getText());
            return listVlastnictva != null;
        }

        @Override
        public void onSuccess() {
            Helper.naplnTabulkuNehnutelnosti(tableViewNehnutelnosti, listVlastnictva.getNehnutelnostiNaListeVlastnictva());
            Helper.naplnTabulkuVlastnikov(tableViewObcanPodiely, listVlastnictva.getVlastniciSPodielom());

            showSuccessDialog("Údaje boli úspešne načítané. Môžete upraviť podiely vlastníkov.");
        }

        @Override
        public void onFail() {
            showWarningDialog("Nepodarilo sa nájsť údaje potrebné pre úpravu majetkového podielu");
        }
    }

}
