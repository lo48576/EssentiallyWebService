package apps.cardina1.red.essentiallywebservice;

import android.content.Intent;
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
        if (!status) {
            ((TextView) findViewById(R.id.text_status)).setTextColor(0xFFFF0000);
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        TableLayout tableView = (TableLayout) findViewById(R.id.table_result_table);

        if (table != null) {
            // Add headers.
            if (!table.columnNames.isEmpty()) {
                TableRow rowView = new TableRow(this);
                for (String name: table.columnNames) {
                    inflater.inflate(R.layout.result_table_header_cell, rowView);
                    TextView columnView = (TextView) rowView.getChildAt(rowView.getChildCount() - 1);
                    columnView.setText(name);
                }
                tableView.addView(rowView);
            }

            // Add cells.
            for (ArrayList<ResultTableCell> row: table.rows) {
                TableRow rowView = new TableRow(this);
                for (ResultTableCell cell: row) {
                    inflater.inflate(R.layout.result_table_cell, rowView);
                    TextView columnView = (TextView) rowView.getChildAt(rowView.getChildCount() - 1);
                    columnView.setText(cell.getDisplayValue());
                    if (!cell.isExactSerialization()) {
                        // color: 0xAARRGGBB.
                        // See <https://developer.android.com/reference/android/widget/TextView.html#setTextColor(int)>.
                        columnView.setTextColor(0xFFFF7777);
                    }
                }
                tableView.addView(rowView);
            }
        }
    }
}
