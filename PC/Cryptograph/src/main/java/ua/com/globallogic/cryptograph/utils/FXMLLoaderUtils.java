package ua.com.globallogic.cryptograph.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public final class FXMLLoaderUtils {
    private final static FXMLLoader FXML_LOADER = new FXMLLoader();

    private FXMLLoaderUtils() {

    }

    static {
        loadFXML();
    }


    private static void loadFXML() {
        try {
            URL location = FXMLLoaderUtils.class.getResource("/ua/com/globallogic/cryptograph/hello-view.fxml");
            if (location == null) {
                System.err.println("FXML file not found");
                return;
            }
            FXML_LOADER.setLocation(location);
            var inputStream = location.openStream();
            FXML_LOADER.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Parent getRoot() {
        return FXML_LOADER.getRoot();
    }
}
