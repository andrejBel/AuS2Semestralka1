package GUI.Controller;

import GUI.AsyncTask;
import InformacnySystem.ISSpravyKatastra;
import Utils.Helper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CImportExportDat extends ControllerBase {

    @FXML
    private JFXTextField textFieldMenoSuboruExport;

    @FXML
    private JFXButton buttonNacitaj;

    @FXML
    private JFXButton buttonExportuj;

    private SimpleBooleanProperty isMenoSuboruOk = new SimpleBooleanProperty(false);

    private List<SimpleBooleanProperty> simpleBooleanProperties = Arrays.asList(
        isMenoSuboruOk
    );
    private List<JFXTextField> textFields;

    private FileChooser.ExtensionFilter extFilter_;
    private FileChooser fileChooser_;
    private DirectoryChooser directoryChooser_;

    public CImportExportDat(ISSpravyKatastra isSpravyKatastra, Stage stage) {
        super(isSpravyKatastra);
        initView();
        textFields = Arrays.asList(
            textFieldMenoSuboruExport
        );
        Helper.DecorateTextFieldWithValidator(textFieldMenoSuboruExport, isMenoSuboruOk);

        extFilter_ = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser_ = new FileChooser();
        directoryChooser_ = new DirectoryChooser();
        fileChooser_.getExtensionFilters().add(extFilter_);

        fileChooser_.setTitle("Vybrať súbor na import");
        directoryChooser_.setTitle("Vybrať priečinok na uloženie súboru");

        fileChooser_.setInitialDirectory(new File(System.getProperty("user.dir")));
        directoryChooser_.setInitialDirectory(new File(System.getProperty("user.dir")));

        buttonNacitaj.setOnAction(
                e -> {
                    File file = fileChooser_.showOpenDialog(stage);
                    if (file != null) {
                        String filePath = file.getAbsolutePath();
                        new ImportDat(filePath).execute();
                    }
                });
        buttonExportuj.setOnAction(event -> {
            if (Helper.DisableButton(buttonExportuj, simpleBooleanProperties, () -> textFields.forEach(JFXTextField::validate))) {
                return;
            }
            File file = directoryChooser_.showDialog(stage);
            if (file != null) {
                String directoryPath = file.getAbsolutePath();
                String fileName = textFieldMenoSuboruExport.getText();
                if (!fileName.endsWith(".csv")) {
                    fileName = fileName + ".csv";
                }
                String path = directoryPath + File.separator + fileName;
                System.out.println(path);
                new ExportDat(path).execute();
            }

        });
    }

    private void clearFormulars() {
        buttonExportuj.disableProperty().unbind();
        buttonExportuj.disableProperty().set(false);
        textFields.forEach(jfxTextField -> {
            jfxTextField.setText("");
            jfxTextField.resetValidation();
        });
    }

    @Override
    public Runnable getRunnableOnSelection() {
        return this::clearFormulars;
    }

    @Override
    protected void initView() {
        loadView();
    }

    @Override
    protected String getViewFileName() {
        return "importExportDat.fxml";
    }

    @Override
    public String getViewName() {
        return "Import/Export dát";
    }

    private class ImportDat extends AsyncTask {

        String cestaKSuboru_;

        public ImportDat(String cestakSuboru) {
            cestaKSuboru_ = cestakSuboru;
        }

        @Override
        public void onPreExecute() {
            showSpinner("Import dát");
        }

        @Override
        public Object doInBackground(Object[] params) {
            return isSpravyKatastra_.importujData(cestaKSuboru_);
        }

        @Override
        public void onPostExecute(Object params) {
            boolean importResult = (Boolean) params;
            if (importResult) {
                showSuccessDialog("Import dát úspešný", false);
            } else {
                showWarningDialog("Import dát neúspešný", false);
            }
        }

        @Override
        public void progressCallback(Object[] params) {

        }

        @Override
        public void onFail(Exception e) {
            showWarningDialog("Import dát neúspešný - vážna chyba");
        }
    }

    private class ExportDat extends AsyncTask {

        String cestaKSuboru_;

        public ExportDat(String cestakSuboru) {
            cestaKSuboru_ = cestakSuboru;
        }

        @Override
        public void onPreExecute() {
            showSpinner("Export dát");
        }

        @Override
        public Object doInBackground(Object[] params) {
            return isSpravyKatastra_.exportujData(cestaKSuboru_);
        }

        @Override
        public void onPostExecute(Object params) {
            boolean exportResult = (Boolean) params;
            if (exportResult) {
                showSuccessDialog("Export dát úspešný", false);
            } else {
                showWarningDialog("Export dát neúspešný", false);
            }
        }

        @Override
        public void progressCallback(Object[] params) {

        }

        @Override
        public void onFail(Exception e) {
            showWarningDialog("Export dát neúspešný - vážna chyba");
        }
    }

}
