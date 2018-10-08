package GUI.Controller;

import GUI.AsyncTask;
import InformacnySystem.ISSpravyKatastra;
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
        Helper.DecorateTextFieldWithValidator(textFieldRodneCislo, isRodneCisloOk, 16, "Rodné číslo");

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
        textFields.forEach(jfxTextField -> jfxTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
            {
                buttonPridajObcana.fire();
            }
        }));

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

    private class PridajObcana extends AsyncTask {

        @Override
        public void onPreExecute() {
            Label label = new Label("Pridávanie obyvateľa");
            label.setAlignment(Pos.CENTER);
            JFXSpinner spinner = new JFXSpinner();
            dialogVBox.getChildren().clear();
            dialogVBox.getChildren().addAll(label, spinner);
            dialog.show();
        }

        @Override
        public Object doInBackground(Object[] params) throws InterruptedException {
            String menoAPriezvisko = textFieldMenoAPriezvisko.getText();
            String rodneCislo = textFieldRodneCislo.getText();
            LocalDate datumNarodenia = datePicker.getValue();
            long datumLong = Helper.ConvertLocalDateToLong(datumNarodenia);
            return new Boolean( isSpravyKatastra_.pridajObcana(menoAPriezvisko, rodneCislo, datumLong));
        }

        @Override
        public void onPostExecute(Object params) {
            boolean added = (Boolean) params;
            if (added) {
                clearFormulars();
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
            if (added) {
                label.setTextFill(Color.GREEN);
                label.setText("Občan úspešne pridaný");
            } else {
                label.setTextFill(Color.RED);
                label.setText("Občan nebol pridaný");
            }
        }

        @Override
        public void progressCallback(Object[] params) {

        }

        @Override
        public void onFail(Exception e) {

        }
    }

}
