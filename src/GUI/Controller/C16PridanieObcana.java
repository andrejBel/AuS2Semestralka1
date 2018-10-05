package GUI.Controller;

import GUI.AsyncTask;
import InformacnySystem.ISSpravyKatastra;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Color;

import java.time.LocalDate;

import static javafx.beans.binding.Bindings.createBooleanBinding;

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


    public C16PridanieObcana(ISSpravyKatastra isSpravyKatastra) {
        super(isSpravyKatastra);
        initView();

        textFieldMenoAPriezvisko.getValidators().add(new RequiredFieldValidator() {
            {
                setMessage(Helper.EMPTY_WARNING_MESSAGE);
            }
        });
        textFieldRodneCislo.getValidators().add(new RequiredFieldValidator() {
            {
                setMessage(Helper.EMPTY_WARNING_MESSAGE);
            }
        });
        ValidatorBase validatorRC = new ValidatorBase() {
            @Override
            protected void eval() {

                TextInputControl textField = (TextInputControl)this.srcControl.get();
                String text = textField.getText();
                if (text != null && text.length() > 0)
                {
                    if (text.contains(" ")) {
                        this.hasErrors.set(true);
                        setMessage("Rodné číslo nesmie mať medzery");
                        return;
                    }
                    if (text.length() == 16) {
                        this.hasErrors.set(false);
                    } else {
                        setMessage("Rodné číslo musí mať 16 znakov");
                        this.hasErrors.set(true);
                    }

                } else
                {
                    this.hasErrors.set(false);
                }
            }
        };
        textFieldRodneCislo.getValidators().add(validatorRC);


        datePicker.getValidators().add(new RequiredFieldValidator() {
            {
                setMessage(Helper.EMPTY_WARNING_MESSAGE);
            }
        });



        textFieldMenoAPriezvisko.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean validateResult = textFieldMenoAPriezvisko.validate();
            isMenoPriezviskoOk.set(validateResult);
        });
        textFieldRodneCislo.textProperty().addListener((observable, oldValue, newValue) -> {

            if (textFieldRodneCislo.getText().length() > 16) {
                String s = textFieldRodneCislo.getText().substring(0, 16);
                textFieldRodneCislo.setText(s);
            }
            boolean validateResult = textFieldRodneCislo.validate();
            isRodneCisloOk.set(validateResult);
        });
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean validateResult = datePicker.validate();
            isdateOk.setValue(validateResult);
        });

        buttonPridajObcana.setOnAction(event -> {
            if (setButtonPridajDisabled()) {
                return;
            }
            new PridajObcana().execute();
        });


    }

    private boolean setButtonPridajDisabled() {
        if (buttonPridajObcana.isDisable() == false && (!isMenoPriezviskoOk.get() || !isRodneCisloOk.get() || !isdateOk.get())) {
            textFieldMenoAPriezvisko.validate();
            textFieldRodneCislo.validate();
            datePicker.validate();
            buttonPridajObcana.disableProperty().unbind();
            buttonPridajObcana.disableProperty().bind(createBooleanBinding(
                    () -> !(isMenoPriezviskoOk.get() && isRodneCisloOk.get() && isdateOk.get()), isMenoPriezviskoOk, isRodneCisloOk, isdateOk
            ));
            return true;
        }
        return false;
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
        textFieldMenoAPriezvisko.setText("");
        textFieldRodneCislo.setText("");
        datePicker.setValue(null);
        textFieldMenoAPriezvisko.resetValidation();
        textFieldRodneCislo.resetValidation();
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
