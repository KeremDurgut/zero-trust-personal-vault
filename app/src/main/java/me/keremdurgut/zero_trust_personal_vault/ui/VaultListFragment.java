package me.keremdurgut.zero_trust_personal_vault.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.keremdurgut.zero_trust_personal_vault.R;
import me.keremdurgut.zero_trust_personal_vault.adapter.VaultAdapter;
import me.keremdurgut.zero_trust_personal_vault.data.VaultItem;
import me.keremdurgut.zero_trust_personal_vault.databinding.FragmentVaultListBinding;
import me.keremdurgut.zero_trust_personal_vault.viewmodel.VaultViewModel;

/**
 * Parola Listesi Fragment'ı - Kayıtlı parolaları listeler.
 */
public class VaultListFragment extends Fragment implements VaultAdapter.OnItemActionListener {

    private FragmentVaultListBinding binding;
    private VaultViewModel viewModel;
    private VaultAdapter adapter;
    private List<VaultItem> allItems = new ArrayList<>();

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
        binding.passwordsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.passwordsRecyclerView.setAdapter(adapter);

        // LiveData gözlemcisi - liste güncellendiğinde UI güncellenir
        viewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            allItems = items != null ? items : new ArrayList<>();
            updateList(allItems);
            binding.storedCountText.setText(String.valueOf(allItems.size()));
        });

        // FAB - Yeni parola ekle
        binding.addEntryFab.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("itemId", -1L);
            Navigation.findNavController(view)
                    .navigate(R.id.action_vaultList_to_addEditItem, args);
        });

        // Profil/Ayarlar butonu
        binding.settingsButton.setOnClickListener(v -> {
            Navigation.findNavController(view)
                    .navigate(R.id.action_vaultList_to_settings);
        });

        // Arama özelliği
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Verileri yükle
        viewModel.loadAllItems();
    }

    private void filterList(String query) {
        if (query.isEmpty()) {
            updateList(allItems);
        } else {
            List<VaultItem> filtered = allItems.stream()
                    .filter(item -> item.getTitle().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            updateList(filtered);
        }
    }

    private void updateList(List<VaultItem> items) {
        adapter.setItems(items);
        // Boş durum kontrolü yapılabilir
    }

    @Override
    public void onItemClick(VaultItem item) {
        Bundle args = new Bundle();
        args.putLong("itemId", item.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_vaultList_to_addEditItem, args);
    }

    @Override
    public void onCopyClick(VaultItem item) {
        String decryptedPassword = viewModel.decryptPassword(item.getEncryptedPassword());

        ClipboardManager clipboard = (ClipboardManager)
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("password", decryptedPassword);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }

        Toast.makeText(requireContext(), R.string.toast_password_copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
