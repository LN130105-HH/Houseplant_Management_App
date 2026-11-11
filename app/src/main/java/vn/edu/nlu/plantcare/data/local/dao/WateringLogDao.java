package vn.edu.nlu.plantcare.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import vn.edu.nlu.plantcare.data.local.entity.WateringLog;

@Dao
public interface WateringLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLog(WateringLog log);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLogs(List<WateringLog> logs);

    @Query("SELECT * FROM watering_logs WHERE plantId = :plantId ORDER BY wateredAt DESC")
    LiveData<List<WateringLog>> getLogsForPlant(String plantId);

    @Query("SELECT * FROM watering_logs WHERE plantId = :plantId ORDER BY wateredAt DESC")
    List<WateringLog> getLogsForPlantDirect(String plantId);

    @Query("SELECT COUNT(*) FROM watering_logs WHERE wateredAt >= :sinceTime")
    int countWateringsSince(long sinceTime);

    @Query("SELECT COUNT(*) FROM watering_logs")
    LiveData<Integer> getTotalWateringsCount();

    @Query("SELECT COUNT(*) FROM watering_logs")
    int getTotalWateringsCountDirect();

    @Query("SELECT * FROM watering_logs WHERE wateredAt >= :sinceTime")
    List<WateringLog> getWateringsSinceDirect(long sinceTime);

    @Query("SELECT * FROM watering_logs WHERE isSynced = 0")
    List<WateringLog> getUnsyncedLogs();
}
