package SmartTrainsDB.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import SmartTrainsDB.DatabaseHelper;
import SmartTrainsDB.modals.exceptions.ImproperlyConfiguredException;
import SmartTrainsDB.modals.fields.Field;
import SmartTrainsDB.modals.fields.IntegerField;

/**
 * Created by root on 11/6/17.
 */

public abstract class Modal implements SQLiteOpenHelperCompactable {
    private ContentValues values = new ContentValues();
    private Field primaryKey;

    public abstract String getModalName();

    protected abstract HashMap<String, Field> getFeildTypes();

    protected Modal getNewInstance() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Modal() {
        populateFieldName();
        primaryKey = ensurePrimaryKey();
    }

    private void populateFieldName() {
        for (Map.Entry<String, Field> entry : getFeildTypes().entrySet()) {
            Field field = entry.getValue();
            field.setName(entry.getKey());
        }
    }

    private Field ensurePrimaryKey() {
        HashMap<String, Field> fieldTypes = getFeildTypes();
        for (Field field : fieldTypes.values()) {
            if (field.isPrimaryKey()) {
                if (!field.getName().equals("_id") || !(field instanceof IntegerField))
                    throw new ImproperlyConfiguredException("primary key should be named _id and should be int");
            }
        }
        Field field = new IntegerField(true);
        fieldTypes.put("_id", field);
        return field;
    }

    public Set<String> getAllFields() {
        return getFeildTypes().keySet();
    }

    public String getCreateSQL() {
        StringBuilder createCommand = new StringBuilder("CREATE TABLE ");
        createCommand.append(getModalName()).append("(");
        for (Iterator<Map.Entry<String, Field>> iterator = getFeildTypes().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Field> entry = iterator.next();
            Field field = entry.getValue();
            createCommand.append(field.getDefinition());
            if (iterator.hasNext()) {
                createCommand.append(", ");
            }
        }
        createCommand.append(" )");
        return createCommand.toString();
    }

    public boolean put(String fieldName, Object value) {
        if (!getAllFields().contains(fieldName)) {
            return false;
        }
        if (value != null && !getFeildTypes().get(fieldName).validate(value)) {
            return false;
        }
        if (value == null) {
            values.putNull(fieldName);
        } else {
            values.put(fieldName, value.toString());
        }
        return true;
    }

    public Object get(String fieldName) {
        return values.get(fieldName);
    }

    public final SQLiteDatabase getWritableDatabase() {
        return DatabaseHelper.getInstance().getWritableDatabase();
    }

    public final SQLiteDatabase getReadableDatabase() {
        return DatabaseHelper.getInstance().getReadableDatabase();
    }

    protected String getSelection() {
        return primaryKey.getName() + "=?";
    }

    protected String[] getSelectionArgs() {
        return new String[]{values.get(primaryKey.getName()).toString()};
    }

    public void delete() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(getModalName(), getSelection(), getSelectionArgs());
    }

    public void update() {
        getWritableDatabase().update(getModalName(), values, getSelection(), getSelectionArgs());
    }

    public Cursor getCursor(String selection, String[] selectionArgs) {
        return getReadableDatabase().query(
                getModalName(),
                getAllFields().toArray(new String[]{}),
                selection,
                selectionArgs,
                null, null, null
        );
    }

    public Modal insert(ContentValues values) {
        Modal instance = getNewInstance();
        instance.values = new ContentValues(values);
        SQLiteDatabase db = getReadableDatabase();
        instance.values.put(primaryKey.getName(), db.insert(getModalName(), null, values));
        db.close();
        return instance;
    }

    public ArrayList<Modal> filter(String selection, String[] selectionArgs) {
        Cursor cursor = getCursor(selection, selectionArgs);
        ArrayList<Modal> modals = new ArrayList<>();
        while (cursor.moveToNext()) {
            Modal newInstance = getNewInstance();
            for (Map.Entry<String, Field> entry : getFeildTypes().entrySet()) {
                newInstance.put(entry.getKey(), entry.getValue().getValue(cursor));
            }
            modals.add(newInstance);
        }
        return modals;
    }

    public ArrayList<Modal> all() {
        return filter(null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
