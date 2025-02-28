package com.example.applock.Activities;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applock.adapters.AppAdapter;
import com.example.applock.models.AppItem;
import com.example.applock.AppLockService;
import com.example.applock.AppPreferenceManager;
import com.example.applock.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView appsRecyclerView;
    private AppAdapter appAdapter;
    private List<AppItem> appList;
    private AppPreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        checkUsageStatsPermission(this);
        requestUsageStatsPermission(this);
        Intent serviceIntent = new Intent(this, AppLockService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            // Optionally, wait until the user grants permission.
        }


        appsRecyclerView = findViewById(R.id.apps_recycler_view);

        // Use a grid with 3 columns:
        appsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        preferenceManager = new AppPreferenceManager(this);
        appList = preferenceManager.getAppList();

        if (appList.isEmpty()) {
            loadInstalledApps();
            preferenceManager.saveAppList(appList);
        }

        // Remove or comment out the old "LinearLayoutManager" line!
        // appsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        appAdapter = new AppAdapter(this, appList);
        appsRecyclerView.setAdapter(appAdapter);

        appAdapter.setOnAppLockChangedListener((packageName, locked) -> {
            preferenceManager.updateAppLockState(packageName, locked);
            Log.d(TAG, "שונה מצב הנעילה של " + packageName + " ל-" + locked);
        });
    }


    private void loadInstalledApps() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        appList = new ArrayList<>();

        for (PackageInfo packageInfo : packages) {
            // סינון אפליקציות מערכת
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String packageName = packageInfo.packageName;
                String appName = pm.getApplicationLabel(packageInfo.applicationInfo).toString();
                appList.add(new AppItem(appName, packageName, false));
            }
        }

        Log.d(TAG, "נטענו " + appList.size() + " אפליקציות משתמש.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // שמירת מצב הנעילה הנוכחי של האפליקציות
        preferenceManager.saveAppList(appList);

        // שמירת הסיסמה הראשית
       // String password = passwordEditText.getText().toString();
        SharedPreferences prefs = getSharedPreferences("master_password_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
      //  editor.putString("master_password", password);
        editor.apply();
    }

    public static boolean checkUsageStatsPermission(Context context) {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public static void requestUsageStatsPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!checkUsageStatsPermission(context)) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                context.startActivity(intent);
            }
        }
    }
}