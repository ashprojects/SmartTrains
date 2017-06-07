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

import SmartTrainTools.Train;
import SmartTrainTools.TravelClass;
import jpro.smarttrains.R;

public class TrainSpinnerAdapter<T> extends ArrayAdapter<Train> {

    Context context;
    int layoutResourceId;
    ArrayList<Train> data = null;

    public TrainSpinnerAdapter(Context context, int layoutResourceId,int txt ,ArrayList<Train> data) {
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
        code.setText(data.get(position).getNoAsString());
        full.setText(data.get(position).getName());
        return row;
    }

    @Override

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(R.layout.def_spinner, parent, false);
        code=(TextView)row.findViewById(R.id.txtRed);
        full=(TextView)row.findViewById(R.id.txtContent);
        code.setText(data.get(position).getNoAsString());
        full.setText(data.get(position).getName());

        return row;
    }

    TextView code,full;

}