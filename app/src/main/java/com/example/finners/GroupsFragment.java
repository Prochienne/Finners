package com.example.finners;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GroupsFragment extends Fragment {

    private String userName;

    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance(String userName) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString("USER_NAME", userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString("USER_NAME");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView welcomeText = view.findViewById(R.id.tvWelcomeHome);
        if (userName != null) {
            welcomeText.setText("Welcome to Finners, " + userName + "!");
        }

        view.findViewById(R.id.btnStartNewGroup).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), CreateGroupActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btnAddExpense).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), AddExpenseActivity.class);
            startActivity(intent);
        });
        
        view.findViewById(R.id.btnNonGroupExpenses).setOnClickListener(v -> {
            // Navigate to non-group expenses or show a toast for now
            // Assuming it might just filter expenses or show a specific list
             android.widget.Toast.makeText(getContext(), "Non-group expenses clicked", android.widget.Toast.LENGTH_SHORT).show();
        });
        
        loadGroups(view);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            loadGroups(getView());
        }
    }
    
    private void loadGroups(View view) {
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("FinnerPrefs", android.content.Context.MODE_PRIVATE);
        java.util.Set<String> groups = prefs.getStringSet("groups", new java.util.HashSet<>());
        
        TextView tvEmptyState = view.findViewById(R.id.tvEmptyState);
        View scrollViewGroups = view.findViewById(R.id.scrollViewGroups);
        android.widget.LinearLayout layoutGroups = view.findViewById(R.id.layoutGroups);
        
        // Clear existing buttons
        layoutGroups.removeAllViews();
        
        if (groups.isEmpty()) {
            // Show empty state
            tvEmptyState.setVisibility(View.VISIBLE);
            scrollViewGroups.setVisibility(View.GONE);
        } else {
            // Hide empty state and show groups
            tvEmptyState.setVisibility(View.GONE);
            scrollViewGroups.setVisibility(View.VISIBLE);
            
            // Create button for each group
            for (String groupName : groups) {
                android.widget.Button groupButton = new android.widget.Button(requireContext());
                android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 16);
                groupButton.setLayoutParams(params);
                groupButton.setText(groupName);
                groupButton.setTextSize(16);
                groupButton.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(requireContext(), GroupDetailsActivity.class);
                    intent.putExtra("GROUP_NAME", groupName);
                    startActivity(intent);
                });
                layoutGroups.addView(groupButton);
            }
        }
    }
}
