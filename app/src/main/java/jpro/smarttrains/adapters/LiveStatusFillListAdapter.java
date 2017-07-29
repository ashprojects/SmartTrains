package jpro.smarttrains.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import SmartTrainTools.Station;
import SmartTrainTools.TrainLiveStatus;
import jpro.smarttrains.R;

/**
 * Created by root on 22/7/17.
 */

public class LiveStatusFillListAdapter extends ArrayAdapter<TrainLiveStatus.LiveStatusItem> {
    LayoutInflater mInflater;
    Station lastStation;


    public LiveStatusFillListAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<TrainLiveStatus.LiveStatusItem> data, Station lastStation) {
        super(context, resource, data);
        this.lastStation = lastStation;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TrainLiveStatus.LiveStatusItem liveStatusItem = getItem(position);
        HolderClass holder;
        if (convertView == null) {
            holder = new HolderClass();
            convertView = mInflater.inflate(R.layout.live_status_item, parent, false);
            holder.stnName = (TextView) convertView.findViewById(R.id.live_status_item_currStn);
            holder.actArr = (TextView) convertView.findViewById(R.id.actArrTextView);
            holder.actDept = (TextView) convertView.findViewById(R.id.actDeptTextView);
            holder.schDept = (TextView) convertView.findViewById(R.id.schDeptTextView);
            holder.schArr = (TextView) convertView.findViewById(R.id.schArrTextView);
            holder.actDept = (TextView) convertView.findViewById(R.id.actDeptTextView);
            holder.imageSpot = (ImageView) convertView.findViewById(R.id.live_status_item_spot_img);
            holder.itemHeader = (TextView) convertView.findViewById(R.id.live_status_list_item_header);
            holder.delayTime = (TextView) convertView.findViewById(R.id.delayStatusInMins);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.list_item_live_status_wrapperlayout);
            convertView.setTag(holder);
        } else {
            holder = (HolderClass) convertView.getTag();
        }
        try {
            holder.stnName.setText(liveStatusItem.getStn().getName() + "\n(" + liveStatusItem.getStn().getCode() + ")");
        } catch (NullPointerException Nex) {
            Nex.printStackTrace();
            holder.stnName.setText("UNKNOWN STATION");
        }
        try {
            holder.schArr.setText("Sch Arr:\n" + liveStatusItem.getSchArr().toString() + ", " + liveStatusItem.getSchArrDate().getBeautifiedDate().substring(0, liveStatusItem.getSchArrDate().getBeautifiedDate().lastIndexOf(" ")));
        } catch (Exception E) {
            holder.schArr.setText("-");
        }
        try {
            holder.schDept.setText("Sch Dept:\n" + liveStatusItem.getSchDept().toString() + ", " + liveStatusItem.getSchDeptDate().getBeautifiedDate().substring(0, liveStatusItem.getSchDeptDate().getBeautifiedDate().lastIndexOf(" ")));
        } catch (Exception E) {
            holder.schDept.setText("-");
        }
        if (liveStatusItem.getArrDelay() < 0) {
            holder.delayTime.setText("Delayed by " + Math.abs(liveStatusItem.getArrDelay()) + " mins");
            holder.delayTime.setTextColor(Color.RED);
        } else if (liveStatusItem.getArrDelay() > 0) {
            holder.delayTime.setTextColor(Color.parseColor("#458A19"));
            holder.delayTime.setText("Running eary by " + liveStatusItem.getArrDelay() + " mins");
        } else {
            holder.delayTime.setTextColor(Color.parseColor("#458A19"));
            holder.delayTime.setText("Right on time");
        }


        try {
            holder.actArr.setText("Act Arr:\n" + liveStatusItem.getActArr().toString() + ", " + liveStatusItem.getActArrDate().getBeautifiedDate().substring(0, liveStatusItem.getActArrDate().getBeautifiedDate().lastIndexOf(" ")));
        } catch (Exception E) {
            holder.actArr.setText("-");
        }
        try {
            holder.actDept.setText("Act Dept:\n" + liveStatusItem.getActDept().toString() + ", " + liveStatusItem.getActDeptDate().getBeautifiedDate().substring(0, liveStatusItem.getActDeptDate().getBeautifiedDate().lastIndexOf(" ")));
        } catch (Exception E) {
            holder.actDept.setText("-");
        }

        if (liveStatusItem.getArrDelay() < 0)
            holder.actArr.setTextColor(Color.RED);

        if (liveStatusItem.getDeptDelay() < 0)
            holder.actDept.setTextColor(Color.RED);

        if (liveStatusItem.getArrDelay() == 0)
            holder.actArr.setTextColor(Color.BLACK);

        if (liveStatusItem.getDeptDelay() == 0)
            holder.actDept.setTextColor(Color.BLACK);


        /*if(!liveStatusItem.isHasArrived()){
            //String diff= "" +SmartTools.timeDifferenceInMinutes(liveStatusItem.getSchArr(),liveStatusItem.getActArr());
            holder.delayTime.setTextColor(Color.DKGRAY);
            holder.actArr.setTextColor(Color.DKGRAY);
            holder.actDept.setTextColor(Color.DKGRAY);
            holder.schDept.setTextColor(Color.DKGRAY);
            holder.schArr.setTextColor(Color.DKGRAY);
        } else if (liveStatusItem.getDeptDelay()>0){
            holder.delayTime.setTextColor(Color.GREEN);
        }*/
        if (liveStatusItem.isHasArrived() && !liveStatusItem.isDestination()) {
            holder.itemHeader.setText("CROSSED");
            holder.itemHeader.setBackgroundColor(Color.parseColor("#458A19"));
            holder.layout.setBackgroundColor(Color.parseColor("#F7FFF2"));
            holder.delayTime.setBackgroundColor(Color.parseColor("#F7FFF2"));
        } else {
            holder.imageSpot.setVisibility(View.GONE);
            holder.itemHeader.setText("NO UPDATE");
            holder.itemHeader.setBackgroundColor(Color.DKGRAY);
            holder.layout.setBackgroundColor(Color.WHITE);
            holder.delayTime.setBackgroundColor(Color.WHITE);
        }
        if (lastStation != null) {
            if (liveStatusItem.getStn().equals(this.lastStation)) {

                holder.itemHeader.setText("MOST RECENTLY CROSSED");
                holder.itemHeader.setTextColor(Color.WHITE);
                holder.itemHeader.setBackgroundColor(Color.parseColor("#395E22"));
                holder.imageSpot.setVisibility(View.VISIBLE);
                holder.imageSpot.setBackgroundColor(Color.parseColor("#EAFEF7"));
                holder.layout.setBackgroundColor(Color.parseColor("#EAFEF7"));
                holder.delayTime.setBackgroundColor(Color.parseColor("#EAFEF7"));
            }
        }
        System.out.println("%%% FOR: " + liveStatusItem.getStn() + " - " + lastStation + " = " + liveStatusItem.getStn().equals(lastStation));
        if (liveStatusItem.isDestination()) {
            holder.actDept.setText("Destination\n\n");
            holder.schDept.setText("Destination");
            //holder.delayTime.setText(""+liveStatusItem.getArrDelay());
            holder.delayTime.setTextColor(liveStatusItem.getArrDelay() < 0 ? Color.RED : Color.GREEN);
        }
        if (liveStatusItem.isSource()) {
            holder.actArr.setText("Source\n\n");
            holder.schArr.setText("Source");
        }


        return convertView;
    }


    static class HolderClass {
        TextView stnName, schArr, schDept, actArr, actDept, delayTime, itemHeader;
        LinearLayout layout;
        ImageView imageSpot;
    }
}
