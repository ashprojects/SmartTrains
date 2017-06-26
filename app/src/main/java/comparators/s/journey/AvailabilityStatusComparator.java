package comparators.s.journey;

import java.util.Comparator;

import SmartTrainTools.Journey;

/**
 * Created by root on 26/6/17.
 */

public class AvailabilityStatusComparator implements Comparator<Journey> {
    @Override
    public int compare(Journey J1, Journey J2) {
        if (J1.getStatus().startsWith("AVAI") && J2.getStatus().startsWith("AVAI")) {
            return Integer.parseInt(J1.getStatus().split(" ")[1]) > Integer.parseInt(J2.getStatus().split(" ")[1]) ? -1 : 0;
        }
        if (J1.getStatus().startsWith("AVAI") || J1.getStatus().startsWith("CURR") || J2.getStatus().startsWith("N.A.") || J1.getStatus().startsWith("GN"))
            return -1;
        if (J2.getStatus().startsWith("AVAI") || J1.getStatus().startsWith("N.A.") || J2.getStatus().startsWith("CURR"))
            return 1;
        if (J1.getStatus().startsWith("RAC"))
            return -1;
        if (J2.getStatus().startsWith("RAC"))
            return 1;

        if (J1.getStatus().startsWith("RL") && J2.getStatus().startsWith("GN"))
            return 1;
        if (J2.getStatus().startsWith("RL") && J1.getStatus().startsWith("GN"))
            return -1;
        if ((J2.getStatus().startsWith("GN") && J1.getStatus().startsWith("GN")) || (J2.getStatus().startsWith("RL") && J1.getStatus().startsWith("RL"))) {
            return 0;
        }
        return 0;
    }
}

