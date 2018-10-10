package GUI.Controller;
import GUI.SimpleTask;
import GUI.View.ViewItems.TableItemNehnutelnost;
import GUI.View.ViewItems.TableItemObcanPodiel;
import InformacnySystem.ISSpravyKatastra;
import Model.ListVlastnictva;
import Model.Nehnutelnost;
import Utils.Helper;
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
import javafx.scene.input.KeyCode;
import structures.AvlTree;

import java.util.Arrays;
import java.util.List;

public class C6VyhladanieLVPodlaNazvuKU extends ControllerBase {

    @FXML
    private JFXTextField textFieldNazovKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldCisloListuVlastnictva;

    @FXML
    private JFXButton buttonHladaj;

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
    private TableColumn<TableItemObcanPodiel, Number> tableColumnPodiel;

    private SimpleBooleanProperty isNazovKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCisloLVOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isNazovKUOk,
            isCisloLVOk
    );

    private List<JFXTextField> textFields;

    public C6VyhladanieLVPodlaNazvuKU(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldNazovKatastralnehoUzemia,
                textFieldCisloListuVlastnictva
        );

        Helper.DecorateTextFieldWithValidator( textFieldNazovKatastralnehoUzemia, isNazovKUOk);
        Helper.DecorateNumberTextFieldWithValidator( textFieldCisloListuVlastnictva, isCisloLVOk);

        Helper.SetActionOnEnter(textFields, () -> buttonHladaj.fire());

        buttonHladaj.setOnAction(event -> {
            if (Helper.DisableButton(buttonHladaj, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new NacitajListVlastnictvaPodlaNazvuKU().execute();
        });

        tableColumnSupisneCislo.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getSupisneCislo()));
        tableColumnAdresa.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAdresa()));
        tableColumnPopis.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPopis()));

        tableColumnMenoPriezvisko.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMenoPriezvisko()));
        tableColumnRodneCislo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRodneCislo()));
        tableColumnDatumNarodenia.setCellValueFactory(param -> new SimpleStringProperty(Helper.FormatujDatum(param.getValue().getDatumNarodenia())));
        tableColumnPodiel.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getStaryPodiel()));


        Helper.InstallCopyPasteHandler(tableViewNehnutelnosti);
        Helper.InstallCopyPasteHandler(tableViewObcanPodiely);

    }

    private void clearFormulars() {
        buttonHladaj.disableProperty().unbind();
        buttonHladaj.disableProperty().set(false);
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
        return "6vyhladanieLVPodlaNazvuKU.fxml";
    }

    @Override
    public String getViewName() {
        return "6. Vyhľadanie LV podľa n. KÚ";
    }

    private class NacitajListVlastnictvaPodlaNazvuKU extends SimpleTask {
        ListVlastnictva listVlastnictva = null;

        @Override
        public boolean compute() {
            long cisloListuVlastnictva = 0;
            try {
                cisloListuVlastnictva = Long.valueOf(textFieldCisloListuVlastnictva.getText());
            } catch (NumberFormatException e) {
                return false;
            }
            listVlastnictva = isSpravyKatastra_.najdiListVlastnictva(textFieldNazovKatastralnehoUzemia.getText(), cisloListuVlastnictva);
            return listVlastnictva != null;
        }

        @Override
        public void onSuccess() {
            Helper.naplnTabulkuNehnutelnosti(tableViewNehnutelnosti, listVlastnictva.getNehnutelnostiNaListeVlastnictva());
            Helper.naplnTabulkuVlastnikov(tableViewObcanPodiely, listVlastnictva.getVlastniciSPodielom());

            showSuccessDialog("List vlastníctva bol úspešne načítaný");
        }

        @Override
        public void onFail() {
            showWarningDialog("Nepodarilo sa nájsť list vlastníctva");
        }

    }

}
