package SmartTrainsDB.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import SmartTrainTools.SmartUtils;
import SmartTrainTools.Train;
import SmartTrainsDB.TrainBean;
import SmartTrainsDB.modals.fields.DateTime;
import SmartTrainsDB.modals.fields.Field;
import SmartTrainsDB.modals.fields.Varchar;

public class RecentTrain extends Modal {
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

    public List<TrainBean> getAllRecentTrain() {
        List<TrainBean> trainBeanArrayList = new ArrayList<>();
        Cursor cursor = this.getCursor(null, null, CREATED_AT + " DESC");

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                System.out.println("DB_FOUND:" + cursor.getString(1));
                TrainBean trainBean = new TrainBean();
                trainBean.setTrno(cursor.getString(cursor.getColumnIndex(TRAIN_NO)));
                trainBean.setTrname(cursor.getString(cursor.getColumnIndex(TRAIN_NAME)));
                trainBean.setFrom(cursor.getString(cursor.getColumnIndex(FROM)));
                trainBean.setTo(cursor.getString(cursor.getColumnIndex(TO)));
                // Adding contact to list
                trainBeanArrayList.add(trainBean);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trainBeanArrayList;
    }

}
