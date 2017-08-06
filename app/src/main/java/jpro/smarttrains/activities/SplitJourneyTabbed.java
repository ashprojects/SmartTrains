package jpro.smarttrains.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import SmartTrainTools.SplitJourney;
import SmartTrainTools.Train;
import jpro.smarttrains.R;
import jpro.smarttrains.fragments.SplitFragment1;
import jpro.smarttrains.fragments.SplitFragment2;

public class SplitJourneyTabbed extends AppCompatActivity {
    int GLOBAL_NUMBER_COUNT_1=10,GLOBAL_NUMBER_COUNT_2=10;;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;


    /**
     * The {@link ViewPager} that will host the section contents.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Your Journey");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_split_jouney_tabbed);

        splitJourney=(SplitJourney) getIntent().getSerializableExtra("j");
        sectionTitle1=splitJourney.getSrcStn().getName()+" - "+splitJourney.getMidStn().getName();
        sectionTitle2=splitJourney.getMidStn().getName()+" - "+splitJourney.getDestStn().getName();

        new FillTrainsInfo(this).execute();

    }

    public void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Nothing special here...", Snackbar.LENGTH_LONG)
                        .setAction("DO NOTHING", null).show();
            }
        });

        if(getIntent().getBooleanExtra("direct",false)){
            Snackbar.make(findViewById(R.id.main_content), "Direct trains from provided stations found!", Snackbar.LENGTH_LONG).show();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class FillTrainsInfo extends AsyncTask<Void,Void,Void>{
        public FillTrainsInfo(Context C){
            pd=new ProgressDialog(C);
            //pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setTitle("Please wait...");

            pd.setMessage("Fetching Info");
            //pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //pd.hide();


            System.out.println(trainsSet1);
            init();
        }

        @Override
        protected void onPreExecute() {

            trainsSet1=splitJourney.getTrainSet1();

            trainsSet2=splitJourney.getTrainSet2();
            System.out.println("FROM TABBED: TS_SIZE:"+trainsSet1.size()+" - "+trainsSet2.size());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int i=0;
            GLOBAL_NUMBER_COUNT_1=trainsSet1.size();
            if(GLOBAL_NUMBER_COUNT_1>6)
                GLOBAL_NUMBER_COUNT_1=6;
            for(Train T:trainsSet1){
                try {
                    i++;
                    //T.fetchInfo_ETrain();
                    if(i==GLOBAL_NUMBER_COUNT_1)
                        break;
                } catch (Exception e) {
                    T.setName("(NETWORK FAILURE)");
                    T.setRunsOn(new int[]{0,0,0,0,0,0,0});

                }
            }
            GLOBAL_NUMBER_COUNT_2=trainsSet2.size();
            if(GLOBAL_NUMBER_COUNT_2>6)
                GLOBAL_NUMBER_COUNT_2=6;
            i=0;
            for(Train T:trainsSet2){
                try {
                    //T.fetchInfo_ETrain();
                    i++;
                    if(i==GLOBAL_NUMBER_COUNT_2)
                        break;
                } catch (Exception e) {
                    T.setName("(NETWORK FAILURE)");
                    T.setRunsOn(new int[]{0,0,0,0,0,0,0});
                    e.printStackTrace();
                }
            }
            return null;
        }
        ProgressDialog pd;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below)

            switch (position){
                case 0:
                    System.out.println("TS_SIZE: "+splitJourney.getTrainSet1().size());
                    return SplitFragment1.newInstance(splitJourney,GLOBAL_NUMBER_COUNT_1);
                case 1: return SplitFragment2.newInstance(splitJourney,GLOBAL_NUMBER_COUNT_2);
                default:return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return sectionTitle1;
                case 1:
                    return sectionTitle2;
            }
            return null;
        }
    }
    private ViewPager mViewPager;
    String sectionTitle1,sectionTitle2;
    public SplitJourney splitJourney;
    public ArrayList<Train> trainsSet1,trainsSet2;
}
