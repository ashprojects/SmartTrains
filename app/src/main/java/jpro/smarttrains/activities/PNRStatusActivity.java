package jpro.smarttrains.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import SmartTrainTools.PNRStatus;
import jpro.smarttrains.R;

public class PNRStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnrstatus);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Share", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        pnrStatus = (PNRStatus) getIntent().getSerializableExtra("pnr_status_object");
        dateTextView = (TextView) findViewById(R.id.pnr_status_date);
        trainTextView = (TextView) findViewById(R.id.pnr_status_trname);
        classTextView = (TextView) findViewById(R.id.pnr_status_class);
        chartStatusTextView = (TextView) findViewById(R.id.pnr_status_chart_status);
        stn1CodeTextView = (TextView) findViewById(R.id.pnr_status_st1code);
        stn1NameTextView = (TextView) findViewById(R.id.pnr_status_st1name);
        stn2CodeTextview = (TextView) findViewById(R.id.pnr_status_st2code);
        stn2NameTextView = (TextView) findViewById(R.id.pnr_status_st2name);
        passengersTableLayout = (TableLayout) findViewById(R.id.pnr_status_table_layout);
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
                TableRow.LayoutParams lp = new TableRow.LayoutParams();
                lp.setMargins(0, 8, 0, 0);
                row.setLayoutParams(lp);

                TextView snTextView = new TextView(this);
                snTextView.setText(String.valueOf(sno));
                TableRow.LayoutParams lpt = new TableRow.LayoutParams();
                lpt.column = sno - 1;
                lpt.weight = (float) 0.3;
                snTextView.setLayoutParams(lpt);
                row.addView(snTextView);

                TextView bkTextView = new TextView(this);
                bkTextView.setText(currPassenger.getBookingStatus());
                TableRow.LayoutParams lpb = new TableRow.LayoutParams();
                lpt.column = sno - 1;
                lpt.weight = (float) 0.3;
                bkTextView.setLayoutParams(lpt);
                row.addView(bkTextView);

                TextView ckTextView = new TextView(this);
                ckTextView.setText(currPassenger.getCurrentStatus());
                TableRow.LayoutParams lpc = new TableRow.LayoutParams();
                lpt.column = sno - 1;
                lpt.weight = (float) 0.4;
                ckTextView.setLayoutParams(lpt);
                row.addView(ckTextView);
                sno--;

                passengersTableLayout.addView(row);
            }
        } catch (Exception E) {

        }

    }

    TextView dateTextView, trainTextView, classTextView, chartStatusTextView, stn1CodeTextView, stn2CodeTextview, stn1NameTextView, stn2NameTextView;
    TableLayout passengersTableLayout;
    PNRStatus pnrStatus;
}
