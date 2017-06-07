package jpro.smarttrains;

import java.util.ArrayList;
import java.util.HashMap;

import SmartTrainTools.RailwayCodes;
import SmartTrainTools.RouteListItem;
import SmartTrainTools.Train;

/**
 * Created by ashish on 16/3/17.
 */

public class Globals {
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

}
