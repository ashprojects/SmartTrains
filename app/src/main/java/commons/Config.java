package commons;

import android.app.Application;
import android.content.Context;

import java.io.InputStream;
import java.util.HashMap;

import SmartTrainTools.ConnectivityGraph;
import SmartTrainTools.RailwayCodes;
import SmartTrainTools.Train;
import jpro.smarttrains.R;

/**
 * Created by ashish on 16/3/17.
 */

public class Config extends Application {
    private static Context mContext;
    public static String etV="2.10.1";
    public static String tQ="GN";
    public static String rq_desc="Hi!";
    public static String changelog="";
    public static String update_Link="";
    public static String tip_of_the_day="Be careful getting on and off the train - there may be a gap between the train and platform or steps.";
    public static String qCacheUrl="http://smartize.esy.es/SmartTrains/ServResp.php";
    public static String reportUrl = "http://smartize.esy.es/SmartTrains/report.php";
    public static RailwayCodes rc=new RailwayCodes();
    public static boolean showWelcomeBox=true;
    public static String verDesc="";
    public static HashMap<String, Train> CachedTrainInfo=new HashMap<>();
    public static HashMap<String, String> cachedAvailabilityStatus=new HashMap<>();
    public static HashMap<String,String> cachedQuotaStation=new HashMap<>();
    public static ConnectivityGraph indiaMap;
    public static String reqUserAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.59 Safari/537.36";
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;


        InputStream inputStream = getResources().openRawResource(R.raw.obj);
        indiaMap = ConnectivityGraph.readGraph(inputStream);
        RailwayCodes.initStationCodes(getResources().openRawResource(R.raw.stations));
    }

    public static Context getContext() {
        return mContext;
    }
}
