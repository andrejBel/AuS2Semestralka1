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

public class C20OdstranenieNehnutelnosti extends ControllerBase {

    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldCisloListuVlastnictva;

    @FXML
    private JFXTextField textFieldSupisneCisloNehnutelnosti;

    @FXML
    private JFXButton buttonOdstranNehnutelnost;

    private SimpleBooleanProperty isCisloKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCisloListuVlastnictvaOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isSupisneCisloOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isCisloKUOk,
            isCisloListuVlastnictvaOk,
            isSupisneCisloOk
    );

    private List<JFXTextField> textFields;

    public C20OdstranenieNehnutelnosti(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldCisloKatastralnehoUzemia,
                textFieldCisloListuVlastnictva,
                textFieldSupisneCisloNehnutelnosti
        );

        Helper.decorateNumberTextFieldWithValidator(textFieldCisloKatastralnehoUzemia, isCisloKUOk);
        Helper.decorateNumberTextFieldWithValidator(textFieldCisloListuVlastnictva, isCisloListuVlastnictvaOk);
        Helper.decorateNumberTextFieldWithValidator(textFieldSupisneCisloNehnutelnosti, isSupisneCisloOk);

        buttonOdstranNehnutelnost.setOnAction(event -> {
            if (Helper.disableButton(buttonOdstranNehnutelnost, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new OdstranNehnutelnost().execute();

        });
    }

    private void clearFormulars() {
        buttonOdstranNehnutelnost.disableProperty().unbind();
        buttonOdstranNehnutelnost.disableProperty().set(false);
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
        return "20odstranenieNehnutelnosti.fxml";
    }

    @Override
    public String getViewName() {
        return "20. Odstránenie nehnuteľnosti";
    }

    private class OdstranNehnutelnost extends SimpleTask {

        @Override
        public boolean compute() {
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
            return true;
        }

        @Override
        public void onSuccess() {
            showSuccessDialog("Nehnuteľnosť úspešne odstránené");
            clearFormulars();
        }

        @Override
        public void onFail() {
            showWarningDialog("Nehnuteľnosť nebola odstránená");
        }
    }


}
