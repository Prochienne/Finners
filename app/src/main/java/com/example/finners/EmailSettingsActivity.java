package com.example.finners;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmailSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_settings);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Switch switchMarketing = findViewById(R.id.switchMarketing);
        switchMarketing.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Marketing emails " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
        
        Switch switchDigest = findViewById(R.id.switchDigest);
        switchDigest.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Weekly digest " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
    }
}
