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

        if (userName == null || userEmail == null) {
            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                userName = user.getDisplayName();
                userEmail = user.getEmail();
            }
        }

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, GroupsFragment.newInstance(userName))
                    .commit();
        }

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_groups) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, GroupsFragment.newInstance(userName))
                        .commit();
                return true;
            } else if (itemId == R.id.nav_friends) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FriendsFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_activity) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ActivityFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_account) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, AccountFragment.newInstance(userName, userEmail))
                        .commit();
                return true;
            }
            return false;
        });
    }
}
