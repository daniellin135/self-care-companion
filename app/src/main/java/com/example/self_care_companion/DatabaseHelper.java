package com.example.self_care_companion;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SelfCareCompanionApp.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // User Table
        db.execSQL("CREATE TABLE User (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT, " +
                "email TEXT UNIQUE, " +
                "pin TEXT)");

        // Mood Table
        db.execSQL("CREATE TABLE Mood (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "mood TEXT)");

        // Journal Table
        db.execSQL("CREATE TABLE Journal (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "entry TEXT)");

        // Habit Table
        db.execSQL("CREATE TABLE Habit (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "label TEXT, " +
                "value REAL, " +
                "goal REAL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "units TEXT)");

        db.execSQL("CREATE INDEX idx_mood_timestamp ON Mood(timestamp)");
        db.execSQL("CREATE INDEX idx_journal_timestamp ON Journal(timestamp)");
        db.execSQL("CREATE INDEX idx_habit_timestamp ON Habit(timestamp)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Mood");
        db.execSQL("DROP TABLE IF EXISTS Journal");
        db.execSQL("DROP TABLE IF EXISTS Habit");
        onCreate(db);
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Mood");
        db.execSQL("DROP TABLE IF EXISTS Journal");
        db.execSQL("DROP TABLE IF EXISTS Habit");
        onCreate(db);
        db.close();
    }

    public void addUser(String firstName, String email, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("email", email);
        values.put("pin", hashPin(pin));
        db.insert("User", null, values);
        db.close();
    }

    public void addMood(String mood) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mood", mood);
        db.insert("Mood", null, values);
        db.close();
    }

    public void addJournalEntry(String entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("entry", entry);
        db.insert("Journal", null, values);
        db.close();
    }

    public void addHabit(String label, double value, String units, double goal) {
        SQLiteDatabase db = this.getWritableDatabase();

        String checkQuery = "SELECT id FROM Habit WHERE label = ? AND DATE(timestamp) = DATE('now')";
        Cursor cursor = db.rawQuery(checkQuery, new String[]{label});

        if (cursor.moveToFirst()) {
            long rowId = cursor.getLong(0);

            ContentValues updateValues = new ContentValues();
            updateValues.put("value", value);
            db.update("Habit", updateValues, "id = ?", new String[]{String.valueOf(rowId)});
        } else {
            ContentValues insertValues = new ContentValues();
            insertValues.put("label", label);
            insertValues.put("value", value);
            insertValues.put("units", units);
            insertValues.put("goal", goal);
            db.insert("Habit", null, insertValues);
        }

        cursor.close();
        db.close();
    }

    public Set<String> getUniqueHabits() {
        SQLiteDatabase db = this.getReadableDatabase();

        TimeZone easternTimeZone = TimeZone.getTimeZone("America/New_York");
        Calendar now = Calendar.getInstance(easternTimeZone);

        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        Date startOfTodayEastern = now.getTime();

        now.add(Calendar.DATE, 1);
        Date startOfTomorrowEastern = now.getTime();

        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String startUtcStr = utcFormat.format(startOfTodayEastern);
        String endUtcStr = utcFormat.format(startOfTomorrowEastern);

        Set<String> habits = new HashSet<>();

        String habitQuery = "SELECT label, units, MAX(goal) FROM Habit GROUP BY label, units";
        Cursor habitCursor = db.rawQuery(habitQuery, null);

        if (habitCursor.moveToFirst()) {
            do {
                String label = habitCursor.getString(0);
                String units = habitCursor.getString(1);
                double goalValue = habitCursor.getDouble(2);

                double todayValue = 0;

                String entryQuery = "SELECT value FROM Habit WHERE label = ? AND timestamp >= ? AND timestamp < ?";
                Cursor entryCursor = db.rawQuery(entryQuery, new String[]{label, startUtcStr, endUtcStr});

                if (entryCursor.moveToFirst()) {
                    todayValue = entryCursor.getDouble(0);
                }
                entryCursor.close();

                String habitString = label + "|" + units + "|" + todayValue + "|" + goalValue;
                habits.add(habitString);

            } while (habitCursor.moveToNext());
        }

        habitCursor.close();
        db.close();

        return habits;
    }

    public Set<String> getUniqueHabitNames() {
        Set<String> habits = getUniqueHabits();
        Set<String> habitNames = new HashSet<>();

        for (String habit : habits) {
            String[] parts = habit.split("\\|");
            if (parts.length > 0) {
                habitNames.add(parts[0]); // first part is the label
            }
        }
        return habitNames;
    }

    public String getMostFrequentMood() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT mood, COUNT(*) as count FROM Mood " +
                       "WHERE timestamp >= datetime('now', '-3 days') " +
                       "GROUP BY mood ORDER BY count DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        String mostFrequentMood = null;

        if (cursor.moveToFirst()) {
            mostFrequentMood = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mostFrequentMood;
    }

    public boolean checkifUserExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM User";
        Cursor cursor = db.rawQuery(query, null);
        boolean exists = false;

        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }
        cursor.close();
        db.close();
        return exists;
    }

    public static String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    public String getUserPin() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT pin FROM User LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        String pin = null;
        if (cursor.moveToFirst()) {
            pin = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return pin;
    }

    public Map<String, Integer> getMoodCountsFiltered(int pastDays) {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Integer> moodMap = new HashMap<>();

        // Convert pastDays into a string like "-7 days"
        String pastDaysString = "-" + pastDays + " days";

        String query = "SELECT mood, COUNT(*) " +
                "FROM Mood " +
                "WHERE DATE(timestamp) >= DATE('now', ?) " +
                "GROUP BY mood";

        Cursor cursor = db.rawQuery(query, new String[]{pastDaysString});

        if (cursor.moveToFirst()) {
            do {
                String mood = cursor.getString(0);
                int count = cursor.getInt(1);
                moodMap.put(mood, count);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return moodMap;
    }


    public Map<String, Double> getHabitValues(String label, int pastDays) {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Double> habitData = new LinkedHashMap<>();

        String query = "SELECT DATE(timestamp) as date, value FROM Habit " +
                "WHERE label = ? AND DATE(timestamp) >= DATE('now', ?) " +
                "ORDER BY DATE(timestamp) ASC";

        String pastDaysString = "-" + pastDays + " day";
        Cursor cursor = db.rawQuery(query, new String[]{label, pastDaysString});

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(0);
                double value = cursor.getDouble(1);
                habitData.put(date, value);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return habitData;
    }

    public double getHabitGoal(String habitLabel) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(goal) FROM Habit WHERE label = ?", new String[]{habitLabel});
        double goal = 0;
        if (cursor.moveToFirst()) {
            goal = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return goal;
    }


}

