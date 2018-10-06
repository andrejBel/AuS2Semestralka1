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

public class C18PridanieNehnutelnosti extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldCisloListuVlastnictva;

    @FXML
    private JFXTextField textFieldSupisneCisloNehnutelnosti;

    @FXML
    private JFXTextField textFieldAdresaNehnutelnosti;

    @FXML
    private JFXTextField textFieldPopisNehnutelnosti;

    @FXML
    private JFXButton buttonPridajNehnutelnost;

    private SimpleBooleanProperty isCisloKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCisloListuVlastnictvaOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isSupisneCisloOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isAdresaNehnutelnostiOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isPopisNehnutelnostiOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isCisloKUOk,
            isCisloListuVlastnictvaOk,
            isSupisneCisloOk,
            isAdresaNehnutelnostiOk,
            isPopisNehnutelnostiOk
    );

    private List<JFXTextField> textFields;

    public C18PridanieNehnutelnosti(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldCisloKatastralnehoUzemia,
                textFieldCisloListuVlastnictva,
                textFieldSupisneCisloNehnutelnosti,
                textFieldAdresaNehnutelnosti,
                textFieldPopisNehnutelnosti
        );


        Helper.decorateNumberTextFieldWithValidator(textFieldCisloKatastralnehoUzemia, isCisloKUOk);
        Helper.decorateNumberTextFieldWithValidator(textFieldCisloListuVlastnictva, isCisloListuVlastnictvaOk);
        Helper.decorateNumberTextFieldWithValidator(textFieldSupisneCisloNehnutelnosti, isSupisneCisloOk);
        Helper.decorateTextFieldWithValidator(textFieldAdresaNehnutelnosti, isAdresaNehnutelnostiOk);
        Helper.decorateTextFieldWithValidator(textFieldPopisNehnutelnosti, isPopisNehnutelnostiOk);

        buttonPridajNehnutelnost.setOnAction(event -> {
            if (Helper.disableButton(buttonPridajNehnutelnost, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }

            new PridajNehnutelnost().execute();
        });


    }

    private void clearFormulars() {
        buttonPridajNehnutelnost.disableProperty().unbind();
        buttonPridajNehnutelnost.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
    }

    @Override
    public Runnable getRunnableOnSelection() {
        return () -> clearFormulars();
    }

    @Override
    protected void initView() {
        loadView();
    }

    @Override
    protected String getViewFileName() {
        return "18pridanieNehnutelnosti.fxml";
    }

    @Override
    public String getViewName() {
        return "18. Pridanie nehnuteľnosti na list vlastníctva";
    }

    private class PridajNehnutelnost extends SimpleTask {

        private Label label;

        PridajNehnutelnost() {
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
            /*
            * textFieldCisloKatastralnehoUzemia,
                textFieldCisloListuVlastnictva,
                textFieldSupisneCisloNehnutelnosti,
                textFieldAdresaNehnutelnosti,
                textFieldPopisNehnutelnosti*/
            long cisloKatastralnehoUzemia = 0;
            long cisloListuVlastnictva = 0;
            long supisneCisloNehnutelnosti = 0;

            try {
                cisloKatastralnehoUzemia= Long.parseLong(textFieldCisloKatastralnehoUzemia.getText());
            }catch (NumberFormatException e) {
                return false;
            }
            try {
                cisloListuVlastnictva= Long.parseLong(textFieldCisloListuVlastnictva.getText());
            }catch (NumberFormatException e) {
                return false;
            }
            try {
                supisneCisloNehnutelnosti= Long.parseLong(textFieldSupisneCisloNehnutelnosti.getText());
            }catch (NumberFormatException e) {
                return false;
            }
            return isSpravyKatastra_.pridajNehnutelnostNaListVlastnictva(cisloKatastralnehoUzemia, cisloListuVlastnictva, supisneCisloNehnutelnosti, textFieldAdresaNehnutelnosti.getText(), textFieldPopisNehnutelnosti.getText());
        }

        @Override
        public void onSuccess() {
            label.setTextFill(Color.GREEN);
            label.setText("Nehnuteľnosť úspešne pridaná");
            clearFormulars();
            dialog.show();
        }

        @Override
        public void onFail() {
            label.setTextFill(Color.RED);
            label.setText("Nehnuteľnosť nebola pridaná");
            dialog.show();
        }
    }

}
