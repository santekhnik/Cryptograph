package testconnectionport;


import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.OutputStream;
import java.util.zip.CRC32;


/*
Неблокуючий режим
Режим зчитування напівблокування
Режим повного блокування
Режим повного блокування читання напівблокування/запису
Режим повного блокування читання/запису
Режим зворотного виклику на основі подій
Для доступності даних
Для відключення портів
Для статусу запису
Для повного пакетного прийому даних
Для отримання пакетів з відокремленими межами
Для зміни стану контрольної лінії або помилок
*/
public class CheckSumTest{


    @Test
    public void test() {
        //        SerialPort comPort = SerialPort.getCommPort("COM1");
//        comPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
//        comPort.openPort();
//
//
//        byte[] command = {0x05, 0x01, 0x00, 0x00, 0x01};
//        comPort.writeBytes(command, command.length);
//
//
//        while (true) {
//            byte[] buffer = new byte[1024];
//            int bytesRead = comPort.readBytes(buffer, buffer.length);
//
//            if (bytesRead > 0) {
//                System.out.println("Отримано дані: " + bytesToHex(buffer, bytesRead));
//                break;
//            }
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        comPort.closePort();
//        SerialPort[] ports = SerialPort.getCommPorts();
//        SerialPort selectedPort = ports[0];
//
//        if (selectedPort.isOpen()) {
//            System.out.println("Порт відкритий.");
//            if (selectedPort.getCTS() && selectedPort.getDSR()) {
//                System.out.println("Порт готовий до передачі даних");
//            } else {
//                System.out.println("Порт не готовий до передачі даних");
//            }
//
//
//            if (selectedPort.getDTR()) {
//                System.out.println("Порт готовий до приймання даних");
//            } else {
//                System.out.println("Порт не готовий до приймання даних");
//            }
//        } else {
//            System.out.println("Порт не відкритий");
//        }
//
//
//        selectedPort.setBaudRate(9600);
//        selectedPort.setNumDataBits(8);
//        selectedPort.setParity(SerialPort.NO_PARITY);
//        selectedPort.setNumStopBits(1);
//        if (selectedPort.openPort()) {
//            try {
//                Thread.sleep(1000);
//                selectedPort.getOutputStream().write('A');
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//
//                selectedPort.closePort();
//            }
//        }
//        byte[] commandBytes = {0x05, 0x01, 0x00, 0x00, 0x01};
//        byte bcc = calculateBCC(commandBytes);
//
//        System.out.println("Command\tSpecific\tBCC");
//        System.out.print(String.format("0x%02X", commandBytes[0]));
//
//        for (int i = 1; i < commandBytes.length; i++) {
//            System.out.print(String.format(" 0x%02X", commandBytes[i]));
//        }
//
//        System.out.println(String.format("\t0x%02X", bcc));
    }

    private static String bytesToHex(byte[] bytes, int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(String.format("%02X ", bytes[i]));
        }
        return result.toString();
    }
    public static byte calculateBCC(byte[] data) {
        byte bcc = 0;

        for (byte b : data) {
            bcc ^= b;
        }

        return bcc;
    }
}
