package GUI.Controller;

import GUI.SimpleTask;
import InformacnySystem.ISSpravyKatastra;
import Model.Obcan;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;

import java.util.Arrays;
import java.util.List;

public class C11ZmenaMajitelaNehnutelnosti extends ControllerBase {


    @FXML
    private JFXTextField textFieldCisloKatastralnehoUzemia;

    @FXML
    private JFXTextField textFieldSupisneCisloNehnutelnosti;

    @FXML
    private JFXTextField textFieldPovodneRodneCislo;

    @FXML
    private JFXTextField textFieldNoveRodneCislo;

    @FXML
    private JFXButton buttonZmenMajitela;

    private SimpleBooleanProperty isCisloKUOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isCisloNehnutelnostiOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isRodneCisloPovodneOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isRodneCisloNoveOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isCisloKUOk,
            isCisloNehnutelnostiOk,
            isRodneCisloPovodneOk,
            isRodneCisloNoveOk
    );

    private List<JFXTextField> textFields;

    public C11ZmenaMajitelaNehnutelnosti(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldCisloKatastralnehoUzemia,
                textFieldSupisneCisloNehnutelnosti,
                textFieldPovodneRodneCislo,
                textFieldNoveRodneCislo
        );

        Helper.DecorateNumberTextFieldWithValidator( textFieldCisloKatastralnehoUzemia, isCisloKUOk);
        Helper.DecorateNumberTextFieldWithValidator( textFieldSupisneCisloNehnutelnosti, isCisloNehnutelnostiOk);
        Helper.DecorateTextFieldWithValidator(textFieldPovodneRodneCislo, isRodneCisloPovodneOk, Obcan.RODNE_CISLO_LENGTH, "Rodné číslo");
        Helper.DecorateTextFieldWithValidator(textFieldNoveRodneCislo, isRodneCisloNoveOk, Obcan.RODNE_CISLO_LENGTH, "Rodné číslo");

        Helper.SetActionOnEnter(textFields, () -> buttonZmenMajitela.fire());

        buttonZmenMajitela.setOnAction(event -> {
            if (Helper.DisableButton(buttonZmenMajitela, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            new ZmenMajitelaNehnutelnosti().execute();
        });

    }

    private void clearFormulars() {
        buttonZmenMajitela.disableProperty().unbind();
        buttonZmenMajitela.disableProperty().set(false);
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
        return "11zmenaMajitelaNehnutelnosti.fxml";
    }

    @Override
    public String getViewName() {
        return "11. Zmena majiteľa nehnuteľnosti";
    }

    private class ZmenMajitelaNehnutelnosti extends SimpleTask {

        @Override
        public boolean compute() {

            long cisloKatastralnehoUzemia = 0;
            long supisneCisloNehnutelnosti = 0;
            try {
                cisloKatastralnehoUzemia= Long.parseLong(textFieldCisloKatastralnehoUzemia.getText());
            }catch (NumberFormatException e) {
                return false;
            }
            try {
                supisneCisloNehnutelnosti = Long.parseLong(textFieldSupisneCisloNehnutelnosti.getText());
            } catch (NumberFormatException e) {
                return false;
            }

            return isSpravyKatastra_.zmenaMajitelaNehnutelnosti(cisloKatastralnehoUzemia, supisneCisloNehnutelnosti, textFieldPovodneRodneCislo.getText(), textFieldNoveRodneCislo.getText());
        }

        @Override
        public void onSuccess() {
            showSuccessDialog("Majiteľ nehnuteľnosti bol zmenený");
            clearFormulars();
        }

        @Override
        public void onFail() {
            showWarningDialog("Majiteľ nehnuteľnosti nebol zmenený");
        }
    }

}
