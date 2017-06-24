package SmartTrainsDB.modals.fields;

import android.database.Cursor;

/**
 * Created by root on 11/6/17.
 */

public class Varchar extends Field {
    private static final String sqlTypeName = "VARCHAR";
    private int maxLength = 32;

    public Varchar(int maxLength) {
        this(maxLength, false);
    }

    public Varchar(boolean isPrimaryKey) {
        super(isPrimaryKey);
    }

    public Varchar() {
        super(false);
    }

    public Varchar(int maxLength, boolean isPrimaryKey) {
        super(isPrimaryKey);
        if (maxLength < 0) {
            maxLength = -maxLength;
        }
        this.maxLength = maxLength;
    }

    public Varchar(int maxLength, boolean unique, boolean notNull) {
        super(unique, notNull);
        this.maxLength = maxLength;
    }

    public Varchar(boolean unique, boolean notNull) {
        super(unique, notNull);
    }

    @Override
    public String getSqlTypeName() {
        return sqlTypeName + "(" + maxLength + ") ";
    }

    @Override
    public boolean validate(Object object) {
        return true;
    }

    @Override
    public Object getValue(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(getName()));
    }
}
