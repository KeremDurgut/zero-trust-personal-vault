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
import me.keremdurgut.zero_trust_personal_vault.databinding.FragmentAuthBinding;
import me.keremdurgut.zero_trust_personal_vault.util.PinManager;

/**
 * Auth Fragment - Handles both Login and Master Password Setup (Register).
 * Displays different strings based on whether the setup is complete.
 */
public class LoginFragment extends Fragment {

    private FragmentAuthBinding binding;
    private boolean isSetupMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check if setup is done
        isSetupMode = !PinManager.isSetupDone(requireContext());

        if (isSetupMode) {
            // Setup Mode (Register)
            binding.authTitleText.setText(R.string.auth_register_title);
            binding.authSubtitleText.setText(R.string.auth_register_subtitle);
            binding.confirmPasswordInputLayout.setVisibility(View.VISIBLE);
            binding.authButton.setText(R.string.auth_register_button);
            binding.authButton.setOnClickListener(v -> handleSetup());
        } else {
            // Login Mode
            binding.authTitleText.setText(R.string.auth_login_title);
            binding.authSubtitleText.setText(R.string.auth_login_subtitle);
            binding.confirmPasswordInputLayout.setVisibility(View.GONE);
            binding.authButton.setText(R.string.auth_login_button);
            binding.authButton.setOnClickListener(v -> handleLogin());
        }
    }

    /**
     * Initial Setup: Creates new master password.
     */
    private void handleSetup() {
        if (binding.passwordEditText.getText() == null || binding.confirmPasswordEditText.getText() == null) return;
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

        if (password.isEmpty()) {
            binding.passwordInputLayout.setError(getString(R.string.error_master_password_empty));
            return;
        }
        binding.passwordInputLayout.setError(null);

        if (password.length() < 4) {
            binding.passwordInputLayout.setError(getString(R.string.error_master_password_min_length));
            return;
        }
        binding.passwordInputLayout.setError(null);

        if (!password.equals(confirmPassword)) {
            binding.confirmPasswordInputLayout.setError(getString(R.string.error_master_password_mismatch));
            return;
        }
        binding.confirmPasswordInputLayout.setError(null);

        // Create and save password
        PinManager.createPin(requireContext(), password);
        Toast.makeText(requireContext(), R.string.success_master_password_created, Toast.LENGTH_SHORT).show();

        // Navigate to Vault List
        navigateToVaultList();
    }

    /**
     * Normal Login: Verifies master password.
     */
    private void handleLogin() {
        if (binding.passwordEditText.getText() == null) return;
        String password = binding.passwordEditText.getText().toString().trim();

        if (password.isEmpty()) {
            binding.passwordInputLayout.setError(getString(R.string.error_master_password_empty));
            return;
        }
        binding.passwordInputLayout.setError(null);

        if (PinManager.verifyPin(requireContext(), password)) {
            // Correct password - navigate to vault
            navigateToVaultList();
        } else {
            binding.passwordInputLayout.setError(getString(R.string.error_wrong_master_password));
        }
    }

    /**
     * Transitions to the Vault List screen.
     */
    private void navigateToVaultList() {
        if (getView() != null) {
            Navigation.findNavController(getView())
                    .navigate(R.id.action_login_to_vaultList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
