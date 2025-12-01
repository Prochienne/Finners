package com.example.finners;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.widget.LinearLayout;
import android.view.View;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

public class GroupDetailsActivity extends AppCompatActivity {

    private String groupName;
    private RecyclerView rvMembers;
    private TextView tvEmptyState;
    private MembersAdapter membersAdapter;
    private List<String> membersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        groupName = getIntent().getStringExtra("GROUP_NAME");
        if (groupName == null) {
            groupName = "Group";
        }

        TextView tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupName.setText(groupName);

        TextView tvGroupDates = findViewById(R.id.tvGroupDates);
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String dates = prefs.getString("group_dates_" + groupName, null);
        
        if (dates != null) {
            tvGroupDates.setText(dates);
            tvGroupDates.setVisibility(android.view.View.VISIBLE);
        } else {
            tvGroupDates.setVisibility(android.view.View.GONE);
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetailsActivity.this, GroupSettingsActivity.class);
            intent.putExtra("GROUP_NAME", groupName);
            startActivity(intent);
        });

        ImageButton btnAddMember = findViewById(R.id.btnAddMember);
        btnAddMember.setOnClickListener(v -> {
            showAddMemberDialog();
        });

        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetailsActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        LinearLayout actionsLayout = (LinearLayout) ((android.widget.HorizontalScrollView) findViewById(R.id.scrollViewActions)).getChildAt(0);
        Button btnBalances = (Button) actionsLayout.getChildAt(1); // Index 1 (0 is SettleUp)

        btnBalances.setOnClickListener(v -> showBalancesDialog());

        Button btnSettleUp = findViewById(R.id.btnSettleUp);
        btnSettleUp.setOnClickListener(v -> {
            Intent intent = new Intent(GroupDetailsActivity.this, SettleUpActivity.class);
            intent.putExtra("GROUP_NAME", groupName);
            startActivity(intent);
        });

        rvMembers = findViewById(R.id.rvMembers);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        membersAdapter = new MembersAdapter(membersList, this::onMemberClick);
        rvMembers.setAdapter(membersAdapter);
    }

    private void onMemberClick(String memberName) {
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String creator = prefs.getString("creator_" + groupName, "");
        
        // If "You" are the creator, allow removal. 
        // Note: "You" is the default value we saved in CreateGroupActivity.
        if ("You".equals(creator)) {
            new AlertDialog.Builder(this)
                .setTitle("Remove Member")
                .setMessage("Remove " + memberName + " from the group?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeMember(memberName);
                })
                .setNegativeButton("Cancel", null)
                .show();
        }
    }

    private void removeMember(String memberName) {
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        Set<String> members = new HashSet<>(prefs.getStringSet("members_" + groupName, new HashSet<>()));
        if (members.contains(memberName)) {
            members.remove(memberName);
            prefs.edit().putStringSet("members_" + groupName, members).apply();
            
            ActivityLogger.log(this, "You removed " + memberName + " from " + groupName);
            loadMembers(); // Refresh list
            Toast.makeText(this, memberName + " removed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMembers();
    }

    private void loadMembers() {
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        Set<String> membersSet = prefs.getStringSet("members_" + groupName, new HashSet<>());
        
        membersList.clear();
        // Add "You" as the first member? Or just other members?
        // "You're the only one here" implies "You" are there.
        // Usually groups show all members. Let's add "You" (current user) if not in list, or just assume "You" is implicit.
        // The prompt says "show the rest of the members there instead".
        // If I add "You", the list is never empty.
        // Let's list the *other* members stored in prefs.
        
        if (membersSet.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvMembers.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvMembers.setVisibility(View.VISIBLE);
            membersList.addAll(membersSet);
            membersAdapter.notifyDataSetChanged();
        }
    }

    private static class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
        private List<String> members;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(String member);
        }

        public MembersAdapter(List<String> members, OnItemClickListener listener) {
            this.members = members;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String member = members.get(position);
            holder.button.setText(member);
            holder.button.setOnClickListener(v -> listener.onItemClick(member));
        }

        @Override
        public int getItemCount() {
            return members.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            Button button;

            ViewHolder(View itemView) {
                super(itemView);
                button = itemView.findViewById(R.id.btnMember);
            }
        }
    }

    private void showBalancesDialog() {
        // Mock data for now, or fetch from repository
        FriendsRepository repo = FriendsRepository.getInstance(this);
        List<Contact> friends = repo.getFriends();
        StringBuilder sb = new StringBuilder();
        for (Contact friend : friends) {
            // Only show balances for members of this group
            if (membersList.contains(friend.getName())) {
                double balance = repo.getBalance(friend.getId());
                if (balance != 0) {
                    if (balance > 0) sb.append(friend.getName()).append(" owes you $").append(String.format("%.2f", balance)).append("\n");
                    else sb.append("You owe ").append(friend.getName()).append(" $").append(String.format("%.2f", -balance)).append("\n");
                }
            }
        }
        if (sb.length() == 0) sb.append("All settled up!");
        
        new AlertDialog.Builder(this)
            .setTitle("Group Balances")
            .setMessage(sb.toString())
            .setPositiveButton("OK", null)
            .show();
    }



    private void showAddMemberDialog() {
        FriendsRepository repo = FriendsRepository.getInstance(this);
        List<Contact> allFriends = repo.getFriends();
        List<Contact> availableFriends = new ArrayList<>();
        
        for (Contact friend : allFriends) {
            if (!membersList.contains(friend.getName())) {
                availableFriends.add(friend);
            }
        }

        if (availableFriends.isEmpty()) {
            Toast.makeText(this, "No new friends to add", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] friendNames = new String[availableFriends.size()];
        for (int i = 0; i < availableFriends.size(); i++) {
            friendNames[i] = availableFriends.get(i).getName();
        }

        new AlertDialog.Builder(this)
            .setTitle("Add Member")
            .setItems(friendNames, (dialog, which) -> {
                Contact selectedFriend = availableFriends.get(which);
                addMember(selectedFriend.getName());
            })
            .show();
    }

    private void addMember(String memberName) {
        android.content.SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        Set<String> members = new HashSet<>(prefs.getStringSet("members_" + groupName, new HashSet<>()));
        members.add(memberName);
        prefs.edit().putStringSet("members_" + groupName, members).apply();
        
        ActivityLogger.log(this, "You added " + memberName + " to " + groupName);
        loadMembers();
        Toast.makeText(this, memberName + " added", Toast.LENGTH_SHORT).show();
    }
}
