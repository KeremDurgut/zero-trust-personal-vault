package me.keremdurgut.zero_trust_personal_vault.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import me.keremdurgut.zero_trust_personal_vault.R;
import me.keremdurgut.zero_trust_personal_vault.adapter.VaultAdapter;
import me.keremdurgut.zero_trust_personal_vault.data.VaultItem;
import me.keremdurgut.zero_trust_personal_vault.databinding.FragmentVaultListBinding;
import me.keremdurgut.zero_trust_personal_vault.viewmodel.VaultViewModel;

/**
 * Parola Listesi Fragment'ı - Kayıtlı parolaları RecyclerView ile listeler.
 * LiveData ile veritabanı değişikliklerini reaktif olarak izler.
 */
public class VaultListFragment extends Fragment implements VaultAdapter.OnItemActionListener {

    private FragmentVaultListBinding binding;
    private VaultViewModel viewModel;
    private VaultAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVaultListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel başlat
        viewModel = new ViewModelProvider(requireActivity()).get(VaultViewModel.class);

        // RecyclerView kurulumu
        adapter = new VaultAdapter(this);
        binding.rvVaultItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvVaultItems.setAdapter(adapter);

        // LiveData gözlemcisi - liste güncellendiğinde UI güncellenir
        viewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
            // Boş durum mesajını göster/gizle
            if (items == null || items.isEmpty()) {
                binding.tvEmptyMessage.setVisibility(View.VISIBLE);
                binding.rvVaultItems.setVisibility(View.GONE);
            } else {
                binding.tvEmptyMessage.setVisibility(View.GONE);
                binding.rvVaultItems.setVisibility(View.VISIBLE);
            }
        });

        // FAB - Yeni parola ekle
        binding.fabAdd.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("itemId", -1L);
            Navigation.findNavController(view)
                    .navigate(R.id.action_vaultList_to_addEditItem, args);
        });

        // Ayarlar butonu
        binding.btnSettings.setOnClickListener(v -> {
            Navigation.findNavController(view)
                    .navigate(R.id.action_vaultList_to_settings);
        });

        // Verileri yükle
        viewModel.loadAllItems();
    }

    /**
     * Satıra tıklama - Düzenleme ekranına git.
     */
    @Override
    public void onItemClick(VaultItem item) {
        Bundle args = new Bundle();
        args.putLong("itemId", item.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_vaultList_to_addEditItem, args);
    }

    /**
     * Kopyala butonu - Parolayı çözüp panoya kopyalar ve Toast gösterir.
     */
    @Override
    public void onCopyClick(VaultItem item) {
        String decryptedPassword = viewModel.decryptPassword(item.getEncryptedPassword());

        ClipboardManager clipboard = (ClipboardManager)
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("password", decryptedPassword);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }

        // Toast mesajı göster
        Toast.makeText(requireContext(), R.string.toast_password_copied, Toast.LENGTH_SHORT).show();
    }

    /**
     * Sil butonu - AlertDialog ile onay alır, sonra siler.
     */
    @Override
    public void onDeleteClick(VaultItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(R.string.dialog_delete_yes, (dialog, which) -> {
                    viewModel.deleteItem(item.getId());
                    Toast.makeText(requireContext(), R.string.toast_item_deleted,
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.dialog_delete_no, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
