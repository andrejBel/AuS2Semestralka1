package GUI.Controller;

import GUI.SimpleTask;
import InformacnySystem.ISSpravyKatastra;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

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

        Helper.decorateNumberTextFieldWithValidator(textFieldCisloKatastralnehoUzemia, isCisloOk);
        Helper.decorateTextFieldWithValidator(textFieldNazovKatastralnehoUzemia, isNazovOk);


        buttonPridajUzemie.setOnAction(event -> {
            if (Helper.disableButton(buttonPridajUzemie, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }

            new PridajKatastralneUzemie().execute();

        });
        textFields.forEach(jfxTextField -> jfxTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
            {
                buttonPridajUzemie.fire();
            }
        }));
    }

    @Override
    public Runnable getRunnableOnSelection() {
        return () -> {
            clearFormulars();
        };
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
