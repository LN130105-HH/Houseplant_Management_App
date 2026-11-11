package vn.edu.nlu.plantcare.ui.plant;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.data.local.entity.FertilizerLog;
import vn.edu.nlu.plantcare.data.local.entity.JournalEntry;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.data.local.entity.WateringLog;
import vn.edu.nlu.plantcare.databinding.DialogAddJournalBinding;
import vn.edu.nlu.plantcare.databinding.DialogCareConfirmBinding;
import vn.edu.nlu.plantcare.databinding.FragmentPlantDetailBinding;
import vn.edu.nlu.plantcare.ui.MainActivity;
import vn.edu.nlu.plantcare.ui.journal.JournalAdapter;
import vn.edu.nlu.plantcare.ui.journal.JournalViewModel;

public class PlantDetailFragment extends Fragment {

    private static final String ARG_PLANT_ID = "plant_id";

    private FragmentPlantDetailBinding binding;
    private PlantViewModel plantViewModel;
    private CareViewModel careViewModel;
    private JournalViewModel journalViewModel;

    private String plantId;
    private Plant currentPlant;

    private JournalAdapter journalAdapter;
    private CareHistoryAdapter historyAdapter;

    private List<WateringLog> currentWaterLogs = new ArrayList<>();
    private List<FertilizerLog> currentFertilizerLogs = new ArrayList<>();

    private String selectedJournalPhotoUri = null;

    public static PlantDetailFragment newInstance(String plantId) {
        PlantDetailFragment fragment = new PlantDetailFragment();
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
        binding = FragmentPlantDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        plantViewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);
        careViewModel = new ViewModelProvider(requireActivity()).get(CareViewModel.class);
        journalViewModel = new ViewModelProvider(requireActivity()).get(JournalViewModel.class);

        setupRecyclerViews();
        setupListeners();
        loadPlantDetails();
    }

    private void setupRecyclerViews() {
        journalAdapter = new JournalAdapter(entry -> {
            journalViewModel.deleteJournalEntry(entry);
            Toast.makeText(getContext(), "Đã xóa nhật ký", Toast.LENGTH_SHORT).show();
        });
        binding.rvJournalEntries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvJournalEntries.setAdapter(journalAdapter);

        historyAdapter = new CareHistoryAdapter();
        binding.rvCareHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCareHistory.setAdapter(historyAdapter);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.btnEditPlant.setOnClickListener(v -> {
            if (currentPlant != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAddEditPlant(currentPlant.getId());
            }
        });

        binding.btnDeletePlant.setOnClickListener(v -> showDeleteConfirmDialog());

        binding.btnWaterNow.setOnClickListener(v -> showCareConfirmDialog(true));
        binding.btnFertilizeNow.setOnClickListener(v -> showCareConfirmDialog(false));

        binding.btnAddJournal.setOnClickListener(v -> showAddJournalDialog());
    }

    private void loadPlantDetails() {
        if (plantId == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        plantViewModel.getPlantById(plantId).observe(getViewLifecycleOwner(), plant -> {
            if (plant == null) return;
            currentPlant = plant;

            binding.tvDetailName.setText(plant.getName());
            binding.tvDetailType.setText(plant.getType());
            binding.tvDetailNotes.setText(!TextUtils.isEmpty(plant.getNote()) ? plant.getNote() : "Chưa có ghi chú đặc biệt cho cây này.");

            binding.tvDetailWaterInterval.setText(plant.getWateringIntervalDays() + " ngày / lần");
            binding.tvDetailFertilizerInterval.setText(plant.getFertilizerIntervalDays() + " ngày / lần (" + plant.getFertilizerType() + ")");

            binding.tvDetailNextWater.setText("Kế tiếp: " + sdf.format(new Date(plant.getNextWateringAt())));
            binding.tvDetailNextFertilizer.setText("Kế tiếp: " + sdf.format(new Date(plant.getNextFertilizerAt())));

            if (plant.getImageUrl() != null && !plant.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(plant.getImageUrl())
                        .placeholder(R.drawable.ic_plant)
                        .error(R.drawable.ic_plant)
                        .into(binding.ivDetailPhoto);
            } else {
                binding.ivDetailPhoto.setImageResource(R.drawable.ic_plant);
            }
        });

        // Journal Entries
        journalViewModel.getJournalEntries(plantId).observe(getViewLifecycleOwner(), entries -> {
            if (entries != null && !entries.isEmpty()) {
                binding.tvEmptyJournal.setVisibility(View.GONE);
                binding.rvJournalEntries.setVisibility(View.VISIBLE);
                journalAdapter.setEntries(entries);
            } else {
                binding.tvEmptyJournal.setVisibility(View.VISIBLE);
                binding.rvJournalEntries.setVisibility(View.GONE);
                journalAdapter.setEntries(new ArrayList<>());
            }
        });

        // History
        careViewModel.getWateringLogs(plantId).observe(getViewLifecycleOwner(), wLogs -> {
            currentWaterLogs = wLogs != null ? wLogs : new ArrayList<>();
            mergeAndDisplayHistory();
        });

        careViewModel.getFertilizerLogs(plantId).observe(getViewLifecycleOwner(), fLogs -> {
            currentFertilizerLogs = fLogs != null ? fLogs : new ArrayList<>();
            mergeAndDisplayHistory();
        });
    }

    private void mergeAndDisplayHistory() {
        List<CareHistoryAdapter.HistoryItem> items = new ArrayList<>();
        for (WateringLog wl : currentWaterLogs) {
            items.add(new CareHistoryAdapter.HistoryItem(true, "Tưới nước", wl.getNote(), wl.getWateredAt()));
        }
        for (FertilizerLog fl : currentFertilizerLogs) {
            items.add(new CareHistoryAdapter.HistoryItem(false, "Bón phân (" + fl.getFertilizerType() + ")", fl.getNote(), fl.getFertilizedAt()));
        }

        Collections.sort(items, (o1, o2) -> Long.compare(o2.timestamp, o1.timestamp));
        historyAdapter.setItems(items);
    }

    private void showCareConfirmDialog(boolean isWater) {
        if (currentPlant == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCareConfirmBinding dialogBinding = DialogCareConfirmBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();

        if (isWater) {
            dialogBinding.tvCareDialogTitle.setText("Xác nhận đã tưới nước 💧");
            dialogBinding.tilFertilizerTypeDialog.setVisibility(View.GONE);
            dialogBinding.etCareNote.setHint("Ví dụ: Tưới 200ml nước buổi sáng");
        } else {
            dialogBinding.tvCareDialogTitle.setText("Xác nhận đã bón phân 🌿");
            dialogBinding.tilFertilizerTypeDialog.setVisibility(View.VISIBLE);
            dialogBinding.etFertilizerTypeDialog.setText(currentPlant.getFertilizerType());
            dialogBinding.etCareNote.setHint("Ví dụ: Bón 1/2 muỗng cà phê hòa tan");
        }

        dialogBinding.btnCancelCareDialog.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnConfirmCare.setOnClickListener(v -> {
            String note = dialogBinding.etCareNote.getText() != null ? dialogBinding.etCareNote.getText().toString().trim() : "";
            if (isWater) {
                careViewModel.waterPlant(currentPlant, !note.isEmpty() ? note : "Đã tưới nước");
            } else {
                String fertilizerType = dialogBinding.etFertilizerTypeDialog.getText() != null ?
                        dialogBinding.etFertilizerTypeDialog.getText().toString().trim() : "Phân hữu cơ";
                careViewModel.fertilizePlant(currentPlant, fertilizerType, !note.isEmpty() ? note : "Đã bón phân");
            }
            Toast.makeText(getContext(), "Đã lưu lịch sử chăm sóc!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showAddJournalDialog() {
        if (currentPlant == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogAddJournalBinding dialogBinding = DialogAddJournalBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();

        selectedJournalPhotoUri = null;

        ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedJournalPhotoUri = uri.toString();
                        dialogBinding.ivJournalPhotoPreview.setVisibility(View.VISIBLE);
                        Glide.with(this).load(uri).centerCrop().into(dialogBinding.ivJournalPhotoPreview);
                    }
                }
        );

        dialogBinding.btnJournalPickPhoto.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        dialogBinding.btnCancelJournal.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnSaveJournal.setOnClickListener(v -> {
            String heightStr = dialogBinding.etJournalHeight.getText() != null ? dialogBinding.etJournalHeight.getText().toString().trim() : "";
            String note = dialogBinding.etJournalNote.getText() != null ? dialogBinding.etJournalNote.getText().toString().trim() : "";

            if (TextUtils.isEmpty(note)) {
                dialogBinding.tilJournalNote.setError("Vui lòng nhập ghi chú nhật ký");
                return;
            }

            double height = 0.0;
            if (!heightStr.isEmpty()) {
                try {
                    height = Double.parseDouble(heightStr);
                } catch (Exception ignored) {}
            }

            JournalEntry entry = new JournalEntry(
                    UUID.randomUUID().toString(),
                    currentPlant.getId(),
                    System.currentTimeMillis(),
                    note,
                    height,
                    selectedJournalPhotoUri,
                    false
            );

            journalViewModel.insertJournalEntry(entry);
            Toast.makeText(getContext(), "Đã thêm nhật ký phát triển!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_plant)
                .setMessage(R.string.delete_plant_confirm)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (currentPlant != null) {
                        plantViewModel.deletePlant(currentPlant);
                        Toast.makeText(getContext(), "Đã xóa cây cảnh thành công", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
