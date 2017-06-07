package SmartTrainsSQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import SmartTrainTools.RouteListItem;
import SmartTrainTools.Station;
import SmartTrainTools.Time;
import SmartTrainTools.Train;

/**
 * Created by root on 27/5/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private final String DATABASE_NAME="smart_trains";
    private final int DATABASE_VERSION=1;
    private final String RECENTS_TABLE_NAME ="train_searches";
    private final String RECENTS_KEY_NO ="trno";
    private final String RECENTS_KEY_NAME ="trname";
    private final String RECENTS_KEY_FROM ="src";
    private final String RECENTS_KEY_TO ="dest";
    private final String RECENTS_KEY_TS="ts";

    private final String TRAINS_TABLE_NAME="trains";
    private final String TRAINS_KEY_NO="no";
    private final String TRAINS_KEY_NAME="name";
    private final String TRAINS_KEY_SRC="src";
    private final String TRAINS_KEY_DEST="dest";

    private final String TRAIN_ROUTE_TABLE_NAME="train_route";
    private final String TRAIN_ROUTE_TRNUMBER="no";
    private final String TRAIN_ROUTE_COUNT="i";
    private final String TRAIN_ROUTE_STCODE="scode";

    private final String TRAIN_ROUTE_ARRTIME="arrtime";
    private final String TRAIN_ROUTE_DEPTTIME="deptime";
    private final String TRAIN_ROUTE_DISTANCE="dist";
    private final String TRAIN_ROUTE_DAYNUMBER="day";



    public DatabaseHandler(Context context) {
        super(context, "smart_trains", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String qry="CREATE TABLE "+ RECENTS_TABLE_NAME +" ("+ RECENTS_KEY_NO +" VARCHAR(5) PRIMARY KEY,"+ RECENTS_KEY_NAME +" VARCHAR(30),"+ RECENTS_KEY_FROM +" VARCHAR(6),"+ RECENTS_KEY_TO +" VARCHAR(6),"+RECENTS_KEY_TS+" DATETIME)";
        sqLiteDatabase.execSQL(qry);

        qry="CREATE TABLE "+TRAINS_TABLE_NAME+" ("+TRAINS_KEY_NO+" varchar(6) PRIMARY KEY, "+TRAINS_KEY_NAME+" varchar(35),"+TRAINS_KEY_SRC+" varchar(6),"+TRAINS_KEY_DEST+" varchar(6))";
        sqLiteDatabase.execSQL(qry);

        qry="CREATE TABLE "+TRAIN_ROUTE_TABLE_NAME+" ("+TRAIN_ROUTE_TRNUMBER+" varchar(6), "+TRAIN_ROUTE_COUNT+" integer, "+TRAIN_ROUTE_STCODE+" varchar(6),"+TRAIN_ROUTE_ARRTIME+" varchar(9),"+TRAIN_ROUTE_DEPTTIME+" varchar(9),"+TRAIN_ROUTE_DISTANCE+" integer,"+TRAIN_ROUTE_DAYNUMBER+" integer)";
        sqLiteDatabase.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    public void addTrain(Train T) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RECENTS_KEY_NO,T.getNo());
        values.put(RECENTS_KEY_NAME, T.getName()); // Contact Name
        values.put(RECENTS_KEY_FROM, T.getSource().getCode()+" - "+T.getSource().getName()); // Contact Phone Number
        values.put(RECENTS_KEY_TO,T.getDestination().getCode()+" - "+T.getDestination().getName());
        values.put(RECENTS_KEY_TS,getDateTime());
        // Inserting Row
        db.insert(RECENTS_TABLE_NAME, null, values);
        db.close(); // Closing database connectiRecenon
        System.out.println("DB_ADDED:"+T.getName());
    }


    public void addTrainRoute(Train T){
        if(containsSavedRoute(T))
            return;
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values;
        values=new ContentValues();
        values.put(TRAINS_KEY_NO,T.getNo());
        values.put(TRAINS_KEY_NAME,T.getName());
        values.put(TRAINS_KEY_SRC,T.getSource().getCode());
        values.put(TRAINS_KEY_DEST,T.getDestination().getCode());
        db.insert(TRAINS_TABLE_NAME,null,values);
        System.out.println("DB: ADDING "+T.getName());
        int i=1;
        for(RouteListItem R:T.getRoute()){
            values=new ContentValues();
            values.put(TRAIN_ROUTE_TRNUMBER,T.getNo());
            values.put(TRAIN_ROUTE_STCODE,R.getStation().getCode());
            values.put(TRAIN_ROUTE_ARRTIME,R.getArrivalTime().toString());
            values.put(TRAIN_ROUTE_DEPTTIME,R.getDepartureTime().toString());
            values.put(TRAIN_ROUTE_DAYNUMBER,R.getDay());
            values.put(TRAIN_ROUTE_COUNT,i++);
            values.put(TRAIN_ROUTE_DISTANCE,R.getDistanceFromSource());
            db.insert(TRAIN_ROUTE_TABLE_NAME,null,values);
            System.out.println("ADDED: "+R.getStation());
        }
        db.close();

    }

    public boolean containsSavedRoute(Train T){
        String query="SELECT * FROM "+TRAIN_ROUTE_TABLE_NAME+" WHERE "+TRAIN_ROUTE_TRNUMBER+" = "+T.getNo();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt!=0;

    }

    public ArrayList<TrainBean> getSavedRoutesBeans(){
        SQLiteDatabase db=this.getWritableDatabase();

        ArrayList<TrainBean> trainBean= new ArrayList<>();
        String qry="SELECT DISTINCT b."+TRAIN_ROUTE_TRNUMBER+",a."+TRAINS_KEY_NAME+",a."+TRAINS_KEY_SRC+",a."+TRAINS_KEY_DEST+" FROM "+TRAIN_ROUTE_TABLE_NAME+" b, "+TRAINS_TABLE_NAME+" a where a."+TRAINS_KEY_NO+"=b."+TRAIN_ROUTE_TRNUMBER;
        Cursor cursor=db.rawQuery(qry,null);
//        "SELECT DISTINCT b.no, a.name, a.src, a.dest from train_route b, trains a where a.no=b.no;"
        if(cursor.moveToFirst()){

            do{
                TrainBean tb=new TrainBean();
                tb.setTrno(cursor.getString(0));
                tb.setTrname(cursor.getString(1));
                tb.setFrom(cursor.getString(2));
                tb.setTo(cursor.getString(3));
                trainBean.add(tb);
            }while (cursor.moveToNext());
        }
        db.close();
        return trainBean;

    }

    public void deleteTrainRouteFor(String T){
        SQLiteDatabase db=this.getWritableDatabase();
        String qry="DELETE FROM "+TRAIN_ROUTE_TABLE_NAME+" WHERE "+TRAIN_ROUTE_TRNUMBER+" = "+T;
        db.execSQL(qry);
        db.close();
    }

    public Train getTrainOffline(TrainBean T){
        ArrayList<RouteListItem> routeListItems=new ArrayList<>();
        String query="SELECT * FROM "+TRAIN_ROUTE_TABLE_NAME+" WHERE "+TRAIN_ROUTE_TRNUMBER+" = "+T.getTrno() +" ORDER BY "+TRAIN_ROUTE_COUNT;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        System.out.println("DB:"+T.getTrno());
        Train train=new Train(T.getTrno(),T.getTrname(),null);
        System.out.println("DB: TOTAL ENTRIES FOR "+T.getTrname()+cursor.getCount());
        if(cursor.moveToFirst()){
            do{
                Station S=new Station(cursor.getString(2));
                RouteListItem rt=new RouteListItem(S);
                rt.setArrivalTime(Time.returnTimeInstanceFromString(cursor.getString(3)));
                rt.setDepartureTime(Time.returnTimeInstanceFromString(cursor.getString(4)));
                rt.setDay(cursor.getInt(6));
                rt.setDistanceFromSource(cursor.getInt(5));
                routeListItems.add(rt);
            } while (cursor.moveToNext());
        }
        train.setRoute(routeListItems);
        System.out.println("DB: FETCH SAVED ROUTE FOR: "+train.getName());
        return train;
    }


    public List<TrainBean> getAllTrainSearches() {
        List<TrainBean> trainBeanArrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + RECENTS_TABLE_NAME +" ORDER BY TS DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                System.out.println("DB_FOUND:"+cursor.getString(1));
                TrainBean trainBean = new TrainBean();
                trainBean.setTrno(cursor.getString(0));
                trainBean.setTrname(cursor.getString(1));
                trainBean.setFrom(cursor.getString(2));
                trainBean.setTo(cursor.getString(3));
                // Adding contact to list
                trainBeanArrayList.add(trainBean);
            } while (cursor.moveToNext());
        }
        System.out.println("RETURNING "+trainBeanArrayList.size()+" BEANS");
        return trainBeanArrayList;
    }

    public int updateTrainSearch(TrainBean trainBean) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RECENTS_KEY_NAME, trainBean.getTrname());
        values.put(RECENTS_KEY_TS,getDateTime());

        // updating row
        return db.update(RECENTS_TABLE_NAME, values, RECENTS_KEY_NO + " = ?",
                new String[] { String.valueOf(trainBean.getTrno()) });
    }

    public void deleteTrainSearch(TrainBean trainBean) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RECENTS_TABLE_NAME, RECENTS_KEY_NO + " = ?",
                new String[] { String.valueOf(trainBean.getTrno()) });
        db.close();
    }
    public void deleteAllTrainSearches(){
        String s="DELETE FROM "+ RECENTS_TABLE_NAME;
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(s);
        db.close();
    }

    public void deleteAllTrainRoutes(){
        String s="DELETE FROM "+ TRAIN_ROUTE_TABLE_NAME;
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(s);
        db.close();
    }
}
