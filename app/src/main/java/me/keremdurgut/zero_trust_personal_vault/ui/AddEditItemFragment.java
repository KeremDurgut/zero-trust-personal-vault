package me.keremdurgut.zero_trust_personal_vault.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import me.keremdurgut.zero_trust_personal_vault.R;
import me.keremdurgut.zero_trust_personal_vault.data.VaultItem;
import me.keremdurgut.zero_trust_personal_vault.databinding.FragmentAddEditItemBinding;
import me.keremdurgut.zero_trust_personal_vault.util.NotificationHelper;
import me.keremdurgut.zero_trust_personal_vault.viewmodel.VaultViewModel;

/**
 * Parola Ekleme/Düzenleme Fragment'ı.
 * itemId = -1 ise yeni ekleme, aksi halde düzenleme modunda çalışır.
 */
public class AddEditItemFragment extends Fragment {

    private FragmentAddEditItemBinding binding;
    private VaultViewModel viewModel;
    private long editItemId = -1L;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditItemBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(VaultViewModel.class);

        // Argümanlardan itemId'yi al
        if (getArguments() != null) {
            editItemId = getArguments().getLong("itemId", -1L);
        }

        isEditMode = (editItemId != -1L);

        if (isEditMode) {
            // Düzenleme modu - mevcut verileri form alanlarına doldur
            binding.tvFormTitle.setText(R.string.edit_item_title);
            loadItemData();
        } else {
            // Ekleme modu
            binding.tvFormTitle.setText(R.string.add_item_title);
        }

        // Kaydet butonu
        binding.btnSave.setOnClickListener(v -> saveItem());
    }

    /**
     * Düzenleme modunda mevcut item verilerini yükler.
     */
    private void loadItemData() {
        viewModel.loadItemById(editItemId);
        viewModel.getSelectedItem().observe(getViewLifecycleOwner(), item -> {
            if (item != null) {
                binding.etTitle.setText(item.getTitle());
                binding.etUsername.setText(item.getUsername());
                // Şifreli parolayı çöz ve göster
                String decryptedPassword = viewModel.decryptPassword(item.getEncryptedPassword());
                binding.etPassword.setText(decryptedPassword);
            }
        });
    }

    /**
     * Form doğrulama ve kaydetme işlemi.
     */
    private void saveItem() {
        String title = binding.etTitle.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Doğrulama
        if (title.isEmpty()) {
            binding.tilTitle.setError(getString(R.string.error_title_empty));
            return;
        }
        binding.tilTitle.setError(null);

        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.error_password_empty));
            return;
        }
        binding.tilPassword.setError(null);

        if (isEditMode) {
            // Güncelleme
            boolean success = viewModel.updateItem(editItemId, title, username, password);
            if (success) {
                Toast.makeText(requireContext(), R.string.toast_item_updated, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Yeni ekleme
            long id = viewModel.addItem(title, username, password);
            if (id > 0) {
                Toast.makeText(requireContext(), R.string.toast_item_saved, Toast.LENGTH_SHORT).show();

                // Bildirim gönder (yeni ekleme)
                NotificationHelper.showPasswordSavedNotification(requireContext(), title);
            }
        }

        // Geri dön
        Navigation.findNavController(requireView()).popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
