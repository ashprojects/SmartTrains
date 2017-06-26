package jpro.smarttrains.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import AnimationTools.SmartAnimator;
import SmartTrainTools.AvailabilityStatus;
import SmartTrainTools.Journey;
import SmartTrainTools.MyDate;
import SmartTrainTools.RailwayCodes;
import SmartTrainTools.RouteListItem;
import SmartTrainTools.SmartTools;
import SmartTrainTools.Station;
import SmartTrainTools.Train;
import SmartTrainTools.TravelClass;
import commons.Config;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.CustomTrainSpinnerAdapter;
import jpro.smarttrains.adapters.QuotaSpinnerAdapter;
import jpro.smarttrains.adapters.StationsViewArrayAdapter;
import jpro.smarttrains.adapters.TrainSpinnerAdapter;
import jpro.smarttrains.adapters.TravelClassAdapter;

public class SeatAvailabilitySimple extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_availability_simple);
        setTitle("Seat Availability");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initVariables();
        getIntentInfo();
        setAllOnClickListeners();
        initClearButtons();
        if(hasIntentData){
            trBox.setVisibility(View.VISIBLE);
            trBox.setText(lastSelectedTrain.getNoAsString()+"-"+lastSelectedTrain.getName());
            trBox.setEnabled(false);
        }
        initAnimations();

    }


    private void setAllOnClickListeners() {
        dateSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                System.out.println(dateG);
                DatePickerDialog dpd = new DatePickerDialog(SeatAvailabilitySimple.this, onc, mYear, mMonth, mDay);
                c.add(Calendar.DAY_OF_MONTH, 120);
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                dpd.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis() - 1000);
                dpd.show();
            }
        });

        stn1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                src=new Station(stn1.getText().toString().split("-")[1].trim());
                avTableRoot.setVisibility(View.INVISIBLE);
                selectedTrainsLL.setVisibility(View.GONE);
                stn2.requestFocus();

            }
        });

        stn2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dest=new Station(stn2.getText().toString().split("-")[1].trim());
                //dest=new Station(stnArrayAdapter.getItem(position).toString().split("-")[1].trim());
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                hideKeyboard();
                avTableRoot.setVisibility(View.INVISIBLE);
                selectedTrainsLL.setVisibility(View.GONE);
                classSpinner.requestFocus();
            }
        });

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                avTableRoot.setVisibility(View.GONE);
                //selectedTrainsLL.setVisibility(View.GONE);
                selectedClass=((TravelClass)classSpinnerAdapter.getItem(i)).getClassCode();
                switch (selectedClass){
                    case "SL": extraClass="3A";
                        break;
                    case "3A": extraClass="2A";
                        break;
                    case "2A": extraClass="3A";
                        break;
                    case "1A": extraClass="2A";
                        break;
                }
                quotaSpinner.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        quotaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                avTableRoot.setVisibility(View.GONE);
                //selectedTrainsLL.setVisibility(View.GONE);
                selectedQuota=quotaAdapter.getItem(i).split(" ")[0];
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                dateSelBtn.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        clear_stn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stn1.setText("");
                clear_stn1.setAlpha(0F);
            }
        });
        clear_stn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stn2.setText("");
                clear_stn2.setAlpha(0.4F);
            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avTableRoot.setVisibility(View.GONE);
                //System.out.println("RUNS_ON: "+dateG.getDayNumber()+": "+lastSelectedTrain.runsOnDate(dateG));
                try {
                    if (lastSelectedTrain.runsOnDate(dateG)) {

                        boolean st = allGood();
                        System.out.println("ST+="+st);
                        if (!spSpanOpen) {
                            st = st && (trBox.getText().toString().split("-").length == 2);
                        }
                        if (st) {

                            new findSeats().execute();
                        } else {
                            showInvalidSnack("Please fill the page with valid values");
                        }
                    } else {
                        String[] x = lastSelectedTrain.getRunningDaysAsString().split(";");
                        try {
                            String k = "";
                            //System.out.println("RS:"+ Arrays.asList(lastSelectedTrain.getRunsOn()));
                            for (int i = 1; i < x.length; ++i) {
                                k += x[i].split(":")[0] + ", ";
                            }
                            if (k.contains(","))
                                k = k.substring(0, k.lastIndexOf(','));

                            showInvalidSnack("Train runs only on " + k);
                        } catch (NullPointerException E) {

                            showInvalidSnack("Train is not running on this date");
                        }
                    }
                }catch (NullPointerException E){
                    if(trBox.getText().toString().split("-").length==2){
                        boolean st = allGood();
                        if (!spSpanOpen) {
                            System.out.println("ST:=" + st);
                            st = st && (trBox.getText().toString().split("-").length == 2);
                        }
                        if (st) {

                            new findSeats().execute();
                        } else {
                            showInvalidSnack("Please fill the page with valid values");
                        }

                    } else
                        showInvalidSnack("Please fill all the details correctly");
                }

            }
        });


    }

    private void getIntentInfo() {
        try {
            lastSelectedTrain = (Train) getIntent().getSerializableExtra("train");
            lastSelectedTrain.getName();
            hasIntentData = true;
            Stations = new String[lastSelectedTrain.getRoute().size()];
            int k = 0;
            for (RouteListItem RT : lastSelectedTrain.getRoute())
                Stations[k++] = (RT.getStation().getName() + " - " + RT.getStation().getCode());
            stnArrayAdapter = new StationsViewArrayAdapter(this, R.layout.def_spinner, R.id.txtContent, Stations);
            classSpinnerAdapter.update(TravelClass.getTravelClassesFrom(lastSelectedTrain.getClassAvail()));
            src = (Station) getIntent().getSerializableExtra("stn1");
            dest = (Station) getIntent().getSerializableExtra("stn2");
            int srcpos = stnArrayAdapter.getPosition(src.getName() + " - " + src.getCode());
            int destpos = stnArrayAdapter.getPosition(dest.getName() + " - " + dest.getCode());
            System.out.println("DEST: " + destpos + " SRC:" + srcpos);
            stn1.setText(Stations[srcpos]);
            stn2.setText(Stations[destpos]);
            src = new Station(Stations[srcpos].split(" - ")[1]);
            dest = new Station(Stations[destpos].split(" - ")[1]);
            stn1.dismissDropDown();
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private void initVariables() {
        String[] travelQuota = new String[]{"GN GENERAL", "TQ TATKAL"};
        ArrayList<String> quotaArrayList = new ArrayList<>(Arrays.asList(travelQuota));
        avTableRoot = (TableLayout) findViewById(R.id.av_table);
        trBox = (TextView) findViewById(R.id.trNo);
        stn1 = (AutoCompleteTextView) findViewById(R.id.stn1);
        stn2 = (AutoCompleteTextView) findViewById(R.id.stn2);
        dateSel = (TextView) findViewById(R.id.date);
        clear_stn1 = (ImageView) findViewById(R.id.seat_avai_clear_src);
        clear_stn2 = (ImageView) findViewById(R.id.seat_avai_clear_dest);
        ldt = (ProgressBar) findViewById(R.id.loadTrainspbar);
        noTrainsDesc = (TextView) findViewById(R.id.seat_avai_simple_no_trains_tv);
        classSpinner = (Spinner) findViewById(R.id.classSpinner);
        quotaSpinner = (Spinner) findViewById(R.id.quotaSpinner);
        dateSelBtn = (Button) findViewById(R.id.dateSelBtnSimpleAvai);
        selectedTrainsLL = (LinearLayout) findViewById(R.id.train_spinner_layout);
        trainSpinner = (Spinner) findViewById(R.id.trNoSpinner);
        findBtn = (Button) findViewById(R.id.gtsAvai);
        classSpinnerList = TravelClass.getAllClassesObjects();
        Stations = RailwayCodes.pullStations();
        stnArrayAdapter = new StationsViewArrayAdapter(this, R.layout.def_spinner, R.id.txtContent, Stations);
        classSpinnerAdapter = new TravelClassAdapter<TravelClass>(this, R.layout.def_spinner, R.id.txtContent, classSpinnerList);
        spinnerAdapter2 = new QuotaSpinnerAdapter<>(this, R.layout.def_spinner, R.id.txtContent, quotaArrayList);
        quotaAdapter = new ArrayAdapter<>(this, R.layout.small_spinner_item, travelQuota);
        trains = new ArrayList<>(Config.rc.getTrains());
        stn1.setAdapter(stnArrayAdapter);
        stn2.setAdapter(stnArrayAdapter);
        classSpinner.setAdapter(classSpinnerAdapter);
        quotaSpinner.setAdapter(spinnerAdapter2);
        stn1.setThreshold(2);
        stn2.setThreshold(2);
        avTableRoot.setVisibility(View.GONE);
    }

    private void initClearButtons() {
        clear_stn1.setAlpha(0F);
        clear_stn2.setAlpha(0F);

        stn1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (!(stn1.getText().length() > 0)) {
                        clear_stn1.setAlpha(0F);
                    } else {
                        clear_stn1.setAlpha(0.4F);
                    }
                } catch (Exception E) {
                    clear_stn1.setAlpha(0F);
                }
            }
        });
        stn2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (!(stn2.getText().length() > 0)) {
                        clear_stn2.setAlpha(0F);
                    } else {
                        clear_stn2.setAlpha(0.4F);
                    }
                } catch (Exception E) {
                    clear_stn2.setAlpha(0F);
                }
            }
        });

    }

    private void initAnimations() {
        SmartAnimator.addActivityTransition(getWindow(), SmartAnimator.Type.EXPLODE, 250);
    }

    private boolean allGood(){
        System.out.println("/****");
        System.out.println("STN1+="+stn1.getText().toString()+"__"+"STN2+="+stn2.getText().toString()+"__DATE+="+dateSel.getText());
        System.out.println(RailwayCodes.isValidStation(stn1.getText().toString())+ " "+RailwayCodes.isValidStation(stn2.getText().toString()));
        boolean st=RailwayCodes.isValidStation(stn1.getText().toString())&&RailwayCodes.isValidStation(stn2.getText().toString())&&(!stn1.getText().toString().equals(stn2.getText().toString()))&&(!dateSel.getText().toString().equals("DATE"));

        return st;
    }

    private void showStatus(boolean status){
        if(status){

            AvailabilityStatus[] ar1,ar2;

            if(((String)quotaSpinner.getSelectedItem()).contains("GN")){
                ar1 =J.getAllGNStatus();
                ar2 =J2.getAllGNStatus();
            } else {
                ar1 =J.getAllCKStatus();
                ar2 =J2.getAllCKStatus();
            }


            //cl1.setText(selectedClass);
            //cl2.setText(extraClass);
            float scale = getResources().getDisplayMetrics().density;


            for(int i=1;i<avTableRoot.getChildCount();++i){
                View v=avTableRoot.getChildAt(i);
                avTableRoot.removeView(v);
            }

            avTableRoot.removeAllViewsInLayout();
            TableRow Srow=new TableRow(this);
            TableRow.LayoutParams x=new TableRow.LayoutParams(0);
            TableLayout.LayoutParams tblp=new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            Srow.setLayoutParams(tblp);
            x.weight = (float) 0.3;

            TextView date=new TextView(this);

            date.setLayoutParams(x);
            date.setText("DATE");

            date.setTextColor(Color.WHITE);
            if(Build.VERSION.SDK_INT>16)
                date.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            date.setTypeface(null,Typeface.BOLD);
            date.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            //date.setTextSize((int)(5*scale+0.5f));
            Srow.addView(date);

            TextView c1=new TextView(this);
            c1.setLayoutParams(x);
            //c1.setTextSize(getResources().getDimension(R.dimen.textsize));
            c1.setText(selectedClass);
            c1.setTextColor(Color.WHITE);
            if(Build.VERSION.SDK_INT>16)
                c1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c1.setTypeface(null,Typeface.BOLD);
            c1.setBackgroundColor(getResources().getColor(R.color.colorAccent));


            Srow.addView(c1);

            TextView c2=new TextView(this);
            c2.setLayoutParams(x);
            c2.setText(extraClass);
            //c2.setTextSize(getResources().getDimension(R.dimen.textsize));
            c2.setTextColor(Color.WHITE);
            if(Build.VERSION.SDK_INT>16)
                c2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c2.setTypeface(null,Typeface.BOLD);
            c2.setBackgroundColor(getResources().getColor(R.color.colorAccent));


            Srow.addView(c2);
            MyDate tblDate=new MyDate(this.dateG);
            avTableRoot.addView(Srow);
            for(int i=0;i<6;++i){
                    TableRow row=new TableRow(this);
                    x=new TableRow.LayoutParams(0);
                    tblp=new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                    row.setLayoutParams(tblp);
                    TextView date2=new TextView(this);
                x.weight = (float) 0.3;
                    date2.setLayoutParams(x);
                    date2.setText(tblDate.getBeautifiedDate());
                    date2.setTextColor(Color.BLACK);
                    if(Build.VERSION.SDK_INT>16)
                        date2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    date2.setTypeface(null,Typeface.BOLD);
                    //date.setPadding((int)(0.5*scale+0.5f),0,0,0);
                //date2.setTextSize((int)(5*scale+0.5f));
                    row.addView(date2);
                    tblDate.increment(1);
                    TableRow.LayoutParams y=new TableRow.LayoutParams(1);
                y.weight = (float) 0.3;
                    boolean e=true;
                    TextView av1=new TextView(this);
                    av1.setLayoutParams(y);
                    av1.setTypeface(null,Typeface.BOLD);
                    try{
                        av1.setText(""+ar1[i].getStatus());
                    } catch (NullPointerException E){
                        e=false;
                        av1.setText("NOT AVAILABLE");
                    }
                    if(Build.VERSION.SDK_INT>16)
                        av1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    if(e) {
                        if (ar1[i].getStatus().contains("AV") && (!ar1[i].getStatus().contains("NOT"))) {
                            av1.setTextColor(Color.parseColor("#22630e"));
                        } else if (ar1[i].getStatus().contains("RAC")) {
                            av1.setTextColor(Color.parseColor("#a3ae08"));
                        } else
                            av1.setTextColor(Color.RED);

                    } else {
                        av1.setTextColor(Color.GRAY);

                    }
                //av1.setTextSize((int)(5*scale+0.5f));
                    row.addView(av1);



                    e=true;
                    TableRow.LayoutParams z = new TableRow.LayoutParams(2);
                z.weight = (float) 0.3;
                    TextView av2 = new TextView(this);
                    av2.setLayoutParams(z);
                    av2.setTypeface(null, Typeface.BOLD);
                    try {
                        av2.setText("" + ar2[i].getStatus());
                    } catch (NullPointerException E){
                        e=false;
                        av2.setText("NOT AVAILABLE");
                    }
                    if(e){
                        if (ar2[i].getStatus().contains("AV") && (!ar2[i].getStatus().contains("NOT"))) {
                            av2.setTextColor(Color.parseColor("#22630e"));
                        } else if (ar2[i].getStatus().contains("RAC")) {
                            av2.setTextColor(Color.parseColor("#a3ae08"));
                        } else
                            av2.setTextColor(Color.RED);
                     } else {
                        av2.setTextColor(Color.GRAY);
                    }
                    if(Build.VERSION.SDK_INT>16)
                        av2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    //av2.setPadding((int) (0.5 * scale + 0.5f), 0, 0, 0);

                //av2.setTextSize((int)(5*scale+0.5f));
                    row.addView(av2);

                row.setBackgroundResource(R.drawable.table_border_bg_white);
                if(row.getChildCount()!=0)
                    avTableRoot.addView(row);
            }

            SmartAnimator.circularRevealView(avTableRoot, 250, SmartAnimator.What.OPEN, null);
            final ScrollView scrollview = ((ScrollView) findViewById(R.id.activity_seat_availability_simple));
            scrollview.post(new Runnable() {
                @Override
                public void run() {
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });


        } else
            showInvalidSnack("Request Failed. Please Try Again Later");
    }

    private class findTrains extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPostExecute(Void aVoid) {

            ldt.setVisibility(View.GONE);
            if(status){
                spSpanOpen=true;
                noTrainsDesc.setVisibility(View.GONE);
                SmartAnimator.circularRevealView(selectedTrainsLL, 500, SmartAnimator.What.OPEN, null);

                trainSpinnerAdapter=new TrainSpinnerAdapter(SeatAvailabilitySimple.this,R.layout.def_spinner,R.id.txtContent,trains);
                trainSpinner.setAdapter(trainSpinnerAdapter);
                trainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        classSpinnerAdapter.update(TravelClass.getTravelClassesFrom(((Train)trainSpinnerAdapter.getItem(i)).getClassAvail()));
                        lastSelectedTrain=(Train)trainSpinnerAdapter.getItem(i);
                        Station T_Src=((Train) trainSpinnerAdapter.getItem(i)).getQuerySrcStn();
                        Station T_Dest=((Train) trainSpinnerAdapter.getItem(i)).getQueryDestStn();
                        //stn1.setSelection(T_Src.getName()+" - "+T_Src.getCode());
                        src=T_Src;
                        dest=T_Dest;
                        //stn2.setSelection(T_Dest.getName()+" - "+T_Dest.getCode());
                        classSpinner.setSelection(0);
                        selectedClass=((TravelClass)classSpinner.getSelectedItem()).getClassCode();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                if (lastSelectedTrain != null) {
                    trainSpinner.setSelection(trainSpinnerAdapter.getPosition(lastSelectedTrain));
                } else {
                    lastSelectedTrain = (Train) trainSpinnerAdapter.getItem(0);
                    trainSpinner.setSelection(0);
                }
                //

            } else if(noDirTrains){
                new AlertDialog.Builder(SeatAvailabilitySimple.this).setTitle("No Direct Trains Found")
                        .setMessage("Sorry We Couldn't find any direct trains between stations provided. Would you like to try out Split Journey?")
                        .setCancelable(true)
                        .setPositiveButton("Yes, Split my Journey", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent in= new Intent(SeatAvailabilitySimple.this,SplitJourneyHome.class);
                                in.putExtra("stn1",src);
                                in.putExtra("stn2",dest);
                                startActivity(in);
                            }
                        }).setNegativeButton("No",null).show();
            } else {
                showInvalidSnack("Connection Failed. Please try again");
            }

        }

        @Override
        protected void onPreExecute() {

            ldt.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(src==null||dest==null){
                return null;
            } else {


                try {

                    x = SmartTools.findTrains(src.getCode(), dest.getCode(), dateG.getDate());
                    if(x.size()==0){
                     noDirTrains=true;
                        return null;
                    }

                    trains=new ArrayList<>();
                    for(int i=0;i<x.size();++i){
                        if(x.get(i).runsOnDate(dateG)){
                            trains.add(x.get(i));
                        }
                    }

                    status=true;
                }catch(NullPointerException E){
                    E.printStackTrace();
                } catch (IOException E){
                    E.printStackTrace();
                    System.out.println("IOEXC");
                    respNull=true;
                }
            }
            return null;
        }
        ArrayList<Train> x;
        boolean status=false;
        boolean noDirTrains=false;
        boolean respNull=true;

    }

    private class findSeats extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(SeatAvailabilitySimple.this);
            pd.setTitle("Loading");
            pd.setMessage("Connecting to Server...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            showStatus(status);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                lastSelectedTrain.fetchInfo_ETrain();
                J=new Journey(lastSelectedTrain,src,dest,dateG,selectedClass,selectedQuota);

                J.fetchAvailability();
                try{
                    J2=new Journey(lastSelectedTrain,src,dest,dateG,extraClass,selectedQuota);
                    J2.fetchAvailability();
                } catch (IOException N){
                    N.printStackTrace();
                }
                status=true;
            } catch (IOException E){
                status=false;
                showInvalidSnack("Connection Failed!");
            }
            return null;
        }
        ProgressDialog pd;
        boolean status;
    }

    private void showInvalidSnack(String msg){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_seat_availability_simple), msg, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private DatePickerDialog.OnDateSetListener onc = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {



            date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            dateG = new MyDate(dayOfMonth, monthOfYear + 1, year);
            dateSel.setText(dateG.getBeautifiedDate());
            if (allGood()) {
                new findTrains().execute();
            }
            //corrPng.setVisibility(View.VISIBLE);

        }
    };

    String[] Stations;
    TextView trBox;
    Train lastSelectedTrain=null;
    String date,selectedClass,selectedQuota,extraClass="3A";
    TextView dateSel, noTrainsDesc;
    Spinner classSpinner,quotaSpinner;
    AutoCompleteTextView stn1,stn2;
    ProgressBar ldt;
    Journey J=null,J2;
    MyDate dateG;
    ImageButton closeSelLL;
    ArrayList<Train> trains;
    ArrayList<TravelClass> classSpinnerList;
    Spinner trainSpinner;
    //LinearLayout allTrainsLL;
    LinearLayout selectedTrainsLL;
    TrainSpinnerAdapter trainSpinnerAdapter;
    TravelClassAdapter classSpinnerAdapter;
    ArrayAdapter<String> quotaAdapter;
    QuotaSpinnerAdapter spinnerAdapter2;
    TableLayout avTableRoot;
    Station src,dest;
    StationsViewArrayAdapter stnArrayAdapter;
    boolean spSpanOpen=false,hasIntentData=false;
    CustomTrainSpinnerAdapter ad;
    Button dateSelBtn,findBtn;
    ImageView clear_stn1, clear_stn2;

}
