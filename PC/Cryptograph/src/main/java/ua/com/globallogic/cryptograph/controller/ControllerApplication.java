package ua.com.globallogic.cryptograph.controller;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ua.com.globallogic.cryptograph.utils.FileSelection;

import java.io.File;

public class ControllerApplication {

    @FXML
    private ChoiceBox<String> portChoiceBox;

    @FXML
    private Button connectButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Label statusLabel;

    private SerialPort selectedPort;
    private FileChooser fileChooser;
    private FileSelection selectedFilePath = new FileSelection();


    @FXML
    private void initialize() {
        ObservableList<String> portList = getAvailablePorts();
        portChoiceBox.setItems(portList);

        connectButton.setOnAction(e -> {
            String selectedPortName = portChoiceBox.getValue();
            if (selectedPortName != null && !selectedPortName.isEmpty()) {
                connectPort(selectedPortName);
            } else {
                statusLabel.setText("Status: Select a port before connecting");
            }
        });

        disconnectButton.setOnAction(e -> disconnectPort());

        fileChooser = new FileChooser();
    }

    @FXML
    private void chooseFile() {
        configureFileChooser(fileChooser);
        Stage stage = (Stage) chooseFileButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            selectedFilePath.setFileSelection(selectedFile.getAbsoluteFile());
            statusLabel.setText("Status: Selected File: " + selectedFilePath);
        }
    }

    private ObservableList<String> getAvailablePorts() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        ObservableList<String> portList = FXCollections.observableArrayList();

        for (SerialPort port : serialPorts) {
            portList.add(port.getSystemPortName());
        }

        return portList;
    }

    private void connectPort(String portName) {
        disconnectPort();

        selectedPort = SerialPort.getCommPort(portName);
        boolean isConnected = selectedPort.openPort();

        if (isConnected) {
            statusLabel.setText("Status: Connected to port: " + portName);
        } else {
            statusLabel.setText("Status: Failed to connect to the port " + portName);
        }

        disableConnectDisconnectButtons(isConnected);
    }

    private void disconnectPort() {
        if (selectedPort != null && selectedPort.isOpen()) {
            selectedPort.closePort();
            statusLabel.setText("Status: Disconnected from the port:" + selectedPort.getSystemPortName());
        }

        disableConnectDisconnectButtons(false);
    }

    private void disableConnectDisconnectButtons(boolean isConnected) {
        connectButton.setDisable(isConnected);
        disconnectButton.setDisable(!isConnected);
    }

    private static void configureFileChooser(FileChooser fileChooser) {
        fileChooser.setTitle("Select a File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
    }
}