package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.splashscreen.SplashScreen;

public class RoutingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        splashScreen.setKeepOnScreenCondition(() -> true);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Intent intent;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(getPackageName(),
                "Schedule Reminder", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("This channel is used to remind the user of their schedule");
        notificationChannel.setVibrationPattern(new long[] {1000, 500, 1000});
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.enableLights(true);
        notificationManager.createNotificationChannel(notificationChannel);

        if (token == null)
            intent = new Intent(this, LoginActivity.class);
        else
        {
            Bundle account = new Bundle();
            Boolean lecturer = sharedPreferences.getBoolean("lecturer", false);

            account.putString("accountID", sharedPreferences.getString("accountID", null));
            account.putString("token", token);
            account.putString("name", sharedPreferences.getString("name", null));
            account.putBoolean("lecturer", lecturer);

            intent = new Intent(this, lecturer ? LecturerMenuActivity.class : StudentMenuActivity.class);
            intent.putExtra("account", account);
        }

        startActivity(intent);
        finish();
    }
}