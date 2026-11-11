package vn.edu.nlu.plantcare.ui.journal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import vn.edu.nlu.plantcare.data.local.entity.JournalEntry;
import vn.edu.nlu.plantcare.data.repository.JournalRepository;

public class JournalViewModel extends AndroidViewModel {
    private final JournalRepository journalRepository;

    public JournalViewModel(@NonNull Application application) {
        super(application);
        journalRepository = new JournalRepository(application);
    }

    public LiveData<List<JournalEntry>> getJournalEntries(String plantId) {
        return journalRepository.getJournalEntries(plantId);
    }

    public void insertJournalEntry(JournalEntry entry) {
        journalRepository.insertJournalEntry(entry);
    }

    public void deleteJournalEntry(JournalEntry entry) {
        journalRepository.deleteJournalEntry(entry);
    }
}
