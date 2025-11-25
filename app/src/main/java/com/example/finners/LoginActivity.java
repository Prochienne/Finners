package com.example.finners;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnBack.setOnClickListener(v -> finish());

        btnLogin.setOnClickListener(v -> {
            EditText etEmail = findViewById(R.id.etEmail);
            EditText etPassword = findViewById(R.id.etPassword);
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            String errorMessage = "Authentication failed.";
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthInvalidUserException e) {
                                errorMessage = "User not found";
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                if (e.getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                                    errorMessage = "Invalid email";
                                } else {
                                    errorMessage = "Incorrect password";
                                }
                            } catch(Exception e) {
                                errorMessage = e.getMessage();
                            }
                            Toast.makeText(LoginActivity.this, errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        tvForgotPassword.setOnClickListener(v -> {
            // Do nothing for now
        });
    }
}
