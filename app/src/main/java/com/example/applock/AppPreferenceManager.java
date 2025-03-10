package com.example.applock;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.applock.models.AppItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AppPreferenceManager {
    private static final String PREF_NAME = "app_lock_preferences";
    private static final String APP_LIST_KEY = "app_list";

    private static final String LAST_UNLOCKED_APP = "last_unlocked_app";

    private static SharedPreferences sharedPreferences;
    private static Gson gson;

    public AppPreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }


    public void saveAppList(List<AppItem> appList) {
        String json = gson.toJson(appList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_LIST_KEY, json);
        editor.apply();
    }


    public static List<AppItem> getAppList() {
        String json = sharedPreferences.getString(APP_LIST_KEY, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<AppItem>>(){}.getType();
        return gson.fromJson(json, type);
    }


    public void updateAppLockState(String packageName, boolean locked) {
        List<AppItem> appList = getAppList();
        for (AppItem app : appList) {
            if (app.getPackageName().equals(packageName)) {
                app.setLocked(locked);
                break;
            }
        }
        saveAppList(appList);
    }


    public static boolean isAppLocked(String packageName) {
        List<AppItem> appList = getAppList();
        for (AppItem app : appList) {
            if (app.getPackageName().equals(packageName)) {
                return app.isLocked();
            }
        }
        return false;
    }

    public static boolean isForegroundAppLocked(String foregroundPackageName) {
        List<AppItem> appList = getAppList();
        for (AppItem app : appList) {
            if (app.getPackageName().equals(foregroundPackageName)) {
                return app.isLocked();
            }
        }
        return false;
    }

    // Save the last unlocked app to SharedPreferences
    public static void saveLastUnlockedApp(String packageName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_UNLOCKED_APP, packageName);
        editor.apply();
    }

    // Retrieve the last unlocked app from SharedPreferences
    public static String getLastUnlockedApp() {
        return sharedPreferences.getString(LAST_UNLOCKED_APP, "");
    }
    // Save last unlock time for a specific app
    public static void saveLastUnlockTime(String packageName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("last_unlock_time_" + packageName, System.currentTimeMillis());
        editor.apply();
    }

    // Get last unlock time for a specific app
    public static long getLastUnlockTime(String packageName) {
        return sharedPreferences.getLong("last_unlock_time_" + packageName, 0);
    }

}
