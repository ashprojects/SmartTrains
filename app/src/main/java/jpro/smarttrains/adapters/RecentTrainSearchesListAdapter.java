package jpro.smarttrains.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import SmartTrainsDB.modals.Locomotive;
import SmartTrainsDB.modals.Modal;
import jpro.smarttrains.R;

/**
 * Created by root on 27/5/17.
 */

public class RecentTrainSearchesListAdapter extends ArrayAdapter<Modal> {
    ArrayList<Modal> items = new ArrayList<>();
    Context context;
    private final int resource;

    public RecentTrainSearchesListAdapter(Context context, int resource, List<Modal> items) {
        super(context, resource, items);
        this.context = context;
        this.items = new ArrayList<>();
        this.items.addAll(items);
        this.resource = resource;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void update(List<Modal> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        RecentTrainSearchesListAdapter.ViewHolder holder;
        if(view==null){
            view = mInflater.inflate(resource, null);
            holder=new ViewHolder();
            holder.no=(TextView)view.findViewById(R.id.rctno);
            holder.name=(TextView)view.findViewById(R.id.rctname);
            holder.to=(TextView)view.findViewById(R.id.rcto);
            holder.from=(TextView)view.findViewById(R.id.rcfrom);
            holder.img = (ImageButton) view.findViewById(R.id.train_item_delete);
            view.setTag(holder);
        } else {
            holder = (RecentTrainSearchesListAdapter.ViewHolder) view.getTag();
        }
        Locomotive train = (Locomotive) items.get(i);
        holder.no.setText(train.getNo());
        holder.name.setText(train.getName());
        holder.from.setText(train.getFrom());
        holder.to.setText(train.getTo());


        final int x = i;

        // set On Click for delete it


        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setMessage("Delete this Train Search?").setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(items.get(x));
                        items.get(x).delete();
                        notifyDataSetChanged();
                    }
                }).setNegativeButton("NO", null).show();
            }
        });
        return view;
    }

    static class ViewHolder {
        TextView no,name,from,to;
        ImageButton img;
    }
    private LayoutInflater mInflater;
}
