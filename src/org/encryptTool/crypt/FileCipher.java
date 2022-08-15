package org.encryptTool.crypt;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

public final class FileCipher {

    private static SecretKey getPasswordBasedKey(String alg, char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[100];
        Random random = new Random(Arrays.hashCode(password));
        random.nextBytes(salt);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, 1000, 256);
        SecretKey pbeKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(pbeKeySpec);
        return new SecretKeySpec(pbeKey.getEncoded(), alg);
    }

    public static void decrypt(File input, File output, String key, String algorithm, OnProcess onProcess) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        assert input.canRead();

        Cipher _cipher = null;

        SecretKey secretKey = getPasswordBasedKey(algorithm, key.toCharArray());

        try {
            _cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        long processed = 0;

        if (!output.exists())
            output.createNewFile();


        try {
            _cipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        FileInputStream inputStream = new FileInputStream(input);
        FileOutputStream outputStream = new FileOutputStream(output);

        byte[] buffer = new byte[64];

        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1){
            byte[] outBytes = _cipher.update(buffer, 0,bytesRead);

            outputStream.write(outBytes);

            processed += buffer.length;

            if (processed < input.length())
                onProcess.update((processed * 100 / input.length()));
        }

        try {
            outputStream.write(_cipher.doFinal());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        inputStream.close();
        outputStream.close();
    }



    public static void encrypt(File input, File output, String key, String algorithm, OnProcess onProcess) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        assert input.canRead();

        Cipher _cipher = null;

        SecretKey secretKey = getPasswordBasedKey(algorithm, key.toCharArray());

        try {
            _cipher = Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        long processed = 0;

        if (!output.exists())
            output.createNewFile();

        try {
            _cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        FileInputStream inputStream = new FileInputStream(input);
        FileOutputStream outputStream = new FileOutputStream(output);

        byte[] buffer = new byte[64];

        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1){
            byte[] outBytes = _cipher.update(buffer, 0,bytesRead);

            outputStream.write(outBytes);

            processed += buffer.length;

            if (processed < input.length())
                onProcess.update((processed * 100.0 / input.length()));
        }

        try {
            outputStream.write(_cipher.doFinal());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        inputStream.close();
        outputStream.close();
    }
}