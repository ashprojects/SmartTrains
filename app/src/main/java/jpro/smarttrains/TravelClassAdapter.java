package jpro.smarttrains;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import SmartTrainTools.TravelClass;
import jpro.smarttrains.R;

public class TravelClassAdapter<T> extends ArrayAdapter<TravelClass> {

    Context context;
    int layoutResourceId;
    ArrayList<TravelClass> data = null;

    public TravelClassAdapter(Context context, int layoutResourceId,int txt ,ArrayList<TravelClass> data) {
        super(context, layoutResourceId,txt, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(R.layout.def_spinner, parent, false);
            code=(TextView)row.findViewById(R.id.txtRed);
            full=(TextView)row.findViewById(R.id.txtContent);
        code.setText(data.get(position).getClassCode());
        full.setText(data.get(position).getClassFull());

        return row;
    }

    public void update(ArrayList<TravelClass> n){
        this.data.clear();
        this.data.addAll(n);
        notifyDataSetChanged();
    }

    @Override

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(R.layout.def_spinner, parent, false);
        code=(TextView)row.findViewById(R.id.txtRed);
        full=(TextView)row.findViewById(R.id.txtContent);
        code.setText(data.get(position).getClassCode());
        full.setText(data.get(position).getClassFull());

        return row;
    }

        TextView code,full;

}