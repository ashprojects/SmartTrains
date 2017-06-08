/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartTrainTools;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jpro.smarttrains.Globals;


/**
 *
 * @author ashish
 */

public class SmartTools {
    // RETURNS AN ARRAYLIST OF FORMAT   <train-number>-<S M T W T F S >


    public static void fixEtvVer(){
        try {
            String link="http://etrain.info/in";
            Document resp=Jsoup.connect(link).get();
            // System.out.println(resp.toString().indexOf("js_v = \'"));
            String x=resp.toString().substring(resp.toString().indexOf("js_v = \'")+"js_v = \'".length(),resp.toString().length()-1);
            String v=x.substring(0,x.indexOf("\'"));
            Globals.etV=v;
        } catch (IOException E){

        }
    }



    /*
                    findTrains()
        Parameters:
            src: Source Station Code
            dest: Destination Station Code

        Used to find Trains between provided stations.

        All OK: an ArrayList<Train> is returned.
        No Trains Found: Returns empty instance of ArrayList<Train> of size 0 (not null).
        Connection Failed: Throws IOException.
        Etrains Down: returns null.


     */

    public static ArrayList<Train> findTrains(String src, String dest, String date) throws IOException{
        String link="http://etrain.info/ajax.php?q=trains&v="+ Globals.etV;
        HashMap<String, String> params=new HashMap<>();
        params.put("stn1",src);
        params.put("stn2",dest);
        if(date!=null)
            params.put("date",date);
        params.put("quota","GN");
        ArrayList<Train> trainss=new ArrayList<>();
        Document resp=Jsoup.connect(link).data(params).ignoreContentType(true).timeout(10000).post();
        JSONParser parser=new JSONParser();
        ArrayList<String> trains=new ArrayList<>();

        try {
            JSONObject obj=(JSONObject)parser.parse(resp.text());
            String data=(String) obj.get("data");
            String[] tr=data.toString().split(";");
            for(String t:tr){
                String[] exploded=t.split(",");
                String rn="-";
                int rq[]=new int[7];
                int cl[]=new int[9];
                //System.out.println("FROM_T_F"+exploded[2]+":");
                for(int j=8;j<15;++j){
                    if(exploded[j].charAt(0)=='1')
                        rq[j-8]=1;
                    else
                        rq[j-8]=0;
                    //System.out.print(" "+rq[j-8]);
                    //rn+=exploded[j]+" ";
                }

                for(int i=15;i<=23;++i){
                    cl[i-15]=(exploded[i].charAt(0)=='0')?0:1;
                }
                Train trn=new Train(exploded[1],false,false);

                trn.setQueryTravelTime(exploded[7].toString());
                trn.setQuerySrcStn(new Station(exploded[3]));
                trn.setQueryDestStn(new Station(exploded[5]));
                trn.setRunsOn(rq);
                trn.setClassAvail(cl);
                trn.setQuerySrcTime(exploded[4].toString());
                trn.setQueryDestTime(exploded[6].toString());
                trn.setName(exploded[2]);
                trainss.add(trn);
            }
            return trainss;
        } catch (ArrayIndexOutOfBoundsException E){
            return new ArrayList<Train>();
        } catch (ParseException ex) {

        }
        return null;

    }


    public static Path split_route(Station A, Station B, InputStream In){
        FileInputStream fin;
        try {
            //fin=new FileInputStream(In);
            ObjectInputStream oin=new ObjectInputStream(In);
            ConnectivityGraph udg=(ConnectivityGraph)oin.readObject();
            double len=0;
            //System.out.println(udg.graph);
            DijkstraShortestPath<String,DefaultEdge> dij;
            //=new DijkstraShortestPath<String,DefaultEdge>(udg.getGraph(), "SDL", "APR");
            String src=A.getCode(),des=B.getCode();

            DijkstraShortestPath dj=new DijkstraShortestPath(udg.getGraph(), src, des);
            List lst=dj.getPathEdgeList();
            len=dj.getPathLength();
            ArrayList<Station> stations=new ArrayList<Station>();
            stations.add(new Station(src));
            String last=src;
            for(int i=0;i<lst.size();++i){
                String stn=((DefaultWeightedEdge)lst.get(i)).toString();
                stn=stn.substring(1, stn.indexOf(")"));
                Station a=new Station(stn.split(":")[0].trim());
                Station b=new Station(stn.split(":")[1].trim());
                if(!stations.contains(a)){
                    stations.add(a);
                }
                if(!stations.contains(b)){

                    stations.add(b);
                }
            }
            return new Path(stations,len);
        } catch (FileNotFoundException ex) {
            //System.out.println("FNF EXCEPTION");
        } catch (IOException ex) {
            //ex.printStackTrace();
            //System.out.println("IO EXCEPTION");
        } catch (ClassNotFoundException ex) {
            //System.out.println("CNF Ex");
        }
        return null;
    }

    public static String generateAvailabilityHash(String trno, MyDate date, Station from, Station to, String tcode, String tclass){
        String code="AV";
        code+=trno+"_"+date.getD()+"-"+date.getM()+"-"+date.getY()+"_"+from.getCode()+"-"+to.getCode()+"_"+tclass+"_"+tcode;
        //System.out.println("Code:"+code);
        return code;
    }

   // public static ArrayList<Stations>


}
