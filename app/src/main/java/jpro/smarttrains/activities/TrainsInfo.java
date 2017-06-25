package jpro.smarttrains.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import SmartTrainTools.Station;
import SmartTrainTools.Train;
import jpro.smarttrains.R;

public class TrainsInfo extends AppCompatActivity {


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
        setContentView(R.layout.activity_trains_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ll=(LinearLayout)findViewById(R.id.runsOnLL);
        from=(TextView)findViewById(R.id.from);
        routeBtn=(Button)findViewById(R.id.trRouteBtn);
        to=(TextView)findViewById(R.id.to);
        seatBtn=(Button)findViewById(R.id.trSeatAvaiBtn);
        T=(Train)getIntent().getSerializableExtra("train");
        try {
            T.getSource();
        } catch (Exception E) {

        }
        System.out.println(T);
        int[] rn=T.getRunsOn();
        from.setText(T.getSource().getName()+" - "+T.getSource().getCode());
        to.setText(T.getDestination().getName()+" - "+T.getDestination().getCode());
        setTitle(T.getNoAsString()+" "+T.getName());
        String s="SMTWTFS";
        float scale = getResources().getDisplayMetrics().density;
        for(int i=0;i<rn.length;++i){
            TextView tx=new TextView(this);
            LinearLayout.LayoutParams lpr=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tx.setLayoutParams(lpr);
            if(Build.VERSION.SDK_INT>16)
                lpr.setMarginStart((int) (2*scale + 0.5f));
            tx.setText(""+s.charAt(i));
            tx.setTypeface(null, Typeface.BOLD);

            int dpAsPixels = (int) (3*scale + 0.5f);
            int lr=dpAsPixels;
            if((s.charAt(i)=='T')||(s.charAt(i)=='F')||(s.charAt(i)=='S')){
                lr=(int) (5*scale + 0.5f);
            }
            tx.setPadding(lr,dpAsPixels,lr,dpAsPixels);
            tx.setTextColor(Color.parseColor("#ffffff"));
            System.out.println("for:"+s.charAt(i)+tx.getPaddingLeft()+"-"+tx.getPaddingRight());
            if(rn[i]==1)
                tx.setBackgroundResource(R.drawable.bg_green_oval);
            else
                tx.setBackgroundResource(R.drawable.bg_red_oval);
            ll.addView(tx);

        }
        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(getApplicationContext(),TrainRoute.class);
                in.putExtra("train",T);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(in, ActivityOptions.makeSceneTransitionAnimation(TrainsInfo.this).toBundle());
                else
                    startActivity(in);
            }
        });

        seatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(getApplicationContext(),SeatAvailabilitySimple.class);
                in.putExtra("train",T);
                in.putExtra("stn2",(Station)getIntent().getSerializableExtra("stn2"));
                in.putExtra("stn1",(Station)getIntent().getSerializableExtra("stn1"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(in, ActivityOptions.makeSceneTransitionAnimation(TrainsInfo.this).toBundle());
                else
                    startActivity(in);
            }
        });
    }
    LinearLayout ll;
    TextView from,to;
    Button routeBtn,seatBtn;
    Train T;
}
