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
            showAddMemberDialog();
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

    private void showAddMemberDialog() {
        FriendsRepository repository = FriendsRepository.getInstance(this);
        java.util.List<Contact> friendsList = repository.getFriends();
        
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        java.util.Set<String> currentMembers = prefs.getStringSet("members_" + groupName, new java.util.HashSet<>());
        
        java.util.List<Contact> availableFriends = new java.util.ArrayList<>();
        for (Contact friend : friendsList) {
            if (!currentMembers.contains(friend.getName())) {
                availableFriends.add(friend);
            }
        }

        if (availableFriends.isEmpty()) {
            Toast.makeText(this, "No new friends to add", Toast.LENGTH_SHORT).show();
            return;
        }

        android.view.View dialogView = android.view.LayoutInflater.from(this).inflate(R.layout.activity_add_group_member, null);
        
        androidx.recyclerview.widget.RecyclerView rvFriends = dialogView.findViewById(R.id.rvFriends);
        TextView tvNoFriends = dialogView.findViewById(R.id.tvNoFriends);
        ImageButton btnBack = dialogView.findViewById(R.id.btnBack); 
        if (btnBack != null) btnBack.setVisibility(android.view.View.GONE); 

        rvFriends.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create();

        GroupMemberAdapter adapter = new GroupMemberAdapter(availableFriends, friend -> {
            addMemberToGroup(friend.getName());
            dialog.dismiss();
        });
        rvFriends.setAdapter(adapter);
        
        tvNoFriends.setVisibility(android.view.View.GONE);
        rvFriends.setVisibility(android.view.View.VISIBLE);

        dialog.show();
    }

    private void addMemberToGroup(String memberName) {
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        java.util.Set<String> members = new java.util.HashSet<>(prefs.getStringSet("members_" + groupName, new java.util.HashSet<>()));
        members.add(memberName);
        prefs.edit().putStringSet("members_" + groupName, members).apply();
        
        Toast.makeText(this, memberName + " added to group", Toast.LENGTH_SHORT).show();
        loadGroupMembers(); 
    }

    private static class GroupMemberAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {
        private java.util.List<Contact> friends;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(Contact contact);
        }

        public GroupMemberAdapter(java.util.List<Contact> friends, OnItemClickListener listener) {
            this.friends = friends;
            this.listener = listener;
        }

        @androidx.annotation.NonNull
        @Override
        public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
            Contact friend = friends.get(position);
            holder.textView.setText(friend.getName());
            holder.itemView.setOnClickListener(v -> listener.onItemClick(friend));
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(android.view.View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
