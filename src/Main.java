import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        JFXTreeView k = new JFXTreeView();
        JFXTabPane tabs = new JFXTabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tab = new Tab();
        tab.setText(" Nadpis");
        tabs.getTabs().add(tab);


        Scene scene = new Scene(tabs, 600, 800); // Manage scene size
        primaryStage.setTitle("Algoritmy a údajové štruktúry 2- Semestrálna práca 1");

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

}
