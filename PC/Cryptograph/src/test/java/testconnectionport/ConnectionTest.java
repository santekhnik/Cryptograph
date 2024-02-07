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

        int BLOCK_SIZE = 32;
        File file = new File("C:\\Users\\vot47\\Desktop\\file.txt");
        List<byte[]> list = new ArrayList<>();

        try (FileReader fileReader = new FileReader(file)) {
            int character;
            byte[] block = new byte[BLOCK_SIZE];
            int index = 0;
            while ((character = fileReader.read()) != -1) {
                block[index++] = (byte) character;
                if (index == BLOCK_SIZE) {
                    list.add(block);
                    block = new byte[BLOCK_SIZE];
                    index = 0;
                }
            }
            System.arraycopy(block, 0, block, 0, index);
            list.add(block);


            byte[] bytes = {108, (byte) list.size()};

            SerialPort serialPort = SerialPort.getCommPort("COM3");
            if (serialPort.openPort()) {
                System.out.println("Port status: " + serialPort.isOpen());
            }
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
            serialPort.writeBytes(bytes, bytes.length);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
            byte[] b = new byte[2];
            int a = serialPort.readBytes(b, b.length);
            System.out.println(a);
            Thread.sleep(1000);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
            for (byte[] s : list) {
                Thread.sleep(200);
                serialPort.writeBytes(s, s.length);
            }

        }

    }
}
