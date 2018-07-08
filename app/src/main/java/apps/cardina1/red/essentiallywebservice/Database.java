package apps.cardina1.red.essentiallywebservice;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Database {
    private final static String CLASS_TAG = "Database";

    private Optional<String> path = Optional.empty();
    private SQLiteDatabase db;

    public Database(String path) throws SQLiteException {
        // `NO_LOCALIZED_COLLATORS` is required to prevent Android from
        // automatically generating `android_metadata` table.
        this.path = Optional.ofNullable(path);
        db = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    public Database(SQLiteDatabase db) {
        this.db = db;

        if (db.isDatabaseIntegrityOk()) {
            Log.d(CLASS_TAG,
                    "onCreate: Database integrity is OK. Enabling foreign key constraint");
            db.setForeignKeyConstraintsEnabled(true);
        } else {
            Log.w(CLASS_TAG,
                    "onCreate: Database integrity is not OK");
        }
    }

    public void close() {
        db.close();
    }

    // See <https://stackoverflow.com/a/15384267>.
    public List<String> getTableNames() {
        List<String> tables = new ArrayList<>();
        try (Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)) {
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    tables.add(c.getString(c.getColumnIndex("name")));
                    c.moveToNext();
                }
            }
        }

        return tables;
    }
}
