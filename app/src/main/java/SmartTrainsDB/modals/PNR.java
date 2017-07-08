package SmartTrainsDB.modals;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import SmartTrainTools.PNRStatus;
import SmartTrainTools.SmartUtils;
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
    public static final String FROM = "_from";
    public static final String TO = "_to";
    public static final String BOARDING_POINT = "boarding_point";
    public static final String RESERVATION_UPTO = "reservation_upto";
    public static final String TRAIN_NO = "train_no";
    public static final String UPDATED_AT = "updated_at";
    public static final String TRACKED = "TRACKED";


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
        fieldTypes.put(UPDATED_AT, new DateTime());
        fieldTypes.put(TRACKED, new BooleanField());
    }

    public static final PNR objects = new PNR();
    private ArrayList<Passenger> passengerCache = null;

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
        putValues(pnr, values);
        SmartTrainsDB.modals.PNR dbPNR = (SmartTrainsDB.modals.PNR) insert(values);
        dbPNR.passengerCache = new ArrayList<>();
        for (PNRStatus.Passenger passenger : pnr.getPassengers()) {
            dbPNR.passengerCache.add(Passenger.objects.add(passenger, pnr.getPNR()));
        }
        return dbPNR;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= 1) {
            db.execSQL(String.format("ALTER table %s ADD COLUMN %s %s", TABLE_NAME, TRACKED, BooleanField.sqlTypeName));
        }
    }

    public void updatePNR(PNRStatus pnr) {
        putValues(pnr, getValues());
        if (passengerCache == null || passengerCache.isEmpty()) {
            getPassengers();
        }
        if (passengerCache.size() == pnr.getPassengers().size()) {
            for (int i = 0; i < passengerCache.size(); i++) {
                Passenger passenger = passengerCache.get(i);
                passenger.put(Passenger.BOOKING_STATUS, pnr.getPassengers().get(i).getBookingStatus());
                passenger.put(Passenger.CURRENT_STATUS, pnr.getPassengers().get(i).getCurrentStatus());
                passenger.update();
            }
        }
        update();
    }

    private void putValues(PNRStatus pnr, ContentValues values) {
        values.put(PNR, pnr.getPNR());
        values.put(CHART_PREPARED, pnr.isChartPrepared());
        values.put(TRAVEL_CLASS, pnr.getTravelClass().getClassCode());
        values.put(DATE_OF_JOURNEY, pnr.getDateOfJourney().format(DateTime.dateTimeFormat));
        values.put(FROM, pnr.getFrom().getCode());
        values.put(TO, pnr.getTo().getCode());
        values.put(BOARDING_POINT, pnr.getBoardingPoint().getCode());
        values.put(RESERVATION_UPTO, pnr.getReservationUpto().getCode());
        values.put(TRAIN_NO, pnr.getTrainNo());
        values.put(UPDATED_AT, SmartUtils.now(DateTime.dateTimeFormat));
    }
    public boolean alreadyExists(String pnr) {
        return filter(PNR + "=?", new String[]{pnr}).size() > 0;
    }

    public ArrayList<Passenger> getPassengers() {
        if (passengerCache == null) {
            passengerCache = Passenger.objects.getPassengers(getValues().get(PNR).toString());
        }
        return passengerCache;
    }

    public PNR getPNR(String pnr) {
        ArrayList<Modal> pnrs = filter(PNR + "=?", new String[]{pnr});
        return pnrs.size() == 0 ? null : (SmartTrainsDB.modals.PNR) pnrs.get(0);
    }

    @Override
    public void delete() {
        Passenger.objects.delete(Passenger.PNR + "=?", new String[]{get(PNR).toString()});
        super.delete();
    }

    public void setTracked(boolean tracked) {
        put(TRACKED, tracked ? 1 : 0);
        update();
    }

    public boolean getTracked() {
        return get(TRACKED).toString().equals("1");
    }

    public ArrayList<PNR> getAllTrackedPNRs() {
        ArrayList<Modal> pnrs = filter(TRACKED + "=1", new String[]{});
        ArrayList<PNR> pnrs1 = new ArrayList<>();
        for (Modal pnr : pnrs) {
            pnrs1.add((SmartTrainsDB.modals.PNR) pnr);
        }
        return pnrs1;
    }
}
