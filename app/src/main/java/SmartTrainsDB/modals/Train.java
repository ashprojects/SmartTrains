package SmartTrainsDB.modals;

import android.content.ContentValues;

import java.util.HashMap;

import SmartTrainsDB.modals.fields.Field;
import SmartTrainsDB.modals.fields.Varchar;

/**
 * Created by root on 24/6/17.
 */

public class Train extends Modal implements Locomotive {
    public static final String TABLE_NAME = "trains";
    public static final String TRAIN_NO = "train_no";
    public static final String TRAIN_NAME = "train_name";
    public static final String FROM = "source";
    public static final String TO = "destination";
    protected static final HashMap<String, Field> fieldTypes = new HashMap<>();

    static {
        fieldTypes.put(TRAIN_NO, new Varchar(5, true, true));
        fieldTypes.put(TRAIN_NAME, new Varchar());
        fieldTypes.put(FROM, new Varchar(6));
        fieldTypes.put(TO, new Varchar(6));
    }

    public static final Train objects = new Train();

    @Override
    public String getModalName() {
        return TABLE_NAME;
    }

    @Override
    protected HashMap<String, Field> getFieldTypes() {
        return fieldTypes;
    }

    public Train addTrain(SmartTrainTools.Train T) {
        ContentValues values = new ContentValues();
        values.put(TRAIN_NO, T.getNo());
        values.put(TRAIN_NAME, T.getName());
        values.put(FROM, T.getSource().getCode() + " - " + T.getSource().getName());
        values.put(TO, T.getDestination().getCode() + " - " + T.getDestination().getName());
        return (Train) this.insert(values);
    }

    public SmartTrainTools.Train getTrain() {
        SmartTrainTools.Train train1 = new SmartTrainTools.Train(getNo(), getName(), null);
        train1.setRoute(TrainRoute.objects.getTrainRoute(getNo()));
        return train1;
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
