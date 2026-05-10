package me.keremdurgut.zero_trust_personal_vault.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import me.keremdurgut.zero_trust_personal_vault.R;
import me.keremdurgut.zero_trust_personal_vault.databinding.FragmentSettingsBinding;
import me.keremdurgut.zero_trust_personal_vault.util.PinManager;
import me.keremdurgut.zero_trust_personal_vault.viewmodel.VaultViewModel;

/**
 * Ayarlar Fragment'ı - PIN değiştirme ve veri temizleme işlemleri.
 */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private VaultViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(VaultViewModel.class);

        // PIN Değiştir butonu
        binding.btnChangePin.setOnClickListener(v -> handleChangePin());

        // Verileri Temizle butonu
        binding.btnClearData.setOnClickListener(v -> handleClearData());
    }

    /**
     * PIN kodunu değiştirir.
     * Mevcut PIN doğrulandıktan sonra yeni PIN kaydedilir.
     */
    private void handleChangePin() {
        String currentPin = binding.etCurrentPin.getText().toString().trim();
        String newPin = binding.etNewPin.getText().toString().trim();
        String newPinConfirm = binding.etNewPinConfirm.getText().toString().trim();

        // Doğrulamalar
        if (currentPin.isEmpty()) {
            binding.tilCurrentPin.setError(getString(R.string.error_pin_empty));
            return;
        }
        binding.tilCurrentPin.setError(null);

        if (!PinManager.verifyPin(requireContext(), currentPin)) {
            binding.tilCurrentPin.setError(getString(R.string.error_current_pin_wrong));
            return;
        }
        binding.tilCurrentPin.setError(null);

        if (newPin.isEmpty()) {
            binding.tilNewPin.setError(getString(R.string.error_pin_empty));
            return;
        }
        binding.tilNewPin.setError(null);

        if (newPin.length() < 4) {
            binding.tilNewPin.setError(getString(R.string.error_pin_min_length));
            return;
        }
        binding.tilNewPin.setError(null);

        if (!newPin.equals(newPinConfirm)) {
            binding.tilNewPinConfirm.setError(getString(R.string.error_pin_mismatch));
            return;
        }
        binding.tilNewPinConfirm.setError(null);

        // PIN'i değiştir
        PinManager.changePin(requireContext(), currentPin, newPin);
        Toast.makeText(requireContext(), R.string.success_pin_changed, Toast.LENGTH_SHORT).show();

        // Alanları temizle
        binding.etCurrentPin.setText("");
        binding.etNewPin.setText("");
        binding.etNewPinConfirm.setText("");
    }

    /**
     * Tüm verileri temizler.
     * AlertDialog ile onay alındıktan sonra veritabanı ve PIN sıfırlanır.
     */
    private void handleClearData() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_clear_title)
                .setMessage(R.string.dialog_clear_message)
                .setPositiveButton(R.string.dialog_clear_yes, (dialog, which) -> {
                    // Tüm veritabanı kayıtlarını sil
                    viewModel.deleteAllItems();
                    // PIN'i de sıfırla
                    PinManager.clearAll(requireContext());
                    Toast.makeText(requireContext(), R.string.toast_data_cleared,
                            Toast.LENGTH_SHORT).show();

                    // Login ekranına geri dön
                    Navigation.findNavController(requireView())
                            .navigate(R.id.loginFragment);
                })
                .setNegativeButton(R.string.dialog_clear_no, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
