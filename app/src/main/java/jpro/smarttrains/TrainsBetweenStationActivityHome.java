package jpro.smarttrains;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import SmartTrainTools.RailwayCodes;
import SmartTrainTools.Station;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Stations= RailwayCodes.pullStations();

        stnArrayAdapter = new StationsViewArrayAdapter(this, R.layout.def_spinner,R.id.txtContent, Stations);

        from.setAdapter(stnArrayAdapter);
        to.setAdapter(stnArrayAdapter);

        from.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fromStn=new Station(from.getText().toString().split(" - ")[0]);
                to.requestFocus();
            }
        });

        to.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toStn=new Station(to.getText().toString().split(" - ")[0]);

            }
        });

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to.clearFocus();
                /*if(fromStn!=null && toStn!=null){
                    try {
                        ArrayList<Train> trains=SmartTools.findTrains(fromStn.getCode(),toStn.getCode(),null);
                        Intent in=new Intent(TrainsBetweenStationActivityHome.this,TrainBetweenStations.class);
                        in.putExtra("trains",trains);
                        startActivity(in);
                    } catch (IOException e) {
                        System.out.println("ERROR_CONNECTING");
                    }
                }*/
                hideKeyboard();
                Toast.makeText(TrainsBetweenStationActivityHome.this,"Feature under development! Coming Soon",Toast.LENGTH_LONG).show();
            }
        });



    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
    AutoCompleteTextView from,to;
    Button showBtn;
    Station fromStn,toStn;
    String[]  Stations;
    StationsViewArrayAdapter stnArrayAdapter;

}
