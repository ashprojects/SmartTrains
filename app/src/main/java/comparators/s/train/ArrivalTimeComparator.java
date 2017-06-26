package comparators.s.train;

import java.util.Comparator;

import SmartTrainTools.Time;
import SmartTrainTools.Train;

/**
 * Created by root on 26/6/17.
 */

public class ArrivalTimeComparator implements Comparator<Train> {

    @Override
    public int compare(Train o1, Train o2) {
        Time t1 = o1.getQuerySrcTime();
        Time t2 = o2.getQuerySrcTime();
        if (Integer.valueOf(t1.getHh()) > Integer.valueOf(t2.getHh())) {
            return 1;
        }
        if (Integer.valueOf(t1.getHh()) == Integer.valueOf(t2.getHh())) {
            if (Integer.valueOf(t1.getMm()) > Integer.valueOf(t2.getMm())) {
                return 1;
            } else if (Integer.valueOf(t1.getMm()) < Integer.valueOf(t2.getMm())) {
                return -1;
            } else {
                return 0;
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
