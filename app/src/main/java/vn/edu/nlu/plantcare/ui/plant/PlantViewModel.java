package vn.edu.nlu.plantcare.ui.plant;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.data.repository.PlantRepository;

public class PlantViewModel extends AndroidViewModel {
    private final PlantRepository plantRepository;

    public PlantViewModel(@NonNull Application application) {
        super(application);
        plantRepository = new PlantRepository(application);
    }

    public LiveData<List<Plant>> getAllPlants(String userId) {
        return plantRepository.getAllPlants(userId);
    }

    public LiveData<Plant> getPlantById(String id) {
        return plantRepository.getPlantById(id);
    }

    public LiveData<List<Plant>> searchPlants(String userId, String query) {
        return plantRepository.searchPlants(userId, query);
    }

    public LiveData<List<Plant>> getPlantsNeedingWater(String userId, long currentTime) {
        return plantRepository.getPlantsNeedingWater(userId, currentTime);
    }

    public LiveData<List<Plant>> getPlantsNeedingFertilizer(String userId, long currentTime) {
        return plantRepository.getPlantsNeedingFertilizer(userId, currentTime);
    }

    public LiveData<Integer> getPlantCount(String userId) {
        return plantRepository.getPlantCount(userId);
    }

    public void insertPlant(Plant plant) {
        plantRepository.insertPlant(plant);
    }

    public void updatePlant(Plant plant) {
        plantRepository.updatePlant(plant);
    }

    public void deletePlant(Plant plant) {
        plantRepository.deletePlant(plant);
    }

    public void syncFromFirestore(String userId, Runnable onComplete) {
        plantRepository.syncFromFirestore(userId, onComplete);
    }

    public void seedInitialDataIfEmpty(String userId) {
        plantRepository.seedInitialDataIfEmpty(userId);
    }
}
