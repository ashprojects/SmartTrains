/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import commons.Config;

/**
 *
 * @author jan
 */
public class Train implements Serializable,Comparable<Train> {

    private final String no;
    private String name;
    private ArrayList<RouteListItem> route;
    private ArrayList<TravelClass> classes;
    private int[] classAvail;
    private int[] runsOn;
    private Station querySrcStn,queryDestStn;
    private Time querySrcTime, queryDestTime;

    public String getQueryTravelTime() {
        return queryTravelTime;
    }

    public void setQueryTravelTime(String queryTravelTime) {
        this.queryTravelTime = queryTravelTime.replace("H","");
    }

    private String queryTravelTime;



    public Station getQueryDestStn() {
        return queryDestStn;
    }

    public void setQueryDestStn(Station destStn) {
        this.queryDestStn = destStn;
    }

    public Station getQuerySrcStn() {
        return querySrcStn;
    }

    public void setQuerySrcStn(Station srcStn) {
        this.querySrcStn = srcStn;
    }

    public String getDepartureTimeof(Station S){
        if(route==null)
            return null;
        int i=0;
        for(;i<route.size();++i){
            RouteListItem X=route.get(i);
            if(X.getStation().equals(S)){
                return X.getDepartureTime().toString();
            }
        }
        return null;
    }

    public boolean runsOnDate(MyDate d){

        return (runsOn[d.getDayNumber()]==1);
    }

    public int[] getRunsOn() {
        return runsOn;
    }

    public String getArrivalTimeof(Station S){
        if(route==null)
            return null;
        int i=0;
        for(;i<route.size();++i){
            RouteListItem X=route.get(i);
            if(X.getStation().equals(S)){
                return X.getArrivalTime().toString();
            }
        }
        return null;
    }

    public int getDayNumberOf(Station S){
        if(route==null)
            return 0;
        int i=0;
        for(;i<route.size();++i){
            RouteListItem X=route.get(i);
            if(X.getStation().equals(S)){
                return X.getDay();
            }
        }
        return 0;
    }
    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    public Time getQueryDestTime() {
        return queryDestTime;
    }

    public void setQueryDestTime(String queryDestTime) {
        this.queryDestTime = new Time(queryDestTime.split(":")[0], queryDestTime.split(":")[1]);
    }

    public Time getQuerySrcTime() {
        return querySrcTime;
    }

    public void setQuerySrcTime(String querySrcTime) {
        this.querySrcTime = new Time(querySrcTime.split(":")[0], querySrcTime.split(":")[1]);
    }

    public void setRunsOn(int[] x){
        //System.out.println("&&&& SETTiNG RUNONS");
        this.runsOn=new int[7];
        for(int i=0;i<7;++i){
            this.runsOn[i]=x[i];
        }


    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        final Train other = (Train) obj;
        return other.getNo().equals(this.getNo()) && other.getName().equals(this.getName());
    }

    public ArrayList<TravelClass> getClasses() {
        return classes;
    }

    public Train(String no, String name, ArrayList<RouteListItem> route) {
        this.no = no;
        this.name = name;
        this.route = route;
    }

    public Train(String no, boolean fetchInfo, boolean fetchAsync) throws IOException {
        this.no = no;
        route = new ArrayList<>();
        this.runsOn=null;
        if (!fetchInfo) {
            return;
        }
        if (!fetchAsync) {
            fetchInfo_ETrain();
        } else {
            new Thread() {

                @Override
                public void run() {
                    try {
                        fetchInfo_ETrain();
                    } catch (Exception E){}
                }

            }.start();
        }
    }

    public ArrayList<RouteListItem> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<RouteListItem> route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "Train{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", route=" + route +
                ", classes=" + classes +
                '}';
    }



    public Station getSource() {
        if (route == null) {
            throw new IllegalStateException("No route found");
        }
        return route.get(0).getStation();
    }

    public Station getDestination() {
        if (route == null) {
            throw new IllegalStateException("No route found");
        }
        return route.get(route.size() - 1).getStation();
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the value of trainNo
     *
     * @return the value of trainNo
     */
    public String getNo() {
        return no;
    }


    public int[] getClassAvail() {
        return classAvail;
    }

    public String getRunningDaysAsString(){
        String[] d=new String[]{"SUNDAY:S","MONDAY:M","TUESDAY:T","WEDNESDAY:W","THURSDAY:T","FRIDAY:F","SATURDAY:S"};
        if(runsOn==null)
            return null;
        String s="";
        for(int i=0;i<runsOn.length;++i){
            if(runsOn[i]==1){
                s+=";"+d[i];
            }
        }
        return s;
    }

    @Override
    public int hashCode() {
        int result = no != null ? no.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (route != null ? route.hashCode() : 0);
        result = 31 * result + (classes != null ? classes.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(classAvail);
        result = 31 * result + Arrays.hashCode(runsOn);
        result = 31 * result + (querySrcStn != null ? querySrcStn.hashCode() : 0);
        result = 31 * result + (queryDestStn != null ? queryDestStn.hashCode() : 0);
        result = 31 * result + (querySrcTime != null ? querySrcTime.hashCode() : 0);
        result = 31 * result + (queryDestTime != null ? queryDestTime.hashCode() : 0);
        result = 31 * result + (queryTravelTime != null ? queryTravelTime.hashCode() : 0);
        return result;
    }

    public void setClassAvail(int[] classAvail) {
        this.classAvail = classAvail;
    }

    // FROM ETrain.info
    public void fetchInfo_ETrain() throws IOException {
        int i;
        if (Config.CachedTrainInfo.containsKey(this.no)) {

            Train X = Config.CachedTrainInfo.get(this.no);
            this.name=X.getName();
            this.setRoute(X.getRoute());
            this.setRunsOn(X.getRunsOn());
            this.setClassAvail(X.getClassAvail());
            this.classes=TravelClass.getAllClassesObjects();
            System.out.println("CACHE_HIT_LOADED: "+this.no+" - "+this.name);
        } else {


            //String train_number = "" + (no>10000?no:"0"+no);
            String train_number = no;
            String link = "http://etrain.info/ajax.php?q=schedule&v=" + Config.etV;
            HashMap<String, String> params = new HashMap<>();
            params.put("train", train_number);
            Document resp = Jsoup.connect(link).data(params).ignoreContentType(true).timeout(10000).post();
            //System.out.println(resp);
            JSONParser parser = new JSONParser();
            try {
                JSONObject ob = (JSONObject) parser.parse(resp.text());
                System.out.println(resp.text());
                String[] list = ((String) ob.get("data")).split(";");
                name = list[4].split(",")[1];
                String[] codeList = list[6].split(",");
                String source = codeList[0];
                String destination = codeList[codeList.length - 1];
                list = ((String) ob.get("data")).split("\\|\\|~~\\|\\|");
                //System.out.println(list[0]);
                //System.out.println(list[1]);
                list = list[1].split(";");
                String[] rs = list[0].split(",");
                if (this.runsOn == null) {
                    int st = 3;
                    this.runsOn = new int[7];
                    for (int k = 0; k < 7; ++k)
                        this.runsOn[k] = rs[st++].equals("0") ? 0 : 1;

                    st = 10;
                    this.classAvail = new int[9];
                    for (int k = 0; k < 8; ++k) {
                        this.classAvail[k] = rs[st++].equals("0") ? 0 : 1;
                    }
                    this.classAvail[8] = rs[9].equals("0") ? 0 : 1;
                    this.classes = TravelClass.getAllClassesObjects();

                }
                for (i = 1; i < list.length; i++) {
                    //sample format AKD,AKODIA,21:03 (Day 1),21:05 (Day 1),170,0
                    String temp[] = list[i].split(",");
                    String stCode = temp[0];
                    String stname = temp[1];
                    Time arrTime, depTime;
                    int day, distance;
                    if (temp[2].equals("Source")) {
                        depTime = Time.parseTime(temp[3].split(" ")[0]);
                        arrTime = depTime;
                        day = 1;
                        distance = 0;
                    } else if (temp[3].equals("Destination")) {
                        arrTime = Time.parseTime(temp[2].split(" ")[0]);
                        depTime = arrTime;
                        day = Integer.parseInt(temp[2].split(" ")[2].split("\\)")[0]);
                        distance = Integer.parseInt(temp[4]);
                    } else {
                        try {
                            String reSplit[] = temp[2].split(" ");
                            arrTime = Time.parseTime(reSplit[0]);
                            day = Integer.parseInt(reSplit[2].split("\\)")[0]);

                        } catch (IllegalArgumentException e) {
                            arrTime = null;
                            day = 1;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            arrTime = null;
                            day = 1;
                        }
                        try {
                            depTime = Time.parseTime(temp[3].split(" ")[0]);
                        } catch (IllegalArgumentException e) {
                            depTime = null;
                        }
                        distance = Integer.parseInt(temp[4]);
                    }
                    //System.out.println(StationFactory.getStationInstance(stCode, stname));
                    RouteListItem rtl = new RouteListItem(StationFactory.getStationInstance(stCode, stname), arrTime, depTime, distance, day);
                    route.add(rtl);

                }

                Config.CachedTrainInfo.put(this.no, this);

            } catch (ParseException ex) {
                ex.printStackTrace();
                // Logger.getLogger(TrainTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ArrayIndexOutOfBoundsException Aex) {
                System.out.println("ARR_BOUND: FOR " + this.no);
            }
        }
    }

    public String getNoAsString(){
        return no;
    }

    @Override
    public int compareTo(Train train) {
        return 0;
    }
/*
    @Override
    public int compareTo(Train train) {
        if(this.no < train.getNo())
            return -1;
        if(this.no > train.getNo())
            return 1;
        return 0;
    }

  */
}
