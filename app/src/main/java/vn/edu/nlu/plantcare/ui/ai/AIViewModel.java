package vn.edu.nlu.plantcare.ui.ai;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import vn.edu.nlu.plantcare.data.local.entity.AIResult;
import vn.edu.nlu.plantcare.data.repository.AIRepository;

public class AIViewModel extends AndroidViewModel {
    private final AIRepository aiRepository;
    private final MutableLiveData<AIResult> analysisResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public AIViewModel(@NonNull Application application) {
        super(application);
        aiRepository = new AIRepository(application);
    }

    public LiveData<AIResult> getAnalysisResult() {
        return analysisResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<AIResult>> getAllResults() {
        return aiRepository.getAllResults();
    }

    public void analyzeImage(String plantId, String base64Image, String mode) {
        isLoading.setValue(true);
        aiRepository.analyzeImage(plantId, base64Image, mode, analysisResult, errorMessage);
        // Note: When result or error arrives, isLoading should be set to false
        analysisResult.observeForever(result -> {
            if (result != null) {
                isLoading.postValue(false);
            }
        });
        errorMessage.observeForever(err -> {
            if (err != null) {
                isLoading.postValue(false);
            }
        });
    }

    public void reset() {
        analysisResult.setValue(null);
        errorMessage.setValue(null);
        isLoading.setValue(false);
    }
}
