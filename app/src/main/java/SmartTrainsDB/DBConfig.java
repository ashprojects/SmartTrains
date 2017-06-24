package SmartTrainsDB;

import SmartTrainsDB.modals.Modal;
import SmartTrainsDB.modals.SQLiteOpenHelperCompactable;

/**
 * Created by root on 11/6/17.
 */

public class DBConfig {
    public static final String dbName = "smart_trains";
    public static final int version = 1;

    public static final SQLiteOpenHelperCompactable[] allRegisteredModals = new Modal[]{
    };
}
