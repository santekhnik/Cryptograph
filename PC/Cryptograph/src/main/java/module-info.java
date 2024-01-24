module ua.com.globallogic.cryptograph {

    /*
     * Utilizes JavaFX and other libraries for developing the graphical user interface (UI) in a Java project.
     */
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    /*
     * Facilitates interaction with COM ports using the jSerialComm library.
     */
    requires com.fazecast.jSerialComm;

    opens ua.com.globallogic.cryptograph to javafx.fxml;
    exports ua.com.globallogic.cryptograph;
}