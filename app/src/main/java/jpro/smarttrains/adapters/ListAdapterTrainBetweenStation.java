package jpro.smarttrains.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import SmartTrainTools.MyDate;
import SmartTrainTools.Station;
import SmartTrainTools.Train;
import SmartTrainTools.TravelClass;
import jpro.smarttrains.R;

/**
 * Created by root on 8/6/17.
 */

public class ListAdapterTrainBetweenStation extends RecyclerView.Adapter<ListAdapterTrainBetweenStation.TrainViewHolder> {
    private int lastAnimatedPos = 0;

    public ListAdapterTrainBetweenStation(Context context, ArrayList<Train> trains, Station start, Station end, MyDate date) {
        this.trains = new ArrayList<>(trains);
        this.src = start;
        this.dest = end;
        this.mInflater = LayoutInflater.from(context);
        this.date = date;
        this.context = context;
    }

    public void update(ArrayList<Train> newTrains) {
        int size = trains.size();
        this.trains.clear();
        notifyItemRangeRemoved(0, size);
        this.trains = new ArrayList<>(newTrains);
        notifyItemRangeInserted(0, trains.size());
    }

    public void update(Comparator<Train> comparator) {
        //this.trains.sort(comparator);
        prevTrains = new ArrayList<>(this.trains);
        Collections.sort(this.trains, comparator);
        notifyItemRangeChanged(0, trains.size());
    }

    @Override
    public int getItemCount() {
        return this.trains == null ? 0 : this.trains.size();
    }

    public Object getItem(int i) {
        return trains.get(i);
    }

    @Override
    public long getItemId(int i) {
        return ((Train) getItem(i)).getNo().hashCode();
    }

    @Override
    public TrainViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        TrainViewHolder holder;

        View view = mInflater.inflate(R.layout.default_train_list, viewGroup, false);

        holder = new TrainViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(TrainViewHolder holder, int i) {
        try {
            //holder.pos.setText(""+i);
            //System.out.println(holder);
            holder.trNo.setText("" + trains.get(i).getNoAsString());
            holder.trName.setText(trains.get(i).getName());
            String deptime = trains.get(i).getDepartureTimeof(trains.get(i).getQuerySrcStn());
            if (deptime == null)
                deptime = trains.get(i).getQuerySrcTime().toString();
            String arrtime = trains.get(i).getArrivalTimeof(trains.get(i).getQueryDestStn());
            if (arrtime == null)
                arrtime = trains.get(i).getQueryDestTime().toString();
            holder.timeSrc.setText(deptime);
            holder.timeDest.setText(arrtime);
            holder.item = getItem(i);
        } catch (NullPointerException E) {
            E.printStackTrace();
        }

        holder.stn1.setText(trains.get(i).getQuerySrcStn().getName() + "-" + trains.get(i).getQuerySrcStn().getCode());
        holder.stn2.setText(trains.get(i).getQueryDestStn().getName() + "-" + trains.get(i).getQueryDestStn().getCode());

        for (int w = 0; w < trains.get(i).getRunsOn().length; ++w) {
            int q = trains.get(i).getRunsOn()[w];
            if (q == 1) {
                holder.tr[w].setTextColor(Color.parseColor("#565454"));
            } else
                holder.tr[w].setTextColor(Color.parseColor("#ECECEC"));

        }
        String tmp = "";
        holder.classLayout.removeAllViews();
        for (TravelClass A : TravelClass.getTravelClassesFrom(trains.get(i).getClassAvail())) {
            TextView tx = new TextView(context);
            tx.setBackgroundResource(R.drawable.bg_rect);
            tx.setPadding(2, 2, 2, 2);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(2, 0, 2, 0);
            tx.setLayoutParams(lp);
            tx.setText(A.getClassCode());

            holder.classLayout.addView(tx);

        }
        //holder.clss.setText(tmp);
        holder.ttm.setText(trains.get(i).getQueryTravelTime().split(":")[0] + " hrs " + trains.get(i).getQueryTravelTime().split(":")[1] + " mins");
        //holder.runsOn.setText(a);

        if (date != null) {
            if (!trains.get(i).runsOnDate(date)) {
                holder.layoutHeader.setBackgroundColor(Color.parseColor("#a9a9a9"));
            } else {
                holder.layoutHeader.setBackgroundColor(Color.parseColor("#2b14d6"));
            }
        }
    }

    public static class TrainViewHolder extends RecyclerView.ViewHolder {
        public TrainViewHolder(View view) {
            super(view);
            TrainViewHolder holder = this;
            holder.tr = new TextView[7];
            holder.trNo = (TextView) view.findViewById(R.id.deftrNo);
            holder.trName = (TextView) view.findViewById(R.id.deftrName);
            holder.stn1 = (TextView) view.findViewById(R.id.defstn1);
            holder.pos = (TextView) view.findViewById(R.id.position);
            holder.stn2 = (TextView) view.findViewById(R.id.defstn2);
            holder.tr[0] = (TextView) view.findViewById(R.id.trSun);
            holder.tr[1] = (TextView) view.findViewById(R.id.trM);
            holder.tr[2] = (TextView) view.findViewById(R.id.trTue);
            holder.tr[3] = (TextView) view.findViewById(R.id.trW);
            holder.tr[4] = (TextView) view.findViewById(R.id.trT);
            holder.tr[5] = (TextView) view.findViewById(R.id.trF);
            holder.tr[6] = (TextView) view.findViewById(R.id.trS);
            holder.timeSrc = (TextView) view.findViewById(R.id.defdt_n_day1);
            holder.timeDest = (TextView) view.findViewById(R.id.defdt_n_day2);
            holder.ttm = (TextView) view.findViewById(R.id.travelTime);
            holder.layoutHeader = (LinearLayout) view.findViewById(R.id.trNoSpan);
            holder.classLayout = (LinearLayout) view.findViewById(R.id.classesLL);
            //holder.runsOn=(TextView)view.findViewById(R.id.runsOn);

        }

        @Override
        public String toString() {
            return "TrainViewHolder{" +
                    "runsOn=" + runsOn +
                    ", stn1=" + stn1 +
                    ", stn2=" + stn2 +
                    ", timeSrc=" + timeSrc +
                    ", timeDest=" + timeDest +
                    ", trNo=" + trNo +
                    ", trName=" + trName +
                    '}';
        }

        TextView stn1, stn2, timeSrc, timeDest, runsOn, trNo, trName, totTrNo, pos, tr[], clss, ttm;
        LinearLayout layoutHeader, classLayout;

        public Object item;
    }

    public ArrayList<Train> trains, prevTrains;
    private LayoutInflater mInflater;
    private Station src, dest, temp;
    private MyDate date = null;
    private Context context;
}
