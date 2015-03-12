package no.ntnu.idi.watchdogprod.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import no.ntnu.idi.watchdogprod.domain.DataUsage;

/**
 * Created by fredsten on 09.03.2015.
 */
public class DataUsageService extends Service {
    static HashMap<String, DataUsage> appDataValues = new HashMap<>();
    private ActivityManager.RunningAppProcessInfo foregroundApp;
    private ActivityManager.RunningAppProcessInfo wasForeground;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();

        if (runningProcesses != null && runningProcesses.size() > 0) {
            foregroundApp = runningProcesses.get(0);
            runningProcesses.remove(runningProcesses.get(0));

            if (!appDataValues.containsKey(foregroundApp.processName)) {
                appDataValues.put(foregroundApp.processName, new DataUsage(foregroundApp.processName));
            }

            int uid = foregroundApp.uid;
            long down = TrafficStats.getUidRxBytes(uid);
            long up = TrafficStats.getUidTxBytes(uid);

            if (wasForeground != null && wasForeground.processName.equals(foregroundApp.processName)) {
                ArrayList<Long> valuesDownForeground = appDataValues.get(foregroundApp.processName).getAppDataValuesDownForeground();
                if(null != appDataValues.get(foregroundApp.processName).getLastdown()) {
                    valuesDownForeground.add((down - appDataValues.get(foregroundApp.processName).getLastdown()));
                }
                appDataValues.get(foregroundApp.processName).setAppDataValuesDownForeground(valuesDownForeground);
                ArrayList<Long> valuesUpForeground = appDataValues.get(foregroundApp.processName).getAppDataValuesUpForeground();
                if(null != appDataValues.get(foregroundApp.processName).getLastup()) {
                    valuesUpForeground.add((up - appDataValues.get(foregroundApp.processName).getLastup()));
                }
                appDataValues.get(foregroundApp.processName).setAppDataValuesUpForeground(valuesUpForeground);
            } else {
                wasForeground = foregroundApp;
            }

            appDataValues.get(foregroundApp.processName).setLastdown(down);
            appDataValues.get(foregroundApp.processName).setLastup(up);

            appDataValues.get(foregroundApp.processName).setLastdown(down);
            appDataValues.get(foregroundApp.processName).setLastup(up);
            //BACKGROUND
            if(runningProcesses.size() > 0) {
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningProcesses) {
                    if (!appDataValues.containsKey(runningAppProcessInfo.processName)) {
                        appDataValues.put(runningAppProcessInfo.processName, new DataUsage(runningAppProcessInfo.processName));
                    }

                    uid = runningAppProcessInfo.uid;
                    down = TrafficStats.getUidRxBytes(uid);
                    up = TrafficStats.getUidTxBytes(uid);

                    if (!(wasForeground.processName.equals(runningAppProcessInfo.processName))) {
                        ArrayList<Long> valuesDownBackground = appDataValues.get(runningAppProcessInfo.processName).getAppDataValuesDownBackground();
                        if(null != appDataValues.get(runningAppProcessInfo.processName).getLastdown()) {
                            valuesDownBackground.add(down - appDataValues.get(runningAppProcessInfo.processName).getLastdown());
                        }
                        appDataValues.get(runningAppProcessInfo.processName).setAppDataValuesDownBackground(valuesDownBackground);
                        ArrayList<Long> valuesUpBackground = appDataValues.get(runningAppProcessInfo.processName).getAppDataValuesUpBackground();
                        if( null != appDataValues.get(runningAppProcessInfo.processName).getLastup()) {
                            valuesUpBackground.add(up - appDataValues.get(runningAppProcessInfo.processName).getLastup());
                        }
                        appDataValues.get(runningAppProcessInfo.processName).setAppDataValuesUpBackground(valuesUpBackground);
                    }
                    appDataValues.get(runningAppProcessInfo.processName).setLastdown(down);
                    appDataValues.get(runningAppProcessInfo.processName).setLastup(up);
                }
            }
        } else {
            // In case there are no processes running (can not happen))
            Toast.makeText(getApplicationContext(), "No application is running", Toast.LENGTH_LONG).show();
        }

        System.out.println("USAGE SERVICE RUNNING");

        return START_NOT_STICKY;
    }
}
