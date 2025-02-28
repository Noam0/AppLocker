package com.example.applock;

import static android.content.Context.WINDOW_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class LockOverlayService extends Service {

    private BroadcastReceiver homeButtonReceiver;
    public static boolean overlayShown = false; // Track if overlay is visible
    private static final String TAG = "LockOverlayService";
    private WindowManager windowManager;
    private View lockView;

    private String masterPassword;

    @Override
    public void onCreate() {
        super.onCreate();
        overlayShown = true;
        Log.d(TAG, "LockOverlayService onCreate()");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // Inflate the overlay layout
        lockView = LayoutInflater.from(this).inflate(R.layout.lock_overly, null);

        // Define layout parameters for the overlay view
        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, // Allow touch events on overlay view
                PixelFormat.TRANSLUCENT
        );


        // Add the view to the window
        windowManager.addView(lockView, params);
        Log.d(TAG, "Overlay view added");


        SharedPreferences prefs = getSharedPreferences("AppLockPrefs", Context.MODE_PRIVATE);
        masterPassword = prefs.getString("master_password", "");
        Log.d(TAG, "Master Password" + masterPassword );

        homeButtonReceiver = new BroadcastReceiver() {
            private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
            private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (reason != null && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        Log.d(TAG, "Home button pressed, removing overlay");
                        overlayShown = false;
                        stopSelf();
                    }
                }
            }
        };

        // Register the receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeButtonReceiver, filter, Context.RECEIVER_NOT_EXPORTED);

        // Setup the unlock button action
        Button unlockButton = lockView.findViewById(R.id.overlay_unlock_button);
        EditText passwordEditText = lockView.findViewById(R.id.overlay_password);
        unlockButton.setOnClickListener(v -> {
            String input = passwordEditText.getText().toString().trim();

            if (input.equals(masterPassword)) {
                Toast.makeText(LockOverlayService.this, "Unlocked", Toast.LENGTH_SHORT).show();
                stopSelf(); // Remove the overlay
            } else {
                Toast.makeText(LockOverlayService.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service started; you might log additional info or handle intent extras.
        Log.d(TAG, "LockOverlayService onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Unregister the receiver
        if (homeButtonReceiver != null) {
            try {
                unregisterReceiver(homeButtonReceiver);
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering receiver", e);
            }
        }

        // Your existing cleanup code...
        if (lockView != null) {
            try {
                overlayShown = false;
                windowManager.removeView(lockView);
                Log.d(TAG, "Overlay view removed");
            } catch (Exception e) {
                Log.e(TAG, "Error removing overlay", e);
            }
        }
        overlayShown = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static boolean isOverlayShown() {
        return overlayShown;
    }

}