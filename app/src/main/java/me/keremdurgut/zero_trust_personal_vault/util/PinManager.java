package me.keremdurgut.zero_trust_personal_vault.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Ana PIN kodunu SharedPreferences ile yöneten sınıf.
 * PIN kodu SHA-256 hash'lenerek saklanır, düz metin olarak kaydedilmez.
 */
public class PinManager {

    private static final String PREF_NAME = "vault_prefs";
    private static final String KEY_PIN_HASH = "pin_hash";
    private static final String KEY_IS_SETUP_DONE = "is_setup_done";
    private static final String KEY_IS_ONBOARDING_DONE = "is_onboarding_done";
    // Oturum boyunca PIN'i bellekte tut (şifreleme/çözme için lazım)
    private static String sessionPin = null;

    /**
     * Onboarding tamamlanmış mı kontrol eder.
     */
    public static boolean isOnboardingDone(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_ONBOARDING_DONE, false);
    }

    /**
     * Onboarding durumunu kaydeder.
     */
    public static void setOnboardingDone(Context context, boolean done) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_ONBOARDING_DONE, done).apply();
    }

    /**
     * İlk kurulum yapılmış mı kontrol eder.
     */
    public static boolean isSetupDone(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_SETUP_DONE, false);
    }

    /**
     * Yeni PIN kodu oluşturur ve hash'ini SharedPreferences'a kaydeder.
     */
    public static void createPin(Context context, String pin) {
        String hash = hashPin(pin);
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_PIN_HASH, hash)
                .putBoolean(KEY_IS_SETUP_DONE, true)
                .apply();
        sessionPin = pin;
    }

    /**
     * Girilen PIN kodunu doğrular.
     */
    public static boolean verifyPin(Context context, String pin) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedHash = prefs.getString(KEY_PIN_HASH, "");
        String inputHash = hashPin(pin);
        boolean matches = savedHash.equals(inputHash);
        if (matches) {
            sessionPin = pin;
        }
        return matches;
    }

    /**
     * PIN kodunu değiştirir. Önce mevcut PIN doğrulanmalıdır.
     */
    public static boolean changePin(Context context, String currentPin, String newPin) {
        if (!verifyPin(context, currentPin)) {
            return false;
        }
        createPin(context, newPin);
        return true;
    }

    /**
     * Oturumdaki PIN kodunu döndürür (şifreleme/çözme işlemleri için).
     */
    public static String getSessionPin() {
        return sessionPin;
    }

    /**
     * Oturumu temizler.
     */
    public static void clearSession() {
        sessionPin = null;
    }

    /**
     * Tüm verileri sıfırlar (PIN dahil).
     */
    public static void clearAll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        sessionPin = null;
    }

    /**
     * PIN kodunu SHA-256 ile hash'ler.
     */
    private static String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return pin; // Fallback
        }
    }
}
