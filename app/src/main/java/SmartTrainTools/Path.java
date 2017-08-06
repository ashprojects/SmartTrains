package SmartTrainTools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by root on 22/3/17.
 */

public class Path implements Serializable {
    ArrayList<Station> stations;
    double distance;

    public Path(ArrayList<Station> stations,double distance) {
        this.distance = distance;
        this.stations = stations;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void removeAllNonJunctions() {
        int i = 0;
        for (ListIterator<Station> iterator = stations.listIterator(); iterator.hasNext(); ) {
            if (i == 0) {
                i++;
                iterator.next();
                continue;
            }
            Station station = iterator.next();
            if (!station.isJunction()) {
                iterator.remove();
            }
            i++;
        }
    }
}
