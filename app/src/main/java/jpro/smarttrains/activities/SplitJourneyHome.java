package jpro.smarttrains.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import SmartTrainTools.MyDate;
import SmartTrainTools.Path;
import SmartTrainTools.RailwayCodes;
import SmartTrainTools.SmartTools;
import SmartTrainTools.Station;
import SmartTrainTools.Train;
import SmartTrainTools.TravelClass;
import Utilities.SmartAnimator;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.StationsViewArrayAdapter;

public class SplitJourneyHome extends AppCompatActivity {

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_journey_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tc=new TravelClass("SL");
        String []Stations = RailwayCodes.pullStations();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tc.getAllClassesFull());
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, Stations);
        StationsViewArrayAdapter carrayAdapter = new StationsViewArrayAdapter(this, R.layout.def_spinner,R.id.txtContent, Stations);


        stn1 = (AutoCompleteTextView) findViewById(R.id.srcStn_Autocomplete);
        stn2 = (AutoCompleteTextView) findViewById(R.id.destStn_Autocomplete);
        stn1.setThreshold(1);
        stn2.setThreshold(1);
        stn1.setAdapter(carrayAdapter);
        stn2.setAdapter(carrayAdapter);

        clear_stn1 = (ImageView) findViewById(R.id.train_between_clear_Src);
        clear_stn2 = (ImageView) findViewById(R.id.train_between_clear_dest);
        /*classSel=(Spinner)findViewById(R.id.split_classSel);
        classSel.setAdapter(spinnerAdapter);*/
        dateSelBtn = (Button) findViewById(R.id.dateSelBtnTrainBetweenStations);
        splitbtn = (Button) findViewById(R.id.showTrains);
        dateSel = (TextView) findViewById(R.id.dateInfoTextView);
        splitbtn.setText("SPLIT JOURNEY");
        clear_stn1.setAlpha(0F);
        clear_stn2.setAlpha(0F);
        dateSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                System.out.println(dateG);
                DatePickerDialog dpd = new DatePickerDialog(SplitJourneyHome.this,onc, mYear, mMonth, mDay);
                c.add(Calendar.DAY_OF_MONTH,90);
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                dpd.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()-1000);
                dpd.show();
            }
        });

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

        stn1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("///88888888888888888888   Clicked on: " + view);
                stn2.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        stn2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard();
            }
        });

        clear_stn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stn1.setText("");
            }
        });
        clear_stn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stn2.setText("");
            }
        });

        splitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!RailwayCodes.isValidStation(stn1.getText().toString())) {
                    makeToast("Invalid value at Station 1. Select one from dropdown");
                    return;
                }
                if (!RailwayCodes.isValidStation(stn2.getText().toString())) {
                    makeToast("Invalid value at Station 2. Select one from dropdown");
                    return;
                }

                if (stn1.getText().toString().equals(stn2.getText().toString())) {
                    makeToast("Are you drunk? Source and destination can't be same");
                    return;
                }
                /*if (dateG.getDate().length() < 1) {
                    makeToast("Please Select Date");
                    return;
                }*/

                final String src = stn1.getText().toString().split("-")[1].trim();
                final String dest = stn2.getText().toString().split("-")[1].trim();
                fromStn = new Station(src, stn1.getText().toString().split("-")[0].trim());
                toStn = new Station(dest, stn2.getText().toString().split("-")[0].trim());

                //ob=new ServiceObj(src,dest,date,classSel.getSelectedItem().toString());
                //makeToast(ob.getAll());

                try {

                    new DoJob().execute();

                } catch (Exception E) {
                    E.printStackTrace();
                    makeToast("Some Error Occurred! Please try again: " + E.getMessage());

                }


            }
        });
        try{
            Station sstn1=(Station)getIntent().getSerializableExtra("stn1");
            Station sstn2=(Station)getIntent().getSerializableExtra("stn2");
            stn1.setText(sstn1.getName()+" - "+sstn1.getCode());
            stn2.setText(sstn2.getName()+" - "+sstn2.getCode());
            splitbtn.performClick();
        } catch (Exception E){

        }
        initAnimations();
    }

    private void initAnimations() {
        SmartAnimator.addActivityTransition(getWindow(), SmartAnimator.Type.FADE, 250);
    }

    private DatePickerDialog.OnDateSetListener onc = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {


            dateSel.setText(dayOfMonth + "/"
                    + (monthOfYear + 1) + "/" + year);
            //date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            dateG = new MyDate(dayOfMonth, monthOfYear + 1, year);
            //corrPng.setVisibility(View.VISIBLE);

        }
    };

    private File createFileFromInputStream(InputStream inputStream) {

        try{
            File f = new File("x.ser");
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            //Logging exception
        }

        return null;
    }

    private void makeToast(String text) {
        Toast.makeText(SplitJourneyHome.this, text, Toast.LENGTH_LONG).show();
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@ "+text);
    }

    private void proceed(boolean dirTrains, ArrayList<Train> trains, Path path){
            Intent in=new Intent(SplitJourneyHome.this,SplitStations.class);
            in.putExtra("src",fromStn);
            in.putExtra("dest",toStn);
            in.putExtra("date",dateG);
            in.putExtra("class",tc);
            in.putExtra("path",path);
            startActivity(in);

    }

    private class DoJob extends AsyncTask<Object,Integer,Void>{

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(SplitJourneyHome.this);
            pd.setTitle("Working...");
            pd.setCancelable(false);
            pd.setMessage("Just a moment...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected Void doInBackground(Object... objects) {

                try{
                    // Date Not Considered
                    trains= SmartTools.findTrains(fromStn.getCode(),toStn.getCode(),null);
                    trains.size();
                    path = SmartTools.split_route(fromStn, toStn);
                    path.removeAllNonJunctions();
                    directTrains=true;
                    //path=null;
                } catch(NullPointerException Ex) {
                    directTrains=false;
                    path = SmartTools.split_route(fromStn, toStn);
                    path.removeAllNonJunctions();
                } catch (IOException E){
                    directTrains=false;
                    trains=null;
                    path=null;
                }
            System.out.println("Optimised Path: " + path.getStations());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.hide();
            try{
                path.getStations();
                proceed(directTrains,trains,path);
            } catch (NullPointerException E){
                System.out.println("NULLL AT POST");
                Snackbar sn=Snackbar.make(findViewById(R.id.activity_split_journey_home),"Connection to Server failed! Please try again",Snackbar.LENGTH_LONG);
                sn.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                sn.show();
            }

        }
        ProgressDialog pd;
        boolean directTrains=false;
        Path path;
        ArrayList<Train> trains;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       onBackPressed();
        return true;
    }

    AutoCompleteTextView stn1;
    AutoCompleteTextView stn2;
    Station fromStn,toStn;
    TravelClass tc;
    Button dateSelBtn,splitbtn;
    TextView dateSel;
    ImageView clear_stn1,clear_stn2;
    MyDate dateG;
    Spinner classSel;
    DatePicker dp;
}
