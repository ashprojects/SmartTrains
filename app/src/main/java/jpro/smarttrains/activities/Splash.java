package jpro.smarttrains.activities;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;

import Downloader.DownloadAndInstallAPKFile;
import SmartTrainTools.SmartTools;
import commons.Config;
import jpro.smarttrains.R;
import jpro.smarttrains.services.TrackPNR;

public class Splash extends AppCompatActivity {


    protected class CheckForUpdate extends AsyncTask<Object, Void, Void> {
        boolean status=false;
        String changelog="";
        ProgressDialog pd;
        String link=null;
        ArrayList<String> tips;
        String welcomeMsg=null;
        protected Void doInBackground(Object... arg0) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String currVersion = pInfo.versionName;
                Config.verDesc = "Version: " + currVersion;
                SmartTools.fixEtvVer();
                //System.out.println("ETV SET TO: "+Config.etV);
                System.out.println("ST: SERVER_WAIT");
                tips=new ArrayList<>();
                Document d = Jsoup.connect(getString(R.string.jsonLink)).ignoreContentType(true).timeout(10000).get();

                JSONParser parser=new JSONParser();

                JSONObject jRootObj=(JSONObject)parser.parse(d.text());
                JSONObject o=(JSONObject)parser.parse(jRootObj.get("AppMeta").toString());
                String v=jRootObj.get("AppVersion").toString();
                Config.rq_desc = jRootObj.get("AppDesc").toString();
                JSONArray tiparray=(JSONArray) jRootObj.get("AppTips");
                for(int i=0;i<tiparray.size();++i){
                    tips.add(tiparray.get(i).toString());
                }
                //System.out.println("****************** ETV: "+o.get("etV"));
               /* if(!Config.etV.equals(o.get("etV")))
                    Config.etV=o.get("etV").toString();
               */
                if (!Config.qCacheUrl.equals(jRootObj.get("AppCacheSrvLink")))
                    Config.qCacheUrl = jRootObj.get("AppCacheSrvLink").toString();
                System.out.println("ST: SERV_RSP: "+ Arrays.asList(v));

                //System.out.println("......*********** CUrrVers: "+currVersion);
                welcomeMsg=jRootObj.get("AppWelcomeMsg").toString();
                if(Float.parseFloat(v)>Float.parseFloat(currVersion)){
                    System.out.println("...*********** INSIDE");
                    link=jRootObj.get("AppLink").toString();
                    status=true;
                    changelog=jRootObj.get("AppChangeLog").toString();
                    Config.update_Link = link;
                    Config.changelog = changelog;
                    Config.verDesc = "Version: " + currVersion + " (v" + jRootObj.get("AppVersion").toString() + ", Click here)";
                } else {

                }
            } catch (Exception E){
                E.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(status){
                makeToast("Something new is on the way. Please update");
                new AlertDialog.Builder(Splash.this).setTitle("Update Available")
                        .setMessage("Smart Trains has just got better. Update to the newest version? It won't take much.\nChangeLog: "+changelog)
                        .setCancelable(false)
                        .setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                System.out.println("----------------------- switch --------------------");
                                switch(id){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        /*Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(link));
                                        startActivity(i);
                                        */
                                        UpdateNow(link);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        Intent in = new Intent(Splash.this, MainHome.class);
                                        in.putExtra("welcomeMsg",welcomeMsg);
                                        in.putExtra("tips",tips);
                                        startActivity(in);
                                }
                                                            }
                        })
                        .setNegativeButton("LATER", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                System.out.println("----------------------- switch --------------------");
                                switch(id){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(link));
                                        startActivity(i);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        Intent in = new Intent(Splash.this, MainHome.class);
                                        in.putExtra("welcomeMsg",welcomeMsg);
                                        in.putExtra("tips",tips);
                                        startActivity(in);
                                }
                            }
                        })
                        .show();
            } else {
                Intent in = new Intent(Splash.this, MainHome.class);
                in.putExtra("welcomeMsg",welcomeMsg);
                in.putExtra("tips",tips);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivity(in, ActivityOptions.makeSceneTransitionAnimation(Splash.this).toBundle());
                else
                    startActivity(in);
            }

        }
    }

    private void UpdateNow(String link) {
        ProgressDialog pd = new ProgressDialog(Splash.this);
        pd.setMessage("This may take a while\nOnce the file is downloaded, you'll be asked to Install the app.");
        pd.setTitle("Dowloading Update");
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        DownloadAndInstallAPKFile downloadAndInstallAPKFile = new DownloadAndInstallAPKFile();
        downloadAndInstallAPKFile.setContext(getApplicationContext(), pd);
        downloadAndInstallAPKFile.execute(link);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        //window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_splash);
        makeToast("Connecting to Server...");
        System.out.println("Update Checker started");
        StartAnimations();
        new CheckForUpdate().execute();
        startService(new Intent(this, TrackPNR.class));
    }
    private void StartAnimations() {
        System.out.println("Update Checker started");
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);

    }


    private void makeToast(String text){
        Toast.makeText(Splash.this,text, Toast.LENGTH_LONG).show();
    }



}
