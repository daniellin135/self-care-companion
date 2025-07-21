package com.example.self_care_companion.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.Calendar;

public class NotificationScheduler {
    private static final int MORNING_REQUEST_CODE = 1001;
    private static final int EVENING_REQUEST_CODE = 1002;
    private static final int MIDDAY_REQUEST_CODE = 1003;

    public static void scheduleMiddayNotification(Context context, int hour, int minute) {
        scheduleNotification(context, hour, minute,
                NotificationReceiver.TYPE_MIDDAY, MIDDAY_REQUEST_CODE);
    }

    public static void scheduleMorningNotification(Context context, int hour, int minute) {
        scheduleNotification(context, hour, minute,
                NotificationReceiver.TYPE_MORNING, MORNING_REQUEST_CODE);
    }

    public static void scheduleEveningNotification(Context context, int hour, int minute) {
        scheduleNotification(context, hour, minute,
                NotificationReceiver.TYPE_EVENING, EVENING_REQUEST_CODE);
    }

    private static void scheduleNotification(Context context, int hour, int minute,
                                             String type, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.EXTRA_NOTIFICATION_TYPE, type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
                intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), pendingIntent);

        Log.d("NotificationScheduler", "Scheduled notification for: " + calendar.getTime());
    }

    public static void cancelNotification(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
                intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}