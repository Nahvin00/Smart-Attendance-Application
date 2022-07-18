package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Size;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.Executors;

public class BarcodeScannerActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private BarcodeScanner barcodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);


        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            permissionResult(true);
        else
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    this::permissionResult).launch(Manifest.permission.CAMERA);
    }

    private void permissionResult(Boolean isGranted) {
        if (isGranted)
        {
            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE).build();

            cameraProviderFuture = ProcessCameraProvider.getInstance(this);
            barcodeScanner = BarcodeScanning.getClient(options);
            cameraProviderFuture.addListener(this::startCamera, ContextCompat.getMainExecutor(this));

        }
        else
        {
            Toast.makeText(this, "This application requires camera to function",
                    Toast.LENGTH_LONG).show();
            finish();

        }
    }

    private void startCamera() {
        try {
            ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();
            Preview preview = new Preview.Builder().build();
            ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                    .setTargetResolution(new Size(1280,720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing
                    (CameraSelector.LENS_FACING_BACK).build();
            PreviewView previewView = findViewById(R.id.prvScanner);

            preview.setSurfaceProvider(previewView.getSurfaceProvider());
            
            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), this::analyze);
            
            processCameraProvider.bindToLifecycle(this,
                    cameraSelector, preview, imageAnalysis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void analyze(ImageProxy imageProxy) {
        @SuppressLint("UnsafeOptInUsageError")
        Image mediaImage = imageProxy.getImage();

        if (mediaImage != null)
        {
            InputImage image = InputImage.fromMediaImage(mediaImage,
                    imageProxy.getImageInfo().getRotationDegrees());

            barcodeScanner.process(image).addOnSuccessListener(this::scanResults)
                    .addOnCompleteListener(barcodes -> imageProxy.close());
        }
    }

    private void scanResults(List<Barcode> barcodes) {
        if (!barcodes.isEmpty())
        {
            Barcode barcode = barcodes.get(0);
            String value = barcode.getDisplayValue();

            if(value != null && value.startsWith("https://mysejahtera.malaysia.gov.my/qrscan"))
            {
                Uri uri = Uri.parse(value);
                Intent intent = new Intent();

                System.out.println(uri.getQueryParameter("lId"));
                System.out.println(uri.getQueryParameter("ln"));

                intent.putExtra("locationID", uri.getQueryParameter("lId"));
                intent.putExtra("locationName", uri.getQueryParameter("ln"));
                setResult(RESULT_OK, intent);
                finish();
            }


        }
    }
}