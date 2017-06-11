package jpro.smarttrains.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import jpro.smarttrains.R;

public class QuotaSpinnerAdapter<T> extends ArrayAdapter<String> {

    Context context;
    int layoutResourceId;
    ArrayList<String> data = null;

    public QuotaSpinnerAdapter(Context context, int layoutResourceId,int txt ,ArrayList<String> data) {
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
        code.setText(data.get(position).split(" ")[0]);
        full.setText(data.get(position).split(" ")[1]);
        return row;
    }

    @Override

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(R.layout.def_spinner, parent, false);
        code=(TextView)row.findViewById(R.id.txtRed);
        full=(TextView)row.findViewById(R.id.txtContent);
        code.setText(data.get(position).split(" ")[0]);
        full.setText(data.get(position).split(" ")[1]);

        return row;
    }

    TextView code,full;

}