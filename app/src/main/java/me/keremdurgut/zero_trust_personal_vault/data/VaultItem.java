package me.keremdurgut.zero_trust_personal_vault.data;

/**
 * Model sınıfı - Kasada saklanan her bir parola kaydını temsil eder.
 * Veritabanında id, title, username ve encryptedPassword alanları saklanır.
 */
public class VaultItem {

    private long id;
    private String title;
    private String username;
    private String encryptedPassword;

    // Yeni kayıt oluşturmak için (id olmadan)
    public VaultItem(String title, String username, String encryptedPassword) {
        this.title = title;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    // Veritabanından okuma için (id ile)
    public VaultItem(long id, String title, String username, String encryptedPassword) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
