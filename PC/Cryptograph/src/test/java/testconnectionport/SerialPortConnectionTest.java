package testconnectionport;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fazecast.jSerialComm.SerialPort;

import static org.junit.jupiter.api.Assumptions.assumeTrue;


public class SerialPortConnectionTest {

    private SerialPort serialPort;

    @BeforeEach
    public void setUp() {
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length > 0) {
            serialPort = ports[0];
        } else {
            assumeTrue(false, "No serial ports found. Skipping tests.");
        }
    }

    @AfterEach
    public void tearDown() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }

    @Test
    public void testPortConnection() {
        assertTrue(serialPort.openPort());
        assertTrue(serialPort.isOpen());
        assertTrue(serialPort.closePort());
        assertFalse(serialPort.isOpen());
    }

    @Test
    public void testBaudRateSetting() {
        int baudRate = 9600;
        serialPort.setBaudRate(baudRate);
        assertEquals(baudRate, serialPort.getBaudRate());
    }

    @Test
    public void testTimeoutSetting() {
        int timeout = 500;
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, timeout, 0);
        assertEquals(timeout, serialPort.getReadTimeout());
    }
}

