package vn.edu.nlu.plantcare.notification;

import android.app.Application;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import vn.edu.nlu.plantcare.data.local.AppDatabase;
import vn.edu.nlu.plantcare.data.local.dao.PlantDao;
import vn.edu.nlu.plantcare.data.local.entity.Plant;
import vn.edu.nlu.plantcare.data.repository.CareRepository;

public class CareActionReceiver extends BroadcastReceiver {
    public static final String ACTION_WATER_CONFIRM = "vn.edu.nlu.plantcare.ACTION_WATER_CONFIRM";
    public static final String ACTION_FERTILIZE_CONFIRM = "vn.edu.nlu.plantcare.ACTION_FERTILIZE_CONFIRM";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        String plantId = intent.getStringExtra("EXTRA_PLANT_ID");
        int notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", -1);

        if (notificationId != -1) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.cancel(notificationId);
            }
        }

        if (plantId == null) return;

        CareRepository careRepository = new CareRepository((Application) context.getApplicationContext());
        AppDatabase db = AppDatabase.getDatabase(context);
        PlantDao plantDao = db.plantDao();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Plant plant = plantDao.getPlantByIdDirect(plantId);
            if (plant != null) {
                if (ACTION_WATER_CONFIRM.equals(action)) {
                    careRepository.waterPlant(plant, "Xác nhận từ thông báo nhanh");
                } else if (ACTION_FERTILIZE_CONFIRM.equals(action)) {
                    careRepository.fertilizePlant(plant, plant.getFertilizerType(), "Xác nhận từ thông báo nhanh");
                }
            }
        });

        Toast.makeText(context, "Đã ghi nhận chăm sóc cây thành công!", Toast.LENGTH_SHORT).show();
    }
}
