package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ScheduleReminder extends Worker {
    public ScheduleReminder(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Notification.Builder builder = new Notification.Builder(context, context.getPackageName())
                .setContentTitle("Class Reminder")
                .setContentText("Your class will start in " +
                        sharedPreferences.getString("interval", "30") + " minutes");
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());

        return Result.success();
    }
}
