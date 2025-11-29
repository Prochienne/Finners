package com.example.finners;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FriendSettingsActivity extends AppCompatActivity {

    private String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_settings);

        friendName = getIntent().getStringExtra("FRIEND_NAME");
        if (friendName == null) {
            friendName = "Friend";
        }

        TextView tvFriendName = findViewById(R.id.tvFriendName);
        tvFriendName.setText(friendName);

        TextView tvSharedGroupsStatus = findViewById(R.id.tvSharedGroupsStatus);
        tvSharedGroupsStatus.setText("You and " + friendName + " do not share any groups.");

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnRemoveFriend = findViewById(R.id.btnRemoveFriend);
        btnRemoveFriend.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Remove Friend")
                .setMessage("Are you sure you want to remove " + friendName + " from your friends list?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeFriend();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
    }

    private void removeFriend() {
        FriendsRepository repository = FriendsRepository.getInstance(this);
        String friendId = getIntent().getStringExtra("FRIEND_ID");
        
        java.util.List<Contact> friends = repository.getFriends();
        Contact friendToRemove = null;
        
        for (Contact friend : friends) {
            if (friendId != null && friend.getId().equals(friendId)) {
                friendToRemove = friend;
                break;
            } else if (friend.getName().equals(friendName)) {
                // Fallback to name if ID is null (though it shouldn't be with updated caller)
                friendToRemove = friend;
                break;
            }
        }
        
        if (friendToRemove != null) {
            repository.removeFriend(friendToRemove);
            Toast.makeText(this, "Friend removed", Toast.LENGTH_SHORT).show();
            
            // Navigate back to Home (Friends tab)
            android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error: Friend not found", Toast.LENGTH_SHORT).show();
        }
    }
}
