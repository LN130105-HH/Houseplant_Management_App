package vn.edu.nlu.plantcare.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import vn.edu.nlu.plantcare.data.local.entity.FertilizerLog;

@Dao
public interface FertilizerLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLog(FertilizerLog log);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLogs(List<FertilizerLog> logs);

    @Query("SELECT * FROM fertilizer_logs WHERE plantId = :plantId ORDER BY fertilizedAt DESC")
    LiveData<List<FertilizerLog>> getLogsForPlant(String plantId);

    @Query("SELECT * FROM fertilizer_logs WHERE plantId = :plantId ORDER BY fertilizedAt DESC")
    List<FertilizerLog> getLogsForPlantDirect(String plantId);

    @Query("SELECT COUNT(*) FROM fertilizer_logs WHERE fertilizedAt >= :sinceTime")
    int countFertilizersSince(long sinceTime);

    @Query("SELECT COUNT(*) FROM fertilizer_logs")
    LiveData<Integer> getTotalFertilizersCount();

    @Query("SELECT COUNT(*) FROM fertilizer_logs")
    int getTotalFertilizersCountDirect();

    @Query("SELECT * FROM fertilizer_logs WHERE fertilizedAt >= :sinceTime")
    List<FertilizerLog> getFertilizersSinceDirect(long sinceTime);

    @Query("SELECT * FROM fertilizer_logs WHERE isSynced = 0")
    List<FertilizerLog> getUnsyncedLogs();
}
