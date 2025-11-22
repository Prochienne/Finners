package com.example.finners;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String userName = getIntent().getStringExtra("USER_NAME");
        TextView welcomeText = findViewById(R.id.tvWelcomeHome);

        if (userName != null) {
            welcomeText.setText("Welcome to Finners, " + userName + "!");
        }
    }
}
