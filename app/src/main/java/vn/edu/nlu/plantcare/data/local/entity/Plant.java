package vn.edu.nlu.plantcare.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "plants")
public class Plant implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String userId;
    private String name;
    private String type;
    private String plantedDate;
    private String imageUrl;
    private String note;
    private int wateringIntervalDays;
    private int fertilizerIntervalDays;
    private String fertilizerType;
    private long lastWateredAt;
    private long lastFertilizedAt;
    private long nextWateringAt;
    private long nextFertilizerAt;
    private boolean isSynced;

    public Plant(@NonNull String id, String userId, String name, String type, String plantedDate,
                 String imageUrl, String note, int wateringIntervalDays, int fertilizerIntervalDays,
                 String fertilizerType, long lastWateredAt, long lastFertilizedAt,
                 long nextWateringAt, long nextFertilizerAt, boolean isSynced) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.plantedDate = plantedDate;
        this.imageUrl = imageUrl;
        this.note = note;
        this.wateringIntervalDays = wateringIntervalDays;
        this.fertilizerIntervalDays = fertilizerIntervalDays;
        this.fertilizerType = fertilizerType;
        this.lastWateredAt = lastWateredAt;
        this.lastFertilizedAt = lastFertilizedAt;
        this.nextWateringAt = nextWateringAt;
        this.nextFertilizerAt = nextFertilizerAt;
        this.isSynced = isSynced;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlantedDate() {
        return plantedDate;
    }

    public void setPlantedDate(String plantedDate) {
        this.plantedDate = plantedDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getWateringIntervalDays() {
        return wateringIntervalDays;
    }

    public void setWateringIntervalDays(int wateringIntervalDays) {
        this.wateringIntervalDays = wateringIntervalDays;
    }

    public int getFertilizerIntervalDays() {
        return fertilizerIntervalDays;
    }

    public void setFertilizerIntervalDays(int fertilizerIntervalDays) {
        this.fertilizerIntervalDays = fertilizerIntervalDays;
    }

    public String getFertilizerType() {
        return fertilizerType;
    }

    public void setFertilizerType(String fertilizerType) {
        this.fertilizerType = fertilizerType;
    }

    public long getLastWateredAt() {
        return lastWateredAt;
    }

    public void setLastWateredAt(long lastWateredAt) {
        this.lastWateredAt = lastWateredAt;
    }

    public long getLastFertilizedAt() {
        return lastFertilizedAt;
    }

    public void setLastFertilizedAt(long lastFertilizedAt) {
        this.lastFertilizedAt = lastFertilizedAt;
    }

    public long getNextWateringAt() {
        return nextWateringAt;
    }

    public void setNextWateringAt(long nextWateringAt) {
        this.nextWateringAt = nextWateringAt;
    }

    public long getNextFertilizerAt() {
        return nextFertilizerAt;
    }

    public void setNextFertilizerAt(long nextFertilizerAt) {
        this.nextFertilizerAt = nextFertilizerAt;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
