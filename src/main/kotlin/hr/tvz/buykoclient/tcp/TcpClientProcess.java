package hr.tvz.buykoclient.tcp;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
public class TcpClientProcess {

    static SecretKey secretKey;
    static KeyPair keyPair;

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

        Thread tGenerate = new Thread(() -> generateKeyPair());
        Thread tKey = new Thread(() -> fetchKey(port0));

        Thread t1 = new Thread(() -> downloadFile(port1, encryptedFilePath1));
        Thread t2 = new Thread(() -> downloadFile(port2, encryptedFilePath2));
        Thread t3 = new Thread(() -> downloadFile(port3, encryptedFilePath3));

        Thread tDecode1 = new Thread(() -> {
            try {
                decodeFiles(encryptedFilePath1, filePath1);
            } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | IOException |
                     InvalidKeyException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
        Thread tDecode2 = new Thread(() -> {
            try {
                decodeFiles(encryptedFilePath2, filePath2);
            } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | IOException |
                     InvalidKeyException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
        Thread tDecode3 = new Thread(() -> {
            try {
                decodeFiles(encryptedFilePath3, filePath3);
            } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | IOException |
                     InvalidKeyException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });

        tGenerate.start();
        tGenerate.join();
        tKey.start();
        tKey.join();

        t1.start();
        t2.start();
        t3.start();

        tDecode1.start();
        tDecode2.start();
        tDecode3.start();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Total execution time (threads): " + executionTime + "ms");

        tDecode1.join();

        try {
            if (Files.readAllBytes(Paths.get(filePath1)).length != 0 &&
                    Files.readAllBytes(Paths.get(filePath2)).length != 0 &&
                    Files.readAllBytes(Paths.get(filePath3)).length != 0
            ) {
                System.exit(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(1);

    }

    private static void fetchKey(int serverPort){
        String serverAddress = "localhost";
        try (Socket socket = new Socket(serverAddress, serverPort)) {

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String request = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            System.out.println("RSA public: " + request);
            out.println(request);

            int keyLength = inputStream.readInt();
            byte[] keyBytes = new byte[keyLength];
            inputStream.readFully(keyBytes);

            inputStream.close();

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] bytes = cipher.doFinal(keyBytes);

            SecretKey key = new SecretKeySpec(bytes, "AES");
            secretKey = key;



        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(int serverPort, String filePath) {
        String serverAddress = "localhost";
        try (Socket socket = new Socket(serverAddress, serverPort)) {

            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }


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

    private static boolean isFileBlank(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.err.println("Invalid file path: " + filePath);
            return false;
        }

        try {
            return Files.size(Paths.get(filePath)) == 0;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return false;
    }

}
