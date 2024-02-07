package ua.com.globallogic.cryptograph;


import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.com.globallogic.cryptograph.utils.FXMLLoaderUtils;

import java.io.IOException;


/**
 * Main class responsible for launching and displaying the graphical user interface using JavaFX.
 */
public class CryptographApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(STYLESHEET_CASPIAN);
        Parent root = FXMLLoaderUtils.getRoot();

        stage.setTitle("Cryptograph");
        stage.setScene(new Scene(root, 800, 483));

        stage.setResizable(false);

        stage.show();
    }

    public static void main(String[] args) {
        //Launches the execution of the JavaFX application,
        // initializing it and calling the start() method to display the graphical user interface.
        launch();
    }
}