package GUI.Controller;

import GUI.SimpleTask;
import GUI.View.ViewItems.TableItemNehnutelnost;
import GUI.View.ViewItems.TableItemObcanPodiel;
import InformacnySystem.ISSpravyKatastra;
import Model.ListVlastnictva;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.NumberStringConverter;

import java.util.Arrays;
import java.util.List;

public class C12ZapisZmenaMajetkovehoPodielu extends ControllerBase {

    private static class MyDoubleStringConverter extends NumberStringConverter {
        @Override
        public Number fromString(String value) {
            value = value.trim();
            value = value.replace(" ", "");
            value = value.replace(",", ".");
            System.out.println("value: " + value);
            if (value.isEmpty() || !isNumber(value)) {
                return null;
            } else {
                return Double.valueOf(value);
            }

        }
        public boolean isNumber(String value) {
            int size = value.length();
            value = value.trim();
            int numberOfDots = 0;
            for (int i = 0; i < size; i++) {
                if (value.charAt(i) == '.') {
                    numberOfDots++;
                    if (numberOfDots > 1) {
                        return false;
                    }
                    continue;
                }
                if (!Character.isDigit(value.charAt(i))) {
                    return false;
                }
            }
            return size > 0;
        }
    }



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

        Helper.decorateNumberTextFieldWithValidator( textFieldCisloKatastralnehoUzemia, isCisloKUOk);
        Helper.decorateNumberTextFieldWithValidator( textFieldCisloListuVlastnictva, isCisloLVOk);
        Helper.decorateTextFieldWithValidator(textFieldRodneCislo, isRodneCisloOk, 16, "Rodné číslo");

        buttonUpravMajetkovyPodiel.setOnAction(event -> {
            if (Helper.disableButton(buttonUpravMajetkovyPodiel, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }

        });

        tableColumnSupisneCislo.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getSupisneCislo()));
        tableColumnAdresa.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAdresa()));
        tableColumnPopis.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPopis()));

        tableViewObcanPodiely.setEditable(true);

        tableColumnMenoPriezvisko.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMenoPriezvisko()));
        tableColumnRodneCislo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRodneCislo()));
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



        ObservableList<TableItemObcanPodiel> tableItemObcanPodiely =  tableViewObcanPodiely.getItems();
        tableItemObcanPodiely.add(new TableItemObcanPodiel("Andrej Beliancin", "1111111111111111", 10.0, 100.0));
        tableItemObcanPodiely.add(new TableItemObcanPodiel("Andrej Beliancin", "1111111111111112", 100.0, 0.0));

        buttonUlozMajetkovePodiely.setOnAction(event -> {
            if (!isSumaPodielovOk()) {
                showWarningDialog("Suma podielov musí byť rovná 100");
                return;
            }
        });
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

    /*
    @Override
    public Runnable getRunnableOnSelection() {
        return () -> clearFormulars();
    }
    */

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

        @Override
        public boolean compute() {

            long cisloKatastralnehoUzemia = 0;
            long cisloListuVlastnictva = 0;
            ListVlastnictva listVlastnictva = isSpravyKatastra_.upravMajetkovyPodielNaListeVlastnictva(cisloKatastralnehoUzemia, cisloListuVlastnictva, textFieldRodneCislo.getText());
            return listVlastnictva != null;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onFail() {

        }
    }

}
