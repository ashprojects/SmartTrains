package jpro.smarttrains.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import SmartTrainTools.MyDate;
import SmartTrainTools.Station;
import SmartTrainTools.Train;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.ListAdapterTrainBetweenStation;

public class TrainBetweenStations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_between_stations);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("All Trains");
        trains = (ArrayList<Train>) getIntent().getSerializableExtra("trains");
        listView = (ListView) findViewById(R.id.allTrainsList);
        f = (TextView) findViewById(R.id.stnStartTBS);
        t = (TextView) findViewById(R.id.stnEndTBS);
        fromStn = (Station) getIntent().getSerializableExtra("from");
        toStn = (Station) getIntent().getSerializableExtra("to");
        date = (MyDate) getIntent().getSerializableExtra("date");
        listAdapter = new ListAdapterTrainBetweenStation(TrainBetweenStations.this, trains, fromStn, toStn, date);
        listView.setAdapter(listAdapter);
        f.setText(fromStn.getName());
        t.setText(toStn.getName());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Train train = (Train) listAdapter.getItem(i);
                new loadTrainInfo().execute(train);
            }
        });

    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            //date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            date = new MyDate(dayOfMonth, monthOfYear + 1, year);

            //corrPng.setVisibility(View.VISIBLE);

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private class loadTrainInfo extends AsyncTask<Train, Void, Train> {
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(TrainBetweenStations.this);
            pd.setMessage("Gathering info...");
            pd.show();

        }

        @Override
        protected Train doInBackground(Train... T) {
            try {
                T[0].fetchInfo_ETrain();
                success = true;
            } catch (Exception E) {
                E.printStackTrace();

            }
            return T[0];
        }

        @Override
        protected void onPostExecute(Train train) {
            pd.hide();
            if (success) {
                Intent in = new Intent(TrainBetweenStations.this, TrainsInfo.class);
                in.putExtra("train", train);
                in.putExtra("stn1", fromStn);
                in.putExtra("stn2", toStn);
                startActivity(in);
            } else {
                Toast.makeText(TrainBetweenStations.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT);
            }
        }

        boolean success = false;
        ProgressDialog pd;
    }


    ListView listView;
    LinearLayout checkBoxLL;
    CheckBox checkBox;
    Station fromStn, toStn;
    ArrayList<Train> trains;
    MyDate date;
    TextView f, t;
    ListAdapterTrainBetweenStation listAdapter;
}
