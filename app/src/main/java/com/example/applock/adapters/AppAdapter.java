package com.example.applock.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.applock.AppPreferenceManager;
import com.example.applock.R;
import com.example.applock.models.AppItem;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private List<AppItem> appList;
    private Context context;
    private AppPreferenceManager preferenceManager;

    public interface OnAppLockChangedListener {
        void onAppLockChanged(String packageName, boolean locked);
    }

    private OnAppLockChangedListener lockChangedListener;

    public AppAdapter(Context context, List<AppItem> appList) {
        this.context = context;
        this.appList = appList;
        this.preferenceManager = new AppPreferenceManager(context);
    }

    public void setOnAppLockChangedListener(OnAppLockChangedListener listener) {
        this.lockChangedListener = listener;
    }

    @Override
    public AppViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_item, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        AppItem app = appList.get(position);
        holder.appNameTextView.setText(app.getAppName());

        // Remove any previous listener to avoid triggering it on recycling
        holder.lockSwitch.setOnCheckedChangeListener(null);

        // Set the switch state based on the current app's locked state
        holder.lockSwitch.setChecked(app.isLocked());

        try {
            Drawable icon = context.getPackageManager().getApplicationIcon(app.getPackageName());
            holder.appImage.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            holder.appImage.setImageResource(R.mipmap.ic_launcher);
        }

        // Attach a new listener
        holder.lockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            app.setLocked(isChecked);
            if (lockChangedListener != null) {
                lockChangedListener.onAppLockChanged(app.getPackageName(), isChecked);
                preferenceManager.updateAppLockState(app.getPackageName(), app.isLocked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    // עדכון הרשימה
    public void updateAppList(List<AppItem> newAppList) {
        this.appList = newAppList;
        notifyDataSetChanged();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        android.widget.TextView appNameTextView;
        android.widget.Switch lockSwitch;
        ImageView appImage;

        public AppViewHolder(android.view.View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.app_name_text_view);
            lockSwitch = itemView.findViewById(R.id.lock_switch);
            appImage = itemView.findViewById(R.id.app_icon_image_view);
        }
    }
}