package SmartTrainsDB.modals;

import android.database.sqlite.SQLiteDatabase;


public interface SQLiteOpenHelperCompactable {
    public void onCreate(SQLiteDatabase db);

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    public String getModalName();
}
