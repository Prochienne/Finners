package com.example.finners;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        etName = findViewById(R.id.etName);
        
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
}
