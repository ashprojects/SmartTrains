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
import SmartTrainsDB.modals.fields.Field;
import SmartTrainsDB.modals.fields.IntegerField;

/**
 * Created by root on 11/6/17.
 */

public abstract class Modal {
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
    }

    private void populateFieldName() {
        for (Map.Entry<String, Field> entry : getFeildTypes().entrySet()) {
            Field field = entry.getValue();
            field.setName(entry.getKey());
        }
    }

    private Field ensurePrimaryKey() {
        HashMap<String, Field> fieldTypes = getFeildTypes();
        boolean isPrimaryKeyPresent = false;
        for (Field field : fieldTypes.values()) {
            if (field.isPrimaryKey()) {
                isPrimaryKeyPresent = true;
                return field;
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
        StringBuilder selection = new StringBuilder("");
        for (Iterator<Map.Entry<String, Field>> iterator = getFeildTypes().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Field> entry = iterator.next();
            selection.append(entry.getKey() + " = ?");
            if (iterator.hasNext()) {
                selection.append(" AND ");
            }
        }
        return selection.toString();
    }

    protected String[] getSelectionArgs() {
        ArrayList<String> args = new ArrayList<>();
        for (Iterator<Map.Entry<String, Field>> iterator = getFeildTypes().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Field> entry = iterator.next();
            if (values.get(entry.getKey()) != null) {
                args.add(values.get(entry.getKey()).toString());
            } else {
                args.add("NULL");
            }
        }
        return args.toArray(new String[]{});
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

    public Modal createRow(ContentValues values) {
        Modal instance = getNewInstance();
        instance.values = new ContentValues(values);
        SQLiteDatabase db = getReadableDatabase();
        db.insert(getModalName(), null, values);
        db.close();
        return instance;
    }

    public ArrayList<Modal> filter(String selection, String[] selectionArgs) {
        Cursor cursor = getCursor(selection, selectionArgs);
        ArrayList<Modal> modals = new ArrayList<>();
        while (cursor.moveToNext()) {
            Modal newInstance = getNewInstance();
            for (Map.Entry<String, Field> entry : getFeildTypes().entrySet()) {
                newInstance.put(entry.getKey(), entry.getValue().getValue());
            }
            modals.add(newInstance);
        }
        return modals;
    }

    public ArrayList<Modal> all() {
        return filter(null, null);
    }

}
