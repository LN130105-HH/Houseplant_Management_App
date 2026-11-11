package vn.edu.nlu.plantcare.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(
    tableName = "journal_entries",
    foreignKeys = @ForeignKey(
        entity = Plant.class,
        parentColumns = "id",
        childColumns = "plantId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = "plantId")}
)
public class JournalEntry implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String plantId;
    private long date;
    private String note;
    private double heightCm;
    private String imageUrl;
    private boolean isSynced;

    public JournalEntry(@NonNull String id, String plantId, long date, String note, double heightCm, String imageUrl, boolean isSynced) {
        this.id = id;
        this.plantId = plantId;
        this.date = date;
        this.note = note;
        this.heightCm = heightCm;
        this.imageUrl = imageUrl;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
