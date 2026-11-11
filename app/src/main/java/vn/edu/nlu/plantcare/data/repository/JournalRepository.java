package vn.edu.nlu.plantcare.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.nlu.plantcare.data.local.AppDatabase;
import vn.edu.nlu.plantcare.data.local.dao.JournalEntryDao;
import vn.edu.nlu.plantcare.data.local.entity.JournalEntry;

public class JournalRepository {
    private final JournalEntryDao journalEntryDao;
    private FirebaseFirestore firestore;

    public JournalRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        journalEntryDao = db.journalEntryDao();
        try {
            firestore = FirebaseFirestore.getInstance();
        } catch (Exception ignored) {
        }
    }

    public LiveData<List<JournalEntry>> getJournalEntries(String plantId) {
        return journalEntryDao.getEntriesForPlant(plantId);
    }

    public void insertJournalEntry(JournalEntry entry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            journalEntryDao.insertEntry(entry);
            if (firestore != null) {
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", entry.getId());
                    map.put("plantId", entry.getPlantId());
                    map.put("date", entry.getDate());
                    map.put("note", entry.getNote());
                    map.put("heightCm", entry.getHeightCm());
                    map.put("imageUrl", entry.getImageUrl());
                    firestore.collection("journal_entries").document(entry.getId()).set(map);
                } catch (Exception ignored) {
                }
            }
        });
    }

    public void deleteJournalEntry(JournalEntry entry) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            journalEntryDao.deleteEntry(entry);
            if (firestore != null) {
                try {
                    firestore.collection("journal_entries").document(entry.getId()).delete();
                } catch (Exception ignored) {
                }
            }
        });
    }
}
