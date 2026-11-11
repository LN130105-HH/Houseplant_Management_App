package vn.edu.nlu.plantcare.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import vn.edu.nlu.plantcare.data.local.entity.JournalEntry;

@Dao
public interface JournalEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntry(JournalEntry entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntries(List<JournalEntry> entries);

    @Update
    void updateEntry(JournalEntry entry);

    @Delete
    void deleteEntry(JournalEntry entry);

    @Query("DELETE FROM journal_entries WHERE id = :id")
    void deleteEntryById(String id);

    @Query("SELECT * FROM journal_entries WHERE plantId = :plantId ORDER BY date DESC")
    LiveData<List<JournalEntry>> getEntriesForPlant(String plantId);

    @Query("SELECT * FROM journal_entries WHERE plantId = :plantId ORDER BY date DESC")
    List<JournalEntry> getEntriesForPlantDirect(String plantId);

    @Query("SELECT COUNT(*) FROM journal_entries")
    LiveData<Integer> getTotalJournalCount();

    @Query("SELECT COUNT(*) FROM journal_entries")
    int getTotalJournalCountDirect();

    @Query("SELECT * FROM journal_entries WHERE isSynced = 0")
    List<JournalEntry> getUnsyncedEntries();
}
