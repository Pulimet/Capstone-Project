package net.alexandroid.network.portwatcher.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.alexandroid.network.portwatcher.data.DbContract.ButtonsEntry;
import net.alexandroid.network.portwatcher.data.DbContract.HistoryEntry;
import net.alexandroid.network.portwatcher.data.DbContract.ScheduleEntry;
import net.alexandroid.network.portwatcher.data.DbContract.WatchlistEntry;

public class Provider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int HISTORY = 100;
    static final int BUTTONS = 200;
    static final int WATCHLIST = 300;
    static final int SCHEDULE = 400;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DbContract.PATH_HISTORY, HISTORY);
        matcher.addURI(authority, DbContract.PATH_BUTTONS, BUTTONS);
        matcher.addURI(authority, DbContract.PATH_WATCHLIST, WATCHLIST);
        matcher.addURI(authority, DbContract.PATH_SCHEDULE, SCHEDULE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case HISTORY:
                return HistoryEntry.CONTENT_TYPE;
            case BUTTONS:
                return ButtonsEntry.CONTENT_TYPE;
            case WATCHLIST:
                return WatchlistEntry.CONTENT_TYPE;
            case SCHEDULE:
                return ScheduleEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case HISTORY:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HistoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case BUTTONS:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ButtonsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case WATCHLIST:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WatchlistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SCHEDULE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ScheduleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        //noinspection ConstantConditions
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case HISTORY:
                long _id_h = db.insert(HistoryEntry.TABLE_NAME, null, values);
                if (_id_h > 0)
                    returnUri = HistoryEntry.buildHistoryUri(_id_h);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case BUTTONS:
                long _id_b = db.insert(ButtonsEntry.TABLE_NAME, null, values);
                if (_id_b > 0)
                    returnUri = ButtonsEntry.buildButtonsUri(_id_b);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case WATCHLIST:
                long _id_w = db.insert(WatchlistEntry.TABLE_NAME, null, values);
                if (_id_w > 0)
                    returnUri = WatchlistEntry.buildWatchlistUri(_id_w);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case SCHEDULE:
                long _id_s = db.insert(ScheduleEntry.TABLE_NAME, null, values);
                if (_id_s > 0)
                    returnUri = ScheduleEntry.buildScheduleUri(_id_s);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        //noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case HISTORY:
                rowsDeleted = db.delete(HistoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BUTTONS:
                rowsDeleted = db.delete(ButtonsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WATCHLIST:
                rowsDeleted = db.delete(WatchlistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SCHEDULE:
                rowsDeleted = db.delete(ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case HISTORY:
                rowsUpdated = db.update(HistoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BUTTONS:
                rowsUpdated = db.update(ButtonsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case WATCHLIST:
                rowsUpdated = db.update(WatchlistEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SCHEDULE:
                rowsUpdated = db.update(ScheduleEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
