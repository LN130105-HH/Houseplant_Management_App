package vn.edu.nlu.plantcare.notification;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class ReminderManager {
    private static final String UNIQUE_PERIODIC_WORK = "PLANT_CARE_PERIODIC_REMINDER";

    public static void schedulePeriodicCareReminder(Context context) {
        PeriodicWorkRequest careRequest =
                new PeriodicWorkRequest.Builder(CareReminderWorker.class, 6, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_PERIODIC_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                careRequest
        );
    }

    public static void triggerImmediateCareCheck(Context context) {
        OneTimeWorkRequest immediateRequest =
                new OneTimeWorkRequest.Builder(CareReminderWorker.class)
                        .build();

        WorkManager.getInstance(context).enqueue(immediateRequest);
    }
}
