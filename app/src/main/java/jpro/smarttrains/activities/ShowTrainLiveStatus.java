package jpro.smarttrains.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import SmartTrainTools.TrainLiveStatus;
import Utilities.SmartAnimator;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.LiveStatusFillListAdapter;

public class ShowTrainLiveStatus extends AppCompatActivity {


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void initAnimations() {
        SmartAnimator.addActivityTransition(getWindow(), SmartAnimator.Type.EXPLODE, 200);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_train_live_status);
        setTitle("Live Train Status");
        trainLiveStatus = (TrainLiveStatus) getIntent().getSerializableExtra("data");
        System.out.println("##-- " + trainLiveStatus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.listView_Live_Train_Status);
        liveStatusItems = trainLiveStatus.getLiveStatusItems();
        if (liveStatusItems.size() < 1) {
            onBackPressed();
            finish();
            return;
        }
        lastDesc = (TextView) findViewById(R.id.live_status_desc);
        fab_refresh = (FloatingActionButton) findViewById(R.id.live_status_refresh_status);
        msgTextView = (TextView) findViewById(R.id.live_status_item_msg);
        liveStatusFillListAdapter = new LiveStatusFillListAdapter(ShowTrainLiveStatus.this, R.layout.live_status_item, liveStatusItems, trainLiveStatus.findLastArrivedStation());
        listView.setAdapter(liveStatusFillListAdapter);
        String trinfo = "";
        trinfo += trainLiveStatus.getTrain().getName() + " (" + trainLiveStatus.getTrain().getNo() + ")\n";
        trinfo += trainLiveStatus.getLiveStatusItems().get(0).getSchDeptDate().getBeautifiedDate();
        msgTextView.setText(trinfo);
        msgTextView.setTextColor(Color.WHITE);
        String msg = trainLiveStatus.getMsg();
        final TrainLiveStatus.LiveStatusItem lv = trainLiveStatus.recentLiveStatusItem();

        if (msg.contains("not started")) {
            lastDesc.setText("TRAIN IS YET TO START FROM SOURCE");
            lastDesc.setTextColor(Color.parseColor("#366F26"));
        } else if (msg.contains("destination")) {
            lastDesc.setText("Arrived " + trainLiveStatus.getTrain().getDestination() + " at " + liveStatusItems.get(liveStatusItems.size() - 1).getActArr());
        } else {

            String delayMsg = "";
            if (lv.getArrDelay() < 0) {
                delayMsg = " (Delayed by " + (-lv.getArrDelay()) + " mins)";
                lastDesc.setTextColor(Color.RED);
            } else if (lv.getArrDelay() > 0) {
                delayMsg = " (Early by " + (lv.getArrDelay()) + " mins)";
            } else {
                delayMsg = "";
            }
            lastDesc.setText("Arrived " + lv.getStn().getName() + " at " + lv.getActArr() + "" +
                    delayMsg
            );

        }
        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RefreshStatus().execute();
            }
        });
        System.out.println("SMOOTH SCROLL POSITION: " + liveStatusFillListAdapter.getPosition(lv));

        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPosition(liveStatusFillListAdapter.getPosition(lv));
            }
        });
        initAnimations();
    }


    private class RefreshStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(ShowTrainLiveStatus.this);
            pd.setMessage("Refreshing...");
            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            liveStatusItems = trainLiveStatus.getLiveStatusItems();
            liveStatusFillListAdapter.notifyDataSetChanged();
            pd.dismiss();
            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.smoothScrollToPosition(liveStatusFillListAdapter.getPosition(trainLiveStatus.recentLiveStatusItem()));
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                trainLiveStatus.refreshNow();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        ProgressDialog pd;
    }

    ListView listView;
    TrainLiveStatus trainLiveStatus;
    TextView msgTextView, lastDesc;
    FloatingActionButton fab_refresh;
    LiveStatusFillListAdapter liveStatusFillListAdapter;
    ArrayList<TrainLiveStatus.LiveStatusItem> liveStatusItems;
}
