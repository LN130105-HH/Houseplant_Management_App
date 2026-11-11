package vn.edu.nlu.plantcare.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import vn.edu.nlu.plantcare.data.local.AppDatabase;
import vn.edu.nlu.plantcare.data.local.dao.FertilizerLogDao;
import vn.edu.nlu.plantcare.data.local.dao.PlantDao;
import vn.edu.nlu.plantcare.data.local.dao.WateringLogDao;
import vn.edu.nlu.plantcare.data.local.entity.FertilizerLog;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.data.local.entity.WateringLog;

public class CareRepository {
    private final PlantDao plantDao;
    private final WateringLogDao wateringLogDao;
    private final FertilizerLogDao fertilizerLogDao;
    private FirebaseFirestore firestore;

    public CareRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        plantDao = db.plantDao();
        wateringLogDao = db.wateringLogDao();
        fertilizerLogDao = db.fertilizerLogDao();
        try {
            firestore = FirebaseFirestore.getInstance();
        } catch (Exception ignored) {
        }
    }

    public LiveData<List<WateringLog>> getWateringLogs(String plantId) {
        return wateringLogDao.getLogsForPlant(plantId);
    }

    public LiveData<List<FertilizerLog>> getFertilizerLogs(String plantId) {
        return fertilizerLogDao.getLogsForPlant(plantId);
    }

    public void waterPlant(Plant plant, String note) {
        long now = System.currentTimeMillis();
        plant.setLastWateredAt(now);
        plant.setNextWateringAt(now + (plant.getWateringIntervalDays() * 86400000L));

        WateringLog log = new WateringLog(
                UUID.randomUUID().toString(),
                plant.getId(),
                now,
                note != null ? note : "Đã tưới nước đầy đủ",
                false
        );

        AppDatabase.databaseWriteExecutor.execute(() -> {
            plantDao.updatePlant(plant);
            wateringLogDao.insertLog(log);
            syncCareToFirestore(plant, log, null);
        });
    }

    public void fertilizePlant(Plant plant, String fertilizerType, String note) {
        long now = System.currentTimeMillis();
        plant.setLastFertilizedAt(now);
        plant.setNextFertilizerAt(now + (plant.getFertilizerIntervalDays() * 86400000L));
        if (fertilizerType != null && !fertilizerType.trim().isEmpty()) {
            plant.setFertilizerType(fertilizerType);
        }

        FertilizerLog log = new FertilizerLog(
                UUID.randomUUID().toString(),
                plant.getId(),
                plant.getFertilizerType() != null ? plant.getFertilizerType() : "Phân bón lá",
                now,
                note != null ? note : "Đã bón phân đúng liều lượng",
                false
        );

        AppDatabase.databaseWriteExecutor.execute(() -> {
            plantDao.updatePlant(plant);
            fertilizerLogDao.insertLog(log);
            syncCareToFirestore(plant, null, log);
        });
    }

    private void syncCareToFirestore(Plant plant, WateringLog wLog, FertilizerLog fLog) {
        if (firestore == null) return;
        try {
            // Update plant next care timestamps in firestore
            Map<String, Object> plantMap = new HashMap<>();
            plantMap.put("lastWateredAt", plant.getLastWateredAt());
            plantMap.put("nextWateringAt", plant.getNextWateringAt());
            plantMap.put("lastFertilizedAt", plant.getLastFertilizedAt());
            plantMap.put("nextFertilizerAt", plant.getNextFertilizerAt());
            firestore.collection("plants").document(plant.getId()).update(plantMap);

            if (wLog != null) {
                Map<String, Object> wMap = new HashMap<>();
                wMap.put("id", wLog.getId());
                wMap.put("plantId", wLog.getPlantId());
                wMap.put("wateredAt", wLog.getWateredAt());
                wMap.put("note", wLog.getNote());
                firestore.collection("watering_logs").document(wLog.getId()).set(wMap);
            }

            if (fLog != null) {
                Map<String, Object> fMap = new HashMap<>();
                fMap.put("id", fLog.getId());
                fMap.put("plantId", fLog.getPlantId());
                fMap.put("fertilizerType", fLog.getFertilizerType());
                fMap.put("fertilizedAt", fLog.getFertilizedAt());
                fMap.put("note", fLog.getNote());
                firestore.collection("fertilizer_logs").document(fLog.getId()).set(fMap);
            }
        } catch (Exception ignored) {
        }
    }
}
