package com.example.finners;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
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
            intent.putExtra("FRIEND_ID", friendId);
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
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey " + friendName + ", just a reminder to settle up on Finners!");
            startActivity(Intent.createChooser(shareIntent, "Send reminder via"));
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
        updateBalanceDisplay();
    }

    private void updateBalanceDisplay() {
        if (friendId == null) return;

        FriendsRepository repository = FriendsRepository.getInstance(this);
        double balance = repository.getBalance(friendId);
        
        TextView tvNoExpensesSmall = findViewById(R.id.tvNoExpensesSmall);
        
        if (balance > 0) {
            tvNoExpensesSmall.setText("owes you $" + String.format("%.2f", balance));
            tvNoExpensesSmall.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
        } else if (balance < 0) {
            tvNoExpensesSmall.setText("you owe $" + String.format("%.2f", Math.abs(balance)));
            tvNoExpensesSmall.setTextColor(android.graphics.Color.parseColor("#F44336")); // Red
        } else {
            tvNoExpensesSmall.setText("settled up");
            tvNoExpensesSmall.setTextColor(android.graphics.Color.GRAY);
        }
    }
}
