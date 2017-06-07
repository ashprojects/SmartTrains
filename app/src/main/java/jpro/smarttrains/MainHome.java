package jpro.smarttrains;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

public class MainHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        rqdesc=(TextView)findViewById(R.id.rq_desc);
        tipSpan=(LinearLayout)findViewById(R.id.tipSpan);
        tipText=(TextView)findViewById(R.id.tipText);
        sAvaiBtn=(ImageButton)findViewById(R.id.smart_avail_start_btn);
        splitBtn=(ImageButton)findViewById(R.id.split_start_btn);
        sTrbyNameBtn=(ImageButton)findViewById(R.id.trains_by_no_btn);
        sSimpleAvail=(ImageButton)findViewById(R.id.seat_avai_simple);
        sOfflineRoutes=(ImageButton)findViewById(R.id.offlineroutes_trigger);
        sTrainBetweenStns=(ImageButton)findViewById(R.id.trains_bwstns_btn);
        verDesc=(TextView)findViewById(R.id.verDesc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "smartizeworks@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "BUG_REPORT");

                startActivity(Intent.createChooser(intent, ""));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        rqdesc.setText(Globals.rq_desc);
        String welcomeMsg="";
        if(Globals.showWelcomeBox) {
        tipText.setText(Globals.tip_of_the_day);
        Globals.showWelcomeBox=false;
            try {
                welcomeMsg = getIntent().getSerializableExtra("welcomeMsg").toString();
                ArrayList<String> tips=(ArrayList<String>) getIntent().getSerializableExtra("tips");
                System.out.println("TIPS: "+tips);
                System.out.println(new Date());
                int c=new Date().getDate()%6;
                System.out.println("C: "+c);
                tipOfTheDay=tips.get(c);
                tipSpan.setVisibility(View.VISIBLE);
                tipText.setText(tipOfTheDay);
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage(welcomeMsg);
                dlgAlert.setTitle("Welcome!");
                dlgAlert.setPositiveButton("OK", null);
                //dlgAlert.show();
            } catch (NullPointerException E) {
                E.printStackTrace();
                tipSpan.setVisibility(View.GONE);
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setIcon(R.drawable.wrong);
                dlgAlert.setMessage("Looks like your Internet is down. SmartTrains requires a working internet connection to function. App may crash and malfunction without connection.");
                dlgAlert.setTitle("Oops!");
                dlgAlert.setPositiveButton("OK", null);
                //dlgAlert.show();
            }
        }

        verDesc.setText(Globals.verDesc);
        verDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verDesc.getText().toString().contains("Update")){
                    new AlertDialog.Builder(MainHome.this).setTitle("Update Available")
                            .setMessage("Smart Trains has just got better. Update to the newest version? It won't take much.\nChangeLog: "+Globals.changelog)
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    System.out.println("----------------------- switch --------------------");
                                    switch(id){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(Globals.update_Link));
                                            startActivity(i);
                                            break;
                                    }
                                }
                            }).setNegativeButton("No",null).show();
                }
            }
        });

        sAvaiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sAvaiBtn.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,SmartAvailActivity.class);
                startActivity(in);
            }
        });

        sOfflineRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sOfflineRoutes.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,OfflineRoutesActivity.class);
                startActivity(in);
            }
        });

        splitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                splitBtn.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,SplitJourneyHome.class);
                startActivity(in);
            }
        });

        sTrbyNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sTrbyNameBtn.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,TrainsByNo.class);
                startActivity(in);
            }
        });

        sTrainBetweenStns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sTrainBetweenStns.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,TrainsBetweenStationActivityHome.class);
                startActivity(in);
            }
        });

        sSimpleAvail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sSimpleAvail.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,SeatAvailabilitySimple.class);
                startActivity(in);
            }
        });


    }




    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the SmartAvailActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent in=null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (item.getItemId()){
            case R.id.nav_smAvail: in=new Intent(MainHome.this,SmartAvailActivity.class);
                break;
            case R.id.nav_smSplit: in=new Intent(MainHome.this,SplitJourneyHome.class);
                break;
            case R.id.nav_trByNo: in=new Intent(MainHome.this,TrainsByNo.class);
                break;
            case R.id.nav_Seat: in=new Intent(MainHome.this,SeatAvailabilitySimple.class);
                break;
            case R.id.nav_trainBwStns: in=new Intent(MainHome.this,TrainsBetweenStationActivityHome.class);
                break;
            case R.id.nav_savedOfflineRoutes:   in=new Intent(MainHome.this,OfflineRoutesActivity.class);
        }
        startActivity(in);
        return true;
    }

    ImageButton sAvaiBtn,splitBtn,sTrbyNameBtn,sSimpleAvail,sTrainBetweenStns,sOfflineRoutes;
    String tipOfTheDay="";
    LinearLayout tipSpan;
    TextView rqdesc;
    TextView tipText;
    TextView verDesc;
}
