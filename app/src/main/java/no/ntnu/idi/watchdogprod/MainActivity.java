package no.ntnu.idi.watchdogprod;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import no.ntnu.idi.watchdogprod.activities.ApplicationListActivity;
import no.ntnu.idi.watchdogprod.activities.BehaviorApplicationListActivity;
import no.ntnu.idi.watchdogprod.activities.PermissionListActivity;
import no.ntnu.idi.watchdogprod.domain.AppInfo;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.ProfileEvent;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelperSingleton;
import no.ntnu.idi.watchdogprod.helpers.PermissionHelperSingleton;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.privacyProfile.Profile;
import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.ApplicationUpdatesDataSource;
import no.ntnu.idi.watchdogprod.sqlite.profile.ProfileDataSource;


public class MainActivity extends ActionBarActivity {
    public static final String KEY_INITIAL_LAUNCH = "initLaunch";
    private Profile profile;
    private LinearLayout appsBtn;
    private LinearLayout permissionListBtn;
    private TextView totalRiskScoreTextView;

    private int updatedAppsCount;

    private TextView cardText;
    private ImageView cardImage;
    private LinearLayout backgroundColor;

    private ApplicationHelperSingleton applicationHelperSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isInitialLaunch = isInitialLaunch();
        applicationHelperSingleton = ApplicationHelperSingleton.getInstance(this.getApplicationContext());

        if (isInitialLaunch) {
            writeAllApplicationsToUpdateLog();
            applicationHelperSingleton.updateApplicationUpdateLogs();
            writeInstalledApplicationsToProfileDb();
        }

        appsBtn = (LinearLayout) findViewById(R.id.main_apps_btn);
        permissionListBtn = (LinearLayout) findViewById(R.id.main_permissions_btn);

        appsBtn.setOnClickListener(new MainButtonListener());
        permissionListBtn.setOnClickListener(new MainButtonListener());

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
    }

    private void initProfile(Context context) throws SQLException {
        setTotalRiskScore();

        profile = new Profile();
        profile.createProfile(this);

        populateBehaviorCards();
    }

    private void setTotalRiskScore() {
        int totalRiskScore = calculateTotalRiskScore();

        totalRiskScoreTextView = (TextView) findViewById(R.id.main_total_risk_score);
        totalRiskScoreTextView.setText("Total risikofaktor " + totalRiskScore + "/100");

        if (totalRiskScore > PrivacyScoreCalculator.HIGH_THRESHOLD) {
            totalRiskScoreTextView.setBackgroundColor(getResources().getColor(R.color.risk_red));
        } else if (totalRiskScore > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            totalRiskScoreTextView.setBackgroundColor(getResources().getColor(R.color.risk_yellow));
        } else {
            totalRiskScoreTextView.setBackgroundColor(getResources().getColor(R.color.risk_green));
        }
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

    private void writeInstalledApplicationsToProfileDb() {
        ProfileDataSource profileDataSource = new ProfileDataSource(this);
        profileDataSource.open();

        ArrayList<ProfileEvent> events = profileDataSource.getInstalledApps();

        boolean found = false;
        for (ExtendedPackageInfo extendedPackageInfo : applicationHelperSingleton.getApplications()) {

            for (ProfileEvent event : events) {
                if (event.getPackageName().equals(extendedPackageInfo.getPackageInfo().packageName)) {
                    found = true;
                }
            }
            if (!found) {
                long id = profileDataSource.insertEvent(extendedPackageInfo.getPackageInfo().packageName, Profile.INSTALLED_DANGEROUS_APP, Double.toString(extendedPackageInfo.getPrivacyScore()));
                if (id != -1) {
                    System.out.println("Application added to database: " + extendedPackageInfo.getPackageInfo().packageName);
                }
            }
            found = false;
        }
        profileDataSource.close();
    }

    private void writeAllApplicationsToUpdateLog() {
        ApplicationUpdatesDataSource dataSource = new ApplicationUpdatesDataSource(this);
        dataSource.open();

        for (ExtendedPackageInfo app : applicationHelperSingleton.getApplications()) {
            AppInfo appInfo = dataSource.insertApplicationUpdate(app.getPackageInfo());
        }

        dataSource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            initProfile(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int calculateTotalRiskScore() {

        int sum = 0;

        for (ExtendedPackageInfo application : applicationHelperSingleton.getApplications()) {
            sum += application.getPrivacyScore();
        }

        return sum / applicationHelperSingleton.getApplications().size();
    }

    private void populateBehaviorCards() {
        createThreatLevelCard();
        createInstallTrendCard();
        createUninstallTrendCard();
        createUpdatedAppsCard();
        createHarmonyCard();
    }

    private void createInstallTrendCard() {
        LinearLayout installLayout = (LinearLayout) findViewById(R.id.main_card_installtrend_layout);
        installLayout.setOnClickListener(new MainButtonListener());

        int installTrend = profile.getInstallTrendRiskIncreasing();

        backgroundColor = (LinearLayout) findViewById(R.id.card_background_install_trend);
        cardText = (TextView) findViewById(R.id.main_card_installtrend_text);
        cardImage = (ImageView) findViewById(R.id.main_card_installtrend_image);

        if (installTrend == Profile.APP_TREND_NEUTRAL) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_install_trend_neutral));
        } else if (installTrend == Profile.APP_TREND_INCREASING) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_red));
            cardText.setText(getResources().getString(R.string.card_install_trend_positive));
            cardImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_trending_up_black_48dp));
        } else if (installTrend == Profile.APP_TREND_DECREASING) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_install_trend_negative));
            cardImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_trending_down_grey600_24dp));
        } else if (installTrend == Profile.APP_TREND_FIXED_HIGH) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_red));
            cardText.setText(getResources().getString(R.string.card_install_trend_fixed_high));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_trending_neutral_black_36dp));
        } else if (installTrend == Profile.APP_TREND_FIXED_LOW) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_install_trend_fixed_low));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_trending_neutral_black_36dp));
        }
    }

    private void createUninstallTrendCard() {

        int uninstallTrend = profile.getUninstallTrendRiskIncreasing();

        backgroundColor = (LinearLayout) findViewById(R.id.card_background_uninstall_trend);
        cardText = (TextView) findViewById(R.id.main_card_uninstalltrend_text);
        cardImage = (ImageView) findViewById(R.id.main_card_uninstalltrend_image);

        if (uninstallTrend == Profile.APP_TREND_INCREASING) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_uninstall_trend_positive));
            cardImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_trending_down_grey600_24dp));
        } else if (uninstallTrend == Profile.APP_TREND_FIXED_HIGH) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText("Det har den siste tiden blitt avinstallert flere apper med høy risikofaktor.");
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_trending_down_black_48dp));
        } else if (uninstallTrend == Profile.APP_TREND_NEUTRAL) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_uninstall_trend_negative));
        }
    }

    private void createUpdatedAppsCard() {
        LinearLayout updateLayout = (LinearLayout) findViewById(R.id.main_card_update_layout);
        updateLayout.setOnClickListener(new MainButtonListener());

        backgroundColor = (LinearLayout) findViewById(R.id.card_background_updates);
        cardText = (TextView) findViewById(R.id.main_card_updates_text);
        cardImage = (ImageView) findViewById(R.id.main_card_updates_image);

        String informationEnding = "har nylig blitt oppdatert. Dette kan ha påvirket risikofaktoren til " + (updatedAppsCount == 1 ? "applikasjonen." : "applikasjonene.") +
                " Klikk for mer informasjon.";

        if (updatedAppsCount > 0) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_yellow));
            cardText.setText(updatedAppsCount == 1 ? "Én" + " app " + informationEnding : updatedAppsCount + " apper " + informationEnding);
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_warning_black_48dp));
        } else {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_updates_no));
        }
    }

    private void createHarmonyCard() {
        ArrayList<String> disharmonyApps = profile.getDisharmonyApps();

        int installedCount = 0;

        for (ExtendedPackageInfo extendedPackageInfo : applicationHelperSingleton.getApplications()) {
            if (disharmonyApps.contains(extendedPackageInfo.getPackageInfo().packageName)) {
                installedCount++;
            }
        }

        if (installedCount > 0) {
            LinearLayout harmonyLayout = (LinearLayout) findViewById(R.id.main_card_harmony_layout);
            harmonyLayout.setOnClickListener(new MainButtonListener());
        }

        backgroundColor = (LinearLayout) findViewById(R.id.card_background_harmony);
        cardText = (TextView) findViewById(R.id.main_card_harmony_text);
        cardImage = (ImageView) findViewById(R.id.main_card_harmony_image);

        if (installedCount > 0) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_red));
            cardText.setText("Du har vist misnøye til én eller flere applikasjoners tillatelser. Klikk her for oversikt over hvilke applikasjoner.");
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_emoticon_sad_white_48dp));
        } else {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardText.setText(getResources().getString(R.string.card_disharmony_neutral));
        }
    }

    private void createThreatLevelCard() {
        LinearLayout threatlevelLayout = (LinearLayout) findViewById(R.id.main_card_threatlevel_layout);
        threatlevelLayout.setOnClickListener(new MainButtonListener());

        cardText = (TextView) findViewById(R.id.main_card_overallthreat_text);
        cardImage = (ImageView) findViewById(R.id.main_card_overallthreat_image);
        backgroundColor = (LinearLayout) findViewById(R.id.card_background_overallthreat);

        int redAppsCount = 0;
        int yellowAppsCount = 0;
        int greenAppsCount = 0;

        updatedAppsCount = 0;

        Date now = new Date();
        final int hoursInDay = 24;

        for (ExtendedPackageInfo application : applicationHelperSingleton.getApplications()) {
            if (application.getUpdateLog() != null && application.getUpdateLog().size() > 0) {
                long diff = now.getTime() - application.getUpdateLog().get(0).getLastUpdateTime();
                if (TimeUnit.MILLISECONDS.toHours(diff) < (hoursInDay * 3)) {
                    updatedAppsCount++;
                }
            }

            if (application.getPrivacyScore() > PrivacyScoreCalculator.HIGH_THRESHOLD) {
                redAppsCount++;
            } else if (application.getPrivacyScore() > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
                yellowAppsCount++;
            } else {
                greenAppsCount++;
            }
        }

        cardText.setText("Det er for øyeblikket installert " + redAppsCount + (redAppsCount == 1 ? " applikasjon" : " applikasjoner") + " med høy risikofaktor, " + yellowAppsCount + " med middels riskofaktor, og " + greenAppsCount + " med lav riskofaktor.");

        if (redAppsCount > 3) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_red));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_emoticon_sad_white_48dp));
        } else if (redAppsCount > 0 || yellowAppsCount > 4) {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_yellow));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_emoticon_neutral_white_48dp));
        } else {
            backgroundColor.setBackgroundColor(getResources().getColor(R.color.risk_green));
            cardImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_emoticon_happy_white_48dp));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_info:
                showInformationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInformationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_main_info, null));
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // not implemented
            }
        });

        builder.create();
        builder.show();
    }

    private class MainButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.main_apps_btn) {
                Intent i = new Intent(MainActivity.this, ApplicationListActivity.class);
                startActivity(i);
            } else if (v.getId() == R.id.main_permissions_btn) {
                Intent i = new Intent(MainActivity.this, PermissionListActivity.class);
                i.putExtra(ApplicationListActivity.PACKAGE_NAME, PermissionHelperSingleton.ALL_PERMISSIONS_KEY);
                startActivity(i);
            } else if (v.getId() == R.id.main_card_harmony_layout) {
                Intent i = new Intent(MainActivity.this, BehaviorApplicationListActivity.class);
                i.putExtra(Profile.BEHAVIOR_APPS_KEY, Profile.BEHAVIOR_HARMONY_APPS);
                startActivity(i);
            } else if (v.getId() == R.id.main_card_update_layout) {
                Intent i = new Intent(MainActivity.this, ApplicationListActivity.class);
                startActivity(i);
            } else if (v.getId() == R.id.main_card_threatlevel_layout) {
                Intent i = new Intent(MainActivity.this, ApplicationListActivity.class);
                startActivity(i);
            } else if (v.getId() == R.id.main_card_installtrend_layout) {
                Intent i = new Intent(MainActivity.this, BehaviorApplicationListActivity.class);
                i.putExtra(Profile.BEHAVIOR_APPS_KEY, Profile.BEHAVIOR_INSTALLED_APPS);
            }
        }

    }
}
