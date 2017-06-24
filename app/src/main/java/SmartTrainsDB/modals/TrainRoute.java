package SmartTrainsDB.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import SmartTrainTools.RouteListItem;
import SmartTrainTools.Station;
import SmartTrainTools.Time;
import SmartTrainsDB.TrainBean;
import SmartTrainsDB.modals.fields.Field;
import SmartTrainsDB.modals.fields.IntegerField;
import SmartTrainsDB.modals.fields.Varchar;


public class TrainRoute extends Modal {
    public static final String TABLE_NAME = "train_route";
    public static final String TRAIN_NO = "train_no";
    public static final String STATION_NO = "station_no";
    public static final String STATION_CODE = "station_code";
    public static final String ARRIVAL_TIME = "arrival_time";

    public static final String DEPARTURE_TIME = "departure_time";
    public static final String DISTANCE = "distance";
    public static final String DAY = "day";
    private static final HashMap<String, Field> fieldTypes = new HashMap<>();

    static {
        fieldTypes.put(TRAIN_NO, new Varchar(5));
        fieldTypes.put(STATION_CODE, new Varchar(6));
        fieldTypes.put(STATION_NO, new IntegerField());
        fieldTypes.put(ARRIVAL_TIME, new Varchar(9));
        fieldTypes.put(DEPARTURE_TIME, new Varchar(9));
        fieldTypes.put(DISTANCE, new IntegerField());
        fieldTypes.put(DAY, new IntegerField());
    }

    public static final TrainRoute objects = new TrainRoute();

    @Override
    public String getModalName() {
        return TABLE_NAME;
    }

    @Override
    protected HashMap<String, Field> getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        db.execSQL(String.format("CREATE INDEX train_route_no on %s (%s, %s)", getModalName(), TRAIN_NO, STATION_NO));
    }

    public void addTrainRoute(SmartTrainTools.Train train) {
        if (containsSavedRoute(train))
            return;
        Train.objects.addTrain(train);
        int i = 1;
        for (RouteListItem R : train.getRoute()) {
            ContentValues values = new ContentValues();
            values.put(TRAIN_NO, train.getNo());
            values.put(STATION_CODE, R.getStation().getCode());
            values.put(ARRIVAL_TIME, R.getArrivalTime().toString());
            values.put(DEPARTURE_TIME, R.getDepartureTime().toString());
            values.put(DAY, R.getDay());
            values.put(STATION_NO, i++);
            values.put(DISTANCE, R.getDistanceFromSource());
            this.insert(values);
        }

    }

    public boolean containsSavedRoute(SmartTrainTools.Train train) {
        return !this.filter(TRAIN_NO + " =?", new String[]{train.getNo()}).isEmpty();
    }

    public void deleteTrainRouteFor(String trainNo) {
        this.delete(TRAIN_NO + "=?", new String[]{trainNo});
    }

    public void deleteAllTrainRoutes() {
        this.delete(null, null);
    }

    public ArrayList<TrainBean> getSavedRoutesTrainBeans() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<TrainBean> trainBean = new ArrayList<>();
        String query = String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s in (SELECT DISTINCT %s from %s)",
                Train.TRAIN_NO, Train.TRAIN_NAME, Train.FROM, Train.TO,
                Train.objects.getModalName(),
                Train.TRAIN_NO,
                TrainRoute.TRAIN_NO, TrainRoute.objects.getModalName()
        );
        //String query = "SELECT ?, ?, ?, ? FROM ? WHERE ? in (SELECT DISTINCT ? from ?)";
        String[] selectionArgs = new String[]{
                Train.TRAIN_NO, Train.TRAIN_NAME, Train.FROM, Train.TO,
                Train.objects.getModalName(),
                Train.TRAIN_NO,
                TrainRoute.TRAIN_NO, TrainRoute.objects.getModalName()
        };
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {

            do {
                TrainBean tb = new TrainBean();
                tb.setTrno(cursor.getString(0));
                tb.setTrname(cursor.getString(1));
                tb.setFrom(cursor.getString(2));
                tb.setTo(cursor.getString(3));
                trainBean.add(tb);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trainBean;

    }

    public ArrayList<RouteListItem> getTrainRoute(TrainBean train) {
        ArrayList<RouteListItem> routeListItems = new ArrayList<>();
        Cursor cursor = this.getCursor(TRAIN_NO + "= ?", new String[]{train.getTrno()}, STATION_NO);
        if (cursor.moveToFirst()) {
            do {
                Station S = new Station(cursor.getString(cursor.getColumnIndex(STATION_CODE)));
                RouteListItem rt = new RouteListItem(S);
                rt.setArrivalTime(Time.returnTimeInstanceFromString(cursor.getString(cursor.getColumnIndex(ARRIVAL_TIME))));
                rt.setDepartureTime(Time.returnTimeInstanceFromString(cursor.getString(cursor.getColumnIndex(DEPARTURE_TIME))));
                rt.setDay(cursor.getInt(cursor.getColumnIndex(DAY)));
                rt.setDistanceFromSource(cursor.getInt(cursor.getColumnIndex(DISTANCE)));
                routeListItems.add(rt);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return routeListItems;
    }
}
