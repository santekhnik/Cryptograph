package ua.com.globallogic.cryptograph;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Main class responsible for launching and displaying the graphical user interface using JavaFX.
 */
public class CryptographApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(CryptographApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //Launches the execution of the JavaFX application,
        // initializing it and calling the start() method to display the graphical user interface.
        launch();
    }
}