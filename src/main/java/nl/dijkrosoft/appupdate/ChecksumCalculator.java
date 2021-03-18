package nl.dijkrosoft.appupdate;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ChecksumCalculator {

    private ChecksumCalculator() {}

    public static String calculate(Path zipFile) throws NoSuchAlgorithmException, IOException {

        byte[] bytes = Files.readAllBytes(zipFile);

        MessageDigest md = MessageDigest.getInstance("MD5");

        return bytesToHex(md.digest(bytes));
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}

