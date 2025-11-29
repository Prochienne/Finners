package com.example.finners;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Switch switchPush = findViewById(R.id.switchPush);
        switchPush.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Push notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        Switch switchEmail = findViewById(R.id.switchEmail);
        switchEmail.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Email notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
        
        Switch switchGroups = findViewById(R.id.switchGroups);
        switchGroups.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Group updates " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
    }
}
