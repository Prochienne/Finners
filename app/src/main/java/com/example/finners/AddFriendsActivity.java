package com.example.finners;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class AddFriendsActivity extends AppCompatActivity {

    private static final int READ_CONTACTS_PERMISSION_REQUEST = 101;
    private RecyclerView rvContacts;
    private ContactsAdapter adapter;
    private List<Contact> contactList;
    private LinearLayout layoutSelectedContacts;
    private View hsvSelectedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        ImageButton btnBack = findViewById(R.id.btnBack);
        rvContacts = findViewById(R.id.rvContacts);
        layoutSelectedContacts = findViewById(R.id.layoutSelectedContacts);
        hsvSelectedContacts = findViewById(R.id.hsvSelectedContacts);

        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            ArrayList<Contact> selectedContacts = new ArrayList<>();
            for (Contact contact : contactList) {
                if (contact.isSelected()) {
                    selectedContacts.add(contact);
                }
            }
            
            if (selectedContacts.isEmpty()) {
                Toast.makeText(this, "No contacts selected", Toast.LENGTH_SHORT).show();
                return;
            }

            new android.app.AlertDialog.Builder(this)
                .setTitle("Add Friends")
                .setMessage("Add " + selectedContacts.size() + " friends?")
                .setPositiveButton("Add", (dialog, which) -> {
                    FriendsRepository repository = FriendsRepository.getInstance(this);
                    for (Contact contact : selectedContacts) {
                        repository.addFriend(contact);
                    }
                    String message = "You added " + selectedContacts.size() + " new friend" + (selectedContacts.size() > 1 ? "s" : "");
                    ActivityLogger.log(this, message);
                    
                    android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();
        adapter = new ContactsAdapter(contactList, this::onContactClick);
        rvContacts.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST);
        } else {
            loadContacts();
        }

        android.widget.EditText etSearchFriend = findViewById(R.id.etSearchFriend);
        etSearchFriend.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        List<Contact> filteredList = new ArrayList<>();
        for (Contact item : contactList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void loadContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cursor != null) {
            Set<String> seenContactIds = new HashSet<>();
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                if (idIndex == -1 || nameIndex == -1 || numberIndex == -1) {
                    continue;
                }

                String id = cursor.getString(idIndex);
                if (seenContactIds.contains(id)) {
                    continue;
                }
                seenContactIds.add(id);
                
                String name = cursor.getString(nameIndex);
                String phoneNumber = cursor.getString(numberIndex);
                contactList.add(new Contact(id, name, phoneNumber));
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }

    private void onContactClick(Contact contact) {
        if (!contact.isSelected()) {
            contact.setSelected(true);
            addSelectedContactView(contact);
        } else {
            contact.setSelected(false);
            removeSelectedContactView(contact);
        }
        adapter.notifyDataSetChanged();
        updateSelectedContactsVisibility();
    }

    private void addSelectedContactView(Contact contact) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_selected_contact, layoutSelectedContacts, false);
        TextView tvName = view.findViewById(R.id.tvName);
        View btnRemove = view.findViewById(R.id.btnRemove);

        tvName.setText(contact.getName());
        view.setTag(contact.getId());

        btnRemove.setOnClickListener(v -> {
            contact.setSelected(false);
            layoutSelectedContacts.removeView(view);
            adapter.notifyDataSetChanged();
            updateSelectedContactsVisibility();
        });

        layoutSelectedContacts.addView(view);
    }

    private void removeSelectedContactView(Contact contact) {
        for (int i = 0; i < layoutSelectedContacts.getChildCount(); i++) {
            View view = layoutSelectedContacts.getChildAt(i);
            if (view.getTag().equals(contact.getId())) {
                layoutSelectedContacts.removeView(view);
                break;
            }
        }
    }

    private void updateSelectedContactsVisibility() {
        boolean hasSelected = layoutSelectedContacts.getChildCount() > 0;
        hsvSelectedContacts.setVisibility(hasSelected ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnNext).setVisibility(hasSelected ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
            } else {
                Toast.makeText(this, "Permission required to load contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
