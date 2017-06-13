package SmartTrainsDB.modals.fields;

import android.database.Cursor;

/**
 * Created by root on 11/6/17.
 */

public abstract class Field {
    private boolean isPrimaryKey = false;
    private String name;
    protected Object value;

    public Field() {
    }

    public Field(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getDefinition() {
        return name + " " + getSqlTypeName() + " " + (isPrimaryKey ? " PRIMARY KEY " : " ");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name == null) {
            this.name = name;
        }
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public abstract String getSqlTypeName();

    public abstract boolean validate(Object object);

    public abstract Object getValue(Cursor cursor);

    @Override
    public String toString() {
        return getValue().toString();
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
}
