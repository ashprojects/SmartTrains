package jpro.smarttrains;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import SmartTrainTools.Train;
import SmartTrainsSQL.DatabaseHandler;
import SmartTrainsSQL.TrainBean;

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
        db=new DatabaseHandler(this);

        offlistview=(ListView)findViewById(R.id.offline_listview);
        trainBeanArrayList=new ArrayList<>();
        clearimg=(ImageView)findViewById(R.id.clearAllImg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        trainBeanArrayList.addAll(db.getSavedRoutesBeans());
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
                Train x= db.getTrainOffline(tb);
                in.putExtra("train",x);
                in.putExtra("isAvailableOffline",true);
                pd.hide();
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
                            db.deleteAllTrainRoutes();
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
    DatabaseHandler db;
}
