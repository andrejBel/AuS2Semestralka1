import GUI.Aplikacia;
import javafx.application.Application;
import javafx.stage.Stage;



public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Aplikacia aplikacia = new Aplikacia(primaryStage);
        aplikacia.run();
    }

}
