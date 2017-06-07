/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author jan
 */
public class StationFactory {

    private static final HashMap<String, Station> stationCache = new HashMap<>();

    public static Station getStationInstance(String code, String name) {
        //Station station=new Station(code, name);
        if (stationCache.containsKey(code)) {
            return stationCache.get(code);
        }      
        Station station = new Station(code, name);       
        stationCache.put(code, station);
        return station;

    }

    public static Station getStationInstance(String code) {
        //Station station=new Station(code, name);
        return stationCache.get(code);
    }

    public String[] getStationList() {
        String[] stationList = new String[stationCache.size()];
        int cursor = 0;
        for (Map.Entry<String, Station> entrySet : stationCache.entrySet()) {
            stationList[cursor++] = entrySet.getValue().toString();
        }
        return stationList;
    }

}
