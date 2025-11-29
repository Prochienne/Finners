package com.example.finners;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {

    private List<JSONObject> expenses;

    public ExpensesAdapter(List<JSONObject> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject expense = expenses.get(position);
        try {
            String description = expense.optString("description", "Expense");
            double amount = expense.optDouble("amount", 0.0);
            String payer = expense.optString("payer", "You");
            long dateMillis = expense.optLong("date", System.currentTimeMillis());
            String currency = expense.optString("currency", "USD");

            holder.tvDescription.setText(description);
            
            String symbol = "$";
            if (currency.equals("EUR")) symbol = "€";
            else if (currency.equals("GBP")) symbol = "£";
            else if (currency.equals("INR")) symbol = "₹";
            else if (currency.equals("JPY")) symbol = "¥";
            
            holder.tvAmount.setText(String.format(Locale.getDefault(), "%s%.2f", symbol, amount));
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
            holder.tvDate.setText(sdf.format(dateMillis));

            if (payer.equals("You")) {
                holder.tvPayer.setText("You paid");
            } else {
                holder.tvPayer.setText(payer + " paid");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDescription, tvPayer, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPayer = itemView.findViewById(R.id.tvPayer);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
