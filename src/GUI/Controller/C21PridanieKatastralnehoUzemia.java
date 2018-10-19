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


public class C21PridanieKatastralnehoUzemia extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldNazovKatastralnehoUzemia;

    @FXML
    private JFXButton buttonPridajUzemie;

    private SimpleBooleanProperty isCisloOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isNazovOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isCisloOk,
            isNazovOk
    );

    private List<JFXTextField> textFields;

    public C21PridanieKatastralnehoUzemia(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

        textFields = Arrays.asList(
                textFieldCisloKatastralnehoUzemia,
                textFieldNazovKatastralnehoUzemia
        );

        Helper.DecorateNumberTextFieldWithValidator(textFieldCisloKatastralnehoUzemia, isCisloOk);
        Helper.DecorateTextFieldWithValidator(textFieldNazovKatastralnehoUzemia, isNazovOk);


        buttonPridajUzemie.setOnAction(event -> {
            if (Helper.DisableButton(buttonPridajUzemie, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }

            new PridajKatastralneUzemie().execute();

        });
        Helper.SetActionOnEnter(textFields, () -> buttonPridajUzemie.fire());
    }

    @Override
    public Runnable getRunnableOnSelection() {
        return this::clearFormulars;
    }

    private void clearFormulars() {
        buttonPridajUzemie.disableProperty().unbind();
        buttonPridajUzemie.disableProperty().set(false);
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
        return "21pridanieKatastralnehoUzemia.fxml";
    }

    @Override
    public String getViewName() {
        return "21. Pridanie katastrálneho územia";
    }

    private class PridajKatastralneUzemie extends SimpleTask {

        @Override
        public boolean compute() {
            long cisloKatastralnehoUzemia = 0;
            try {
                cisloKatastralnehoUzemia = Long.parseLong(textFieldCisloKatastralnehoUzemia.getText());
            } catch (NumberFormatException e) {
                return false;
            }
            return isSpravyKatastra_.pridajKatastralneUzemie(cisloKatastralnehoUzemia, textFieldNazovKatastralnehoUzemia.getText());
        }


        @Override
        public void onSuccess() {
            showSuccessDialog("Katastrálne územie úspešne pridané");
            clearFormulars();
        }

        @Override
        public void onFail() {
            showWarningDialog("Katastrálne územie nebolo pridané");
        }
    }

}
