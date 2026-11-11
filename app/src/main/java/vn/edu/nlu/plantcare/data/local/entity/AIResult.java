package vn.edu.nlu.plantcare.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "ai_results")
public class AIResult implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String plantId;
    private String imageUrl;
    private String resultType; // IDENTIFY or HEALTH_CHECK
    private String plantName;
    private String description;
    private String careAdvice;
    private String lightRequirement;
    private String waterRequirement;
    private String temperatureRequirement;
    private String healthStatus;
    private String diseaseDiagnosis;
    private String treatmentAdvice;
    private long createdAt;

    public AIResult(@NonNull String id, String plantId, String imageUrl, String resultType,
                    String plantName, String description, String careAdvice,
                    String lightRequirement, String waterRequirement, String temperatureRequirement,
                    String healthStatus, String diseaseDiagnosis, String treatmentAdvice,
                    long createdAt) {
        this.id = id;
        this.plantId = plantId;
        this.imageUrl = imageUrl;
        this.resultType = resultType;
        this.plantName = plantName;
        this.description = description;
        this.careAdvice = careAdvice;
        this.lightRequirement = lightRequirement;
        this.waterRequirement = waterRequirement;
        this.temperatureRequirement = temperatureRequirement;
        this.healthStatus = healthStatus;
        this.diseaseDiagnosis = diseaseDiagnosis;
        this.treatmentAdvice = treatmentAdvice;
        this.createdAt = createdAt;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCareAdvice() {
        return careAdvice;
    }

    public void setCareAdvice(String careAdvice) {
        this.careAdvice = careAdvice;
    }

    public String getLightRequirement() {
        return lightRequirement;
    }

    public void setLightRequirement(String lightRequirement) {
        this.lightRequirement = lightRequirement;
    }

    public String getWaterRequirement() {
        return waterRequirement;
    }

    public void setWaterRequirement(String waterRequirement) {
        this.waterRequirement = waterRequirement;
    }

    public String getTemperatureRequirement() {
        return temperatureRequirement;
    }

    public void setTemperatureRequirement(String temperatureRequirement) {
        this.temperatureRequirement = temperatureRequirement;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getDiseaseDiagnosis() {
        return diseaseDiagnosis;
    }

    public void setDiseaseDiagnosis(String diseaseDiagnosis) {
        this.diseaseDiagnosis = diseaseDiagnosis;
    }

    public String getTreatmentAdvice() {
        return treatmentAdvice;
    }

    public void setTreatmentAdvice(String treatmentAdvice) {
        this.treatmentAdvice = treatmentAdvice;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
