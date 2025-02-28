package com.example.applock.Activities;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applock.R;

public class LockScreenActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "AppLockPrefs";
    public static final String KEY_MASTER_PASSWORD = "master_password";

    private EditText passwordEditText;
    private Button unlockButton;
    private String lockedPackage; // The package that triggered the lock (optional)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        // For API level 27+ use setShowWhenLocked and setTurnScreenOn
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        // Make it full screen and dismiss the keyguard
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        passwordEditText = findViewById(R.id.password_edittext);
        unlockButton = findViewById(R.id.unlock_button);

        // Retrieve locked package from intent extras if needed.
        lockedPackage = getIntent().getStringExtra("lockedPackage");

        unlockButton.setOnClickListener(v -> {
            String enteredPassword = passwordEditText.getText().toString().trim();
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String masterPassword = prefs.getString(KEY_MASTER_PASSWORD, "");

            if (TextUtils.isEmpty(enteredPassword) || !enteredPassword.equals(masterPassword)) {
                Toast.makeText(LockScreenActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LockScreenActivity.this, "Unlocked", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}