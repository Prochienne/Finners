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
            Toast.makeText(this, "Remove friend clicked", Toast.LENGTH_SHORT).show();
        });

        Button btnBlockUser = findViewById(R.id.btnBlockUser);
        btnBlockUser.setOnClickListener(v -> {
            Toast.makeText(this, "Block user clicked", Toast.LENGTH_SHORT).show();
        });

        Button btnReportUser = findViewById(R.id.btnReportUser);
        btnReportUser.setOnClickListener(v -> {
            Toast.makeText(this, "Report user clicked", Toast.LENGTH_SHORT).show();
        });
    }
}
