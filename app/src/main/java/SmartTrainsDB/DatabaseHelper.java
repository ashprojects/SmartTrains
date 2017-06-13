package SmartTrainsDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

import SmartTrainsDB.modals.Modal;
import commons.Config;

/**
 * Created by root on 11/6/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper ourInstance;
    private static HashMap<String, Modal> allModals = new HashMap<>();

    public static DatabaseHelper getInstance() {
        if (ourInstance == null) {
            ourInstance = new DatabaseHelper(Config.getContext(), DBConfig.dbName, null, DBConfig.version);
        }
        return ourInstance;
    }

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        for (Modal modal : DBConfig.allRegisteredModals) {
            allModals.put(modal.getModalName(), modal);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DBHelper on createRow");
        for (Modal modal : DBConfig.allRegisteredModals) {
            db.execSQL(modal.getCreateSQL());
            System.out.println(modal.getCreateSQL());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Modal getModal(String name) {
        return allModals.get(name);
    }
}
