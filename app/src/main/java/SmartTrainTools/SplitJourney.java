package SmartTrainTools;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by root on 23/3/17.
 */

public class SplitJourney implements Serializable{
    Station srcStn,midStn,destStn;
    MyDate date;
    TravelClass tclass;
    double pathLen;
    ArrayList<Train> trainSet1,trainSet2;

    public double getPathLen() {
        return pathLen;
    }

    public void setPathLen(double pathLen) {
        this.pathLen = pathLen;
    }



    public Station getSrcStn() {
        return srcStn;
    }

    public void setSrcStn(Station srcStn) {
        this.srcStn = srcStn;
    }

    public Station getMidStn() {
        return midStn;
    }

    public SplitJourney(Station srcStn, Station midStn, Station destStn, MyDate date, TravelClass tclass, ArrayList<Train> trainSet1, ArrayList<Train> trainSet2) {
        this.srcStn = srcStn;
        this.midStn = midStn;
        this.destStn = destStn;
        this.date = date;
        this.tclass = tclass;
        this.trainSet1 = trainSet1;
        this.trainSet2 = trainSet2;
    }

    public void setMidStn(Station midStn) {
        this.midStn = midStn;
    }

    public Station getDestStn() {
        return destStn;
    }

    public void setDestStn(Station destStn) {
        this.destStn = destStn;
    }

    public MyDate getDate() {
        return date;
    }

    public void setDate(MyDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SplitJourney{" +
                "trainSet1=" + trainSet1 +
                ", destStn=" + destStn +
                ", midStn=" + midStn +
                ", srcStn=" + srcStn +
                ", trainSet2=" + trainSet2 +
                '}';
    }

    public TravelClass getTclass() {
        return tclass;
    }

    public void setTclass(TravelClass tclass) {
        this.tclass = tclass;
    }

    public ArrayList<Train> getTrainSet1() {
        return this.trainSet1;
    }

    public void setTrainSet1(ArrayList<Train> trainSet1) {
        this.trainSet1 = trainSet1;
    }

    public ArrayList<Train> getTrainSet2() {
        return this.trainSet2;
    }

    public void setTrainSet2(ArrayList<Train> trainSet2) {
        this.trainSet2 = trainSet2;
    }
}
