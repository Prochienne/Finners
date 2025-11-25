package com.example.finners;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private List<Contact> friends;

    public FriendsAdapter(List<Contact> friends) {
        this.friends = friends;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Contact friend = friends.get(position);
        holder.name.setText(friend.getName());
        
        FriendsRepository repository = FriendsRepository.getInstance(holder.itemView.getContext());
        double balance = repository.getBalance(friend.getId());
        
        if (balance > 0) {
            holder.status.setText("owes you $" + String.format("%.2f", balance));
            holder.status.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
        } else if (balance < 0) {
            holder.status.setText("you owe $" + String.format("%.2f", Math.abs(balance)));
            holder.status.setTextColor(android.graphics.Color.parseColor("#F44336")); // Red
        } else {
            holder.status.setText("settled up");
            holder.status.setTextColor(android.graphics.Color.GRAY);
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), FriendDetailsActivity.class);
            intent.putExtra("FRIEND_NAME", friend.getName());
            intent.putExtra("FRIEND_ID", friend.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView status;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvFriendName);
            status = itemView.findViewById(R.id.tvFriendStatus);
        }
    }
}
