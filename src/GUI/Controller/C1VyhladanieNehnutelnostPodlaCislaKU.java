package GUI.Controller;

import GUI.SimpleTask;
import GUI.View.ViewItems.TableItemNehnutelnostListVlastnictva;
import GUI.View.ViewItems.TableItemObcan;
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
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.xml.ws.Holder;
import java.util.Arrays;
import java.util.List;

public class C1VyhladanieNehnutelnostPodlaCislaKU extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldSupisneCisloNehnutelnosti;

    @FXML
    private JFXButton buttonHladaj;

    @FXML
    private TableView<TableItemObcan> tableViewObcaniaSTrvalymPobytom;

    @FXML
    private TableColumn<TableItemObcan, String> tableColumnMenoPriezviskoTP;

    @FXML
    private TableColumn<TableItemObcan, String> tableColumnRodneCisloTP;

    @FXML
    private TableColumn<TableItemObcan, String> tableColumnDatumNarodeniaTP;

    @FXML
    private TableView<TableItemNehnutelnostListVlastnictva> tableViewNehnutelnosti;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictva, Number> tableColumnSupisneCislo;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictva, String> tableColumnAdresa;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictva, String> tableColumnPopis;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictva, Number> tableColumnCisloListuVlastnictva;

    @FXML
    private TableView<TableItemObcanPodiel> tableViewObcanPodiely;

    @FXML
    private TableColumn<TableItemObcanPodiel, String>  tableColumnMenoPriezviskoVL;

    @FXML
    private TableColumn<TableItemObcanPodiel, String>  tableColumnRodneCisloVL;

    @FXML
    private TableColumn<TableItemObcanPodiel, String>  tableColumnDatumNarodeniaVL;

    @FXML
    private TableColumn<TableItemObcanPodiel, Number>  tableColumnPodiel;

    private SimpleBooleanProperty isCisloKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isSupisneCisloOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isCisloKUOk,
            isSupisneCisloOk
    );

    private List<JFXTextField> textFields;

    public C1VyhladanieNehnutelnostPodlaCislaKU(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldCisloKatastralnehoUzemia,
                textFieldSupisneCisloNehnutelnosti
        );
        Helper.DecorateNumberTextFieldWithValidator(textFieldCisloKatastralnehoUzemia, isCisloKUOk);
        Helper.DecorateNumberTextFieldWithValidator(textFieldSupisneCisloNehnutelnosti, isSupisneCisloOk);
        Helper.SetActionOnEnter(textFields, () -> buttonHladaj.fire());

        buttonHladaj.setOnAction(event -> {
            if (Helper.DisableButton(buttonHladaj, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new NacitajNehnutelnostPodlaCislaKU().execute();
        });

        tableColumnMenoPriezviskoTP.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMenoPriezvisko()));
        tableColumnRodneCisloTP.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRodneCislo()));
        tableColumnDatumNarodeniaTP.setCellValueFactory(param -> new SimpleStringProperty(Helper.FormatujDatum(param.getValue().getDatumNarodenia())));

        tableColumnSupisneCislo.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getSupisneCislo()));
        tableColumnAdresa.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAdresa()));
        tableColumnPopis.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPopis()));
        tableColumnCisloListuVlastnictva.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getCisloListuVlastnictva()));


        tableColumnMenoPriezviskoVL.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMenoPriezvisko()));
        tableColumnRodneCisloVL.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRodneCislo()));
        tableColumnDatumNarodeniaVL.setCellValueFactory(param -> new SimpleStringProperty(Helper.FormatujDatum(param.getValue().getDatumNarodenia())));
        tableColumnPodiel.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getStaryPodiel()));


        Helper.InstallCopyPasteHandler(tableViewObcaniaSTrvalymPobytom);
        Helper.InstallCopyPasteHandler(tableViewNehnutelnosti);
        Helper.InstallCopyPasteHandler(tableViewObcanPodiely);

        Helper.SetRowFactory(tableViewNehnutelnosti, TableItemNehnutelnostListVlastnictva -> {
            Nehnutelnost nehnutelnost = holderNehnutelnost_.value;
            return nehnutelnost != null && TableItemNehnutelnostListVlastnictva.getSupisneCislo() == nehnutelnost.getSupisneCislo();
        });
    }

    private void clearFormulars() {
        buttonHladaj.disableProperty().unbind();
        buttonHladaj.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
        tableViewObcaniaSTrvalymPobytom.getItems().clear();
        tableViewNehnutelnosti.getItems().clear();
        tableViewObcanPodiely.getItems().clear();
        holderNehnutelnost_.value = null;
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
        return "1vyhladaniaNehnutelnostiPodlaCislaKU.fxml";
    }

    @Override
    public String getViewName() {
        return "1. Vyhľadanie nehnuteľnosti podľa čísla KU";
    }

    private Holder<Nehnutelnost> holderNehnutelnost_ = new Holder<>();

    private class NacitajNehnutelnostPodlaCislaKU extends SimpleTask {


        @Override
        public boolean compute() {

            long cisloKatastralnehoUzemia = 0;
            try {
                cisloKatastralnehoUzemia = Long.valueOf(textFieldCisloKatastralnehoUzemia.getText());
            } catch (NumberFormatException e) {
                return false;
            }
            long supisneCisloNehnutelonosti = 0;
            try {
                supisneCisloNehnutelonosti = Long.valueOf(textFieldSupisneCisloNehnutelnosti.getText());
            } catch (NumberFormatException e) {
                return false;
            }
            Nehnutelnost nehnutelnost = isSpravyKatastra_.najdiNehnutelnostVKU(cisloKatastralnehoUzemia, supisneCisloNehnutelonosti);
            holderNehnutelnost_.value = nehnutelnost;
            return nehnutelnost != null;
        }

        @Override
        public void onSuccess() {
            Nehnutelnost nehnutelnost = holderNehnutelnost_.value;

            ListVlastnictva listVlastnictva = nehnutelnost.getListVlastnictva();
            Helper.naplnTabulkuObcaniaSTrvalymPobytom(tableViewObcaniaSTrvalymPobytom, nehnutelnost.getObcaniaSTrvalymPobytom());
            Helper.naplnTabulkuNehnutelnostiSListomVlastnictva(tableViewNehnutelnosti, listVlastnictva.getNehnutelnostiNaListeVlastnictva());
            Helper.naplnTabulkuVlastnikov(tableViewObcanPodiely, listVlastnictva.getVlastniciSPodielom());


            showSuccessDialog("Nehnuteľnosť bola úspešne nájdená");
        }

        @Override
        public void onFail() {
            showWarningDialog("Nepodarilo sa nájsť nehnuteľnosť");
        }
    }

}
