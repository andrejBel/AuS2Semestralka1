import GUI.Aplikacia;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Aplikacia aplikacia = new Aplikacia(primaryStage);
        aplikacia.run();
    }

}
