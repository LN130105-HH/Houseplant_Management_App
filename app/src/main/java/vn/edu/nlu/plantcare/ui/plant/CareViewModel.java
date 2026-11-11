package vn.edu.nlu.plantcare.ui.plant;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import vn.edu.nlu.plantcare.data.local.entity.FertilizerLog;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.data.local.entity.WateringLog;
import vn.edu.nlu.plantcare.data.repository.CareRepository;

public class CareViewModel extends AndroidViewModel {
    private final CareRepository careRepository;

    public CareViewModel(@NonNull Application application) {
        super(application);
        careRepository = new CareRepository(application);
    }

    public LiveData<List<WateringLog>> getWateringLogs(String plantId) {
        return careRepository.getWateringLogs(plantId);
    }

    public LiveData<List<FertilizerLog>> getFertilizerLogs(String plantId) {
        return careRepository.getFertilizerLogs(plantId);
    }

    public void waterPlant(Plant plant, String note) {
        careRepository.waterPlant(plant, note);
    }

    public void fertilizePlant(Plant plant, String fertilizerType, String note) {
        careRepository.fertilizePlant(plant, fertilizerType, note);
    }
}
