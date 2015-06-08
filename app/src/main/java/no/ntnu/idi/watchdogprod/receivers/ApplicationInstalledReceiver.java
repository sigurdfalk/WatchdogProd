package no.ntnu.idi.watchdogprod.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import no.ntnu.idi.watchdogprod.MainActivity;
import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.activities.ApplicationDetailActivity;
import no.ntnu.idi.watchdogprod.activities.ApplicationListActivity;
import no.ntnu.idi.watchdogprod.activities.ApplicationUpdateLogActivity;
import no.ntnu.idi.watchdogprod.domain.AppInfo;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelperSingleton;
import no.ntnu.idi.watchdogprod.helpers.SharedPreferencesHelper;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;
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


        ApplicationUpdatesDataSource dataSource = new ApplicationUpdatesDataSource(context);
        dataSource.open();

        ArrayList<AppInfo> allPreviousVersions = dataSource.getApplicationUpdatesByPackageName(packageName);
        Collections.sort(allPreviousVersions);

        AppInfo newVersion = null;
        ApplicationHelperSingleton applicationHelperSingleton = ApplicationHelperSingleton.getInstance(context.getApplicationContext());
        applicationHelperSingleton.updateInstance();
        ExtendedPackageInfo packageInfo = applicationHelperSingleton.getApplicationByPackageName(packageName);

        newVersion = dataSource.insertApplicationUpdate(packageInfo.getPackageInfo());


        if (newVersion != null && allPreviousVersions.size() > 0) {
            AppInfo mostRecentPreviousVersion = allPreviousVersions.get(0);

            ArrayList<PermissionDescription> newPermissions = applicationHelperSingleton.getPermissionHelper().newRequestedPermissions(mostRecentPreviousVersion.getPermissions(), newVersion.getPermissions());
            ArrayList<PermissionDescription> oldPermissions = applicationHelperSingleton.getPermissionHelper().removedPermissions(mostRecentPreviousVersion.getPermissions(), newVersion.getPermissions());

            if (newPermissions.size() > 0 || oldPermissions.size() > 0) {
                SharedPreferencesHelper.setDoShowAppWarningSign(context, packageName, true);

                String appName = context.getPackageManager().getApplicationLabel(packageInfo.getPackageInfo().applicationInfo).toString();
                showNotification(context, appName + " oppdatert", "Risikofaktor endret ved siste oppdatering", packageName);

            } else {
                SharedPreferencesHelper.setDoShowAppWarningSign(context, packageName, false);
            }
        }
        dataSource.close();

        ProfileDataSource profileDataSource = new ProfileDataSource(context);
        profileDataSource.open();
        long id = profileDataSource.insertEvent(packageName, Profile.INSTALLED_DANGEROUS_APP, packageInfo.getPrivacyScore() + "");
        profileDataSource.close();
    }

    private void showNotification(Context context, String title, String text, String packageName) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.telenor_white)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);

        Intent resultIntent = new Intent(context, ApplicationUpdateLogActivity.class);
        resultIntent.putExtra(ApplicationListActivity.PACKAGE_NAME, packageName);
        Intent firstBackIntent = new Intent(context, ApplicationDetailActivity.class);
        firstBackIntent.putExtra(ApplicationListActivity.PACKAGE_NAME, packageName);
        firstBackIntent.putExtra(ApplicationDetailActivity.FROM_NOTIFICATION, true);
        Intent secondBackIntent = new Intent(context, ApplicationListActivity.class);
        Intent thirdBackIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(thirdBackIntent);
        stackBuilder.addNextIntent(secondBackIntent);
        stackBuilder.addNextIntent(firstBackIntent);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(new Random().nextInt(), mBuilder.build());
    }
}
