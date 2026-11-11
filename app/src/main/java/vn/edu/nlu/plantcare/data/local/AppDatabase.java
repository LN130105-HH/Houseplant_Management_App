package vn.edu.nlu.plantcare.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.edu.nlu.plantcare.data.local.dao.AIResultDao;
import vn.edu.nlu.plantcare.data.local.dao.FertilizerLogDao;
import vn.edu.nlu.plantcare.data.local.dao.JournalEntryDao;
import vn.edu.nlu.plantcare.data.local.dao.PlantDao;
import vn.edu.nlu.plantcare.data.local.dao.UserDao;
import vn.edu.nlu.plantcare.data.local.dao.WateringLogDao;
import vn.edu.nlu.plantcare.data.local.entity.AIResult;
import vn.edu.nlu.plantcare.data.local.entity.FertilizerLog;
import vn.edu.nlu.plantcare.data.local.entity.JournalEntry;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.data.local.entity.User;
import vn.edu.nlu.plantcare.data.local.entity.WateringLog;

@Database(
    entities = {
        User.class,
        Plant.class,
        WateringLog.class,
        FertilizerLog.class,
        JournalEntry.class,
        AIResult.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract UserDao userDao();
    public abstract PlantDao plantDao();
    public abstract WateringLogDao wateringLogDao();
    public abstract FertilizerLogDao fertilizerLogDao();
    public abstract JournalEntryDao journalEntryDao();
    public abstract AIResultDao aiResultDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "plantcare_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
