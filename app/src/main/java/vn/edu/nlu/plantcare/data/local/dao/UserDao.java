package vn.edu.nlu.plantcare.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import vn.edu.nlu.plantcare.data.local.entity.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    User getUserById(String uid);

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    LiveData<User> getUserByIdLiveData(String uid);

    @Query("DELETE FROM users WHERE uid = :uid")
    void deleteUser(String uid);
}
