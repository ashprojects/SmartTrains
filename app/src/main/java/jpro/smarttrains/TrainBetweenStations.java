package jpro.smarttrains;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import SmartTrainTools.Train;

public class TrainBetweenStations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_between_stations);
        trains=(ArrayList<Train>)getIntent().getSerializableExtra("trains");
        listView=(ListView)findViewById(R.id.allTrainsList);
        //listAdapter=new TrainsViewListAdapter(this,trains,,trains.size(),null);
    }

    ListView listView;
    ArrayList<Train> trains;
    TrainsViewListAdapter listAdapter;
}
