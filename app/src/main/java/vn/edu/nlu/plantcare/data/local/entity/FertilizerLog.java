package vn.edu.nlu.plantcare.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(
    tableName = "fertilizer_logs",
    foreignKeys = @ForeignKey(
        entity = Plant.class,
        parentColumns = "id",
        childColumns = "plantId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = "plantId")}
)
public class FertilizerLog implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String plantId;
    private String fertilizerType;
    private long fertilizedAt;
    private String note;
    private boolean isSynced;

    public FertilizerLog(@NonNull String id, String plantId, String fertilizerType, long fertilizedAt, String note, boolean isSynced) {
        this.id = id;
        this.plantId = plantId;
        this.fertilizerType = fertilizerType;
        this.fertilizedAt = fertilizedAt;
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

    public String getFertilizerType() {
        return fertilizerType;
    }

    public void setFertilizerType(String fertilizerType) {
        this.fertilizerType = fertilizerType;
    }

    public long getFertilizedAt() {
        return fertilizedAt;
    }

    public void setFertilizedAt(long fertilizedAt) {
        this.fertilizedAt = fertilizedAt;
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
