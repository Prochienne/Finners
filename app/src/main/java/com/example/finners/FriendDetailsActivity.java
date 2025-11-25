package com.example.finners;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FriendDetailsActivity extends AppCompatActivity {

    private String friendId;
    private String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);

        friendName = getIntent().getStringExtra("FRIEND_NAME");
        friendId = getIntent().getStringExtra("FRIEND_ID");
        
        if (friendName == null) {
            friendName = "Friend";
        }

        TextView tvFriendName = findViewById(R.id.tvFriendName);
        tvFriendName.setText(friendName);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(FriendDetailsActivity.this, FriendSettingsActivity.class);
            intent.putExtra("FRIEND_NAME", friendName);
            startActivity(intent);
        });

        Button btnSettleUp = findViewById(R.id.btnSettleUp);
        btnSettleUp.setOnClickListener(v -> {
            Intent intent = new Intent(FriendDetailsActivity.this, SettleUpActivity.class);
            intent.putExtra("FRIEND_ID", friendId);
            intent.putExtra("FRIEND_NAME", friendName);
            startActivity(intent);
        });

        Button btnRemind = findViewById(R.id.btnRemind);
        btnRemind.setOnClickListener(v -> {
            Toast.makeText(this, "Remind clicked", Toast.LENGTH_SHORT).show();
        });

        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(FriendDetailsActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Ideally show balance here too, but layout doesn't have a TextView for it yet.
        // For now, Settle Up button works.
    }
}
