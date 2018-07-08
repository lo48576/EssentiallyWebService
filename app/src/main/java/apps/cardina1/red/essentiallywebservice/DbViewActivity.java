package apps.cardina1.red.essentiallywebservice;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public class DbViewActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    private final static String ACTIVITY_TAG = "DbViewActivity";
    public final static String DB_URI_EXTRA = "db_uri";

    private File file;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Uri uri = getDbUri();

        // Query the display name of the resource.
        View headerView = navigationView.getHeaderView(0);
        TextView uriText = headerView.findViewById(R.id.db_file_uri);
        uriText.setText(uri.toString());
        Cursor cursor = getContentResolver()
                .query(uri, new String[]{ OpenableColumns.DISPLAY_NAME }, null, null, null, null);
        Optional<String> displayNameResult = getDisplayName();
        Log.d(ACTIVITY_TAG, "onCreate: display name = " + displayNameResult);
        if (!displayNameResult.isPresent()) {
            finish();
            return;
        }
        String displayName = displayNameResult.get();
        TextView displayNameText = headerView.findViewById(R.id.db_file_name);
        displayNameText.setText(displayName);

        Optional<File> fileResult = loadToAppLocalFile(uri, displayName);
        Log.d(ACTIVITY_TAG, "onCreate: created app-local file: " + fileResult);
        if (!fileResult.isPresent()) {
            Toast.makeText(
                    DbViewActivity.this,
                    R.string.toast_failed_to_create_app_local_file_copy,
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        file = fileResult.get();

        try {
            db = SQLiteDatabase.openDatabase(file.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(
                    DbViewActivity.this,
                    R.string.toast_failed_to_open_db_file,
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (db.isDatabaseIntegrityOk()) {
            Log.d(ACTIVITY_TAG,
                    "onCreate: Database integrity is OK. Enabling foreign key constraint");
            db.setForeignKeyConstraintsEnabled(true);
        } else {
            Log.w(ACTIVITY_TAG,
                    "onCreate: Database integrity is not OK");
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(ACTIVITY_TAG, "onDestroy");
        if (db != null) {
            db.close();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.db_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Optional<File> loadToAppLocalFile(Uri uri, String name) {
        Log.d(ACTIVITY_TAG, "loadToAppLocalFile: uri = " + uri.toString() + ", name = " + name);
        File localFile = null;
        try (InputStream contentStream = getContentResolver().openInputStream(uri)) {
            localFile = createAppLocalFile(name);
            // About stream-to-file copy, see <http://www.baeldung.com/convert-input-stream-to-a-file>.
            // NOTE: `java.nio.file.{Files, Paths}` are available for API level 26 or above.
            try (OutputStream outStream = new FileOutputStream(localFile)) {
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = contentStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                Log.e(ACTIVITY_TAG,
                        "loadToAppLocalFile: failed to copy stream into file: " + uri + ", " + localFile);
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            Log.e(ACTIVITY_TAG, "loadToAppLocalFile: cannot open input stream: " + uri);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(ACTIVITY_TAG,
                    "loadToAppLocalFile: failed to close content stream: " + uri);
            e.printStackTrace();
        }
        return Optional.ofNullable(localFile);
    }

    private File createAppLocalFile(String name) {
        Log.d(ACTIVITY_TAG, "createAppLocalFile");
        File file = new File(getFilesDir(), name);
        Log.d(ACTIVITY_TAG, "createAppLocalFile: file = " + file);
        return file;
    }

    private Uri getDbUri() {
        return Uri.parse(getIntent().getStringExtra(DB_URI_EXTRA));
    }

    private Optional<String> getDisplayName() {
        Cursor cursor = getContentResolver()
                .query(getDbUri(), new String[]{ OpenableColumns.DISPLAY_NAME }, null, null, null, null);
        Optional<String> displayName = Optional.empty();
        try {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = Optional.of(cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
            }
        } finally {
            cursor.close();
        }
        return displayName;
    }
}
