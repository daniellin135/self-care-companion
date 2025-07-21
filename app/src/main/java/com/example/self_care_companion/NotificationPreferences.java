package com.example.self_care_companion;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationPreferences {
    private static final String PREFS_NAME = "notification_settings";
    private static final String MORNING_ENABLED = "morning_enabled";
    private static final String MIDDAY_ENABLED = "midday_enabled";
    private static final String EVENING_ENABLED = "evening_enabled";

    private final SharedPreferences prefs;

    public NotificationPreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // notifs enabled by default
    public boolean isMorningEnabled() { return prefs.getBoolean(MORNING_ENABLED, true); }
    public boolean isMiddayEnabled() { return prefs.getBoolean(MIDDAY_ENABLED, true); }
    public boolean isEveningEnabled() { return prefs.getBoolean(EVENING_ENABLED, true); }

    public void setMorningEnabled(boolean enabled) { prefs.edit().putBoolean(MORNING_ENABLED, enabled).apply(); }
    public void setMiddayEnabled(boolean enabled) { prefs.edit().putBoolean(MIDDAY_ENABLED, enabled).apply(); }
    public void setEveningEnabled(boolean enabled) { prefs.edit().putBoolean(EVENING_ENABLED, enabled).apply(); }
}