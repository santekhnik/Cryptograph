package ua.com.globallogic.cryptograph.utils;


import com.dlsc.formsfx.model.structure.Field;
import com.fazecast.jSerialComm.SerialPort;
import ua.com.globallogic.cryptograph.controller.ControllerApplication;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public final class CryptoUtils {
    //Size of the data block for transmission to the COM port.
    private static final int BLOCK_SIZE = 32;
    // List of our blocks
    private static List<byte[]> listOfByteArrays = new ArrayList<>();


    private CryptoUtils() {

    }

    public static void main(String[] args) {
        encryption(new File("Q:\\JavaLear\\Downloads\\description.txt"));


    }

    public static void encryption(File path) {
        try (FileReader fileReader = new FileReader(path)) {
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


        } catch (IOException e) {
            e.printStackTrace();

        }


    }
    //Task 1
    /*
     * Implement functionality for encryption. This should include:
     *
     *
     * 3)sending blocks to COM port
     * 4)receiving encrypted blocks
     * 5)combining blocks into output file
     */

    //Task 2
    /*
     *Implement functionality for decryption This should include:
     *
     *  +++ 1)file selection
     * ++2)reading file by blocks
     * 3)sending blocks to COM port
     * 4)receiving decrypted blocks
     * 5)combining blocks into output file
     */


}
