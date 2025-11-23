package com.example.finners;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.IntroViewHolder> {

    private String userName;
    private String userEmail;

    public Adapter(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public Adapter() {
        this.userName = "User";
        this.userEmail = "user@example.com";
    }

    @NonNull
    @Override
    public IntroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 1. Pick the layout file based on the viewType (slide number)
        int layoutId = R.layout.welcome;
        if (viewType == 1) layoutId = R.layout.expense;
        if (viewType == 2) layoutId = R.layout.settle;
        if (viewType == 3) layoutId = R.layout.get_started;

        // 2. Create the view (This replaces setContentView in an adapter)
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new IntroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntroViewHolder holder, int position) {
        if (position == 0) {
            android.widget.TextView welcomeText = holder.itemView.findViewById(R.id.textView2);
            if (welcomeText != null && userName != null) {
                String firstName = userName.split(" ")[0];
                welcomeText.setText("Welcome to Finners, " + firstName);
            }
        } else if (position == 3) {
            View skipSetup = holder.itemView.findViewById(R.id.tvSkipSetup);
            if (skipSetup != null) {
                skipSetup.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(v.getContext(), HomeActivity.class);
                    intent.putExtra("USER_NAME", userName);
                    intent.putExtra("USER_EMAIL", userEmail);
                    v.getContext().startActivity(intent);
                });
            }
            
            View addGroup = holder.itemView.findViewById(R.id.btnAddGroup);
            if (addGroup != null) {
                addGroup.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(v.getContext(), CreateGroupActivity.class);
                    v.getContext().startActivity(intent);
                });
            }
        }
    }

    @Override
    public int getItemCount() { return 4; } // 4 Slides

    @Override
    public int getItemViewType(int position) { return position; }

    public static class IntroViewHolder extends RecyclerView.ViewHolder {
        public IntroViewHolder(@NonNull View itemView) { super(itemView); }
    }
}