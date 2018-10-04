package GUI.Controller;

import InformacnySystem.ISSpravyKatastra;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public abstract class ControllerBase {

    protected ISSpravyKatastra isSpravyKatastra_;
    protected Parent view_;

    public ControllerBase(ISSpravyKatastra isSpravyKatastra) {
        this.isSpravyKatastra_ = isSpravyKatastra;
    }

    public Parent getView() {
        return view_;
    }

    // nutne zavolat v konstruktore
    protected abstract void initView();

    // cesta k fxml suborom s view
    protected abstract String getViewFileName();

    // nazov pre obsah viewu
    public abstract String getViewName();

    protected Parent loadView() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource( "/GUI/View/" + getViewFileName()));
        loader.setController(this);
        Parent view = null;
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

}
