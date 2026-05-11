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
 * Settings Fragment - Password change and data clearing operations.
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

        // Back button - navigate to home page (Vault List)
        binding.backNavButton.setOnClickListener(v -> 
            Navigation.findNavController(requireView()).popBackStack()
        );

        // Change Password button
        binding.changePasswordButton.setOnClickListener(v -> handleChangePassword());

        // Clear Data button
        binding.clearDataButton.setOnClickListener(v -> handleClearData());
    }

    /**
     * Changes the master password.
     * After the current password is verified, the new password is saved.
     */
    private void handleChangePassword() {
        if (binding.currentPasswordEditText.getText() == null || 
            binding.newPasswordEditText.getText() == null || 
            binding.confirmPasswordEditText.getText() == null) return;

        String currentPassword = binding.currentPasswordEditText.getText().toString().trim();
        String newPassword = binding.newPasswordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

        // Validations
        if (currentPassword.isEmpty()) {
            binding.currentPasswordInputLayout.setError(getString(R.string.error_master_password_empty));
            return;
        }
        binding.currentPasswordInputLayout.setError(null);

        if (!PinManager.verifyPin(requireContext(), currentPassword)) {
            binding.currentPasswordInputLayout.setError(getString(R.string.error_current_master_password_wrong));
            return;
        }
        binding.currentPasswordInputLayout.setError(null);

        if (newPassword.isEmpty()) {
            binding.newPasswordInputLayout.setError(getString(R.string.error_master_password_empty));
            return;
        }
        binding.newPasswordInputLayout.setError(null);

        if (newPassword.length() < 4) {
            binding.newPasswordInputLayout.setError(getString(R.string.error_master_password_min_length));
            return;
        }
        binding.newPasswordInputLayout.setError(null);

        if (!newPassword.equals(confirmPassword)) {
            binding.confirmPasswordInputLayout.setError(getString(R.string.error_master_password_mismatch));
            return;
        }
        binding.confirmPasswordInputLayout.setError(null);

        // Change password
        PinManager.createPin(requireContext(), newPassword);
        Toast.makeText(requireContext(), R.string.success_master_password_changed, Toast.LENGTH_SHORT).show();

        // Clear fields
        binding.currentPasswordEditText.setText("");
        binding.newPasswordEditText.setText("");
        binding.confirmPasswordEditText.setText("");
    }

    /**
     * Clears all data.
     * After confirmation with AlertDialog, database and password are reset.
     */
    private void handleClearData() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_clear_title)
                .setMessage(R.string.dialog_clear_message)
                .setPositiveButton(R.string.dialog_clear_yes, (dialog, which) -> {
                    // Delete all database records
                    viewModel.deleteAllItems();
                    // Reset password and setup status
                    PinManager.clearAll(requireContext());
                    Toast.makeText(requireContext(), R.string.toast_data_cleared,
                            Toast.LENGTH_SHORT).show();

                    // Go back to login screen
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_settings_to_login);
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
