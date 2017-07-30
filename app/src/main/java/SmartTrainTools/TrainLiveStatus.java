package SmartTrainTools;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import commons.Config;

/**
 * Created by root on 9/7/17.
 */

public class TrainLiveStatus implements Serializable {
    Train train;
    Calendar minDate;
    String msg;
    MyDate selectedDate = null;

    enum Status {
        DELAYED, ONTIME, BEFORE
    }

    public TrainLiveStatus(Train T) throws IOException {
        this.train = T;
        T.fetchInfo_ETrain();
        RouteListItem lastListItem = T.getRoute().get(T.getRoute().size() - 1);
        MyDate d = MyDate.getMyDateInstance(Calendar.getInstance());
        int dt = lastListItem.getDay();
        System.out.println("_DT FACTOR: " + dt);
        boolean flag = false;
        while (dt > 0) {
            if (this.train.runsOnDate(d))
                flag = true;

            d.decrement();
            dt--;
        }
        if (flag)
            minDate = MyDate.getCalendarInstance(d);
        else
            minDate = null;

    }

    public static String textBetweenBrackets(String x) {

        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(x);
        m.find();
        return m.group(1);
    }

    public ArrayList<LiveStatusItem> getLiveStatusItems() {
        return this.liveStatusItems;
    }

    public String getMsg() {
        return msg;
    }

    public void refreshNow() throws IOException {
        if (selectedDate == null)
            return;
        // GC will take all
        this.liveStatusItems = null;
        this.fetchNow(this.selectedDate);
    }

    public boolean fetchNow(MyDate date) throws IOException {
        this.selectedDate = date;
        String link = "https://etrain.info/ajax.php?q=runningstatus&v=" + Config.etV;
        HashMap<String, String> params = new HashMap<>();
        params.put("train", train.getNoAsString());
        params.put("atstn", train.getSource().getCode());
        params.put("date", date.getDate());
        params.put("final", "1");
        ArrayList<Train> trainss = new ArrayList<>();
        System.out.println("LIVE SENDING: " + link + " " + params);
        Document respDoc = Jsoup.connect(link).userAgent(Config.reqUserAgent).data(params).header("X-Requested-With", "XMLHttpRequest").header("Referer", "http://etrain.info/in").ignoreContentType(true).timeout(10000).post();
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(respDoc.text());
            System.out.println("LIVE RESPONSE: " + respDoc.text());
            String resp = (String) obj.get("data");

            System.out.println("LIVE RESP: " + resp);
            String x = resp.split("\\|\\|~~\\|\\|")[1];
            x = Jsoup.parse(x).text();
            String[] y = x.split("\\^");
            int i = 0;
            msg = y[0].split("~")[6].replaceAll("<\\\\/span>", "");

            liveStatusItems = new ArrayList<>();
            for (String t : y) {
                i++;

                t = t.replace("<\\/span>", "");
                String sarr, sdept, aarr, adept;
                String datesarr, datesdept, dateaarr, dateadept, stnname;
                String[] a = t.split("~");
                //System.out.println(t);
                try {
                    stnname = a[0];
                    LiveStatusItem item = new LiveStatusItem();
                    item.setStn(new Station(Config.rc.getStationCode(stnname), stnname));
                    item.setHasArrived(!a[4].contains("ETD"));
                    System.out.println("_FN: " + a[1]);
                    if (a[1].contains("Source")) {

                        item.setSource(true);
                        adept = a[4].split(",")[0];
                        sdept = a[2].split(",")[0];
                        datesdept = a[2].split(",")[1];
                        dateadept = a[4].split(",")[1];

                        item.setActDeptDate(MyDate.parseMyDate(dateadept, "dd MMM yyyy"));
                        item.setSchDeptDate(MyDate.parseMyDate(datesdept, "dd MMM yyyy"));
                        item.setSchDept(new Time(sdept.split(":")[0], sdept.split(":")[1]));
                        item.setActDept(new Time(adept.split(":")[0], adept.split(":")[1]));
                        try {
                            item.setDeptDelay(Integer.parseInt(TrainLiveStatus.textBetweenBrackets(a[3])));
                        } catch (Exception Nex) {

                            item.setDeptDelay(SmartTools.timeDifferenceInMinutes(item.getSchDept(), item.getActDept()));

                        }


                    } else if (a[2].contains("Destina")) {
                        item.setHasArrived(!a[3].contains("ETA"));
                        item.setDestination(true);
                        sarr = a[1].split(",")[0];
                        aarr = a[3].split(",")[0];
                        datesarr = a[1].split(",")[1];
                        dateaarr = a[3].split(",")[1];
                        item.setSchArr(new Time(sarr.split(":")[0], sarr.split(":")[1]));
                        item.setActArr(new Time(aarr.split(":")[0], aarr.split(":")[1]));
                        item.setActArrDate(MyDate.parseMyDate(dateaarr, "dd MMM yyyy"));
                        item.setSchArrDate(MyDate.parseMyDate(datesarr, "dd MMM yyyy"));
                        try {
                            item.setArrDelay(Integer.parseInt(TrainLiveStatus.textBetweenBrackets(a[3])));
                        } catch (NumberFormatException Nex) {
                            item.setArrDelay(-SmartTools.timeDifferenceInMinutes(item.getSchArr(), item.getActArr()));
                        }

                    } else {

                        sarr = a[1].split(",")[0];
                        sdept = a[2].split(",")[0];
                        aarr = a[3].split(",")[0];
                        adept = a[4].split(",")[0];

                        datesarr = a[1].split(",")[1];
                        datesdept = a[2].split(",")[1];
                        dateaarr = a[3].split(",")[1];
                        dateadept = a[4].split(",")[1];
                        String res;
                        //item.setStn(new Station(Config.rc.getStationCode(stnname),stnname));
                        item.setActArr(new Time(aarr.split(":")[0], aarr.split(":")[1]));
                        item.setSchArr(new Time(sarr.split(":")[0], sarr.split(":")[1]));
                        item.setActDept(new Time(adept.split(":")[0], adept.split(":")[1]));
                        item.setSchDept(new Time(sdept.split(":")[0], sdept.split(":")[1]));

                        item.setActArrDate(MyDate.parseMyDate(dateaarr, "dd MMM yyyy"));
                        item.setSchArrDate(MyDate.parseMyDate(datesarr, "dd MMM yyyy"));
                        item.setSchDeptDate(MyDate.parseMyDate(datesdept, "dd MMM yyyy"));
                        item.setActDeptDate(MyDate.parseMyDate(dateadept, "dd MMM yyyy"));
                        try {
                            res = TrainLiveStatus.textBetweenBrackets(a[3]).replace("m", "");
                            if (res.contains("+")) {
                                item.setArrDelay(Integer.parseInt(res.replaceAll("\\+", "")));
                            } else
                                item.setArrDelay(Integer.parseInt(res));
                        } catch (NumberFormatException Nex) {
                            //Nex.printStackTrace();

                            item.setArrDelay(-SmartTools.timeDifferenceInMinutes(item.getSchArr(), item.getActArr()));
                        }

                        try {
                            res = TrainLiveStatus.textBetweenBrackets(a[4]).replace("m", "");
                            if (res.contains("+")) {
                                item.setDeptDelay(Integer.parseInt(res.replaceAll("\\+", "")));
                            } else {
                                item.setDeptDelay(Integer.parseInt(res));
                            }
                        } catch (NumberFormatException Nex) {
                            item.setDeptDelay(-SmartTools.timeDifferenceInMinutes(item.getSchDept(), item.getActDept()));
                        }


                    }
                    //System.out.println("## FOR "+item.getStn().getName()+": "+item.getDeptDelay()+"___ "+item.getArrDelay());

                    //System.out.println("## FOR "+item.getStn().getName()+": "+item.getDeptDelay()+"___ "+item.getArrDelay()+" "+hasArr);
                    liveStatusItems.add(item);
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        } catch (Exception E) {
            return false;
        }
        return false;
    }

    public Train getTrain() {
        return train;
    }

    public Calendar getMinDate() {
        return minDate;
    }


    /**
     * Compute the Station which is the last possible arrived Station
     * Uses O(n2) for computation
     * Because I don't trust Indian Railways :D
     *
     * @return A Station Instance
     */

    public Station findLastArrivedStation() throws NullPointerException {
        /*int i=0;
        boolean flag=false;
        Station x=null;
        for(i=0;i<liveStatusItems.size();++i){
            if(!liveStatusItems.get(i).isHasArrived()){
                flag=true;
                for(int j=i+1;j<liveStatusItems.size();++j){
                    System.out.println("!!! Checking forL "+liveStatusItems.get(i).getStn()+" = "+liveStatusItems.get(j).getStn());
                    if(liveStatusItems.get(j).isHasArrived()){
                        flag=false;
                        break;
                    }
                }
                if(flag){
                    x=new Station(liveStatusItems.get(i).getStn().getCode());
                    break;
                }
            }
        }
        return x;*/

        if (msg.contains("crossed")) {
            String stn = msg.substring(msg.indexOf("crossed") + "crossed".length(), msg.indexOf(" at")).trim();
            String stnName = stn.substring(0, stn.indexOf(" ("));
            Station x = new Station(textBetweenBrackets(stn), stnName);
            System.out.println("----- CREATED:  " + x);
            return x;
        } else if (msg.contains("arrived")) {
            String stn = msg.substring(msg.indexOf("arrived") + "arrived".length(), msg.indexOf(" at")).trim();
            String stnName = stn.substring(0, stn.indexOf(" ("));
            Station x = new Station(textBetweenBrackets(stn), stnName);
            System.out.println("----- CREATED:  " + x);
            return x;
        }

        return null;

    }

    public LiveStatusItem recentLiveStatusItem() {
        Station last = findLastArrivedStation();
        for (LiveStatusItem X : liveStatusItems) {
            if (X.getStn().equals(last))
                return X;
        }
        return null;
    }

    public class LiveStatusItem implements Serializable {
        Station stn;

        public boolean isHasArrived() {
            return hasArrived;
        }

        public void setHasArrived(boolean hasArrived) {
            this.hasArrived = hasArrived;
        }

        Time schArr, schDept, actArr, actDept;
        MyDate schArrDate, schDeptDate, actArrDate, actDeptDate;
        String msg;
        Status status;
        boolean isSource, isDestination, hasArrived;
        int arrDelay, deptDelay;

        @Override
        public String toString() {
            return "LiveStatusItem{" +
                    "stn=" + stn +
                    ", schArr=" + schArr +
                    ", schDept=" + schDept +
                    ", actArr=" + actArr +
                    ", actDept=" + actDept +
                    ", schArrDate=" + schArrDate +
                    ", schDeptDate=" + schDeptDate +
                    ", actArrDate=" + actArrDate +
                    ", actDeptDate=" + actDeptDate +
                    ", msg='" + msg + '\'' +
                    ", status=" + status +
                    ", isSource=" + isSource +
                    ", isDestination=" + isDestination +
                    ", arrDelay=" + arrDelay +
                    ", hasArrived= " + hasArrived +
                    ", deptDelay=" + deptDelay +
                    '}';
        }

        public int getArrDelay() {
            return arrDelay;
        }

        public void setArrDelay(int arrDelay) {
            if (arrDelay < 0) {
                this.status = Status.DELAYED;
            } else {
                this.status = Status.ONTIME;
            }
            this.arrDelay = arrDelay;
        }

        public int getDeptDelay() {
            return deptDelay;
        }

        public void setDeptDelay(int deptDelay) {
            if (deptDelay < 0) {
                this.status = Status.DELAYED;
            } else {
                this.status = Status.ONTIME;
            }
            this.deptDelay = deptDelay;
        }

        public MyDate getSchArrDate() {
            return schArrDate;
        }

        public MyDate getSchDeptDate() {
            return schDeptDate;
        }

        public MyDate getActArrDate() {
            return actArrDate;
        }

        public MyDate getActDeptDate() {
            return actDeptDate;
        }

        public boolean isSource() {
            return isSource;
        }

        public void setSource(boolean source) {
            if (source) {
                schArr = null;
                actArr = null;

                schArrDate = null;
                schDeptDate = null;
            }
            isSource = source;
        }

        public boolean isDestination() {
            return isDestination;
        }

        public void setDestination(boolean destination) {
            if (destination) {
                actDept = null;
                schDept = null;
                actDeptDate = null;
                schDeptDate = null;
            }
            isDestination = destination;
        }

        public void setSchArrDate(MyDate schArrDate) {
            this.schArrDate = schArrDate;
        }

        public void setSchDeptDate(MyDate schDeptDate) {
            this.schDeptDate = schDeptDate;
        }

        public void setActArrDate(MyDate actArrDate) {
            this.actArrDate = actArrDate;
        }

        public void setActDeptDate(MyDate actDeptDate) {
            this.actDeptDate = actDeptDate;
        }

        public Station getStn() {
            return stn;
        }

        public void setStn(Station stn) {
            this.stn = stn;
        }

        public Time getSchArr() {
            return schArr;
        }

        public void setSchArr(Time schArr) {
            this.schArr = schArr;
        }

        public Time getSchDept() {
            return schDept;
        }

        public void setSchDept(Time schDept) {
            this.schDept = schDept;
        }

        public Time getActArr() {
            return actArr;
        }

        public void setActArr(Time actArr) {
            this.actArr = actArr;
        }

        public Time getActDept() {
            return actDept;
        }

        public void setActDept(Time actDept) {
            this.actDept = actDept;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }


    }

    @Override
    public String toString() {
        return "TrainLiveStatus{" +
                ", minDate=" + minDate +
                ", liveStatusItems=" + liveStatusItems +
                '}';
    }

    ArrayList<LiveStatusItem> liveStatusItems;
}
