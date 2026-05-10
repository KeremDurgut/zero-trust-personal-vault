package me.keremdurgut.zero_trust_personal_vault.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import me.keremdurgut.zero_trust_personal_vault.R;
import me.keremdurgut.zero_trust_personal_vault.databinding.FragmentLoginBinding;
import me.keremdurgut.zero_trust_personal_vault.util.PinManager;

/**
 * Giriş Fragment'ı - Ana PIN kodu ile kimlik doğrulama.
 * İlk kurulumda PIN oluşturma, sonraki girişlerde PIN doğrulama yapar.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private boolean isSetupMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // İlk kurulum mu yoksa normal giriş mi kontrol et
        isSetupMode = !PinManager.isSetupDone(requireContext());

        if (isSetupMode) {
            // İlk kurulum modu
            binding.tvLoginTitle.setText(R.string.setup_title);
            binding.tvLoginSubtitle.setText(R.string.setup_subtitle);
            binding.tilPinConfirm.setVisibility(View.VISIBLE);
            binding.btnLogin.setText(R.string.btn_setup);
        } else {
            // Normal giriş modu
            binding.tvLoginTitle.setText(R.string.login_title);
            binding.tvLoginSubtitle.setText(R.string.login_subtitle);
            binding.tilPinConfirm.setVisibility(View.GONE);
            binding.btnLogin.setText(R.string.btn_login);
        }

        binding.btnLogin.setOnClickListener(v -> {
            if (isSetupMode) {
                handleSetup();
            } else {
                handleLogin();
            }
        });
    }

    /**
     * İlk kurulum: Yeni PIN oluşturur.
     */
    private void handleSetup() {
        String pin = binding.etPin.getText().toString().trim();
        String pinConfirm = binding.etPinConfirm.getText().toString().trim();

        if (pin.isEmpty()) {
            binding.tilPin.setError(getString(R.string.error_pin_empty));
            return;
        }
        binding.tilPin.setError(null);

        if (pin.length() < 4) {
            binding.tilPin.setError(getString(R.string.error_pin_min_length));
            return;
        }
        binding.tilPin.setError(null);

        if (!pin.equals(pinConfirm)) {
            binding.tilPinConfirm.setError(getString(R.string.error_pin_mismatch));
            return;
        }
        binding.tilPinConfirm.setError(null);

        // PIN oluştur ve kaydet
        PinManager.createPin(requireContext(), pin);
        Toast.makeText(requireContext(), R.string.success_pin_created, Toast.LENGTH_SHORT).show();

        // Vault listesine yönlendir
        navigateToVaultList();
    }

    /**
     * Normal giriş: PIN doğrulama.
     */
    private void handleLogin() {
        String pin = binding.etPin.getText().toString().trim();

        if (pin.isEmpty()) {
            binding.tilPin.setError(getString(R.string.error_pin_empty));
            return;
        }
        binding.tilPin.setError(null);

        if (PinManager.verifyPin(requireContext(), pin)) {
            // Doğru PIN - vault listesine yönlendir
            navigateToVaultList();
        } else {
            binding.tilPin.setError(getString(R.string.error_wrong_pin));
        }
    }

    /**
     * Vault listesine geçiş yapar.
     */
    private void navigateToVaultList() {
        Navigation.findNavController(requireView())
                .navigate(R.id.action_login_to_vaultList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
