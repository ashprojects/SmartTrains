package jpro.smarttrains.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import SmartTrainTools.MyDate;
import SmartTrainTools.RailwayCodes;
import SmartTrainTools.SmartTools;
import SmartTrainTools.Station;
import SmartTrainTools.Train;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.StationsViewArrayAdapter;

public class TrainsBetweenStationActivityHome extends AppCompatActivity {


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
        setContentView(R.layout.activity_trains_between_station_home);
        setTitle("Train Between Stations");
        from=(AutoCompleteTextView)findViewById(R.id.srcStn_Autocomplete);
        to=(AutoCompleteTextView)findViewById(R.id.destStn_Autocomplete);
        showBtn=(Button)findViewById(R.id.showTrains);
        dateInfo = (TextView) findViewById(R.id.dateInfoTextView);
        dateSelbtn = (Button) findViewById(R.id.dateSelBtnTrainBetweenStations);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Stations= RailwayCodes.pullStations();

        stnArrayAdapter = new StationsViewArrayAdapter(this, R.layout.def_spinner,R.id.txtContent, Stations);

        from.setAdapter(stnArrayAdapter);
        to.setAdapter(stnArrayAdapter);

        from.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fromStn = new Station(from.getText().toString().split(" - ")[1]);
                to.requestFocus();
            }
        });

        to.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toStn = new Station(to.getText().toString().split(" - ")[1]);
                hideKeyboard();
            }
        });

        dateSelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(TrainsBetweenStationActivityHome.this, onDateSetListener, mYear, mMonth, mDay);
                c.add(Calendar.DAY_OF_MONTH, 120);
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                dpd.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis() - 1000);
                dpd.show();
            }
        });

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to.clearFocus();

                if (fromStn == null || toStn == null) {
                    Snackbar.make(findViewById(R.id.activity_trains_between_station_home), "Please fill stations", Snackbar.LENGTH_LONG).show();
                } else {
                    new findTrainsAsync().execute();
                }
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            //date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            dateObj = new MyDate(dayOfMonth, monthOfYear + 1, year);
            dateInfo.setText(dateObj.getBeautifiedDate());
            //corrPng.setVisibility(View.VISIBLE);

        }
    };

    private class findTrainsAsync extends AsyncTask<Void, Void, ArrayList<Train>> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(TrainsBetweenStationActivityHome.this);
            pd.setMessage("Connecting");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Train> doInBackground(Void... voids) {
            try {
                ArrayList<Train> trains = SmartTools.findTrains(fromStn.getCode(), toStn.getCode(), null);
                return trains;
            } catch (IOException E) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Train> trains) {
            try {
                pd.hide();
                if (trains.size() == 0) {
                    throw new ArrayIndexOutOfBoundsException("Direct Trains Not Available");
                }

                Intent in = new Intent(TrainsBetweenStationActivityHome.this, TrainBetweenStations.class);
                in.putExtra("trains", trains);
                in.putExtra("from", fromStn);
                in.putExtra("to", toStn);
                in.putExtra("date", dateObj);
                startActivity(in);

            } catch (ArrayIndexOutOfBoundsException Aux) {
                new AlertDialog.Builder(TrainsBetweenStationActivityHome.this).setTitle("No Direct Trains Found")
                        .setMessage("Sorry We Couldn't find any direct trains between stations provided. Would you like to try out Split Journey?")
                        .setCancelable(true)
                        .setPositiveButton("Yes, Split my Journey", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent in = new Intent(TrainsBetweenStationActivityHome.this, SplitJourneyHome.class);
                                in.putExtra("stn1", fromStn);
                                in.putExtra("stn2", toStn);
                                in.putExtra("date", dateObj);
                                startActivity(in);
                            }
                        }).setNegativeButton("No", null).show();

            } catch (NullPointerException E) {
                Toast.makeText(TrainsBetweenStationActivityHome.this, "Connection Failure", Toast.LENGTH_SHORT);
            }

        }

        ProgressDialog pd;
    }
    AutoCompleteTextView from,to;
    Button showBtn, dateSelbtn;
    Station fromStn,toStn;
    String[]  Stations;
    MyDate dateObj = null;
    TextView dateInfo;
    StationsViewArrayAdapter stnArrayAdapter;

}
