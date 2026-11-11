package vn.edu.nlu.plantcare.ui.plant;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.UUID;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.databinding.FragmentAddEditPlantBinding;
import vn.edu.nlu.plantcare.ui.auth.AuthViewModel;

public class AddEditPlantFragment extends Fragment {

    private static final String ARG_PLANT_ID = "plant_id";

    private FragmentAddEditPlantBinding binding;
    private PlantViewModel plantViewModel;
    private AuthViewModel authViewModel;

    private String plantId;
    private Plant existingPlant;
    private String selectedPhotoUri;

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedPhotoUri = uri.toString();
                    Glide.with(this).load(uri).centerCrop().into(binding.ivPlantPhotoPreview);
                }
            });

    public static AddEditPlantFragment newInstance(String plantId) {
        AddEditPlantFragment fragment = new AddEditPlantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLANT_ID, plantId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plantId = getArguments().getString(ARG_PLANT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditPlantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        plantViewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.btnBackAddEdit.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        binding.btnPickImage.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        if (plantId != null) {
            binding.tvAddEditTitle.setText(R.string.edit_plant);
            loadExistingPlantData();
        } else {
            binding.tvAddEditTitle.setText(R.string.add_plant);
        }

        binding.btnSavePlant.setOnClickListener(v -> handleSavePlant());
    }

    private void loadExistingPlantData() {
        plantViewModel.getPlantById(plantId).observe(getViewLifecycleOwner(), plant -> {
            if (plant == null) return;
            existingPlant = plant;

            binding.etPlantName.setText(plant.getName());
            binding.etPlantType.setText(plant.getType());
            binding.etWateringInterval.setText(String.valueOf(plant.getWateringIntervalDays()));
            binding.etFertilizerInterval.setText(String.valueOf(plant.getFertilizerIntervalDays()));
            binding.etFertilizerType.setText(plant.getFertilizerType());
            binding.etNotes.setText(plant.getNote());

            selectedPhotoUri = plant.getImageUrl();
            if (selectedPhotoUri != null && !selectedPhotoUri.isEmpty()) {
                Glide.with(this).load(selectedPhotoUri).centerCrop().into(binding.ivPlantPhotoPreview);
            }
        });
    }

    private void handleSavePlant() {
        String name = binding.etPlantName.getText() != null ? binding.etPlantName.getText().toString().trim() : "";
        String type = binding.etPlantType.getText() != null ? binding.etPlantType.getText().toString().trim() : "";
        String waterIntervalStr = binding.etWateringInterval.getText() != null ? binding.etWateringInterval.getText().toString().trim() : "3";
        String fertilizerIntervalStr = binding.etFertilizerInterval.getText() != null ? binding.etFertilizerInterval.getText().toString().trim() : "30";
        String fertilizerType = binding.etFertilizerType.getText() != null ? binding.etFertilizerType.getText().toString().trim() : "Phân bón NPK";
        String notes = binding.etNotes.getText() != null ? binding.etNotes.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            binding.tilPlantName.setError("Vui lòng nhập tên cây");
            return;
        } else {
            binding.tilPlantName.setError(null);
        }

        int waterInterval = 3;
        try {
            waterInterval = Integer.parseInt(waterIntervalStr);
            if (waterInterval <= 0) waterInterval = 1;
        } catch (Exception ignored) {}

        int fertilizerInterval = 30;
        try {
            fertilizerInterval = Integer.parseInt(fertilizerIntervalStr);
            if (fertilizerInterval <= 0) fertilizerInterval = 1;
        } catch (Exception ignored) {}

        long now = System.currentTimeMillis();
        String userId = authViewModel.getCurrentUserId();

        if (existingPlant != null) {
            existingPlant.setName(name);
            existingPlant.setType(type);
            existingPlant.setWateringIntervalDays(waterInterval);
            existingPlant.setFertilizerIntervalDays(fertilizerInterval);
            existingPlant.setFertilizerType(fertilizerType);
            existingPlant.setNote(notes);
            existingPlant.setImageUrl(selectedPhotoUri);

            plantViewModel.updatePlant(existingPlant);
            Toast.makeText(getContext(), "Đã cập nhật cây cảnh!", Toast.LENGTH_SHORT).show();
        } else {
            String newId = UUID.randomUUID().toString();
            Plant newPlant = new Plant(
                    newId,
                    userId,
                    name,
                    type,
                    "",
                    selectedPhotoUri != null ? selectedPhotoUri : "",
                    notes,
                    waterInterval,
                    fertilizerInterval,
                    fertilizerType,
                    now,
                    now,
                    now + (waterInterval * 86400000L),
                    now + (fertilizerInterval * 86400000L),
                    false
            );

            plantViewModel.insertPlant(newPlant);
            Toast.makeText(getContext(), "Đã thêm cây cảnh mới thành công!", Toast.LENGTH_SHORT).show();
        }

        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
