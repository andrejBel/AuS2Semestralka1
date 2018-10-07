package GUI;

import GUI.Controller.*;
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

    private List<ControllerBase> controllers;


    public Aplikacia(Stage stage) {

        stage_ = stage;
        tabPane_ = new JFXTabPane();
        tabPane_.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        controllers = Arrays.asList(
                new CGenerator(isSpravyKatastra_),
                new C7VypisNehnutelnosti(isSpravyKatastra_),
                new C8VypisNehnutelnostiMajitelaVKU(isSpravyKatastra_),
                new C9VypisNehnutelnostimajitela(isSpravyKatastra_),
                new C10PridanieTrvalehoPobytu(isSpravyKatastra_),
                new C12ZapisZmenaMajetkovehoPodielu(isSpravyKatastra_),
                new C13OdstranenieMajetkovehoPodielu(isSpravyKatastra_),
                new C15VypisKatastralnychUzemi(isSpravyKatastra_),
                new C16PridanieObcana(isSpravyKatastra_),
                new C17PridanieListuVlastnictva(isSpravyKatastra_),
                new C18PridanieNehnutelnosti(isSpravyKatastra_),
                //new C20OdstranenieNehnutelnosti(isSpravyKatastra_),
                new C21PridanieKatastralnehoUzemia(isSpravyKatastra_)
        );

        for (ControllerBase controller : controllers) {
            Tab tab = new Tab(controller.getViewName());
            tab.setContent(controller.getView());
            if (controller.getRunnableOnSelection() != null) {
                tab.setOnSelectionChanged(event -> {
                    if (tab.isSelected()) {
                        controller.getRunnableOnSelection().run();
                    }
                });
            }
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
