package vn.edu.nlu.plantcare.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.nlu.plantcare.data.local.AppDatabase;
import vn.edu.nlu.plantcare.data.local.dao.PlantDao;
import vn.edu.nlu.plantcare.data.local.entity.Plant;

public class PlantRepository {
    private final PlantDao plantDao;
    private FirebaseFirestore firestore;

    public PlantRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        plantDao = db.plantDao();
        try {
            firestore = FirebaseFirestore.getInstance();
        } catch (Exception ignored) {
        }
    }

    public LiveData<List<Plant>> getAllPlants(String userId) {
        return plantDao.getAllPlants(userId);
    }

    public LiveData<Plant> getPlantById(String id) {
        return plantDao.getPlantById(id);
    }

    public LiveData<List<Plant>> searchPlants(String userId, String query) {
        return plantDao.searchPlants(userId, query);
    }

    public LiveData<List<Plant>> getPlantsNeedingWater(String userId, long currentTime) {
        return plantDao.getPlantsNeedingWater(userId, currentTime);
    }

    public LiveData<List<Plant>> getPlantsNeedingFertilizer(String userId, long currentTime) {
        return plantDao.getPlantsNeedingFertilizer(userId, currentTime);
    }

    public LiveData<Integer> getPlantCount(String userId) {
        return plantDao.getPlantCount(userId);
    }

    public void insertPlant(Plant plant) {
        // Calculate initial next care dates if not set
        long now = System.currentTimeMillis();
        if (plant.getLastWateredAt() <= 0) {
            plant.setLastWateredAt(now);
        }
        if (plant.getNextWateringAt() <= 0) {
            plant.setNextWateringAt(plant.getLastWateredAt() + (plant.getWateringIntervalDays() * 86400000L));
        }

        if (plant.getLastFertilizedAt() <= 0) {
            plant.setLastFertilizedAt(now);
        }
        if (plant.getNextFertilizerAt() <= 0) {
            plant.setNextFertilizerAt(plant.getLastFertilizedAt() + (plant.getFertilizerIntervalDays() * 86400000L));
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            plantDao.insertPlant(plant);
            syncPlantToFirestore(plant);
        });
    }

    public void updatePlant(Plant plant) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            plantDao.updatePlant(plant);
            syncPlantToFirestore(plant);
        });
    }

    public void deletePlant(Plant plant) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            plantDao.deletePlant(plant);
            if (firestore != null) {
                try {
                    firestore.collection("plants").document(plant.getId()).delete();
                } catch (Exception ignored) {
                }
            }
        });
    }

    private void syncPlantToFirestore(Plant plant) {
        if (firestore == null) return;
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", plant.getId());
            map.put("userId", plant.getUserId());
            map.put("name", plant.getName());
            map.put("type", plant.getType());
            map.put("plantedDate", plant.getPlantedDate());
            map.put("imageUrl", plant.getImageUrl());
            map.put("note", plant.getNote());
            map.put("wateringIntervalDays", plant.getWateringIntervalDays());
            map.put("fertilizerIntervalDays", plant.getFertilizerIntervalDays());
            map.put("fertilizerType", plant.getFertilizerType());
            map.put("lastWateredAt", plant.getLastWateredAt());
            map.put("lastFertilizedAt", plant.getLastFertilizedAt());
            map.put("nextWateringAt", plant.getNextWateringAt());
            map.put("nextFertilizerAt", plant.getNextFertilizerAt());

            firestore.collection("plants").document(plant.getId()).set(map)
                .addOnSuccessListener(aVoid -> {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        plant.setSynced(true);
                        plantDao.updatePlant(plant);
                    });
                });
        } catch (Exception ignored) {
        }
    }

    public void syncFromFirestore(String userId, Runnable onComplete) {
        if (firestore == null) {
            if (onComplete != null) onComplete.run();
            return;
        }
        try {
            firestore.collection("plants")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String id = doc.getString("id");
                            if (id == null) id = doc.getId();
                            String name = doc.getString("name");
                            String type = doc.getString("type");
                            String plantedDate = doc.getString("plantedDate");
                            String imageUrl = doc.getString("imageUrl");
                            String note = doc.getString("note");
                            Long wInterval = doc.getLong("wateringIntervalDays");
                            Long fInterval = doc.getLong("fertilizerIntervalDays");
                            String fType = doc.getString("fertilizerType");
                            Long lastW = doc.getLong("lastWateredAt");
                            Long lastF = doc.getLong("lastFertilizedAt");
                            Long nextW = doc.getLong("nextWateringAt");
                            Long nextF = doc.getLong("nextFertilizerAt");

                            Plant plant = new Plant(
                                id,
                                userId,
                                name != null ? name : "Cây cảnh",
                                type != null ? type : "Nội thất",
                                plantedDate != null ? plantedDate : "",
                                imageUrl != null ? imageUrl : "",
                                note != null ? note : "",
                                wInterval != null ? wInterval.intValue() : 3,
                                fInterval != null ? fInterval.intValue() : 30,
                                fType != null ? fType : "NPK",
                                lastW != null ? lastW : System.currentTimeMillis(),
                                lastF != null ? lastF : System.currentTimeMillis(),
                                nextW != null ? nextW : System.currentTimeMillis() + 86400000L * 3,
                                nextF != null ? nextF : System.currentTimeMillis() + 86400000L * 30,
                                true
                            );
                            plantDao.insertPlant(plant);
                        }
                        if (onComplete != null) onComplete.run();
                    });
                })
                .addOnFailureListener(e -> {
                    if (onComplete != null) onComplete.run();
                });
        } catch (Exception e) {
            if (onComplete != null) onComplete.run();
        }
    }

    public void seedInitialDataIfEmpty(String userId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Plant> existing = plantDao.getAllPlantsDirect(userId);
            if (existing == null || existing.isEmpty()) {
                long now = System.currentTimeMillis();
                long oneDay = 86400000L;

                // Plant 1: Needs water today
                Plant p1 = new Plant(
                    "sample_plant_1",
                    userId,
                    "Cây Lưỡi Hổ",
                    "Sansevieria trifasciata",
                    "01/10/2026",
                    "",
                    "Đặt tại góc phòng khách, lọc không khí tốt, tưới vừa phải.",
                    5,
                    30,
                    "Phân hữu cơ NPK",
                    now - (5 * oneDay),
                    now - (10 * oneDay),
                    now - 1000, // Due today!
                    now + (20 * oneDay),
                    false
                );

                // Plant 2: Due in 2 days
                Plant p2 = new Plant(
                    "sample_plant_2",
                    userId,
                    "Cây Kim Tiền",
                    "Zamioculcas zamiifolia",
                    "15/10/2026",
                    "",
                    "Tượng trưng cho may mắn tài lộc, ưa bóng râm mát.",
                    7,
                    30,
                    "Phân bón lá",
                    now - (5 * oneDay),
                    now - (25 * oneDay),
                    now + (2 * oneDay),
                    now + (5 * oneDay),
                    false
                );

                // Plant 3: Due for fertilizer today
                Plant p3 = new Plant(
                    "sample_plant_3",
                    userId,
                    "Cây Trầu Bà Lỗ",
                    "Monstera adansonii",
                    "20/10/2026",
                    "",
                    "Cây thân leo lá xẻ nghệ thuật, độ ẩm cao.",
                    3,
                    15,
                    "Phân trùn quế",
                    now - oneDay,
                    now - (16 * oneDay),
                    now + (2 * oneDay),
                    now - 1000, // Fertilizer due today!
                    false
                );

                plantDao.insertPlant(p1);
                plantDao.insertPlant(p2);
                plantDao.insertPlant(p3);
            }
        });
    }
}
