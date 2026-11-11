package vn.edu.nlu.plantcare.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import vn.edu.nlu.plantcare.data.local.entity.AIResult;

@Dao
public interface AIResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertResult(AIResult result);

    @Query("SELECT * FROM ai_results ORDER BY createdAt DESC")
    LiveData<List<AIResult>> getAllResults();

    @Query("SELECT * FROM ai_results WHERE id = :id LIMIT 1")
    LiveData<AIResult> getResultById(String id);

    @Query("SELECT * FROM ai_results WHERE plantId = :plantId ORDER BY createdAt DESC")
    LiveData<List<AIResult>> getResultsForPlant(String plantId);
}
