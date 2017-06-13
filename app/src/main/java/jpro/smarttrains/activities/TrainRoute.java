package jpro.smarttrains.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import SmartTrainTools.RouteListItem;
import SmartTrainTools.SmartTools;
import SmartTrainTools.Train;
import SmartTrainsDB.DatabaseHandler;
import jpro.smarttrains.R;


public class TrainRoute extends AppCompatActivity {
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
        setContentView(R.layout.activity_train_route);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db=new DatabaseHandler(this);

        no=(TextView)findViewById(R.id.srn);

        download=(Button)findViewById(R.id.downloadRouteBtn);

        stn=(TextView)findViewById(R.id.stnName);
        arr=(TextView)findViewById(R.id.arrTime);
        dep=(TextView)findViewById(R.id.deptTime);
        day=(TextView)findViewById(R.id.dayNo);
        dis=(TextView)findViewById(R.id.dis);
        desc=(TextView)findViewById(R.id.trainDesc);
        mainTable=(TableLayout)findViewById(R.id.tableLayout);
        train=(Train)getIntent().getSerializableExtra("train");
        try{
            isAvailableOffline=getIntent().getBooleanExtra("isAvailableOffline",false);

        } catch (Exception E){

        }

        if(isAvailableOffline){
            download.setVisibility(View.GONE);
        }
        System.out.println(train.getRoute());
        ArrayList<RouteListItem> routeListItems=train.getRoute();
        setTitle(train.getNoAsString()+" "+train.getName()+" ROUTE");
        int sr=1;
        float scale = getResources().getDisplayMetrics().density;
        for(RouteListItem item:routeListItems){
            TextView[] tx=new TextView[7];
            TableRow row=new TableRow(this);
            TableLayout.LayoutParams p= new TableLayout.LayoutParams();
            p.height=TableLayout.LayoutParams.WRAP_CONTENT;
            p.width = TableLayout.LayoutParams.MATCH_PARENT;
            row.setLayoutParams(p);
            String arr="",dept="";

            row.setBackgroundResource(R.drawable.table_border);
            String code=item.getStation().getCode();
            String sname=item.getStation().getName();
            try {
                if (sname.length() > 10) {
                    //sname = sname.substring(0, 9) + "..";
                }
            } catch (NullPointerException E){}
            int day=item.getDay();
            int dis=item.getDistanceFromSource();
            String diff = null;
            diff = SmartTools.timeDifferenceInMinutes(item.getArrivalTime(), item.getDepartureTime());
            if(sr==1){
               arr="-";
                diff = "-";
            } else {
                arr=item.getArrivalTime().toString();
            }
            if(sr==routeListItems.size()){
                dept="-";
                diff = "-";
            } else {
                dept=item.getDepartureTime().toString();
            }
            String array[] = new String[]{String.valueOf(sr), sname + "\r\n(" + code + ")", arr, dept, diff, "" + dis + "\r\n kms", "" + day};
            if(sr==1){
                row.setBackgroundResource(R.drawable.table_border_bg_trstart);
                //row.setBackgroundColor(getResources().getColor(R.color.trStart));
            } else if(sr==routeListItems.size()){
                row.setBackgroundResource(R.drawable.table_border_bg_trend);
                //row.setBackgroundColor(getResources().getColor(R.color.trEnd));
            }
            float[] arr_we = new float[]{(float) 0.15, (float) 0.5, (float) 0.3, (float) 0.3, (float) 0.2, (float) 0.2, (float) 0.12};
            for(int i=0;i<7;++i){
                tx[i]=new TextView(this);
                TableRow.LayoutParams x=new TableRow.LayoutParams(i);
                x.setMargins((int)(2*scale+0.5f),(int)(2*scale+0.5f),(int)(2*scale+0.5f),(int)(2*scale+0.5f));
                x.weight = arr_we[i];
                x.height = TableLayout.LayoutParams.MATCH_PARENT;
                x.width = 0;
                tx[i].setLayoutParams(x);
                tx[i].setText(""+array[i]);
                if (i > 1) {
                    tx[i].setGravity(Gravity.CENTER_HORIZONTAL);
                }
                tx[i].setTextColor(Color.BLACK);
                tx[i].setPadding((int)(1*scale+0.5f),0,0,0);
                tx[i].setTextSize(12);
                //tx[i].setHeight(TableRow.LayoutParams.WRAP_CONTENT);
                row.addView(tx[i]);

            }
            sr++;
            mainTable.addView(row);

        }

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.addTrainRoute(train);
                System.out.println("ADDED!");
                Toast.makeText(getApplicationContext(),"Route Saved Offline!",Toast.LENGTH_SHORT).show();
                download.setEnabled(false);
            }
        });

    }
    Train train;
    DatabaseHandler db;
    TableLayout mainTable;
    Button download;
    boolean isAvailableOffline=false;
    TextView no,code,stn,arr,dep,day,dis,desc;
}
