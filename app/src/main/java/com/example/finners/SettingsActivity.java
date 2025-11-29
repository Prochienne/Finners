package com.example.finners;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    public static final String EXTRA_SETTINGS_TYPE = "SETTINGS_TYPE";
    public static final String TYPE_EDIT_PROFILE = "EDIT_PROFILE";
    public static final String TYPE_EMAIL_SETTINGS = "EMAIL_SETTINGS";
    public static final String TYPE_NOTIFICATIONS = "NOTIFICATIONS";
    public static final String TYPE_SECURITY = "SECURITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String type = getIntent().getStringExtra(EXTRA_SETTINGS_TYPE);
        if (type == null) {
            finish();
            return;
        }

        switch (type) {
            case TYPE_EDIT_PROFILE:
                setupEditProfile();
                break;
            case TYPE_EMAIL_SETTINGS:
                setupEmailSettings();
                break;
            case TYPE_NOTIFICATIONS:
                setupNotifications();
                break;
            case TYPE_SECURITY:
                setupSecurity();
                break;
            default:
                finish();
                break;
        }
    }

    private void setupEditProfile() {
        setContentView(R.layout.activity_edit_profile);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        EditText etName = findViewById(R.id.etName);
        
        // Load current name
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String currentName = prefs.getString("user_name", "User Name");
        etName.setText(currentName);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            prefs.edit().putString("user_name", newName).apply();
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupEmailSettings() {
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

    private void setupNotifications() {
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

    private void setupSecurity() {
        setContentView(R.layout.activity_security);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change password feature coming soon", Toast.LENGTH_SHORT).show();
        });

        Switch switch2FA = findViewById(R.id.switch2FA);
        switch2FA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "2FA " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
    }
}
