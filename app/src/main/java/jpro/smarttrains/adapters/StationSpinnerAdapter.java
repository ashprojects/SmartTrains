package jpro.smarttrains.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import SmartTrainTools.Station;
import jpro.smarttrains.R;

/**
 * Created by root on 22/7/17.
 */

public class StationSpinnerAdapter extends ArrayAdapter<Station> {
    int resource;
    Context context;

    public StationSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull Station[] objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        } else {
            Station station = getItem(position);
            if (station != null) {
                TextView lblNo = (TextView) convertView.findViewById(R.id.txtRed);
                TextView lblName = (TextView) convertView.findViewById(R.id.txtContent);
                if (lblName != null)
                    lblName.setText(station.getName());
                if (lblNo != null) {
                    lblNo.setText("" + station.getCode());
                }
            }
        }
        return convertView;
    }


}
