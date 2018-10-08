package GUI.Controller;

import GUI.SimpleTask;
import InformacnySystem.ISSpravyKatastra;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;

import java.util.Arrays;
import java.util.List;

public class C10PridanieTrvalehoPobytu extends ControllerBase {


    @FXML
    private JFXTextField textFieldNazovKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldSupisneCisloNehnutelnosti;

    @FXML
    private JFXTextField textFieldRodneCislo;

    @FXML
    private JFXButton buttonPridajTrvalyPobyt;


    private SimpleBooleanProperty isNazovKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isSupisneCisloOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isRodneCisloOk = new SimpleBooleanProperty(false);


    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isNazovKUOk,
            isSupisneCisloOk,
            isRodneCisloOk
    );

    private List<JFXTextField> textFields;

    public C10PridanieTrvalehoPobytu(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldNazovKatastralnehoUzemia,
                textFieldSupisneCisloNehnutelnosti,
                textFieldRodneCislo
        );
        Helper.DecorateTextFieldWithValidator(textFieldNazovKatastralnehoUzemia, isNazovKUOk);
        Helper.DecorateNumberTextFieldWithValidator( textFieldSupisneCisloNehnutelnosti, isSupisneCisloOk);
        Helper.DecorateTextFieldWithValidator(textFieldRodneCislo, isRodneCisloOk, 16, "Rodné číslo");

        buttonPridajTrvalyPobyt.setOnAction(event -> {
            if (Helper.DisableButton(buttonPridajTrvalyPobyt, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new PridajTrvalyPobyt().execute();
        });
        textFields.forEach(jfxTextField -> jfxTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
            {
                buttonPridajTrvalyPobyt.fire();
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
        buttonPridajTrvalyPobyt.disableProperty().unbind();
        buttonPridajTrvalyPobyt.disableProperty().set(false);
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
        return "10pridanieTrvalehoPobytu.fxml";
    }

    @Override
    public String getViewName() {
        return "10. Pridanie trvalého pobytu";
    }

    private class PridajTrvalyPobyt extends SimpleTask {

        @Override
        public boolean compute() {
            long supisneCisloNehnutelnosti = 0;
            try {
                supisneCisloNehnutelnosti = Long.parseLong(textFieldSupisneCisloNehnutelnosti.getText());
            } catch (NumberFormatException e) {
                return false;
            }
            return isSpravyKatastra_.zapisObcanoviTrvalyPobyt(textFieldNazovKatastralnehoUzemia.getText(), supisneCisloNehnutelnosti, textFieldRodneCislo.getText());
        }

        @Override
        public void onSuccess() {
            showSuccessDialog("Občanovi bol zmenený trvalý pobyt");
            clearFormulars();
        }

        @Override
        public void onFail() {
            showWarningDialog("Občanovi nebol zmenený trvalý pobyt");
        }
    }

}
