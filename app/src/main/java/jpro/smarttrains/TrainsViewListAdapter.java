package jpro.smarttrains;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import SmartTrainTools.Station;
import SmartTrainTools.Train;
import SmartTrainTools.TravelClass;

/**
 * Created by root on 23/3/17.
 */

public class TrainsViewListAdapter extends BaseAdapter {
    private int lastAnimatedPos=0;
    public TrainsViewListAdapter(Context context, ArrayList<Train> trains, Station start, Station end,Station t) {
        this.trains=trains;
        this.src=start;
        this.dest=end;
        this.temp=t;
        this.mInflater = LayoutInflater.from(context);
    }

    public void update(ArrayList<Train> newTrains){
        this.trains.clear();
        this.trains=newTrains;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        System.out.println("COUNT_FROM_ADAPTER: "+this.trains.size());
        return this.trains==null?0:this.trains.size();
    }

    @Override
    public Object getItem(int i) {
        return trains.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null){

            view = mInflater.inflate(R.layout.default_train_list, null);
            holder = new ViewHolder();
            holder.tr=new TextView[7];
            holder.trNo=(TextView)view.findViewById(R.id.deftrNo);
            holder.trName=(TextView)view.findViewById(R.id.deftrName);
            holder.stn1=(TextView)view.findViewById(R.id.defstn1);
            holder.pos=(TextView)view.findViewById(R.id.position);
            holder.stn2=(TextView)view.findViewById(R.id.defstn2);
            holder.tr[0]=(TextView)view.findViewById(R.id.trSun);
            holder.tr[1]=(TextView)view.findViewById(R.id.trM);
            holder.tr[2]=(TextView)view.findViewById(R.id.trTue);
            holder.tr[3]=(TextView)view.findViewById(R.id.trW);
            holder.tr[4]=(TextView)view.findViewById(R.id.trT);
            holder.tr[5]=(TextView)view.findViewById(R.id.trF);
            holder.tr[6]=(TextView)view.findViewById(R.id.trS);
            holder.timeSrc=(TextView)view.findViewById(R.id.defdt_n_day1);
            holder.timeDest=(TextView)view.findViewById(R.id.defdt_n_day2);
            holder.classLayout = (LinearLayout) view.findViewById(R.id.classesLL);
            holder.ttm=(TextView)view.findViewById(R.id.travelTime);
            //holder.runsOn=(TextView)view.findViewById(R.id.runsOn);

            view.setTag(holder);
        } else {
            holder = (TrainsViewListAdapter.ViewHolder) view.getTag();
        }
            //System.out.println("FOR TRAINS: ^^^^^^: " + trains.get(i).getNo());
            try{
                //holder.pos.setText(""+i);
                //System.out.println(holder);
                holder.trNo.setText(""+trains.get(i).getNoAsString());
                if(trains.get(i).getDepartureTimeof(temp)==null)
                    holder.trName.setText(trains.get(i).getName());
                else
                    holder.trName.setText(trains.get(i).getName()+" (DIRECT)");
                String deptime=trains.get(i).getDepartureTimeof(trains.get(i).getQuerySrcStn());
                if(deptime==null)
                    deptime="OTHER LOCAL STATION";
                String arrtime=trains.get(i).getArrivalTimeof(trains.get(i).getQueryDestStn());
                if(arrtime==null)
                    arrtime="OTHER LOCAL STATION";
                holder.timeSrc.setText(deptime);
                holder.timeDest.setText(arrtime);
            } catch (NullPointerException E){
                E.printStackTrace();
                return view;
            }

            holder.stn1.setText(trains.get(i).getQuerySrcStn().getName()+"-"+trains.get(i).getQuerySrcStn().getCode());
            holder.stn2.setText(trains.get(i).getQueryDestStn().getName()+"-"+trains.get(i).getQueryDestStn().getCode());

        for(int w=0;w<trains.get(i).getRunsOn().length;++w){
                int q=trains.get(i).getRunsOn()[w];
                if(q==1){
                    holder.tr[w].setTextColor(Color.parseColor("#565454"));
                } else
                    holder.tr[w].setTextColor(Color.parseColor("#ECECEC"));

            }
        String tmp="";
        for(TravelClass A: TravelClass.getTravelClassesFrom(trains.get(i).getClassAvail())){
            tmp+=A.getClassCode()+" ";
        }
        //holder.clss.setText(tmp);
        holder.ttm.setText(trains.get(i).getQueryTravelTime().split(":")[0]+" hrs "+trains.get(i).getQueryTravelTime().split(":")[1]+" mins");
            //holder.runsOn.setText(a);
        if((i==trains.size()-1)&&(i!=lastAnimatedPos)) {
            lastAnimatedPos=i;
            Animation animation = AnimationUtils.loadAnimation(viewGroup.getContext(), R.anim.slide);
            view.startAnimation(animation);
        }
        return view;
    }

    static class ViewHolder {
        @Override
        public String toString() {
            return "ViewHolder{" +
                    "runsOn=" + runsOn +
                    ", stn1=" + stn1 +
                    ", stn2=" + stn2 +
                    ", timeSrc=" + timeSrc +
                    ", timeDest=" + timeDest +
                    ", trNo=" + trNo +
                    ", trName=" + trName +
                    '}';
        }

        LinearLayout classLayout;
        TextView stn1,stn2,timeSrc,timeDest,runsOn,trNo,trName,totTrNo,pos,tr[],clss,ttm;
    }

    private ArrayList<Train> trains;
    private LayoutInflater mInflater;
    private Station src,dest,temp;

}
