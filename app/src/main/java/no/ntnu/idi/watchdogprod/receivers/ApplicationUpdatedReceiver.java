package no.ntnu.idi.watchdogprod.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by sigurdhf on 10.03.2015.
 */
public class ApplicationUpdatedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String packageName = intent.getDataString();
        Toast toast = Toast.makeText(context, packageName + " is updated!", Toast.LENGTH_LONG);
        toast.show();

        System.out.println("PACKAGE_REPLACED: " + packageName);
    }
}
