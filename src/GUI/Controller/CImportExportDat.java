package GUI.Controller;

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
                        System.out.println(file.getAbsolutePath());
                        if (isSpravyKatastra_.importujData(filePath)) {
                            showSuccessDialog("uspesne");
                        } else {
                            showWarningDialog("neuspech");
                        }

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
                isSpravyKatastra_.exportujData(path);
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
        return "importExportDat.fxml";
    }

    @Override
    public String getViewName() {
        return "Import/Export dát";
    }
}
