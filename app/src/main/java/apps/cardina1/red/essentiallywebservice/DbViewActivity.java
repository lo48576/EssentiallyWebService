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
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbViewActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
               OnFragmentInteractionListener
{
    private final static String ACTIVITY_TAG = "DbViewActivity";
    public final static String DB_URI_EXTRA = "db_uri";
    public final static String FRAG_INTR_QUERY_WITH_TABLE_RESULT = "query_with_result";

    private File file;
    private Database db;
    private List<MenuItem> tableItems = new ArrayList<>();

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
            db = new Database(file.getPath());
        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(
                    DbViewActivity.this,
                    R.string.toast_failed_to_open_db_file,
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set up DB-related UI components.
        updateTablesMenu();
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

        // See <https://qiita.com/Hoshi_7/items/bee2588397348a4e0105>.
        FragmentManager fragmentManager = getSupportFragmentManager();

        int id = item.getItemId();

        if (tableItems.contains(item)) {
            String tableName = item.getTitle().toString();
            Log.d(ACTIVITY_TAG, "onNavigationItemSelected: table item selected: " + tableName);
            // FIXME: unimplemented.
            Log.d(ACTIVITY_TAG, "onNavigationItemSelected: FIXME: unimplemented");
        } else if (id == R.id.nav_raw_query) {

        } else if (id == R.id.nav_select) {
            fragmentManager.beginTransaction()
                .replace(R.id.content_db_view, SelectFragment.newInstance(db.getTableNames()))
                .commit();
        } else if (id == R.id.nav_insert) {

        } else if (id == R.id.nav_update) {

        } else if (id == R.id.nav_delete) {

        } else if (id == R.id.nav_create) {

        } else if (id == R.id.nav_drop) {

        } else if (id == R.id.nav_alter) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String tag, Object data) {
        Log.d(ACTIVITY_TAG, "onListFragmentInteraction: tag = " + tag);
        if (tag.equals(FRAG_INTR_QUERY_WITH_TABLE_RESULT)) {
            Optional<String> query = (Optional<String>) data;
            if (query.isPresent()) {
                Cursor cursor = db.rawQuery(query.get());
                cursor.moveToFirst();
                // For each row.
                while (!cursor.isAfterLast()) {
                    // For each column.
                    int columnCount = cursor.getColumnCount();
                    for (int col_i = 0; col_i < columnCount; col_i++) {
                        String colString = "";
                        boolean colIsStringRepresentable = false;
                        switch (cursor.getType(col_i)) {
                            case Cursor.FIELD_TYPE_BLOB:
                                colString = "(blob (" + cursor.getBlob(col_i).length + " bytes))";
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                colString = String.valueOf(cursor.getDouble(col_i));
                                colIsStringRepresentable = true;
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                colString = String.valueOf(cursor.getInt(col_i));
                                colIsStringRepresentable = true;
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                                colString = "(null)";
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                colString = cursor.getString(col_i).toString();
                                colIsStringRepresentable = true;
                                break;
                        }
                        Log.d(ACTIVITY_TAG,
                                "onFragmentInteraction(FRAG_INTR_QUERY_WITH_TABLE_RESULT): " +
                                "[string representable: " +
                                colIsStringRepresentable +
                                "] " +
                                cursor.getColumnName(col_i) +
                                " = " +
                                colString);
                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
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
            localFile = null;
        } catch (IOException e) {
            Log.e(ACTIVITY_TAG,
                    "loadToAppLocalFile: failed to close content stream: " + uri);
            e.printStackTrace();
            localFile = null;
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

    private void updateTablesMenu() {
        Log.d(ACTIVITY_TAG, "updateTablesMenu");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem tablesItem = menu.findItem(R.id.nav_tables);
        Menu tablesMenu = tablesItem.getSubMenu();

        tablesMenu.clear();
        tableItems.clear();
        for (String name: db.getTableNames()) {
            Log.d(ACTIVITY_TAG, "updateTablesMenu: adding table items to the drawer: " + name);
            // FIXME: Set appropriate parameters: groudId, itemId, and order.
            MenuItem item = tablesMenu.add(Menu.NONE, Menu.NONE, Menu.NONE, name);
            item.setIcon(R.drawable.baseline_view_module_24);
            tableItems.add(item);
        }
    }
}
