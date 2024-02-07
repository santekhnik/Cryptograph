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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private void encrypt() throws InterruptedException {
        if (selectedFilePath.getFileSelection() == null) {
            statusLabel.setText("Status: File selection is required");
            return;
        }
        File file = selectedFilePath.getFileSelection().getAbsoluteFile();
        final int BLOCK_SIZE = 32;
        List<byte[]> listOfByteArrays = new ArrayList<>();
        if (portChoiceBox.getValue() != null && !portChoiceBox.getValue().isEmpty()) {
            if (selectedPort != null && selectedPort.isOpen()) {
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

                    if (index > 0) {
                        System.arraycopy(block, 0, block, 0, index);
                        listOfByteArrays.add(block);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selectedPort.setBaudRate(38400);
                byte blocks = 0;
                if (listOfByteArrays.size() == 1) {
                    blocks = 97;
                } else if (listOfByteArrays.size() == 2) {
                    blocks = 98;
                } else if (listOfByteArrays.size() == 3) {
                    blocks = 99;
                } else {
                    blocks = 100;
                }
                byte[] dataToSend = {105, blocks};
                selectedPort.writeBytes(dataToSend, dataToSend.length);
                Thread.sleep(1000);

                byte[] receivedData = new byte[2];
                int numBytesRead = selectedPort.readBytes(receivedData, receivedData.length);
                System.out.println("Received: " + new String(receivedData));
                Thread.sleep(1000);

                for (int i = 0; i < listOfByteArrays.size(); i++) {
                    selectedPort.writeBytes(listOfByteArrays.get(i), listOfByteArrays.get(i).length);
                    Thread.sleep(1000);
                }
                Thread.sleep(1000);

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < listOfByteArrays.size(); i++) {
                    byte[] receivedBytes = new byte[32];
                    numBytesRead = selectedPort.readBytes(receivedBytes, receivedBytes.length);
                    Thread.sleep(1000);
                    stringBuilder.append(new String(receivedBytes));
                }
                try (FileWriter fileWriter = new FileWriter(file)) {
                    fileWriter.write(stringBuilder.toString());
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
    private void decrypt() throws InterruptedException {
        if (selectedFilePath.getFileSelection() == null) {
            statusLabel.setText("Status: File selection is required");
            return;
        }
        File file = selectedFilePath.getFileSelection().getAbsoluteFile();
        final int BLOCK_SIZE = 32;

        if (portChoiceBox.getValue() != null && !portChoiceBox.getValue().isEmpty()) {
            if (selectedPort != null && selectedPort.isOpen()) {
                List<byte[]> listOfByteArrays = new ArrayList<>();
                try (FileReader fileReader = new FileReader(file)) {
                    int character;
                    byte[] block = new byte[BLOCK_SIZE];
                    int index = 0;
                    while ((character = fileReader.read()) != -1 && listOfByteArrays.size() < 4) {
                        block[index++] = (byte) character;
                        if (index == BLOCK_SIZE) {
                            listOfByteArrays.add(block);
                            block = new byte[BLOCK_SIZE];
                            index = 0;
                        }
                    }
                    if (index > 0) {
                        System.arraycopy(block, 0, block, 0, index);
                        listOfByteArrays.add(block);
                    }

                    System.out.println(listOfByteArrays.size());
                    for (byte[] s : listOfByteArrays) {
                        System.out.println(Arrays.toString(s));
                        System.out.println(s.length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SerialPort serialPort = SerialPort.getCommPort("COM3");
                if (!serialPort.openPort()) {
                    System.err.println("Failed to open port.");
                    return;
                }
                System.out.println("Port status: " + (selectedPort.isOpen() ? "open" : "closed"));

                selectedPort.setBaudRate(38400);

                byte blocks = 0;
                if (listOfByteArrays.size() == 1) {
                    blocks = 97;
                } else if (listOfByteArrays.size() == 2) {
                    blocks = 98;
                } else if (listOfByteArrays.size() == 3) {
                    blocks = 99;
                } else {
                    blocks = 100;
                }
                System.out.println(blocks);

//105//117
                byte[] dataToSend = {117, blocks};
                selectedPort.writeBytes(dataToSend, dataToSend.length);
                Thread.sleep(1000);

                byte[] receivedData = new byte[2];
                int numBytesRead = serialPort.readBytes(receivedData, receivedData.length);
                System.out.println("Received: " + new String(receivedData));
                Thread.sleep(1000);


                for (int i = 0; i < listOfByteArrays.size(); i++) {
                    serialPort.writeBytes(listOfByteArrays.get(i), listOfByteArrays.get(i).length);
                    Thread.sleep(1000);
                }
                Thread.sleep(1000);
                String stringBuilder = "";
                for (int i = 0; i < listOfByteArrays.size(); i++) {
                    byte[] receivedBytes = new byte[32];
                    numBytesRead = serialPort.readBytes(receivedBytes, receivedBytes.length);
                    Thread.sleep(1000);
                    stringBuilder += new String(receivedBytes);
                }
                System.out.println(stringBuilder);


                try (FileWriter fileWriter = new FileWriter(file)) {
                    fileWriter.write(stringBuilder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                statusLabel.setText("Status: Port is not open. Connect to the port first.");
            }
        } else {
            statusLabel.setText("Status: Select a port before decrypting");
        }

    }
}
