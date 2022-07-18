package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Schedule.class}, version = 1, exportSchema = false)
public abstract class ScheduleDatabase extends RoomDatabase {
    private static ScheduleDatabase INSTANCE;

    public abstract ScheduleManager getScheduleManager();

    public static ScheduleDatabase getInstance(Context context){
        synchronized (ScheduleDatabase.class){
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ScheduleDatabase.class, "dbSchedules")
                        .fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }
}
