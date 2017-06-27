package SmartTrainsDB;

import SmartTrainsDB.modals.Modal;
import SmartTrainsDB.modals.PNR;
import SmartTrainsDB.modals.Passenger;
import SmartTrainsDB.modals.RecentTrain;
import SmartTrainsDB.modals.SQLiteOpenHelperCompactable;
import SmartTrainsDB.modals.Train;
import SmartTrainsDB.modals.TrainRoute;

public class DBConfig {
    public static final String dbName = "smart_trains";
    public static final int version = 1;

    public static final SQLiteOpenHelperCompactable[] allRegisteredModals = new Modal[]{
            Train.objects,
            RecentTrain.objects,
            TrainRoute.objects,
            PNR.objects,
            Passenger.objects
    };
}
