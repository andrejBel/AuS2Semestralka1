package GUI.Controller;

import GUI.SimpleTask;
import InformacnySystem.ISSpravyKatastra;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;

import java.util.Arrays;
import java.util.List;

public class C22OdstranenieKatastralnehoUzemia extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemiaOdstrovaneho;

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemiaNoveho;

    @FXML
    private JFXTextField textFieldNazovKatastralnehoUzemiaNove;

    @FXML
    private JFXButton buttonOdstranUzemie;

    private SimpleBooleanProperty isCisloOdstranovanehoOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCisloNovehoOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isNazovOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isCisloOdstranovanehoOk,
            isCisloNovehoOk,
            isNazovOk
    );

    private List<JFXTextField> textFields;

    public C22OdstranenieKatastralnehoUzemia(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

        textFields = Arrays.asList(
                textFieldCisloKatastralnehoUzemiaOdstrovaneho,
                textFieldCisloKatastralnehoUzemiaNoveho,
                textFieldNazovKatastralnehoUzemiaNove
        );

        Helper.DecorateNumberTextFieldWithValidator(textFieldCisloKatastralnehoUzemiaOdstrovaneho, isCisloOdstranovanehoOk);
        Helper.DecorateNumberTextFieldWithValidator(textFieldCisloKatastralnehoUzemiaNoveho, isCisloNovehoOk);
        Helper.DecorateTextFieldWithValidator(textFieldNazovKatastralnehoUzemiaNove, isNazovOk);

        Helper.SetActionOnEnter(textFields, () -> buttonOdstranUzemie.fire());

        buttonOdstranUzemie.setOnAction(event -> {
            if (Helper.DisableButton(buttonOdstranUzemie, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }

            new OdstranKatastralneUzemie().execute();

        });

    }

    @Override
    public Runnable getRunnableOnSelection() {
        return this::clearFormulars;
    }

    private void clearFormulars() {
        buttonOdstranUzemie.disableProperty().unbind();
        buttonOdstranUzemie.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
    }

    @Override
    protected void initView() {
        loadView();
    }

    @Override
    protected String getViewFileName() {
        return "22odstranenieKatastralenehoUzemia.fxml";
    }

    @Override
    public String getViewName() {
        return "22. Odstránenie katastrálneho územia";
    }

    private class OdstranKatastralneUzemie extends SimpleTask
    {

        @Override
        public boolean compute() {
            long cisloKatastralnehoUzemiaOdstraneneho = 0;
            try {
                cisloKatastralnehoUzemiaOdstraneneho = Long.parseLong(textFieldCisloKatastralnehoUzemiaOdstrovaneho.getText());
            } catch (NumberFormatException e) {
                return false;
            }
            long cisloKatastralnehoUzemiaNoveho = 0;
            try {
                cisloKatastralnehoUzemiaNoveho = Long.parseLong(textFieldCisloKatastralnehoUzemiaNoveho.getText());
            } catch (NumberFormatException e) {
                return false;
            }
            return isSpravyKatastra_.odstranAPresunKatastralneUzemie(cisloKatastralnehoUzemiaOdstraneneho, cisloKatastralnehoUzemiaNoveho, textFieldNazovKatastralnehoUzemiaNove.getText());
        }

        @Override
        public void onSuccess() {

            showSuccessDialog("Katastrálne územie bolo odstránené a jeho agenda presunutá do iného katastrálneho územia");


        }

        @Override
        public void onFail() {
            showWarningDialog("Katastrálne územie nebolo odstránené");
        }
    }
}
