package SmartTrainsDB;

/**
 * Created by root on 27/5/17.
 */

public class TrainBean {
    String trno,trname;
    String from, to;

    public TrainBean() {
    }

    public TrainBean(String from, String to, String trname, String trno) {
        this.from = from;
        this.to = to;
        this.trname = trname;
        this.trno = trno;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTrname() {
        return trname;
    }

    public void setTrname(String trname) {
        this.trname = trname;
    }

    public String getTrno() {
        return trno;
    }

    public void setTrno(String trno) {
        this.trno = trno;
    }
}
