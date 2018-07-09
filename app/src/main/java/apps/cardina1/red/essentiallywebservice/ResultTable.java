package apps.cardina1.red.essentiallywebservice;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class ResultTable implements Serializable {
    // TODO: should not be public.
    public ArrayList<ArrayList<ResultTableCell>> rows;
    public ArrayList<String> columnNames;

    public ResultTable() {
        rows = new ArrayList<>();
        columnNames = new ArrayList<>();
    }

    public void setColumnNames(ArrayList<String> names) {
        columnNames = names;
    }

    public void addRow(ArrayList<ResultTableCell> row) {
        rows.add(row);
    }

    public void setupTableView(TableLayout tableView, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Add headers.
        if (!columnNames.isEmpty()) {
            TableRow rowView = new TableRow(context);
            for (String name: columnNames) {
                inflater.inflate(R.layout.result_table_header_cell, rowView);
                TextView columnView = (TextView) rowView.getChildAt(rowView.getChildCount() - 1);
                columnView.setText(name);
            }
            tableView.addView(rowView);
        }

        // Add cells.
        for (ArrayList<ResultTableCell> row: rows) {
            TableRow rowView = new TableRow(context);
            for (ResultTableCell cell: row) {
                inflater.inflate(R.layout.result_table_cell, rowView);
                TextView columnView = (TextView) rowView.getChildAt(rowView.getChildCount() - 1);
                columnView.setText(cell.getDisplayValue());
                if (!cell.isExactSerialization()) {
                    // color: 0xAARRGGBB.
                    // See <https://developer.android.com/reference/android/widget/TextView.html#setTextColor(int)>.
                    columnView.setTextColor(0xFFFF7777);
                }
                switch (cell.getValueType()) {
                    case Cursor.FIELD_TYPE_BLOB:
                        columnView.setBackgroundColor(0xFFDDDDDD);
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        columnView.setBackgroundColor(0xFFDDDDDD);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        columnView.setBackgroundColor(0xFFDDDDFF);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        columnView.setBackgroundColor(0xFFDDDDFF);
                        break;
                }
            }
            tableView.addView(rowView);
        }
    }
}
