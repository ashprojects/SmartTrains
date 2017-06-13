package SmartTrainsDB.modals;

import android.content.ContentValues;

import java.util.HashMap;

import SmartTrainsDB.DatabaseHelper;
import SmartTrainsDB.modals.fields.DateTime;
import SmartTrainsDB.modals.fields.DoubleField;
import SmartTrainsDB.modals.fields.Field;
import SmartTrainsDB.modals.fields.IntegerField;
import SmartTrainsDB.modals.fields.Varchar;

/**
 * Created by root on 12/6/17.
 */

public class TestTable extends Modal {
    public static HashMap<String, Field> fieldTypes = new HashMap<>();

    static {
        fieldTypes.put("id", new IntegerField(true));
        fieldTypes.put("name", new Varchar(32));
        fieldTypes.put("percent", new DoubleField());
        fieldTypes.put("date", new DateTime());
    }

    @Override
    public String getModalName() {
        return "TEST";
    }

    @Override
    protected HashMap<String, Field> getFeildTypes() {
        return fieldTypes;
    }

    public static void test() {
        TestTable table = (TestTable) DatabaseHelper.getInstance().getModal("TEST");
        DatabaseHelper.getInstance().getWritableDatabase();
//        System.out.println(table.getCreateSQL());
//        System.out.println(table.getAllFields());
//        DatabaseHelper.getInstance().getWritableDatabase().execSQL(
//
//                "insert into TEST values(1, \"blah\", 22.2, \"02-02-1995\")"
//        );
        ContentValues values = new ContentValues();
        //values.put("id", 3);
        values.put("name", "new test name");
        //
        // values.put("percent", 2.2);
        //values.put("date","sds");
        TestTable t = (TestTable) table.createRow(values);
        System.out.println(t);

    }
}
