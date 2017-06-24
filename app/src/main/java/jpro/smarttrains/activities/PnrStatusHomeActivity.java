package jpro.smarttrains.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.simple.parser.ParseException;

import java.io.IOException;

import SmartTrainTools.PNRStatus;
import jpro.smarttrains.R;

public class PnrStatusHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnr_status_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View promptView = layoutInflater.inflate(R.layout.input_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText pnr = (EditText) promptView.findViewById(R.id.userInput);
        Button btn = (Button) promptView.findViewById(R.id.userInputBtn);
        pnr.setHint("ENTER PNR");
        btn.setText("SHOW STATUS");
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPNRStatusAsync a = new GetPNRStatusAsync();
                System.out.println("== CREATED ASYNC OPERATIONS " + pnr.getText().toString());
                a.execute(pnr.getText().toString());
            }
        });


    }


    class GetPNRStatusAsync extends AsyncTask<String, Void, PNRStatus> {


        @Override
        protected void onPostExecute(PNRStatus pnrStatus) {
            pd.hide();
            if (pnrStatus != null) {
                Intent in = new Intent(PnrStatusHomeActivity.this, PNRStatusActivity.class);
                in.putExtra("pnrObject", pnrStatus);
                startActivity(in);
            }
        }

        @Override
        protected PNRStatus doInBackground(String... strings) {
            PNRStatus status = null;
            try {
                status = new PNRStatus(strings[0]);
            } catch (ParseException E) {

            } catch (IOException E) {
                System.out.println("IOEXCEPTION AT " + strings[0]);
            }
            return status;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(PnrStatusHomeActivity.this);
            pd.setMessage("Connecting");
            pd.show();
        }

        ProgressDialog pd;
    }
}
