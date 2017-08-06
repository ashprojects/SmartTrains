package jpro.smarttrains.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import SmartTrainTools.MyDate;
import SmartTrainTools.Path;
import SmartTrainTools.SmartTools;
import SmartTrainTools.SplitJourney;
import SmartTrainTools.Station;
import SmartTrainTools.Train;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.SplitStationsListAdapter;

public class SplitStations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_stations);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        src=(Station) getIntent().getSerializableExtra("src");
        dest=(Station) getIntent().getSerializableExtra("dest");
        date=(MyDate)getIntent().getSerializableExtra("date");
        list=(ListView)findViewById(R.id.sp_list);
        mainPath=(Path)getIntent().getSerializableExtra("path");
        progressBar=(ProgressBar)findViewById(R.id.split_progressBar);
        Snackbar.make(findViewById(R.id.activity_split_stations), "Working... Please wait", Snackbar.LENGTH_LONG).show();
        listAdapter=new SplitStationsListAdapter(this,splitJourneys);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in=new Intent(getApplicationContext(),SplitJourneyTabbed.class);
                //System.out.println(" SPLITT: "+splitJourneys.get(i));
                in.putExtra("j",splitJourneys.get(i));
                in.putExtra("direct",direct);
                startActivity(in);
            }
        });
        new findStationsAsync().execute();
    }

    private void retry(){
        new findStationsAsync().execute();
    }


    private class findStationsAsync extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            //System.out.println("src:"+src+" dest "+dest);
            splitJourneys=new ArrayList<>();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            //System.out.println("FOUND:: "+splitJourneys);
            listAdapter.update(splitJourneys);
            if(failed){
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_split_stations), "Connection Failed", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                retry();
                            }
                        });
                //snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
               // snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
            if (splitJourneys.size() == 0) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_split_stations), "TOO MANY DIRECT TRAINS. CAN'T FIND A BETTER MATCH! :(", Snackbar.LENGTH_INDEFINITE);
                //snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                // snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
            //super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... v) {
            int sizeStart=0;
            String sdate;
            if(date==null)
                sdate=null;
            else
                sdate=date.getDate();
            try{
                fromSrctrains= SmartTools.findTrains(src.getCode(),dest.getCode(),sdate);
                sizeStart=fromSrctrains.size();
                //System.out.println("SETTING SIZESTART AS: "+sizeStart);
            } catch (Exception Ex){
                direct=false;
                Ex.printStackTrace();
            }
            int count=0;

            ArrayList<Station> stations=mainPath.getStations();
            System.out.println("Stations: "+stations);
            for(int i=1;i<stations.size();++i){

                if(count==3)
                    break;
                try{
                    if(i==stations.size()-1)
                        break;
                    ArrayList<Train> trainsDest= SmartTools.findTrains(stations.get(i).getCode(),dest.getCode(),sdate);
                    //System.out.println("sizeStart: "+sizeStart+" FROM CURR: "+stations.get(i).getCode()+" to DEST, "+trainsDest+"\nSIZE:"+trainsDest.size());

                    int size=trainsDest.size();
                    if (size == 0)
                        continue;
                    if(sizeStart<size) {
                        ArrayList<Train> trainsMid = SmartTools.findTrains(src.getCode(), stations.get(i).getCode(), sdate);
                        if (trainsMid.size() == 0)
                            break;
                        SplitJourney x = new SplitJourney(src, stations.get(i), dest, date, null, trainsMid, trainsDest);

                        count++;
                        x.setPathLen(mainPath.getDistance());
                        //System.out.println("ADDEDD: "+x);
                        splitJourneys.add(x);
                        sizeStart=size;
                    }

                } catch (NullPointerException E){
                    E.printStackTrace();
                } catch (Exception E){
                    System.out.println("Setting failed=true");
                    E.printStackTrace();
                    failed=true;
                }

            }
            System.out.println("ASYNC findstationsAsync completed: "+splitJourneys.size());

        return null;
        }

        ProgressDialog pd;
        ArrayList<Train> fromSrctrains;
        boolean failed=false;

    }
    boolean direct=true;
    Path mainPath;
    ArrayList<SplitJourney> splitJourneys;
    Station src,dest;
    MyDate date;
    SplitStationsListAdapter listAdapter;
    ListView list;
    ProgressBar progressBar;
}
