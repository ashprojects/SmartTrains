package SmartTrainsDB.modals;

import android.content.ContentValues;

import java.util.HashMap;

import SmartTrainTools.PNRStatus;
import SmartTrainsDB.modals.fields.BooleanField;
import SmartTrainsDB.modals.fields.DateTime;
import SmartTrainsDB.modals.fields.Field;
import SmartTrainsDB.modals.fields.Varchar;

/**
 * Created by root on 24/6/17.
 */

public class PNR extends Modal {
    public static final String TABLE_NAME = "pnr";
    public static final String PNR = "pnr";
    public static final String CHART_PREPARED = "chart_prepared";
    public static final String TRAVEL_CLASS = "travel_class";
    public static final String DATE_OF_JOURNEY = "date_of_journey";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String BOARDING_POINT = "boarding_point";
    public static final String RESERVATION_UPTO = "reservation_upto";
    public static final String TRAIN_NO = "train_no";


    public static final HashMap<String, Field> fieldTypes = new HashMap<>();

    static {
        fieldTypes.put(PNR, new Varchar(true, true));
        fieldTypes.put(CHART_PREPARED, new BooleanField());
        fieldTypes.put(TRAVEL_CLASS, new Varchar());
        fieldTypes.put(DATE_OF_JOURNEY, new DateTime());
        fieldTypes.put(FROM, new Varchar(6));
        fieldTypes.put(TO, new Varchar(6));
        fieldTypes.put(BOARDING_POINT, new Varchar(6));
        fieldTypes.put(RESERVATION_UPTO, new Varchar(6));
        fieldTypes.put(TRAIN_NO, new Varchar(5));
    }

    public static final PNR objects = new PNR();

    @Override
    public String getModalName() {
        return TABLE_NAME;
    }

    @Override
    protected HashMap<String, Field> getFieldTypes() {
        return fieldTypes;
    }

    public PNR addPNR(PNRStatus pnr) {
        ContentValues values = new ContentValues();
        values.put(PNR, pnr.getPNR());
        values.put(CHART_PREPARED, pnr.isChartPrepared());
        values.put(TRAVEL_CLASS, pnr.getTravelClass().getClassCode());
        values.put(DATE_OF_JOURNEY, pnr.getDateOfJourney().format(DateTime.dateTimeFormat));
        values.put(FROM, pnr.getFrom().getCode());
        values.put(TO, pnr.getTo().getCode());
        values.put(BOARDING_POINT, pnr.getBoardingPoint().getCode());
        values.put(RESERVATION_UPTO, pnr.getReservationUpto().getCode());
        values.put(TRAIN_NO, pnr.getTrainNo());
        return (SmartTrainsDB.modals.PNR) insert(values);
    }

}
