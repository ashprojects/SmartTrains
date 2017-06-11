package jpro.smarttrains.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import SmartTrainTools.SplitJourney;
import SmartTrainTools.Train;
import jpro.smarttrains.R;
import jpro.smarttrains.activities.TrainsInfo;
import jpro.smarttrains.adapters.TrainsViewListAdapter;

/**
 * Created by root on 23/3/17.
 */

public class SplitFragment2 extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    public SplitFragment2() {
    }

    public static SplitFragment2 newInstance(SplitJourney j,int end){
        SplitFragment2 sfrag2=new SplitFragment2();
        Bundle args=new Bundle();
        args.putSerializable("j",j);
        args.putInt("end",end);
        sfrag2.setArguments(args);
        return sfrag2;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.splitJourney=(SplitJourney)getArguments().getSerializable("j");
        System.out.println("CREATED FRAG2");
        View rootView = inflater.inflate(R.layout.frag_alltrains, container, false);
        System.out.println("%$%$TRAIN SET:"+splitJourney.getTrainSet2());
        end=getArguments().getInt("end");
        listAdapter=new TrainsViewListAdapter(this.getContext(),new ArrayList<>(splitJourney.getTrainSet2().subList(0,end)),splitJourney.getMidStn(),splitJourney.getDestStn(),splitJourney.getSrcStn());
        listView=(ListView)rootView.findViewById(R.id.allTrainsList);
        listView.setAdapter(listAdapter);
        ((TextView)rootView.findViewById(R.id.tot_train_no)).setText("Total Trains: "+splitJourney.getTrainSet2().size());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(splitJourney.getTrainSet2().get(i).getName());
                Intent in=new Intent(getActivity().getApplicationContext(),TrainsInfo.class);
                in.putExtra("train",splitJourney.getTrainSet2().get(i));
                in.putExtra("stn1",splitJourney.getTrainSet2().get(i).getQuerySrcStn());
                in.putExtra("stn2",splitJourney.getTrainSet2().get(i).getQueryDestStn());
                startActivityForResult(in,0);

            }
        });
        listViewProgressBar=(LinearLayout) rootView.findViewById(R.id.prog_AlltrainsView);
        listViewProgressBar.setVisibility(View.VISIBLE);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                System.out.println("SCROLL: "+i+" "+i1+" "+i2);
                if (listView.getLastVisiblePosition() == (listAdapter.getCount() - 1)&&(listView.getCount()!=end-1)){
                    System.out.println("WORKING>>>");
                    new Work().execute();
                }
            }
        });
        return rootView;
    }


    private class Work extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            snack= Snackbar.make(getView().findViewById(R.id.frag_AllTrains),"Fetching more trains. Please wait",Snackbar.LENGTH_INDEFINITE);
            snack.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Train> tr= splitJourney.getTrainSet2();
            st=end;
            System.out.println("END:"+end+" SIZE:"+tr.size());
            if(end>=tr.size()-1)
                return null;
            if(end+5>tr.size())
                end=tr.size();
            else
                end+=5;
            action=true;
            for(int i=st;i<end;++i){
                try{
                    System.out.println("FETCHING FOR;"+tr.get(i));
                    tr.get(i).fetchInfo_ETrain();
                } catch (Exception E){
                    E.printStackTrace();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            snack.dismiss();
            if(action)
                listAdapter.update(new ArrayList<Train>(splitJourney.getTrainSet2().subList(0,end)));
            else
                listView.setOnScrollListener(null);
        }
        boolean action=false;
        ProgressDialog pd;
        Snackbar snack;
    }

    SplitJourney splitJourney;
    int st=2,end=20;
    LinearLayout listViewProgressBar;
    ListView listView;
    TrainsViewListAdapter listAdapter;
}
