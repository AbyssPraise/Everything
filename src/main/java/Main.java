import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("app.fxml"));
        primaryStage.setTitle("everything");
        Dimension screen= Toolkit.getDefaultToolkit().getScreenSize();
        primaryStage.setScene(new Scene(root,screen.width * 0.5, screen.height * 0.5));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
