package apps.cardina1.red.essentiallywebservice;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String ACTIVITY_TAG = "MainActivity";
    private final static int REQ_OPEN_DB_FILE = 4097;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(ACTIVITY_TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(ACTIVITY_TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.item_open_file:
                Log.d(ACTIVITY_TAG, "Selected: Open file");
                dispatchFileSelectionIntent();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        switch (reqCode) {
            case REQ_OPEN_DB_FILE:
                if (resCode == RESULT_OK) {
                    String uri = data.getDataString();
                    Log.d(ACTIVITY_TAG, "onActivityResult: REQ_OPEN_DB_FILE: uri = " + uri);
                    Intent intent = new Intent(this, DbViewActivity.class);
                    intent.putExtra(DbViewActivity.DB_URI_EXTRA, uri);
                    startActivity(intent);
                }
                break;
        }
    }

    private void dispatchFileSelectionIntent() {
        Log.d(ACTIVITY_TAG, "dispatchFileSelectionIntent");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Allow file with any mime-type because Android may not recognize sqlite3 file.
        intent.setType("*/*");
        startActivityForResult(intent, REQ_OPEN_DB_FILE);
    }
}
