package com.example.finners;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GroupSettingsActivity extends AppCompatActivity {

    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        groupName = getIntent().getStringExtra("GROUP_NAME");
        if (groupName == null) {
            groupName = "Group";
        }

        TextView tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupName.setText(groupName);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnEditGroup = findViewById(R.id.btnEditGroup);
        btnEditGroup.setOnClickListener(v -> {
            Toast.makeText(this, "Edit group clicked", Toast.LENGTH_SHORT).show();
        });

        Button btnAddPeople = findViewById(R.id.btnAddPeople);
        btnAddPeople.setOnClickListener(v -> {
            Toast.makeText(this, "Add people clicked", Toast.LENGTH_SHORT).show();
        });

        Button btnInviteLink = findViewById(R.id.btnInviteLink);
        btnInviteLink.setOnClickListener(v -> {
            Toast.makeText(this, "Invite link clicked", Toast.LENGTH_SHORT).show();
        });

        Button btnLeaveGroup = findViewById(R.id.btnLeaveGroup);
        btnLeaveGroup.setOnClickListener(v -> {
            Toast.makeText(this, "Leave group clicked", Toast.LENGTH_SHORT).show();
        });

        Button btnDeleteGroup = findViewById(R.id.btnDeleteGroup);
        btnDeleteGroup.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Delete group")
                .setMessage("Are you you ABSOLUTELY sure you want to delete this group? This will remove this group for ALL users involved, not just yourself.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Remove group from SharedPreferences
                    android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
                    java.util.Set<String> groups = new java.util.HashSet<>(prefs.getStringSet("groups", new java.util.HashSet<>()));
                    if (groups.contains(groupName)) {
                        groups.remove(groupName);
                        prefs.edit().putStringSet("groups", groups).apply();
                    }

                    // Log activity
                    String logMessage = "You deleted the group \"" + groupName + "\".";
                    String logsJson = prefs.getString("activity_logs", "[]");
                    try {
                        org.json.JSONArray jsonArray = new org.json.JSONArray(logsJson);
                        jsonArray.put(logMessage);
                        prefs.edit().putString("activity_logs", jsonArray.toString()).apply();
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                    // Navigate back to Groups tab
                    android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
        });

        Switch switchSimplifyDebts = findViewById(R.id.switchSimplifyDebts);
        switchSimplifyDebts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Simplify debts: " + isChecked, Toast.LENGTH_SHORT).show();
        });
        
        // Add dummy members for demonstration
        LinearLayout layoutMembers = findViewById(R.id.layoutMembers);
        addMemberButton(layoutMembers, "You");
    }

    private void addMemberButton(LinearLayout container, String memberName) {
        Button memberButton = new Button(this);
        memberButton.setText(memberName);
        container.addView(memberButton);
    }
}
