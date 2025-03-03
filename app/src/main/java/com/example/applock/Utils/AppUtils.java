package com.example.applock.Utils;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class AppUtils {

    /**
     * Returns the package name of the current foreground app using UsageEvents.
     * Make sure you have permission to access usage stats.
     *
     * @param context the context to use
     * @return the package name of the foreground app, or an empty string if not found
     */
    public static String getForegroundApp(Context context) {
        String currentApp = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long currentTime = System.currentTimeMillis();
            // Query events in the last second
            UsageEvents usageEvents = usm.queryEvents(currentTime - 1000, currentTime);
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                // Look for events where an app moved to the foreground
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    currentApp = event.getPackageName();
                }
            }
        }
        return currentApp;
    }

    /**
     * Returns the default home launcher package name.
     *
     * @param context the context to use
     * @return the package name of the default home launcher
     */
    public static String getDefaultHomePackage(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            return resolveInfo.activityInfo.packageName;
        }
        return "";
    }

    /**
     * Checks whether the current foreground app is the home screen.
     *
     * @param context the context to use
     * @return true if the current foreground app is the home screen, false otherwise
     */
    public static boolean isHomeScreen(Context context) {
        String foregroundApp = getForegroundApp(context);
        String homePackage = getDefaultHomePackage(context);
        return foregroundApp.equals(homePackage);
    }
}
