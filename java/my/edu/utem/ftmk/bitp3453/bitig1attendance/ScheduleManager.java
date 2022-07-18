package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScheduleManager {

    @Insert
    long insertSchedule(Schedule schedule);

    @Update
    int updateSchedule(Schedule schedule);

    @Delete
    int deleteSchedule(Schedule schedule);

    @Query("SELECT * FROM Schedule WHERE lecturerID = :lecturerID ORDER BY startDateTime DESC")
    LiveData<List<Schedule>> getSchedules(String lecturerID);
}
