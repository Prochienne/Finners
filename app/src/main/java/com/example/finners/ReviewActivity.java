package com.example.finners;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView rvSelectedContacts;
    private ContactsAdapter adapter;
    private List<Contact> selectedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ImageButton btnBack = findViewById(R.id.btnBack);
        rvSelectedContacts = findViewById(R.id.rvSelectedContacts);
        Button btnAddFriends = findViewById(R.id.btnAddFriends);

        btnBack.setOnClickListener(v -> finish());

        selectedContacts = (ArrayList<Contact>) getIntent().getSerializableExtra("selected_contacts");
        if (selectedContacts == null) {
            selectedContacts = new ArrayList<>();
        }

        rvSelectedContacts.setLayoutManager(new LinearLayoutManager(this));
        // Reuse ContactsAdapter for display, but disable click listener or handle differently if needed
        adapter = new ContactsAdapter(selectedContacts, contact -> {
            // Optional: Handle click in review screen if needed, e.g., to remove
        });
        rvSelectedContacts.setAdapter(adapter);

        btnAddFriends.setOnClickListener(v -> {
            FriendsRepository repository = FriendsRepository.getInstance(this);
            for (Contact contact : selectedContacts) {
                repository.addFriend(contact);
            }
            // Navigate back to FriendsFragment (MainActivity)
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
