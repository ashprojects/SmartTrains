package SmartTrainsDB.modals.fields;

import android.database.Cursor;

/**
 *  abstract class to represent a column to table in db
 */
public abstract class Field {
    private boolean isPrimaryKey = false, unique = false, notNull = false;
    private String name;

    public Field() {
    }

    public Field(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public Field(boolean unique, boolean notNull) {
        this.unique = unique;
        this.notNull = notNull;
    }

    public String getDefinition() {
        return name + " "
                + getSqlTypeName()
                + " " + (isPrimaryKey ? " PRIMARY KEY " : (unique ? " UNIQUE " : " "))
                + " " + (notNull ? " NOT NULL " : " ");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name == null) {
            this.name = name;
        }
    }
    public abstract String getSqlTypeName();

    public abstract boolean validate(Object object);

    public abstract Object getValue(Cursor cursor);

    @Override
    public String toString() {
        return getSqlTypeName().toString();
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
}
