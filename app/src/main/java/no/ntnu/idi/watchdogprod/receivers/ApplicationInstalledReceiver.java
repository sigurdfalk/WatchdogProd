package no.ntnu.idi.watchdogprod.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import no.ntnu.idi.watchdogprod.ApplicationHelper;
import no.ntnu.idi.watchdogprod.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.MainActivity;
import no.ntnu.idi.watchdogprod.PermissionHelper;
import no.ntnu.idi.watchdogprod.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.SharedPreferencesHelper;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.AppInfo;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.ApplicationUpdatesDataSource;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileDataSource;

/**
 * Created by fredsten on 09.03.2015.
 */
public class ApplicationInstalledReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String dataString = intent.getDataString();
        String packageName = dataString.split(":")[1];

        SharedPreferencesHelper.setDoShowAppInfoSign(context, packageName, true);

        Toast toast = Toast.makeText(context, packageName + " is installed!", Toast.LENGTH_LONG);
        toast.show();

        ApplicationUpdatesDataSource dataSource = new ApplicationUpdatesDataSource(context);
        dataSource.open();

        ArrayList<AppInfo> allPreviousVersions = dataSource.getApplicationUpdatesByPackageName(packageName);
        Collections.sort(allPreviousVersions);

        AppInfo newVersion = null;

        try {
            newVersion = dataSource.insertApplicationUpdate(ApplicationHelper.getAppInfo(packageName, context));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (newVersion != null && allPreviousVersions.size() > 0) {
            AppInfo mostRecentPreviousVersion = allPreviousVersions.get(0);

            if (PermissionHelper.newRequestedPermissions(mostRecentPreviousVersion.getPermissions(), newVersion.getPermissions()).size() > 0 || PermissionHelper.removedPermissions(mostRecentPreviousVersion.getPermissions(), newVersion.getPermissions()).size() > 0) {
                SharedPreferencesHelper.setDoShowAppWarningSign(context, packageName, true);
            } else {
                SharedPreferencesHelper.setDoShowAppWarningSign(context, packageName, false);
            }
        }

        dataSource.close();

        /*
        ProfileDataSource profileDataSource = new ProfileDataSource(context);
        profileDataSource.open();
        ExtendedPackageInfo extendedPackageInfo = ApplicationHelper.getThirdPartyApplication(context,packageName);
        profileDataSource.insert(packageName, Profile.INSTALLED_DANGEROUS_APP, PrivacyScoreCalculator.calculateScore(extendedPackageInfo.getPermissionDescriptions()) + "");
        profileDataSource.close();*/
    }
}
