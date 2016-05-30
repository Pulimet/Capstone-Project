package net.alexandroid.network.portwatcher.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DbContract {

    // The "Content authority" is a name for the entire content provider
    public static final String CONTENT_AUTHORITY = "net.alexandroid.network.portwatcher";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://net.alexandroid.network.portwatcher/history/ is a valid path
    public static final String PATH_HISTORY = "history";
    public static final String PATH_BUTTONS = "buttons";
    public static final String PATH_WATCHLIST = "watchlist";
    public static final String PATH_SCHEDULE = "schedule";

    public static final class HistoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;

        public static final String TABLE_NAME = "history";

        public static final String COLUMN_HOST = "host";
        public static final String COLUMN_PORTS = "ports";
        public static final String COLUMN_WERE_OPEN = "were_open";
        public static final String COLUMN_DATE_TIME = "date_time";

        public static Uri buildHistoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class ButtonsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BUTTONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUTTONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUTTONS;

        public static final String TABLE_NAME = "buttons";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PORTS = "ports";

        public static Uri buildButtonsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class WatchlistEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WATCHLIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WATCHLIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WATCHLIST;

        public static final String TABLE_NAME = "watchlist";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_HOST = "host";
        public static final String COLUMN_PORTS = "ports";

        public static Uri buildWatchlistUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class ScheduleEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHEDULE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHEDULE;

        public static final String TABLE_NAME = "schedule";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_HOST = "host";
        public static final String COLUMN_PORTS = "ports";
        public static final String COLUMN_INTERVAL = "interval";
        public static final String COLUMN_ENABLED = "enabled";

        public static Uri buildScheduleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
