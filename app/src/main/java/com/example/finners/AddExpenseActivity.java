package com.example.finners;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etSearch, etDescription, etAmount;
    private RecyclerView rvSearchResults;
    private Button btnPayer, btnCurrency;
    private List<String> selectedNames = new ArrayList<>();
    private List<String> allSearchableItems = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private String currentPayer = "You";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveExpense());

        etSearch = findViewById(R.id.etSearch);
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        btnPayer = findViewById(R.id.btnPayer);
        btnCurrency = findViewById(R.id.btnCurrency);

        // Setup Search RecyclerView
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(new ArrayList<>(), this::onItemSelected);
        rvSearchResults.setAdapter(searchAdapter);

        loadSearchableItems();

        etSearch.setOnClickListener(v -> {
            rvSearchResults.setVisibility(View.VISIBLE);
            searchAdapter.updateList(allSearchableItems);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnPayer.setOnClickListener(v -> showPayerSelectionDialog());
        
        btnCurrency.setOnClickListener(v -> {
            Toast.makeText(this, "Currency selection not implemented yet", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSearchableItems() {
        allSearchableItems.clear();
        // Load Groups
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        Set<String> groups = prefs.getStringSet("groups", new HashSet<>());
        allSearchableItems.addAll(groups);

        // Load Friends
        List<Contact> friends = FriendsRepository.getInstance(this).getFriends();
        for (Contact friend : friends) {
            allSearchableItems.add(friend.getName());
        }
        
        // Add dummy contacts if empty for testing
        if (allSearchableItems.isEmpty()) {
            allSearchableItems.add("Alice");
            allSearchableItems.add("Bob");
            allSearchableItems.add("Charlie");
        }
    }

    private void filterSearch(String query) {
        List<String> filteredList = new ArrayList<>();
        for (String item : allSearchableItems) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        searchAdapter.updateList(filteredList);
        rvSearchResults.setVisibility(View.VISIBLE);
    }

    private void onItemSelected(String name) {
        if (!selectedNames.contains(name)) {
            selectedNames.add(name);
            etSearch.setText(String.join(", ", selectedNames));
        }
        rvSearchResults.setVisibility(View.GONE);
    }

    private void showPayerSelectionDialog() {
        List<String> potentialPayers = new ArrayList<>();
        potentialPayers.add("You");
        potentialPayers.addAll(selectedNames);

        String[] payersArray = potentialPayers.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Select Payer")
                .setItems(payersArray, (dialog, which) -> {
                    currentPayer = payersArray[which];
                    btnPayer.setText(currentPayer.equals("You") ? "you" : currentPayer);
                })
                .show();
    }

    private void saveExpense() {
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int numberOfPeople = selectedNames.size() + 1; // +1 for "You"
        
        double splitAmount = amount / numberOfPeople;
        double youGetBack = amount - splitAmount;
        
        // Update balances
        FriendsRepository repository = FriendsRepository.getInstance(this);
        List<Contact> friends = repository.getFriends();
        
        for (String name : selectedNames) {
            for (Contact friend : friends) {
                if (friend.getName().equals(name)) {
                    // Assuming "You" paid, so friend owes you splitAmount
                    // If we support other payers later, this logic needs update
                    repository.updateBalance(friend.getId(), splitAmount);
                    break;
                }
            }
        }

        String logMessage = "You added \"" + description + "\"";
        String subMessage = "You get back " + String.format("%.2f", youGetBack);

        saveActivityLog(logMessage, subMessage);
        
        Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveActivityLog(String message, String subMessage) {
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String logsJson = prefs.getString("activity_logs", "[]");
        try {
            JSONArray jsonArray = new JSONArray(logsJson);
            // Append new message (ActivityFragment displays in reverse)
            // We need a way to store title and subtitle. 
            // Since existing logs are strings, I'll use a delimiter or JSON object.
            // But ActivityFragment expects strings.
            // I will format it as "Title|Subtitle" and update ActivityFragment to parse it.
            jsonArray.put(message + "|" + subMessage);
            prefs.edit().putString("activity_logs", jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Inner Adapter Class
    private static class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
        private List<String> items;
        private final OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(String item);
        }

        public SearchAdapter(List<String> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        public void updateList(List<String> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String item = items.get(position);
            holder.textView.setText(item);
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
