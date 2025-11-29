package com.example.finners;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WhiteboardActivity extends AppCompatActivity {

    private EditText etWhiteboard;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiteboard);

        groupName = getIntent().getStringExtra("GROUP_NAME");
        if (groupName == null) groupName = "General";

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(groupName + " Whiteboard");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etWhiteboard = findViewById(R.id.etWhiteboard);

        loadWhiteboard();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveWhiteboard();
    }

    private void loadWhiteboard() {
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String content = prefs.getString("whiteboard_" + groupName, "");
        etWhiteboard.setText(content);
    }

    private void saveWhiteboard() {
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        prefs.edit().putString("whiteboard_" + groupName, etWhiteboard.getText().toString()).apply();
    }
}
