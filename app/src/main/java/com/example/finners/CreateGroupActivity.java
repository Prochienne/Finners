package com.example.finners;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;
import android.widget.LinearLayout;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText etGroupName, etStartDate, etEndDate;
    private Button btnTypeTrip, btnTypeHome, btnTypeCouple, btnTypeOther;
    private LinearLayout layoutTripDetails, layoutDatesInput;
    private Switch switchTripDates;
    private String selectedType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        ImageButton btnClose = findViewById(R.id.btnClose);
        TextView tvDone = findViewById(R.id.tvDone);
        etGroupName = findViewById(R.id.etGroupName);
        btnTypeTrip = findViewById(R.id.btnTypeTrip);
        btnTypeHome = findViewById(R.id.btnTypeHome);
        btnTypeCouple = findViewById(R.id.btnTypeCouple);
        btnTypeOther = findViewById(R.id.btnTypeOther);
        
        layoutTripDetails = findViewById(R.id.layoutTripDetails);
        layoutDatesInput = findViewById(R.id.layoutDatesInput);
        switchTripDates = findViewById(R.id.switchTripDates);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);

        btnClose.setOnClickListener(v -> finish());

        tvDone.setOnClickListener(v -> {
            String groupName = etGroupName.getText().toString().trim();
            if (groupName.isEmpty()) {
                Toast.makeText(this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedType.isEmpty()) {
                Toast.makeText(this, "Please select a group type", Toast.LENGTH_SHORT).show();
                return;
            }
            // Save group to SharedPreferences
            saveGroup(groupName);
            Toast.makeText(this, "Group '" + groupName + "' created!", Toast.LENGTH_SHORT).show();
            finish();
        });

        View.OnClickListener typeClickListener = v -> {
            resetTypeButtons();
            v.setSelected(true);
            if (v.getId() == R.id.btnTypeTrip) {
                selectedType = "Trip";
                layoutTripDetails.setVisibility(View.VISIBLE);
            } else {
                layoutTripDetails.setVisibility(View.GONE);
                if (v.getId() == R.id.btnTypeHome) selectedType = "Home";
                else if (v.getId() == R.id.btnTypeCouple) selectedType = "Couple";
                else if (v.getId() == R.id.btnTypeOther) selectedType = "Other";
            }
        };

        btnTypeTrip.setOnClickListener(typeClickListener);
        btnTypeHome.setOnClickListener(typeClickListener);
        btnTypeCouple.setOnClickListener(typeClickListener);
        btnTypeOther.setOnClickListener(typeClickListener);

        switchTripDates.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutDatesInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));

        // Handle Intent Extras for Pre-selection
        String intentGroupType = getIntent().getStringExtra("GROUP_TYPE");
        if (intentGroupType != null) {
            if (intentGroupType.equalsIgnoreCase("Trip")) {
                btnTypeTrip.performClick();
            } else if (intentGroupType.equalsIgnoreCase("Home")) {
                btnTypeHome.performClick();
            } else if (intentGroupType.equalsIgnoreCase("Couple")) {
                btnTypeCouple.performClick();
            } else if (intentGroupType.equalsIgnoreCase("Other")) {
                btnTypeOther.performClick();
            }
        }
    }



    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    editText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void resetTypeButtons() {
        btnTypeTrip.setSelected(false);
        btnTypeHome.setSelected(false);
        btnTypeCouple.setSelected(false);
        btnTypeOther.setSelected(false);
    }

    private void saveGroup(String groupName) {
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        Set<String> groups = prefs.getStringSet("groups", new HashSet<>());
        
        // Create a new HashSet because the one from getStringSet might be immutable
        Set<String> updatedGroups = new HashSet<>(groups);
        updatedGroups.add(groupName);
        
        prefs.edit().putStringSet("groups", updatedGroups).apply();

        if ("Trip".equals(selectedType) && switchTripDates.isChecked()) {
            String startDate = etStartDate.getText().toString();
            String endDate = etEndDate.getText().toString();
            if (!startDate.isEmpty() && !endDate.isEmpty()) {
                prefs.edit().putString("group_dates_" + groupName, startDate + " - " + endDate).apply();
            }
        }
        
        // Save activity log
        saveActivityLog("You created the group \"" + groupName + "\".");
    }

    private void saveActivityLog(String message) {
        SharedPreferences prefs = getSharedPreferences("FinnerPrefs", MODE_PRIVATE);
        String logsJson = prefs.getString("activity_logs", "[]");
        try {
            JSONArray jsonArray = new JSONArray(logsJson);
            // Add new message at the end
            jsonArray.put(message);
            prefs.edit().putString("activity_logs", jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
