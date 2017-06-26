package jpro.smarttrains.activities;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import AnimationTools.SmartAnimator;
import SmartTrainTools.MyDate;
import SmartTrainTools.RailwayCodes;
import SmartTrainTools.SmartUtils;
import SmartTrainTools.Station;
import SmartTrainTools.Train;
import SmartTrainTools.TravelClass;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.StationsViewArrayAdapter;
import jpro.smarttrains.adapters.TravelClassAdapter;

import static SmartTrainTools.SmartTools.findTrains;

public class SmartAvailActivity extends AppCompatActivity {
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
    protected void onResume() {
        try{
            this.trains.clear();
        } catch ( Exception E){

        }
        super.onResume();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Smart Availability");
        Stations = RailwayCodes.pullStations();
        //new CheckForUpdate().execute();
        clear_stn1 = (ImageView) findViewById(R.id.clear_stn1);
        clear_stn2 = (ImageView) findViewById(R.id.clear_stn2);
        trains = new ArrayList<>();
        TravelClass tc = new TravelClass("SL");
        gtsBtn = (Button) findViewById(R.id.gtsBtn);
        classSel = (Spinner) findViewById(R.id.classSel);
        corrPng = (ImageView) findViewById(R.id.correctPng);
        dateSelBtn = (Button) findViewById(R.id.dateSelBtn);
        stn1 = (AutoCompleteTextView) findViewById(R.id.stn1);
        stn2 = (AutoCompleteTextView) findViewById(R.id.stn2);
        dateSel = (TextView) findViewById(R.id.dateSel1);

        TravelClassAdapter<TravelClass> spinnerAdapter = new TravelClassAdapter<TravelClass>(this, R.layout.def_spinner,R.id.txtContent, TravelClass.getAllClassesObjects());

        //CustomStationSpinnerAdapter ad=new CustomStationSpinnerAdapter(this,R.layout.activity_home,R.id.scode,allStations);
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, Stations);
        StationsViewArrayAdapter carrayAdapter = new StationsViewArrayAdapter(this, R.layout.def_spinner,R.id.txtContent, Stations);


        stn1.setThreshold(2);
        stn2.setThreshold(2);
        classSel.setAdapter(spinnerAdapter);
        stn1.setAdapter(carrayAdapter);
        classSel.setSelection(3);
        stn2.setAdapter(carrayAdapter);
        dateSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(SmartAvailActivity.this, onc, mYear, mMonth, mDay);
                c.add(Calendar.DAY_OF_MONTH, 120);
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                dpd.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis() - 1000);
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

        stn1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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


        gtsBtn.setOnClickListener(new View.OnClickListener() {
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
                if (date.length() < 1) {
                    makeToast("Please Select Date");
                    return;
                }

                final String src = stn1.getText().toString().split("-")[1].trim();
                final String dest = stn2.getText().toString().split("-")[1].trim();
                fromStn = new Station(src, stn1.getText().toString().split("-")[0].trim());
                toStn = new Station(dest, stn2.getText().toString().split("-")[0].trim());

                //ob=new ServiceObj(src,dest,date,classSel.getSelectedItem().toString());
                //makeToast(ob.getAll());
                if(SmartUtils.validTime()){
                    try {

                        process = new ConnectNow(SmartAvailActivity.this);
                        process.execute(src, dest);

                    } catch (Exception E) {
                        E.printStackTrace();
                        makeToast("Some Error Occurred! Please try again: " + E.getMessage());

                    }
                } else {
                    Snackbar.make(findViewById(R.id.smartAvailActivity),"Sorry, App is not functional within this time range!",Snackbar.LENGTH_LONG).show();
                }



            }
        });
        initAnimations();

    }

    private void initAnimations() {
        SmartAnimator.addActivityTransition(getWindow(), SmartAnimator.Type.EXPLODE, 250);
    }

    private static class TaskProgress {
        final int percentage;
        final String message;

        TaskProgress(int percentage, String message) {
            this.percentage = percentage;
            this.message = message;
        }
    }

    private class ConnectNow extends AsyncTask<Object, TaskProgress, Void> {
        ProgressDialog pd;
        String msg = "Fetching required data... May take some time";

        public ConnectNow(Context C) {
            pd = new ProgressDialog(C);
            pd.setCancelable(true);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    process.cancel(true);
                }
            });
            pd.setMessage(msg);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setProgressNumberFormat(null);
            pd.setTitle("Initialising...");
            trains.clear();

        }

        public void onProgressUpdate(TaskProgress progress) {
            pd.setProgress(progress.percentage);
            pd.setMessage(progress.message);
        }

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected void onPostExecute(Void D) {
            if (!isCancelled()) {
                if (pd.isShowing())
                    pd.dismiss();
                passIntent();
            }
            //System.out.println(trains);

        }

        @Override
        protected Void doInBackground(Object... arg0) {
            try {
                ArrayList<Train> a = findTrains(arg0[0].toString(), arg0[1].toString(), null);
                pd.setMax(a.size());
                for (Train t : a) {
                    //Train t = new Train(Integer.parseInt(b.split("-")[0]), true, false);
                    try {
                        //t.fetchInfo_ETrain();
                        if (isCancelled()) {
                            trains.clear();
                            break;
                        }

                        int[] rn = new int[7];
                        //String x = b.split("-")[1];
                    /*System.out.println(x);
                    String[] y = x.split(" ");
                    for (int i = 0; i < 7; ++i) {
                        rn[i] = Integer.parseInt(y[i]);
                    }
                    t.setRunsOn(rn);*/
                        trains.add(t);
                        pd.incrementProgressBy(1);
                    } catch (Exception E){

                    }
                    publishProgress(new TaskProgress(pd.getProgress() + 1, msg + ": " + t.getName()));
                }
            } catch (Exception E) {
                E.printStackTrace();
            }
            return null;
        }


    }


    private void makeToast(String text) {
        Toast.makeText(SmartAvailActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private DatePickerDialog.OnDateSetListener onc = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {


            dateSel.setText(dayOfMonth + "/"
                    + (monthOfYear + 1) + "/" + year);
            date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            dateG = new MyDate(dayOfMonth, monthOfYear + 1, year);
            //corrPng.setVisibility(View.VISIBLE);

        }
    };

    protected void passIntent() {
        //makeToast("Values: "+ob.getList());
        if (trains.size() > 0) {
            Intent in = new Intent(SmartAvailActivity.this, AllTrainsStatus.class);
            if (trains == null)
                System.out.println("WARNING: SENDING NULL TRAIN ARRAYLIST");
            in.putExtra("Trains", trains);
            in.putExtra("fromStn", fromStn);
            in.putExtra("toStn", toStn);
            in.putExtra("date", dateG);
            in.putExtra("travelClass", (TravelClass)classSel.getSelectedItem());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                startActivity(in, ActivityOptions.makeSceneTransitionAnimation(SmartAvailActivity.this).toBundle());
            else
                startActivity(in);

        } else {
            makeToast("Sorry, We couldn't find any trains between provided stations. Are you connected to the Internet?");
        }
    }

    AutoCompleteTextView stn1;
    AutoCompleteTextView stn2;
    Station fromStn, toStn;
    TravelClass tc;
    MyDate dateG;
    DatePicker dp;
    ConnectNow process;
    ArrayList<Train> trains = null;
    TextView dateSel;
    String Stations[];
    ArrayList<Station> allStations;
    Button dateSelBtn, gtsBtn;
    ImageView corrPng, clear_stn1, clear_stn2;
    Spinner classSel;
    String date = "";
}
