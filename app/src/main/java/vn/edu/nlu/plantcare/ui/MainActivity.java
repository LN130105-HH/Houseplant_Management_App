package vn.edu.nlu.plantcare.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.databinding.ActivityMainBinding;
import vn.edu.nlu.plantcare.ui.ai.AIScanFragment;
import vn.edu.nlu.plantcare.ui.home.HomeFragment;
import vn.edu.nlu.plantcare.ui.plant.AddEditPlantFragment;
import vn.edu.nlu.plantcare.ui.plant.PlantDetailFragment;
import vn.edu.nlu.plantcare.ui.plant.PlantsFragment;
import vn.edu.nlu.plantcare.ui.profile.ProfileFragment;
import vn.edu.nlu.plantcare.ui.statistics.StatisticsFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Notification permission result handled
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkNotificationPermission();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment targetFragment = null;

            if (itemId == R.id.nav_home) {
                targetFragment = new HomeFragment();
            } else if (itemId == R.id.nav_plants) {
                targetFragment = new PlantsFragment();
            } else if (itemId == R.id.nav_ai_scan) {
                targetFragment = new AIScanFragment();
            } else if (itemId == R.id.nav_statistics) {
                targetFragment = new StatisticsFragment();
            } else if (itemId == R.id.nav_profile) {
                targetFragment = new ProfileFragment();
            }

            if (targetFragment != null) {
                loadFragment(targetFragment, false);
                return true;
            }
            return false;
        });

        // Initial fragment
        if (savedInstanceState == null) {
            String initialPlantId = getIntent().getStringExtra("EXTRA_PLANT_ID");
            if (initialPlantId != null) {
                navigateToPlantDetail(initialPlantId);
            } else {
                loadFragment(new HomeFragment(), false);
            }
        }
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction transaction = fm.beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void navigateToPlantDetail(String plantId) {
        PlantDetailFragment fragment = PlantDetailFragment.newInstance(plantId);
        loadFragment(fragment, true);
    }

    public void navigateToAddEditPlant(String plantId) {
        AddEditPlantFragment fragment = AddEditPlantFragment.newInstance(plantId);
        loadFragment(fragment, true);
    }

    public void navigateToPlants() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_plants);
    }

    public void navigateToAIScan() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_ai_scan);
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
