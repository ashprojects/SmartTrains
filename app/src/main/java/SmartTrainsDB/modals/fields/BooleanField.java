package SmartTrainsDB.modals.fields;


import android.database.Cursor;

public class BooleanField extends IntegerField {
    public BooleanField() {
        super();
    }

    @Override
    public boolean validate(Object object) {
        return super.validate(object) && (((Integer) object == 0) || ((Integer) object == 1));
    }

    @Override
    public Object getValue(Cursor cursor) {
        return super.getValue(cursor);
    }
}
