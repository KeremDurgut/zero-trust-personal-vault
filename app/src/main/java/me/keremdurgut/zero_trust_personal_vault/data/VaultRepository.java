package me.keremdurgut.zero_trust_personal_vault.data;

import android.content.Context;

import java.util.List;

/**
 * Repository katmanı - ViewModel ile veritabanı arasında köprü görevi görür.
 * MVVM mimarisinde Data katmanını temsil eder.
 */
public class VaultRepository {

    private final DatabaseHelper databaseHelper;

    public VaultRepository(Context context) {
        this.databaseHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * Tüm parola kayıtlarını getirir.
     */
    public List<VaultItem> getAllItems() {
        return databaseHelper.getAllItems();
    }

    /**
     * Belirli bir parola kaydını getirir.
     */
    public VaultItem getItemById(long id) {
        return databaseHelper.getItemById(id);
    }

    /**
     * Yeni bir parola kaydı ekler.
     * @return eklenen kaydın ID'si
     */
    public long insertItem(VaultItem item) {
        return databaseHelper.insertItem(item);
    }

    /**
     * Mevcut bir parola kaydını günceller.
     * @return güncellenen satır sayısı
     */
    public int updateItem(VaultItem item) {
        return databaseHelper.updateItem(item);
    }

    /**
     * Bir parola kaydını siler.
     * @return silinen satır sayısı
     */
    public int deleteItem(long id) {
        return databaseHelper.deleteItem(id);
    }

    /**
     * Tüm kayıtları siler.
     */
    public void deleteAllItems() {
        databaseHelper.deleteAllItems();
    }
}
