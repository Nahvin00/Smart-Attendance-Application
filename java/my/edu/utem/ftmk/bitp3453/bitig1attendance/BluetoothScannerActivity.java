package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.health.PackageHealthStats;

import java.util.Map;

public class BluetoothScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scanner);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) ==
                            PackageManager.PERMISSION_GRANTED)
            {
                permissionResults(null);
            }
            else
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                        this::permissionResults).launch(new String[]
                        {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN});
        }

    }

    private void permissionResults(Map<String, Boolean> results) {

    }
}