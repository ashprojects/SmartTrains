package SmartTrainTools;

import java.io.Serializable;
import java.util.ArrayList;

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
}
