package comparators.s.train;

import java.util.Comparator;

import SmartTrainTools.Train;

/**
 * Created by root on 26/6/17.
 */

public class TravelTimeComparator implements Comparator<Train> {
    @Override
    public int compare(Train o1, Train o2) {
        String t1 = o1.getQueryTravelTime();
        String t2 = o2.getQueryTravelTime();
        System.out.println("-- Comparing " + o1.getName() + t1 + " " + o2.getName() + t2 + " is " + (Integer.valueOf(t1.split(":")[0]) > Integer.valueOf(t2.split(":")[0])));
        if (Integer.valueOf(t1.split(":")[0]) > Integer.valueOf(t2.split(":")[0])) {
            return 1;
        }
        if (Integer.valueOf(t1.split(":")[0]) == Integer.valueOf(t2.split(":")[0])) {
            if (Integer.valueOf(t1.split(":")[1]) > Integer.valueOf(t2.split(":")[1])) {
                return 1;
            }
            if (Integer.valueOf(t1.split(":")[1]) == Integer.valueOf(t2.split(":")[1])) {
                return 0;
            }
            return -1;
        }
        return -1;
    }
}
