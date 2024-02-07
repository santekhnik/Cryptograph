package testconnectionport;

import com.fazecast.jSerialComm.SerialPort;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectionTest {


    @Test
    public void isDeviceConnected() throws Exception {
        SerialPort serialPort = SerialPort.getCommPort("COM3");
        serialPort.openPort();
        System.out.println("Status port: " + serialPort.isOpen());
        serialPort.setBaudRate(38400);
        byte [] b = {105,97};
        serialPort.writeBytes(b, b.length);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte [] b1 = new byte[2];
        serialPort.readBytes(b1, b.length);
        for(byte b321 : b1){
            System.out.println((char) b321);
        }

        serialPort.closePort();

    }
}
