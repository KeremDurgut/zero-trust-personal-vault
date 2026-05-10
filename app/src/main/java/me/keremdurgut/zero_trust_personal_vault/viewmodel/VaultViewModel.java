package me.keremdurgut.zero_trust_personal_vault.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import me.keremdurgut.zero_trust_personal_vault.data.VaultItem;
import me.keremdurgut.zero_trust_personal_vault.data.VaultRepository;
import me.keremdurgut.zero_trust_personal_vault.util.AESCrypt;
import me.keremdurgut.zero_trust_personal_vault.util.PinManager;

/**
 * ViewModel sınıfı - MVVM mimarisinde View ile Data katmanı arasında köprü.
 * LiveData kullanarak UI'yi reaktif olarak günceller.
 */
public class VaultViewModel extends AndroidViewModel {

    private final VaultRepository repository;
    private final MutableLiveData<List<VaultItem>> allItemsLiveData;
    private final MutableLiveData<VaultItem> selectedItemLiveData;

    public VaultViewModel(@NonNull Application application) {
        super(application);
        repository = new VaultRepository(application);
        allItemsLiveData = new MutableLiveData<>(new ArrayList<>());
        selectedItemLiveData = new MutableLiveData<>();
    }

    /**
     * Tüm parola kayıtlarını LiveData olarak döndürür.
     * Parolalar şifrelenmiş olarak gelir, UI tarafında çözülür.
     */
    public LiveData<List<VaultItem>> getAllItems() {
        return allItemsLiveData;
    }

    /**
     * Seçili parola kaydını LiveData olarak döndürür.
     */
    public LiveData<VaultItem> getSelectedItem() {
        return selectedItemLiveData;
    }

    /**
     * Veritabanından tüm kayıtları yükler ve LiveData'ya aktarır.
     */
    public void loadAllItems() {
        List<VaultItem> items = repository.getAllItems();
        allItemsLiveData.setValue(items);
    }

    /**
     * Belirli bir kaydı yükler.
     */
    public void loadItemById(long id) {
        VaultItem item = repository.getItemById(id);
        selectedItemLiveData.setValue(item);
    }

    /**
     * Yeni bir parola kaydı ekler. Parola AES ile şifrelenir.
     * @return eklenen kaydın ID'si, hata durumunda -1
     */
    public long addItem(String title, String username, String plainPassword) {
        String masterPin = PinManager.getSessionPin();
        if (masterPin == null) return -1;

        String encryptedPassword = AESCrypt.encrypt(plainPassword, masterPin);
        if (encryptedPassword == null) return -1;

        VaultItem item = new VaultItem(title, username, encryptedPassword);
        long id = repository.insertItem(item);
        if (id > 0) {
            loadAllItems(); // Listeyi güncelle
        }
        return id;
    }

    /**
     * Mevcut bir parola kaydını günceller. Parola AES ile yeniden şifrelenir.
     * @return güncelleme başarılı mı
     */
    public boolean updateItem(long id, String title, String username, String plainPassword) {
        String masterPin = PinManager.getSessionPin();
        if (masterPin == null) return false;

        String encryptedPassword = AESCrypt.encrypt(plainPassword, masterPin);
        if (encryptedPassword == null) return false;

        VaultItem item = new VaultItem(id, title, username, encryptedPassword);
        int result = repository.updateItem(item);
        if (result > 0) {
            loadAllItems(); // Listeyi güncelle
        }
        return result > 0;
    }

    /**
     * Bir parola kaydını siler.
     * @return silme başarılı mı
     */
    public boolean deleteItem(long id) {
        int result = repository.deleteItem(id);
        if (result > 0) {
            loadAllItems(); // Listeyi güncelle
        }
        return result > 0;
    }

    /**
     * Şifrelenmiş parolayı çözerek düz metin olarak döndürür.
     */
    public String decryptPassword(String encryptedPassword) {
        String masterPin = PinManager.getSessionPin();
        if (masterPin == null) return "[Oturum bulunamadı]";
        return AESCrypt.decrypt(encryptedPassword, masterPin);
    }

    /**
     * Tüm kayıtları siler.
     */
    public void deleteAllItems() {
        repository.deleteAllItems();
        loadAllItems();
    }
}
