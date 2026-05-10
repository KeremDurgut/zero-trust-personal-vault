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
 * Her satırda başlık, kullanıcı adı ve eylem butonları bulunur.
 */
public class VaultAdapter extends RecyclerView.Adapter<VaultAdapter.VaultViewHolder> {

    private List<VaultItem> items = new ArrayList<>();
    private final OnItemActionListener listener;

    /**
     * Adapter eylem dinleyici arayüzü.
     */
    public interface OnItemActionListener {
        void onItemClick(VaultItem item);
        void onCopyClick(VaultItem item);
        void onDeleteClick(VaultItem item);
    }

    public VaultAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    /**
     * Listeyi günceller ve RecyclerView'ı yeniler.
     */
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

    /**
     * ViewHolder - Her bir parola satırının görünümünü tutar.
     */
    class VaultViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvUsername;
        private final ImageButton btnCopy;
        private final ImageButton btnDelete;

        public VaultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvUsername = itemView.findViewById(R.id.tvItemUsername);
            btnCopy = itemView.findViewById(R.id.btnCopy);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(VaultItem item) {
            tvTitle.setText(item.getTitle());
            tvUsername.setText(item.getUsername() != null && !item.getUsername().isEmpty()
                    ? item.getUsername() : "-");

            // Satıra tıklama -> düzenleme
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

            // Kopyala butonu -> parolayı panoya kopyala
            btnCopy.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCopyClick(item);
                }
            });

            // Sil butonu -> silme onayı
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item);
                }
            });
        }
    }
}
