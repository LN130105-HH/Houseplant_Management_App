package vn.edu.nlu.plantcare.ui.plant;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.databinding.FragmentPlantsBinding;
import vn.edu.nlu.plantcare.ui.MainActivity;
import vn.edu.nlu.plantcare.ui.auth.AuthViewModel;

public class PlantsFragment extends Fragment implements PlantAdapter.OnPlantClickListener {

    private FragmentPlantsBinding binding;
    private PlantViewModel plantViewModel;
    private CareViewModel careViewModel;
    private AuthViewModel authViewModel;

    private PlantAdapter plantAdapter;
    private String currentUserId;
    private List<Plant> currentAllPlants = new ArrayList<>();
    private String currentQuery = "";
    private int selectedFilterId = R.id.chipAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlantsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        plantViewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);
        careViewModel = new ViewModelProvider(requireActivity()).get(CareViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        currentUserId = authViewModel.getCurrentUserId();

        setupRecyclerView();
        setupSearchAndFilter();

        binding.fabAddPlant.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAddEditPlant(null);
            }
        });

        loadPlants();
    }

    private void setupRecyclerView() {
        plantAdapter = new PlantAdapter(this);
        binding.rvPlants.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPlants.setAdapter(plantAdapter);
    }

    private void setupSearchAndFilter() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s != null ? s.toString().trim() : "";
                applyFilterAndSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                selectedFilterId = checkedIds.get(0);
            } else {
                selectedFilterId = R.id.chipAll;
            }
            applyFilterAndSearch();
        });
    }

    private void loadPlants() {
        plantViewModel.getAllPlants(currentUserId).observe(getViewLifecycleOwner(), plants -> {
            currentAllPlants = plants != null ? plants : new ArrayList<>();
            applyFilterAndSearch();
        });
    }

    private void applyFilterAndSearch() {
        List<Plant> filtered = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (Plant p : currentAllPlants) {
            boolean matchesSearch = currentQuery.isEmpty() ||
                    p.getName().toLowerCase().contains(currentQuery.toLowerCase()) ||
                    (p.getType() != null && p.getType().toLowerCase().contains(currentQuery.toLowerCase()));

            if (!matchesSearch) continue;

            if (selectedFilterId == R.id.chipNeedsWater) {
                if (p.getNextWateringAt() <= now) {
                    filtered.add(p);
                }
            } else if (selectedFilterId == R.id.chipNeedsFertilizer) {
                if (p.getNextFertilizerAt() <= now) {
                    filtered.add(p);
                }
            } else {
                filtered.add(p);
            }
        }

        plantAdapter.setPlants(filtered);

        if (filtered.isEmpty()) {
            binding.tvEmptyPlants.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmptyPlants.setVisibility(View.GONE);
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
        careViewModel.waterPlant(plant, "Tưới nhanh từ danh sách");
        Toast.makeText(getContext(), "Đã tưới nước cho " + plant.getName() + " 💧", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
