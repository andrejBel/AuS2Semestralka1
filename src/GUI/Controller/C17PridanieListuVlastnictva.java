package GUI.Controller;

import InformacnySystem.ISSpravyKatastra;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import static javafx.beans.binding.Bindings.createBooleanBinding;

public class C17PridanieListuVlastnictva extends ControllerBase {

    private static final String EMPTY_WARNING_MESSAGE = "Vstup musí byť zadaný";

    @FXML
    private JFXTextField textFieldNazovKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldCisloListuVlastnictva;

    @FXML
    private JFXButton buttonPridajListVlastnictva;

    private SimpleBooleanProperty isNazovKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCisloListuVlastnictvaOk = new SimpleBooleanProperty(false);

    public C17PridanieListuVlastnictva(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

        Helper.decorateTextFieldWithValidator(textFieldNazovKatastralnehoUzemia);
        Helper.decorateNumberTextFieldWithValidator(textFieldCisloListuVlastnictva);



        textFieldCisloListuVlastnictva.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textFieldCisloListuVlastnictva.setText(newValue.replaceAll("[^\\d]", ""));
            }
            boolean validateResult = textFieldCisloListuVlastnictva.validate();
            isCisloListuVlastnictvaOk.set(validateResult);
        });

        textFieldNazovKatastralnehoUzemia.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean validateResult = textFieldNazovKatastralnehoUzemia.validate();
            isNazovKUOk.set(validateResult);
        });

        buttonPridajListVlastnictva.setOnAction(event -> {
            if (setButtonPridajDisabled()) {
                return;
            }
            String nazovKatastralnehoUzemia = textFieldNazovKatastralnehoUzemia.getText();
            long cisloListuVlastnictva = 0;
            boolean parsed = true;
            try {
                cisloListuVlastnictva = Long.valueOf(textFieldCisloListuVlastnictva.getText());
            } catch (NumberFormatException e) {
                parsed = false;
            }
            boolean added = false;
            if (parsed) {
                added = isSpravyKatastra.pridajListVlastnictva(textFieldNazovKatastralnehoUzemia.getText(), cisloListuVlastnictva);
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
                label.setText("List vlastníctva úspešne pridaný");
                clearFormulars();
            } else {
                label.setTextFill(Color.RED);
                label.setText("List vlastníctva nebol pridaný");
            }
            dialog.show();

        });

    }

    private boolean setButtonPridajDisabled() {
        if (buttonPridajListVlastnictva.isDisable() == false && (!isNazovKUOk.get() || !isCisloListuVlastnictvaOk.get() )) {
            textFieldCisloListuVlastnictva.validate();
            textFieldNazovKatastralnehoUzemia.validate();

            buttonPridajListVlastnictva.disableProperty().unbind();
            buttonPridajListVlastnictva.disableProperty().bind(createBooleanBinding(
                    () -> !(isNazovKUOk.get() && isCisloListuVlastnictvaOk.get()), isNazovKUOk, isCisloListuVlastnictvaOk
            ));
            return true;
        }
        return false;
    }

    private void clearFormulars() {
        buttonPridajListVlastnictva.disableProperty().unbind();
        buttonPridajListVlastnictva.disableProperty().set(false);
        textFieldCisloListuVlastnictva.setText("");
        textFieldNazovKatastralnehoUzemia.setText("");
        textFieldCisloListuVlastnictva.resetValidation();
        textFieldNazovKatastralnehoUzemia.resetValidation();
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
}
