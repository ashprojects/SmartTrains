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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import Exceptions.AvailabilityFailure;
import jpro.smarttrains.Globals;

/**
 *
 * @author ashish
 */
public class Journey implements Comparable<Journey>{
    Train train;

    Station src,dest,initStn;
    MyDate date,initDate,destDate,finalAvDate;
    String Tclass,quota;
    String status;
    AvailabilityStatus astatus, ainitStatus;
    AvailabilityStatus allGNStatus[]=null;
    AvailabilityStatus allCKStatus[]=null;
    public String initStatus;
    public String partialStatus;
    int fare;
    String desc;
    boolean completed=false;
    boolean dateChanged;
    boolean runningToday=true;

    public boolean isDateChanged() {
        return this.dateChanged;
    }


    public MyDate getInitDate() {
        return initDate;
    }

    public void checkDateValidity(){
        try {
            int day = SmartUtils.getDayNumber(this.date)-1;
            if(this.getTrain().getRunsOn()[day]!=1){
                // System.out.println("XXXXXXXXX Running set false");
                this.runningToday=false;
            }
        } catch(java.text.ParseException E){

            this.runningToday=true;
        }
    }



    public boolean validOnThisDate(){

        return this.runningToday;
    }


    public MyDate getDate() {
        return this.date;
    }

    public Train getTrain() {

        return train;
    }



    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Journey(Train train, Station src, Station dest, MyDate date, String Tclass, String quota) {
        this.train = train;
        this.src = src;
        this.initStn=src;
        this.dest = dest;
        this.date = date;
        this.initDate=new MyDate(date.getD(),date.getM(),date.getY());
        this.Tclass = Tclass;
        this.quota = quota;
        this.status="";
        this.dateChanged=false;
        this.desc="6-Working...";
        this.checkDateValidity();
        this.destDate=new MyDate(date.getD(),date.getM(),date.getY());

    }

    public String getStatus() {
        return status;
    }

    public String getInitStatus() {
        return initStatus;
    }

    public boolean isCompleted() {
        return completed;
    }


    // FUNCTION to fetch cached Source Quota station
    // Assumption: Only one quota station exists
    private Station fetchCachedQuotaStation(){
        String url= Globals.qCacheUrl;
        HashMap<String,String> params=new HashMap<>();
        params.put("reqType","get");
        params.put("t",""+this.getTrain().getNo());
        params.put("i","smartize.admin");
        Document resp;
        try {
            resp=Jsoup.connect(url).data(params).post();
            JSONParser jsp=new JSONParser();
            JSONObject obj=(JSONObject)jsp.parse(resp.text());
            System.out.println("________________FROM FETCH CACHE FUNCTION: "+resp.text());
            if(obj.get("found").toString().equals("Y")){
                return new Station(obj.get("stnCode").toString());
            } else {
                return null;
            }
        } catch (IOException E){
            return null;
        } catch (Exception E){
            return null;
        }

    }

    private boolean storeQoutaStationToServer(){
        String url=Globals.qCacheUrl;
        HashMap<String,String> params=new HashMap<>();
        params.put("reqType","put");
        params.put("t",""+this.getTrain().getNo());
        params.put("i","smartize.admin");
        params.put("stn",this.src.getCode());
        Document resp;
        try {
            resp=Jsoup.connect(url).data(params).post();
            JSONParser jsp=new JSONParser();
            System.out.println("res: "+resp.text());
            JSONObject obj=(JSONObject)jsp.parse(resp.text());
            System.out.println(obj.toJSONString());
            if(obj.get("status").toString().equals("Y")||obj.get("status").toString().equals("E")){
                System.out.println(obj.get("status").toString());
                return true;
            } else {
                System.out.println("Server Internal Failure");
                return false;
            }
        } catch (IOException E){
            System.out.println("Server Connection Failure");
            return false;
        } catch (Exception E){

            System.out.println("Server Connection Failure: ");
            E.printStackTrace();
            return false;
        }

    }


    public MyDate getDestDate() {
        return destDate;
    }

    public void fun() throws SocketTimeoutException,IOException{
        /*if(!this.train.getClasses().contains(new TravelClass(this.Tclass)));{
            this.desc="CLASS NOT AVAILABLE";
        }*/

        // Increment Destination Date (Arrival Day for Destination- Departure Day for Source)
        this.destDate.increment(this.train.getDayNumberOf(this.dest) - this.train.getDayNumberOf(this.src));
        String co=SmartTools.generateAvailabilityHash(this.train.getNo(),date,this.src,this.dest,"GN",this.Tclass);
        if(Globals.cachedAvailabilityStatus.containsKey(co)){
            System.out.println("Status found: "+co);
            this.status=Globals.cachedAvailabilityStatus.get(co);
        } else {
            fetchAvailability();
        }

        this.initStatus=this.status;
        ArrayList<RouteListItem> route_=this.train.getRoute();
        fetchFare();
        if(status.startsWith("AVA")){
            this.desc="0-Available Directly from Source!";
            return;
        } else if(status.startsWith("TRAIN")){
            this.desc = "4-Train Departed";
        } else if(status.startsWith("CURR")){
            this.desc="3-CURR_AVAILABLE. Check on IRCTC";
        } else {
            Station X=null;
            if(Globals.cachedQuotaStation.containsKey(this.train.getNo())){
                X=new Station(Globals.cachedQuotaStation.get(this.train.getNo()));
            } else {
                X = fetchCachedQuotaStation();
            }
            System.out.println(" ============= fetch from srv complete: "+X);
            if(X!=null){
                src=X;
                Globals.cachedQuotaStation.put(this.train.getNo(),X.getCode());
                System.out.println("------***** I FOUND QUOTA FROM SERVER: "+src.getName()+" for train: "+this.train.getName());
                int a=-1,b=-1;
                for(int i=0;i<route_.size();++i){
                    RouteListItem Y=route_.get(i);
                    if(Y.getStation().getCode().equals(X.getCode()))
                        a=i;
                    if(Y.getStation().getCode().equals(initStn.getCode()))
                        b=i;
                }
                System.out.println("ROUE: "+route_);
                System.out.println("a:"+a+" b:"+b);
                System.out.println("b:"+route_.get(b).getDay()+" a:"+route_.get(a).getDay());
                if(route_.get(b).getDay()>route_.get(a).getDay()){
                    System.out.println("Date decremented , even from server");
                    this.date.decrement();
                    this.dateChanged=true;
                }
                co = SmartTools.generateAvailabilityHash(this.train.getNo(), date, src, this.dest, "GN", this.Tclass);
                if(Globals.cachedAvailabilityStatus.containsKey(co)){
                    System.out.println("Status found: "+co);
                    this.status=Globals.cachedAvailabilityStatus.get(co);
                } else {
                    fetchAvailability();
                }

                if(status.startsWith("AVA")) {
                    this.desc = "1-Available from Different Source!";
                    this.fetchFare();
                    return;
                } else {
                    this.desc="2-NOT AVAILABLE FROM ANY SOURCE";
                    this.src=initStn;
                    this.fare=Integer.parseInt(getFare());
                    this.status=this.getInitStatus();
                    return;
                }
            }
            int mainSrcLoc=0;
            System.out.println(this.train.getName());

            Station[] route=new Station[route_.size()];
            for(int i=0;i<route_.size();++i){
                route[i]=route_.get(i).getStation();
                if(route[i].getCode().equals(this.src.getCode()))
                    mainSrcLoc=i;
            }
            if(route[0].getCode().equals(this.src.getCode())){
                System.out.println("FROM SOURCE, UNAVAILABLE");
                this.desc="7-FROM SOURCE, UNAVAILABLE";
                completed=true;
            } else {
                System.out.println(route[0].getCode()+route[mainSrcLoc]);
                int end=mainSrcLoc;
                int lastAv=-1;
                int st=0,mid=0;

                while (true) {
                    if (end > mid && mid > st) {
                    } else
                    if(end-mid==1 || (mid==0&&end==0&&st==0))
                        break;
                    mid = (st + end) / 2;
                    if(route_.get(mid).getDay()<route_.get(end).getDay()){
                        this.date.decrement();
                        this.dateChanged=true;
                    }
                    this.src=route[mid];
                    co = SmartTools.generateAvailabilityHash(this.train.getNo(), date, this.src, this.dest, "GN", this.Tclass);
                    if(Globals.cachedAvailabilityStatus.containsKey(co)){
                        System.out.println("Status found: "+co);
                        this.status=Globals.cachedAvailabilityStatus.get(co);
                    } else {
                        fetchAvailability();
                    }
                    if(status.contains("RAC"))

                        this.partialStatus=status;
                    if (status.startsWith("AVAI")) {
                        lastAv=mid;
                        System.out.println("LAST AV UPDATED:"+lastAv+":"+route[lastAv]);
                        st=mid;
                        mid=(st+end)/2;
                    } else {
                        end=mid;
                        mid=(st+end)/2;
                    }
                    System.out.println("Checked: "+route[mid]+" with status: "+status+" start: "+st+" end"+end+" mid"+mid);
                }

                completed=true;
                if(lastAv>0){
                    this.desc="1-Available from Different Source";
                    this.src=route[lastAv];
                    co = SmartTools.generateAvailabilityHash(this.train.getNo(), date, this.src, this.dest, "GN", this.Tclass);
                    if(Globals.cachedAvailabilityStatus.containsKey(co)){
                        System.out.println("Status found: "+co);
                        this.status=Globals.cachedAvailabilityStatus.get(co);
                    } else {
                        fetchAvailability();
                    }
                    this.fetchFare();
                    this.fare=Integer.parseInt(getFare());
                    this.finalAvDate=new MyDate(this.date.getD(),this.date.getM(),this.date.getY());
                    System.out.println("Fare: "+this.fare);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean s=storeQoutaStationToServer();
                            System.out.println("----- ****** Stored station "+src.getName()+" to server: "+s);
                        }
                    }).run();

                } else {
                    this.desc="2-NOT AVAILABLE FROM ANY SOURCE";
                    this.src=initStn;
                    this.fare=Integer.parseInt(getFare());
                    this.status=this.getInitStatus();

                }
            }


        }
    }

    public Station getDest() {
        return dest;
    }

    public Station getSrc() {

        return src;
    }

    public String getTravelTimeAsString(Station fromS){
        ArrayList<RouteListItem> r=this.train.getRoute();
        Time s=null,d=null;
        int sday=0,dday=0,ddiff;
        for(RouteListItem A:r){
            if(A.getStation().getCode().equals(fromS.getCode())){
                s=A.getDepartureTime();
                sday=A.getDay();
            }
            if(A.getStation().getCode().equals(this.dest.getCode())){
                d=A.getArrivalTime();
                dday=A.getDay();
                break;
            }
        }
        int p=0,q=0;
        ddiff=dday-sday;
        if(ddiff>0)
            p=ddiff*24;
        p-=Integer.parseInt(s.getHh());
        p+=Integer.parseInt(d.getHh());
        if(Integer.parseInt(d.getMm())-Integer.parseInt(s.getMm())<0){
            p--;
            q=60+Integer.parseInt(d.getMm())-Integer.parseInt(s.getMm());
        } else {
            q=Integer.parseInt(d.getMm())-Integer.parseInt(s.getMm());
        }
        return p+" hrs "+q+" mins";
    }

    public String getFare(){
        return String.valueOf(this.fare);
    }

    public void fetchFare() {

        // 25/8/16
        String[] temp=date.toString().split("/");
        String date2=(temp[2].length()>2?temp[2]:"20"+temp[2])+"-"+((Integer.parseInt(temp[1])>10)?temp[1]:""+temp[1])+"-"+temp[0];
        String furl="http://railwayapi.com/getFareEnquiry.php";
        String sclass=this.Tclass;
        HashMap<String,String> params=new HashMap<>();
        //params="pnrQ="+sfrom+"&dest="+sto+"&train="+tr+"&jdate="+date+"&age=30&jquota=GN"
        params.put("pnrQ", this.src.getName()+" - "+this.src.getCode());
        params.put("dest", this.dest.getName()+" - "+this.dest.getCode());
        params.put("train", String.valueOf(this.train.getNo())+" "+this.train.getName());
        params.put("jdate", date2);
        params.put("age", "30");
        params.put("jquota", this.quota);
        System.out.println(params);
        Document resp=null;
        try {
            resp = Jsoup.connect(furl).data(params).post();
        } catch (Exception E){
            this.fare=0;
            E.printStackTrace();
            System.out.println("FARE UNAVAILABLE");
            return;
        }
        Elements el=resp.getElementsByTag("tr");
        System.out.println(el.size());
        int i=0;
        for(Element e: el){
            if(i==0){
                i++;
                continue;
            }
            Element temp1=e.getElementsByTag("td").get(5);
            TravelClass tc=new TravelClass(sclass);
            if(temp1.text().trim().equals(tc.getClassFull())){
                this.fare=Integer.parseInt(e.getElementsByTag("td").get(4).text());
            }
        }
        System.out.println("FARE FOR "+this.train.getName()+" = "+this.getFare());
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AvailabilityStatus[] getAllCKStatus() {
        return allCKStatus;
    }

    public AvailabilityStatus[] getAllGNStatus() {
        return allGNStatus;
    }

    public void fetchAvailability() throws IOException,AvailabilityFailure{
        if(!validOnThisDate())
            return;
        /*String cd=SmartTools.generateAvailabilityHash(this.train.getNo(),this.date,this.src,this.dest,"GN",this.Tclass);
        if(Globals.cachedAvailabilityStatus.containsKey(cd)){
            this.status=Globals.cachedAvailabilityStatus.get(cd);
            return;
        }*/
        String date=this.date.getDateUnParsed();
        String d[]=date.split("/");
        System.out.println("date:"+date);
        date=d[1]+"/"+d[0]+"/"+d[2];
        String purl="http://railways.makemytrip.com/railways//json/avail";
        HashMap<String,String> params=new HashMap<>();
        params.put("srcCode", this.src.getCode());
        params.put("destCode", this.dest.getCode());

        params.put("trainNo", String.valueOf(train.getNo()).length()<5?"0"+String.valueOf(train.getNo()):String.valueOf(train.getNo()));
        params.put("dateOfJourney", date);
        params.put("coachCode", this.Tclass);
        System.out.println("sending:" + params);
        Document resp=Jsoup.connect(purl).data(params).ignoreContentType(true).timeout(10000).post();
        JSONParser parser=new JSONParser();
        try {
            JSONObject ar=(JSONObject)parser.parse(resp.text());
            System.out.println("RESP: "+resp.text());
            JSONObject status1=(JSONObject)ar.get(date);
            JSONObject s1=(JSONObject)status1.get(this.Tclass);
            if(s1==null)
                throw new IOException("Couldn\'t fetch status for: "+ train.getName());

            allGNStatus=new AvailabilityStatus[6];
            allCKStatus=new AvailabilityStatus[6];
            if(s1.get("seatStatus").toString().startsWith("AVAILABLE")){
                this.status= "AVAILABLE "+s1.get("seatCount");
            } else
                this.status = s1.get("seatStatus").toString();

            MyDate temp=new MyDate(this.date);
            try {
                int ckc=0;
                for (int i = 0; i < 6; ++i) {
                    String[] t = temp.getDateUnParsed().split("/");
                    String stemp = t[1] + "/" + t[0] + "/" + t[2];
                    System.out.println("FOR:" + stemp);
                    JSONObject status2 = (JSONObject) ar.get(stemp);
                    JSONObject s2 = (JSONObject) status2.get(this.Tclass);
                    AvailabilityStatus ew;
                    AvailabilityStatus ckew;
                    String c=SmartTools.generateAvailabilityHash(this.train.getNo(),temp,this.src,this.dest,"GN",this.Tclass);

                    if (s2.get("seatStatus").toString().startsWith("AVAILABLE")) {
                        //String c=SmartTools.generateAvailabilityHash(this.train.getNo(),temp,this.src,this.dest,this.quota,this.Tclass);
                        ew = new AvailabilityStatus("AVAILABLE " + s2.get("seatCount"), new MyDate(temp));
                        Globals.cachedAvailabilityStatus.put(c,"AVAILABLE "+s2.get("seatCount"));
                    } else {
                        Globals.cachedAvailabilityStatus.put(c,s2.get("seatStatus").toString());
                        ew = new AvailabilityStatus("" + s2.get("seatStatus"), new MyDate(temp));
                    }
                    ew.setTrainNo(this.train.getNoAsString());
                    if(Boolean.parseBoolean(s2.get("tatkalBook").toString())){
                        if (s2.get("tatkalSeatStatus").toString().startsWith("AVAILABLE")) {
                            ckew = new AvailabilityStatus("AVAILABLE " + s2.get("tatkalSeatCount"), new MyDate(temp));
                        } else {
                            ckew = new AvailabilityStatus("" + s2.get("tatkalSeatStatus"), new MyDate(temp));
                        }
                        ckew.setTrainNo(this.train.getNoAsString());
                        allCKStatus[ckc++]=ckew;
                    }

                    allGNStatus[i] = ew;
                    temp.increment(1);
                }
            } catch (NullPointerException E){

            }
            System.out.println("AV_ARRAY_GN:"+Arrays.asList(allGNStatus));
            System.out.println("AV_ARRAY_CK:"+Arrays.asList(allCKStatus));


        } catch(ParseException PEx){
            throw new AvailabilityFailure("Parse Failure at Availability Check");
        }
    }

  
    public Station getInitStn() {
        return initStn;
    }

    @Override
    public int compareTo(Journey J) {
        if(this.status.startsWith("AVAI")&&J.getStatus().startsWith("AVAI")){
            return Integer.parseInt(this.status.split(" ")[1])>Integer.parseInt(J.getStatus().split(" ")[1])?-1:0;
        }
        if(this.status.startsWith("AVAI")||this.getStatus().startsWith("CURR")||J.getStatus().startsWith("N.A.")||this.status.startsWith("GN"))
            return -1;
        if(J.getStatus().startsWith("AVAI")||this.status.startsWith("N.A.")||J.getStatus().startsWith("CURR"))
            return 1;
        if(this.status.startsWith("RAC"))
            return -1;
        if(J.getStatus().startsWith("RAC"))
            return 1;

        if(this.getStatus().startsWith("RL")&&J.getStatus().startsWith("GN"))
            return 1;
        if(J.getStatus().startsWith("RL")&&this.getStatus().startsWith("GN"))
            return -1;
        if((J.getStatus().startsWith("GN")&&this.getStatus().startsWith("GN"))||(J.getStatus().startsWith("RL")&&this.getStatus().startsWith("RL"))) {
            return 0;
        }
        return 0;
    }
    private int dateIndex=0;
}
