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

        private Label label;

        public PridajKatastralneUzemie() {
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
            label.setTextFill(Color.GREEN);
            label.setText("Katastrálne územie úspešne pridané");
            clearFormulars();
            dialog.show();
        }

        @Override
        public void onFail() {
            label.setTextFill(Color.RED);
            label.setText("Katastrálne územie nebolo pridané");
            dialog.show();
        }
    }

}
