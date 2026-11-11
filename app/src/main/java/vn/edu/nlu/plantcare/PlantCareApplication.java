package vn.edu.nlu.plantcare;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import vn.edu.nlu.plantcare.notification.ReminderManager;

public class PlantCareApplication extends Application {

    public static final String CHANNEL_WATERING_ID = "plantcare_watering_channel";
    public static final String CHANNEL_FERTILIZER_ID = "plantcare_fertilizer_channel";
    public static final String CHANNEL_GENERAL_ID = "plantcare_general_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        ReminderManager.schedulePeriodicCareReminder(this);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager == null) return;

            // Watering Channel
            NotificationChannel wateringChannel = new NotificationChannel(
                    CHANNEL_WATERING_ID,
                    "Lịch nhắc tưới nước",
                    NotificationManager.IMPORTANCE_HIGH
            );
            wateringChannel.setDescription("Thông báo nhắc nhở khi đến lịch tưới nước cho cây cảnh");
            wateringChannel.enableVibration(true);
            manager.createNotificationChannel(wateringChannel);

            // Fertilizer Channel
            NotificationChannel fertilizerChannel = new NotificationChannel(
                    CHANNEL_FERTILIZER_ID,
                    "Lịch nhắc bón phân",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            fertilizerChannel.setDescription("Thông báo nhắc nhở khi đến lịch bón phân định kỳ cho cây");
            fertilizerChannel.enableVibration(true);
            manager.createNotificationChannel(fertilizerChannel);

            // General Channel
            NotificationChannel generalChannel = new NotificationChannel(
                    CHANNEL_GENERAL_ID,
                    "Thông báo chung",
                    NotificationManager.IMPORTANCE_LOW
            );
            generalChannel.setDescription("Thông báo cập nhật hệ thống và nhật ký cây cảnh");
            manager.createNotificationChannel(generalChannel);
        }
    }
}
