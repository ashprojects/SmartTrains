package SmartTrainsDB;

import SmartTrainsDB.modals.Modal;
import SmartTrainsDB.modals.TestTable;

/**
 * Created by root on 11/6/17.
 */

public class DBConfig {
    public static final String dbName = "smart_trains";
    public static final int version = 1;

    public static final Modal[] allRegisteredModals = new Modal[]{
            new TestTable()
    };
}
