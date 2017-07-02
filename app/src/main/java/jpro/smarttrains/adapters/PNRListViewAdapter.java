package jpro.smarttrains.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import SmartTrainTools.MyDate;
import SmartTrainsDB.modals.Modal;
import SmartTrainsDB.modals.PNR;
import SmartTrainsDB.modals.Passenger;
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
        setText(view, R.id.pnr_list_item_pnrNo, "PNR No. " + item.get(PNR.PNR));
        try {
            String[] d = MyDate.parseMyDate(item.get(PNR.DATE_OF_JOURNEY).toString(), DateTime.dateTimeFormat).getBeautifiedDate().split(" ");
            setText(view, R.id.pnr_list_item_pnrDate, "" + d[0] + " " + d[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setText(view, R.id.pnr_list_item_pnrTitle, "Trip to " + Config.rc.getStationName(item.get(PNR.TO).toString()));
        setText(view, R.id.pnr_list_item_trInfo, item.get(PNR.TRAIN_NO).toString() + "-" + Config.rc.getTrainName(item.get(PNR.TRAIN_NO).toString()));
        ArrayList<Passenger> passengers = item.getPassengers();
        String currStatus = "";
        for (Passenger P : passengers) {
            String curr = P.get(Passenger.CURRENT_STATUS).toString();
            if (curr.contains("CNF")) {
                currStatus += "<font color=#489323>" + curr + "</font>,";
            } else {
                currStatus += "<font color=#B82C2C>" + curr + "</font>,";
            }
        }
        currStatus = currStatus.substring(0, currStatus.length() - 1);
        setText(view, R.id.pnr_list_item_pnrShortStatus, Html.fromHtml(currStatus).toString());
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
