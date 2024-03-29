package GUI.Controller;

import GUI.View.ViewItems.TableItemNehnutelnost;
import InformacnySystem.ISSpravyKatastra;
import Model.Nehnutelnost;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import Structures.AvlTree;

import java.util.Arrays;
import java.util.List;

public class C7VypisNehnutelnosti extends ControllerBase {

    @FXML
    private JFXTextField textFieldNazovKatastralnehoUzemia;

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

    private SimpleBooleanProperty isNazovKUOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isNazovKUOk
    );

    private List<JFXTextField> textFields;

    public C7VypisNehnutelnosti(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

        textFields = Arrays.asList(
                textFieldNazovKatastralnehoUzemia
        );

        Helper.DecorateTextFieldWithValidator(textFieldNazovKatastralnehoUzemia, isNazovKUOk);

        buttonHladaj.setOnAction(event -> {
            if (Helper.DisableButton(buttonHladaj, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            nacitajNehnutelnosti();

        });
        Helper.SetActionOnEnter(textFields, () -> buttonHladaj.fire());

        tableColumnSupisneCislo.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getSupisneCislo()));
        tableColumnAdresa.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAdresa()));
        tableColumnPopis.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPopis()));
    }

    private void nacitajNehnutelnosti() {
        AvlTree<Nehnutelnost> nehnutelnosti = isSpravyKatastra_.getNehnutelnostiKatastralnehoUzemia(textFieldNazovKatastralnehoUzemia.getText());
        if (nehnutelnosti != null) {
            Helper.naplnTabulkuNehnutelnosti(tableViewNehnutelnosti, nehnutelnosti);
        } else {
            showWarningDialog("Katastrálne územie nebolo nájdené");
        }
    }

    private void clearFormulars() {
        buttonHladaj.disableProperty().unbind();
        buttonHladaj.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
        tableViewNehnutelnosti.getItems().clear();
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
        return "7vypisNehnutelnosti.fxml";
    }

    @Override
    public String getViewName() {
        return "7. Výpis nehnutelností KÚ";
    }



}
