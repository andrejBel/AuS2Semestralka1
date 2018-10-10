package GUI.Controller;

import GUI.View.ViewItems.TableItemNehnutelnostListVlastnictva;
import InformacnySystem.ISSpravyKatastra;
import Model.ListVlastnictva;
import Model.Nehnutelnost;
import Model.Obcan;
import Utils.Helper;
import Utils.Pair;
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

public class C8VypisNehnutelnostiMajitelaVKU extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldRodneCislo;

    @FXML
    private JFXButton buttonHladaj;

    @FXML
    private TableView<TableItemNehnutelnostListVlastnictva> tableViewNehnutelnostiListVlastnictva;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictva, Number> tableColumnSupisneCislo;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictva, String> tableColumnAdresa;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictva, String> tableColumnPopis;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictva, Number> tableColumnPodiel;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictva, Number> tableColumnCisloLV;

    private SimpleBooleanProperty isCisloKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isRodneCisloOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isCisloKUOk,
            isRodneCisloOk
    );

    private List<JFXTextField> textFields;

    public C8VypisNehnutelnostiMajitelaVKU(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

        textFields = Arrays.asList(
                textFieldCisloKatastralnehoUzemia,
                textFieldRodneCislo
        );

        Helper.DecorateNumberTextFieldWithValidator( textFieldCisloKatastralnehoUzemia, isCisloKUOk);
        Helper.DecorateTextFieldWithValidator(textFieldRodneCislo, isRodneCisloOk, Obcan.RODNE_CISLO_LENGTH, "Rodné číslo");

        buttonHladaj.setOnAction(event -> {
            if (Helper.DisableButton(buttonHladaj, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            nacitajData();
        });
        Helper.SetActionOnEnter(textFields, () -> buttonHladaj.fire());


        tableColumnSupisneCislo.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getSupisneCislo()));
        tableColumnAdresa.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAdresa()));
        tableColumnPopis.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPopis()));
        tableColumnPodiel.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getPodiel()));
        tableColumnCisloLV.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getCisloListuVlastnictva()));


    }

    private void nacitajData() {
        ObservableList<TableItemNehnutelnostListVlastnictva> tableViewNehnutelnostiListVlastnictvaItems = tableViewNehnutelnostiListVlastnictva.getItems();
        tableViewNehnutelnostiListVlastnictvaItems.clear();
        long cisloKatastralnhoUzemia = 0;
        try {
            cisloKatastralnhoUzemia = Long.valueOf(textFieldCisloKatastralnehoUzemia.getText());
        } catch (NumberFormatException e) {
            showWarningDialog("Nesprávne číslo katastrálneho územia");
            return;
        }
        Pair<Obcan, AvlTree<ListVlastnictva>> obcanSListamiVlastnictva = isSpravyKatastra_.getObcanoveListyVlastnictvaPodlaKatastralnehoUzemia(cisloKatastralnhoUzemia, textFieldRodneCislo.getText());
        if (obcanSListamiVlastnictva != null) {
            Obcan obcan = obcanSListamiVlastnictva.getKey();
            AvlTree<ListVlastnictva> listyVlastnictva = obcanSListamiVlastnictva.getValue();
            if (listyVlastnictva != null) {
                showSuccessDialog("Nehnuteľnosti boli úspešne načítané");
                for (ListVlastnictva listVlastnictva: listyVlastnictva) {
                    ListVlastnictva.ObcanSPodielom obcanSPodielom = listVlastnictva.getObcanSPodielom(obcan);
                    for (Nehnutelnost nehnutelnost: listVlastnictva.getNehnutelnostiNaListeVlastnictva()) {
                        TableItemNehnutelnostListVlastnictva tableItemNehnutelnostListVlastnictva = new TableItemNehnutelnostListVlastnictva(
                                nehnutelnost.getSupisneCislo(),
                                nehnutelnost.getAdresa(),
                                nehnutelnost.getPopis(),
                                obcanSPodielom.getPodiel(),
                                listVlastnictva.getCisloListuVlastnictva()
                        );
                        tableViewNehnutelnostiListVlastnictvaItems.add(tableItemNehnutelnostListVlastnictva);
                    }
                }
            } else {
                showSuccessDialog("Vlastník nemá v danom katastrálnom území žiadnu nehnuteľnosť");
            }
        } else {
            showWarningDialog("Nepodarilo sa načítať nehnuteľnosti");
        }
    }

    private void clearFormulars() {
        buttonHladaj.disableProperty().unbind();
        buttonHladaj.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
        tableViewNehnutelnostiListVlastnictva.getItems().clear();
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
        return "8vypisNehnutelnostiMajitelaVKU.fxml";
    }

    @Override
    public String getViewName() {
        return "8. Výpis nehnuteľností majiteľa v KÚ";
    }
}
