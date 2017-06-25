package jpro.smarttrains.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import SmartTrainTools.PNRStatus;
import jpro.smarttrains.R;

public class PNRStatusActivity extends AppCompatActivity {


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnrstatus);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Share", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        pnrStatus = (PNRStatus) getIntent().getSerializableExtra("pnrObject");

        setTitle("To " + pnrStatus.getTo().getName());
        dateTextView = (TextView) findViewById(R.id.pnr_status_date);
        trainTextView = (TextView) findViewById(R.id.pnr_status_trname);
        classTextView = (TextView) findViewById(R.id.pnr_status_class);
        chartStatusTextView = (TextView) findViewById(R.id.pnr_status_chart_status);
        stn1CodeTextView = (TextView) findViewById(R.id.pnr_status_st1code);
        stn1NameTextView = (TextView) findViewById(R.id.pnr_status_st1name);
        stn2CodeTextview = (TextView) findViewById(R.id.pnr_status_st2code);
        stn2NameTextView = (TextView) findViewById(R.id.pnr_status_st2name);
        passengersTableLayout = (TableLayout) findViewById(R.id.pnr_status_table_layout);
        bgToolbarImage = (ImageView) findViewById(R.id.pnr_status_image);
        try {
            dateTextView.setText(pnrStatus.getDateOfJourney().getBeautifiedDate());
            trainTextView.setText(pnrStatus.getTrainNo() + " " + pnrStatus.getTrainName());
            classTextView.setText(pnrStatus.getTravelClass().getClassFull());
            chartStatusTextView.setText(pnrStatus.isChartPrepared() ? "CHART PREPARED" : "CHART NOT PREPARED");
            stn1CodeTextView.setText(pnrStatus.getBoardingPoint().getCode());
            stn2CodeTextview.setText(pnrStatus.getTo().getCode());
            stn1NameTextView.setText(pnrStatus.getBoardingPoint().getName());
            stn2NameTextView.setText(pnrStatus.getTo().getName());
            int sno = 1;
            for (PNRStatus.Passenger currPassenger : pnrStatus.getPassengers()) {
                TableRow row = new TableRow(this);
                TableLayout.LayoutParams lp = new TableLayout.LayoutParams();
                lp.bottomMargin = 10;
                row.setLayoutParams(lp);

                TextView snTextView = new TextView(this);
                snTextView.setText(String.valueOf(sno));
                TableRow.LayoutParams lpt = new TableRow.LayoutParams();
                lpt.column = 0;
                lpt.weight = (float) 0.3;
                snTextView.setTextSize(16);
                snTextView.setLayoutParams(lpt);
                row.addView(snTextView);

                TextView bkTextView = new TextView(this);
                bkTextView.setText(currPassenger.getBookingStatus());
                if (!currPassenger.getBookingStatus().contains("CNF")) {
                    bkTextView.setTextColor(Color.RED);
                } else {
                    bkTextView.setTextColor(Color.parseColor("#558000"));
                }
                TableRow.LayoutParams lpb = new TableRow.LayoutParams();
                lpb.column = 1;
                lpb.weight = (float) 0.3;

                bkTextView.setTextSize(16);
                bkTextView.setLayoutParams(lpb);
                row.addView(bkTextView);
                TextView ckTextView = new TextView(this);
                ckTextView.setText(currPassenger.getCurrentStatus());
                if (!currPassenger.getCurrentStatus().contains("CNF")) {
                    ckTextView.setTextColor(Color.RED);
                } else {
                    ckTextView.setTextColor(Color.parseColor("#558000"));
                }
                TableRow.LayoutParams lpc = new TableRow.LayoutParams();
                lpc.column = 2;
                lpc.weight = (float) 0.4;
                ckTextView.setLayoutParams(lpc);
                ckTextView.setTextSize(16);
                row.addView(ckTextView);
                sno++;
                passengersTableLayout.addView(row);
            }
        } catch (Exception E) {

        }

    }


    TextView dateTextView, trainTextView, classTextView, chartStatusTextView, stn1CodeTextView, stn2CodeTextview, stn1NameTextView, stn2NameTextView;
    TableLayout passengersTableLayout;
    PNRStatus pnrStatus;
    ImageView bgToolbarImage;
}
