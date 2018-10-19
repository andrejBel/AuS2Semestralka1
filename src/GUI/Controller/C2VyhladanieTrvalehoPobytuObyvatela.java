package GUI.Controller;

import GUI.SimpleTask;
import GUI.View.ViewItems.TableItemNehnutelnostListVlastnictva;
import GUI.View.ViewItems.TableItemObcan;
import GUI.View.ViewItems.TableItemObcanPodiel;
import InformacnySystem.ISSpravyKatastra;
import Model.ListVlastnictva;
import Model.Nehnutelnost;
import Model.Obcan;
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

public class C2VyhladanieTrvalehoPobytuObyvatela extends ControllerBase {

    @FXML
    private JFXTextField textFieldRodneCislo;

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
    private TableColumn<TableItemObcanPodiel, String> tableColumnMenoPriezviskoVL;

    @FXML
    private TableColumn<TableItemObcanPodiel, String> tableColumnRodneCisloVL;

    @FXML
    private TableColumn<TableItemObcanPodiel, String> tableColumnDatumNarodeniaVL;

    @FXML
    private TableColumn<TableItemObcanPodiel, Number> tableColumnPodiel;

    private SimpleBooleanProperty isRodneCisloOk = new SimpleBooleanProperty(false);


    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
        isRodneCisloOk
    );

    private List<JFXTextField> textFields;

    public C2VyhladanieTrvalehoPobytuObyvatela(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldRodneCislo
        );
        Helper.DecorateTextFieldWithValidator(textFieldRodneCislo, isRodneCisloOk, Obcan.RODNE_CISLO_LENGTH, "Rodné číslo");

        Helper.SetActionOnEnter(textFields, () -> buttonHladaj.fire());

        buttonHladaj.setOnAction(event -> {
            if (Helper.DisableButton(buttonHladaj, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new NacitajTrvalyPobytObcana().execute();
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

        Helper.SetRowFactory(tableViewNehnutelnosti, tableItemNehnutelnost -> {
            Nehnutelnost nehnutelnost = holderNehnutelnost_.value;
            return nehnutelnost != null && tableItemNehnutelnost.getSupisneCislo() == nehnutelnost.getSupisneCislo();
        });
        Helper.SetRowFactory(tableViewObcaniaSTrvalymPobytom, tableItemObcan -> {
            Obcan obcan = holderObcan_.value;
            return obcan != null && tableItemObcan.getRodneCislo().equals(obcan.getRodneCislo());
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
        holderObcan_.value = null;
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
        return "2VyhladanieTrvalehoPobytuObyvatela.fxml";
    }

    @Override
    public String getViewName() {
        return "2. Vyhľadanie trvalého pobytu obyvateľa";
    }

    private Holder<Nehnutelnost> holderNehnutelnost_ = new Holder<>();
    private Holder<Obcan> holderObcan_ = new Holder<>();

    private class NacitajTrvalyPobytObcana extends SimpleTask {


        @Override
        public boolean compute() {

            Obcan obcan = isSpravyKatastra_.najdiObcana(textFieldRodneCislo.getText());
            if (obcan != null) {
                holderObcan_.value = obcan;
                holderNehnutelnost_.value = obcan.getTrvalyPobyt();
            }
            return obcan != null;
        }

        @Override
        public void onSuccess() {
            Obcan obcan = holderObcan_.value;

            Nehnutelnost nehnutelnost = holderNehnutelnost_.value;
            if (nehnutelnost != null) {
                showSuccessDialog("Trvalý pobyt občana nájdený");
                ListVlastnictva listVlastnictva = nehnutelnost.getListVlastnictva();
                Helper.naplnTabulkuObcaniaSTrvalymPobytom(tableViewObcaniaSTrvalymPobytom, nehnutelnost.getObcaniaSTrvalymPobytom());
                Helper.naplnTabulkuNehnutelnostiSListomVlastnictva(tableViewNehnutelnosti, listVlastnictva.getNehnutelnostiNaListeVlastnictva());
                Helper.naplnTabulkuVlastnikov(tableViewObcanPodiely, listVlastnictva.getVlastniciSPodielom());
            } else {
                showSuccessDialog("Občan nemá trvalý pobyt");
            }


        }

        @Override
        public void onFail() {
            showWarningDialog("Nepodarilo sa nájsť občana");
        }
    }

}
