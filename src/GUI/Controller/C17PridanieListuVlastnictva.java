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


public class C17PridanieListuVlastnictva extends ControllerBase {


    @FXML
    private JFXTextField textFieldNazovKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldCisloListuVlastnictva;

    @FXML
    private JFXButton buttonPridajListVlastnictva;

    private SimpleBooleanProperty isNazovKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCisloListuVlastnictvaOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isNazovKUOk,
            isCisloListuVlastnictvaOk
    );

    private List<JFXTextField> textFields;

    public C17PridanieListuVlastnictva(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldNazovKatastralnehoUzemia,
                textFieldCisloListuVlastnictva
        );

        Helper.DecorateTextFieldWithValidator(textFieldNazovKatastralnehoUzemia, isNazovKUOk);
        Helper.DecorateNumberTextFieldWithValidator(textFieldCisloListuVlastnictva, isCisloListuVlastnictvaOk);

        buttonPridajListVlastnictva.setOnAction(event -> {
            if (Helper.DisableButton(buttonPridajListVlastnictva, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new PridanieListuVlastnictva().execute();
        });
        textFields.forEach(jfxTextField -> jfxTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
            {
                buttonPridajListVlastnictva.fire();
            }
        }));

    }

    private void clearFormulars() {
        buttonPridajListVlastnictva.disableProperty().unbind();
        buttonPridajListVlastnictva.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
    }

    @Override
    public Runnable getRunnableOnSelection() {
        return () -> {
            clearFormulars();
        };
    }

    @Override
    protected void initView() {
        loadView();
    }

    @Override
    protected String getViewFileName() {
        return "17pridanieListuVlastnictva.fxml";
    }

    @Override
    public String getViewName() {
        return "17. Pridanie listu vlastníctva";
    }

    private class PridanieListuVlastnictva extends SimpleTask {

        @Override
        public boolean compute() {
            String nazovKatastralnehoUzemia = textFieldNazovKatastralnehoUzemia.getText();
            long cisloListuVlastnictva = 0;
            try {
                cisloListuVlastnictva = Long.valueOf(textFieldCisloListuVlastnictva.getText());
            } catch (NumberFormatException e) {
                return false;
            }
            boolean added = false;
            return isSpravyKatastra_.pridajListVlastnictva(textFieldNazovKatastralnehoUzemia.getText(), cisloListuVlastnictva);
        }

        @Override
        public void onSuccess() {
            showSuccessDialog("List vlastníctva úspešne pridaný");
            clearFormulars();
        }

        @Override
        public void onFail() {
            showWarningDialog("List vlastníctva nebol pridaný");
        }
    }

}
