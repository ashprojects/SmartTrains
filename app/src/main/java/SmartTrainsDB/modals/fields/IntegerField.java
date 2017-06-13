package SmartTrainsDB.modals.fields;

import android.database.Cursor;

/**
 * Created by root on 11/6/17.
 */

public class IntegerField extends Field {
    private static final String sqlTypeName = "INT";

    public IntegerField(boolean isPrimaryKey) {
        super(isPrimaryKey);
    }

    public IntegerField() {
        super(false);
    }

    @Override
    public Integer getValue() {
        if (this.value instanceof Integer) {
            return (Integer) this.value;
        }
        return Integer.parseInt(this.value.toString());
    }

    @Override
    public void setValue(String value) {
        this.value = Integer.parseInt(value);
    }

    @Override
    public String getSqlTypeName() {
        return sqlTypeName;
    }

    @Override
    public boolean validate(Object object) {
        if (this.value instanceof Integer) {
            return true;
        }
        try {
            Integer.parseInt(object.toString());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Object getValue(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(getName()));
    }
}
