package com.example.finners;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class FriendsFragment extends Fragment {

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        Button btnAddFriends = view.findViewById(R.id.btnAddFriends);
        androidx.recyclerview.widget.RecyclerView rvFriends = view.findViewById(R.id.rvFriends);
        android.widget.TextView tvFriendsEmpty = view.findViewById(R.id.tvFriendsEmpty);

        btnAddFriends.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFriendsActivity.class);
            startActivity(intent);
        });

        FriendsRepository repository = FriendsRepository.getInstance(requireContext());
        java.util.List<Contact> friends = repository.getFriends();

        if (friends.isEmpty()) {
            rvFriends.setVisibility(View.GONE);
            tvFriendsEmpty.setVisibility(View.VISIBLE);
        } else {
            rvFriends.setVisibility(View.VISIBLE);
            tvFriendsEmpty.setVisibility(View.GONE);
            rvFriends.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
            rvFriends.setAdapter(new FriendsAdapter(friends));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh list on resume
        View view = getView();
        if (view != null) {
            androidx.recyclerview.widget.RecyclerView rvFriends = view.findViewById(R.id.rvFriends);
            android.widget.TextView tvFriendsEmpty = view.findViewById(R.id.tvFriendsEmpty);
            FriendsRepository repository = FriendsRepository.getInstance(requireContext());
            java.util.List<Contact> friends = repository.getFriends();

            if (friends.isEmpty()) {
                rvFriends.setVisibility(View.GONE);
                tvFriendsEmpty.setVisibility(View.VISIBLE);
            } else {
                rvFriends.setVisibility(View.VISIBLE);
                tvFriendsEmpty.setVisibility(View.GONE);
                rvFriends.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                rvFriends.setAdapter(new FriendsAdapter(friends));
            }
        }
    }
}
