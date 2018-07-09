package apps.cardina1.red.essentiallywebservice;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultTableActivity extends AppCompatActivity {
    private final static String ACTIVITY_TAG = "ResultTableActivity";
    public final static String QUERY_EXTRA = "query";
    public final static String STATUS_EXTRA = "status";
    public final static String ERROR_MSG_EXTRA = "error_msg";
    public final static String RESULT_TABLE_EXTRA = "result_table";

    ResultTable table = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_table);
        Intent intent = getIntent();

        String query = intent.getStringExtra(QUERY_EXTRA);
        boolean status = intent.getBooleanExtra(STATUS_EXTRA, false);
        table = (ResultTable) intent.getSerializableExtra(RESULT_TABLE_EXTRA);

        ((TextView) findViewById(R.id.text_query)).setText(query + ";");
        String statusString = status ? "Success" : "Failed";
        ((TextView) findViewById(R.id.text_status)).setText(statusString);
        if (status) {
            TableLayout tableView = (TableLayout) findViewById(R.id.table_result_table);
            table.setupTableView(tableView, this);
        } else {
            ((TextView) findViewById(R.id.text_status)).setTextColor(0xFFFF0000);

            String errorMsg = intent.getStringExtra(ERROR_MSG_EXTRA);
            TextView errorMsgView = (TextView) findViewById(R.id.text_error_msg);
            errorMsgView.setText(errorMsg);
            errorMsgView.setVisibility(View.VISIBLE);
        }
    }
}
