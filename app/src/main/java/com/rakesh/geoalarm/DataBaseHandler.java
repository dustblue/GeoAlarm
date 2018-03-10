package com.rakesh.geoalarm;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.rakesh.geoalarm.MainActivity.TAG;

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "AlarmsDB";

    private static final String ALARMS = "Alarms";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_DATE = "date";

    DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + ALARMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT, "
                + KEY_LAT + " REAL, " + KEY_LNG + " REAL, " + KEY_ENABLED + " BOOLEAN, "
                + KEY_DATE + " DATETIME)";

        db.execSQL(CREATE_EVENTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ALARMS);
        onCreate(db);
    }

    void addAlarm(Alarm alarm) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            ContentValues values = new ContentValues();
            values.put(KEY_NAME, alarm.getName());
            values.put(KEY_LAT, alarm.getLat());
            values.put(KEY_LNG, alarm.getLng());
            values.put(KEY_ENABLED, alarm.getEnabled());
            values.put(KEY_DATE, alarm.getDate());

            db.insert(ALARMS, null, values);
        } catch (Exception e) {
            Log.e(TAG, "SQLite Exception : " + e.getMessage());
        }
    }

    void updateAlarm(Alarm alarm, int id) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            ContentValues values = new ContentValues();
            values.put(KEY_NAME, alarm.getName());
            values.put(KEY_LAT, alarm.getLat());
            values.put(KEY_LNG, alarm.getLng());
            values.put(KEY_ENABLED, alarm.getEnabled());
            values.put(KEY_DATE, alarm.getDate());

            db.update(ALARMS, values, KEY_ID + " = " + id, null);
        } catch (Exception e) {
            Log.e(TAG, "SQLite Exception : " + e.getMessage());
        }
    }

    void deleteAlarm(int id) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(ALARMS, KEY_ID + " = " + id, null);
        } catch (Exception e) {
            Log.e(TAG, "SQLite Exception : " + e.getMessage());
        }
    }

    Alarm getAlarm(int id) {
        Alarm alarm = new Alarm();
        String select = "SELECT * FROM " + ALARMS + " WHERE " + KEY_ID + " is " + id;

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (Cursor cursor = db.rawQuery(select, null)) {
                if (cursor.moveToFirst()) {
                    alarm.setId(cursor.getInt(0));
                    alarm.setName(cursor.getString(1));
                    alarm.setLat(cursor.getDouble(2));
                    alarm.setLng(cursor.getDouble(3));
                    alarm.setEnabled(cursor.getInt(4) > 0);
                }
            } catch (Exception e) {
                Log.e(TAG, "Cursor Exception : " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "SQLite Exception : " + e.getMessage());
        }

        return alarm;
    }

    List<Alarm> getAllAlarms() {
        List<Alarm> alarmsList = new ArrayList<>();
        String select = "SELECT * FROM " + ALARMS + " ORDER BY " + KEY_DATE + " DESC";

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (Cursor cursor = db.rawQuery(select, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        Alarm alarm = new Alarm();
                        alarm.setId(cursor.getInt(0));
                        alarm.setName(cursor.getString(1));
                        alarm.setLat(cursor.getDouble(2));
                        alarm.setLng(cursor.getDouble(3));
                        alarm.setEnabled(cursor.getInt(4) > 0);

                        alarmsList.add(alarm);

                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.e(TAG, "Cursor Exception : " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "SQLite Exception : " + e.getMessage());
        }

        return alarmsList;
    }
}
