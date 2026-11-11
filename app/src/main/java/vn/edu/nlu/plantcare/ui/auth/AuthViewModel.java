package vn.edu.nlu.plantcare.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import vn.edu.nlu.plantcare.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public LiveData<String> getAuthError() {
        return authRepository.getAuthError();
    }

    public LiveData<Boolean> getAuthSuccess() {
        return authRepository.getAuthSuccess();
    }

    public String getCurrentUserId() {
        return authRepository.getCurrentUserId();
    }

    public String getCurrentUserEmail() {
        return authRepository.getCurrentUserEmail();
    }

    public String getCurrentDisplayName() {
        return authRepository.getCurrentDisplayName();
    }

    public boolean isUserLoggedIn() {
        return authRepository.isUserLoggedIn();
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    public void register(String email, String password, String displayName) {
        authRepository.register(email, password, displayName);
    }

    public void logout() {
        authRepository.logout();
    }
}
