package no.ntnu.idi.watchdogprod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import no.ntnu.idi.watchdogprod.sqlite.applicationupdates.AppInfo;

/**
 * Created by sigurdhf on 06.03.2015.
 */
public class ApplicationDetailActivity extends ActionBarActivity {
    public static final int ANSWER_HAPPY = 0;
    public static final int ANSWER_NEUTRAL = 1;
    public static final int ANSWER_SAD = 2;

    private String applicationPackageName;
    private ExtendedPackageInfo packageInfo;

    private View permissionFactWrapper;
    private TextView infoHeader;
    private TextView infoFact;
    private int currentPermissionFact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

        applicationPackageName = getIntent().getExtras().getString(ApplicationListActivity.PACKAGE_NAME);
        packageInfo = ApplicationHelper.getExtendedPackageInfo(this, applicationPackageName);
        currentPermissionFact = 0;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(ApplicationHelper.getApplicationName(packageInfo.getPackageInfo(), this));
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button showRuleViolations = (Button) findViewById(R.id.app_detail_show_rules);
        Button showDataUsage = (Button) findViewById(R.id.app_detail_data_usage);
        Button showQuestionDialog = (Button) findViewById(R.id.app_detail_questions);

        LinearLayout permissionsInfoWrapper = (LinearLayout) findViewById(R.id.app_detail_permissions_wrapper);
        LinearLayout updatesInfoWrapper = (LinearLayout) findViewById(R.id.app_detail_updates_wrapper);

        infoHeader = (TextView) findViewById(R.id.permission_fact_header);
        infoFact = (TextView) findViewById(R.id.permission_fact_fact);

        ButtonListener buttonListener = new ButtonListener();

        permissionsInfoWrapper.setOnClickListener(buttonListener);
        updatesInfoWrapper.setOnClickListener(buttonListener);

        showRuleViolations.setOnClickListener(buttonListener);
        showDataUsage.setOnClickListener(buttonListener);
        showQuestionDialog.setOnClickListener(buttonListener);

        TextView privacyScore = (TextView) findViewById(R.id.app_detail_privacy_score);
        privacyScore.setText("Risikofaktor " + (int) packageInfo.getPrivacyScore() + "/" + PrivacyScoreCalculator.MAX_SCORE);
        setScoreBackgroundColor(privacyScore, packageInfo.getPrivacyScore());
        fillPermissionsCard(packageInfo);
        fillUpdatesCard(packageInfo);
        initQuestions();
    }

    private void initQuestions() {
        ArrayList<PermissionFact> permissionFacts = packageInfo.getPermissionFacts();
        permissionFactWrapper = findViewById(R.id.permission_fact_wrapper);

        if (permissionFacts.size() == 0) {
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
            ArrayList<String> addedPermissions = PermissionHelper.newRequestedPermissions(previousUpdate.getPermissions(), latestUpdate.getPermissions());
            ArrayList<String> removedPermissions = PermissionHelper.removedPermissions(previousUpdate.getPermissions(), latestUpdate.getPermissions());

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
        params.width = (pix != 0) ? pix : 2;
        diagramHigh.setLayoutParams(params);

        params = diagramMedium.getLayoutParams();
        width = (countMedium / countAll) * 100.0;
        pix = getPixelsFromDp((int) width);
        params.width = (pix != 0) ? pix : 2;
        diagramMedium.setLayoutParams(params);

        params = diagramLow.getLayoutParams();
        width = (countLow / countAll) * 100.0;
        pix = getPixelsFromDp((int) width);
        params.width = (pix != 0) ? pix : 2;
        diagramLow.setLayoutParams(params);

    }

    private int getPixelsFromDp(int dps) {
        System.out.println(dps);
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
                // ToDo show dialog
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setScoreBackgroundColor(TextView textView, double score) {
        if (score > PrivacyScoreCalculator.HIGH_THRESHOLD) {
            textView.setBackgroundColor(this.getResources().getColor(R.color.risk_red));
        } else if (score > PrivacyScoreCalculator.MEDIUM_THRESHOLD) {
            textView.setBackgroundColor(this.getResources().getColor(R.color.risk_yellow));
        } else {
            textView.setBackgroundColor(this.getResources().getColor(R.color.risk_green));
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.permission_fact_radio_happy:
                if (checked) {
                    writePermissionFactInteraction(ANSWER_HAPPY);
                }

                break;
            case R.id.permission_fact_radio_neutral:
                if (checked) {
                    writePermissionFactInteraction(ANSWER_NEUTRAL);
                }

                break;
            case R.id.permission_fact_radio_sad:
                if (checked) {
                    writePermissionFactInteraction(ANSWER_SAD);
                }

                break;
        }

        currentPermissionFact++;
        showNewPermissionFact();
    }

    private void showNewPermissionFact() {
        final ArrayList<PermissionFact> permissionFacts = packageInfo.getPermissionFacts();

        if (currentPermissionFact >= permissionFacts.size()) {
            permissionFactWrapper.setVisibility(View.GONE);
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
        // ToDo implement
    }

    private class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.app_detail_show_rules) {
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
            } else if (v.getId() == R.id.app_detail_data_usage) {
                Intent i = new Intent(ApplicationDetailActivity.this, DataUsageActivity.class);
//                Intent i = new Intent(ApplicationDetailActivity.this, UserQuestionActivity.class);

                i.putExtra("packageName",applicationPackageName);
                i.putExtra("appName", ApplicationHelper.getApplicationName(packageInfo.getPackageInfo(), getBaseContext()));
                startActivity(i);


//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.setComponent(new ComponentName("com.android.settings",
//                        "com.android.settings.Settings$DataUsageSummaryActivity"));
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
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
