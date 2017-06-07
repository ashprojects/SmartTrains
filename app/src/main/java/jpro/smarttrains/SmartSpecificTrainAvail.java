package jpro.smarttrains;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SmartSpecificTrainAvail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_specific_train_avail);
    }

    TextView trName,stn1,stn2,dt1,dt2,dateShow,status;
    Spinner stn1Spinner,stn2Spinner;
    SpinnerAdapter sn;
}
