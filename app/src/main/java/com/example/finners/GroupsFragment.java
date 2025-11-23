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

        view.findViewById(R.id.btnAddHousehold).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), CreateGroupActivity.class);
            intent.putExtra("GROUP_TYPE", "Home");
            startActivity(intent);
        });

        view.findViewById(R.id.btnAddTrip).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), CreateGroupActivity.class);
            intent.putExtra("GROUP_TYPE", "Trip");
            startActivity(intent);
        });
    }
}
