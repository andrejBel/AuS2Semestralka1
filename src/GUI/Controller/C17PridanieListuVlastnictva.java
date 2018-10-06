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
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;


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

        Helper.decorateTextFieldWithValidator(textFieldNazovKatastralnehoUzemia, isNazovKUOk);
        Helper.decorateNumberTextFieldWithValidator(textFieldCisloListuVlastnictva, isCisloListuVlastnictvaOk);

        buttonPridajListVlastnictva.setOnAction(event -> {
            if (Helper.disableButton(buttonPridajListVlastnictva, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new PridanieListuVlastnictva().execute();
        });

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

        private Label label;

        public PridanieListuVlastnictva() {
            label = new Label();
            label.setStyle("-fx-font-weight: bold");
            label.setAlignment(Pos.CENTER);
            dialogVBox.getChildren().clear();
            dialogVBox.getChildren().addAll(label);
            JFXButton button = new JFXButton("Zavrieť");
            button.setOnAction(event1 -> {
                dialog.close();
            });
            dialogLayout.setActions(button);
        }

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
            label.setTextFill(Color.GREEN);
            label.setText("List vlastníctva úspešne pridaný");
            clearFormulars();
            dialog.show();
        }

        @Override
        public void onFail() {
            label.setTextFill(Color.RED);
            label.setText("List vlastníctva nebol pridaný");
            dialog.show();
        }
    }

}
