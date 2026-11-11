package vn.edu.nlu.plantcare.ui.plant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.databinding.ItemPlantBinding;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {

    public interface OnPlantClickListener {
        void onPlantClick(Plant plant);
        void onQuickWaterClick(Plant plant);
    }

    private List<Plant> plantList = new ArrayList<>();
    private final OnPlantClickListener listener;

    public PlantAdapter(OnPlantClickListener listener) {
        this.listener = listener;
    }

    public void setPlants(List<Plant> plants) {
        this.plantList = plants != null ? plants : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlantBinding binding = ItemPlantBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new PlantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);
        holder.bind(plant, listener);
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlantBinding binding;

        public PlantViewHolder(ItemPlantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Plant plant, OnPlantClickListener listener) {
            Context context = itemView.getContext();
            binding.tvPlantName.setText(plant.getName());
            binding.tvPlantType.setText(plant.getType() != null && !plant.getType().isEmpty() ? plant.getType() : "Cây cảnh trong nhà");

            // Load photo
            if (plant.getImageUrl() != null && !plant.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(plant.getImageUrl())
                        .placeholder(R.drawable.ic_plant)
                        .error(R.drawable.ic_plant)
                        .centerCrop()
                        .into(binding.ivPlant);
            } else {
                binding.ivPlant.setImageResource(R.drawable.ic_plant);
            }

            // Water status calculation
            long now = System.currentTimeMillis();
            long diffWaterMillis = plant.getNextWateringAt() - now;
            long diffWaterDays = diffWaterMillis / 86400000L;

            if (diffWaterMillis <= 0) {
                long overdueDays = Math.abs(diffWaterDays);
                if (overdueDays == 0) {
                    binding.tvWaterStatus.setText("Tưới hôm nay!");
                } else {
                    binding.tvWaterStatus.setText("Trễ " + overdueDays + " ngày");
                }
                binding.tvWaterStatus.setBackgroundResource(R.drawable.bg_badge_alert);
                binding.tvWaterStatus.setTextColor(ContextCompat.getColor(context, R.color.error));
            } else {
                binding.tvWaterStatus.setText(diffWaterDays == 0 ? "Tưới hôm nay" : diffWaterDays + " ngày nữa");
                binding.tvWaterStatus.setBackgroundResource(R.drawable.bg_badge_water);
                binding.tvWaterStatus.setTextColor(ContextCompat.getColor(context, R.color.water_blue));
            }

            // Fertilizer status calculation
            long diffFertilizerMillis = plant.getNextFertilizerAt() - now;
            long diffFertilizerDays = diffFertilizerMillis / 86400000L;

            if (diffFertilizerMillis <= 0) {
                binding.tvFertilizerStatus.setText("Bón phân ngay!");
                binding.tvFertilizerStatus.setBackgroundResource(R.drawable.bg_badge_alert);
                binding.tvFertilizerStatus.setTextColor(ContextCompat.getColor(context, R.color.error));
            } else {
                binding.tvFertilizerStatus.setText(diffFertilizerDays + " ngày");
                binding.tvFertilizerStatus.setBackgroundResource(R.drawable.bg_badge_fertilizer);
                binding.tvFertilizerStatus.setTextColor(ContextCompat.getColor(context, R.color.fertilizer_amber));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onPlantClick(plant);
            });

            binding.btnQuickWater.setOnClickListener(v -> {
                if (listener != null) listener.onQuickWaterClick(plant);
            });
        }
    }
}
