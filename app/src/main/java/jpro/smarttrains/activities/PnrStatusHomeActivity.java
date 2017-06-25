package jpro.smarttrains.activities;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.math.BigInteger;
import java.security.SecureRandom;

import AnimationTools.Animator;
import SmartTrainTools.PNRStatus;
import SmartTrainsDB.modals.PNR;
import jpro.smarttrains.R;
import jpro.smarttrains.adapters.PNRListViewAdapter;

public class PnrStatusHomeActivity extends AppCompatActivity {

    FloatingActionButton newPnrBtn;
    ListView savedPNRs;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public final static class SessionIdentifierGenerator {
        private static SecureRandom random = new SecureRandom();

        public static String nextSessionId() {
            return new BigInteger(130, random).toString(32);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnr_status_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fb = new FloatingActionButton(this);
        fb.setBackgroundColor(Color.RED);
        setTitle("My Journeys");

        newPnrBtn = (FloatingActionButton) findViewById(R.id.pnr_status_home_fab_bottom);
        newPnrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                String clipdata = item.getText().toString();
                LayoutInflater layoutInflater = LayoutInflater.from(PnrStatusHomeActivity.this);
                View promptView = layoutInflater.inflate(R.layout.input_prompt, null);
                final EditText pnr = (EditText) promptView.findViewById(R.id.userInput);
                Button btn = (Button) promptView.findViewById(R.id.userInputBtn);
                ImageButton clearPnrBtn = (ImageButton) promptView.findViewById(R.id.input_promt_clear);
                clearPnrBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pnr.setText("");
                    }
                });

                pnr.setHint("ENTER PNR");
                btn.setText("SHOW STATUS");
                if (clipdata.matches("[0-9]+") && clipdata.length() == 10) {
                    Toast.makeText(PnrStatusHomeActivity.this, "Pasted from Clipboard", Toast.LENGTH_SHORT).show();
                    pnr.setText(clipdata);
                }
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PnrStatusHomeActivity.this);
                alertDialogBuilder.setView(promptView);
                alertDialogBuilder.setCancelable(true);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (pnr.getText().toString().length() < 10) {
                            Toast.makeText(PnrStatusHomeActivity.this, "Please enter valid PNR", Toast.LENGTH_SHORT);
                        } else {
                            GetPNRStatusAsync a = new GetPNRStatusAsync();
                            a.execute(pnr.getText().toString());
                        }
                    }
                });
                alertDialogBuilder.show();
            }
        });

        savedPNRs = (ListView) findViewById(R.id.content_pnr_status_home).findViewById(R.id.pnr_home_allPnrListView);
        savedPNRs.setAdapter(new PNRListViewAdapter(PnrStatusHomeActivity.this, R.layout.pnr_list_item, PNR.objects.all()));
        if (Build.VERSION.SDK_INT >= 21)
            savedPNRs.setNestedScrollingEnabled(true);


        savedPNRs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("--VIEW");

            }
        });

        initAnimations();

    }

    private void initAnimations() {
        Animator.addActivityTransition(getWindow(), Animator.Type.EXPLODE, 200);
    }


    class GetPNRStatusAsync extends AsyncTask<String, Void, PNRStatus> {

        @Override
        protected void onPostExecute(PNRStatus pnrStatus) {
            pd.hide();
            if (pnrStatus != null) {
                if (!PNR.objects.alreadyExists(pnrStatus.getPNR())) {
                    PNR.objects.addPNR(pnrStatus);
                    savedPNRs.setAdapter(new PNRListViewAdapter(PnrStatusHomeActivity.this, R.layout.pnr_list_item, PNR.objects.all()));

                }
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
                E.printStackTrace();
            } catch (Exception E) {
                E.printStackTrace();
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
