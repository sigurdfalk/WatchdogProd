package no.ntnu.idi.watchdogprod;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.util.ArrayList;

import no.ntnu.idi.watchdogprod.domain.AppInfo;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.ProfileBehavior;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelper;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelper;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.ApplicationUpdatesDataSource;
import no.ntnu.idi.watchdogprod.services.DataUsagePosterService;
import no.ntnu.idi.watchdogprod.services.DataUsageService;
import no.ntnu.idi.watchdogprod.activities.*;


public class MainActivity extends ActionBarActivity {
    public static final String KEY_INITIAL_LAUNCH = "initLaunch";
    private Profile profile;
    private int installTrend;
    private int uninstallTrend;
    private ImageView appsBtn;
    private ImageView permissionListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appsBtn = (ImageView) findViewById(R.id.main_apps_btn);
        permissionListBtn = (ImageView) findViewById(R.id.main_permissions_btn);

        appsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ApplicationListActivity.class);
                startActivity(i);
            }
        });

        permissionListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PermissionListActivity.class);
                i.putExtra(ApplicationListActivity.PACKAGE_NAME, PermissionHelper.ALL_PERMISSIONS_KEY);
                startActivity(i);
            }
        });

        initProfile(this);

        final LinearLayout root = (LinearLayout) findViewById(R.id.main_tips);

        final ImageView cancelTips = (ImageView) findViewById(R.id.main_cancel_tips);
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_up);
        cancelTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        root.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                root.startAnimation(animation);
            }
        });

        if (isInitialLaunch()) {
            // ToDo bugfix at dette garanteres å gjøre før man kan gå inn i app list
            writeAllApplicationsToUpdateLog();
        }

        boolean answeredQuestions = checkAnsweredQuestionsState();
        if (!answeredQuestions) {
//            Intent intent = new Intent(MainActivity.this, UserQuestionActivity.class);
//            startActivity(intent);
        }


    }

    private void initProfile(Context context) {
        //TODO FRA DB: EULA LEST/IKKE LEST - BESVARELSE PÅ SPØRSMÅL - EVENTS - TILBAKEMELDINGER PÅ FRA APPANALYSE

        profile = Profile.getInstance();
        profile.createProfile(this);

        installTrend = profile.getInstallTrendRiskIncreasing();
        System.out.println("IN TREND " + installTrend);
        uninstallTrend = profile.getUninstallTrendRiskIncreasing();
        System.out.println("UN TREND " + uninstallTrend);
        populateProfileBehaviorList(context);
    }

    private boolean isInitialLaunch() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean isInitialLaunch = sharedPref.getBoolean(KEY_INITIAL_LAUNCH, true);

        if (isInitialLaunch) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(KEY_INITIAL_LAUNCH, false);
            editor.commit();
            return true;
        }

        return false;
    }

    private void writeAllApplicationsToUpdateLog() {
        ArrayList<ExtendedPackageInfo> applications = ApplicationHelper.getThirdPartyApplications(this);

        ApplicationUpdatesDataSource dataSource = new ApplicationUpdatesDataSource(this);
        dataSource.open();

        for (ExtendedPackageInfo app : applications) {
            try {
                AppInfo appInfo = dataSource.insertApplicationUpdate(ApplicationHelper.getAppInfo(app.getPackageInfo().packageName, this));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        dataSource.close();
    }

    public void populateProfileBehaviorList(Context context) {

//        final LinearLayout myLayout = (LinearLayout)findViewById(R.id.main_layout_cards);
//        View card = View.inflate(getApplicationContext(), R.layout.profile_behavior_card, null);
//        TextView cardTitle = (TextView) card.findViewById(R.id.behavior_card_title);
//        cardTitle.setText("Nedgang");
//        TextView cardDecription = (TextView) card.findViewById(R.id.behavior_card_text);
//        cardDecription.setText("Nedgang");
//        myLayout.addView(card);

        if (installTrend > 0) {
        } else if (installTrend < 0) {
        } else {
        }

        if (uninstallTrend > 0) {
        } else if (uninstallTrend < 0) {
        } else {
        }
    }

    public boolean checkAnsweredQuestionsState() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getBoolean("answeredQuestions", false);
    }

    private boolean isScreenLockActivated() {
        String LOCKSCREEN_UTILS = "com.android.internal.widget.LockPatternUtils";
        try {
            Class<?> lockUtilsClass = Class.forName(LOCKSCREEN_UTILS);
            Object lockUtils = lockUtilsClass.getConstructor(Context.class).newInstance(this);

            Method method = lockUtilsClass.getMethod("getActivePasswordQuality");

            int lockProtectionLevel = Integer.valueOf(String.valueOf(method.invoke(lockUtils)));

            if (lockProtectionLevel >= DevicePolicyManager.PASSWORD_QUALITY_NUMERIC) {
                return true;
            }
        } catch (Exception e) {
            Log.e("reflectInternalUtils", "ex:" + e);
        }
        return false;
    }

    private void setDataUsageSendingAlarm() {

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), DataUsageService.class);
        intent.putExtra("dataUsageSendingAlarm", true);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1234, intent, 0);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {

        }
        int timeForAlarm = 5000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeForAlarm, timeForAlarm, pendingIntent);
    }

    private void setDatabaseLoggingAlarm() {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), DataUsagePosterService.class);
        intent.putExtra("databaseLoggingAlarm", true);
        PendingIntent pendingIntent = PendingIntent.getService(this, 8888, intent, 0);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {

        }
//        long timeForAlarm = AlarmManager.INTERVAL_HOUR;
        long timeForAlarm = 20000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeForAlarm, timeForAlarm, pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.profile_information) {
            return true;
        } else if (id == R.id.main_settings) {
            //
        }

        return super.onOptionsItemSelected(item);
    }
}
