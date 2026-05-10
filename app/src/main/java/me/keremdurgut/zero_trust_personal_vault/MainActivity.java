package me.keremdurgut.zero_trust_personal_vault;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import me.keremdurgut.zero_trust_personal_vault.databinding.ActivityMainBinding;
import me.keremdurgut.zero_trust_personal_vault.util.NotificationHelper;

/**
 * Tek Aktivite (Single Activity) - Tüm ekranlar Fragment olarak yönetilir.
 * Navigation Component ile fragment geçişleri kontrol edilir.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Bildirim kanalını oluştur
        NotificationHelper.createNotificationChannel(this);

        // Navigation Controller'ı başlat
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}