package vn.edu.nlu.plantcare.ui.journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.edu.nlu.plantcare.data.local.entity.JournalEntry;
import vn.edu.nlu.plantcare.databinding.ItemJournalBinding;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    public interface OnJournalDeleteListener {
        void onDelete(JournalEntry entry);
    }

    private List<JournalEntry> entries = new ArrayList<>();
    private final OnJournalDeleteListener deleteListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public JournalAdapter(OnJournalDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setEntries(List<JournalEntry> entries) {
        this.entries = entries != null ? entries : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJournalBinding binding = ItemJournalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new JournalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        JournalEntry entry = entries.get(position);
        holder.bind(entry, dateFormat, deleteListener);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {
        private final ItemJournalBinding binding;

        public JournalViewHolder(ItemJournalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(JournalEntry entry, SimpleDateFormat dateFormat, OnJournalDeleteListener listener) {
            Context context = itemView.getContext();
            binding.tvJournalDate.setText(dateFormat.format(new Date(entry.getDate())));

            if (entry.getHeightCm() > 0) {
                binding.tvJournalHeight.setVisibility(View.VISIBLE);
                binding.tvJournalHeight.setText("Cao: " + entry.getHeightCm() + " cm");
            } else {
                binding.tvJournalHeight.setVisibility(View.GONE);
            }

            binding.tvJournalNote.setText(entry.getNote());

            if (entry.getImageUrl() != null && !entry.getImageUrl().isEmpty()) {
                binding.ivJournalPhoto.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(entry.getImageUrl())
                        .centerCrop()
                        .into(binding.ivJournalPhoto);
            } else {
                binding.ivJournalPhoto.setVisibility(View.GONE);
            }

            binding.btnDeleteJournal.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(entry);
            });
        }
    }
}
