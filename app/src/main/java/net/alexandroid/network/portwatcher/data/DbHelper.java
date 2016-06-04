package net.alexandroid.network.portwatcher.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.alexandroid.network.portwatcher.data.DbContract.ButtonsEntry;
import net.alexandroid.network.portwatcher.data.DbContract.HistoryEntry;
import net.alexandroid.network.portwatcher.data.DbContract.ScheduleEntry;
import net.alexandroid.network.portwatcher.data.DbContract.WatchlistEntry;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data.db";
    private static final String NULL_STR = "NULL STR";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                HistoryEntry.COLUMN_HOST + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_PORTS + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_WERE_OPEN + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_DATE_TIME + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_BUTTONS_TABLE = "CREATE TABLE " + ButtonsEntry.TABLE_NAME + " (" +
                ButtonsEntry._ID + " INTEGER PRIMARY KEY," +
                ButtonsEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ButtonsEntry.COLUMN_PORTS + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_WATCHLIST_TABLE = "CREATE TABLE " + WatchlistEntry.TABLE_NAME + " (" +
                WatchlistEntry._ID + " INTEGER PRIMARY KEY," +
                WatchlistEntry.COLUMN_HOST + " TEXT NOT NULL, " +
                WatchlistEntry.COLUMN_PORTS + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_SCHEDULE_TABLE = "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " (" +
                ScheduleEntry._ID + " INTEGER PRIMARY KEY," +
                ScheduleEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_HOST + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_PORTS + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_INTERVAL + " INTEGER NOT NULL, " +
                ScheduleEntry.COLUMN_ENABLED + " INTEGER NOT NULL " +
                " );";


        db.execSQL(SQL_CREATE_HISTORY_TABLE);
        db.execSQL(SQL_CREATE_BUTTONS_TABLE);
        db.execSQL(SQL_CREATE_WATCHLIST_TABLE);
        db.execSQL(SQL_CREATE_SCHEDULE_TABLE);

        //addHistoryDummy(db);
        //addWatchListDummy(db);
        addButtons(db);
    }

    private void addHistoryDummy(SQLiteDatabase db) {
        ContentValues values = getHistoryContentValues("blabla.com", "80,90", "80", System.currentTimeMillis());
        db.insert(HistoryEntry.TABLE_NAME, NULL_STR, values);
    }

    private void addWatchListDummy(SQLiteDatabase db) {
        ContentValues values = getWatchlistContentValues("alexandroid.net", "80,90");
        db.insert(WatchlistEntry.TABLE_NAME, NULL_STR, values);
    }

    public static ContentValues getHistoryContentValues(String host, String ports, String wereOpen, long dateTime) {
        ContentValues values = new ContentValues();
        values.put(HistoryEntry.COLUMN_HOST, host);
        values.put(HistoryEntry.COLUMN_PORTS, ports);
        values.put(HistoryEntry.COLUMN_WERE_OPEN, wereOpen);
        values.put(HistoryEntry.COLUMN_DATE_TIME, dateTime);
        return values;
    }

    public static ContentValues getWatchlistContentValues(String host, String ports) {
        ContentValues values = new ContentValues();
        values.put(WatchlistEntry.COLUMN_HOST, host);
        values.put(WatchlistEntry.COLUMN_PORTS, ports);
        return values;
    }

    public static ContentValues getButtonsContentValues(String title, String ports) {
        ContentValues values = new ContentValues();
        values.put(ButtonsEntry.COLUMN_TITLE, title);
        values.put(ButtonsEntry.COLUMN_PORTS, ports);
        return values;
    }

    private void addButtons(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(ButtonsEntry.COLUMN_TITLE, "80");
        values.put(ButtonsEntry.COLUMN_PORTS, "80");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "90");
        values.put(ButtonsEntry.COLUMN_PORTS, "90");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "8080");
        values.put(ButtonsEntry.COLUMN_PORTS, "8080");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "GV DVR/NVR");
        values.put(ButtonsEntry.COLUMN_PORTS, "80,4550,5550,6550,5552,8866,5511");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "GV CenterV2");
        values.put(ButtonsEntry.COLUMN_PORTS, "5547");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "GV IP Device");
        values.put(ButtonsEntry.COLUMN_PORTS, "80,5552,10000");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "Rifatron");
        values.put(ButtonsEntry.COLUMN_PORTS, "80,2000,50100");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "Procam");
        values.put(ButtonsEntry.COLUMN_PORTS, "80,90");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "Avigilon");
        values.put(ButtonsEntry.COLUMN_PORTS, "38880-38883,80,50081-50083");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "Evermedia");
        values.put(ButtonsEntry.COLUMN_PORTS, "80,5555");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "Win4Net");
        values.put(ButtonsEntry.COLUMN_PORTS, "80, 9010, 2000");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "Sentinel");
        values.put(ButtonsEntry.COLUMN_PORTS, "80,8000,9000");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "Provision");
        values.put(ButtonsEntry.COLUMN_PORTS, "80, 8000");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "Dahua");
        values.put(ButtonsEntry.COLUMN_PORTS, "80, 37777, 37778");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
        values.put(ButtonsEntry.COLUMN_TITLE, "Avtech");
        values.put(ButtonsEntry.COLUMN_PORTS, "80");
        db.insert(ButtonsEntry.TABLE_NAME, NULL_STR, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + HistoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ButtonsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WatchlistEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleEntry.TABLE_NAME);
        onCreate(db);
    }


}
