package com.example.finners;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnBack.setOnClickListener(v -> finish());

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Optional: finish LoginActivity so back button doesn't go back to login
        });

        tvForgotPassword.setOnClickListener(v -> {
            // Do nothing for now
        });
    }
}
