package com.example.finners;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class AddGroupMemberActivity extends AppCompatActivity {

    private String groupName;
    private RecyclerView rvFriends;
    private FriendsAdapter adapter;
    private List<Contact> friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_member);

        groupName = getIntent().getStringExtra("GROUP_NAME");
        if (groupName == null) {
            finish();
            return;
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvFriends = findViewById(R.id.rvFriends);
        TextView tvNoFriends = findViewById(R.id.tvNoFriends);

        FriendsRepository repository = FriendsRepository.getInstance(this);
        friendsList = repository.getFriends();

        // Filter out friends who are already in the group
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        Set<String> currentMembers = prefs.getStringSet("members_" + groupName, new HashSet<>());
        
        List<Contact> availableFriends = new ArrayList<>();
        for (Contact friend : friendsList) {
            if (!currentMembers.contains(friend.getName())) {
                availableFriends.add(friend);
            }
        }

        if (availableFriends.isEmpty()) {
            rvFriends.setVisibility(View.GONE);
            tvNoFriends.setVisibility(View.VISIBLE);
        } else {
            rvFriends.setVisibility(View.VISIBLE);
            tvNoFriends.setVisibility(View.GONE);
            
            // We need a click listener to add the friend
            // Since FriendsAdapter might not support click listener for this purpose, 
            // we might need to create a simple adapter or modify FriendsAdapter.
            // For simplicity, let's assume we can reuse FriendsAdapter but we need to handle clicks.
            // However, FriendsAdapter in FriendsFragment didn't seem to take a listener.
            // Let's check FriendsAdapter.java.
            
            // For now, I'll create a simple inline adapter or use a new one if FriendsAdapter is not suitable.
            // But to be safe and quick, I'll create a simple inner class adapter here or reuse ContactsAdapter if possible.
            // ContactsAdapter was used in AddFriendsActivity and has selection logic.
            
            // Let's use a simple anonymous adapter for now or just reuse FriendsAdapter if it allows clicks.
            // Actually, let's check FriendsAdapter first.
            
            rvFriends.setLayoutManager(new LinearLayoutManager(this));
            // Using a custom adapter for this specific task to ensure click functionality
            rvFriends.setAdapter(new GroupMemberAdapter(availableFriends, this::onFriendClick));
        }
    }

    private void onFriendClick(Contact friend) {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Add Member")
            .setMessage("Add " + friend.getName() + " to " + groupName + "?")
            .setPositiveButton("Add", (dialog, which) -> {
                addMemberToGroup(friend.getName());
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void addMemberToGroup(String memberName) {
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        Set<String> members = new HashSet<>(prefs.getStringSet("members_" + groupName, new HashSet<>()));
        members.add(memberName);
        prefs.edit().putStringSet("members_" + groupName, members).apply();
        
        Toast.makeText(this, memberName + " added to group", Toast.LENGTH_SHORT).show();
        finish(); // Go back to settings to see the updated list
    }
    
    // Simple Adapter for this activity
    private class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {
        private List<Contact> friends;
        private OnItemClickListener listener;

        public GroupMemberAdapter(List<Contact> friends, OnItemClickListener listener) {
            this.friends = friends;
            this.listener = listener;
        }

        @androidx.annotation.NonNull
        @Override
        public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
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

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(Contact contact);
    }
}
