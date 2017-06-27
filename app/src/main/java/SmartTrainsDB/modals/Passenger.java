package SmartTrainsDB.modals;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.HashMap;

import SmartTrainTools.PNRStatus;
import SmartTrainsDB.modals.fields.Field;
import SmartTrainsDB.modals.fields.Varchar;

public class Passenger extends Modal {
    public static final String TABLE_NAME = "passenger";
    public static final String PNR = "pnr";
    public static final String BOOKING_STATUS = "booking_status";
    public static final String CURRENT_STATUS = "current_status";

    public static final HashMap<String, Field> fieldTypes = new HashMap<>();

    static {
        fieldTypes.put(PNR, new Varchar(false, true));
        fieldTypes.put(BOOKING_STATUS, new Varchar());
        fieldTypes.put(CURRENT_STATUS, new Varchar());
    }

    public static final Passenger objects = new Passenger();

    @Override
    public String getModalName() {
        return TABLE_NAME;
    }

    @Override
    protected HashMap<String, Field> getFieldTypes() {
        return fieldTypes;
    }

    public ArrayList<Passenger> getPassengers(String pnr) {
        ArrayList<Passenger> list = new ArrayList<>();
        for (Modal modal : filter(PNR + " =?", new String[]{pnr}, "_id")) {
            list.add((Passenger) modal);
        }
        return list;
    }

    public Passenger add(PNRStatus.Passenger passenger, String pnr) {
        ContentValues values = new ContentValues();
        values.put(BOOKING_STATUS, passenger.getBookingStatus());
        values.put(CURRENT_STATUS, passenger.getCurrentStatus());
        values.put(PNR, pnr);
        return (Passenger) insert(values);
    }
}
