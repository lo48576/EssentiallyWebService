package apps.cardina1.red.essentiallywebservice;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

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
}
