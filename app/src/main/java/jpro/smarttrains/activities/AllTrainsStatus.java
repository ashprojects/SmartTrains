package jpro.smarttrains.activities;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import AnimationTools.SmartAnimator;
import Exceptions.AvailabilityFailure;
import SmartTrainTools.Journey;
import SmartTrainTools.MyDate;
import SmartTrainTools.SmartUtils;
import SmartTrainTools.Station;
import SmartTrainTools.Train;
import SmartTrainTools.TravelClass;
import commons.Config;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.AvaiCustomAdapter;

public class AllTrainsStatus extends AppCompatActivity {

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        cancelled=true;
        super.onDestroy();


    }

    private void initAnimations() {
        SmartAnimator.addActivityTransition(getWindow(), SmartAnimator.Type.EXPLODE, 250);
    }
    @Override
    public void onBackPressed(){
        cancelled=true;
        System.out.println("--- BACK PRESSED!");
        this.journeys.clear();
        this.l.setAdapter(null);
        bA.notifyDataSetChanged();
        this.finish();
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trains_status);
        allCompletedTextView=(TextView)findViewById(R.id.allCompletedTextView);
        allTrainsFirstspan=(LinearLayout)findViewById(R.id.allTrainsFirstLL);
        progressBar=(ProgressBar)findViewById(R.id.listMain);
        ArrayList<Train> trains=(ArrayList)getIntent().getSerializableExtra("Trains");
        stn1Main=(TextView)findViewById(R.id.mstn1);
        dateForward=(ImageButton)findViewById(R.id.dateforward);
        dateBackward=(ImageButton)findViewById(R.id.datebackward);
        stn2Main=(TextView)findViewById(R.id.mstn2);
        dayats=(TextView)findViewById(R.id.day_ats);
        date=(TextView)findViewById(R.id.mdate);
        backDateBtn=(ImageButton)findViewById(R.id.change_date_back_btn);
        stn1Main.setText(((Station)getIntent().getSerializableExtra("fromStn")).getName());
        stn2Main.setText(((Station)getIntent().getSerializableExtra("toStn")).getName());
        dayats.setText(SmartUtils.getDayName((MyDate)getIntent().getSerializableExtra("date")).toUpperCase());
        x=(MyDate)getIntent().getSerializableExtra("date");
        date.setText(x.getD()+" "+SmartUtils.getParsedDate(x,"MMM"));
        l=(ListView)findViewById(R.id.statusListView);
        journeys=new ArrayList<>();
        bA=new AvaiCustomAdapter(this,journeys);
        l.setAdapter(bA);
        progressBar.setProgress(0);
        ac=getSupportActionBar();
        //ActionBar actionBar = getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*System.out.println("******************************************************************");
        System.out.println("******************************************************************");
        System.out.println("******************************************************************");
        System.out.println(((ArrayList)getIntent().getSerializableExtra("Trains")));
        System.out.println(getIntent().getSerializableExtra("fromStn"));
        System.out.println(getIntent().getSerializableExtra("toStn"));
        System.out.println(getIntent().getSerializableExtra("date"));
        System.out.println(getIntent().getSerializableExtra("travelClass"));
        */
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        journeys=new ArrayList<>();
        processes=new ArrayList<>();
        for(int i=0;i<trains.size();++i){
            Journey j = new Journey(trains.get(i), trains.get(i).getQuerySrcStn(), trains.get(i).getQueryDestStn(), new MyDate((MyDate) getIntent().getSerializableExtra("date")), ((TravelClass) getIntent().getSerializableExtra("travelClass")).getClassCode(), Config.tQ);
            journeys.add(j);
            try{
                //j.getTrain().fetchInfo_ETrain();
                j.fetchAvailability();
            } catch(Exception E){
                System.out.println("Exception in fetching Availability first");
            }
            ProcessIt k=new ProcessIt(this);
            k.execute(j);
            processes.add(k);
        }

        bA.update(journeys);

        backDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateIndex==4){

                } else {
                    ArrayList<Journey> xj = new ArrayList<Journey>();
                    for (int i = 0; i < initJourneys.size(); ++i) {
                        Journey y=initJourneys.get(i);


                    }
                    bA.notifyDataSetChanged();
                    bA.update(new ArrayList<Journey>());
                    makeToast("DONE!");
                }


            }
        });

        dateForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                x.increment(1);
                Intent i=getIntent();
                i.putExtra("date",x);
                finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(AllTrainsStatus.this).toBundle());
                else
                    startActivity(i);

            }
        });

        dateBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                x.decrement();
                Intent i=getIntent();
                i.putExtra("date",x);
                finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(AllTrainsStatus.this).toBundle());
                else
                    startActivity(i);
            }
        });

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Journey x=(Journey)adapterView.getItemAtPosition(i);
                //TextView desc=x.getDesc();
                Intent in=new Intent(getApplicationContext(),TrainsInfo.class);
                in.putExtra("train",x.getTrain());
                in.putExtra("date",x.getDate());
                /*
                in.putExtra("stn1",x.getSrc());
                in.putExtra("stn2",x.getDesc());
                */
                try {
                    x.getTrain().getSource();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        startActivity(in, ActivityOptions.makeSceneTransitionAnimation(AllTrainsStatus.this).toBundle());
                    else
                        startActivity(in);
                } catch (Exception E) {

                }

                String text=x.getDesc();
                /*if(text.contains("Different")){
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(view.getContext());
                    dlgAlert.setMessage(getString(R.string.boardInfo));
                    dlgAlert.setTitle("Available but...");
                    dlgAlert.show();
                } else if(text.contains("Directly")){
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(view.getContext());
                    dlgAlert.setMessage(getString(R.string.dirAvInfo));
                    dlgAlert.setTitle("Available!");
                    dlgAlert.show();
                } else if(text.contains("Not")){
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(view.getContext());
                    dlgAlert.setMessage(getString(R.string.nAvInfo));
                    dlgAlert.setTitle("Not Available!");
                    dlgAlert.show();
                } else if(text.contains("SERVER")){
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(view.getContext());
                    dlgAlert.setMessage("Sorry for the inconvenience. Please try again later");
                    dlgAlert.setTitle("Railway Server Down!");
                    dlgAlert.show();
                }*/

            }
        });
        initAnimations();
    }

    private void makeToast(String text){
        Toast.makeText(AllTrainsStatus.this,text, Toast.LENGTH_SHORT).show();
    }
    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        return true;
    }


    private class ProcessIt extends AsyncTask<Object, Integer, Void> {

        public ProcessIt(Context C){


        }
        @Override
        protected void onPreExecute(){
            System.out.println("STARTED_CONNECTNOW");
        }

        @Override
        protected void onPostExecute(Void D) {
            counter++;
            bA.update(journeys);
            System.out.println("DONE WITH THAT TRAIN **********");
            progressBar.setProgress(progressBar.getProgress()+progressStep);
            //TextView t=(TextView)findViewById(R.id.fetchingText);
            //t.setVisibility(View.INVISIBLE);
            if(ot)
                showAtvSpan();
            if(counter==journeys.size()){
                initJourneys=new ArrayList<Journey>(journeys);
                progressBar.setVisibility(View.GONE);
                //makeToast("Completed!");
                Snackbar.make(findViewById(R.id.all_train_status_activity),"All Status fetched successfully",Snackbar.LENGTH_SHORT).show();
                allCompletedTextView.setVisibility(View.VISIBLE);
                Collections.sort(journeys);
                bA.update(journeys);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                     getWindow().setStatusBarColor(getResources().getColor(R.color.darkGreen));
                ac.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkGreen)));
                if(Build.VERSION.SDK_INT>15)
                    allTrainsFirstspan.setBackground(new ColorDrawable(getResources().getColor(R.color.darkGreen)));
            }
        }

        private void showAtvSpan(){
            RelativeLayout.LayoutParams rl=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
            rl.setMargins(4,8,4,15);
            l.setLayoutParams(rl);
            TextView x=(TextView)findViewById(R.id.nA_oth);
            x.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Object... arg0) {
            Journey obj=(Journey)arg0[0];
            String trn=String.valueOf(obj.getTrain().getNo());

            try {
                obj.getTrain().fetchInfo_ETrain();
                if(cancelled){
                    //System.out.println("User CANCELLED REQUEST!");
                    return null;
                }
                //System.out.println("Working for: "+obj.getTrain().getName());
                if(obj.validOnThisDate())
                    obj.fun();
                else {
                   /* if((obj.getDest().getCode()+obj.getTrain().getArrivalTimeof((obj.getDest()))==null)||(obj.getTrain().getDepartureTimeof(obj.getSrc()).toString())==null){
                        ot=true;
                    }*/
                    System.out.println(obj.getTrain().getName()+"%%%%%%%%%%%%%% ITS NOT RUNNING ON THIS DATE %%%%%%%%%%%%%%%%%%");
                    obj.setDesc("NOT RUNNING ON THIS DATE");
                    obj.setStatus("N.A.");
                }
                System.out.println("********** ADDED: "+obj);
            } catch(AvailabilityFailure E){
                E.printStackTrace();
               /* if((obj.getDest().getCode()+obj.getTrain().getArrivalTimeof((obj.getDest()))==null)||(obj.getTrain().getDepartureTimeof(obj.getSrc()).toString())==null){
                    ot=true;
                }*/
                obj.setDesc("NOT AVAILABLE");
                obj.setStatus("N.A.");

            }
            catch (Exception AEx) {
               // if((obj.getDest().getCode()+obj.getTrain().getArrivalTimeof((obj.getDest()))==null)||(obj.getTrain().getDepartureTimeof(obj.getSrc()).toString())==null)
                AEx.printStackTrace();
                obj.setDesc("NOT AVAILABLE");
                obj.setStatus("N.A.");
            }

            return null ;
        }
        boolean ot=false;
    }


    private ProgressBar progressBar;
    private AvaiCustomAdapter bA;
    private int progressStep=0;
    private ListView l;
    private LinearLayout allTrainsFirstspan;
    private TextView stn1Main,stn2Main,date,allCompletedTextView,dayats;
    ArrayList<Journey> journeys,initJourneys;
    ArrayList<ProcessIt> processes;
    private int counter=0;
    private int dateIndex=0;
    private ImageView arrow1;
    MyDate x;
    private boolean cancelled=false;
    private ImageButton backDateBtn,dateForward,dateBackward;
    ActionBar ac;

}
