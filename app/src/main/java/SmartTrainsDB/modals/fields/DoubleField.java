package SmartTrainsDB.modals.fields;

import android.database.Cursor;

/**
 * Created by root on 11/6/17.
 */

public class DoubleField extends Field {
    private static final String sqlTypeName = "DOUBLE";

    public DoubleField(boolean isPrimaryKey) {
        super(isPrimaryKey);
    }

    public DoubleField() {
    }

    @Override
    public Double getValue() {
        if (this.value instanceof Double) {
            return (Double) this.value;
        }
        return Double.parseDouble(this.value.toString());
    }

    @Override
    public void setValue(String value) {
        this.value = Double.parseDouble(value);
    }

    @Override
    public String getSqlTypeName() {
        return sqlTypeName;
    }

    @Override
    public boolean validate(Object object) {
        if (this.value instanceof Double) {
            return true;
        }
        try {
            Double.parseDouble(object.toString());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Object getValue(Cursor cursor) {
        return cursor.getDouble(cursor.getColumnIndex(getName()));
    }
}
