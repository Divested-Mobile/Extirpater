package us.spotco.extirpater;

import java.io.File;
import java.util.Random;

public class Utils {

    public static void deleteFilesByPrefix(File path, String prefix) {
        try {
            for (File f : path.listFiles()) {
                if (f.isFile()) {
                    if ((f + "").contains(prefix)) {
                        f.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRandomString(Random rng, int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int rn = rng.nextInt(base.length());
            temp.append(base.substring(rn, rn + 1));
        }
        return temp.toString();
    }

    public static byte[] generateRandomByteArray(Random rng, int size) {
        byte[] bytes = new byte[size];
        rng.nextBytes(bytes);
        return bytes;
    }

    public static byte[] generateByteArray(int b, int length) {
        byte[] bytes = new byte[length];
        for (int x = 0; x < length; x++) {
            bytes[x] = (byte) b;
        }
        return bytes;
    }

}
