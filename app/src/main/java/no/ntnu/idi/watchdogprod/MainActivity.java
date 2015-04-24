package no.ntnu.idi.watchdogprod;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
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
import java.util.Date;
import java.util.concurrent.TimeUnit;

import no.ntnu.idi.watchdogprod.domain.AppInfo;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.ProfileBehavior;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelper;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelper;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
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
    private int disharmony;
    private ImageView appsBtn;
    private ImageView permissionListBtn;
    private TextView totalRiskScoreTextView;

    private int updatedAppsCount;

    private TextView cardText;
    private ImageView cardImage;
    private LinearLayout backgroundColor;

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

        totalRiskScoreTextView = (TextView)findViewById(R.id.main_total_risk_score);
        totalRiskScoreTextView.setText("Total risikofaktor: " + calculateTotalRiskScore() + "/100");

        profile = new Profile();
        profile.createProfile(this);

        installTrend = profile.getInstallTrendRiskIncreasing();
        System.out.println("IN TREND " + installTrend);
        uninstallTrend = profile.getUninstallTrendRiskIncreasing();
        System.out.println("UN TREND " + uninstallTrend);
        populateBehaviorCards(context);
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

    private int calculateTotalRiskScore() {
        ApplicationHelper.clearApplicationList();
        ArrayList<ExtendedPackageInfo> applications = ApplicationHelper.getThirdPartyApplications(this);

        int sum = 0;

        for (ExtendedPackageInfo application : applications) {
            sum += application.getPrivacyScore();
        }

        return sum / applications.size();
    }

    private void populateBehaviorCards(Context context) {
        createThreatLevelCard();
        createInstallTrendCard();
        createUninstallTrendCard();
        createUpdatedAppsCard();
        createHarmonyCard();
    }

    private void createInstallTrendCard() {
        backgroundColor = (LinearLayout)findViewById(R.id.card_background_install_trend);
        cardText = (TextView)findViewById(R.id.main_card_installtrend_text);
        cardImage = (ImageView)findViewById(R.id.main_card_installtrend_image);

        if (installTrend == Profile.APP_TREND_INCREASING) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_red));
            cardText.setText(getResources().getString(R.string.card_install_trend_positive));
            cardImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_trending_up_black_48dp));
        } else if (installTrend < Profile.APP_TREND_DECREASING) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_install_trend_negative));
            cardImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_trending_down_grey600_24dp));
        } else if(installTrend == Profile.APP_TREND_FIXED_HIGH) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_red));
            cardText.setText(getResources().getString(R.string.card_install_trend_fixed_high));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_trending_neutral_black_36dp));
        } else if(installTrend == Profile.APP_TREND_FIXED_LOW) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_install_trend_fixed_low));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_trending_neutral_black_36dp));
        }
    }

    private void createUpdatedAppsCard() {
        backgroundColor = (LinearLayout)findViewById(R.id.card_background_updates);
        cardText = (TextView)findViewById(R.id.main_card_updates_text);
        cardImage = (ImageView)findViewById(R.id.main_card_updates_image);

        String informationEnding = "har nylig blitt oppdatert. Dette kan ha påvirket risikofaktoren til applikasjonene.";

        if(updatedAppsCount > 0 ) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_yellow));
            cardText.setText(updatedAppsCount == 1 ? updatedAppsCount + " app " + informationEnding : updatedAppsCount + " apper " + informationEnding);
        } else {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_updates_no));
        }
    }

    private void createUninstallTrendCard() {
        backgroundColor = (LinearLayout)findViewById(R.id.card_background_uninstall_trend);
        cardText = (TextView)findViewById(R.id.main_card_uninstalltrend_text);
        cardImage = (ImageView)findViewById(R.id.main_card_uninstalltrend_image);

        if (uninstallTrend == Profile.APP_TREND_INCREASING) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_uninstall_trend_positive));
            cardImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_trending_up_black_48dp));
        } else if (uninstallTrend < Profile.APP_TREND_DECREASING) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_uninstall_trend_negative));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_trending_neutral_black_36dp));
        }
    }

    private void createHarmonyCard() {
        backgroundColor = (LinearLayout)findViewById(R.id.card_background_harmony);
        cardText = (TextView)findViewById(R.id.main_card_harmony_text);
        cardImage = (ImageView)findViewById(R.id.main_card_harmony_image);

        if(disharmony == Profile.APP_DISHARMONY) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_red));
            cardText.setText(getResources().getString(R.string.card_disharmony_positive));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_emoticon_sad_grey600_36dp));
        } else if(disharmony == Profile.APP_HARMONY) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_disharmony_negative));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_emoticon_happy_grey600_36dp));
        } else {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_yellow));
            cardText.setText(getResources().getString(R.string.card_disharmony_neutral));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_emoticon_neutral_grey600_36dp));
        }
    }

    private void createThreatLevelCard() {
        TextView cardText = (TextView)findViewById(R.id.main_card_overallthreat_text);
        ImageView cardImage = (ImageView)findViewById(R.id.main_card_overallthreat_image);
        LinearLayout backgroundColor = (LinearLayout)findViewById(R.id.card_background_overallthreat);

        ApplicationHelper.clearApplicationList();
        ArrayList<ExtendedPackageInfo> applications = ApplicationHelper.getThirdPartyApplications(this);

        int redAppsCount = 0;
        int yellowAppsCount = 0;
        int greenAppsCount = 0;

        updatedAppsCount = 0;

        Date now = new Date();
        final int hoursInDay = 24;


        for (ExtendedPackageInfo application : applications) {
            if(application.getUpdateLog() != null && application.getUpdateLog().size() > 0 ) {
                long diff = now.getTime() - application.getUpdateLog().get(0).getLastUpdateTime();
                if (TimeUnit.MILLISECONDS.toHours(diff) < (hoursInDay * 3)) {
                    updatedAppsCount++;
                }
            }

            if(application.getPrivacyScore() > PrivacyScoreCalculator.HIGH_THRESHOLD) {
                redAppsCount++;
            } else if(application.getPrivacyScore() > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
                yellowAppsCount++;
            } else {
                greenAppsCount++;
            }
        }

        cardText.setText("Det er for øyeblikket installert " + redAppsCount + " applikasjoner med høy risikofaktor, " + yellowAppsCount + " med middels riskofaktor, og " + greenAppsCount + " med lav riskofaktor");

        if(redAppsCount > 3) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_red));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_emoticon_sad_grey600_36dp));
        } else  if(yellowAppsCount > 4) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_yellow));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_emoticon_neutral_grey600_36dp));
        } else {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_emoticon_happy_grey600_36dp));
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
