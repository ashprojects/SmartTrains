package jpro.smarttrains.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public abstract class SelfRevivingService extends Service {
    protected boolean revive = true;

    public SelfRevivingService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.revive) {
            revive(10000);
        }
    }

    protected void revive(int time) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, new Intent(SelfRevivingService.this, this.getClass()), PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + time, pendingIntent);
    }
}
