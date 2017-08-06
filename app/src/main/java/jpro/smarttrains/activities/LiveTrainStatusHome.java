package jpro.smarttrains.activities;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import SmartTrainTools.MyDate;
import SmartTrainTools.Train;
import SmartTrainTools.TrainLiveStatus;
import Utilities.SmartAnimator;
import commons.Config;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.CustomTrainSpinnerAdapter;
import jpro.smarttrains.views.DelayedAutoCompleteTextView;

public class LiveTrainStatusHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_train_status_home);
        initVariables();
        initAnimations();
    }


    private void initAnimations() {
        SmartAnimator.addActivityTransition(getWindow(), SmartAnimator.Type.EXPLODE, 250);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private void initVariables() {

        trainSelectTextView = (DelayedAutoCompleteTextView) findViewById(R.id.running_status_select_trName);
        dateSelBtn = (Button) findViewById(R.id.running_status_select_date);
        fromStnCode = (TextView) findViewById(R.id.running_status_home_st1code);
        toStnCode = (TextView) findViewById(R.id.running_status_home_st2code);
        fromStnFull = (TextView) findViewById(R.id.running_status_home_st1name);
        cross = (ImageButton) findViewById(R.id.running_status_clear_btn);
        toStnFull = (TextView) findViewById(R.id.running_status_home_st2name);
        hiddenDateSelLayout = (LinearLayout) findViewById(R.id.running_status_TrInfoLL);
        pb = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        trains = new ArrayList<Train>(Config.rc.getTrains());
        hiddenDateSelLayout.setVisibility(View.GONE);
        setTitle("Live Train Status");
        CustomTrainSpinnerAdapter ad = new CustomTrainSpinnerAdapter(this, R.layout.activity_live_train_status_home, R.id.txtContent, trains);
        trainSelectTextView.setAdapter(ad);
        trainSelectTextView.setThreshold(3);
        trainSelectTextView.setLoadingIndicator(pb);
        cross.setAlpha(0F);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        trainSelectTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (trainSelectTextView.getText().toString().length() > 0) {
                    cross.setAlpha(0.4F);
                } else {
                    cross.setAlpha(0F);
                }
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainSelectTextView.setText("");
            }
        });
        try {
            Train train = (Train) getIntent().getSerializableExtra("train");
            selectedTrain = train;
            trainSelectTextView.setText(train.getNo() + " - " + train.getName());
            new GetTrainInfo().execute();
        } catch (Exception E) {
            E.printStackTrace();
        }
        trainSelectTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTrain = trains.get(position);
                hideKeyboard();
                new GetTrainInfo().execute();
                System.out.println("999 STARTED");
            }
        });
        dialog = new DatePickerDialog();

        dateSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();

                if (trainLiveStatus.getMinDate() == null) {
                    Toast.makeText(LiveTrainStatusHome.this, "No Dates Selectable for Given Train", Toast.LENGTH_LONG).show();
                } else {
                    ArrayList<Calendar> calendars = new ArrayList<Calendar>();
                    if (trainLiveStatus.getTrain().runsOnDate(MyDate.getMyDateInstance(trainLiveStatus.getMinDate())))
                        calendars.add(trainLiveStatus.getMinDate());
                    MyDate startDate = MyDate.getMyDateInstance(trainLiveStatus.getMinDate());
                    MyDate endDate = MyDate.getMyDateInstance(today);
                    MyDate tempDate = new MyDate(startDate);
                    while (!tempDate.equals(endDate)) {
                        if (trainLiveStatus.getTrain().runsOnDate(tempDate)) {
                            System.out.println("Train runs on " + tempDate);
                            calendars.add(MyDate.getCalendarInstance(tempDate));
                        }
                        tempDate.increment(1);
                    }
                    if (trainLiveStatus.getTrain().runsOnDate(endDate))
                        calendars.add(MyDate.getCalendarInstance(endDate));
                    Calendar[] calendarArray = new Calendar[calendars.size()];
                    for (int i = 0; i < calendarArray.length; ++i) {
                        calendarArray[i] = calendars.get(i);
                    }
                    dialog.setSelectableDays(calendarArray);
                    dialog.show(getSupportFragmentManager(), "Select Date");
                }

            }
        });

        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                MyDate date = new MyDate(dayOfMonth, monthOfYear + 1, year);
                System.out.println("FOR DATE: " + date.getBeautifiedDate());
                new GetLiveTrainStatus().execute(date);
            }
        });


    }


    private void showSnack(String text, String listenerText, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_running_status_home), text, Snackbar.LENGTH_SHORT);
        if (listener != null) {
            snackbar.setAction(listenerText, listener);
        }
        snackbar.show();
    }

    private void fillTrainInfo() {
        SmartAnimator.circularRevealView(hiddenDateSelLayout, 250, SmartAnimator.What.OPEN, null);
        fromStnCode.setText(selectedTrain.getSource().getCode());
        toStnCode.setText(selectedTrain.getDestination().getCode());
        fromStnFull.setText(selectedTrain.getSource().getName());
        toStnFull.setText(selectedTrain.getDestination().getName());
    }


    private class GetLiveTrainStatus extends AsyncTask<MyDate, Void, Void> {

        @Override
        protected void onPreExecute() {
            status = true;
            pd = new ProgressDialog(LiveTrainStatusHome.this);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected Void doInBackground(MyDate... date) {
            try {
                if (trainLiveStatus.fetchNow(date[0])) {
                    //System.out.println(trainLiveStatus);
                } else {
                    //status=false;
                }
            } catch (IOException E) {
                status = false;
                E.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            System.out.println(trainLiveStatus);
            Intent in = new Intent(LiveTrainStatusHome.this, ShowTrainLiveStatus.class);
            if (status) {
                in.putExtra("data", trainLiveStatus);
                startActivity(in);
            } else {
                Toast.makeText(LiveTrainStatusHome.this, "Connection Failure. Please try again", Toast.LENGTH_LONG).show();

            }

        }

        ProgressDialog pd;
        boolean status;
    }

    private class GetTrainInfo extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            hiddenDateSelLayout.setVisibility(View.INVISIBLE);
            pd = new ProgressDialog(LiveTrainStatusHome.this);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                selectedTrain.getNo();
            } catch (Exception E) {
                asyncStatus = 3;
                return null;
            }

            try {
                trainLiveStatus = new TrainLiveStatus(selectedTrain);
                asyncStatus = 1;
            } catch (IOException e) {
                asyncStatus = 2;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void obj) {
            pd.dismiss();
            if (asyncStatus == 1) {
                fillTrainInfo();
            } else if (asyncStatus == 2) {
                showSnack("Connection Failed", "RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new GetTrainInfo().execute();
                    }
                });
            } else {
                showSnack("Please select Train from dropdown", "", null);
            }
        }

        ProgressDialog pd;
        int asyncStatus = 1;
            /*
                1 = OK
                2 = Connection Failed
                3 = Train null
             */
    }


    ArrayList<Train> trains;
    Button dateSelBtn;
    ImageButton cross;
    Train selectedTrain;
    ProgressBar pb;
    DatePickerDialog dialog;
    TrainLiveStatus trainLiveStatus;
    TextView fromStnCode, toStnCode, fromStnFull, toStnFull;
    DelayedAutoCompleteTextView trainSelectTextView;
    LinearLayout hiddenDateSelLayout;
}
