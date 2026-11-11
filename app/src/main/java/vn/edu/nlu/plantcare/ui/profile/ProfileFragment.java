package vn.edu.nlu.plantcare.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.data.repository.AIRepository;
import vn.edu.nlu.plantcare.databinding.FragmentProfileBinding;
import vn.edu.nlu.plantcare.notification.ReminderManager;
import vn.edu.nlu.plantcare.ui.auth.AuthViewModel;
import vn.edu.nlu.plantcare.ui.auth.LoginActivity;
import vn.edu.nlu.plantcare.ui.plant.PlantViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AuthViewModel authViewModel;
    private PlantViewModel plantViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        plantViewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);

        binding.tvProfileName.setText(authViewModel.getCurrentDisplayName());
        binding.tvProfileEmail.setText(authViewModel.getCurrentUserEmail());

        binding.etApiKey.setText(AIRepository.getApiKey());

        binding.btnSaveApiKey.setOnClickListener(v -> {
            String key = binding.etApiKey.getText() != null ? binding.etApiKey.getText().toString().trim() : "";
            if (!key.isEmpty()) {
                AIRepository.setApiKey(key);
                Toast.makeText(getContext(), "Đã lưu Gemini API Key mới!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập API Key", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSyncNow.setOnClickListener(v -> {
            binding.pbSync.setVisibility(View.VISIBLE);
            binding.btnSyncNow.setEnabled(false);

            String userId = authViewModel.getCurrentUserId();
            plantViewModel.syncFromFirestore(userId, () -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.pbSync.setVisibility(View.GONE);
                        binding.btnSyncNow.setEnabled(true);
                        binding.tvSyncBadge.setText("Đã đồng bộ");
                        binding.tvSyncBadge.setBackgroundResource(R.drawable.bg_badge_success);
                        Toast.makeText(getContext(), R.string.sync_success, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        binding.btnTestNotification.setOnClickListener(v -> {
            ReminderManager.triggerImmediateCareCheck(requireContext());
            Toast.makeText(getContext(), "Đang gửi thông báo nhắc việc nếu có cây cần chăm sóc...", Toast.LENGTH_SHORT).show();
        });

        binding.btnLogout.setOnClickListener(v -> {
            authViewModel.logout();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            if (getActivity() != null) {
                getActivity().finishAffinity();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
