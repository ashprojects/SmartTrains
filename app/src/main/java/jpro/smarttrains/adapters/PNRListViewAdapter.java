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

import java.util.List;

import SmartTrainsDB.modals.Modal;
import SmartTrainsDB.modals.PNR;
import commons.Config;
import jpro.smarttrains.R;

public class PNRListViewAdapter extends ArrayAdapter<Modal> {
    private final LayoutInflater mInflater;
    private final int resource;

    public PNRListViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Modal> objects) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = mInflater.inflate(resource, parent, false);
        }
        PNR item = (PNR) getItem(position);
        setText(view, R.id.pnr_list_item_pnrNo, "PNR No. " + item.get(PNR.PNR));
        setText(view, R.id.pnr_list_item_pnrDate, item.get(PNR.DATE_OF_JOURNEY).toString());
        setText(view, R.id.pnr_list_item_pnrTitle, "Trip to " + item.get(PNR.TO));
        setText(view, R.id.pnr_list_item_trInfo, item.get(PNR.TRAIN_NO).toString() + " " + Config.rc.getTrainName(item.get(PNR.TRAIN_NO).toString()));
        return view;
    }

    private void setText(View view, int textViewId, String text) {
        ((TextView) view.findViewById(textViewId)).setText(text);
    }
}
