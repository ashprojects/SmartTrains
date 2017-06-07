package jpro.smarttrains;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.widget.Filter;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import SmartTrainTools.Train;

public class CustomTrainSpinnerAdapter extends ArrayAdapter<Train> {

    Context context;
    int resource, textViewResourceId;
    List<Train> items, tempItems, suggestions;

    public CustomTrainSpinnerAdapter(Context context, int resource, int textViewResourceId, List<Train> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;

        this.items = items;
        tempItems = new ArrayList<>(items); // this makes the difference.
        suggestions = new ArrayList<Train>();
    }


    public void update(ArrayList<Train> n){
        //System.out.println("UPDATING ITEMS");
        this.items.clear();
        this.tempItems.clear();
        this.suggestions.clear();
        this.items = n;
        tempItems = new ArrayList<>(n); // this makes the difference.
        suggestions = new ArrayList<Train>();
        notifyDataSetChanged();
        System.out.println(this.items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.def_spinner, parent, false);
        }
        Train t=items.get(position);
        if (t != null) {
            TextView lblNo = (TextView) view.findViewById(R.id.txtRed);
            TextView lblName=(TextView)view.findViewById(R.id.txtContent);
            if (lblName != null)
                lblName.setText(t.getName());
            if(lblNo!=null){
                lblNo.setText(""+t.getNoAsString());
            }

        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Train) resultValue).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Train people : tempItems) {
                   // System.out.println("MATCHING: "+constraint+" with "+people.getName());
                    if (people.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        //System.out.println("ADDED "+people.getName() +" for "+constraint);
                        suggestions.add(people);
                    } else if(people.getNoAsString().contains(constraint.toString())){
                       // System.out.println("ADDED "+people.getName() +" for "+constraint);
                        suggestions.add(people);
                    }
                }
                //System.out.println("Suggestios: "+suggestions);
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Train> filterList = (ArrayList<Train>) results.values;
            System.out.println("FILTER LIST: "+filterList);
            if (results != null && results.count > 0) {
                clear();
                for (Train x : filterList) {
                    add(x);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
