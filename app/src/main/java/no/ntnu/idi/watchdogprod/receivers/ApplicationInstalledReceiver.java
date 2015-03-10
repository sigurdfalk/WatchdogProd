package no.ntnu.idi.watchdogprod.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import no.ntnu.idi.watchdogprod.MainActivity;
import no.ntnu.idi.watchdogprod.R;

/**
 * Created by fredsten on 09.03.2015.
 */
public class ApplicationInstalledReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String packageName = intent.getDataString();
        Toast toast = Toast.makeText(context, packageName + " is installed!", Toast.LENGTH_LONG);
        toast.show();

        System.out.println("PACKAGE_ADDED: " + packageName);
    }
}
