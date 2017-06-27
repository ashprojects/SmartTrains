package jpro.smarttrains.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;

import SmartTrainTools.MyDate;
import SmartTrainTools.Station;
import SmartTrainTools.Train;
import comparator.train.ArrivalTimeComparator;
import comparator.train.TravelTimeComparator;
import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator;
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
        System.out.println("found trains " + trains.size());
        recyclerView = (RecyclerView) findViewById(R.id.allTrainsList);
        sortBySpinner = (Spinner) findViewById(R.id.train_between_sort_by_spinner);
        f = (TextView) findViewById(R.id.stnStartTBS);
        t = (TextView) findViewById(R.id.stnEndTBS);
        fromStn = (Station) getIntent().getSerializableExtra("from");
        toStn = (Station) getIntent().getSerializableExtra("to");
        date = (MyDate) getIntent().getSerializableExtra("date");
        listAdapter = new ListAdapterTrainBetweenStation(TrainBetweenStations.this, trains, fromStn, toStn, date);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new FlipInBottomXAnimator());
        recyclerView.getItemAnimator().setChangeDuration(1000);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        recyclerView.setAdapter(listAdapter);
        f.setText(fromStn.getName());
        t.setText(toStn.getName());
        recyclerView.addOnItemTouchListener(new OnItemTouchListener(getApplicationContext(), recyclerView));


        String[] sortMethods = new String[]{"Arrival Time", "Travel Time"};
        sortSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.small_spinner_item, sortMethods);
        sortBySpinner.setAdapter(sortSpinnerAdapter);
        sortBySpinner.setSelection(0);

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Comparator<Train> comparator = null;
                switch (sortSpinnerAdapter.getItem(position)) {
                    case "Arrival Time":
                        comparator = new ArrivalTimeComparator();
                        break;
                    case "Travel Time":
                        comparator = new TravelTimeComparator();
                        break;
                    default:
                        System.out.println("__ REACHED DEFAULT");
                }
                //Collections.sort(trains, comparator);
                //listAdapter.update(trains);
                listAdapter.update(comparator);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    class OnItemTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;

        public OnItemTouchListener(Context context, final RecyclerView recyclerView) {
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && gestureDetector.onTouchEvent(e)) {
                Train train = (Train) listAdapter.getItem(rv.getChildAdapterPosition(child));
                new loadTrainInfo().execute(train);
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    RecyclerView recyclerView;
    LinearLayout checkBoxLL;
    CheckBox checkBox;
    Station fromStn, toStn;
    ArrayList<Train> trains;
    MyDate date;
    TextView f, t;
    Button shufll_btn;
    Spinner sortBySpinner;
    ArrayAdapter<String> sortSpinnerAdapter;
    ListAdapterTrainBetweenStation listAdapter;
}
