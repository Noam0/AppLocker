package com.example.applock.models;

public class AppItem {
    private String appName;
    private String packageName;
    private boolean locked;

    public AppItem(String appName, String packageName, boolean locked) {
        this.appName = appName;
        this.packageName = packageName;
        this.locked = locked;
    }

    // קונסטרקטור ריק עבור Gson
    public AppItem() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}