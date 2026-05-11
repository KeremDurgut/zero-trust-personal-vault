package me.keremdurgut.zero_trust_personal_vault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.keremdurgut.zero_trust_personal_vault.R;
import me.keremdurgut.zero_trust_personal_vault.data.VaultItem;

/**
 * RecyclerView Adapter - Parola listesini görüntüler.
 */
public class VaultAdapter extends RecyclerView.Adapter<VaultAdapter.VaultViewHolder> {

    private List<VaultItem> items = new ArrayList<>();
    private final OnItemActionListener listener;

    public interface OnItemActionListener {
        void onItemClick(VaultItem item);
        void onCopyClick(VaultItem item);
    }

    public VaultAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<VaultItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vault, parent, false);
        return new VaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaultViewHolder holder, int position) {
        VaultItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VaultViewHolder extends RecyclerView.ViewHolder {

        private final TextView appNameText;
        private final ImageButton copyPasswordButton;

        public VaultViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameText = itemView.findViewById(R.id.appNameText);
            copyPasswordButton = itemView.findViewById(R.id.copyPasswordButton);
        }

        public void bind(VaultItem item) {
            appNameText.setText(item.getTitle());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

            copyPasswordButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCopyClick(item);
                }
            });
        }
    }
}
