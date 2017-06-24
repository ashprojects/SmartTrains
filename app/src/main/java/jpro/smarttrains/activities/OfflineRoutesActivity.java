package jpro.smarttrains.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import SmartTrainTools.Train;
import SmartTrainsDB.TrainBean;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_routes);
        setTitle("Saved Train Routes");

        offlistview=(ListView)findViewById(R.id.offline_listview);
        trainBeanArrayList=new ArrayList<>();
        clearimg=(ImageView)findViewById(R.id.clearAllImg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        trainBeanArrayList.addAll(SmartTrainsDB.modals.TrainRoute.objects.getSavedRoutesTrainBeans());
        adapter=new RecentTrainSearchesListAdapter(OfflineRoutesActivity.this,trainBeanArrayList);
        offlistview.setAdapter(adapter);


        offlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProgressDialog pd=new ProgressDialog(OfflineRoutesActivity.this);
                pd.setTitle("Loading...");
                pd.show();
                Intent in=new Intent(OfflineRoutesActivity.this,TrainRoute.class);
                TrainBean tb=(TrainBean) adapterView.getItemAtPosition(i);
                long stime = System.currentTimeMillis();
                Train x = SmartTrainsDB.modals.Train.objects.getTrain(tb);
                System.out.println(System.currentTimeMillis() - stime);
                in.putExtra("train",x);
                in.putExtra("isAvailableOffline",true);
                pd.hide();
                System.out.println("about to start");
                startActivity(in);
            }
        });

        clearimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trainBeanArrayList.size()!=0) {
                    new AlertDialog.Builder(OfflineRoutesActivity.this).setTitle("Delete?").setMessage("Clear all saved routes?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SmartTrainsDB.modals.TrainRoute.objects.deleteAllTrainRoutes();
                            trainBeanArrayList.clear();
                            adapter.update(trainBeanArrayList);
                        }
                    }).setNegativeButton("NO",null).show();
                }

            }
        });
    }



    ImageView clearimg,deletethis;
    ArrayList<TrainBean> trainBeanArrayList;
    ListView offlistview;

    RecentTrainSearchesListAdapter adapter;
}
