package ua.com.globallogic.cryptograph;

public class BCCChecksumCalculator {

    // Функція для розрахунку BCC чек-суми для масиву байтів
    public static byte calculateBCC(byte[] data) {
        byte bcc = 0;

        for (byte b : data) {
            bcc ^= b; // кожен біт з bcc буде використовуватися для обчислення відповідного біта в bcc, з використанням XOR
        }

        return bcc;
    }

    public static void main(String[] args) {

        byte[] dataArray = "Hello, World!".getBytes(); // масив який буде на вході функції calculateBCC
        byte bccChecksum = calculateBCC(dataArray);

        System.out.println("BCC Checksum: " + bccChecksum);
    }
}
