package jpro.smarttrains.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

import SmartTrainTools.PNRStatus;
import SmartTrainsDB.modals.PNR;
import SmartTrainsDB.modals.Passenger;
import jpro.smarttrains.R;
import jpro.smarttrains.activities.PNRStatusActivity;

public class TrackPNR extends SelfRevivingService {
    public TrackPNR() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new UpdatePNRs().execute();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isPNRStatusChanged(PNR oldPNR, PNRStatus newPNR) {
        final ArrayList<Passenger> oldPassengers = oldPNR.getPassengers();
        final ArrayList<PNRStatus.Passenger> newPassengers = newPNR.getPassengers();
        if (oldPassengers.size() != newPassengers.size()) {
            return true;
        }
        for (int i = 0; i < oldPassengers.size(); i++) {
            if (!comparePassengers(oldPassengers.get(i), newPassengers.get(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean comparePassengers(Passenger oldPassenger, PNRStatus.Passenger newPassenger) {
        return oldPassenger.get(Passenger.CURRENT_STATUS).toString().equalsIgnoreCase(newPassenger.getCurrentStatus());
    }

    public class UpdatePNRs extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ArrayList<PNR> trackedPNRs = PNR.objects.getAllTrackedPNRs();
            if (trackedPNRs.size() == 0) {
                TrackPNR.super.revive = false;
                stopSelf();
            }
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            int i = 0;
            for (PNR pnr : trackedPNRs) {
                PNRStatus pnrStatus = null;
                try {
                    pnrStatus = new PNRStatus(pnr.get(PNR.PNR).toString());
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                } finally {
                    if (isPNRStatusChanged(pnr, pnrStatus)) {
                        pnr.updatePNR(pnrStatus);
                        Notification notification = getNotification(pnr);
                        notificationManager.notify(i++, notification);
                    }
                }
            }
            revive(1000 * 60 * 60);
            return null;
        }
    }

    private Notification getNotification(PNR pnr) {
        Intent intent = new Intent(this, PNRStatusActivity.class);
        intent.putExtra("pnr", pnr.get(PNR.PNR).toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("PNR status changed.")
                .setContentText("PNR No. " + pnr.get(PNR.PNR) + " is updated. Click to view.")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        return notification;
    }
}
