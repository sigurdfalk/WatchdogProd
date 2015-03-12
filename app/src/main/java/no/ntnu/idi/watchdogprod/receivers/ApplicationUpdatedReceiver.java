package no.ntnu.idi.watchdogprod.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import no.ntnu.idi.watchdogprod.ApplicationHelper;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.AppInfo;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.ApplicationUpdatesDataSource;

/**
 * Created by sigurdhf on 10.03.2015.
 */
public class ApplicationUpdatedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String dataString = intent.getDataString();
        String packageName = dataString.split(":")[1];
        Toast toast = Toast.makeText(context, packageName + " is updated!", Toast.LENGTH_LONG);
        toast.show();

        ApplicationUpdatesDataSource dataSource = new ApplicationUpdatesDataSource(context);
        dataSource.open();
        try {
            AppInfo appInfo = dataSource.insertApplicationUpdate(ApplicationHelper.getAppInfo(packageName, context));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        dataSource.close();
    }
}
