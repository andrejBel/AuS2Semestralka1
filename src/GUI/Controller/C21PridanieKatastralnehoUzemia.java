package GUI.Controller;

import InformacnySystem.ISSpravyKatastra;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import static javafx.beans.binding.Bindings.createBooleanBinding;

public class C21PridanieKatastralnehoUzemia extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldNazovKatastralnehoUzemia;

    @FXML
    private JFXButton buttonPridajUzemie;

    private SimpleBooleanProperty isCisloOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isNazovOk = new SimpleBooleanProperty(false);

    public C21PridanieKatastralnehoUzemia(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

        textFieldCisloKatastralnehoUzemia.getValidators().add(new RequiredFieldValidator() {
            {
                setMessage(Helper.EMPTY_WARNING_MESSAGE);
            }
        });
        textFieldCisloKatastralnehoUzemia.getValidators().add(new IntegerValidator() {
            {
                setMessage(Helper.EMPTY_WARNING_MESSAGE_NUMBER);
            }
        });

        textFieldNazovKatastralnehoUzemia.getValidators().add(new RequiredFieldValidator() {
            {
                setMessage(Helper.EMPTY_WARNING_MESSAGE);
            }
        });

        textFieldCisloKatastralnehoUzemia.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textFieldCisloKatastralnehoUzemia.setText(newValue.replaceAll("[^\\d]", ""));
            }
            boolean validateResult = textFieldCisloKatastralnehoUzemia.validate();
            isCisloOk.set(validateResult);
        });

        textFieldNazovKatastralnehoUzemia.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean validateResult = textFieldNazovKatastralnehoUzemia.validate();
            isNazovOk.set(validateResult);
        });


        buttonPridajUzemie.setOnAction(event -> {
            if (setButtonPridajDisabled()) {
                return;
            }
            long cisloKatastralnehoUzemia = 0;
            boolean parsed = true;
            try {
                cisloKatastralnehoUzemia = Long.parseLong(textFieldCisloKatastralnehoUzemia.getText());
            }catch (NumberFormatException e) {
                parsed = false;
            }
            boolean added = false;
            if (parsed) {
                added = isSpravyKatastra.pridajKatastralneUzemie(cisloKatastralnehoUzemia, textFieldNazovKatastralnehoUzemia.getText());
            }

            Label label = new Label();
            label.setStyle("-fx-font-weight: bold");
            label.setAlignment(Pos.CENTER);
            dialogVBox.getChildren().clear();
            dialogVBox.getChildren().addAll(label);
            JFXButton button = new JFXButton("Zavrieť");
            button.setOnAction(event1 -> {
                dialog.close();
            });
            dialogLayout.setActions(button);
            if (added && parsed) {
                label.setTextFill(Color.GREEN);
                label.setText("Katastrálne územie úspešne pridané");
                clearFormulars();
            } else {
                label.setTextFill(Color.RED);
                label.setText("Katastrálne územie nebolo pridané");
            }
            dialog.show();

        });

    }


    private boolean setButtonPridajDisabled() {
        if (buttonPridajUzemie.isDisable() == false && (!isNazovOk.get() || !isCisloOk.get() )) {
            textFieldCisloKatastralnehoUzemia.validate();
            textFieldNazovKatastralnehoUzemia.validate();

            buttonPridajUzemie.disableProperty().unbind();
            buttonPridajUzemie.disableProperty().bind(createBooleanBinding(
                    () -> !(isNazovOk.get() && isCisloOk.get()), isNazovOk, isCisloOk
            ));
            return true;
        }
        return false;
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
        textFieldCisloKatastralnehoUzemia.setText("");
        textFieldNazovKatastralnehoUzemia.setText("");
        textFieldCisloKatastralnehoUzemia.resetValidation();
        textFieldNazovKatastralnehoUzemia.resetValidation();
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
}
