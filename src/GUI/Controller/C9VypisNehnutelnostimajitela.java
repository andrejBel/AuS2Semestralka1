package GUI.Controller;


import GUI.View.ViewItems.TableItemNehnutelnostListVlastnictvaKU;
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

public class C9VypisNehnutelnostimajitela extends ControllerBase {

    @FXML
    private JFXTextField textFieldRodneCislo;

    @FXML
    private JFXButton buttonHladaj;

    @FXML
    private TableView<TableItemNehnutelnostListVlastnictvaKU> tableViewNehnutelnostiListVlastnictvaKU;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictvaKU, Number> tableColumnSupisneCislo;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictvaKU, String> tableColumnAdresa;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictvaKU, String> tableColumnPopis;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictvaKU, Number> tableColumnPodiel;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictvaKU, Number> tableColumnCisloLV;

    @FXML
    private TableColumn<TableItemNehnutelnostListVlastnictvaKU, Number> tableColumnCisloKU;

    private SimpleBooleanProperty isRodneCisloOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isRodneCisloOk
    );

    private List<JFXTextField> textFields;

    public C9VypisNehnutelnostimajitela(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldRodneCislo
        );

        Helper.DecorateTextFieldWithValidator(textFieldRodneCislo, isRodneCisloOk, 16, "Rodné číslo");
        buttonHladaj.setOnAction(event -> {
            if (Helper.DisableButton(buttonHladaj, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            nacitajData();
        });

        tableColumnSupisneCislo.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getSupisneCislo()));
        tableColumnAdresa.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAdresa()));
        tableColumnPopis.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPopis()));
        tableColumnPodiel.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getPodiel()));
        tableColumnCisloLV.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getCisloListuVlastnictva()));
        tableColumnCisloKU.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getCisloKatastralnehoUzemia()));

        textFields.forEach(jfxTextField -> jfxTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
            {
                buttonHladaj.fire();
            }
        }));
    }

    private void nacitajData() {
        ObservableList<TableItemNehnutelnostListVlastnictvaKU> tableViewNehnutelnostiListVlastnictvaItems = tableViewNehnutelnostiListVlastnictvaKU.getItems();
        tableViewNehnutelnostiListVlastnictvaItems.clear();
        Obcan obcan = isSpravyKatastra_.najdiObcana(textFieldRodneCislo.getText());
        if (obcan != null) {
            AvlTree< Pair<Long, AvlTree<ListVlastnictva>> > obcanoveListyVlastnictva = obcan.getListyVlatnictva();
            if (obcanoveListyVlastnictva.getSize() == 0) {
                showSuccessDialog("Oban nemá žiadne nehnuteľnosti");
            } else {
                showSuccessDialog("Nehnuteľnosti boli úspešne načítané");
            }

            for (Pair<Long, AvlTree<ListVlastnictva>> listyVKU: obcanoveListyVlastnictva) {
                long cisloKU = listyVKU.getKey();
                AvlTree<ListVlastnictva> listyVlastnictva = listyVKU.getValue();
                for (ListVlastnictva listVlastnictva: listyVlastnictva) {
                    ListVlastnictva.ObcanSPodielom obcanSPodielom = listVlastnictva.getObcanSPodielom(obcan);
                    for (Nehnutelnost nehnutelnost: listVlastnictva.getNehnutelnostiNaListeVlastnictva()) {
                        TableItemNehnutelnostListVlastnictvaKU tableItemNehnutelnostListVlastnictvaKU = new TableItemNehnutelnostListVlastnictvaKU(
                                nehnutelnost.getSupisneCislo(),
                                nehnutelnost.getAdresa(),
                                nehnutelnost.getPopis(),
                                obcanSPodielom.getPodiel(),
                                listVlastnictva.getCisloListuVlastnictva(),
                                cisloKU
                        );
                        tableViewNehnutelnostiListVlastnictvaItems.add(tableItemNehnutelnostListVlastnictvaKU);
                    }
                }

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
        tableViewNehnutelnostiListVlastnictvaKU.getItems().clear();
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
        return "9vypisNehnutelnosti.fxml";
    }

    @Override
    public String getViewName() {
        return "9. Výpis nehnuteľností";
    }
}
