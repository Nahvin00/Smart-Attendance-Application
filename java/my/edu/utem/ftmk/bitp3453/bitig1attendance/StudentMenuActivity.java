package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class StudentMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_menu);

        String name = getIntent().getStringExtra("name");

        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(this::scan);

        new AlertDialog.Builder(this).
                setMessage("Welcome " + name).show();

        FirebaseMessaging.getInstance().subscribeToTopic("the_first_thread");
    }

    private void scan(View view)
    {
        startActivity(new Intent(this, BarcodeScannerActivity.class));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("Are you sure want to logout?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> logout())
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void logout()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}