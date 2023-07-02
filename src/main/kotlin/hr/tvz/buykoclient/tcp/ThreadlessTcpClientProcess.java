package hr.tvz.buykoclient.tcp;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
public class ThreadlessTcpClientProcess {

    static SecretKey secretKey;

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        String path = "src/main/resources/";
        int port0 = 12340;
        int port1 = 12345;
        int port2 = 12346;
        int port3 = 12347;
        String encryptedFilePath1 = path + "encrypted-language-pack.xml";
        String encryptedFilePath2 = path + "encrypted-updates.bin";
        String encryptedFilePath3 = path + "encrypted-news.bin";
        String filePath1 = path + "language-pack.xml";
        String filePath2 = path + "updates.bin";
        String filePath3 = path + "news.bin";

        fetchKey(port0);

        downloadFile(port1, encryptedFilePath1);
        downloadFile(port2, encryptedFilePath2);
        downloadFile(port3, encryptedFilePath3);


        try {
            decodeFiles(encryptedFilePath1, filePath1);
            decodeFiles(encryptedFilePath2, filePath2);
            decodeFiles(encryptedFilePath3, filePath3);
        } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | IOException |
                 InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Total execution time (threads): " + executionTime + "ms");
    }

    private static void fetchKey(int serverPort){
        String serverAddress = "localhost";
        try (Socket socket = new Socket(serverAddress, serverPort)) {

            System.out.println("Connected to server: " + serverAddress + ":" + serverPort);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            int keyLength = inputStream.readInt();
            byte[] keyBytes = new byte[keyLength];
            inputStream.readFully(keyBytes);

            System.out.println("Received Private key from the server.");

            inputStream.close();

            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            System.out.println("AES key: " + key.getEncoded().toString());
            System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
            secretKey = key;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(int serverPort, String filePath) {
        String serverAddress = "localhost";
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            System.out.println("Connected to server: " + serverAddress + ":" + serverPort);

            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("File downloaded: " + filePath);

            fileOutputStream.close();
            bufferedInputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decodeFiles(String filePath1, String filePath2) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedLanguageContent = new String(Files.readAllBytes(Paths.get(filePath1))).getBytes();
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedCipherText = Base64.getDecoder().decode(encryptedLanguageContent);
        byte[] plaintextBytes = cipher.doFinal(decodedCipherText);
        System.out.println(new String(plaintextBytes, StandardCharsets.UTF_8));
        Files.write(Paths.get(filePath2), plaintextBytes);
    }

    private static PublicKey convertToPublicKey(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private static PrivateKey convertToPrivateKey(byte[] privateKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

}
