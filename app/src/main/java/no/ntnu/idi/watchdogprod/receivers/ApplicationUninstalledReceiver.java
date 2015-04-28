package no.ntnu.idi.watchdogprod.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import no.ntnu.idi.watchdogprod.domain.ProfileEvent;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileDataSource;

/**
 * Created by fredsten on 23.03.2015.
 */
public class ApplicationUninstalledReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String dataString = intent.getDataString();
        String packageName = dataString.split(":")[1];
        Toast toast = Toast.makeText(context, packageName + " is uninstalled!", Toast.LENGTH_LONG);
        toast.show();

        ProfileDataSource profileDataSource = new ProfileDataSource(context);
        profileDataSource.open();
        ProfileEvent profileEvent = profileDataSource.getSpecificEventForApp(Profile.INSTALLED_DANGEROUS_APP,packageName);
        if(profileEvent != null) {
            String riskScore = profileEvent.getValue();
            long id = profileDataSource.insertEvent(packageName, Profile.UNINSTALLED_DANGEROUS_APP, riskScore);
            if(id != -1) {
                System.out.println("UNINSTALL APP DB ER GOOD" + packageName + " SCORE: " + riskScore);
            }
            profileDataSource.close();
        } else {
            System.out.println("APP UNINSTALL ER NULL I DB" + packageName);
        }


    }
}
