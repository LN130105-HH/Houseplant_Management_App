package vn.edu.nlu.plantcare.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.databinding.ItemTodayCareBinding;

public class TodayCareAdapter extends RecyclerView.Adapter<TodayCareAdapter.TodayCareViewHolder> {

    public interface OnTodayCareClickListener {
        void onCareDone(Plant plant, boolean isWater, boolean isFertilizer);
        void onPlantSelected(Plant plant);
    }

    public static class CareTask {
        public Plant plant;
        public boolean isWater;
        public boolean isFertilizer;

        public CareTask(Plant plant, boolean isWater, boolean isFertilizer) {
            this.plant = plant;
            this.isWater = isWater;
            this.isFertilizer = isFertilizer;
        }
    }

    private List<CareTask> taskList = new ArrayList<>();
    private final OnTodayCareClickListener listener;

    public TodayCareAdapter(OnTodayCareClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<CareTask> tasks) {
        this.taskList = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodayCareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTodayCareBinding binding = ItemTodayCareBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new TodayCareViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayCareViewHolder holder, int position) {
        CareTask task = taskList.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TodayCareViewHolder extends RecyclerView.ViewHolder {
        private final ItemTodayCareBinding binding;

        public TodayCareViewHolder(ItemTodayCareBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CareTask task, OnTodayCareClickListener listener) {
            Context context = itemView.getContext();
            binding.tvCarePlantName.setText(task.plant.getName());

            if (task.plant.getImageUrl() != null && !task.plant.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(task.plant.getImageUrl())
                        .placeholder(R.drawable.ic_plant)
                        .error(R.drawable.ic_plant)
                        .into(binding.ivCarePlant);
            } else {
                binding.ivCarePlant.setImageResource(R.drawable.ic_plant);
            }

            if (task.isWater && task.isFertilizer) {
                binding.tvCareReason.setText("Cần tưới nước & bón phân hôm nay");
            } else if (task.isWater) {
                binding.tvCareReason.setText("Cần tưới nước hôm nay 💧");
            } else {
                binding.tvCareReason.setText("Cần bón phân định kỳ 🌿");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onPlantSelected(task.plant);
            });

            binding.btnDoCare.setOnClickListener(v -> {
                if (listener != null) listener.onCareDone(task.plant, task.isWater, task.isFertilizer);
            });
        }
    }
}
