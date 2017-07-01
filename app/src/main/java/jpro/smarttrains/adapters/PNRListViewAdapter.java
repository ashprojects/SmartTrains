package jpro.smarttrains.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

import SmartTrainTools.MyDate;
import SmartTrainsDB.modals.Modal;
import SmartTrainsDB.modals.PNR;
import SmartTrainsDB.modals.fields.DateTime;
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
        final PNR item = (PNR) getItem(position);
        System.out.println("-- FOR " + position + " item:" + item + " " + item.get(PNR.DATE_OF_JOURNEY));
        setText(view, R.id.pnr_list_item_pnrNo, "PNR No. " + item.get(PNR.PNR));
        try {
            setText(view, R.id.pnr_list_item_pnrDate, "" + MyDate.parseMyDate(item.get(PNR.DATE_OF_JOURNEY).toString(), DateTime.dateTimeFormat));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setText(view, R.id.pnr_list_item_pnrTitle, "Trip to " + item.get(PNR.TO));
        setText(view, R.id.pnr_list_item_trInfo, item.get(PNR.TRAIN_NO).toString() + " " + Config.rc.getTrainName(item.get(PNR.TRAIN_NO).toString()));

        ImageView img = (ImageView) view.findViewById(R.id.pnr_list_item_delete);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("--delete");
                new AlertDialog.Builder(getContext()).setMessage("Would you like to delete this?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        remove(item);
                        item.delete();
                    }
                }).setNegativeButton("NO", null).show();
            }
        });


        return view;
    }

    private void setText(View view, int textViewId, String text) {
        ((TextView) view.findViewById(textViewId)).setText(text);
    }
}
