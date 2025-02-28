package com.example.applock.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applock.R;


public class PasswordActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "AppLockPrefs";
    public static final String KEY_MASTER_PASSWORD = "master_password";

    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        // Initialize UI elements
        passwordEditText = findViewById(R.id.password_edittext);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edittext);
        actionButton = findViewById(R.id.action_button);

        // Check if a master password is already saved
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedPassword = prefs.getString(KEY_MASTER_PASSWORD, null);

        if (TextUtils.isEmpty(savedPassword)) {
            // First-time flow: set a new password
            setupNewPasswordFlow();
        } else {
            // Login flow: ask for password input only
            setupLoginFlow();
        }
    }

    private void setupNewPasswordFlow() {
        // Show both password fields and set button text to "Set Password"
        confirmPasswordEditText.setVisibility(View.VISIBLE);
        actionButton.setText("Set Password");

        actionButton.setOnClickListener(v -> {
            String pass1 = passwordEditText.getText().toString().trim();
            String pass2 = confirmPasswordEditText.getText().toString().trim();
            if (pass1.length() != 4) {
                Toast.makeText(this, "Please enter a 4-digit password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass1.equals(pass2)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            // Save the new master password
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_MASTER_PASSWORD, pass1);
            editor.apply();

            Toast.makeText(this, "Password set successfully", Toast.LENGTH_SHORT).show();
            // Proceed to MainActivity
            startActivity(new Intent(PasswordActivity.this, MainActivity.class));
            finish();
        });
    }

    private void setupLoginFlow() {
        // Hide the confirm password field and set button text to "Login"
        confirmPasswordEditText.setVisibility(View.GONE);
        actionButton.setText("Login");

        actionButton.setOnClickListener(v -> {
            String input = passwordEditText.getText().toString().trim();
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String savedPassword = prefs.getString(KEY_MASTER_PASSWORD, "");
            if (input.equals(savedPassword)) {
                // Correct password: go to MainActivity
                startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Incorrect password. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}