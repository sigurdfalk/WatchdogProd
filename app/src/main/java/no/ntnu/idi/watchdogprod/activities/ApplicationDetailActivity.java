package no.ntnu.idi.watchdogprod.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import no.ntnu.idi.watchdogprod.R;
import no.ntnu.idi.watchdogprod.adapters.ApplicationListAdapter;
import no.ntnu.idi.watchdogprod.domain.Answer;
import no.ntnu.idi.watchdogprod.domain.AppInfo;
import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;
import no.ntnu.idi.watchdogprod.domain.PermissionDescription;
import no.ntnu.idi.watchdogprod.domain.PermissionFact;
import no.ntnu.idi.watchdogprod.domain.Rule;
import no.ntnu.idi.watchdogprod.helpers.ApplicationHelperSingleton;
import no.ntnu.idi.watchdogprod.helpers.SharedPreferencesHelper;
import no.ntnu.idi.watchdogprod.privacyProfile.PrivacyScoreCalculator;
import no.ntnu.idi.watchdogprod.sqlite.answers.AnswersDataSource;

/**
 * Created by sigurdhf on 06.03.2015.
 */
public class ApplicationDetailActivity extends ActionBarActivity {
    public static final String FROM_NOTIFICATION = "fromNotification";
    public static String APP_DELETED_INTENT_KEY = "deletedPackage";
    private String applicationPackageName;
    private ExtendedPackageInfo packageInfo;
    private ApplicationHelperSingleton applicationHelperSingleton;
    private PrivacyScoreCalculator privacyScoreCalculator;
    private View permissionFactWrapper;
    private TextView infoHeader;
    private TextView infoFact;
    private int currentPermissionFact;
    private LinearLayout uninstall;
    private String deletedAppPackage;
    private int APP_DELETE_CODE = 2015;
    private AnswersDataSource answersDataSource;

    private double riskScore;

    private LinearLayout riskScoreBackground;
    private TextView riskScoreText;
    private ImageView riskScoreIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

        answersDataSource = new AnswersDataSource(this);
        applicationHelperSingleton = ApplicationHelperSingleton.getInstance(this.getApplicationContext());

        applicationPackageName = getIntent().getExtras().getString(ApplicationListActivity.PACKAGE_NAME);

        if (getIntent().getExtras().getBoolean(FROM_NOTIFICATION, false)) {
            applicationHelperSingleton.updateInstance();
        }

        packageInfo = applicationHelperSingleton.getApplicationByPackageName(applicationPackageName);
        currentPermissionFact = 0;

        String appName = ApplicationHelperSingleton.getApplicationName(this, packageInfo.getPackageInfo());

        riskScore = packageInfo.getPrivacyScore();

        riskScoreBackground = (LinearLayout) findViewById(R.id.app_detail_privacy_score_background);
        riskScoreText = (TextView) findViewById(R.id.app_detail_privacy_score);
        riskScoreIndicator = (ImageView) findViewById(R.id.app_detail_privacy_score_indicator);
        riskScoreText.setText("Risikofaktor " + (int) riskScore + "/" + PrivacyScoreCalculator.MAX_SCORE);
        setScoreBackgroundColor(riskScoreBackground, riskScore);

        uninstall = (LinearLayout) findViewById(R.id.app_detail_uninstall_wrapper);
        TextView uninstallText = (TextView) findViewById(R.id.app_detail_uninstall_text);
        uninstallText.setText(String.format(getResources().getString(R.string.touch_to_uninstall), appName));

        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri packageURI = Uri.parse("package:" + applicationPackageName);
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                deletedAppPackage = applicationPackageName;
                startActivityForResult(uninstallIntent, APP_DELETE_CODE);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(appName);
        actionBar.setDisplayHomeAsUpEnabled(true);

        LinearLayout permissionsInfoWrapper = (LinearLayout) findViewById(R.id.app_detail_permissions_wrapper);
        LinearLayout updatesInfoWrapper = (LinearLayout) findViewById(R.id.app_detail_updates_wrapper);
        LinearLayout indicatorsWrapper = (LinearLayout) findViewById(R.id.app_detail_indicators_wrapper);

        infoHeader = (TextView) findViewById(R.id.permission_fact_header);
        infoFact = (TextView) findViewById(R.id.permission_fact_fact);

        ButtonListener buttonListener = new ButtonListener();

        permissionsInfoWrapper.setOnClickListener(buttonListener);
        updatesInfoWrapper.setOnClickListener(buttonListener);
        indicatorsWrapper.setOnClickListener(buttonListener);

        fillPermissionsCard(packageInfo);
        fillUpdatesCard(packageInfo);
        fillIndicatorsCard(packageInfo);
        initQuestions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_DELETE_CODE) {

            PackageInfo packageInfoExists = null;

            try {
                packageInfoExists = ApplicationHelperSingleton.getApplicationPackageInfo(this, deletedAppPackage);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (packageInfoExists == null) {
                Intent intent = new Intent();
                intent.putExtra(APP_DELETED_INTENT_KEY, deletedAppPackage);
                setResult(ApplicationListAdapter.APP_DELETED_CODE, intent);
                ApplicationDetailActivity.this.finish();
            } else {
                //user Clicked on cancel
            }
        }
    }

    private void initQuestions() {
        ArrayList<PermissionFact> permissionFacts = packageInfo.getPermissionFacts();
        permissionFactWrapper = findViewById(R.id.permission_fact_wrapper);

        if (permissionFacts.size() == 0 || !SharedPreferencesHelper.doShowAppInfoSign(this, packageInfo.getPackageInfo().packageName)) {
            permissionFactWrapper.setVisibility(View.GONE);
            return;
        }

        permissionFactWrapper.setVisibility(View.VISIBLE);
        PermissionFact permissionFact = permissionFacts.get(currentPermissionFact);

        setPermissionFact(permissionFact);
    }

    private void setPermissionFact(PermissionFact permissionFact) {
        infoHeader.setText(permissionFact.getHeader());
        infoFact.setText(permissionFact.getFact());

        ((RadioButton) findViewById(R.id.permission_fact_radio_happy)).setChecked(false);
        ((RadioButton) findViewById(R.id.permission_fact_radio_neutral)).setChecked(false);
        ((RadioButton) findViewById(R.id.permission_fact_radio_sad)).setChecked(false);
    }

    private void fillIndicatorsCard(ExtendedPackageInfo packageInfo) {
        TextView text = (TextView) findViewById(R.id.app_detail_indicators_text);
        TextView count = (TextView) findViewById(R.id.app_detail_indicators_count);
        ArrayList<Rule> indicators = packageInfo.getViolatedRules();

        String stringCount = null;

        if (indicators.size() == 0) {
            stringCount = "ingen";
            count.setBackgroundColor(getResources().getColor(R.color.risk_green));
        } else {
            stringCount = Integer.toString(indicators.size());
            count.setBackgroundColor(getResources().getColor(R.color.risk_red));
        }

        count.setText(Integer.toString(indicators.size()));
        text.setText("Denne applikasjonen har " + stringCount + " indikatorer på farlig adferd");
    }

    private void fillPermissionsCard(ExtendedPackageInfo app) {
        TextView text = (TextView) findViewById(R.id.app_detail_permissions_text);

        ArrayList<PermissionDescription> permissions = app.getPermissionDescriptions();

        text.setText("Denne applikasjonen krever " + permissions.size() + " tillatelser, hvor " + getPermissionRiskCount(PrivacyScoreCalculator.RISK_HIGH, permissions) + " av disse har høy risikofaktor for ditt personvern.");
        setPermissionDiagram(permissions);
    }

    private void fillUpdatesCard(ExtendedPackageInfo app) {
        AppInfo latestUpdate = app.getUpdateLog().get(0);

        TextView updatesText = (TextView) findViewById(R.id.app_detail_updates_text);

        if (app.getUpdateLog().size() == 1) {
            updatesText.setText("Denne oppdateringen hadde ingen innvirkning på applikasjonens risikofaktor.");
        } else {
            AppInfo previousUpdate = app.getUpdateLog().get(1);
            ArrayList<PermissionDescription> addedPermissions = applicationHelperSingleton.getPermissionHelper().newRequestedPermissions(previousUpdate.getPermissions(), latestUpdate.getPermissions());
            ArrayList<PermissionDescription> removedPermissions = applicationHelperSingleton.getPermissionHelper().removedPermissions(previousUpdate.getPermissions(), latestUpdate.getPermissions());

            if (addedPermissions.size() == 0 && removedPermissions.size() == 0) {
                updatesText.setText("Denne oppdateringen hadde ingen innvirkning på applikasjonens risikofaktor.");
            }

            if (addedPermissions.size() > 0) {
                updatesText.setText("Det ble i denne oppdateringen lagt til " + addedPermissions.size() + " nye tillatelser");

                if (removedPermissions.size() > 0) {
                    updatesText.append(" og " + removedPermissions.size() + " gamle tillatelser ble fjernet.");
                } else {
                    updatesText.append(".");
                }
            } else if (removedPermissions.size() > 0) {
                updatesText.setText("Det ble i denne oppdateringen fjernet " + removedPermissions.size() + " tillatelser.");
            }
        }

        ImageView warningSign = (ImageView) findViewById(R.id.app_detail_updates_warning);

        if (SharedPreferencesHelper.doShowAppWarningSign(this, packageInfo.getPackageInfo().packageName)) {
            warningSign.setVisibility(View.VISIBLE);
        } else {
            warningSign.setVisibility(View.GONE);
        }

        setUpdateDateTime(latestUpdate);
    }

    private void setUpdateDateTime(AppInfo latestUpdate) {
        TextView date = (TextView) findViewById(R.id.app_detail_updates_date);
        TextView time = (TextView) findViewById(R.id.app_detail_updates_time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Date updateDate = new Date(latestUpdate.getLastUpdateTime());

        date.setText(dateFormat.format(updateDate));
        time.setText(timeFormat.format(updateDate));
    }

    private void setPermissionDiagram(ArrayList<PermissionDescription> permissions) {
        TextView diagramHigh = (TextView) findViewById(R.id.app_detail_permissions_high);
        TextView diagramMedium = (TextView) findViewById(R.id.app_detail_permissions_medium);
        TextView diagramLow = (TextView) findViewById(R.id.app_detail_permissions_low);

        double countHigh = getPermissionRiskCount(PrivacyScoreCalculator.RISK_HIGH, permissions);
        double countMedium = getPermissionRiskCount(PrivacyScoreCalculator.RISK_MEDIUM, permissions);
        double countLow = getPermissionRiskCount(PrivacyScoreCalculator.RISK_LOW, permissions);
        double countAll = permissions.size();

        ViewGroup.LayoutParams params = diagramHigh.getLayoutParams();
        double width = (countHigh / countAll) * 100.0;
        int pix = getPixelsFromDp((int) width);
        params.height = (pix != 0) ? pix : 2;
        diagramHigh.setLayoutParams(params);

        params = diagramMedium.getLayoutParams();
        width = (countMedium / countAll) * 100.0;
        pix = getPixelsFromDp((int) width);
        params.height = (pix != 0) ? pix : 2;
        diagramMedium.setLayoutParams(params);

        params = diagramLow.getLayoutParams();
        width = (countLow / countAll) * 100.0;
        pix = getPixelsFromDp((int) width);
        params.height = (pix != 0) ? pix : 2;
        diagramLow.setLayoutParams(params);

    }

    private int getPixelsFromDp(int dps) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    private int getPermissionRiskCount(int riskLevel, ArrayList<PermissionDescription> permissions) {
        int count = 0;

        for (PermissionDescription permissionDescription : permissions) {
            if (permissionDescription.getRisk() == riskLevel) {
                count++;
            }
        }

        return count;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_information_info:
                showInformationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInformationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_application_detail_info, null));
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // not implemented
            }
        });

        builder.create();
        builder.show();
    }


    private void setScoreBackgroundColor(LinearLayout background, double score) {
        if (score > PrivacyScoreCalculator.HIGH_THRESHOLD) {
            background.setBackgroundColor(this.getResources().getColor(R.color.risk_red));
        } else if (score > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            background.setBackgroundColor(this.getResources().getColor(R.color.risk_yellow));
        } else {
            background.setBackgroundColor(this.getResources().getColor(R.color.risk_green));
        }
    }

    public void onRadioButtonClicked(View view) {

        switch (view.getId()) {
            case R.id.permission_fact_radio_happy:
                writePermissionFactInteraction(Answer.ANSWER_HAPPY);
                break;
            case R.id.permission_fact_radio_neutral:
                writePermissionFactInteraction(Answer.ANSWER_NEUTRAL);
                break;
            case R.id.permission_fact_radio_sad:
                writePermissionFactInteraction(Answer.ANSWER_SAD);
                break;
        }

        currentPermissionFact++;
        showNewPermissionFact();
    }

    private void showNewPermissionFact() {
        final ArrayList<PermissionFact> permissionFacts = packageInfo.getPermissionFacts();

        applicationHelperSingleton.updateApplicationPrivacyScores();
        double newRiskScore = applicationHelperSingleton.getApplicationByPackageName(applicationPackageName).getPrivacyScore();

        if (newRiskScore > riskScore) {
            riskScoreIndicator.setImageResource(R.mipmap.ic_arrow_up_black_48dp);
        } else if (newRiskScore < riskScore) {
            riskScoreIndicator.setImageResource(R.mipmap.ic_arrow_down_black_48dp);
        } else {
            riskScoreIndicator.setImageResource(R.mipmap.ic_trending_neutral_black_48dp);
        }

        riskScore = newRiskScore;
        riskScoreText.setText("Risikofaktor " + (int) riskScore + "/" + PrivacyScoreCalculator.MAX_SCORE);
        setScoreBackgroundColor(riskScoreBackground, riskScore);

        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.anim_pulse);
        riskScoreIndicator.startAnimation(pulse);
        riskScoreText.startAnimation(pulse);

        if (currentPermissionFact >= permissionFacts.size()) {
            permissionFactWrapper.setVisibility(View.GONE);
            SharedPreferencesHelper.setDoShowAppInfoSign(this, packageInfo.getPackageInfo().packageName, false);
            return;
        }

        final Animation slideOutRight = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_right);
        final Animation slideInRight = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_right);

        slideOutRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setPermissionFact(permissionFacts.get(currentPermissionFact));
                permissionFactWrapper.startAnimation(slideInRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        permissionFactWrapper.startAnimation(slideOutRight);
    }

    private void writePermissionFactInteraction(int answer) {
        PermissionFact fact = packageInfo.getPermissionFacts().get(currentPermissionFact);

        try {
            answersDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
            // handle error
        }

        answersDataSource.insertAnswer(fact.getId(), new Date().getTime(), packageInfo.getPackageInfo().packageName, answer);

        answersDataSource.close();
    }

    private class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.app_detail_indicators_wrapper) {
                Intent i = new Intent(ApplicationDetailActivity.this, RuleViolationsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, packageInfo.getPackageInfo().packageName);
                i.putExtras(bundle);

                startActivity(i);
            } else if (v.getId() == R.id.app_detail_permissions_wrapper) {
                Intent i = new Intent(ApplicationDetailActivity.this, PermissionListActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, packageInfo.getPackageInfo().packageName);
                i.putExtras(bundle);

                startActivity(i);
            } else if (v.getId() == R.id.app_detail_updates_wrapper) {
                Intent i = new Intent(ApplicationDetailActivity.this, ApplicationUpdateLogActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(ApplicationListActivity.PACKAGE_NAME, packageInfo.getPackageInfo().packageName);
                i.putExtras(bundle);

                startActivity(i);
            }
        }
    }
}
