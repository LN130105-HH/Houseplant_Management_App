package vn.edu.nlu.plantcare.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.nlu.plantcare.data.local.AppDatabase;
import vn.edu.nlu.plantcare.data.local.dao.FertilizerLogDao;
import vn.edu.nlu.plantcare.data.local.dao.JournalEntryDao;
import vn.edu.nlu.plantcare.data.local.dao.PlantDao;
import vn.edu.nlu.plantcare.data.local.dao.WateringLogDao;
import vn.edu.nlu.plantcare.data.local.entity.FertilizerLog;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.data.local.entity.WateringLog;

public class StatisticsRepository {
    private final WateringLogDao wateringLogDao;
    private final FertilizerLogDao fertilizerLogDao;
    private final JournalEntryDao journalEntryDao;
    private final PlantDao plantDao;

    public static class DayCareStats {
        public String dayLabel;
        public int waterCount;
        public int fertilizerCount;

        public DayCareStats(String dayLabel, int waterCount, int fertilizerCount) {
            this.dayLabel = dayLabel;
            this.waterCount = waterCount;
            this.fertilizerCount = fertilizerCount;
        }
    }

    public static class CareOverview {
        public int totalWaterings;
        public int totalFertilizers;
        public int totalJournals;
        public String mostCaredPlantName;
        public List<DayCareStats> last7DaysStats;

        public CareOverview() {
            last7DaysStats = new ArrayList<>();
        }
    }

    public StatisticsRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        wateringLogDao = db.wateringLogDao();
        fertilizerLogDao = db.fertilizerLogDao();
        journalEntryDao = db.journalEntryDao();
        plantDao = db.plantDao();
    }

    public LiveData<Integer> getTotalWaterings() {
        return wateringLogDao.getTotalWateringsCount();
    }

    public LiveData<Integer> getTotalFertilizers() {
        return fertilizerLogDao.getTotalFertilizersCount();
    }

    public LiveData<Integer> getTotalJournals() {
        return journalEntryDao.getTotalJournalCount();
    }

    public void loadCareOverview(String userId, MutableLiveData<CareOverview> resultLiveData) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            CareOverview overview = new CareOverview();
            overview.totalWaterings = wateringLogDao.getTotalWateringsCountDirect();
            overview.totalFertilizers = fertilizerLogDao.getTotalFertilizersCountDirect();
            overview.totalJournals = journalEntryDao.getTotalJournalCountDirect();

            // Calculate last 7 days care counts
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.DAY_OF_YEAR, -6); // 7 days window

            long sevenDaysAgo = calendar.getTimeInMillis();
            List<WateringLog> wLogs = wateringLogDao.getWateringsSinceDirect(sevenDaysAgo);
            List<FertilizerLog> fLogs = fertilizerLogDao.getFertilizersSinceDirect(sevenDaysAgo);

            String[] dayNames = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
            for (int i = 0; i < 7; i++) {
                long startOfDay = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                long endOfDay = calendar.getTimeInMillis();

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                String label = dayNames[dayOfWeek];

                int wCount = 0;
                for (WateringLog wl : wLogs) {
                    if (wl.getWateredAt() >= startOfDay && wl.getWateredAt() < endOfDay) {
                        wCount++;
                    }
                }

                int fCount = 0;
                for (FertilizerLog fl : fLogs) {
                    if (fl.getFertilizedAt() >= startOfDay && fl.getFertilizedAt() < endOfDay) {
                        fCount++;
                    }
                }

                overview.last7DaysStats.add(new DayCareStats(label, wCount, fCount));
            }

            // Determine most cared plant
            List<Plant> plants = plantDao.getAllPlantsDirect(userId);
            if (plants != null && !plants.isEmpty()) {
                Map<String, Integer> plantCareCount = new HashMap<>();
                for (Plant p : plants) {
                    List<WateringLog> plW = wateringLogDao.getLogsForPlantDirect(p.getId());
                    List<FertilizerLog> plF = fertilizerLogDao.getLogsForPlantDirect(p.getId());
                    plantCareCount.put(p.getId(), (plW != null ? plW.size() : 0) + (plF != null ? plF.size() : 0));
                }

                String topPlantId = null;
                int maxCount = -1;
                for (Map.Entry<String, Integer> entry : plantCareCount.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        maxCount = entry.getValue();
                        topPlantId = entry.getKey();
                    }
                }

                if (topPlantId != null) {
                    Plant topPlant = plantDao.getPlantByIdDirect(topPlantId);
                    if (topPlant != null) {
                        overview.mostCaredPlantName = topPlant.getName();
                    }
                }
            }

            if (overview.mostCaredPlantName == null) {
                overview.mostCaredPlantName = "Chưa có dữ liệu";
            }

            resultLiveData.postValue(overview);
        });
    }
}
