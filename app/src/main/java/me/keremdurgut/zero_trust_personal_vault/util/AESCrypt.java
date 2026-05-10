package me.keremdurgut.zero_trust_personal_vault.util;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES şifreleme yardımcı sınıfı.
 * Parolaları AES/CBC/PKCS5Padding algoritması ile şifreler ve çözer.
 * Master PIN'den PBKDF2 ile türetilen anahtar kullanılır.
 */
public class AESCrypt {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final int IV_LENGTH = 16;
    // Sabit salt - uygulamaya özel
    private static final byte[] SALT = "ZeroTrustVault2024".getBytes();

    /**
     * Master PIN'den AES anahtarı türetir.
     */
    private static SecretKeySpec deriveKey(String masterPin) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(masterPin.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
        SecretKey secretKey = factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

    /**
     * Düz metni AES ile şifreler.
     * Çıktı formatı: Base64(IV + şifreli veri)
     */
    public static String encrypt(String plainText, String masterPin) {
        try {
            SecretKeySpec keySpec = deriveKey(masterPin);

            // Rastgele IV oluştur
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));

            // IV + şifreli veriyi birleştir
            byte[] combined = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);

            return Base64.encodeToString(combined, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES ile şifrelenmiş metni çözer.
     * Giriş formatı: Base64(IV + şifreli veri)
     */
    public static String decrypt(String encryptedText, String masterPin) {
        try {
            SecretKeySpec keySpec = deriveKey(masterPin);

            byte[] combined = Base64.decode(encryptedText, Base64.NO_WRAP);

            // IV'yi ayır
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Şifreli veriyi ayır
            byte[] encrypted = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "[Çözülemedi]";
        }
    }
}
