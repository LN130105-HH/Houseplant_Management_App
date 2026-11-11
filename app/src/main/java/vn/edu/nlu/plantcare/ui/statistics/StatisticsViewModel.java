package vn.edu.nlu.plantcare.ui.statistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import vn.edu.nlu.plantcare.data.repository.StatisticsRepository;

public class StatisticsViewModel extends AndroidViewModel {
    private final StatisticsRepository statisticsRepository;
    private final MutableLiveData<StatisticsRepository.CareOverview> careOverview = new MutableLiveData<>();

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        statisticsRepository = new StatisticsRepository(application);
    }

    public LiveData<StatisticsRepository.CareOverview> getCareOverview() {
        return careOverview;
    }

    public LiveData<Integer> getTotalWaterings() {
        return statisticsRepository.getTotalWaterings();
    }

    public LiveData<Integer> getTotalFertilizers() {
        return statisticsRepository.getTotalFertilizers();
    }

    public LiveData<Integer> getTotalJournals() {
        return statisticsRepository.getTotalJournals();
    }

    public void loadStatistics(String userId) {
        statisticsRepository.loadCareOverview(userId, careOverview);
    }
}
