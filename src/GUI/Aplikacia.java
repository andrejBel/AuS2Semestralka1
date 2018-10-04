package GUI;

import GUI.Controller.ControllerBase;
import GUI.Controller.Generator;
import InformacnySystem.ISSpravyKatastra;
import com.jfoenix.controls.JFXTabPane;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class Aplikacia {

    //IS
    private ISSpravyKatastra isSpravyKatastra_ = new ISSpravyKatastra();

    // javafx
    private JFXTabPane tabPane_;
    private Stage stage_;

    private List<ControllerBase> controllers = Arrays.asList(
            new Generator(isSpravyKatastra_)
    );


    public Aplikacia(Stage stage) {

        stage_ = stage;
        tabPane_ = new JFXTabPane();
        tabPane_.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        for (ControllerBase controller : controllers) {
            Tab tab = new Tab(controller.getViewName());
            tab.setContent(controller.getView());
            tabPane_.getTabs().add(tab);
        }

        Scene scene = new Scene(tabPane_, 600, 800);
        stage.setTitle("Algoritmy a údajové štruktúry 2- Semestrálna práca 1");
        stage.setScene(scene);
        stage.setMaximized(true);

    }

    public void run() {
        stage_.show();
    }


}
