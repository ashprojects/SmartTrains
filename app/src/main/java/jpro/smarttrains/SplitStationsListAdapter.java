package jpro.smarttrains;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import SmartTrainTools.SplitJourney;

/**
 * Created by root on 23/3/17.
 */

public class SplitStationsListAdapter extends BaseAdapter {
    public SplitStationsListAdapter(Context context, ArrayList<SplitJourney> results){
        this.splitJourneys =new ArrayList<>();
        if(results!=null)
            this.splitJourneys.addAll(results);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return splitJourneys.size();
    }

    @Override
    public Object getItem(int i) {
        return splitJourneys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public void update(ArrayList<SplitJourney> x){
        this.splitJourneys.clear();
        this.splitJourneys.addAll(x);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null){
            view = mInflater.inflate(R.layout.list_split_station, null);
            holder=new ViewHolder();
            holder.src=(TextView)view.findViewById(R.id.stnStart);
            holder.dest=(TextView)view.findViewById(R.id.stnEnd);
            holder.t1=(TextView)view.findViewById(R.id.nTrains1);
            holder.t2=(TextView)view.findViewById(R.id.nTrains2);
            holder.mid1=(TextView)view.findViewById(R.id.stnMid_1);
            holder.mid2=(TextView)view.findViewById(R.id.stnMid_2);
            holder.dis=(TextView)view.findViewById(R.id.splitDis);
            holder.title=(TextView)view.findViewById(R.id.viaStn);;
        } else {
            holder = (SplitStationsListAdapter.ViewHolder) view.getTag();
        }
        holder.src.setText(splitJourneys.get(i).getSrcStn().getName());
        holder.dest.setText(splitJourneys.get(i).getDestStn().getName());
        holder.mid1.setText(splitJourneys.get(i).getMidStn().getName());
        holder.mid2.setText(splitJourneys.get(i).getMidStn().getName());
        holder.title.setText("Via "+splitJourneys.get(i).getMidStn().getName());
        try{
            holder.t1.setText(splitJourneys.get(i).getTrainSet1().size()+" Trains");
            holder.t2.setText(splitJourneys.get(i).getTrainSet2().size()+" Trains");
            holder.dis.setText("Distance: "+splitJourneys.get(i).getPathLen()+" kms");

        } catch (NullPointerException E){

        }
        return view;
    }

    static class ViewHolder {
        TextView title,src,mid1,mid2,dest,t1,t2,dis;

    }
    private LayoutInflater mInflater;
    ArrayList<SplitJourney> splitJourneys;
}
