package com.example.finners;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;

public class SettleUpActivity extends AppCompatActivity {

    private TextView tvSettleMessage, tvCurrentBalance;
    private EditText etAmount;
    private String friendId;
    private String friendName;
    private double currentBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_up);

        ImageButton btnBack = findViewById(R.id.btnBack);
        tvSettleMessage = findViewById(R.id.tvSettleMessage);
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance);
        etAmount = findViewById(R.id.etAmount);
        Button btnSave = findViewById(R.id.btnSave);

        friendId = getIntent().getStringExtra("FRIEND_ID");
        friendName = getIntent().getStringExtra("FRIEND_NAME");

        if (friendId == null) {
            // No friend passed, allow selection
            tvSettleMessage.setText("Select a friend to settle with");
            tvSettleMessage.setOnClickListener(v -> showFriendSelectionDialog());
            // Auto-show dialog if entering without friend
            showFriendSelectionDialog();
        } else {
            tvSettleMessage.setText("Settle up with " + friendName);
            loadBalance();
        }

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveSettlement());
    }

    private void showFriendSelectionDialog() {
        FriendsRepository repository = FriendsRepository.getInstance(this);
        java.util.List<Contact> friends = repository.getFriends();
        
        if (friends.isEmpty()) {
            Toast.makeText(this, "No friends to settle with", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] friendNames = new String[friends.size()];
        for (int i = 0; i < friends.size(); i++) {
            friendNames[i] = friends.get(i).getName();
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Friend")
                .setItems(friendNames, (dialog, which) -> {
                    Contact selectedFriend = friends.get(which);
                    friendId = selectedFriend.getId();
                    friendName = selectedFriend.getName();
                    tvSettleMessage.setText("Settle up with " + friendName);
                    loadBalance();
                })
                .show();
    }

    private void loadBalance() {
        if (friendId == null) return;
        
        FriendsRepository repository = FriendsRepository.getInstance(this);
        currentBalance = repository.getBalance(friendId);
        
        String balanceText;
        if (currentBalance > 0) {
            balanceText = friendName + " owes you $" + String.format("%.2f", currentBalance);
        } else if (currentBalance < 0) {
            balanceText = "You owe " + friendName + " $" + String.format("%.2f", Math.abs(currentBalance));
        } else {
            balanceText = "You are all settled up";
        }
        tvCurrentBalance.setText(balanceText);
        
        // Pre-fill amount with absolute value of current balance
        if (currentBalance != 0) {
            etAmount.setText(String.format("%.2f", Math.abs(currentBalance)));
        } else {
            etAmount.setText("");
        }
    }

    private void saveSettlement() {
        if (friendId == null) {
            Toast.makeText(this, "Please select a friend first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        FriendsRepository repository = FriendsRepository.getInstance(this);
        
        // Determine direction based on current balance
        // If balance > 0 (they owe you), settlement reduces it (they paid you)
        // If balance < 0 (you owe them), settlement increases it (you paid them)
        // If balance is 0, assume you paid them? Or disable? 
        // For now, let's assume we are clearing the debt.
        
        double adjustment = 0;
        String logMessage = "";
        
        if (currentBalance > 0) {
            // They owe you, so they are paying you
            adjustment = -amount;
            logMessage = friendName + " paid you $" + String.format("%.2f", amount);
        } else {
            // You owe them (or 0), so you are paying them
            adjustment = amount; // Adding positive amount to negative balance moves it towards 0
            logMessage = "You paid " + friendName + " $" + String.format("%.2f", amount);
        }

        repository.updateBalance(friendId, adjustment);
        saveActivityLog(logMessage);
        
        Toast.makeText(this, "Settled up!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveActivityLog(String message) {
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String logsJson = prefs.getString("activity_logs", "[]");
        try {
            JSONArray jsonArray = new JSONArray(logsJson);
            jsonArray.put(message); // Simple message for settlement
            prefs.edit().putString("activity_logs", jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
