package vn.edu.nlu.plantcare.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(
    tableName = "watering_logs",
    foreignKeys = @ForeignKey(
        entity = Plant.class,
        parentColumns = "id",
        childColumns = "plantId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = "plantId")}
)
public class WateringLog implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String plantId;
    private long wateredAt;
    private String note;
    private boolean isSynced;

    public WateringLog(@NonNull String id, String plantId, long wateredAt, String note, boolean isSynced) {
        this.id = id;
        this.plantId = plantId;
        this.wateredAt = wateredAt;
        this.note = note;
        this.isSynced = isSynced;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public long getWateredAt() {
        return wateredAt;
    }

    public void setWateredAt(long wateredAt) {
        this.wateredAt = wateredAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
