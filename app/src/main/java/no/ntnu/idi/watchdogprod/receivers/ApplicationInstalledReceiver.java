package no.ntnu.idi.watchdogprod.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import no.ntnu.idi.watchdogprod.MainActivity;
import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.activities.ApplicationDetailActivity;
import no.ntnu.idi.watchdogprod.activities.ApplicationListActivity;
import no.ntnu.idi.watchdogprod.activities.ApplicationUpdateLogActivity;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelper;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelper;
import no.ntnu.idi.watchdogprod.helpers.SharedPreferencesHelper;
import no.ntnu.idi.watchdogprod.domain.AppInfo;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
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

        try {
            newVersion = dataSource.insertApplicationUpdate(ApplicationHelper.getAppInfo(packageName, context));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (newVersion != null && allPreviousVersions.size() > 0) {
            AppInfo mostRecentPreviousVersion = allPreviousVersions.get(0);

            ArrayList<PermissionDescription> newPermissions = PermissionHelper.newRequestedPermissions(context, mostRecentPreviousVersion.getPermissions(), newVersion.getPermissions());
            ArrayList<PermissionDescription> oldPermissions = PermissionHelper.removedPermissions(context, mostRecentPreviousVersion.getPermissions(), newVersion.getPermissions());

            if (newPermissions.size() > 0 || oldPermissions.size() > 0) {
                SharedPreferencesHelper.setDoShowAppWarningSign(context, packageName, true);

                try {
                    PackageInfo packageInfo = ApplicationHelper.getPackageInfo(packageName, context);
                    String appName = context.getPackageManager().getApplicationLabel(packageInfo.applicationInfo).toString();
                    showNotification(context, appName + " oppdatert", "Risikofaktor endret ved siste oppdatering", packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                SharedPreferencesHelper.setDoShowAppWarningSign(context, packageName, false);
            }
        }

        dataSource.close();

        ProfileDataSource profileDataSource = new ProfileDataSource(context);
        profileDataSource.open();
        ExtendedPackageInfo extendedPackageInfo = ApplicationHelper.getThirdPartyApplication(context,packageName);
        profileDataSource.insertEvent(packageName, Profile.INSTALLED_DANGEROUS_APP, PrivacyScoreCalculator.calculateScore(extendedPackageInfo.getPermissionDescriptions()) + "");
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
        resultIntent.putExtra(ApplicationUpdateLogActivity.FROM_NOTIFICATION, true);
        Intent firstBackIntent = new Intent(context, ApplicationDetailActivity.class);
        firstBackIntent.putExtra(ApplicationListActivity.PACKAGE_NAME, packageName);
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
