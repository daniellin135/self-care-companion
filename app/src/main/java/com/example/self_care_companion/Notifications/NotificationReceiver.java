package com.example.self_care_companion.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.core.app.NotificationCompat;

import com.example.self_care_companion.DatabaseHelper;
import com.example.self_care_companion.MainActivity;
import com.example.self_care_companion.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String EXTRA_NOTIFICATION_TYPE = "notification_type";
    public static final String TYPE_MORNING = "morning";
    public static final String TYPE_EVENING = "evening";
    public static final String TYPE_MIDDAY = "midday";

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE);
        String message = "";

        if (TYPE_MORNING.equals(type)) {
            message = "Good morning! Would you like to check-in?";
        } else if (TYPE_EVENING.equals(type)) {
            message = "Good evening! Would you like to reflect?";
        } else if (TYPE_MIDDAY.equals(type)) {
            message = getHabitMessage(context);
        }

        showNotification(context, message);
    }

    private String getHabitMessage(Context context) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // get today's habits
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String query = "SELECT label, value, goal, units FROM habit WHERE date(timestamp) = ? ORDER BY timestamp DESC";
            Cursor cursor = db.rawQuery(query, new String[]{today});

            if (cursor.moveToFirst()) {
                // if all habits are completed, custom message!
                boolean allCompleted = true;
                cursor.moveToFirst();
                do {
                    double value = cursor.getDouble(1);
                    double goal = cursor.getDouble(2);
                    if (value < goal) {
                        allCompleted = false;
                        break;
                    }
                } while (cursor.moveToNext());

                if (allCompleted) {
                    cursor.close();
                    return "Amazing! You've completed all your habits today! 🌟";
                }

                // pick a random habit that's not complete
                cursor.moveToFirst();
                String randomHabit = "";
                String randomUnits = "";
                double remainingAmount = 0;
                int incompleteCount = 0;

                do {
                    double value = cursor.getDouble(1);
                    double goal = cursor.getDouble(2);
                    if (value < goal) {
                        incompleteCount++;
                        if (new Random().nextInt(incompleteCount) == 0) {
                            randomHabit = cursor.getString(0);
                            randomUnits = cursor.getString(3);
                            remainingAmount = goal - value;
                        }
                    }
                } while (cursor.moveToNext());

                cursor.close();

                if (!randomHabit.isEmpty()) {
                    // "You have X units to go for your habit goal! Keep it up!"
                    return "You have " + remainingAmount + " " + randomUnits + " to go for your " +
                            randomHabit.toLowerCase() + " goal! Keep it up!";
                }
            }

            // no habits found:
            return "Time for a habit check! How are your goals going?";

        } catch (Exception e) {
            return "Time for a habit check! How are your goals going?";
        }
    }

    private void showNotification(Context context, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.self_care_logo)
                .setContentTitle("Self Care Companion")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}