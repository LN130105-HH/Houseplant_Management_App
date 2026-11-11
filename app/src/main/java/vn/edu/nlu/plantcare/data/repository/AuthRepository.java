package vn.edu.nlu.plantcare.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import vn.edu.nlu.plantcare.data.local.AppDatabase;
import vn.edu.nlu.plantcare.data.local.dao.UserDao;
import vn.edu.nlu.plantcare.data.local.entity.User;

public class AuthRepository {
    private final UserDao userDao;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private final MutableLiveData<String> authError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> authSuccess = new MutableLiveData<>();

    public AuthRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        try {
            firebaseAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
        } catch (Exception ignored) {
            // Firebase may be offline or uninitialized
        }
    }

    public LiveData<String> getAuthError() {
        return authError;
    }

    public LiveData<Boolean> getAuthSuccess() {
        return authSuccess;
    }

    public String getCurrentUserId() {
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getUid();
        }
        return "local_user_default";
    }

    public String getCurrentUserEmail() {
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getEmail();
        }
        return "gardener@plantcare.local";
    }

    public String getCurrentDisplayName() {
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null) {
            String name = firebaseAuth.getCurrentUser().getDisplayName();
            if (name != null && !name.trim().isEmpty()) {
                return name;
            }
        }
        return "Người làm vườn";
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth != null && firebaseAuth.getCurrentUser() != null;
    }

    public void login(String email, String password) {
        if (firebaseAuth != null) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fUser = authResult.getUser();
                    if (fUser != null) {
                        saveUserLocal(fUser.getUid(), fUser.getEmail(), fUser.getDisplayName(), null);
                    }
                    authSuccess.postValue(true);
                })
                .addOnFailureListener(e -> {
                    authError.postValue(e.getMessage());
                });
        } else {
            // Offline fallback
            saveUserLocal("local_user_default", email, "Người làm vườn", null);
            authSuccess.postValue(true);
        }
    }

    public void register(String email, String password, String displayName) {
        if (firebaseAuth != null) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fUser = authResult.getUser();
                    if (fUser != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();
                        fUser.updateProfile(profileUpdates);
                        saveUserLocal(fUser.getUid(), email, displayName, null);
                        saveUserFirestore(fUser.getUid(), email, displayName);
                    }
                    authSuccess.postValue(true);
                })
                .addOnFailureListener(e -> {
                    authError.postValue(e.getMessage());
                });
        } else {
            saveUserLocal("local_user_default", email, displayName, null);
            authSuccess.postValue(true);
        }
    }

    private void saveUserLocal(String uid, String email, String displayName, String photoUrl) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = new User(uid, email, displayName, photoUrl, System.currentTimeMillis());
            userDao.insertUser(user);
        });
    }

    private void saveUserFirestore(String uid, String email, String displayName) {
        if (firestore != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("uid", uid);
            userData.put("email", email);
            userData.put("displayName", displayName);
            userData.put("createdAt", System.currentTimeMillis());
            firestore.collection("users").document(uid).set(userData);
        }
    }

    public void logout() {
        if (firebaseAuth != null) {
            firebaseAuth.signOut();
        }
    }
}
