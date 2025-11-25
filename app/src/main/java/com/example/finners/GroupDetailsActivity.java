package com.example.finners;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GroupDetailsActivity extends AppCompatActivity {

    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        groupName = getIntent().getStringExtra("GROUP_NAME");
        if (groupName == null) {
            groupName = "Group";
        }

        TextView tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupName.setText(groupName);

        TextView tvGroupDates = findViewById(R.id.tvGroupDates);
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String dates = prefs.getString("group_dates_" + groupName, null);
        
        if (dates != null) {
            tvGroupDates.setText(dates);
            tvGroupDates.setVisibility(android.view.View.VISIBLE);
        } else {
            tvGroupDates.setVisibility(android.view.View.GONE);
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetailsActivity.this, GroupSettingsActivity.class);
            intent.putExtra("GROUP_NAME", groupName);
            startActivity(intent);
        });

        Button btnAddMember = findViewById(R.id.btnAddMember);
        btnAddMember.setOnClickListener(v -> {
            Toast.makeText(this, "Add member clicked", Toast.LENGTH_SHORT).show();
        });

        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetailsActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        Button btnSettleUp = findViewById(R.id.btnSettleUp);
        btnSettleUp.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetailsActivity.this, SettleUpActivity.class);
            // We don't pass a friend ID here, so SettleUpActivity will prompt for selection
            startActivity(intent);
        });
    }
}
