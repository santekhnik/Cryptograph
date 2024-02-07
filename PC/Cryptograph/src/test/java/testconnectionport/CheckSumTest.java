package testconnectionport;


import com.fazecast.jSerialComm.SerialPort;
import org.junit.jupiter.api.Test;
import java.io.IOException;




public class CheckSumTest  {
    @Test
    public void test() throws IOException, InterruptedException {

        SerialPort sp = SerialPort.getCommPort("COM3");
        sp.setComPortParameters(9600, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        if (sp.openPort()) {
            System.out.println("Port is open");
        } else {
            System.out.println("Failed to open port");
            return;
        }

        while (true) {
            byte b = (byte) sp.getInputStream().read();
            Thread.sleep(500);
            System.out.println("Received data: " + b);
        }


    }

}
