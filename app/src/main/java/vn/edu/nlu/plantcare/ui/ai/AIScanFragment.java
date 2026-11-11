package vn.edu.nlu.plantcare.ui.ai;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.data.local.entity.AIResult;
import vn.edu.nlu.plantcare.databinding.FragmentAiScanBinding;
import vn.edu.nlu.plantcare.ui.MainActivity;
import vn.edu.nlu.plantcare.ui.plant.AddEditPlantFragment;

public class AIScanFragment extends Fragment {

    private FragmentAiScanBinding binding;
    private AIViewModel aiViewModel;

    private Uri photoUri;
    private File photoFile;
    private String currentBase64Image;
    private String currentMode = "IDENTIFY";
    private AIResult lastResult;

    // Gallery launcher
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    processImageUri(uri);
                }
            });

    // Camera launcher
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (Boolean.TRUE.equals(success) && photoUri != null) {
                    processImageUri(photoUri);
                }
            });

    // Camera permission launcher
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (Boolean.TRUE.equals(isGranted)) {
                    launchCamera();
                } else {
                    Toast.makeText(getContext(), "Cần quyền camera để chụp ảnh cây", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAiScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aiViewModel = new ViewModelProvider(this).get(AIViewModel.class);

        setupModeToggle();
        setupImagePickers();
        observeViewModel();

        binding.btnCreatePlantFromResult.setOnClickListener(v -> {
            if (lastResult != null && getActivity() instanceof MainActivity) {
                // Pre-fill plant name and navigate to add plant
                AddEditPlantFragment fragment = AddEditPlantFragment.newInstance(null);
                ((MainActivity) getActivity()).loadFragment(fragment, true);
            }
        });
    }

    private void setupModeToggle() {
        binding.toggleMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnModeIdentify) {
                    currentMode = "IDENTIFY";
                    binding.btnAnalyze.setText("Nhận diện loại cây");
                } else if (checkedId == R.id.btnModeHealthCheck) {
                    currentMode = "HEALTH_CHECK";
                    binding.btnAnalyze.setText("Kiểm tra bệnh cây");
                }
            }
        });
    }

    private void setupImagePickers() {
        binding.btnGalleryPick.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        binding.btnCameraCapture.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        binding.btnAnalyze.setOnClickListener(v -> {
            if (currentBase64Image == null) {
                Toast.makeText(getContext(), "Vui lòng chụp hoặc chọn ảnh cây trước", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.layoutAiResult.setVisibility(View.GONE);
            aiViewModel.analyzeImage(null, currentBase64Image, currentMode);
        });
    }

    private void launchCamera() {
        try {
            File storageDir = new File(requireContext().getCacheDir(), "images");
            if (!storageDir.exists()) storageDir.mkdirs();
            photoFile = File.createTempFile("plant_photo_", ".jpg", storageDir);
            photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile
            );
            cameraLauncher.launch(photoUri);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Không thể khởi động camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void processImageUri(Uri uri) {
        try {
            binding.layoutPhotoPlaceholder.setVisibility(View.GONE);
            Glide.with(this).load(uri).centerCrop().into(binding.ivAiImagePreview);

            // Compress and convert to Base64 (efficient, avoid OOM & save bandwidth)
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            if (is != null) is.close();

            if (bitmap != null) {
                int maxDim = 800;
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                if (width > maxDim || height > maxDim) {
                    float ratio = Math.min((float) maxDim / width, (float) maxDim / height);
                    bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(width * ratio), Math.round(height * ratio), true);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
                byte[] bytes = outputStream.toByteArray();
                currentBase64Image = Base64.encodeToString(bytes, Base64.NO_WRAP);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void observeViewModel() {
        aiViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (Boolean.TRUE.equals(isLoading)) {
                binding.layoutAiLoading.setVisibility(View.VISIBLE);
                binding.btnAnalyze.setEnabled(false);
            } else {
                binding.layoutAiLoading.setVisibility(View.GONE);
                binding.btnAnalyze.setEnabled(true);
            }
        });

        aiViewModel.getAnalysisResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            lastResult = result;

            binding.layoutAiResult.setVisibility(View.VISIBLE);
            binding.tvResultTitle.setText(result.getPlantName());
            binding.tvResultDescription.setText(result.getDescription());

            if ("IDENTIFY".equalsIgnoreCase(result.getResultType())) {
                binding.layoutCareSpecs.setVisibility(View.VISIBLE);
                binding.layoutHealthTreatment.setVisibility(View.GONE);

                binding.tvSpecLight.setText("Ánh sáng: " + result.getLightRequirement());
                binding.tvSpecWater.setText("Tưới nước: " + result.getWaterRequirement());
                binding.tvSpecTemp.setText("Nhiệt độ: " + result.getTemperatureRequirement());
                binding.btnCreatePlantFromResult.setVisibility(View.VISIBLE);
            } else {
                binding.layoutCareSpecs.setVisibility(View.GONE);
                binding.layoutHealthTreatment.setVisibility(View.VISIBLE);

                binding.tvResultTitle.setText("Chẩn đoán: " + result.getHealthStatus());
                binding.tvTreatmentAdvice.setText(result.getTreatmentAdvice() != null ? result.getTreatmentAdvice() : result.getCareAdvice());
                binding.btnCreatePlantFromResult.setVisibility(View.GONE);
            }
        });

        aiViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
