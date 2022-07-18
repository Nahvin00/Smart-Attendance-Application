package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScheduleViewModel extends AndroidViewModel {

    private final ScheduleManager scheduleManager;
    private LiveData<List<Schedule>> schedules;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        scheduleManager = ScheduleDatabase.getInstance(application).getScheduleManager();
    }

    public long insertSchedule(Schedule schedule){
        long scheduleID = 0;
        try {
            scheduleID = Executors.newSingleThreadExecutor()
                    .submit(() -> scheduleManager.insertSchedule(schedule))
                    .get();
            if (scheduleID != 0)
            {
                schedule.setScheduleID(scheduleID);
                remind(schedule);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return scheduleID;
    }

    public int updateSchedule(Schedule schedule){
        int status = 0;
        try {
            status= Executors.newSingleThreadExecutor()
                    .submit(() -> scheduleManager.updateSchedule(schedule)).get();

            if (status != 0)
                remind(schedule);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }

    public int deleteSchedule(Schedule schedule){
        int status = 0;
        try {
            status= Executors.newSingleThreadExecutor()
                    .submit(() -> scheduleManager.deleteSchedule(schedule)).get();

            if (status != 0)
            {
                WorkManager.getInstance(getApplication()).cancelUniqueWork(String.valueOf(schedule.getScheduleID()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }

    public LiveData<List<Schedule>> getSchedules(String lecturerID){
        if(schedules == null)
            schedules = scheduleManager.getSchedules(lecturerID);
        return schedules;
    }

    private void remind(Schedule schedule) {
        Application application = getApplication();
        long delay = schedule.getStartDateTime() - System.currentTimeMillis() -
                Long.parseLong(PreferenceManager.getDefaultSharedPreferences(application)
                        .getString("interval", "30")) * 60_000;

        WorkManager.getInstance(getApplication()).enqueueUniqueWork(String.valueOf
                (schedule.getScheduleID()), ExistingWorkPolicy.REPLACE, new OneTimeWorkRequest
                .Builder(ScheduleReminder.class).setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build());

        AsyncTask.execute(() ->sendNotification(schedule));
        //Executors.newSingleThreadExecutor().execute(this::sendNotification);
    }

    private void sendNotification(Schedule schedule) {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) new
                    URL("https://firebase.google.com/docs/cloud-messaging/http-server-ref")
                    .openConnection();
            JSONObject request = new JSONObject(), notification = new JSONObject();

            GregorianCalendar start = new GregorianCalendar(), end = new GregorianCalendar();

            start.setTimeInMillis(schedule.getStartDateTime());
            end.setTimeInMillis(schedule.getStartDateTime());
            end.add(GregorianCalendar.HOUR, schedule.getDuration());

            notification.put("title", schedule.getCourse());
            notification.put("body", DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT)
                    .format(start.getTime()) + "until" + DateFormat.getTimeInstance(DateFormat.SHORT)
                    .format(end.getTime()));

            notification.put("tag", String.valueOf((schedule.getScheduleID())));
            notification.put("icon", "ic_menu_camera");
            notification.put("click_action", "dfdf");
            request.put("to", "/topics/the_first_thread");
            request.put("notification", notification);

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "key=AAAA20vkCXA:APA91bFmdinAScB6fTbyJ0dnNhZhEnT_mPLoBJ2UxgqFA3ejEfcDeS6mkh34h5GAKIPZWbLO2u5LYB5g9gn4hyxthTeYfCrmdbrLg2aglwHTkvoqQXiaTiJ5iCcDQjJtEhm1pCjBpuaC");
            connection.getOutputStream().write(request.toString().getBytes());
            System.out.println(connection.getResponseCode());
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
