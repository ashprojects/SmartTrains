package jpro.smarttrains.activities;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import SmartTrainTools.Train;
import SmartTrainsDB.modals.Modal;
import Utilities.SmartAnimator;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.RecentTrainSearchesListAdapter;

public class OfflineRoutesActivity extends AppCompatActivity {

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        onBackPressed();
        return true;
    }

    private void initAnimations() {
        SmartAnimator.addActivityTransition(getWindow(), SmartAnimator.Type.EXPLODE, 250);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_routes);
        setTitle("Saved Train Routes");

        offlistview=(ListView)findViewById(R.id.offline_listview);


        adapter = new RecentTrainSearchesListAdapter(OfflineRoutesActivity.this, R.layout.list_item_small_train_view, new ArrayList<Modal>(SmartTrainsDB.modals.TrainRoute.objects.getSavedRoutesTrains()));

        clearimg=(ImageView)findViewById(R.id.clearAllImg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        offlistview.setAdapter(adapter);
        offlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProgressDialog pd=new ProgressDialog(OfflineRoutesActivity.this);
                pd.setTitle("Loading...");
                pd.show();
                Intent in=new Intent(OfflineRoutesActivity.this,TrainRoute.class);
                Train x = ((SmartTrainsDB.modals.Train) adapterView.getItemAtPosition(i)).getTrain();
                in.putExtra("train",x);
                in.putExtra("isAvailableOffline",true);
                pd.hide();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(in, ActivityOptions.makeSceneTransitionAnimation(OfflineRoutesActivity.this).toBundle());
                else
                    startActivity(in);
            }
        });

        clearimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SmartTrainsDB.modals.Train.objects.all().size() != 0) {
                    new AlertDialog.Builder(OfflineRoutesActivity.this).setTitle("Delete?").setMessage("Clear all saved routes?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SmartTrainsDB.modals.TrainRoute.objects.deleteAllTrainRoutes();
                            adapter.update(SmartTrainsDB.modals.TrainRoute.objects.all());
                        }
                    }).setNegativeButton("NO",null).show();
                }

            }
        });
        initAnimations();
    }



    ImageView clearimg,deletethis;
    ListView offlistview;
    int size = 0;
    RecentTrainSearchesListAdapter adapter;
}
