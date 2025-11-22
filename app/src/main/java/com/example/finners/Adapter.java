package com.example.finners;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.IntroViewHolder> {

    @NonNull
    @Override
    public IntroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 1. Pick the layout file based on the viewType (slide number)
        int layoutId = R.layout.welcome;
        if (viewType == 1) layoutId = R.layout.expense;
        if (viewType == 2) layoutId = R.layout.settle;

        // 2. Create the view (This replaces setContentView in an adapter)
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new IntroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntroViewHolder holder, int position) { }

    @Override
    public int getItemCount() { return 3; } // 3 Slides

    @Override
    public int getItemViewType(int position) { return position; }

    class IntroViewHolder extends RecyclerView.ViewHolder {
        public IntroViewHolder(@NonNull View itemView) { super(itemView); }
    }
}