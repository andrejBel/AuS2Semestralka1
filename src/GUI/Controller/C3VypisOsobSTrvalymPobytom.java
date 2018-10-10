package GUI.Controller;

import GUI.SimpleTask;
import GUI.View.ViewItems.TableItemNehnutelnost;
import GUI.View.ViewItems.TableItemObcan;
import InformacnySystem.ISSpravyKatastra;
import Model.Nehnutelnost;
import Model.Obcan;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import structures.AvlTree;

import java.util.Arrays;
import java.util.List;

import static javafx.beans.binding.Bindings.createBooleanBinding;

public class C3VypisOsobSTrvalymPobytom extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldCisloListuVlastnictva;

    @FXML
    private JFXTextField textFieldSupisneCisloNehnutelnosti;

    @FXML
    private JFXButton buttonHladaj;

    @FXML
    private TableView<TableItemObcan> tableViewObcan;

    @FXML
    private TableColumn<TableItemObcan, String> tableColumnMenoPriezvisko;

    @FXML
    private TableColumn<TableItemObcan, String> tableColumnRodneCislo;

    @FXML
    private TableColumn<TableItemObcan, String> tableColumnDatumNarodenia;

    private SimpleBooleanProperty isCisloKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCisloListuVlastnictvaOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isSupisneCisloOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isCisloKUOk,
            isCisloListuVlastnictvaOk,
            isSupisneCisloOk
    );

    private List<JFXTextField> textFields;

    public C3VypisOsobSTrvalymPobytom(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldCisloKatastralnehoUzemia,
                textFieldCisloListuVlastnictva,
                textFieldSupisneCisloNehnutelnosti
        );
        Helper.DecorateNumberTextFieldWithValidator(textFieldCisloKatastralnehoUzemia, isCisloKUOk);
        Helper.DecorateNumberTextFieldWithValidator(textFieldCisloListuVlastnictva, isCisloListuVlastnictvaOk);
        Helper.DecorateNumberTextFieldWithValidator(textFieldSupisneCisloNehnutelnosti, isSupisneCisloOk);
        buttonHladaj.setOnAction(event -> {
            if (Helper.DisableButton(buttonHladaj, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new nacitajOsobyStravalymPobytomVnehnutelnosti().execute();
        });
        Helper.SetActionOnEnter(textFields, () -> buttonHladaj.fire());
        tableViewObcan.setRowFactory(tv -> {
            System.out.println();
            TableRow<TableItemObcan> row = new TableRow<>();
            BooleanBinding binding = row.itemProperty().isNotNull();

            StringBinding stringBinding = Bindings.createStringBinding(() -> {
                if (row.itemProperty().isNotNull().get()) {
                    TableItemObcan tableItemObcan = row.getItem();
                    if (tableItemObcan.getMenoPriezvisko().equals("Andrej Beliancin")) {
                        return "-fx-background-color: red ;";
                    }
                }
                return "";
            }, row.itemProperty());
            row.styleProperty().bind(stringBinding);

            return row ;
        });

        tableColumnMenoPriezvisko.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMenoPriezvisko()));
        tableColumnRodneCislo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRodneCislo()));
        tableColumnDatumNarodenia.setCellValueFactory(param -> new SimpleStringProperty(Helper.FormatujDatum(param.getValue().getDatumNarodenia())));
        Helper.InstallCopyPasteHandler(tableViewObcan);
    }

    private void clearFormulars() {
        buttonHladaj.disableProperty().unbind();
        buttonHladaj.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
        tableViewObcan.getItems().clear();
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
        return "3.vypisOsTrvPobytomVNe.fxml";
    }

    @Override
    public String getViewName() {
        return "3. Osoby s trvalým pobytom v nehnuteľnosti";
    }

    private class nacitajOsobyStravalymPobytomVnehnutelnosti extends SimpleTask {

        Nehnutelnost nehnutelnost_ = null;

        @Override
        public boolean compute() {
            long cisloKatastralnehoUzemia = 0;
            long cisloListuVlastnictva = 0;
            long supisneCisloNehnutelnosti = 0;

            try {
                cisloKatastralnehoUzemia= Long.parseLong(textFieldCisloKatastralnehoUzemia.getText());
            }catch (NumberFormatException e) {
                return false;
            }
            try {
                cisloListuVlastnictva= Long.parseLong(textFieldCisloListuVlastnictva.getText());
            }catch (NumberFormatException e) {
                return false;
            }
            try {
                supisneCisloNehnutelnosti= Long.parseLong(textFieldSupisneCisloNehnutelnosti.getText());
            }catch (NumberFormatException e) {
                return false;
            }
            nehnutelnost_ = isSpravyKatastra_.najdiNehnutelnost(cisloKatastralnehoUzemia, cisloListuVlastnictva, supisneCisloNehnutelnosti);
            return nehnutelnost_ != null;
        }

        @Override
        public void onSuccess() {
            AvlTree<Obcan> obcaniaStrvalymPobytom = nehnutelnost_.getObcaniaSTravalymPobytom();
            showSuccessDialog("Nehnuteľnosť bola nájdená. " + obcaniaStrvalymPobytom.getSize() + " obyvateľov má v nej trvalý pobyt");
            Helper.naplnTabulkuObcaniaSTravlymPobytom(tableViewObcan, obcaniaStrvalymPobytom);
        }

        @Override
        public void onFail() {
            showWarningDialog("Nehnuteľnosť sa nepodarilo nájsť");
        }
    }

}
