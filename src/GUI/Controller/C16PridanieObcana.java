package GUI.Controller;

import GUI.AsyncTask;
import GUI.SimpleTask;
import InformacnySystem.ISSpravyKatastra;
import Model.Obcan;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class C16PridanieObcana extends ControllerBase {


    @FXML
    private JFXTextField textFieldMenoAPriezvisko;

    @FXML
    private JFXTextField textFieldRodneCislo;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private JFXButton buttonPridajObcana;

    private SimpleBooleanProperty isMenoPriezviskoOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isRodneCisloOk = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isdateOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
            isMenoPriezviskoOk,
            isRodneCisloOk,
            isdateOk
    );

    private List<JFXTextField> textFields;

    public C16PridanieObcana(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
                textFieldMenoAPriezvisko,
                textFieldRodneCislo
        );

        Helper.DecorateTextFieldWithValidator(textFieldMenoAPriezvisko, isMenoPriezviskoOk);
        Helper.DecorateTextFieldWithValidator(textFieldRodneCislo, isRodneCisloOk, Obcan.RODNE_CISLO_LENGTH, "Rodné číslo");

        datePicker.getValidators().add(new RequiredFieldValidator() {
            {
                setMessage(Helper.EMPTY_WARNING_MESSAGE);
            }
        });


        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean validateResult = datePicker.validate();
            isdateOk.setValue(validateResult);
        });

        buttonPridajObcana.setOnAction(event -> {
            if (Helper.DisableButton(buttonPridajObcana, simpleBooleanProperties, () -> {
                textFields.forEach(JFXTextField::validate);
                datePicker.validate();
            })) {
                return;
            }
            new PridajObcana().execute();
        });
        Helper.SetActionOnEnter(textFields, () -> buttonPridajObcana.fire());

    }


    @Override
    protected void initView() {
        loadView();
    }

    @Override
    protected String getViewFileName() {
        return "16pridanieObcana.fxml";
    }

    @Override
    public String getViewName() {
        return "16. Pridanie občana";
    }

    @Override
    public Runnable getRunnableOnSelection() {
        return () -> {
            clearFormulars();
        };
    }

    private void clearFormulars() {
        buttonPridajObcana.disableProperty().unbind();
        buttonPridajObcana.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
        datePicker.setValue(null);
        datePicker.resetValidation();
    }

    private class PridajObcana extends SimpleTask {

        @Override
        public boolean compute() {
            String menoAPriezvisko = textFieldMenoAPriezvisko.getText();
            String rodneCislo = textFieldRodneCislo.getText();
            LocalDate datumNarodenia = datePicker.getValue();
            long datumLong = Helper.ConvertLocalDateToLong(datumNarodenia);
            return isSpravyKatastra_.pridajObcana(menoAPriezvisko, rodneCislo, datumLong);
        }

        @Override
        public void onSuccess() {
            showSuccessDialog("Občan úspešne pridaný");
        }

        @Override
        public void onFail() {
            showWarningDialog("Občan nebol pridaný");
        }
    }

}
