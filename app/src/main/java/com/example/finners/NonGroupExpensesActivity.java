package com.example.finners;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NonGroupExpensesActivity extends AppCompatActivity {

    private RecyclerView rvExpenses;
    private TextView tvEmptyState;
    private ExpensesAdapter adapter;
    private List<JSONObject> expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_group_expenses);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvExpenses = findViewById(R.id.rvExpenses);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        expenseList = new ArrayList<>();
        adapter = new ExpensesAdapter(expenseList);
        rvExpenses.setAdapter(adapter);

        loadNonGroupExpenses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNonGroupExpenses();
    }

    private void loadNonGroupExpenses() {
        expenseList.clear();
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String expensesJson = prefs.getString("expenses_list", "[]");

        try {
            JSONArray jsonArray = new JSONArray(expensesJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject expense = jsonArray.getJSONObject(i);
                // Check if group_id is missing or empty
                if (!expense.has("group_id") || expense.getString("group_id").isEmpty()) {
                    expenseList.add(expense);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (expenseList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvExpenses.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvExpenses.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
}
