package vn.edu.nlu.plantcare.ui.plant;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.databinding.ItemCareHistoryBinding;

public class CareHistoryAdapter extends RecyclerView.Adapter<CareHistoryAdapter.HistoryViewHolder> {

    public static class HistoryItem {
        public boolean isWater;
        public String title;
        public String note;
        public long timestamp;

        public HistoryItem(boolean isWater, String title, String note, long timestamp) {
            this.isWater = isWater;
            this.title = title;
            this.note = note;
            this.timestamp = timestamp;
        }
    }

    private List<HistoryItem> items = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public void setItems(List<HistoryItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCareHistoryBinding binding = ItemCareHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(items.get(position), dateFormat);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCareHistoryBinding binding;

        public HistoryViewHolder(ItemCareHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HistoryItem item, SimpleDateFormat format) {
            binding.tvHistoryTitle.setText(item.title);
            binding.tvHistoryNote.setText(item.note != null ? item.note : "");
            binding.tvHistoryTime.setText(format.format(new Date(item.timestamp)));

            if (item.isWater) {
                binding.ivHistoryIcon.setImageResource(R.drawable.ic_water_drop);
                binding.ivHistoryIcon.setColorFilter(itemView.getContext().getColor(R.color.water_blue));
            } else {
                binding.ivHistoryIcon.setImageResource(R.drawable.ic_fertilizer);
                binding.ivHistoryIcon.setColorFilter(itemView.getContext().getColor(R.color.fertilizer_amber));
            }
        }
    }
}
