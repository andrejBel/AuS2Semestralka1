package GUI.Controller;

import InformacnySystem.ISSpravyKatastra;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public abstract class ControllerBase {

    @FXML
    protected StackPane rootStackPane_;

    @FXML
    protected JFXDialog dialog;

    @FXML
    protected JFXDialogLayout dialogLayout;

    @FXML
    protected VBox dialogVBox;

    @FXML
    protected VBox contentVBox;

    protected ISSpravyKatastra isSpravyKatastra_;

    public ControllerBase(ISSpravyKatastra isSpravyKatastra) {
        this.isSpravyKatastra_ = isSpravyKatastra;
    }

    public Parent getView() {
        return rootStackPane_;
    }


    public Runnable getRunnableOnSelection() {
        return null;
    }

    // nutne zavolat v konstruktore
    protected abstract void initView();

    // cesta k fxml suborom s view
    protected abstract String getViewFileName();

    // nazov pre obsah viewu
    public abstract String getViewName();

    protected void loadView() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource( "/GUI/View/" + getViewFileName()));
        loader.setController(this);
        rootStackPane_ = null;
        try {
            rootStackPane_ = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // inicializacia pre premenne
        dialog.setDialogContainer(rootStackPane_);
        dialog.setOnDialogOpened(event -> contentVBox.setEffect(new BoxBlur(3,3,3)));
        dialog.setOnDialogClosed(event -> contentVBox.setEffect(null));

        dialogVBox.setAlignment(Pos.CENTER);
    }

}
