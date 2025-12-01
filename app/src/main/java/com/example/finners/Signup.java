package com.example.finners;

import com.hbb20.CountryCodePicker;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        CountryCodePicker ccp = findViewById(R.id.ccp);
        EditText phone = findViewById(R.id.editTextPhone);
        ccp.registerCarrierNumberEditText(phone);
        Button done = findViewById(R.id.done);
        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        View number = findViewById(R.id.number);
        EditText pass = findViewById(R.id.pass);
        View password = findViewById(R.id.password);
        View emails = findViewById(R.id.emails);
        TextView welcome = findViewById(R.id.welcome);
        TextView create = findViewById(R.id.create);
        TextView currencyText = findViewById(R.id.tvCurrency);
        View box = findViewById(R.id.box);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    welcome.setVisibility(View.GONE);
                    create.setVisibility(View.GONE);
                    email.setVisibility(View.VISIBLE);
                    pass.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    emails.setVisibility(View.VISIBLE);
                    ccp.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    currencyText.setVisibility(View.VISIBLE);
                    number.setVisibility(View.VISIBLE);
                    box.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        currencyText.setOnClickListener(v -> {
            CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");
            picker.setListener((name1, code, symbol, flagDrawableResID) -> {
                currencyText.setText("I use " + code + " as my currency. Change »");
                
                // Save to SharedPreferences
                getSharedPreferences("FinnerPrefs", MODE_PRIVATE)
                        .edit()
                        .putString("user_currency_code", code)
                        .putString("user_currency_symbol", symbol)
                        .apply();
            });
            picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
        });

        done.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = pass.getText().toString().trim();
            String nameText = name.getText().toString().trim();
            String phoneText = phone.getText().toString().trim();

            // Validation Logic
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(Signup.this, "Invalid email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (passwordText.length() < 8) {
                Toast.makeText(Signup.this, "Password must be atleast 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Phone validation: exactly 9 digits (ignoring spaces)
            String cleanPhone = phoneText.replace(" ", "");
            if (cleanPhone.length() != 9 || !cleanPhone.matches("\\d+")) {
                Toast.makeText(Signup.this, "Enter correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(Signup.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(Signup.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nameText)
                                        .build();
                                user.updateProfile(profileUpdates);
                            }
                            
                            // Save name to SharedPreferences as well
                            getSharedPreferences("FinnerPrefs", MODE_PRIVATE)
                                    .edit()
                                    .putString("user_name", nameText)
                                    .apply();

                            Intent i = new Intent(Signup.this, Into.class);
                            i.putExtra("USER_NAME", nameText);
                            i.putExtra("USER_EMAIL", emailText);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(Signup.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            Intent i = new Intent(Signup.this, MainActivity.class);
            startActivity(i);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}