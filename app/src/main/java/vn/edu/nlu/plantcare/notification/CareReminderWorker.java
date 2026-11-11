package vn.edu.nlu.plantcare.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import vn.edu.nlu.plantcare.PlantCareApplication;
import vn.edu.nlu.plantcare.R;
import vn.edu.nlu.plantcare.data.local.AppDatabase;
import vn.edu.nlu.plantcare.data.local.dao.PlantDao;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.ui.MainActivity;

public class CareReminderWorker extends Worker {

    public CareReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        AppDatabase db = AppDatabase.getDatabase(context);
        PlantDao plantDao = db.plantDao();

        long now = System.currentTimeMillis();
        // Check plants due or overdue for watering or fertilizing
        List<Plant> plantsNeedingCare = plantDao.getAllPlantsNeedingCare(now);

        if (plantsNeedingCare != null && !plantsNeedingCare.isEmpty()) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            int notificationId = 1000;
            for (Plant plant : plantsNeedingCare) {
                boolean needsWater = plant.getNextWateringAt() <= now;
                boolean needsFertilizer = plant.getNextFertilizerAt() <= now;

                if (needsWater) {
                    sendWaterNotification(context, notificationManager, plant, notificationId++);
                }
                if (needsFertilizer) {
                    sendFertilizerNotification(context, notificationManager, plant, notificationId++);
                }
            }
        }

        return Result.success();
    }

    private void sendWaterNotification(Context context, NotificationManager manager, Plant plant, int notificationId) {
        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        openIntent.putExtra("EXTRA_PLANT_ID", plant.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, notificationId, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Action button "Đã tưới"
        Intent actionIntent = new Intent(context, CareActionReceiver.class);
        actionIntent.setAction(CareActionReceiver.ACTION_WATER_CONFIRM);
        actionIntent.putExtra("EXTRA_PLANT_ID", plant.getId());
        actionIntent.putExtra("EXTRA_NOTIFICATION_ID", notificationId);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(
                context, notificationId + 500, actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PlantCareApplication.CHANNEL_WATERING_ID)
                .setSmallIcon(R.drawable.ic_water_drop)
                .setContentTitle("Đã đến giờ tưới nước: " + plant.getName() + " 💧")
                .setContentText("Cây của bạn đang khát nước theo chu kỳ " + plant.getWateringIntervalDays() + " ngày/lần.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_check, "Đã tưới xong", actionPendingIntent);

        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }

    private void sendFertilizerNotification(Context context, NotificationManager manager, Plant plant, int notificationId) {
        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        openIntent.putExtra("EXTRA_PLANT_ID", plant.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, notificationId, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent actionIntent = new Intent(context, CareActionReceiver.class);
        actionIntent.setAction(CareActionReceiver.ACTION_FERTILIZE_CONFIRM);
        actionIntent.putExtra("EXTRA_PLANT_ID", plant.getId());
        actionIntent.putExtra("EXTRA_NOTIFICATION_ID", notificationId);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(
                context, notificationId + 500, actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PlantCareApplication.CHANNEL_FERTILIZER_ID)
                .setSmallIcon(R.drawable.ic_fertilizer)
                .setContentTitle("Lịch bón phân cho: " + plant.getName() + " 🌿")
                .setContentText("Bổ sung dinh dưỡng (" + (plant.getFertilizerType() != null ? plant.getFertilizerType() : "phân bón") + ") cho cây phát triển.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_check, "Đã bón phân", actionPendingIntent);

        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }
}
