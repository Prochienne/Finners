package com.example.finners;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    private String userName;
    private String userEmail;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(String userName, String userEmail) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString("USER_NAME", userName);
        args.putString("USER_EMAIL", userEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString("USER_NAME");
            userEmail = getArguments().getString("USER_EMAIL");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView nameText = view.findViewById(R.id.tvUserName);
        TextView emailText = view.findViewById(R.id.tvUserEmail);

        if (userName != null) {
            nameText.setText(userName);
        }
        if (userEmail != null) {
            emailText.setText(userEmail);
        }
    }
}
