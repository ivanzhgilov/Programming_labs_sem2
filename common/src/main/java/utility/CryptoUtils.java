package utility;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final String SECRET_KEY_STRING = "my-super-secret-key-for-lab-16b"; // Must be 16 bytes for AES-128

    private static final SecretKey SECRET_KEY = new SecretKeySpec(
            SECRET_KEY_STRING.substring(0, 16).getBytes(StandardCharsets.UTF_8),
            "AES"
    );

    public static String encrypt(String plainText) {
        if (plainText == null) return null;

        try {
            long timestamp = System.currentTimeMillis();
            String dataToEncrypt = plainText + "|" + timestamp;
            byte[] bytesToEncrypt = dataToEncrypt.getBytes(StandardCharsets.UTF_8);

            byte[] iv = new byte[IV_LENGTH_BYTE];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, spec);

            byte[] encrypted = cipher.doFinal(bytesToEncrypt);

            ByteBuffer bb = ByteBuffer.allocate(iv.length + encrypted.length);
            bb.put(iv);
            bb.put(encrypted);

            return Base64.getEncoder().encodeToString(bb.array());
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decrypt(String encryptedBase64) {
        if (encryptedBase64 == null) return null;

        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedBase64);

            ByteBuffer bb = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            bb.get(iv);
            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, spec);

            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
