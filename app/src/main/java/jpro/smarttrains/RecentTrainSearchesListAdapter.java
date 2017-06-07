package jpro.smarttrains;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import SmartTrainTools.Train;
import SmartTrainsSQL.TrainBean;

/**
 * Created by root on 27/5/17.
 */

public class RecentTrainSearchesListAdapter extends BaseAdapter {
    ArrayList<TrainBean> items=new ArrayList<>();
    Context context;
    public RecentTrainSearchesListAdapter(Context context,List<TrainBean> items) {
        this.context = context;
        this.items = new ArrayList<>();
        this.items.addAll(items);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void update(List<TrainBean> items){
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        RecentTrainSearchesListAdapter.ViewHolder holder;
        if(view==null){
            view = mInflater.inflate(R.layout.listitem_trains_by_no_recent,null);
            holder=new ViewHolder();
            holder.no=(TextView)view.findViewById(R.id.rctno);
            holder.name=(TextView)view.findViewById(R.id.rctname);
            holder.to=(TextView)view.findViewById(R.id.rcto);
            holder.from=(TextView)view.findViewById(R.id.rcfrom);
            view.setTag(holder);
        } else {
            holder = (RecentTrainSearchesListAdapter.ViewHolder) view.getTag();
        }
        System.out.println("FOR i:"+i);
        holder.no.setText(items.get(i).getTrno());
        holder.name.setText(items.get(i).getTrname());
        holder.from.setText(items.get(i).getFrom());
        holder.to.setText(items.get(i).getTo());

        return view;
    }

    static class ViewHolder {
        TextView no,name,from,to;

    }
    private LayoutInflater mInflater;
}
