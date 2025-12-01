package com.example.finners;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class AddExpenseActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private EditText etSearch, etDescription, etAmount;
    private RecyclerView rvSearchResults;
    private ChipGroup cgSelectedParticipants;
    private Button btnPayer, btnCurrency, btnSplit, btnDate;
    private ImageButton btnBillIcon, btnNote, btnReceipt;
    private List<String> selectedNames = new ArrayList<>();
    private List<String> allSearchableItems = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private String currentPayer = "You";
    
    // Metadata fields
    private long selectedDate = System.currentTimeMillis();
    private String expenseNote = "";
    private String receiptUri = "";
    private String selectedCategory = "General";
    private String selectedCurrency = "USD";
    private String selectedCurrencySymbol = "$"; // New field
    private String recurringInterval = "Never";
    private ImageButton btnRecurring;
    
    private enum SplitType {
        EQUALLY, UNEQUALLY, PERCENTAGES, SHARES
    }
    
    private SplitType currentSplitType = SplitType.EQUALLY;
    private Map<String, Double> splitWeights = new HashMap<>(); 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        try {
            db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            db = null; // Firebase not available
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveExpense());

        etSearch = findViewById(R.id.etSearch);
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        cgSelectedParticipants = findViewById(R.id.cgSelectedParticipants);
        btnPayer = findViewById(R.id.btnPayer);
        btnCurrency = findViewById(R.id.btnCurrency);
        
        // Load saved currency
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String savedCode = prefs.getString("user_currency_code", "USD");
        String savedSymbol = prefs.getString("user_currency_symbol", "$");
        selectedCurrency = savedCode;
        selectedCurrencySymbol = savedSymbol;
        btnCurrency.setText(savedSymbol);
        
        btnSplit = findViewById(R.id.btnSplit);
        
        btnBillIcon = findViewById(R.id.btnBillIcon);
        btnDate = findViewById(R.id.btnDate);
        btnNote = findViewById(R.id.btnNote);
        btnReceipt = findViewById(R.id.btnReceipt);

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
        
        btnCurrency.setOnClickListener(v -> showCurrencySelectionDialog());

        btnSplit.setOnClickListener(v -> showSplitSelectionDialog());
        
        // Metadata Listeners
        btnBillIcon.setOnClickListener(v -> showCategorySelectionDialog());
        btnDate.setOnClickListener(v -> showDatePickerDialog());
        btnNote.setOnClickListener(v -> showNoteDialog());
        btnReceipt.setOnClickListener(v -> showReceiptDialog());
        
        btnRecurring = findViewById(R.id.btnRecurring);
        btnRecurring.setOnClickListener(v -> showRecurrenceDialog());
        
        updateDateButton();
    }

    private void onItemSelected(String item) {
        if (!selectedNames.contains(item)) {
            selectedNames.add(item);
            addParticipantChip(item);
        } else {
            Toast.makeText(this, item + " is already added", Toast.LENGTH_SHORT).show();
        }
        etSearch.setText("");
        rvSearchResults.setVisibility(View.GONE);
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }

    private void addParticipantChip(String name) {
        Chip chip = new Chip(this);
        chip.setText(name);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            selectedNames.remove(name);
            cgSelectedParticipants.removeView(chip);
        });
        cgSelectedParticipants.addView(chip);
    }

    private void loadSearchableItems() {
        allSearchableItems.clear();
        FriendsRepository repository = FriendsRepository.getInstance(this);
        if (repository != null) {
            List<Contact> friends = repository.getFriends();
            for (Contact friend : friends) {
                allSearchableItems.add(friend.getName());
            }
        }
    }

    private void filterSearch(String query) {
        List<String> filtered = new ArrayList<>();
        if (query.isEmpty()) {
            filtered.addAll(allSearchableItems);
        } else {
            String lowerQuery = query.toLowerCase();
            for (String item : allSearchableItems) {
                if (item.toLowerCase().contains(lowerQuery)) {
                    filtered.add(item);
                }
            }
        }
        searchAdapter.updateList(filtered);
    }

    private void showPayerSelectionDialog() {
        List<String> participants = new ArrayList<>();
        participants.add("You");
        participants.addAll(selectedNames);
        
        String[] payers = participants.toArray(new String[0]);
        
        new AlertDialog.Builder(this)
            .setTitle("Select Payer")
            .setItems(payers, (dialog, which) -> {
                currentPayer = payers[which];
                btnPayer.setText("Paid by " + currentPayer);
            })
            .show();
    }

    private void showSplitSelectionDialog() {
        String[] splitTypes = {"Equally", "Unequally", "Percentages", "Shares"};
        new AlertDialog.Builder(this)
            .setTitle("How to split?")
            .setItems(splitTypes, (dialog, which) -> {
                switch (which) {
                    case 0:
                        currentSplitType = SplitType.EQUALLY;
                        btnSplit.setText("Split Equally");
                        break;
                    case 1:
                        currentSplitType = SplitType.UNEQUALLY;
                        btnSplit.setText("Split Unequally");
                        showCustomSplitDialog();
                        break;
                    case 2:
                        currentSplitType = SplitType.PERCENTAGES;
                        btnSplit.setText("Split by %");
                        showCustomSplitDialog();
                        break;
                    case 3:
                        currentSplitType = SplitType.SHARES;
                        btnSplit.setText("Split by Shares");
                        showCustomSplitDialog();
                        break;
                }
            })
            .show();
    }

    private void showCustomSplitDialog() {
        android.view.View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_split_adjustment, null);
        
        TextView tvSplitTitle = dialogView.findViewById(R.id.tvSplitTitle);
        TextView tvTotalSummary = dialogView.findViewById(R.id.tvTotalSummary);
        TextView tvRemaining = dialogView.findViewById(R.id.tvRemaining);
        RecyclerView rvSplitParticipants = dialogView.findViewById(R.id.rvSplitParticipants);
        
        List<String> participants = new ArrayList<>();
        participants.add("You");
        participants.addAll(selectedNames);
        
        // Initialize weights if empty or size mismatch
        if (splitWeights.isEmpty() || splitWeights.size() != participants.size()) {
            splitWeights.clear();
            if (currentSplitType == SplitType.UNEQUALLY) {
                double amount = 0;
                try {
                    amount = Double.parseDouble(etAmount.getText().toString());
                } catch (NumberFormatException e) {
                    amount = 0;
                }
                double split = amount / participants.size();
                for (String p : participants) splitWeights.put(p, split);
            } else if (currentSplitType == SplitType.PERCENTAGES) {
                double split = 100.0 / participants.size();
                for (String p : participants) splitWeights.put(p, split);
            } else if (currentSplitType == SplitType.SHARES) {
                for (String p : participants) splitWeights.put(p, 1.0);
            }
        }

        SplitAdjustmentAdapter adapter = new SplitAdjustmentAdapter(participants, splitWeights, currentSplitType, selectedCurrencySymbol, new SplitAdjustmentAdapter.OnSplitChangeListener() {
            @Override
            public void onSplitChanged() {
                updateSplitSummary(tvTotalSummary, tvRemaining, participants);
            }
        });
        
        rvSplitParticipants.setLayoutManager(new LinearLayoutManager(this));
        rvSplitParticipants.setAdapter(adapter);
        
        updateSplitSummary(tvTotalSummary, tvRemaining, participants);
        
        String title = "Adjust Split";
        if (currentSplitType == SplitType.UNEQUALLY) title = "Enter amounts";
        else if (currentSplitType == SplitType.PERCENTAGES) title = "Enter percentages";
        else if (currentSplitType == SplitType.SHARES) title = "Enter shares";
        
        tvSplitTitle.setText(title);

        new AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK", (dialog, which) -> {
                if (validateSplit(participants)) {
                    dialog.dismiss();
                } else {
                    Toast.makeText(AddExpenseActivity.this, "Split saved", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", (dialog, which) -> {
            })
            .show();
    }

    private void updateSplitSummary(TextView tvTotal, TextView tvRemaining, List<String> participants) {
        double total = 0;
        for (String p : participants) {
            total += splitWeights.getOrDefault(p, 0.0);
        }
        
        if (currentSplitType == SplitType.UNEQUALLY) {
            double expenseAmount = 0;
            try {
                expenseAmount = Double.parseDouble(etAmount.getText().toString());
            } catch (NumberFormatException e) {
                expenseAmount = 0;
            }
            tvTotal.setText(String.format(Locale.getDefault(), "Total: %s%.2f / %s%.2f", selectedCurrencySymbol, total, selectedCurrencySymbol, expenseAmount));
            double remaining = expenseAmount - total;
            tvRemaining.setText(String.format(Locale.getDefault(), "Remaining: %s%.2f", selectedCurrencySymbol, remaining));
            tvRemaining.setTextColor(Math.abs(remaining) < 0.01 ? 0xFF00AA00 : 0xFFFF0000); // Green if balanced, Red if not
            
        } else if (currentSplitType == SplitType.PERCENTAGES) {
            tvTotal.setText(String.format(Locale.getDefault(), "Total: %.1f%% / 100%%", total));
            double remaining = 100.0 - total;
            tvRemaining.setText(String.format(Locale.getDefault(), "Remaining: %.1f%%", remaining));
            tvRemaining.setTextColor(Math.abs(remaining) < 0.1 ? 0xFF00AA00 : 0xFFFF0000);
            
        } else if (currentSplitType == SplitType.SHARES) {
            tvTotal.setText(String.format(Locale.getDefault(), "Total Shares: %.1f", total));
            tvRemaining.setText("");
        }
    }

    private boolean validateSplit(List<String> participants) {
        double total = 0;
        for (String p : participants) {
            total += splitWeights.getOrDefault(p, 0.0);
        }

        if (currentSplitType == SplitType.UNEQUALLY) {
            double expenseAmount = 0;
            try {
                expenseAmount = Double.parseDouble(etAmount.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid expense amount", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (Math.abs(expenseAmount - total) > 0.01) {
                Toast.makeText(this, "Total split amount must match expense amount", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (currentSplitType == SplitType.PERCENTAGES) {
            if (Math.abs(100.0 - total) > 0.1) {
                Toast.makeText(this, "Percentages must add up to 100%", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        
        return true;
    }

    private static class SplitAdjustmentAdapter extends RecyclerView.Adapter<SplitAdjustmentAdapter.ViewHolder> {
        private List<String> participants;
        private Map<String, Double> weights;
        private SplitType type;
        private String currencySymbol;
        private OnSplitChangeListener listener;

        public interface OnSplitChangeListener {
            void onSplitChanged();
        }

        public SplitAdjustmentAdapter(List<String> participants, Map<String, Double> weights, SplitType type, String currencySymbol, OnSplitChangeListener listener) {
            this.participants = participants;
            this.weights = weights;
            this.type = type;
            this.currencySymbol = currencySymbol;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_split_participant, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String name = participants.get(position);
            holder.tvName.setText(name);
            
            double val = weights.getOrDefault(name, 0.0);
            if (type == SplitType.PERCENTAGES || type == SplitType.SHARES) {
                 // Show as int if it's a whole number for shares/percentages
                 if (val == (long) val) {
                     holder.etAmount.setText(String.format(Locale.getDefault(), "%d", (long)val));
                 } else {
                     holder.etAmount.setText(String.format(Locale.getDefault(), "%.2f", val));
                 }
            } else {
                holder.etAmount.setText(String.format(Locale.getDefault(), "%.2f", val));
            }

            if (type == SplitType.UNEQUALLY) holder.tvUnit.setText(currencySymbol);
            else if (type == SplitType.PERCENTAGES) holder.tvUnit.setText("%");
            else if (type == SplitType.SHARES) holder.tvUnit.setText("shares");
            
            holder.etAmount.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    try {
                        double newVal = Double.parseDouble(holder.etAmount.getText().toString());
                        weights.put(name, newVal);
                        listener.onSplitChanged();
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return participants.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvUnit;
            EditText etAmount;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvParticipantName);
                etAmount = itemView.findViewById(R.id.etSplitAmount);
                tvUnit = itemView.findViewById(R.id.tvSplitUnit);
            }
        }
    }

    private void showCategorySelectionDialog() {
        String[] categories = {"General", "Food", "Transport", "Entertainment", "Shopping", "Utilities"};
        new AlertDialog.Builder(this)
            .setTitle("Select Category")
            .setItems(categories, (dialog, which) -> {
                selectedCategory = categories[which];
                Toast.makeText(this, "Selected: " + selectedCategory, Toast.LENGTH_SHORT).show();
            })
            .show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, dayOfMonth);
            selectedDate = newDate.getTimeInMillis();
            updateDateButton();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        btnDate.setText(sdf.format(selectedDate));
    }

    private void showNoteDialog() {
        EditText input = new EditText(this);
        input.setText(expenseNote);
        new AlertDialog.Builder(this)
            .setTitle("Add Note")
            .setView(input)
            .setPositiveButton("OK", (dialog, which) -> {
                expenseNote = input.getText().toString();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showReceiptDialog() {
        Toast.makeText(this, "Receipt attachment not implemented", Toast.LENGTH_SHORT).show();
    }

    private void showCurrencySelectionDialog() {
        com.mynameismidori.currencypicker.CurrencyPicker picker = com.mynameismidori.currencypicker.CurrencyPicker.newInstance("Select Currency");
        picker.setListener(new com.mynameismidori.currencypicker.CurrencyPickerListener() {
            @Override
            public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                selectedCurrency = code;
                selectedCurrencySymbol = symbol;
                btnCurrency.setText(symbol);
                picker.dismiss();
            }
        });
        picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
    }

    private void saveExpense() {
        try {
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

            double amount = 0.0;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Calculate splits
            Map<String, Double> finalAmounts = new HashMap<>();
            List<String> participants = new ArrayList<>();
            participants.add("You");
            if (selectedNames != null) {
                participants.addAll(selectedNames);
            }

            if (currentSplitType == SplitType.EQUALLY) {
                double splitAmount = amount / participants.size();
                for (String p : participants) finalAmounts.put(p, splitAmount);
            } else if (currentSplitType == SplitType.UNEQUALLY) {
                if (splitWeights != null) {
                    finalAmounts.putAll(splitWeights);
                }
            } else if (currentSplitType == SplitType.PERCENTAGES) {
                for (String p : participants) {
                    double percent = (splitWeights != null) ? splitWeights.getOrDefault(p, 0.0) : 0.0;
                    finalAmounts.put(p, amount * (percent / 100.0));
                }
            } else if (currentSplitType == SplitType.SHARES) {
                double totalShares = 0;
                if (splitWeights != null) {
                    for (double s : splitWeights.values()) totalShares += s;
                }
                if (totalShares == 0) totalShares = 1; 
                
                for (String p : participants) {
                    double shares = (splitWeights != null) ? splitWeights.getOrDefault(p, 0.0) : 0.0;
                    finalAmounts.put(p, amount * (shares / totalShares));
                }
            }

            double youPaid = (currentPayer != null && currentPayer.equals("You")) ? amount : 0; 
            double youOwe = finalAmounts.getOrDefault("You", 0.0);
            double youGetBack = youPaid - youOwe;
            
            // Update balances
            FriendsRepository repository = FriendsRepository.getInstance(this);
            SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
            String defaultCurrency = prefs.getString("user_currency_code", "USD");
            
            if (repository != null) {
                List<Contact> friends = repository.getFriends();
                
                if (friends != null && selectedNames != null) {
                    for (String name : selectedNames) {
                        if (name == null) continue;
                        for (Contact friend : friends) {
                            if (friend != null && friend.getName() != null && friend.getName().equals(name)) {
                                double friendOwes = finalAmounts.getOrDefault(name, 0.0);
                                double youOweAmount = youOwe;
                                
                                // Convert amounts if expense currency differs from default
                                if (!selectedCurrency.equals(defaultCurrency)) {
                                    friendOwes = CurrencyConverter.convert(friendOwes, selectedCurrency, defaultCurrency);
                                    youOweAmount = CurrencyConverter.convert(youOwe, selectedCurrency, defaultCurrency);
                                }
                                
                                if (currentPayer != null && currentPayer.equals("You")) {
                                    repository.updateBalance(friend.getId(), friendOwes);
                                } else if (currentPayer != null && currentPayer.equals(name)) {
                                    repository.updateBalance(friend.getId(), -youOweAmount); 
                                }
                                break;
                            }
                        }
                    }
                }
            }

            String logMessage = "You added \"" + description + "\"";
            String subMessage;
            if (youGetBack > 0) {
                subMessage = "You get back " + String.format("%.2f", youGetBack);
            } else if (youGetBack < 0) {
                subMessage = "You owe " + String.format("%.2f", -youGetBack);
            } else {
                subMessage = "You are settled";
            }

            ActivityLogger.log(this, logMessage, subMessage);
            saveExpenseDetails(description, amount, finalAmounts);
            
            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Throwable e) {
            e.printStackTrace();
            try {
                ActivityLogger.log(this, "Error adding expense", e.getMessage());
                Toast.makeText(this, "Error adding expense: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                // Ignore secondary errors
            }
        }
    }


    
    private void saveExpenseDetails(String description, double amount, Map<String, Double> splits) {
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String expensesJson = prefs.getString("expenses_list", "[]");
        try {
            JSONArray jsonArray = new JSONArray(expensesJson);
            JSONObject expense = new JSONObject();
            expense.put("id", UUID.randomUUID().toString());
            expense.put("description", description);
            expense.put("amount", amount);
            expense.put("payer", currentPayer);
            expense.put("date", selectedDate);
            expense.put("category", selectedCategory);
            expense.put("note", expenseNote);
            expense.put("receipt", receiptUri);
            expense.put("currency", selectedCurrency);
            expense.put("recurring", recurringInterval);
            
            JSONObject splitsJson = new JSONObject();
            for (Map.Entry<String, Double> entry : splits.entrySet()) {
                splitsJson.put(entry.getKey(), entry.getValue());
            }
            expense.put("splits", splitsJson);
            
            jsonArray.put(expense);
            prefs.edit().putString("expenses_list", jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showRecurrenceDialog() {
        String[] intervals = {"Never", "Daily", "Weekly", "Bi-weekly", "Monthly", "Yearly"};
        new AlertDialog.Builder(this)
            .setTitle("Repeat expense")
            .setItems(intervals, (dialog, which) -> {
                recurringInterval = intervals[which];
                if (!recurringInterval.equals("Never")) {
                    Toast.makeText(this, "Repeats: " + recurringInterval, Toast.LENGTH_SHORT).show();
                    // Optionally change icon tint or something to indicate active
                }
            })
            .show();
    }

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
