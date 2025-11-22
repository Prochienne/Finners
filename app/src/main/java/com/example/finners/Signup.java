package com.example.finners;
import com.hbb20.CountryCodePicker;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        CountryCodePicker ccp = findViewById(R.id.ccp);
        EditText phone = findViewById(R.id.editTextPhone);
        ccp.registerCarrierNumberEditText(phone);
        Button done=findViewById(R.id.done);
        EditText name = findViewById(R.id.name);
        View email = findViewById(R.id.email);
        View number=findViewById(R.id.number);
        View pass = findViewById(R.id.pass);
        View password = findViewById(R.id.password);
        View emails = findViewById(R.id.emails);
        View box = findViewById(R.id.box);
        TextView welcome = findViewById(R.id.welcome);
        TextView create = findViewById(R.id.create);
        TextView currencyText = findViewById(R.id.tvCurrency);
        CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");
        picker.setListener(new CurrencyPickerListener() {
            @Override
            public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                currencyText.setText("I use " + code + " (" + symbol + ") as my currency. Change »");
                picker.dismiss();
            }
        });
        currencyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
            }
        });
        name.addTextChangedListener(new TextWatcher() {
                                              @Override
                                              public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                              }

                                              @Override
                                              public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                  // THIS IS THE IMPORTANT PART

                                                  // Check if the text length is greater than 0 (user has typed something)
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
                                                  } else {
                                                  }
                                              }

                                              @Override
                                              public void afterTextChanged(Editable s) {
                                              }
                                          });
        done.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(Signup.this, Into.class);
                                        i.putExtra("USER_NAME", name.getText().toString());
                                        i.putExtra("USER_EMAIL", ((EditText)findViewById(R.id.email)).getText().toString());
                                        startActivity(i);
                                    }
                                });
        ImageButton back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(Signup.this, MainActivity.class);
                                        startActivity(i);
                                    }
                                });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}