package SmartTrainsDB.modals;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.List;

import SmartTrainTools.SmartUtils;
import SmartTrainTools.Train;
import SmartTrainsDB.TrainBean;
import SmartTrainsDB.modals.fields.DateTime;
import SmartTrainsDB.modals.fields.Field;
import SmartTrainsDB.modals.fields.Varchar;

public class RecentTrain extends Modal implements Locomotive {
    public static final String TABLE_NAME = "train_searches";
    public static final String TRAIN_NO = "train_no";
    public static final String TRAIN_NAME = "train_name";
    public static final String FROM = "source";
    public static final String TO = "destination";
    public static final String CREATED_AT = "created_at";
    public static final HashMap<String, Field> fieldTypes = new HashMap<>();

    static {
        fieldTypes.put(TRAIN_NO, new Varchar(5, true, true));
        fieldTypes.put(TRAIN_NAME, new Varchar());
        fieldTypes.put(FROM, new Varchar(6));
        fieldTypes.put(TO, new Varchar(6));
        fieldTypes.put(CREATED_AT, new DateTime(true, true));
    }

    public static final RecentTrain objects = new RecentTrain();

    @Override
    public String getModalName() {
        return TABLE_NAME;
    }

    @Override
    protected HashMap<String, Field> getFieldTypes() {
        return fieldTypes;
    }

    public RecentTrain addTrain(Train T) {
        ContentValues values = new ContentValues();
        values.put(TRAIN_NO, T.getNo());
        values.put(TRAIN_NAME, T.getName());
        values.put(FROM, T.getSource().getCode() + " - " + T.getSource().getName());
        values.put(TO, T.getDestination().getCode() + " - " + T.getDestination().getName());
        values.put(CREATED_AT, SmartUtils.now(DateTime.dateTimeFormat));
        return (RecentTrain) this.insert(values);
    }

    public void deleteAllRecentTrains() {
        this.delete(null, null);
    }

    public void deleteTrainSearch(TrainBean trainBean) {
        this.delete(TRAIN_NO, new String[]{trainBean.getTrno()});
    }

    public void updateRecentTrain(TrainBean trainBean) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TRAIN_NAME, trainBean.getTrname());
        values.put(CREATED_AT, SmartUtils.now(DateTime.dateTimeFormat));

        // updating row
        db.update(getModalName(), values, TRAIN_NO + " = ?",
                new String[]{trainBean.getTrno()});
    }

    public List getAllRecentTrain() {
        return objects.all();
    }

    @Override
    public String getName() {
        return get(TRAIN_NAME).toString();
    }

    @Override
    public String getNo() {
        return get(TRAIN_NO).toString();
    }

    @Override
    public String getFrom() {
        return get(FROM).toString();
    }

    @Override
    public String getTo() {
        return get(TO).toString();
    }
}
