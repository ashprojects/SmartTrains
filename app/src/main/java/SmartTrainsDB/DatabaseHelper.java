package SmartTrainsDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

import SmartTrainsDB.modals.Modal;
import SmartTrainsDB.modals.SQLiteOpenHelperCompactable;
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
            ourInstance.getWritableDatabase().close();
        }
        return ourInstance;
    }

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        for (SQLiteOpenHelperCompactable modal : DBConfig.allRegisteredModals) {
            if (modal instanceof Modal) {
                allModals.put(modal.getModalName(), (Modal) modal);
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (SQLiteOpenHelperCompactable modal : DBConfig.allRegisteredModals) {
            modal.onCreate(db);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (SQLiteOpenHelperCompactable modal : DBConfig.allRegisteredModals) {
            modal.onUpgrade(db, oldVersion, newVersion);
        }

    }

    public Modal getModal(String name) {
        return allModals.get(name);
    }
}
