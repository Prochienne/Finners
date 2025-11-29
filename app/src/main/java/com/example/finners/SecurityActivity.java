package com.example.finners;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecurityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
