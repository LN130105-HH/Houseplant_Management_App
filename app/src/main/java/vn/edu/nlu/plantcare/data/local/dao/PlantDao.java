package vn.edu.nlu.plantcare.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import vn.edu.nlu.plantcare.data.local.entity.Plant;

@Dao
public interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlant(Plant plant);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlants(List<Plant> plants);

    @Update
    void updatePlant(Plant plant);

    @Delete
    void deletePlant(Plant plant);

    @Query("DELETE FROM plants WHERE id = :id")
    void deletePlantById(String id);

    @Query("SELECT * FROM plants WHERE userId = :userId ORDER BY name ASC")
    LiveData<List<Plant>> getAllPlants(String userId);

    @Query("SELECT * FROM plants WHERE userId = :userId ORDER BY name ASC")
    List<Plant> getAllPlantsDirect(String userId);

    @Query("SELECT * FROM plants WHERE id = :id LIMIT 1")
    LiveData<Plant> getPlantById(String id);

    @Query("SELECT * FROM plants WHERE id = :id LIMIT 1")
    Plant getPlantByIdDirect(String id);

    @Query("SELECT * FROM plants WHERE userId = :userId AND (name LIKE '%' || :query || '%' OR type LIKE '%' || :query || '%') ORDER BY name ASC")
    LiveData<List<Plant>> searchPlants(String userId, String query);

    @Query("SELECT * FROM plants WHERE userId = :userId AND nextWateringAt <= :time ORDER BY nextWateringAt ASC")
    LiveData<List<Plant>> getPlantsNeedingWater(String userId, long time);

    @Query("SELECT * FROM plants WHERE userId = :userId AND nextFertilizerAt <= :time ORDER BY nextFertilizerAt ASC")
    LiveData<List<Plant>> getPlantsNeedingFertilizer(String userId, long time);

    @Query("SELECT * FROM plants WHERE nextWateringAt <= :time OR nextFertilizerAt <= :time")
    List<Plant> getAllPlantsNeedingCare(long time);

    @Query("SELECT * FROM plants WHERE isSynced = 0")
    List<Plant> getUnsyncedPlants();

    @Query("SELECT COUNT(*) FROM plants WHERE userId = :userId")
    LiveData<Integer> getPlantCount(String userId);
}
