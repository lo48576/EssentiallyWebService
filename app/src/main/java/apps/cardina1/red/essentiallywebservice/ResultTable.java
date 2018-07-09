package apps.cardina1.red.essentiallywebservice;

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
}
