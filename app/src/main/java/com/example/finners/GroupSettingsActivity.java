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
            showEditGroupDialog();
        });

        Button btnAddPeople = findViewById(R.id.btnAddPeople);
        btnAddPeople.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, AddGroupMemberActivity.class);
            intent.putExtra("GROUP_NAME", groupName);
            startActivity(intent);
        });

        Button btnInviteLink = findViewById(R.id.btnInviteLink);
        btnInviteLink.setOnClickListener(v -> {
            String inviteLink = "https://finners.app/invite/" + java.util.UUID.randomUUID().toString();
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Join my group '" + groupName + "' on Finners! " + inviteLink);
            startActivity(android.content.Intent.createChooser(shareIntent, "Share invite link via"));
        });

        Button btnLeaveGroup = findViewById(R.id.btnLeaveGroup);
        btnLeaveGroup.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Leave group")
                .setMessage("Are you sure you want to leave this group?")
                .setPositiveButton("Leave", (dialog, which) -> {
                    leaveGroup();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        Button btnDeleteGroup = findViewById(R.id.btnDeleteGroup);
        btnDeleteGroup.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Delete group")
                .setMessage("Are you you ABSOLUTELY sure you want to delete this group? This will remove this group for ALL users involved, not just yourself.")
                .setPositiveButton("OK", (dialog, which) -> {
                    try {
                        // Remove group from SharedPreferences
                        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
                        java.util.Set<String> groups = new java.util.HashSet<>(prefs.getStringSet("groups", new java.util.HashSet<>()));
                        if (groups.contains(groupName)) {
                            groups.remove(groupName);
                            prefs.edit().putStringSet("groups", groups).apply();
                        }

                        // Log activity
                        ActivityLogger.log(getApplicationContext(), "You deleted the group \"" + groupName + "\".");

                        // Navigate back to Groups tab
                        android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error deleting group", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
        });

        Switch switchSimplifyDebts = findViewById(R.id.switchSimplifyDebts);
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        boolean isSimplifyEnabled = prefs.getBoolean("simplify_" + groupName, false);
        switchSimplifyDebts.setChecked(isSimplifyEnabled);
        
        switchSimplifyDebts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("simplify_" + groupName, isChecked).apply();
            String status = isChecked ? "enabled" : "disabled";
            Toast.makeText(this, "Simplify debts " + status, Toast.LENGTH_SHORT).show();
        });
        
        // Load actual members
        loadGroupMembers();
    }

    private void addMemberButton(LinearLayout container, String memberName) {
        Button memberView = new Button(this);
        memberView.setText(memberName);
        memberView.setTextSize(16);
        memberView.setPadding(0, 16, 0, 16);
        // Style as flat button or similar if desired, for now default button is fine or maybe text button style
        // memberView.setBackground(null); // Make it look like text if preferred, but user said "members buttons"
        
        memberView.setOnClickListener(v -> onMemberClick(memberName));
        container.addView(memberView);
    }

    private void onMemberClick(String memberName) {
        if ("You".equals(memberName)) return; // Cannot remove yourself this way (use Leave Group)

        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String creator = prefs.getString("creator_" + groupName, "");
        
        if ("You".equals(creator)) {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Remove Member")
                .setMessage("Remove " + memberName + " from the group?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeMember(memberName);
                })
                .setNegativeButton("Cancel", null)
                .show();
        }
    }

    private void removeMember(String memberName) {
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        java.util.Set<String> members = new java.util.HashSet<>(prefs.getStringSet("members_" + groupName, new java.util.HashSet<>()));
        if (members.contains(memberName)) {
            members.remove(memberName);
            prefs.edit().putStringSet("members_" + groupName, members).apply();
            
            ActivityLogger.log(this, "You removed " + memberName + " from " + groupName);
            loadGroupMembers(); // Refresh list
            Toast.makeText(this, memberName + " removed", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditGroupDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(groupName);
        
        new android.app.AlertDialog.Builder(this)
            .setTitle("Rename Group")
            .setView(input)
            .setPositiveButton("Save", (dialog, which) -> {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty() && !newName.equals(groupName)) {
                    renameGroup(newName);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void renameGroup(String newName) {
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        java.util.Set<String> groups = new java.util.HashSet<>(prefs.getStringSet("groups", new java.util.HashSet<>()));
        
        if (groups.contains(groupName)) {
            groups.remove(groupName);
            groups.add(newName);
            prefs.edit().putStringSet("groups", groups).apply();
            
            // Migrate members
            java.util.Set<String> members = prefs.getStringSet("members_" + groupName, new java.util.HashSet<>());
            prefs.edit().putStringSet("members_" + newName, members).apply();
            prefs.edit().remove("members_" + groupName).apply();
            
            groupName = newName;
            TextView tvGroupName = findViewById(R.id.tvGroupName);
            tvGroupName.setText(groupName);
            Toast.makeText(this, "Group renamed", Toast.LENGTH_SHORT).show();
        }
    }

    private void leaveGroup() {
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        java.util.Set<String> groups = new java.util.HashSet<>(prefs.getStringSet("groups", new java.util.HashSet<>()));
        
        if (groups.contains(groupName)) {
            groups.remove(groupName);
            prefs.edit().putStringSet("groups", groups).apply();
            
            // Navigate back to Home
            android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void loadGroupMembers() {
        LinearLayout layoutMembers = findViewById(R.id.layoutMembers);
        layoutMembers.removeAllViews();
        
        // Always add "You"
        addMemberButton(layoutMembers, "You");
        
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        java.util.Set<String> members = prefs.getStringSet("members_" + groupName, new java.util.HashSet<>());
        
        for (String member : members) {
            addMemberButton(layoutMembers, member);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGroupMembers();
    }
}
