package apps.cardina1.red.essentiallywebservice;

import android.database.Cursor;

import java.io.Serializable;

public class ResultTableCell implements Serializable {
    private String displayValue = "(uninitialized)";
    private int valueType = -1;

    public ResultTableCell(Cursor cursor, int col_i) {
        valueType = cursor.getType(col_i);
        switch (valueType) {
            case Cursor.FIELD_TYPE_BLOB:
                displayValue = "(blob (" + cursor.getBlob(col_i).length + " bytes))";
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                displayValue = String.valueOf(cursor.getDouble(col_i));
                break;
            case Cursor.FIELD_TYPE_INTEGER:
                displayValue = String.valueOf(cursor.getInt(col_i));
                break;
            case Cursor.FIELD_TYPE_NULL:
                displayValue = "(null)";
                break;
            case Cursor.FIELD_TYPE_STRING:
                displayValue = cursor.getString(col_i).toString();
                break;
        }
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public int getValueType() {
        return valueType;
    }

    public boolean isExactSerialization() {
        switch (getValueType()) {
            case Cursor.FIELD_TYPE_BLOB:
                return false;
            case Cursor.FIELD_TYPE_FLOAT:
                return true;
            case Cursor.FIELD_TYPE_INTEGER:
                return true;
            case Cursor.FIELD_TYPE_NULL:
                return false;
            case Cursor.FIELD_TYPE_STRING:
                return true;
            default:
                return false;
        }
    }
}
