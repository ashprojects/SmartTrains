package jpro.smarttrains;

import android.app.Application;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import SmartTrainTools.ConnectivityGraph;
import SmartTrainTools.RailwayCodes;
import SmartTrainTools.Train;

/**
 * Created by ashish on 16/3/17.
 */

public class Globals extends Application {
    private static Context mContext;
    public static String etV="2.10.1";
    public static String tQ="GN";
    public static String rq_desc="Hi!";
    public static String changelog="";
    public static String update_Link="";
    public static String tip_of_the_day="Be careful getting on and off the train - there may be a gap between the train and platform or steps.";
    public static String qCacheUrl="http://smartize.esy.es/SmartTrains/ServResp.php";
    public static RailwayCodes rc=new RailwayCodes();
    public static boolean showWelcomeBox=true;
    public static String verDesc="";
    public static HashMap<String, Train> CachedTrainInfo=new HashMap<>();
    public static HashMap<String, String> cachedAvailabilityStatus=new HashMap<>();
    public static HashMap<String,String> cachedQuotaStation=new HashMap<>();
    public static ConnectivityGraph indiaMap;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;


        InputStream inputStream = getResources().openRawResource(R.raw.obj2);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(inputStream);
            indiaMap = (ConnectivityGraph) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Context getContext() {
        return mContext;
    }
}
