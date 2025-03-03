package com.example.applock;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.applock.Activities.LockScreenActivity;

import java.util.List;

public class AppLockService extends Service {
    private static final String TAG = "AppLockService";
    private static final String CHANNEL_ID = "app_lock_service_channel";
    private boolean running = true;
    private String lastLockedApp = "";

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate() called");

        // Create a notification channel for Oreo+ devices.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Lock Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created");
        }
        // Build a notification to run the service in the foreground.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App Lock Service")
                .setContentText("Monitoring locked apps")
                .setSmallIcon(android.R.drawable.ic_lock_lock); // Use your own icon if available.
        startForeground(1, builder.build());
        Log.d(TAG, "Foreground service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand() called");


        new Thread(() -> {
            while (running) {
                String foregroundApp = getForegroundApp();
                Log.d(TAG, "Foreground app: " + foregroundApp);
                if (!foregroundApp.isEmpty()) {
                    // Check if the current foreground app is locked
                    if (AppPreferenceManager.isForegroundAppLocked(foregroundApp)) {
                        // Check if overlay is not already showing AND
                        // either it's a new locked app OR we're returning to the same app
                        if (!LockOverlayService.isOverlayShown() &&
                                (!foregroundApp.equals(lastLockedApp) || lastLockedApp.isEmpty())) {
                            lastLockedApp = foregroundApp;
                            Log.d(TAG, "Locked app detected: " + foregroundApp);
                            changeToLockOverlay(foregroundApp);
                        }
                    } else {
                        // Reset lastLockedApp if the current app isn't locked
                        lastLockedApp = "";
                    }
                }
                try {
                    Thread.sleep(2000); // Check every 2 seconds
                } catch (InterruptedException e) {
                    Log.e(TAG, "Monitoring thread interrupted", e);
                }
            }
            Log.d(TAG, "Monitoring thread exiting");
        }).start();

        return START_STICKY;
    }

    // Helper method to get the package name of the current foreground app.
    private String getForegroundApp() {
        String currentApp = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 10000, time);
            if (appList != null && !appList.isEmpty()) {
                UsageStats recentStats = null;
                for (UsageStats stats : appList) {
                    if (recentStats == null || stats.getLastTimeUsed() > recentStats.getLastTimeUsed()) {
                        recentStats = stats;
                    }
                }
                if (recentStats != null) {
                    currentApp = recentStats.getPackageName();
                }
            }
        }
        return currentApp;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not binding.
    }

    @Override
    public void onDestroy() {
        running = false;
        Log.d(TAG, "Service onDestroy() called. Stopping monitoring.");
        super.onDestroy();
    }

    public void changeToLockScreenActivity(String foregroundApp){
        Intent lockIntent = new Intent(AppLockService.this, LockScreenActivity.class);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        lockIntent.putExtra("lockedPackage", foregroundApp);
        startActivity(lockIntent);
        Log.d(TAG, "LockScreenActivity launched for " + foregroundApp);
    }

    public void changeToLockOverlay(String foregroundApp) {
        if (!LockOverlayService.isOverlayShown()) {
            Intent overlayIntent = new Intent(AppLockService.this, LockOverlayService.class);
            // You can pass extras if needed
            overlayIntent.putExtra("lockedPackage", foregroundApp);
            startService(overlayIntent);
            Log.d(TAG, "LockOverlayService started for " + foregroundApp);
        } else {
            Log.d(TAG, "Overlay already shown for " + foregroundApp);
        }
    }
}