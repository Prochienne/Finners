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
        btnAddFriends.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFriendsActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
