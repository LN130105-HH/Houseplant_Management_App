package vn.edu.nlu.plantcare.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.databinding.FragmentHomeBinding;
import vn.edu.nlu.plantcare.ui.MainActivity;
import vn.edu.nlu.plantcare.ui.auth.AuthViewModel;
import vn.edu.nlu.plantcare.ui.plant.CareViewModel;
import vn.edu.nlu.plantcare.ui.plant.PlantAdapter;
import vn.edu.nlu.plantcare.ui.plant.PlantViewModel;

public class HomeFragment extends Fragment implements TodayCareAdapter.OnTodayCareClickListener, PlantAdapter.OnPlantClickListener {

    private FragmentHomeBinding binding;
    private PlantViewModel plantViewModel;
    private CareViewModel careViewModel;
    private AuthViewModel authViewModel;

    private TodayCareAdapter todayCareAdapter;
    private PlantAdapter recentPlantAdapter;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        plantViewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);
        careViewModel = new ViewModelProvider(requireActivity()).get(CareViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        currentUserId = authViewModel.getCurrentUserId();
        binding.tvGreeting.setText(getString(R.string.home_greeting, authViewModel.getCurrentDisplayName()));

        plantViewModel.seedInitialDataIfEmpty(currentUserId);

        setupRecyclerViews();
        observeData();

        binding.cardQuickAdd.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAddEditPlant(null);
            }
        });

        binding.cardQuickAi.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAIScan();
            }
        });
    }

    private void setupRecyclerViews() {
        todayCareAdapter = new TodayCareAdapter(this);
        binding.rvTodayCare.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTodayCare.setAdapter(todayCareAdapter);

        recentPlantAdapter = new PlantAdapter(this);
        binding.rvRecentPlants.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRecentPlants.setAdapter(recentPlantAdapter);
    }

    private void observeData() {
        long now = System.currentTimeMillis();

        plantViewModel.getAllPlants(currentUserId).observe(getViewLifecycleOwner(), plants -> {
            if (plants != null && !plants.isEmpty()) {
                recentPlantAdapter.setPlants(plants);
                binding.tvMyGardenHeader.setText("Vườn cây của tôi (" + plants.size() + ")");

                List<TodayCareAdapter.CareTask> tasks = new ArrayList<>();
                int waterDueCount = 0;
                int fertilizerDueCount = 0;

                for (Plant p : plants) {
                    boolean needWater = p.getNextWateringAt() <= now;
                    boolean needFertilizer = p.getNextFertilizerAt() <= now;

                    if (needWater) waterDueCount++;
                    if (needFertilizer) fertilizerDueCount++;

                    if (needWater || needFertilizer) {
                        tasks.add(new TodayCareAdapter.CareTask(p, needWater, needFertilizer));
                    }
                }

                binding.tvWaterDueCount.setText(String.valueOf(waterDueCount));
                binding.tvFertilizerDueCount.setText(String.valueOf(fertilizerDueCount));

                if (tasks.isEmpty()) {
                    binding.tvEmptyTodayCare.setVisibility(View.VISIBLE);
                    binding.rvTodayCare.setVisibility(View.GONE);
                } else {
                    binding.tvEmptyTodayCare.setVisibility(View.GONE);
                    binding.rvTodayCare.setVisibility(View.VISIBLE);
                    todayCareAdapter.setTasks(tasks);
                }
            } else {
                binding.tvMyGardenHeader.setText("Vườn cây của tôi (0)");
                binding.tvWaterDueCount.setText("0");
                binding.tvFertilizerDueCount.setText("0");
                binding.tvEmptyTodayCare.setVisibility(View.VISIBLE);
                binding.rvTodayCare.setVisibility(View.GONE);
                recentPlantAdapter.setPlants(new ArrayList<>());
            }
        });
    }

    @Override
    public void onCareDone(Plant plant, boolean isWater, boolean isFertilizer) {
        if (isWater) {
            careViewModel.waterPlant(plant, "Chăm sóc nhanh từ trang chủ");
        }
        if (isFertilizer) {
            careViewModel.fertilizePlant(plant, plant.getFertilizerType(), "Chăm sóc nhanh từ trang chủ");
        }
        Toast.makeText(getContext(), "Đã cập nhật lịch chăm sóc cho " + plant.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlantSelected(Plant plant) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToPlantDetail(plant.getId());
        }
    }

    @Override
    public void onPlantClick(Plant plant) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateToPlantDetail(plant.getId());
        }
    }

    @Override
    public void onQuickWaterClick(Plant plant) {
        careViewModel.waterPlant(plant, "Tưới nhanh");
        Toast.makeText(getContext(), "Đã tưới nước cho " + plant.getName() + " 💧", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
