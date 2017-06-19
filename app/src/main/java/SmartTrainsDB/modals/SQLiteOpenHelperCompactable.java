package SmartTrainsDB.modals;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by root on 19/6/17.
 */

public interface SQLiteOpenHelperCompactable {
    public void onCreate(SQLiteDatabase db);

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    public String getModalName();
}
