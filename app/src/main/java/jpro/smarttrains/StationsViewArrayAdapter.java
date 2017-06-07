package jpro.smarttrains;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import static android.R.attr.label;

/**
 * Created by root on 31/3/17.
 */

public class StationsViewArrayAdapter extends ArrayAdapter {
    public StationsViewArrayAdapter(Context context, int resource, int textViewResourceId, String[] stns) {
        super(context, resource, textViewResourceId, stns);
        this.context=context;
        this.stns=stns;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }



    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.def_spinner, parent, false);
        }

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView red=(TextView)row.findViewById(R.id.txtRed);
        TextView ct=(TextView)row.findViewById(R.id.txtContent);

        red.setText(getItem(position).toString().split(" - ")[1]);
        ct.setText(getItem(position).toString().split(" - ")[0]);


        return row;
    }
    Context context;
    String stns[];

}
