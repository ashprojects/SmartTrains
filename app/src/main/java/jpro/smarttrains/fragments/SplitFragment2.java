package jpro.smarttrains.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import SmartTrainTools.SplitJourney;
import SmartTrainTools.Train;
import jpro.smarttrains.R;
import jpro.smarttrains.activities.TrainsInfo;
import jpro.smarttrains.adapters.ListAdapterTrainBetweenStation;
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
        this.end = getArguments().getInt("end");
        System.out.println("CREATED FRAG1");
        View rootView = inflater.inflate(R.layout.frag_alltrains, container, false);
        System.out.println("%$%$TRAIN SET:" + splitJourney.getTrainSet1());
        //listAdapter=new TrainsViewListAdapter(this.getContext(),new ArrayList<>(splitJourney.getTrainSet1().subList(0,end)),splitJourney.getSrcStn(),splitJourney.getMidStn(),splitJourney.getDestStn());
        listAdapterTrainBetweenStation = new ListAdapterTrainBetweenStation(this.getContext(), splitJourney.getTrainSet2(), splitJourney.getMidStn(), splitJourney.getDestStn(), null);


        listView = (RecyclerView) rootView.findViewById(R.id.allTrainsList);
        listView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        listView.setAdapter(listAdapterTrainBetweenStation);
        listView.addOnItemTouchListener(new SplitFragment2.OnItemTouchListener(getContext(), listView));
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(splitJourney.getTrainSet1().get(i).getName());
                Intent in=new Intent(getActivity().getApplicationContext(),TrainsInfo.class);
                in.putExtra("train",splitJourney.getTrainSet1().get(i));
                in.putExtra("stn1",splitJourney.getTrainSet1().get(i).getQuerySrcStn());
                in.putExtra("stn2",splitJourney.getTrainSet1().get(i).getQueryDestStn());
                startActivityForResult(in,0);

            }
        });*/
        listViewProgressBar=(LinearLayout) rootView.findViewById(R.id.prog_AlltrainsView);
        listViewProgressBar.setVisibility(View.GONE);


        ((TextView) rootView.findViewById(R.id.tot_train_no)).setText("Total Trains: " + splitJourney.getTrainSet2().size());
        return rootView;
    }

    private class loadTrainInfo extends AsyncTask<Train, Void, Train> {
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getContext());
            pd.setMessage("Gathering info...");
            pd.show();

        }

        @Override
        protected Train doInBackground(Train... T) {
            try {
                T[0].fetchInfo_ETrain();
                success = true;
            } catch (Exception E) {
                E.printStackTrace();

            }
            return T[0];
        }

        @Override
        protected void onPostExecute(Train train) {
            pd.hide();
            if (success) {
                Intent in = new Intent(getContext(), TrainsInfo.class);
                in.putExtra("train", train);
                in.putExtra("stn1", splitJourney.getSrcStn());
                in.putExtra("stn2", splitJourney.getMidStn());
                startActivity(in);
            } else {
                Toast.makeText(getContext(), "Something went wrong. Please try again", Toast.LENGTH_SHORT);
            }
        }

        boolean success = false;
        ProgressDialog pd;
    }

    class OnItemTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;

        public OnItemTouchListener(Context context, final RecyclerView recyclerView) {
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && gestureDetector.onTouchEvent(e)) {
                Train train = (Train) listAdapterTrainBetweenStation.getItem(rv.getChildAdapterPosition(child));
                new SplitFragment2.loadTrainInfo().execute(train);
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    SplitJourney splitJourney;
    int st=2,end=20;
    LinearLayout listViewProgressBar;
    RecyclerView listView;
    ListAdapterTrainBetweenStation listAdapterTrainBetweenStation;
    TrainsViewListAdapter listAdapter;
}
