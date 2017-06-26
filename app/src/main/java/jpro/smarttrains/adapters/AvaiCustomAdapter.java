package jpro.smarttrains.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import SmartTrainTools.Journey;
import SmartTrainTools.SmartUtils;
import comparators.s.journey.AvailabilityStatusComparator;
import jpro.smarttrains.R;

/**
 * Created by Ashish on 17/08/2016.
 */
public class AvaiCustomAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private int lastAnimatedPos=0;
    private static ArrayList<Journey> journeys;

    public AvaiCustomAdapter(Context context, ArrayList<Journey> results) {
        this.journeys = results;
        mInflater = LayoutInflater.from(context);

    }


    public int getCount() {
        return journeys.size();
    }

    public Object getItem(int position) {
        return journeys.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void update(ArrayList<Journey> L){
        this.journeys.clear();
        Collections.sort(L, new AvailabilityStatusComparator());
        this.journeys.addAll(L);

        this.notifyDataSetChanged();
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            System.out.println();
            convertView = mInflater.inflate(R.layout.list, null);
            holder = new ViewHolder();
            holder.trSpan=(LinearLayout)convertView.findViewById(R.id.trNoSpan);
            holder.trNo = (TextView) convertView.findViewById(R.id.trNo);
            holder.trName = (TextView) convertView.findViewById(R.id.trName);
            holder.trFare = (TextView) convertView.findViewById(R.id.fare);
            holder.desc=(TextView) convertView.findViewById(R.id.desc);
            holder.progressBar=(ProgressBar)convertView.findViewById(R.id.listpr);
            holder.trStn1 = (TextView) convertView.findViewById(R.id.rstn1);
            holder.trStn2 = (TextView) convertView.findViewById(R.id.rstn2);
            holder.trStatus = (TextView) convertView.findViewById(R.id.status);
            holder.dtnday1=(TextView)convertView.findViewById(R.id.dt_n_day1);
            holder.dtnday2=(TextView)convertView.findViewById(R.id.dt_n_day2);
            holder.boardingSpan=(LinearLayout)convertView.findViewById(R.id.boardingInfoSpan);
            holder.boardingDate=(TextView) convertView.findViewById(R.id.boardingDate);
            holder.boardingStn=(TextView)convertView.findViewById(R.id.boardingStn);
            holder.travelTime=(TextView)convertView.findViewById(R.id.timeSpan);
            holder.listSpan=(LinearLayout)convertView.findViewById(R.id.avListLL);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageView img=(ImageView)convertView.findViewById(R.id.trOK);
        boolean changedBoarding=!journeys.get(position).getSrc().getCode().equals(journeys.get(position).getInitStn().getCode());
        boolean changedDate=journeys.get(position).isDateChanged();
        if(journeys.get(position).getFare().equals("0")){
            holder.trFare.setText("-");
        } else {
            holder.trFare.setText("Rs. "+journeys.get(position).getFare());
        }

        System.out.println("DESC:: "+journeys.get(position).getDesc());
        if(!journeys.get(position).getDesc().contains("Working")){
            holder.progressBar.setVisibility(View.INVISIBLE);
            //System.out.println("++++++++++++++++++++++++++++++++++++ DISABLED: "+journeys.get(position).getDesc());
        }
        try{
            holder.desc.setText(journeys.get(position).getDesc().split("-")[1]);
        } catch (Exception E){
            holder.desc.setText("NOT AVAILABLE");

        }

        if(journeys.get(position).getDesc().startsWith("1"))
            holder.desc.setTextColor(Color.parseColor("#FF9B810D"));
        else if(journeys.get(position).getDesc().startsWith("2")) {
            holder.desc.setTextColor(Color.RED);
            holder.trSpan.setBackgroundColor(Color.parseColor("#e67c0b"));
        }
        else
            holder.desc.setTextColor(Color.parseColor("#FF219C08"));

        holder.dtnday2.setText(getTime(journeys.get(position).getTrain().getArrivalTimeof((journeys.get(position).getDest())))+", "+SmartUtils.getDayName(journeys.get(position).getDestDate())+" "+ journeys.get(position).getDestDate().getD()+" "+SmartUtils.getParsedDate(journeys.get(position).getDestDate(),"MMM"));
        holder.boardingSpan.setVisibility(View.GONE);
        try{
            holder.travelTime.setText(journeys.get(position).getTravelTimeAsString(journeys.get(position).getInitStn()));
        } catch( Exception E){
            holder.travelTime.setText("-");

        }
        if(!journeys.get(position).getStatus().startsWith("AVA")){
            holder.listSpan.setBackgroundResource(R.drawable.redborder);
            //System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$ SETTING DATE :");
            //System.out.println("DATE: "+journeys.get(position).getDate()+" destdate: "+journeys.get(position).getDestDate());
            //holder.dtnday1.setText(SmartUtils.getDayName(journeys.get(position).getInitDate())+" "+ journeys.get(position).getInitDate().getD()+" "+SmartUtils.getParsedDate(journeys.get(position).getInitDate(),"MMM"));
            holder.trStatus.setTextColor(Color.RED);
            holder.dtnday1.setText(getTime(journeys.get(position).getTrain().getDepartureTimeof(journeys.get(position).getInitStn()))+", "+SmartUtils.getDayName(journeys.get(position).getInitDate())+" "+journeys.get(position).getInitDate().getD()+" "+ SmartUtils.getParsedDate(journeys.get(position).getInitDate(),"MMM"));
            img.setVisibility(View.INVISIBLE);
        } else {
            holder.listSpan.setBackgroundResource(R.drawable.greenborder);
            holder.trStatus.setTextColor(Color.parseColor("#FF219C08"));
            img.setVisibility(View.VISIBLE);
              holder.dtnday1.setText(getTime(journeys.get(position).getTrain().getDepartureTimeof(journeys.get(position).getSrc()))+", "+ SmartUtils.getDayName(journeys.get(position).getDate())+", "+ journeys.get(position).getDate().getD()+" "+SmartUtils.getParsedDate(journeys.get(position).getDate(),"MMM"));
            if(changedBoarding){

                holder.boardingSpan.setVisibility(View.VISIBLE);
                holder.boardingStn.setText(journeys.get(position).getInitStn().getName());
                holder.boardingDate.setText(journeys.get(position).getTrain().getDepartureTimeof(journeys.get(position).getInitStn())+", "+journeys.get(position).getInitDate().getD()+" "+SmartUtils.getParsedDate(journeys.get(position).getInitDate(),"MMM"));
            }
        }

        if(journeys.get(position).getStatus().startsWith("N.")){
            System.out.println("&&&&&&&&&&&&&&&&&&& "+journeys.get(position).getTrain().getName()+" changing color");
            holder.trSpan.setBackgroundColor(Color.parseColor("#ada4a4"));
            holder.desc.setTextColor(Color.RED);
            if(!journeys.get(position).getDesc().startsWith("SERVER")) {
                holder.desc.setText("Not running on " + journeys.get(position).getDate().getDate()+ " (Only "+ SmartUtils.getRunsOnString(journeys.get(position).getTrain().getRunsOn())+")");
                holder.dtnday1.setText(getTime(journeys.get(position).getTrain().getDepartureTimeof(journeys.get(position).getSrc()))+"-");
                holder.dtnday2.setText(getTime(journeys.get(position).getTrain().getArrivalTimeof((journeys.get(position).getDest())))+"-");
                // holder.dtnday2.setText(getTime(journeys.get(position).getTrain().getArrivalTimeof((journeys.get(position).getDest())))+"-");
            }
            else
                holder.desc.setText(journeys.get(position).getDesc());
            holder.trFare.setText("-");
        } else {
            holder.trSpan.setBackgroundColor(Color.parseColor("#41a323"));
        }

        if(journeys.get(position).getStatus().startsWith("RAC"))
            holder.trStatus.setTextColor(Color.parseColor("#FFA0B107"));
        String trno=String.valueOf(journeys.get(position).getTrain().getNo());
        if(trno.length()==4)
            trno="0"+trno;
        holder.trNo.setText(trno);
        holder.trName.setText(journeys.get(position).getTrain().getName());
        holder.trStatus.setText(journeys.get(position).getStatus());
        holder.trStn1.setText(journeys.get(position).getSrc().getName()+"-"+journeys.get(position).getSrc().getCode());
        if(changedBoarding) {
            holder.trStn1.setTextColor(Color.parseColor("#FFBF9919"));
            if(changedDate){
                System.out.println("********************************************************* DATE CHANGED + CHANGED BOARDING ");
                System.out.println("++++++++"+holder.desc.getText().toString());
                holder.desc.setText(holder.desc.getText().toString()+" On Previous Date ");
            }
        }
        else
            holder.trStn1.setTextColor(Color.BLACK);

        holder.trStn2.setText(journeys.get(position).getDest().getName()+"- "+getTime(journeys.get(position).getDest().getCode()));
        if(oth){

        }

        if((position==journeys.size()-1)&&(position!=lastAnimatedPos)) {
            lastAnimatedPos=position;
            Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.slide);
            convertView.startAnimation(animation);
        }
        System.out.println("VIEW ADDED: "+journeys.get(position).getTrain().getName());
        return convertView;
    }

    private String getTime(String T){
        if(T==null) {
            oth=true;
            return "OTH";
        } else {
            return T;
        }
    }

    static class ViewHolder {
        TextView trNo,desc;
        TextView trName;
        TextView trStatus;
        TextView trFare;
        ProgressBar progressBar;
        TextView trStn1;
        LinearLayout boardingSpan;
        TextView trStn2;
        LinearLayout trSpan;
        LinearLayout listSpan;
        TextView dtnday1,dtnday2,boardingStn,boardingDate,travelTime;

    }
    ListView list;
    TextView othText;
    boolean oth=false;
}
