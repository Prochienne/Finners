package com.example.finners;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private String userName;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userName = getIntent().getStringExtra("USER_NAME");
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, GroupsFragment.newInstance(userName))
                    .commit();
        }

        findViewById(R.id.nav_groups).setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, GroupsFragment.newInstance(userName))
                    .commit();
        });

        findViewById(R.id.nav_friends).setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FriendsFragment())
                    .commit();
        });

        findViewById(R.id.nav_activity).setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ActivityFragment())
                    .commit();
        });

        findViewById(R.id.nav_account).setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, AccountFragment.newInstance(userName, userEmail))
                    .commit();
        });
    }
}
