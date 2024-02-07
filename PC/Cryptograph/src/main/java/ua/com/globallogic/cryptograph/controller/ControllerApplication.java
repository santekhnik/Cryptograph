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
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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

    @FXML
    private Button encryptButton;

    @FXML
    private Button decryptButton;

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

        chooseFileButton.setOnAction(e -> chooseFile());

        encryptButton.setDisable(true);
        decryptButton.setDisable(true);

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
            encryptButton.setDisable(false);
            decryptButton.setDisable(false);
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

    @FXML
    private void encrypt() {
        File file = selectedFilePath.getFileSelection().getAbsoluteFile();
        final int BLOCK_SIZE = 32;
        List<byte[]> listOfByteArrays = new ArrayList<>();

        if (portChoiceBox.getValue() != null && !portChoiceBox.getValue().isEmpty()) {
            if (selectedPort != null && selectedPort.isOpen()) {
                statusLabel.setText("Status: Encryption in progress...");
                try (FileReader fileReader = new FileReader(file)) {

                    int character;
                    byte[] block = new byte[BLOCK_SIZE];
                    int index = 0;
                    while ((character = fileReader.read()) != -1) {
                        block[index++] = (byte) character;
                        if (index == BLOCK_SIZE) {
                            listOfByteArrays.add(block);
                            block = new byte[BLOCK_SIZE];
                            index = 0;
                        }
                    }
                    System.arraycopy(block, 0, block, 0, index);
                    listOfByteArrays.add(block);

                    statusLabel.setText("blocks: " + listOfByteArrays.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                statusLabel.setText("Status: Port is not open. Connect to the port first.");
            }
        } else {
            statusLabel.setText("Status: Select a port before encrypting");
        }
    }

    @FXML
    private void decrypt() {
        File file = selectedFilePath.getFileSelection().getAbsoluteFile();
        if (portChoiceBox.getValue() != null && !portChoiceBox.getValue().isEmpty()) {
            if (selectedPort != null && selectedPort.isOpen()) {
                statusLabel.setText("Status: Decryption in progress...");

            } else {
                statusLabel.setText("Status: Port is not open. Connect to the port first.");
            }
        } else {
            statusLabel.setText("Status: Select a port before decrypting");
        }

    }
}
