package jpro.smarttrains.activities;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import Downloader.DownloadAndInstallAPKFile;
import Utilities.SmartAnimator;
import commons.Config;
import jpro.smarttrains.R;

public class MainHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initVariables();

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
        rqdesc.setText(Config.rq_desc);
        String welcomeMsg="";
        if (Config.showWelcomeBox) {
            tipText.setText(Config.tip_of_the_day);
            Config.showWelcomeBox = false;
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

        verDesc.setText(Config.verDesc);
        verDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verDesc.getText().toString().contains("Click")) {
                    new AlertDialog.Builder(MainHome.this).setTitle("Update Available")
                            .setMessage("Smart Trains has just got better. Update to the newest version? It won't take much.\nChangeLog: " + Config.changelog)
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    System.out.println("----------------------- switch --------------------");
                                    switch(id){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            ProgressDialog pd = new ProgressDialog(MainHome.this);
                                            pd.setMessage("This may take a while\nOnce the file is downloaded, you'll be asked to Install the app.");
                                            pd.setTitle("Dowloading Update");
                                            pd.setCancelable(false);
                                            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                            DownloadAndInstallAPKFile downloadAndInstallAPKFile = new DownloadAndInstallAPKFile();
                                            downloadAndInstallAPKFile.setContext(getApplicationContext(), pd);
                                            downloadAndInstallAPKFile.execute(Config.update_Link);
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

        pnrStatusImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pnrStatusImageButton.startAnimation(AnimationUtils.loadAnimation(MainHome.this, R.anim.wobble));
                Intent in = new Intent(MainHome.this, PnrStatusHomeActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MainHome.this).toBundle());
                else
                    startActivity(in);
            }
        });

        sOfflineRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sOfflineRoutes.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,OfflineRoutesActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MainHome.this).toBundle());
            }
        });

        splitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                splitBtn.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,SplitJourneyHome.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MainHome.this).toBundle());
                else
                    startActivity(in);
            }
        });

        sTrbyNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sTrbyNameBtn.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,TrainsByNo.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MainHome.this).toBundle());
                else
                    startActivity(in);
            }
        });

        sTrainBetweenStns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sTrainBetweenStns.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,TrainsBetweenStationActivityHome.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MainHome.this).toBundle());
                else
                    startActivity(in);
            }
        });

        sSimpleAvail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sSimpleAvail.startAnimation(AnimationUtils.loadAnimation(MainHome.this,R.anim.wobble));
                Intent in=new Intent(MainHome.this,SeatAvailabilitySimple.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MainHome.this).toBundle());
                else
                    startActivity(in);
            }
        });


        initAnimations();

    }


    private void initAnimations() {
        SmartAnimator.addActivityTransition(getWindow(), SmartAnimator.Type.EXPLODE, 250);
    }

    private void initVariables() {
        rqdesc = (TextView) findViewById(R.id.rq_desc);
        tipSpan = (LinearLayout) findViewById(R.id.tipSpan);
        tipText = (TextView) findViewById(R.id.tipText);
        sAvaiBtn = (ImageButton) findViewById(R.id.smart_avail_start_btn);
        splitBtn = (ImageButton) findViewById(R.id.split_start_btn);
        sTrbyNameBtn = (ImageButton) findViewById(R.id.trains_by_no_btn);
        sSimpleAvail = (ImageButton) findViewById(R.id.seat_avai_simple);
        sOfflineRoutes = (ImageButton) findViewById(R.id.offlineroutes_trigger);
        sTrainBetweenStns = (ImageButton) findViewById(R.id.trains_bwstns_btn);
        pnrStatusImageButton = (ImageButton) findViewById(R.id.pnr_status_launch_btn);
        verDesc = (TextView) findViewById(R.id.verDesc);
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
                break;
            case R.id.nav_about:
                in = new Intent(MainHome.this, About.class);
                break;
            case R.id.nav_pnrStatus:
                in = new Intent(MainHome.this, PnrStatusHomeActivity.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MainHome.this).toBundle());
        else
            startActivity(in);
        return true;
    }

    ImageButton sAvaiBtn, splitBtn, sTrbyNameBtn, sSimpleAvail, sTrainBetweenStns, sOfflineRoutes, pnrStatusImageButton;
    String tipOfTheDay="";
    LinearLayout tipSpan;
    TextView rqdesc;
    TextView tipText;
    TextView verDesc;
}
