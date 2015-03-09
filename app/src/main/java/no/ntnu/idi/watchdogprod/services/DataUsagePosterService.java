package no.ntnu.idi.watchdogprod.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.HashMap;

import no.ntnu.idi.watchdogprod.sqlite.datausage.DataLog;
import no.ntnu.idi.watchdogprod.sqlite.datausage.DataUsageSource;

/**
 * Created by fredsten on 09.03.2015.
 */
public class DataUsagePosterService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DataUsageSource dataDBSource = new DataUsageSource(this);
        dataDBSource.open();
        DataLog dataLog;

        for(String key: DataUsageService.appDataValues.keySet()){
            dataLog = new DataLog(-1,null, DataUsageService.appDataValues.get(key).getPackageName(), DataUsageService.appDataValues.get(key).getTotalDownForeground(), DataUsageService.appDataValues.get(key).getTotalDownBackground(),
                    DataUsageService.appDataValues.get(key).getTotalUpForeground(), DataUsageService.appDataValues.get(key).getTotalUpBackground());
            dataDBSource.insert(dataLog);
        }
        dataDBSource.close();

        DataUsageService.appDataValues = new HashMap<>(); //Nuller ut etter databaseinnlegg.
        return START_NOT_STICKY;
    }
}
