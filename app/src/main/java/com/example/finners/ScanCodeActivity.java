package com.example.finners;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ScanCodeActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private DecoratedBarcodeView barcodeScannerView;
    private View layoutScan, layoutMyCode;
    private Button btnTabScan, btnTabMyCode;
    private ImageView ivQrCode;
    private String myCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnTabScan = findViewById(R.id.btnTabScan);
        btnTabMyCode = findViewById(R.id.btnTabMyCode);
        layoutScan = findViewById(R.id.layoutScan);
        layoutMyCode = findViewById(R.id.layoutMyCode);
        barcodeScannerView = findViewById(R.id.barcodeScannerView);
        ivQrCode = findViewById(R.id.ivQrCode);
        Button btnShareCode = findViewById(R.id.btnShareCode);
        Button btnCopyCode = findViewById(R.id.btnCopyCode);
        Button btnChangeCode = findViewById(R.id.btnChangeCode);

        btnBack.setOnClickListener(v -> finish());

        btnTabScan.setOnClickListener(v -> showScanTab());
        btnTabMyCode.setOnClickListener(v -> showMyCodeTab());

        btnShareCode.setOnClickListener(v -> shareCode());
        btnCopyCode.setOnClickListener(v -> copyCode());
        btnChangeCode.setOnClickListener(v -> generateNewCode());

        // Initial state
        showScanTab();
        generateNewCode();
    }

    private void showScanTab() {
        layoutScan.setVisibility(View.VISIBLE);
        layoutMyCode.setVisibility(View.GONE);
        btnTabScan.setTextColor(Color.BLACK);
        btnTabMyCode.setTextColor(Color.parseColor("#666666"));
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            startScanning();
        }
    }

    private void showMyCodeTab() {
        layoutScan.setVisibility(View.GONE);
        layoutMyCode.setVisibility(View.VISIBLE);
        btnTabScan.setTextColor(Color.parseColor("#666666"));
        btnTabMyCode.setTextColor(Color.BLACK);
        barcodeScannerView.pause();
    }

    private void startScanning() {
        barcodeScannerView.resume();
        barcodeScannerView.decodeContinuous(result -> {
            // Handle scan result
            runOnUiThread(() -> {
                Toast.makeText(this, "Scanned: " + result.getText(), Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void generateNewCode() {
        myCode = UUID.randomUUID().toString();
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(myCode, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void shareCode() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "My Finners Code: " + myCode);
        startActivity(Intent.createChooser(shareIntent, "Share Code via"));
    }

    private void copyCode() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Finners Code", myCode);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Code copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Toast.makeText(this, "Camera permission required to scan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (layoutScan.getVisibility() == View.VISIBLE) {
            barcodeScannerView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }
}
